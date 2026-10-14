# Guía de consumo: Puertos de Cuenta (Eva - EP-01)

## Resumen
Eva expone dos puertos de entrada para que otros dueños operen sobre la tabla `usuario`:

| Puerto | Paquete | Uso |
|---|---|---|
| `CuentasPort` | `application.port.in` | Lectura + `bloquear` (lock pesimista) |
| `CuentaEscrituraInterna` | `application.port.in` | Mutaciones acotadas (requieren tx existente) |

---

## 1. CuentasPort

**Inyección:**
```java
@Autowired
private CuentasPort cuentasPort;
```

**Operaciones:**

| Método | Tx | Descripción |
|---|---|---|
| `buscarPorCorreoNormalizado(String)` | `REQUIRED`, readOnly | Busca por correo normalizado (lowercase, trim) |
| `leer(UUID)` | `REQUIRED`, readOnly | Lee cuenta por ID con estado efectivo |
| `bloquear(UUID)` | `REQUIRED` | **Lock pesimista** fila usuario; devuelve snapshot con lock |
| `contarActivos(Set<UUID>)` | `REQUIRED`, readOnly | Cuenta cuántos IDs están ACTIVO |
| `consultar(FiltroCuenta, Paginacion)` | `REQUIRED`, readOnly | Listado paginado con filtros |

**Ejemplo uso (Jose Luis - Login):**
```java
@Transactional // REQUIRED
public LoginResultado login(String correo, String contrasena) {
    String norm = normalizar(correo);
    Optional<CuentaSnapshot> cuenta = cuentasPort.buscarPorCorreoNormalizado(norm);
    if (cuenta.isEmpty()) return falloGenerico();

    // Verificar contraseña con Juan José...
    // Si fallo elegible:
    CuentaSnapshot bloqueada = cuentasPort.bloquear(cuenta.get().id());
    // Registrar fallo con Luis...
    return resultado;
}
```

**Ejemplo uso (Luis - Protección):**
```java
@Transactional // REQUIRED
public void registrarFalloLogin(UUID usuarioId, HuellaFallo huella) {
    CuentaSnapshot bloqueada = cuentasPort.bloquear(usuarioId);
    // Evaluar si corresponde bloqueo...
    if (debeBloquear) {
        // El lock ya está tomado por bloquear()
        // Guardar contadores via CuentaEscrituraInterna
    }
}
```

---

## 2. CuentaEscrituraInterna

**Inyección:**
```java
@Autowired
private CuentaEscrituraInterna escritura;
```

**⚠️ REQUISITO OBLIGATORIO:** El llamador **DEBE** tener una transacción activa (`@Transactional`). Los métodos usan `Propagation.MANDATORY`.

| Método | Tx | Qué modifica | Qué NO modifica |
|---|---|---|---|
| `actualizarBasicos(UUID, CambioBasicos)` | `MANDATORY` | nombres, apellidos, celular, tipoDocumento | roles, estado, passwordHash, contadores, MFA, fechas, version |
| `guardarContadores(UUID, ContadoresProteccion)` | `MANDATORY` | bloqueosSeguidos, intentosFallidos | básicos, roles, estado, password, MFA |
| `guardarMfaVoluntario(UUID, boolean)` | `MANDATORY` | **Reservado SPEC-10** - lanza `UnsupportedOperationException` | - |

**Ejemplo uso (Valery - Perfil propio):**
```java
@Transactional // REQUIRED - abre tx
public void actualizarPerfilPropio(ActorUsuario actor, CambioBasicosRequest req) {
    // Validar campos...
    CambioBasicos cambio = new CambioBasicos(req.nombres(), req.apellidos(), req.celular(), null, null);
    CuentaSnapshot actualizada = escritura.actualizarBasicos(actor.sub(), cambio);
    // Actualizar perfil_cliente/perfil_vendedor si aplica...
    // Commit automático al salir del método
}
```

**Ejemplo uso (Luis - Protección):**
```java
@Transactional // REQUIRED - misma tx que bloquear()
public void procesarQuintoFallo(UUID usuarioId) {
    CuentaSnapshot bloqueada = cuentasPort.bloquear(usuarioId); // lock + tx
    // ... lógica de bloqueo ...
    escritura.guardarContadores(usuarioId, new ContadoresProteccion(5, 0));
    // Commit único al final
}
```

---

## 3. DTOs de entrada

```java
// application.port.in.CambioBasicos
record CambioBasicos(
    String nombres,           // null = no cambia
    String apellidos,         // null = no cambia
    String celular,           // null = no cambia
    String tipoDocumento,     // "DNI"|"CE"|"PASAPORTE" o null
    String numeroDocumento    // solo para Valery (encriptado/HMAC)
) {}

// application.port.in.ContadoresProteccion
record ContadoresProteccion(
    int bloqueosSeguidos,
    int intentosFallidos
) {}
```

**Regla:** `null` = "no cambiar este campo". El adaptador ignora campos null.

---

## 4. Snapshot devuelto (`CuentaSnapshot`)

```java
record CuentaSnapshot(
    UUID id,
    String nombres,
    String apellidos,
    String correo,
    String celular,
    EstadoCuenta estadoBase,      // valor en BD
    EstadoCuenta estadoEfectivo,  // calculado: BLOQUEADO vencido -> ACTIVO
    boolean correoVerificado,
    boolean mfaHabilitado,
    int bloqueosSeguidos,
    String tipoDocumento,         // "DNI"|"CE"|"PASAPORTE" o null
    String documentoEnmascarado,  // "*****234"
    Set<CodigoRol> roles          // roles actuales
) {}
```

**Seguridad:** NUNCA contiene `passwordHash`, `documentoCifrado`, `documentoHmac`, tokens, OTP.

---

## 5. Errores esperados

| Situación | Excepción |
|---|---|
| Usuario no existe | `IllegalArgumentException("Usuario no encontrado: " + id)` |
| Llamada sin transacción activa | `TransactionRequiredException` (Spring) |
| `guardarMfaVoluntario` en H3 | `UnsupportedOperationException("reservado para SPEC-10")` |

---

## 6. Orden de locks (ACUERDOS-INTERNOS.md §3)

Cuando un coordinador usa múltiples puertos:

1. **Sergio** bloquea `rol.ADMIN_SISTEMA` (si afecta roles)
2. **Eva** bloquea `usuario` (actor y objetivo, por UUID ordenado)
3. **Jose Luis** bloquea `sesion_familia` (raíces, UUID ordenado)
4. **Dueños internos**: credencial, protección, MFA, tokens
5. **Auditoría/Outbox** insert en misma tx

> El `bloquear(UUID)` de `CuentasPort` toma lock nivel 2. Los coordinadores deben invocarlo **después** del lock de Sergio (si aplica) y **antes** de locks de Jose Luis.

---

## 7. Tests de contrato (para verificar integración)

Eva provee tests en:
- `CuentaEscrituraAdapterContractTest` - verifica campos permitidos, MANDATORY, rollback
- `CuentasPortTransaccionalTest` - verifica propagación, readOnly, delegación

Otros dueños pueden usar estos tests como referencia para sus tests de integración.

---

## 8. Contacto

Dueña: **Eva Lucía Moreno Zevallos** (EP-01)
Cualquier duda sobre firmas, semántica o pruebas → comunicar antes de integrar.