# Specs de interfaz

| Campo | Valor |
|---|---|
| **Dueño** | Dueño de cada épica; Valery revisa UX |
| **Estado** | 15 specs con responsables por épica; aprobación funcional pendiente |
| **Plantilla** | [`_PLANTILLA-FRONT.md`](_PLANTILLA-FRONT.md) |
| **Cómo se programa** | [`../componentes-react/`](../componentes-react/): una spec de componentes React por pantalla, con el mismo número |
| **Sistema de diseño** | [`../../docs/diseno/sistema-diseno.md`](../../docs/diseno/sistema-diseno.md) |

Las 18 specs de `specs/funcionales/` son **de backend**: dicen qué hace el servicio y se
verifican contra su API. Esta carpeta guarda las **specs de interfaz**: cómo se
ve y cómo se comporta cada pantalla de la SPA. Van aparte por indicación del
profesor, y las mantiene el dueño de la funcionalidad completa; UX revisa.


**Aprobación incremental H3:** Luis comunica revisión y aprobación de alcance
y diseño UI por los siete dueños. Se registra para los recorridos elegidos
FRONT-00/01/04/06/07/08/10/12/13, no para todos los flujos posteriores.
[Planes aprobados y diseño técnico cerrado](../../plan/hito-3/CIERRE-PLAN.md).

## Responsables por pantalla y bloque

La selección de [H3 por estudiante](../../plan/hito-3/README.md) se lee junto
con estas specs completas. FRONT-00/06/07/08/13 concretan su alcance visual
H3; la aprobación del incremento de los siete dueños fue comunicada por Luis
el 12-oct. El alcance futuro de las specs conserva su revisión propia. No anunciar flujos posteriores
como disponibles ni sustituir respuestas del servidor por éxito ficticio.

Reparto vigente del 11-oct-2026; no implica aprobación de pantallas. [Dueños completos](../../docs/responsabilidades.md) y [épicas](../epicas/README.md).

| FRONT | Responsable |
|---|---|
| 00 | Jose Luis |
| 01 | Jose Luis |
| 02 | Eva Lucía |
| 03 | Eva Lucía |
| 04 | Luis David |
| 05 | Juan José |
| 06 | Valery |
| 07 | Eva Lucía (contenedor; bloques por épica) |
| 08 | Eva Lucía (contenedor; bloques por épica) |
| 09 | Luis David |
| 10 | Juan José |
| 11 | Eva Lucía |
| 12 | Luis David |
| 13 | Christian |
| 14 | Christian |

FRONT-00 integra sesión (Jose Luis), política (Juan José) y autorización (Sergio). FRONT-07/08 integra cuentas (Eva), roles (Sergio), bloqueo (Luis David), atributos (Valery) y auditoría (Christian); cada dueño desarrolla su bloque de punta a punta. Lotes y revisión UX no dividen el trabajo por capas.

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
| [09](FRONT-09-segundo-factor.md) | Segundo factor: activar y desactivar | 10 | ⬜ Falta | Definida local; publicación/implementación pendientes |
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
| `POST /api/v2/auth/mfa/cambios` · `/reenviar` · `/confirmar` | 09 |
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
| `POST /api/v2/usuarios` | 11 |
| `GET /usuarios/{id}` | 08 |
| `DELETE /usuarios/{id}` · `POST /usuarios/{id}/reactivar` | 07, 08 |
| `POST /usuarios/{id}/bloquear` · `POST /usuarios/{id}/desbloquear` | 07, 08 |
| `POST /usuarios/{id}/roles` · `DELETE /usuarios/{id}/roles/{rol}` | 08 |
| `GET /auditoria` | 08, 14 |
| `GET /auditoria/exportar` | 14 |

---

## Huecos detectados en el backend y el contrato

El estado vigente de H-01 a H-15, sus dueños, prioridades y cierres están en el [registro único](../../docs/registro-inconsistencias.md). Esta tabla conserva el diagnóstico de origen, no un segundo seguimiento.

Diagnóstico histórico al redactar interfaz. En el corte 11-oct se editaron
specs y contrato por delegación del PO informada por Luis. La tabla siguiente
conserva los problemas de origen; su resolución vigente está en el registro.

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


## Tratamientos del corte 11-oct

La tabla H-01–H-15 anterior conserva diagnóstico de origen.
El [registro vigente](../../docs/registro-inconsistencias.md) gobierna su estado.
Memoria de sesión definida (H-11); FRONT-11 alineada con A6; direcciones
limitadas a listado/creación; sin OTP para verificar celular; exportación
aplazada. FRONT-09 está definida en contrato local v2; publicación y servicios pendientes. El recorrido mínimo de
Ventas no convierte los pendientes de registro/MFA/atributos en resueltos.
