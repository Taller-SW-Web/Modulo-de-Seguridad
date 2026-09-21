# Matriz de trazabilidad

| Campo | Valor |
|---|---|
| **Dueño** | Product Owner |
| **Actualización** | En cada aprobación de spec y en cada cambio de contrato |
| **Para qué** | Que ninguna pantalla, endpoint o evento exista sin spec que lo origine, y ninguna spec sin forma de verificarla |

Este documento nació de una omisión: la auditoría llevaba seis menciones en
cuatro specs, un requisito de la API de identidad que dependía de ella y una
pantalla en los wireframes, y aun así no tenía spec. Nadie lo vio porque no había
ninguna tabla donde el hueco se hiciera visible. Esta es esa tabla.

---

## 1. Índice de las dieciocho specs

Son specs **de backend**: cada una define una función del servicio, verificable
por su API. Las specs de interfaz —pantallas, estados, validaciones del lado del
cliente— van aparte, en [`front/`](front/), y las lleva el responsable de UX.

| SPEC | Funcionalidad | Responsable | Hito | Archivo | Estado |
|---|---|---|---|---|---|
| 01 | Registro de clientes | Eva Lucía | 3 | [`SPEC-01-registro-clientes.md`](SPEC-01-registro-clientes.md) | 🔶 Borrador |
| 02 | Verificación de correo | Juan José | 3 | [`SPEC-02-verificacion-correo.md`](SPEC-02-verificacion-correo.md) | 🔶 Borrador |
| 03 | Alta y consulta administrativa de cuentas | Christian | 3 | [`SPEC-03-alta-consulta-cuentas.md`](SPEC-03-alta-consulta-cuentas.md) | 🔶 Borrador |
| 04 | Baja y reactivación de cuentas | Eva Lucía | 3 | [`SPEC-04-baja-reactivacion.md`](SPEC-04-baja-reactivacion.md) | 🔶 Borrador |
| 05 | Inicio de sesión con correo y contraseña | Jose Luis | 3 | [`SPEC-05-inicio-sesion.md`](SPEC-05-inicio-sesion.md) | 🔶 Borrador |
| 06 | Renovación y cierre de sesión | Jose Luis | 3 | [`SPEC-06-renovacion-cierre-sesion.md`](SPEC-06-renovacion-cierre-sesion.md) | 🔶 Borrador |
| 07 | Política y cambio de contraseña | Juan José | 3 · 4 | [`SPEC-07-politica-cambio-contrasena.md`](SPEC-07-politica-cambio-contrasena.md) | 🔶 Borrador |
| 08 | Recuperación de contraseña | Juan José | 4 | [`SPEC-08-recuperacion-contrasena.md`](SPEC-08-recuperacion-contrasena.md) | 🔶 Borrador |
| 09 | Segundo factor en el inicio de sesión (OTP) | Luis David | 4 | [`SPEC-09-mfa-inicio-sesion.md`](SPEC-09-mfa-inicio-sesion.md) | 🔶 Borrador |
| 10 | Activación y desactivación del segundo factor | Luis David | 4 | [`SPEC-10-activacion-mfa.md`](SPEC-10-activacion-mfa.md) | 🔶 Borrador |
| 11 | Gestión de roles y permisos | Eva Lucía | 4 | [`SPEC-11-roles-permisos.md`](SPEC-11-roles-permisos.md) | 🔶 Borrador |
| 12 | Registro de auditoría | Christian | 3 | [`SPEC-12-registro-auditoria.md`](SPEC-12-registro-auditoria.md) | 🔶 Borrador |
| 13 | Consulta y exportación de la auditoría | Christian | 5 | [`SPEC-13-consulta-auditoria.md`](SPEC-13-consulta-auditoria.md) | 🔶 Borrador |
| 14 | Bloqueo automático por intentos fallidos | Luis David | 5 | [`SPEC-14-bloqueo-automatico.md`](SPEC-14-bloqueo-automatico.md) | 🔶 Borrador |
| 15 | Bloqueo y desbloqueo por un administrador | Luis David | 5 | [`SPEC-15-bloqueo-manual.md`](SPEC-15-bloqueo-manual.md) | 🔶 Borrador |
| 16 | Gestión de atributos de usuarios | Eva Lucía | 5 | [`SPEC-16-atributos.md`](SPEC-16-atributos.md) | 🔶 Borrador |
| 17 | Claves públicas, tokens de servicio e introspección | Sergio | 1 · 4 | [`SPEC-17-tokens-servicio.md`](SPEC-17-tokens-servicio.md) | 🔶 Borrador · contrato publicado |
| 18 | Consulta de identidad para los demás módulos | Sergio | 1 · 4 | [`SPEC-18-consulta-identidad.md`](SPEC-18-consulta-identidad.md) | 🔶 Borrador · contrato publicado |

### 1.1 De nueve a dieciocho (20 de septiembre)

En la presentación del Hito 1 el profesor pidió **una spec por función**: el set
tenía que quedar entre 10 y 20 specs, y «gestión de usuarios», por ejemplo,
agrupaba demasiadas funciones distintas. Cada spec antigua se partió por sus
endpoints, que ya iban separados; ninguna regla cambió de contenido y **el
contrato publicado no cambió**. Dos specs quedaron enteras porque ya eran una
sola función: roles (ahora 11) y atributos (ahora 16).

| Antigua | Nuevas |
|---|---|
| SPEC-01 Registro y gestión de usuarios | 01 Registro · 02 Verificación de correo · 03 Alta y consulta · 04 Baja y reactivación |
| SPEC-02 Autenticación | 05 Inicio de sesión · 06 Renovación y cierre de sesión |
| SPEC-03 Gestión de credenciales | 07 Política y cambio · 08 Recuperación |
| SPEC-04 OTP y MFA | 09 Segundo factor en el login · 10 Activación del segundo factor |
| SPEC-05 Roles y permisos | 11, sin cambios |
| SPEC-06 Auditoría | 12 Registro · 13 Consulta y exportación |
| SPEC-07 Bloqueo de cuentas | 14 Bloqueo automático · 15 Bloqueo manual |
| SPEC-08 Atributos | 16, sin cambios (ahora es dueña de `GET /auth/me`) |
| SPEC-09 API de identidad | 17 Tokens de servicio · 18 Consulta de identidad |

La política de contraseñas **sigue sin ser una spec suelta**, como pidió el
profesor el 13 de septiembre: vive en SPEC-07 junto al cambio de contraseña.

Al dividir se añadieron pocos requisitos, y todos salen de algo que la spec
antigua ya decía en otra sección o que el contrato ya publicaba: la auditoría
propia de cada parte, la emisión del par de tokens (RF-05.6), el refresco
vencido (RF-06.4), la política en el alta (RF-03.3) y `GET /password/politica`
(RF-07.11). También se añadieron tres escenarios: ESC-01.4, ESC-03.3 y ESC-03.4.
Los no funcionales marcados como *valor propuesto* los confirma cada
responsable.

**Si ves un número antiguo** —en un PDF entregado, en un artboard de Stitch o en
un commit viejo—, la equivalencia exacta de cada requisito y escenario está en
la §8.

---

Las historias de usuario de cada spec, con los RF y escenarios que cubren, están
en [`historias-usuario.md`](historias-usuario.md). Todo RF y todo escenario
pertenece al menos a una historia.

---

## 2. Specs ↔ endpoints del contrato

✅ publicado en `openapi.yaml`

| Endpoint | SPEC | Estado |
|---|---|---|
| `GET /auth/.well-known/openid-configuration` | 17 | ✅ |
| `GET /auth/.well-known/jwks.json` | 17 | ✅ |
| `POST /auth/token` | 17 | ✅ |
| `POST /auth/introspeccion` | 17 | ✅ |
| `GET /usuarios/{id}` | 18 (la amplían 03 y 15) | ✅ |
| `POST /usuarios/lote` | 18 | ✅ |
| `GET /usuarios/{id}/direcciones` | 16, 18 | ✅ |
| `GET /roles` | 11, 18 | ✅ |
| `GET /permisos` | 11, 18 | ✅ |
| `POST /auth/registro` | 01, 07 | ✅ |
| `POST /auth/verificar-correo` | 02, 16 | ✅ |
| `POST /auth/verificar-correo/reenviar` | 02 | ✅ |
| `GET /usuarios` | 03 | ✅ |
| `POST /usuarios` | 03 | ✅ |
| `DELETE /usuarios/{id}` | 04 | ✅ |
| `POST /usuarios/{id}/reactivar` | 04 | ✅ |
| `POST /auth/login` | 05 | ✅ |
| `POST /auth/refresh` | 06 | ✅ |
| `POST /auth/logout` | 06 | ✅ |
| `GET /password/politica` | 07 | ✅ |
| `POST /password/cambiar` | 07 | ✅ |
| `POST /password/recuperar` | 08 | ✅ |
| `POST /password/restablecer` | 08 | ✅ |
| `POST /auth/otp/solicitar` | 09 | ✅ |
| `POST /auth/otp/verificar` | 09 | ✅ |
| `POST /auth/otp/habilitar` | 10 | ✅ |
| `POST /auth/otp/deshabilitar` | 10 | ✅ |
| `POST /usuarios/{id}/roles` | 11 | ✅ |
| `DELETE /usuarios/{id}/roles/{rol}` | 11 | ✅ |
| `GET /auditoria` | 13 | ✅ |
| `GET /auditoria/exportar` | 13 | ✅ |
| `GET /auth/me/actividad` | 13 | ✅ |
| `POST /auth/desbloquear` | 14 | ✅ |
| `POST /usuarios/{id}/bloquear` | 15 | ✅ |
| `POST /usuarios/{id}/desbloquear` | 15 | ✅ |
| `GET /auth/me` | 16 | ✅ |
| `PATCH /usuarios/{id}/atributos` | 16 | ✅ |
| `POST /usuarios/{id}/correo` | 16 | ✅ |
| `POST /usuarios/{id}/direcciones` | 16 | ✅ |

**Ninguna spec puede quedarse sin endpoint.** Si al redactar la tuya no
encuentras aquí ninguno que la haga observable desde fuera, díselo al PO: o
falta un endpoint, o la spec no es una funcionalidad.

La única excepción es **SPEC-12**, y es deliberada: el registro de auditoría no
tiene endpoint propio porque lo escriben las demás specs. Se observa desde
fuera con los tres endpoints de SPEC-13.

---

## 3. Specs ↔ pantallas

Las ocho del Hito 1, según `docs/plan-hito-1.md` §5. La primera versión en
Stitch las desglosa en 47 artboards, uno por estado, rotulados todavía con la
**numeración antigua** (`SPEC-0X · Pantalla · (letra) estado`); la equivalencia
está arriba, en la §1.1. Cada pantalla tendrá su spec de interfaz en
[`front/`](front/).

| # | Pantalla | SPEC de backend | Interfaz |
|---|---|---|---|
| 1 | Inicio de sesión — sin estado propio de «cuenta bloqueada» | 05, 07, 14 | Valery |
| 2 | Registro de cliente, con medidor de fuerza | 01, 07 | Valery |
| 3 | Verificación de correo | 02 | Valery |
| 4 | Desafío de código OTP | 09 | Luis David |
| 5 | Recuperar contraseña | 08 | Juan José / Valery |
| 6 | Mi cuenta — perfil y direcciones | 16 | Christian |
| 7 | Panel admin — listado de usuarios | 03, 04, 11, 15 | Christian |
| 8 | Panel admin — detalle: roles, bloqueos, historial | 11, 13, 15 | Christian |

SPEC-17 y SPEC-18 no tienen pantalla y es correcto: las consumen máquinas. Su
equivalente a una demo es el `curl` al JWKS. SPEC-06 y SPEC-12 tampoco: ocurren
sin que el usuario lo vea. **SPEC-10 sí necesita una** —activar y desactivar el
segundo factor desde *Mi cuenta*— y hoy no tiene wireframe (ver §7).

---

## 4. Specs ↔ eventos publicados

Detalle en [`catalogo-eventos.md`](catalogo-eventos.md).

| Evento | Publica | Consumen |
|---|---|---|
| `usuario.creado` | 02 (registro verificado) · 03 (alta administrativa) | Los seis módulos |
| `usuario.desactivado` | 04 | Los seis módulos |
| `usuario.reactivado` | 04 | Los seis módulos |
| `usuario.bloqueado` | 14 · 15 | Los seis módulos |
| `usuario.desbloqueado` | 14 · 15 | Los seis módulos |
| `usuario.roles_cambiados` | 11 | Los seis módulos |
| `usuario.atributos_actualizados` | 16 | Marketplace, Retail, Despacho |

Las demás specs no publican eventos. Es intencionado: un inicio de sesión o un
cambio de contraseña no cambia nada que otro módulo necesite saber.

---

## 5. Specs ↔ tablas de datos

Para que Eva pueda cerrar `docs/arquitectura/modelo-datos.md` sin perseguir a
nadie.

| Tabla | Dueña | Comparten |
|---|---|---|
| `usuario` | 01 | 02, 03, 04, 05, 07, 08, 14, 15, 16 |
| `credencial` · `password_historial` | 07 | 01, 03, 05, 08 |
| `token_refresco` | 06 | 04, 05, 08, 09, 11, 15 |
| `otp` · `desafio_mfa` | 09 | 05, 10 |
| `rol` · `permiso` · `usuario_rol` | 11 | 01, 03, 05, 18 |
| `auditoria_seguridad` | **12** | Todas la escriben; la lee 13 |
| `intento_login` · `bloqueo` | 14 | 05, 08, 15 |
| `perfil_cliente` · `perfil_vendedor` · `direccion` | 16 | 01, 18 |
| `cliente_servicio` · `cliente_servicio_scope` | 17 | 18 |

---

## 6. Specs ↔ verificación

Los escenarios *Dado / Cuando / Entonces* de cada spec **son** el plan de
pruebas del Hito 5. La regla de la plantilla: mínimo dos por requisito clave,
uno de ellos caso borde.

| SPEC | Requisitos | Escenarios | Marcados caso borde | Cumple la regla |
|---|---|---|---|---|
| 01 | 7 | 4 | 3 | 🔶 Por revisar |
| 02 | 7 | 5 | 3 | 🔶 Por revisar |
| 03 | 4 | 4 | 3 | 🔶 Por revisar |
| 04 | 4 | 3 | 1 | 🔶 Por revisar |
| 05 | 7 | 5 | 0 | 🔶 Faltan marcas de caso borde |
| 06 | 5 | 4 | 0 | 🔶 Faltan marcas de caso borde |
| 07 | 11 | 9 | 0 | 🔶 Faltan marcas de caso borde |
| 08 | 7 | 6 | 0 | 🔶 Faltan marcas de caso borde |
| 09 | 12 | 11 | 6 | 🔶 Por revisar |
| 10 | 5 | 5 | 2 | 🔶 Por revisar |
| 11 | 10 | 7 | 3 | 🔶 Por revisar |
| 12 | 7 | 7 | 4 | 🔶 Por revisar |
| 13 | 5 | 6 | 2 | 🔶 Por revisar |
| 14 | 12 | 16 | 9 | 🔶 Por revisar |
| 15 | 7 | 9 | 6 | 🔶 Por revisar |
| 16 | 10 | 10 | 4 | 🔶 Por revisar |
| 17 | 11 | 9 | 5 | ✅ |
| 18 | 6 | 7 | 3 | ✅ |

Las cuatro con cero marcas (05 a 08) sí tienen escenarios de error —contraseña
incorrecta, token reutilizado, token vencido—; lo que les falta es rotularlos
*(caso borde)* como pide la plantilla.

---

## 7. Huecos abiertos

Lo que hoy sabemos que falta. Se cierra o se convierte en decisión escrita.

| Hueco | Quién | Para cuándo |
|---|---|---|
| Los permisos de los módulos consumidores (`pedido.crear`…) no están acordados; hoy `permisos` solo lleva los de este módulo | Eva Lucía | Hito 4 |
| Los `client_id` y `client_secret` de los seis módulos no están creados | Christian | Hito 4 |
| El catálogo de eventos no está acordado con los seis equipos | Sergio | Hito 2 |
| Cada responsable revisa su spec dividida, confirma los *valores propuestos* y la aprueba con su propio commit | Los siete | Hito 2 |
| SPEC-05 a SPEC-08 no rotulan sus casos borde | Jose Luis, Juan José | Hito 2 |
| SPEC-10 no tiene pantalla en los wireframes | Valery, Luis David | Hito 2 |
| Los artboards de Stitch siguen con la numeración antigua | Valery | Al pasarlos a Figma |
| Specs de interfaz en [`front/`](front/) | Valery | Por definir |

---

## 8. Equivalencia exacta de números antiguos

Cada requisito y escenario de las nueve specs antiguas, y dónde está ahora.
Se lee así: en la fila de SPEC-01, `4→02.1` significa que el antiguo RF-01.4 es
hoy RF-02.1.

| Antigua | RF: número antiguo → nuevo | ESC: número antiguo → nuevo |
|---|---|---|
| SPEC-01 | 1→01.1 · 2→01.2 · 3→01.3 · 4→02.1 · 5→02.2 · 6→02.3 · 7→02.4 · 8→03.1 · 9→02.5 · 10→01.6 · 11→03.2 · 12→04.1 · 13→04.2 · 14→04.3 · 15→02.6 · 16→01.4 · 17→01.5 | 1→01.1 · 2→01.2 · 3→02.1 · 4→02.2 · 5→02.3 · 6→02.4 · 7→02.5 · 8→03.1 · 9→03.2 · 10→04.1 · 11→04.2 · 12→04.3 · 13→01.3 |
| SPEC-02 | 1→05.1 · 2→05.2 · 3→06.1 · 4→06.2 · 5→06.3 · 6→05.3 · 7→05.4 · 8→05.5 | 1→05.1 · 2→05.2 · 3→05.3 · 4→05.4 · 5→05.5 · 6→06.1 · 7→06.2 · 8→06.3 · 9→06.4 |
| SPEC-03 | 1→07.1 · 2→07.2 · 3→07.3 · 4→07.4 · 5→07.5 · 6→07.6 · 7→07.7 · 8→08.1 · 9→08.2 · 10→08.3 · 11→08.4 · 12→08.5 · 13→07.8 · 14→07.9 · 15→07.10 | 1→07.1 · 2→07.2 · 3→07.3 · 4→07.4 · 5→07.5 · 6→07.6 · 7→07.7 · 8→08.1 · 9→08.2 · 10→08.3 · 11→08.4 · 12→08.5 · 13→07.8 · 14→07.9 · 15→08.6 |
| SPEC-04 | 1→09.1 · 2→09.2 · 3→09.3 · 4→09.4 · 5→09.5 · 6→09.6 · 7→09.7 · 8→09.8 · 9→09.9 · 10→09.10 · 11→10.1 · 12→10.2 · 13→10.3 · 14→10.4 · 15→09.12 · 16→09.11 | 1→09.1 · 2→09.2 · 3→09.3 · 4→09.4 · 5→09.5 · 6→09.6 · 7→09.7 · 8→09.8 · 9→09.9 · 10→09.10 · 11→10.1 · 12→10.2 · 13→10.3 · 14→09.11 · 15→10.4 · 16→10.5 |
| SPEC-05 | 1→11.1 · 2→11.2 · 3→11.3 · 4→11.4 · 5→11.5 · 6→11.6 · 7→11.7 · 8→11.8 · 9→11.9 · 10→11.10 | 1→11.1 · 2→11.2 · 3→11.3 · 4→11.4 · 5→11.5 · 6→11.6 · 7→11.7 |
| SPEC-06 | 1→12.1 · 2→12.2 · 3→12.3 · 4→12.4 · 5→12.5 · 6→12.6 · 7→13.1 · 8→13.2 · 9→13.3 · 10→13.4 · 11→12.7 · 12→13.5 | 1→12.1 · 2→12.2 · 3→12.3 · 4→12.4 · 5→13.1 · 6→13.2 · 7→13.3 · 8→13.4 · 9→13.5 · 10→12.5 · 11→12.6 · 12→13.6 · 13→12.7 |
| SPEC-07 | 1→14.1 · 2→14.2 · 3→14.3 · 4→14.4 · 5→14.5 · 6→14.6 · 7→14.7 · 8→15.1 · 9→15.2 · 10→15.3 · 11→15.4 · 12→14.8 · 13→14.9 · 14→14.10 · 15→15.5 · 16→14.11 · 17→14.12 · 18→15.6 | 1→14.1 · 2→14.2 · 3→14.3 · 4→14.4 · 5→14.5 · 6→14.6 · 7→14.7 · 8→15.1 · 9→15.2 · 10→15.3 · 11→15.4 · 12→15.5 · 13→15.6 · 14→14.8 · 15→14.9 · 16→15.7 · 17→14.10 · 18→14.11 · 19→14.12 · 20→14.13 · 21→14.14 · 22→14.15 · 23→14.16 · 24→15.8 · 25→15.9 |
| SPEC-08 | 1→16.1 · 2→16.2 · 3→16.3 · 4→16.4 · 5→16.5 · 6→16.6 · 7→16.7 · 8→16.8 · 9→16.9 · 10→16.10 | 1→16.1 · 2→16.2 · 3→16.3 · 4→16.4 · 5→16.5 · 6→16.6 · 7→16.7 · 8→16.8 · 9→16.9 · 10→16.10 |
| SPEC-09 | 1→17.1 · 1b→17.2 · 2→17.3 · 3→17.4 · 4→17.5 · 5→17.6 · 6→17.7 · 7→18.1 · 8→18.2 · 9→18.3 · 10→18.4 · 11→17.8 · 12→17.9 · 13→18.5 · 14→18.6 · 15→17.10 · 16→17.11 | 1→17.1 · 2→17.2 · 3→17.3 · 4→17.4 · 5→17.5 · 6→17.6 · 7→18.1 · 8→18.2 · 9→18.3 · 10→18.4 · 11→18.5 · 12→18.6 · 13→17.7 · 14→17.8 · 15→18.7 · 16→17.9 |
