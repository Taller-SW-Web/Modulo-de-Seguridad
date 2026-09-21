# SPEC-03 — Alta y consulta administrativa de cuentas

| Campo | Valor |
|---|---|
| **Responsable** | Christian Gabriel Arancivia Salas |
| **Hito objetivo** | Hito 3 (Sem. 8) |
| **Estado** | Borrador |
| **Aprobada por** | Product Owner — pendiente de aprobación |

> **Origen.** Sale de dividir la antigua SPEC-01 «Registro y gestión de usuarios» el 20 de septiembre, por indicación del profesor: una spec por función. Pasa a Christian porque es el backend del panel de administración que ya tenía asignado. La equivalencia de números está en [`trazabilidad.md`](trazabilidad.md) §1.

---

## Contexto — ¿por qué?

Los vendedores y el personal de gestión no se autorregistran: un cliente que pudiera darse a sí mismo el rol `VENDEDOR` o `ADMIN_VENTAS` rompería la segregación de funciones de todo el marketplace. Sus cuentas las crea un `ADMIN_SISTEMA`.

Ese mismo administrador necesita ver qué cuentas existen, en qué estado están y qué roles tienen, para operar el panel de administración.

---

## Propósito — ¿para qué?

Permitir que un `ADMIN_SISTEMA` dé de alta cuentas de vendedores y de personal de gestión, y que liste y consulte las cuentas del sistema.

El resultado observable es una cuenta que nace `ACTIVO` con el rol indicado, la publicación de `usuario.creado`, y un listado paginado y filtrable para el panel.

---

## Alcance — ¿hasta dónde?

- Alta de cuentas `VENDEDOR` y de los cuatro roles de gestión con `POST /api/v1/usuarios`.
- La cuenta nace `ACTIVO`, sin verificación de correo.
- Listado paginado con filtros por estado, rol y correo (`GET /api/v1/usuarios`).
- Consulta de una cuenta con token de administrador (`GET /api/v1/usuarios/{id}`).
- Permisos `usuario.crear` y `usuario.ver` (catálogo de SPEC-11).
- Publicación de `usuario.creado` y registro en auditoría.

---

## Requisitos — ¿qué debe hacer?

| Código | Requisito |
|---|---|
| RF-03.1 | Un `ADMIN_SISTEMA` debe poder dar de alta cuentas con rol `VENDEDOR` o con cualquiera de los cuatro roles de gestión (`POST /api/v1/usuarios`). La cuenta nace `ACTIVO`, sin verificación de correo, y se publica `usuario.creado`. Los clientes solo se crean mediante el registro público. |
| RF-03.2 | Quien tenga el permiso `usuario.ver` (`ADMIN_SISTEMA`, SPEC-11) debe poder listar las cuentas con filtros por estado, rol y correo, paginadas (`GET /api/v1/usuarios`), y consultar una cuenta (`GET /api/v1/usuarios/{id}`, el mismo endpoint que usan los módulos consumidores). |
| RF-03.3 | La contraseña inicial de la cuenta debe cumplir la política de SPEC-07; si no la cumple, el sistema responde `422 POLITICA_INCUMPLIDA` y no crea la cuenta. |
| RF-03.4 | El alta y cualquier intento sin permiso deben registrarse mediante SPEC-12 (`USUARIO_CREADO`, `ACCESO_DENEGADO`). |

---

## Escenarios — ¿cómo verificamos?

Formato Dado / Cuando / Entonces. Los marcados como **caso borde** cubren
condiciones límite, de error o de seguridad.

### ESC-03.1 Alta de un vendedor por un administrador

**Dado** un `ADMIN_SISTEMA` autenticado.

**Cuando** envía `POST /api/v1/usuarios` con `{ correo, contrasena, rol: VENDEDOR }`.

**Entonces** el sistema crea la cuenta directamente en `ACTIVO`, sin enviar correo de verificación, responde `201` y publica `usuario.creado`.

### ESC-03.2 Listado de cuentas sin permiso *(caso borde de seguridad)*

**Dado** un usuario autenticado con rol `CLIENTE` o `VENDEDOR`.

**Cuando** envía `GET /api/v1/usuarios`.

**Entonces** el sistema responde `403 SCOPE_INSUFICIENTE`, no devuelve ninguna cuenta y registra `ACCESO_DENEGADO` mediante SPEC-12.

### ESC-03.3 Alta con un correo ya registrado *(caso borde)*

**Dado** un `ADMIN_SISTEMA` autenticado y una cuenta existente con el correo `vendedor@correo.com`.

**Cuando** envía `POST /api/v1/usuarios` con ese mismo correo.

**Entonces** el sistema responde `409 CORREO_NO_DISPONIBLE`, no crea la cuenta y no publica ningún evento.

### ESC-03.4 Alta con una contraseña que no cumple la política *(caso borde)*

**Dado** un `ADMIN_SISTEMA` autenticado.

**Cuando** envía `POST /api/v1/usuarios` con la contraseña `vendedor1`.

**Entonces** el sistema responde `422 POLITICA_INCUMPLIDA` indicando en `errores` las reglas que falló (por ejemplo `LONGITUD_MINIMA` y `CARACTER_ESPECIAL`) y no crea la cuenta.

---

## Requisitos no funcionales — ¿con qué condiciones?

- El listado debe responder en menos de 500 ms en el percentil 95 sobre 10 000 cuentas. *(valor propuesto al dividir la spec; lo confirma su responsable antes de aprobarla)*
- El listado nunca devuelve el hash de contraseña ni el número de documento en claro.
- Los mensajes de error no deben permitir enumerar cuentas existentes a quien no tenga `usuario.ver`.
- El tratamiento de datos personales debe alinearse con la Ley N.º 29733 de Protección de Datos Personales.

---

## Fuera de alcance — ¿qué NO hará?

- El registro público de clientes, que es de SPEC-01: los clientes no se crean por esta vía.
- La baja y la reactivación, que son de SPEC-04.
- Asignar o revocar roles después del alta, que es de SPEC-11.
- La edición de atributos de la cuenta, que es de SPEC-16.
- Las pantallas del panel, que se especifican aparte en `specs/front/`.

---

## Impacto en el contrato

| Endpoint / evento | Añade, modifica o elimina | Acordado con el PO |
|---|---|---|
| `POST /api/v1/usuarios` | Utiliza contrato existente | ⬜ |
| `GET /api/v1/usuarios` | Añade | ⬜ |
| `GET /api/v1/usuarios/{id}` | Amplía: también con token de administrador (`usuario.ver`) | ⬜ |
| `usuario.creado` | Publica evento | ⬜ |

---

## Lista de completitud

La spec se cierra cuando:

- [ ] Todos los requisitos están implementados
- [ ] Cada requisito clave tiene al menos dos escenarios automatizados, uno de ellos caso borde
- [ ] Los no funcionales están verificados, o la desviación está documentada y aprobada
- [ ] Los endpoints figuran en la documentación OpenAPI publicada (`specs/openapi.yaml`)
- [ ] Nada de lo declarado fuera de alcance se construyó de forma encubierta
- [ ] El código está en `main` con revisión aprobada y desplegado en nube
