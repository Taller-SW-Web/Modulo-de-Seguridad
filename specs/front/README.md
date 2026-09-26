# Specs de interfaz

| Campo | Valor |
|---|---|
| **Dueño** | Valery Cristin Gutierrez Bendezu — responsable de UX |
| **Estado** | Primera redacción de las 15 specs, todas en **Borrador** y sin responsable asignado |
| **Plantilla** | [`_PLANTILLA-FRONT.md`](_PLANTILLA-FRONT.md) |

Las 18 specs de `specs/` son **de backend**: dicen qué hace el servicio y se
verifican contra su API. Esta carpeta guarda las **specs de interfaz**: cómo se
ve y cómo se comporta cada pantalla de la SPA. Van aparte por indicación del
profesor, y las lleva la persona responsable de UX.

## Qué va aquí y qué no

| Va en una spec de interfaz | Va en la spec de backend |
|---|---|
| Estados de la pantalla: vacío, cargando, error, éxito | Qué responde el endpoint en cada caso |
| Validaciones del lado del cliente, para ayudar al usuario | Las reglas de verdad, que el backend aplica siempre |
| Textos y mensajes que ve el usuario | Los códigos de error (`catalogo-errores.md`) |
| Accesibilidad, diseño adaptable, navegación | Rendimiento, seguridad, datos personales |
| Qué endpoint llama la pantalla y cuándo | El contrato de ese endpoint (`openapi.yaml`) |

**Una spec de interfaz no inventa reglas.** Si una pantalla necesita algo que su
spec de backend no tiene, se cambia primero la spec de backend. Por eso cada
spec tiene una sección *Huecos detectados en el backend*: lo que falta se anota
y se lleva al PO, no se resuelve en la interfaz.

## Numeración

`FRONT-NN-nombre-corto.md`, un archivo por pantalla. Del 01 al 08 siguen el
orden de las ocho pantallas del Hito 1 en [`../trazabilidad.md`](../trazabilidad.md) §3.
Del 09 en adelante son pantallas que exigen las specs de backend y todavía no
tienen wireframe. `FRONT-00` no es una pantalla: recoge lo que comparten todas.

---

## Índice

| FRONT | Pantalla | SPEC de backend | Wireframe | Estado |
|---|---|---|---|---|
| [00](FRONT-00-comportamiento-comun.md) | Comportamiento común: sesión, rutas, errores, medidor de fuerza | 06, 07, 11 | No aplica | 🔶 Borrador |
| [01](FRONT-01-inicio-sesion.md) | Inicio de sesión | 05, 07, 14 | Pantalla 1 | 🔶 Borrador |
| [02](FRONT-02-registro-cliente.md) | Registro de cliente | 01, 07 | Pantalla 2 | 🔶 Borrador |
| [03](FRONT-03-verificacion-correo.md) | Verificación de correo | 02, 16 | Pantalla 3 | 🔶 Borrador |
| [04](FRONT-04-desafio-otp.md) | Desafío del código de un solo uso | 09, 07 | Pantalla 4 | 🔶 Borrador |
| [05](FRONT-05-recuperar-contrasena.md) | Recuperar contraseña: pedir enlace y definir la nueva | 08, 07, 14 | Pantalla 5 | 🔶 Borrador |
| [06](FRONT-06-mi-cuenta.md) | Mi cuenta: perfil y direcciones | 16 | Pantalla 6 | 🔶 Borrador |
| [07](FRONT-07-admin-listado-usuarios.md) | Panel admin: listado de usuarios | 03, 04, 11, 15 | Pantalla 7 | 🔶 Borrador |
| [08](FRONT-08-admin-detalle-usuario.md) | Panel admin: detalle de usuario | 11, 13, 15 (03, 04, 16) | Pantalla 8 | 🔶 Borrador |
| [09](FRONT-09-segundo-factor.md) | Segundo factor: activar y desactivar | 10 | ⬜ Falta | 🔴 Bloqueada por H-01 y H-02 |
| [10](FRONT-10-cambio-contrasena.md) | Cambiar contraseña | 07 | ⬜ Falta | 🔶 Borrador |
| [11](FRONT-11-admin-alta-usuario.md) | Panel admin: alta de cuenta | 03, 07 | ⬜ Falta | 🔶 Borrador |
| [12](FRONT-12-desbloqueo-con-enlace.md) | Desbloqueo con el enlace del correo | 14 | ⬜ Falta | 🔶 Borrador |
| [13](FRONT-13-actividad-propia.md) | Actividad reciente de mi cuenta | 13 | ⬜ Falta | 🔶 Borrador |
| [14](FRONT-14-admin-auditoria.md) | Panel admin: auditoría | 13 | ⬜ Falta | 🔶 Borrador |

Los wireframes 1 a 8 son los artboards de Stitch (proyecto
`889177073946089595`), todavía con la numeración antigua de specs; el paso a
Figma está pendiente.

---

## Endpoints ↔ pantallas

Todo endpoint que usa una persona desde la SPA tiene al menos una pantalla.
Los de SPEC-17 y SPEC-18 (JWKS, `/auth/token`, introspección, `/usuarios/lote`,
`/roles`, `/permisos`) los consumen máquinas y no tienen pantalla.

| Endpoint | FRONT |
|---|---|
| `POST /auth/login` | 01 |
| `POST /auth/refresh` · `POST /auth/logout` | 00 |
| `POST /auth/registro` | 02 |
| `POST /auth/verificar-correo` · `POST /auth/verificar-correo/reenviar` | 03 |
| `POST /auth/otp/solicitar` · `POST /auth/otp/verificar` | 04 |
| `POST /auth/otp/habilitar` · `POST /auth/otp/deshabilitar` | 09 |
| `GET /password/politica` | 00 (medidor), 02, 05, 10, 11 |
| `POST /password/recuperar` · `POST /password/restablecer` | 05 |
| `POST /password/cambiar` | 10 |
| `POST /auth/desbloquear` | 12 |
| `GET /auth/me` | 06, 09, 10 |
| `GET /auth/me/actividad` | 13 |
| `PATCH /usuarios/{id}/atributos` | 06, 08 |
| `POST /usuarios/{id}/correo` | 06 |
| `GET /usuarios/{id}/direcciones` · `POST /usuarios/{id}/direcciones` | 06 |
| `GET /usuarios` | 07 |
| `POST /usuarios` | 11 |
| `GET /usuarios/{id}` | 08 |
| `DELETE /usuarios/{id}` · `POST /usuarios/{id}/reactivar` | 07, 08 |
| `POST /usuarios/{id}/bloquear` · `POST /usuarios/{id}/desbloquear` | 07, 08 |
| `POST /usuarios/{id}/roles` · `DELETE /usuarios/{id}/roles/{rol}` | 08 |
| `GET /auditoria` | 08, 14 |
| `GET /auditoria/exportar` | 14 |

---

## Huecos detectados en el backend y el contrato

Salieron al redactar las specs de interfaz. **No se ha tocado ninguna spec de
backend ni `openapi.yaml`**: cada hueco se lleva al PO y al responsable de la
spec de backend, que deciden si cambiar el contrato o dejarlo por escrito como
fuera de alcance.

| # | Hueco | Spec de backend | Afecta a | Gravedad |
|---|---|---|---|---|
| H-01 | `Usuario` (respuesta de `/auth/me` y `/usuarios/{id}`) no incluye `mfaHabilitado`, `fechaNacimiento` ni los atributos de vendedor (`codigoVendedor`, `tienda`, `fechaIngreso`), aunque SPEC-16 y SPEC-10 los definen | 16, 10 | 06, 08, 09 | 🔴 Bloquea FRONT-09 |
| H-02 | `POST /auth/otp/habilitar` envía un código, pero no hay endpoint que lo reciba para completar la activación (RF-10.2). `POST /auth/otp/deshabilitar` no recibe código, aunque RF-10.3 lo exige | 10 | 09 | 🔴 Bloquea FRONT-09 |
| H-03 | Tras cambiar el celular no hay forma de verificarlo: el contrato remite a `/auth/otp/solicitar` y `/verificar`, que exigen un `challengeToken` que solo emite el login (relacionado con el acuerdo A2) | 16, 10 | 06 | 🟠 |
| H-04 | Las direcciones solo se listan y se añaden: no se pueden editar, borrar ni cambiar la predeterminada sin crear otra | 16 | 06 | 🟠 |
| H-05 | `429 DEMASIADAS_SOLICITUDES` no indica cuándo reintentar (ni cabecera `Retry-After` ni campo), aunque ESC-09.7 lo pide | 09, 02, 08 | 00, 03, 04, 05 | 🟡 |
| H-06 | No está escrito si pedir un código OTP nuevo invalida el anterior, ni qué responde `/otp/solicitar` con `canal: SMS` en una cuenta sin celular | 09 | 04 | 🟡 |
| H-07 | `GET /auditoria` filtra una sola `accion` por consulta, y SPEC-12 no fija qué lleva `detalle` en `CUENTA_BLOQUEADA` (tipo, motivo, vencimiento) | 13, 12 | 08, 14 | 🟡 |
| H-08 | `GET /auditoria/exportar` no admite `actorId`, `resultado` ni `ip`, aunque RF-13.2 pide el mismo criterio de filtrado que la consulta | 13 | 14 | 🟡 |
| H-09 | `GET /roles` solo acepta token de servicio: el panel no puede leer el nombre ni la descripción de los roles y usa etiquetas propias | 11, 18 | 00, 07, 08, 11 | 🟢 |
| H-10 | `CrearUsuarioRequest.rol` admite `CLIENTE`, aunque RF-03.1 lo excluye, y no hay código de error para ese caso | 03 | 11 | 🟢 |
| H-11 | No hay decisión escrita sobre dónde guarda la SPA el token de refresco (memoria, `sessionStorage`, cookie `HttpOnly`). Condiciona la persistencia de la sesión, el riesgo ante XSS y la coordinación entre pestañas | 06 | 00 | 🟠 Pide un ADR |
| H-12 | No hay de dónde leer el texto vigente de los términos y del tratamiento de datos, ni su versión, aunque RF-01.4 guarda la versión aceptada | 01 | 02 | 🟡 |
| H-13 | No está decidido si el titular puede registrar o corregir su documento: RF-16.2 limita la edición propia a nombres, apellidos y teléfono, pero `AtributosUsuario` acepta el documento | 16 | 06 | 🟡 |
| H-14 | No hay un canal de soporte definido para quien sufre un bloqueo manual | 15 | 01, 12 | 🟢 |
| H-15 | No hay forma de saber si un enlace de recuperación sigue vigente antes de enviar el formulario | 08 | 05 | 🟢 |

🔴 bloquea una pantalla · 🟠 limita una pantalla · 🟡 obliga a una solución
provisional · 🟢 menor.
