# SPEC-03 — Alta y consulta administrativa de cuentas

| Campo | Valor |
|---|---|
| **Responsable** | Christian Gabriel Arancivia Salas |
| **Hito objetivo** | Hito 3 (Sem. 8) |
| **Estado** | ✅ Aprobada |
| **Aprobada por** | Sergio Alejandro Osorio Montenegro (Product Owner), 2 de octubre de 2026 |

> **Origen.** Sale de dividir la antigua SPEC-01 «Registro y gestión de usuarios» el 20 de septiembre, por indicación del profesor: una spec por función. Pasa a Christian porque es el backend del panel de administración que ya tenía asignado. La equivalencia de números está en [`trazabilidad.md`](trazabilidad.md) §1.

---

## Contexto — ¿por qué?

Los vendedores y el personal de gestión no se autorregistran: un cliente que pudiera darse a sí mismo el rol `VENDEDOR` o `ADMIN_VENTAS` rompería la segregación de funciones de todo el marketplace. Sus cuentas las crea un `ADMIN_SISTEMA`.

Dos casos más llegaron de los otros módulos (acuerdos A6 y A7): Despacho necesita cuentas para sus repartidores, que no tienen ninguno de los seis roles (A5), y Retail necesita registrar a un cliente en la tienda —sin pedirle una contraseña delante del vendedor— y encontrar después a un cliente que ya existe.

En todos los casos la contraseña la define la propia persona con un enlace de activación: nadie más llega a conocerla.

Ese mismo administrador necesita ver qué cuentas existen, en qué estado están y qué roles tienen, para operar el panel de administración.

---

## Propósito — ¿para qué?

Permitir que un `ADMIN_SISTEMA` dé de alta cuentas de personal y que liste y consulte las cuentas del sistema; que un vendedor registre y encuentre clientes en la tienda; y que quien recibe una cuenta creada por otro la active con su propia contraseña.

El resultado observable es una cuenta en `PENDIENTE_VERIFICACION` con un enlace de activación, que pasa a `ACTIVO` —y publica `usuario.creado`— cuando su titular la activa; un listado paginado y filtrable para el panel; y una búsqueda exacta por documento para la tienda.

---

## Alcance — ¿hasta dónde?

- Alta de cuentas `VENDEDOR`, de los cuatro roles de gestión o sin rol, con `POST /api/v1/usuarios`.
- Registro asistido de clientes en tienda con `POST /api/v1/usuarios/clientes`.
- Activación de esas cuentas con `POST /api/v1/auth/activar-cuenta`.
- Búsqueda de un cliente por documento con `POST /api/v1/usuarios/busqueda-documento`.
- Listado paginado con filtros por estado, rol y correo (`GET /api/v1/usuarios`).
- Consulta de una cuenta con token de administrador (`GET /api/v1/usuarios/{id}`).
- Permisos `usuario.crear`, `usuario.ver`, `cliente.registrar` y `cliente.buscar` (catálogo de SPEC-11).
- Publicación de `usuario.creado` y registro en auditoría.

---

## Requisitos — ¿qué debe hacer?

| Código | Requisito |
|---|---|
| RF-03.1 | Un `ADMIN_SISTEMA` debe poder dar de alta cuentas con rol `VENDEDOR`, con cualquiera de los cuatro roles de gestión o **sin rol**, para un perfil que vive en otro módulo (`POST /api/v1/usuarios`, acuerdos A5 y A6). El administrador no define la contraseña: la cuenta nace `PENDIENTE_VERIFICACION` y se envía un enlace de activación (RF-03.5). Los clientes no se crean por esta vía. |
| RF-03.2 | Quien tenga el permiso `usuario.ver` (`ADMIN_SISTEMA`, SPEC-11) debe poder listar las cuentas con filtros por estado, rol y correo, paginadas (`GET /api/v1/usuarios`), y consultar una cuenta (`GET /api/v1/usuarios/{id}`, el mismo endpoint que usan los módulos consumidores). |
| RF-03.3 | La contraseña que se define al activar la cuenta debe cumplir la política de SPEC-07; si no la cumple, el sistema responde `422 POLITICA_INCUMPLIDA` y la cuenta sigue sin activar. |
| RF-03.4 | El alta, la activación, cada búsqueda por documento y cualquier intento sin permiso deben registrarse mediante SPEC-12 (`USUARIO_CREADO`, `CUENTA_ACTIVADA`, `BUSQUEDA_POR_DOCUMENTO`, `ACCESO_DENEGADO`). |
| RF-03.5 | Toda cuenta creada por otra persona —alta administrativa o registro asistido— recibe por correo un **enlace de activación** de un solo uso, válido 72 horas. Con `POST /api/v1/auth/activar-cuenta` su titular define la contraseña y acepta los términos; la cuenta pasa a `ACTIVO` con el correo verificado y se publica `usuario.creado`. |
| RF-03.6 | Quien tenga el permiso `cliente.registrar` (`VENDEDOR`) debe poder registrar a un cliente en tienda (`POST /api/v1/usuarios/clientes`) con correo, nombres y apellidos, y opcionalmente celular y documento. La cuenta nace `PENDIENTE_VERIFICACION` con rol `CLIENTE` y `canalOrigen: RETAIL`, y se devuelve su `id` al momento. Si el correo o el documento ya pertenecen a una cuenta, responde `409 CORREO_NO_DISPONIBLE` o `409 DOCUMENTO_NO_DISPONIBLE`. |
| RF-03.7 | Quien tenga el permiso `cliente.buscar` (`VENDEDOR`) debe poder encontrar a un cliente por tipo y número de documento exactos (`POST /api/v1/usuarios/busqueda-documento`, ADR-007). Responde solo `id`, nombre completo, documento enmascarado y estado, y únicamente para cuentas `CLIENTE` en `ACTIVO` o `PENDIENTE_VERIFICACION`; cualquier otra responde `404`, igual que una inexistente. Admite 60 búsquedas por hora y vendedor; a la siguiente, `429 DEMASIADAS_SOLICITUDES`. |
| RF-03.8 | El documento se guarda cifrado (SPEC-16) y además con un **HMAC** del tipo y el número, que es lo único que permite buscarlo sin descifrar y lo hace único entre las cuentas. |
| RF-03.9 | Una cuenta creada por otra persona que no se active en **30 días** se elimina con todos sus datos, porque se tomaron sin consentimiento escrito (Ley N.º 29733). Es la única eliminación física de cuentas del módulo. |

---

## Escenarios — ¿cómo verificamos?

Formato Dado / Cuando / Entonces. Los marcados como **caso borde** cubren
condiciones límite, de error o de seguridad.

### ESC-03.1 Alta de un vendedor por un administrador

**Dado** un `ADMIN_SISTEMA` autenticado.

**Cuando** envía `POST /api/v1/usuarios` con `{ correo, nombres, apellidos, rol: VENDEDOR }`.

**Entonces** el sistema crea la cuenta en `PENDIENTE_VERIFICACION`, envía al correo un enlace de activación válido 72 horas, responde `201` y **todavía no** publica `usuario.creado`.

### ESC-03.2 Listado de cuentas sin permiso *(caso borde de seguridad)*

**Dado** un usuario autenticado con rol `CLIENTE` o `VENDEDOR`.

**Cuando** envía `GET /api/v1/usuarios`.

**Entonces** el sistema responde `403 SCOPE_INSUFICIENTE`, no devuelve ninguna cuenta y registra `ACCESO_DENEGADO` mediante SPEC-12.

### ESC-03.3 Alta con un correo ya registrado *(caso borde)*

**Dado** un `ADMIN_SISTEMA` autenticado y una cuenta existente con el correo `vendedor@correo.com`.

**Cuando** envía `POST /api/v1/usuarios` con ese mismo correo.

**Entonces** el sistema responde `409 CORREO_NO_DISPONIBLE`, no crea la cuenta y no publica ningún evento.

### ESC-03.4 Activación con una contraseña que no cumple la política *(caso borde)*

**Dado** una cuenta creada por un administrador y su enlace de activación vigente.

**Cuando** el titular envía `POST /api/v1/auth/activar-cuenta` con la contraseña `vendedor1`.

**Entonces** el sistema responde `422 POLITICA_INCUMPLIDA` indicando en `errores` las reglas que falló (por ejemplo `LONGITUD_MINIMA` y `CARACTER_ESPECIAL`), la cuenta sigue en `PENDIENTE_VERIFICACION` y el enlace sigue sirviendo.

### ESC-03.5 Activación correcta

**Dado** una cuenta en `PENDIENTE_VERIFICACION` creada por un vendedor en tienda y su enlace de activación vigente.

**Cuando** el titular envía `POST /api/v1/auth/activar-cuenta` con una contraseña válida y `aceptaTerminos: true`.

**Entonces** la cuenta pasa a `ACTIVO` con el correo verificado, se guardan la fecha y la versión de los términos, el enlace deja de servir, se registra `CUENTA_ACTIVADA` y se publica `usuario.creado`.

### ESC-03.6 Alta de un repartidor sin rol

**Dado** un `ADMIN_SISTEMA` autenticado.

**Cuando** envía `POST /api/v1/usuarios` con `{ correo, nombres, apellidos }` y sin `rol`.

**Entonces** la cuenta se crea con `roles: []` y, una vez activada, su token lleva `roles: []`: Despacho lo autoriza por su propio registro enlazado al `sub` (A5).

### ESC-03.7 Registro asistido en tienda

**Dado** un usuario con rol `VENDEDOR` autenticado.

**Cuando** envía `POST /api/v1/usuarios/clientes` con correo, nombres, apellidos y `{ tipoDocumento: DNI, numeroDocumento: "45781234" }`.

**Entonces** el sistema responde `201` con el `id` y `estado: PENDIENTE_VERIFICACION`, la cuenta tiene rol `CLIENTE` y `canalOrigen: RETAIL`, y el cliente recibe el enlace de activación.

### ESC-03.8 Registro asistido de un documento que ya existe *(caso borde)*

**Dado** un cliente ya registrado con el DNI `45781234`.

**Cuando** un vendedor intenta registrar otra cuenta con ese mismo DNI.

**Entonces** el sistema responde `409 DOCUMENTO_NO_DISPONIBLE` y no crea nada: el vendedor debe buscarlo (RF-03.7).

### ESC-03.9 Búsqueda por documento

**Dado** un cliente `ACTIVO` con DNI `45781234` y un vendedor autenticado.

**Cuando** el vendedor envía `POST /api/v1/usuarios/busqueda-documento` con `{ tipoDocumento: DNI, numeroDocumento: "45781234" }`.

**Entonces** el sistema responde `200` solo con `id`, `nombreCompleto`, `documentoEnmascarado: "*****234"` y `estado`, y registra `BUSQUEDA_POR_DOCUMENTO`.

### ESC-03.10 Búsqueda de una cuenta que no es de cliente o está dada de baja *(caso borde de seguridad)*

**Dado** una cuenta `INACTIVO`, o una cuenta `VENDEDOR`, con un documento registrado.

**Cuando** un vendedor la busca por ese documento.

**Entonces** el sistema responde `404 NO_ENCONTRADO`, exactamente igual que para un documento que no existe.

### ESC-03.11 Búsqueda con token de servicio *(caso borde de seguridad)*

**Dado** el token de servicio de `modulo-retail`.

**Cuando** llama a `POST /api/v1/usuarios/busqueda-documento`.

**Entonces** el sistema responde `403`: la búsqueda solo la hace una persona identificada, para que la auditoría diga quién buscó a quién.

### ESC-03.12 Exceso de búsquedas *(caso borde)*

**Dado** un vendedor que ya hizo 60 búsquedas en la última hora.

**Cuando** hace la siguiente.

**Entonces** el sistema responde `429 DEMASIADAS_SOLICITUDES` sin consultar la base.

### ESC-03.13 Cuenta sin activar durante 30 días *(caso borde de privacidad)*

**Dado** una cuenta creada en tienda hace 30 días que nunca se activó.

**Cuando** se ejecuta la tarea programada de limpieza.

**Entonces** la cuenta y todos sus datos se eliminan, y queda un registro de auditoría sin datos personales.

---

## Requisitos no funcionales — ¿con qué condiciones?

- El listado debe responder en menos de 500 ms en el percentil 95 sobre 10 000 cuentas. *(valor propuesto al dividir la spec; lo confirma su responsable antes de aprobarla)*
- El listado nunca devuelve el hash de contraseña ni el número de documento en claro.
- Los mensajes de error no deben permitir enumerar cuentas existentes a quien no tenga `usuario.ver`. La búsqueda por documento es la excepción acotada de ADR-007: exacta, auditada, limitada y solo con token de vendedor.
- El tratamiento de datos personales debe alinearse con la Ley N.º 29733 de Protección de Datos Personales.

---

## Fuera de alcance — ¿qué NO hará?

- El registro público de clientes, que es de SPEC-01.
- La búsqueda por correo o por celular, que no existe (ADR-006), y la búsqueda por `RUC`: los datos de facturación de una empresa son de Ventas (A7).
- La baja y la reactivación, que son de SPEC-04.
- Asignar o revocar roles después del alta, que es de SPEC-11.
- La edición de atributos de la cuenta, que es de SPEC-16.
- Las pantallas del panel, que se especifican aparte en `specs/front/`.

---

## Impacto en el contrato

| Endpoint / evento | Añade, modifica o elimina | Acordado con el PO |
|---|---|---|
| `POST /api/v1/usuarios` | Modifica: sin contraseña, rol opcional, nace `PENDIENTE_VERIFICACION` (A6). Solo lo usa nuestro panel | ✅ |
| `POST /api/v1/usuarios/clientes` | Añade (A6) | ✅ |
| `POST /api/v1/usuarios/busqueda-documento` | Añade (A7) | ✅ |
| `POST /api/v1/auth/activar-cuenta` | Añade (A6) | ✅ |
| `GET /api/v1/usuarios` | Añade | ⬜ |
| `GET /api/v1/usuarios/{id}` | Amplía: también con token de administrador (`usuario.ver`) | ⬜ |
| `usuario.creado` | Publica evento, al activarse la cuenta | ⬜ |

---

## Lista de completitud

La spec se cierra cuando:

- [ ] Todos los requisitos están implementados
- [ ] Cada requisito clave tiene al menos dos escenarios automatizados, uno de ellos caso borde
- [ ] Los no funcionales están verificados, o la desviación está documentada y aprobada
- [ ] Los endpoints figuran en la documentación OpenAPI publicada (`specs/openapi.yaml`)
- [ ] Nada de lo declarado fuera de alcance se construyó de forma encubierta
- [ ] El código está en `main` con revisión aprobada y desplegado en nube
