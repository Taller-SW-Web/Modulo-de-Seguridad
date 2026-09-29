# Inconsistencias del proyecto

| Campo | Valor |
|---|---|
| **Dueño** | Product Owner |
| **Revisión** | 26 al 28 de septiembre de 2026, sobre `main` en `6e14777`; los archivos citados no cambian hasta `1b8f2e3` |
| **Alcance** | Specs de backend, `openapi.yaml`, catálogos, kit de integración, glosario y documentos de `docs/` |
| **Estado** | Abierto: ninguna corregida todavía |

Este documento reúne las contradicciones que aparecieron al comparar entre sí
los documentos del proyecto: dos archivos que dicen cosas distintas sobre lo
mismo, o una regla que alguien da por hecha y ninguna spec escribe. **No corrige
nada**: cada punto dice dónde está el problema y qué hay que decidir, para que
lo resuelva quien sea dueño del archivo.

No incluye los huecos que salieron al redactar las specs de interfaz (H-01 a
H-15): están en [`specs/front/README.md`](../specs/front/README.md#huecos-detectados-en-el-backend-y-el-contrato).

Gravedad: 🔴 rompe algo al implementar o al integrarse · 🟠 deja una regla sin
definir · 🟡 documentación desalineada · 🟢 menor.

---

## Resumen

| # | Inconsistencia | Dónde | Dueño | Gravedad |
|---|---|---|---|---|
| 1 | Una contraseña corta en el registro recibe `400` en vez de `422` | `openapi.yaml`, SPEC-07 | PO, Juan José | 🔴 |
| 2 | ~~`iss` tiene dos valores distintos~~ ✅ resuelto | Kit, `openapi.yaml` | PO | 🔴 |
| 3 | ~~No está escrito cómo viajan los scopes dentro del token, ni si se exige `aud`~~ ✅ resuelto | SPEC-17, `openapi.yaml` | PO | 🔴 |
| 4 | `mfa_requerido` frente a `mfaRequerido` | SPEC-05, contrato | Jose Luis | 🔴 |
| 5 | `caducidadDiasAdmin` depende del rol en un endpoint público | `openapi.yaml`, SPEC-07 | PO, Juan José | 🟠 |
| 6 | El límite de 90 días es «más de» en un sitio y «o más» en otro | SPEC-07, catálogo, contrato | Juan José | 🟠 |
| 7 | La recuperación responde `429` sin que SPEC-08 defina el límite | SPEC-08, catálogo, contrato | Juan José | 🟠 |
| 8 | Un escenario acepta dos códigos de error distintos | SPEC-09 | Luis David | 🟠 |
| 9 | Cerrar sesión exige un token de acceso vigente | `openapi.yaml`, SPEC-06 | Jose Luis | 🟠 |
| 10 | Casos sin respuesta definida en desbloquear y reactivar | SPEC-15, SPEC-04, contrato | Luis David, Eva Lucía | 🟠 |
| 11 | Nadie define el «canal preferido» del segundo factor | `openapi.yaml`, SPEC-09 | Luis David | 🟠 |
| 12 | Se cita la decisión «Q10», que no existe | SPEC-01, SPEC-04 | PO | 🟠 |
| 13 | ~~SPEC-17 y el kit dicen cosas distintas sobre cómo se piden los scopes~~ ✅ resuelto | SPEC-17, kit | PO | 🟡 |
| 14 | La trazabilidad cuenta 7 requisitos en SPEC-02, que tiene 8 | `trazabilidad.md` | PO | 🟡 |
| 15 | El catálogo atribuye `CREDENCIALES_INVALIDAS` solo a SPEC-05 | Catálogo de errores | PO | 🟡 |
| 16 | `NoEncontrado` dice que solo se alcanza con token de servicio | `openapi.yaml` | PO | 🟡 |
| 17 | La cabecera del contrato cita solo SPEC-17 y SPEC-18 | `openapi.yaml` | PO | 🟢 |
| 18 | Un plan de specs no figura en ningún índice | `specs/` | PO | 🟢 |
| 19 | El glosario prohíbe «dar de baja», que usan las specs y el contrato | `CONTEXT.md` | PO | 🟢 |
| 20 | `SCOPE_INSUFICIENTE` se devuelve a personas sin permiso, no solo a módulos | Glosario, catálogo | PO | 🟢 |

Aparte, cuatro documentos se citan y todavía no existen (ver [al final](#documentos-citados-que-no-existen)).

---

## 🔴 Rompen algo al implementar o al integrarse

### 1. Una contraseña corta en el registro recibe el error equivocado

- `specs/openapi.yaml:2608` — `RegistroRequest.contrasena` declara `minLength: 10`.
- SPEC-07 y el catálogo de errores — toda regla de la política se responde con
  un único `422 POLITICA_INCUMPLIDA` y la lista de reglas falladas en `errores[]`.

Un validador de esquema (el de Spring, o el entorno simulado) rechaza `Abc1!`
con `400 VALIDACION` antes de llegar a la política. El cliente no recibe
`LONGITUD_MINIMA` y el medidor de fuerza no puede marcarla. Además, ESC-07.2
espera que el rechazo sea el de la política.

**Por decidir:** quitar `minLength` del esquema y dejar la regla solo en la
política, o documentar que la longitud es la única regla que responde `400`.

### 2. `iss` tiene dos valores distintos

- `specs/kit-integracion.md:122` y `:170` — `iss: "auth-service"`.
- `specs/openapi.yaml:224` — el documento de descubrimiento declara
  `issuer: "http://localhost:8080/api/v1/auth"`.

Spring configura la validación con `issuer-uri` y exige que el `iss` del token
coincida con el `issuer` publicado: si los valores difieren, rechaza todos los
tokens. El canal Chatbot ya programa contra `auth-service` (su ADR-0009) y
Despacho pide confirmar el valor (§3.3 y §8.1 de su `api-contract.md`).

**Por decidir:** un único valor de `iss`, igual en el kit, en el documento de
descubrimiento y en los tokens emitidos.

> ✅ **Resuelto** en el acuerdo A4 (PR #13): `iss` = `issuer` del descubrimiento
> (RF-17.14) y el kit ya lo usa. **Falta avisar a Chatbot**, que programa contra
> `auth-service`.

### 3. No está escrito cómo viajan los scopes dentro del token, ni si se exige `aud`

- `specs/openapi.yaml:2454` — la respuesta de `POST /auth/token` trae `scopes`
  como arreglo.
- Ni SPEC-17 ni el kit dicen qué claim lleva los scopes **dentro del JWT**: el
  estándar usa `scope` como texto separado por espacios; nuestra respuesta, un
  arreglo.
- Tampoco se dice si el token lleva `aud`.

Despacho pregunta las dos cosas por escrito en su contrato y, hasta tener
respuesta, no puede validar los tokens de servicio.

**Por decidir:** nombre y forma del claim de scopes, y si existe `aud`.

> ✅ **Resuelto** en el acuerdo A4 (PR #13): claim `scope` en texto (RF-17.13) y
> `aud` con las APIs dueñas (RF-17.12), que salen de la tabla `scope`.

### 4. `mfa_requerido` frente a `mfaRequerido`

- `specs/SPEC-05-inicio-sesion.md:118` — ESC-05.5 muestra `"mfa_requerido": true`.
- `DesafioMfa` en el contrato y ESC-09.8 usan `mfaRequerido`.

Una prueba escrita a partir de SPEC-05 fallará contra el contrato.

**Corrección:** cambiar ESC-05.5 a `mfaRequerido`, que es el nombre publicado.

---

## 🟠 Reglas sin definir

### 5. `caducidadDiasAdmin` depende del rol en un endpoint público

- `specs/openapi.yaml:2703` — `caducidadDiasAdmin` es «`null` para el resto de roles».
- `specs/openapi.yaml:1339` — `GET /password/politica` es público (`security: []`):
  no sabe quién pregunta ni qué rol tiene.

**Por decidir:** que el campo siempre valga 90 y describa la regla de los roles
de gestión, o que el endpoint se vuelva autenticado.

### 6. El límite de 90 días es «más de» en un sitio y «o más» en otro

- `specs/catalogo-errores.md:112` y el contrato — contraseña de **más de** 90 días.
- `specs/SPEC-07-politica-cambio-contrasena.md:102` — ESC-07.7 dice **90 días o más**.

El día 90 exacto se trata distinto según qué documento se lea.

**Por decidir:** una sola frontera, escrita igual en los tres sitios.

### 7. La recuperación responde `429` sin que SPEC-08 defina el límite

- `specs/catalogo-errores.md:90` — `DEMASIADAS_SOLICITUDES` cubre «recuperaciones repetidas».
- El contrato — `POST /password/recuperar` responde `429`.
- SPEC-08 — no tiene ningún requisito de límite: ni cuántas solicitudes ni en qué ventana.

**Por decidir:** el límite (por correo, como el reenvío de verificación de
RF-02.4, para no revelar qué cuentas existen) y añadirlo como requisito de SPEC-08.

### 8. Un escenario acepta dos códigos de error distintos

- `specs/SPEC-09-mfa-inicio-sesion.md:130` — ESC-09.11 espera «`401 TOKEN_INVALIDO`
  o `401 CODIGO_INVALIDO`» ante un desafío inexistente o manipulado.
- El contrato — `/auth/otp/solicitar` documenta `TOKEN_INVALIDO`; `/auth/otp/verificar`
  no lo documenta.

Una prueba no puede verificar un «o».

**Por decidir:** un código para cada endpoint, y documentarlo en `/auth/otp/verificar`.

### 9. Cerrar sesión exige un token de acceso vigente

- `specs/openapi.yaml:110` — la seguridad global es `tokenUsuario`.
- `specs/openapi.yaml:1087` — `POST /auth/logout` no la sobreescribe, así que la hereda.

Si el token de acceso ya venció, hay que renovar la sesión para poder cerrarla.
SPEC-06 no lo menciona.

**Por decidir:** que `/auth/logout` sea público y se autorice solo con el
`refreshToken` del cuerpo, o dejar escrito en SPEC-06 que exige un token vigente.

### 10. Casos sin respuesta definida en desbloquear y reactivar

- `POST /usuarios/{id}/desbloquear` (SPEC-15) — no dice qué responde sobre una
  cuenta que no está `BLOQUEADO`.
- `specs/openapi.yaml:496` — `POST /usuarios/{id}/reactivar` «no hace nada y
  responde igual» sobre una cuenta que no está `INACTIVO`. La regla está solo en
  el contrato: SPEC-04 no la tiene.

**Por decidir:** el comportamiento de desbloquear, y llevar la regla de
reactivar a SPEC-04 con su escenario.

### 11. Nadie define el «canal preferido» del segundo factor

- `specs/openapi.yaml:2674` — `DesafioMfa.canal` es el «canal preferido de la cuenta».
- Ninguna spec dice dónde se guarda, quién lo elige ni cuál es su valor por defecto.

**Por decidir:** si el canal preferido existe (y entonces SPEC-09 o SPEC-10 lo
define) o si el campo vale siempre `EMAIL` mientras el SMS siga simulado.

### 12. Se cita la decisión «Q10», que no existe

- `specs/SPEC-01-registro-clientes.md:114` y `specs/SPEC-04-baja-reactivacion.md:94` —
  la baja por decisión del titular está «pendiente de decisión (Q10)».
- No hay en el repositorio ninguna lista de preguntas donde esté Q10.

**Por decidir:** registrar la pregunta donde se lleven las decisiones abiertas
(`trazabilidad.md` §7 o un issue) y citar ese lugar.

---

## 🟡 Documentación desalineada

### 13. SPEC-17 y el kit dicen cosas distintas sobre cómo se piden los scopes

- `specs/SPEC-17-tokens-servicio.md:108` — los scopes «se conceden por escrito en
  la sincronización entre equipos, **no por petición**».
- `specs/kit-integracion.md:268` y `docs/integracion/acuerdos.md` — «pídanlo por
  **issue** con la etiqueta `integracion`».

**Corrección:** alinear SPEC-17 con el proceso del kit, que es el que se usó para A1, A2 y A3.

> ✅ **Resuelto**: SPEC-17 ya dice que se piden por issue y se conceden en `acuerdos.md`.

### 14. La trazabilidad cuenta 7 requisitos en SPEC-02, que tiene 8

- `specs/trazabilidad.md:216` — SPEC-02 con 7 requisitos.
- `specs/SPEC-02-verificacion-correo.md:53` — RF-02.8, añadido con el acuerdo A1.

Se comprobaron las 18 filas de la tabla §6: es la única que no coincide.

**Corrección:** 8 requisitos en la fila de SPEC-02.

### 15. El catálogo atribuye `CREDENCIALES_INVALIDAS` solo a SPEC-05

- `specs/catalogo-errores.md:81` — columna Spec: `05`.
- También lo devuelven `POST /password/cambiar` (SPEC-07) y
  `POST /usuarios/{id}/correo` (SPEC-16), cuando la contraseña actual no coincide.

**Corrección:** `05, 07, 16`, y mencionar ese segundo significado en la descripción.

### 16. `NoEncontrado` dice que solo se alcanza con token de servicio

- `specs/openapi.yaml:2314` — «Solo se llega aquí con un token de servicio válido
  y el scope correcto».
- La misma respuesta la usan los endpoints de administración con token de usuario
  (`DELETE /usuarios/{id}`, roles, bloqueo…).

**Corrección:** «con un token válido y el permiso o scope necesario».

### 17. La cabecera del contrato cita solo SPEC-17 y SPEC-18

- `specs/openapi.yaml:49` — «Especificaciones de origen: SPEC-17 y SPEC-18».
- Hoy el contrato cubre las 18 specs.

**Corrección:** citar `specs/trazabilidad.md` §2, que tiene el mapa endpoint → spec.

### 18. Un plan de specs no figura en ningún índice

- `specs/SPEC-17-18-api-identidad.plan.md` no aparece en `trazabilidad.md` ni en
  el `README.md`.

**Por decidir:** enlazarlo desde la trazabilidad o moverlo fuera de `specs/`,
donde su nombre se confunde con el de una spec.

---

## 🟢 El glosario frente al uso real

### 19. El glosario prohíbe «dar de baja», que usan las specs y el contrato

- `CONTEXT.md:100` — «dar de baja» está entre los términos a evitar para *baja lógica*.
- SPEC-04, SPEC-11, `openapi.yaml`, el prompt de wireframes y las specs de
  interfaz lo usan en todas partes.

**Por decidir:** aceptarlo en el glosario como el verbo de *baja lógica*, o
reemplazarlo en todos los documentos. Lo primero es más barato y es como habla el equipo.

### 20. `SCOPE_INSUFICIENTE` se devuelve a personas sin permiso, no solo a módulos

- `CONTEXT.md` separa *scope* (módulos) de *permiso* (personas).
- `specs/catalogo-errores.md:87` — `SCOPE_INSUFICIENTE` es también la respuesta a
  una persona sin el permiso necesario, en ocho specs.

El código ya está publicado y no se renombra.

**Corrección:** dejar escrita la excepción en el glosario y en el catálogo.

---

## Documentos citados que no existen

Se citan desde otros archivos y todavía no se han escrito. No son errores:
casi todos figuran como tareas pendientes. Se listan para que no se pierdan.

| Documento | Lo cita | Responsable según el plan |
|---|---|---|
| `docs/arquitectura/modelo-datos.md` | `trazabilidad.md` §5 | Eva Lucía |
| `docs/arquitectura/implementacion.md` | `docs/plan-hito-1.md`, `docs/arquitectura/README.md` (tarea pendiente) | Jose Luis |
| `docs/arquitectura/despliegue.md` | `docs/plan-hito-1.md`, `docs/arquitectura/README.md` (tarea pendiente) | Christian |
| `docs/wireframes/README.md` | `docs/wireframes/PROMPT-claude-design.md`: ahí va el enlace a Figma | Valery |
