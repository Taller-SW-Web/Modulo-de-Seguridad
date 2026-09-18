# Catálogo de códigos de error

| Campo | Valor |
|---|---|
| **Dueño** | Product Owner — igual que el contrato |
| **Aplica a** | Las nueve specs y `specs/openapi.yaml` |
| **Estado** | Borrador — los códigos marcados ✅ ya están publicados y no se tocan |

Este documento existe porque los códigos de error estaban dispersos: cada spec
inventaba los suyos y nadie comprobaba que dos specs no llamaran distinto a lo
mismo. Un consumidor que ramifica por `code` necesita que ese código sea estable
y único, así que el catálogo tiene un solo dueño.

---

## Convenciones

Todas las respuestas de error usan `application/problem+json` (RFC 7807) con
esta forma:

```json
{
  "type": "https://g7.unmsm.pe/errores/credenciales-invalidas",
  "title": "Credenciales inválidas",
  "status": 401,
  "code": "CREDENCIALES_INVALIDAS",
  "detail": "El correo o la contraseña no coinciden."
}
```

- **Ramifica por `code`, nunca por `detail`.** El texto de `detail` puede
  cambiar sin aviso; el `code` no.
- Los códigos son **ASCII, mayúsculas, con guion bajo**. Sin acentos, sin ñ.
- El `type` es el `code` en minúsculas con guiones, colgando de
  `https://g7.unmsm.pe/errores/`.
- Un código nuevo **se añade al final de este documento y al contrato en la
  misma sesión de trabajo**. Un código que solo existe en el código fuente no
  existe.

---

## La regla que gobierna la mitad del catálogo

**Ningún error puede permitir averiguar si una cuenta existe.**

Es la razón por la que este catálogo tiene menos códigos de los que cabría
esperar: varios estados distintos comparten un mismo código a propósito. Un
atacante que pueda distinguir «contraseña incorrecta» de «ese correo no existe»
obtiene una lista de clientes del marketplace probando correos.

Por eso:

| Situación real | Lo que responde el sistema |
|---|---|
| Contraseña incorrecta | `401 CREDENCIALES_INVALIDAS` |
| El correo no está registrado | `401 CREDENCIALES_INVALIDAS` — idéntico |
| Cuenta bloqueada, **con cualquier contraseña** | `401 CREDENCIALES_INVALIDAS` — idéntico |
| Cuenta inactiva, con cualquier contraseña | `401 CREDENCIALES_INVALIDAS` — idéntico |
| Cuenta sin verificar, con cualquier contraseña | `401 CREDENCIALES_INVALIDAS` — idéntico |
| Recuperación con correo existente | `202` sin cuerpo |
| Recuperación con correo inexistente | `202` sin cuerpo — idéntico, y con la misma latencia |

**Sin la contraseña correcta de una cuenta `ACTIVO`, el login responde siempre
el mismo `401`.** Si una cuenta bloqueada respondiera distinto con la contraseña
correcta, el bloqueo no frenaría un ataque de fuerza bruta, solo le cambiaría el
mensaje de éxito. Quien queda bloqueado se entera por el correo de aviso que
exige SPEC-07. Solo quien completa la autenticación de una cuenta `ACTIVO` puede
recibir otra cosa: tokens, un desafío, o `403 PASSWORD_CADUCADA`.

---

## Códigos ya publicados en el contrato

No se renombran: hay seis equipos que van a ramificar por ellos.

| Código | HTTP | Spec | Cuándo se devuelve |
|---|---|---|---|
| `VALIDACION` ✅ | 400 | Todas | El cuerpo de la petición no cumple el esquema: falta un campo, el tipo no corresponde, el formato del correo es inválido |
| `LOTE_DEMASIADO_GRANDE` ✅ | 400 | 09 | La consulta por lote trae más de 100 identificadores |
| `TOKEN_INVALIDO` ✅ | 401 | Todas | Falta el token, la firma no valida o ya venció |
| `CREDENCIALES_INVALIDAS` ✅ | 401 | 02 | Correo o contraseña incorrectos, **o el correo no existe, o la cuenta no está `ACTIVO`** |
| `REFRESCO_INVALIDO` ✅ | 401 | 02 | El token de refresco no existe, ya se usó o fue revocado |
| `CODIGO_INVALIDO` ✅ | 401 | 04 | El código OTP no coincide con el desafío |
| `CLIENTE_INVALIDO` ✅ | 401 | 09 | El `client_id` o el `client_secret` del módulo consumidor no son válidos |
| `CUENTA_NO_DISPONIBLE` ✅ | 403 | 07 | Un administrador intenta bloquear una cuenta `INACTIVO` o `PENDIENTE_VERIFICACION`. **Ya no lo devuelve el login**, que responde `401` |
| `TOKEN_NO_APLICABLE` ✅ | 403 | 09 | Un token de servicio intenta una operación que actúa en nombre de una persona |
| `SCOPE_INSUFICIENTE` ✅ | 403 | 01, 05, 06, 07, 08, 09 | El token es válido pero no tiene el permiso necesario. **No se revela cuál haría falta** |
| `NO_ENCONTRADO` ✅ | 404 | 01, 05, 07, 08, 09 | No existe el recurso. Solo se llega aquí con token y permiso válidos |
| `CORREO_NO_DISPONIBLE` ✅ | 409 | 01 | El correo ya está registrado. El texto no confirma ni niega la existencia de la cuenta |
| `DEMASIADAS_SOLICITUDES` ✅ | 429 | 01, 03, 04 | Se superó el límite de solicitudes: más de 3 OTP en 15 min, más de 3 reenvíos de verificación en una hora, o recuperaciones repetidas |
| `NO_DISPONIBLE` ✅ | 503 | Todas | Dependencia caída: base de datos, cola de correo o auditoría crítica |

---

## Códigos propios de cada spec

Nacieron al redactar las specs y **ya están todos publicados en `openapi.yaml`**.
Si una spec necesita uno nuevo, se añade aquí y al contrato en el mismo cambio.

### SPEC-01 — Registro

| Código | HTTP | Cuándo |
|---|---|---|
| `ENLACE_EXPIRADO` | 410 | El enlace de verificación de correo tiene más de 24 h |
| `ENLACE_YA_USADO` | 410 | El enlace de verificación ya se consumió |

### SPEC-03 — Credenciales y contraseñas

| Código | HTTP | Cuándo |
|---|---|---|
| `POLITICA_INCUMPLIDA` | 422 | La contraseña no cumple la política. **Un solo código para todas las reglas**, historial incluido; cuál falló va en el array `errores` (`LONGITUD_MINIMA`, `MAYUSCULA`, `MINUSCULA`, `DIGITO`, `CARACTER_ESPECIAL`, `CONTRASENA_COMUN`, `DATOS_PERSONALES`, `YA_UTILIZADA`) |
| `PASSWORD_CADUCADA` | 403 | Quien tiene un rol de gestión completa la autenticación con una contraseña de más de 90 días. No recibe tokens: la restablece con el flujo de recuperación. Lo devuelven `/auth/login` y `/auth/otp/verificar` |
| `TOKEN_RECUPERACION_INVALIDO` | 401 | El token de recuperación no existe, ya se usó, o fue reemplazado por una solicitud posterior |
| `TOKEN_RECUPERACION_EXPIRADO` | 410 | El token de recuperación tiene más de 30 minutos |

> **Por qué un solo `POLITICA_INCUMPLIDA` y no cinco códigos.** Las specs en PDF
> proponían `PASSWORD_DEMASIADO_CORTA`, `PASSWORD_SIN_CARACTER_ESPECIAL`,
> `PASSWORD_CONTIENE_DATOS_PERSONALES` y demás. Un código por regla obliga al
> frontend a conocer la política, que es justo lo que `GET /password/politica`
> viene a evitar. Con un código y un array de reglas incumplidas, añadir una
> regla nueva no rompe a nadie:
>
> ```json
> {
>   "code": "POLITICA_INCUMPLIDA",
>   "status": 422,
>   "errores": [
>     { "regla": "LONGITUD_MINIMA", "esperado": 10 },
>     { "regla": "CARACTER_ESPECIAL" }
>   ]
> }
> ```

### SPEC-04 — OTP y MFA

| Código | HTTP | Cuándo |
|---|---|---|
| `OTP_EXPIRADO` | 410 | El código tiene más de 5 minutos |
| `OTP_INTENTOS_AGOTADOS` | 401 | Se agotaron los 3 intentos; el OTP queda invalidado |
| `MFA_OBLIGATORIO` | 422 | Una cuenta con algún rol de gestión (`ADMIN_VENTAS`, `GESTOR_DESPACHO`, `GESTOR_COMERCIAL`, `ADMIN_SISTEMA`) intenta desactivar su segundo factor. Si tiene varios roles, manda el más estricto |

### SPEC-06 — Auditoría

| Código | HTTP | Cuándo |
|---|---|---|
| `EXPORTACION_DEMASIADO_GRANDE` | 413 | El filtro abarca más de 100 000 registros |

### SPEC-07 — Bloqueo

| Código | HTTP | Cuándo |
|---|---|---|
| `TOKEN_DESBLOQUEO_INVALIDO` | 401 | El enlace de desbloqueo no existe, ya se usó o fue reemplazado por un bloqueo posterior |
| `TOKEN_DESBLOQUEO_EXPIRADO` | 410 | El enlace de desbloqueo tiene más de 30 minutos |
| `ADMINISTRADOR_PROTEGIDO` | 422 | Un administrador intenta bloquearse, darse de baja o quitarse el rol `ADMIN_SISTEMA` a sí mismo, o hacerlo con el último `ADMIN_SISTEMA` activo. **Un solo código para SPEC-01, SPEC-05 y SPEC-07**, porque es una sola regla |

### SPEC-08 — Atributos

| Código | HTTP | Cuándo |
|---|---|---|
| `ATRIBUTO_NO_APLICABLE` | 422 | Se intenta asignar un atributo de vendedor a un cliente, o al revés |

---

## Códigos prohibidos

Aparecen en las specs en PDF y **no deben implementarse**. Cada uno filtra
información o duplica algo que ya existe.

| No usar | Usar en su lugar | Por qué |
|---|---|---|
| `423 Locked` con el motivo del bloqueo | `401 CREDENCIALES_INVALIDAS` | Un `423` confirma que la cuenta existe **y** que está bloqueada. Con eso se enumera y además se sabe a quién se ha conseguido bloquear |
| `403 CUENTA_NO_DISPONIBLE` en el login | `401 CREDENCIALES_INVALIDAS` | Con la contraseña correcta, un `403` le avisa al atacante de que acertó, aunque la cuenta esté bloqueada |
| `AUTH_EMAIL_YA_REGISTRADO` | `CORREO_NO_DISPONIBLE` | Mismo significado, nombre que afirma de más |
| `TOKEN_EXPIRED` / `TOKEN_INVALID` (verificación de correo) | `ENLACE_EXPIRADO` / `ENLACE_YA_USADO` | Los códigos van en español (ADR-004) y ya existen |
| `BAD_REQUEST`, `FORBIDDEN`, `UNPROCESSABLE_ENTITY` | `VALIDACION`, `SCOPE_INSUFICIENTE`, el código concreto del caso | Son nombres de estado HTTP, no códigos: no dicen nada que el `status` no diga ya |
| `SCOPE_INSUFFICIENT` | `SCOPE_INSUFICIENTE` | Errata en inglés de un código publicado |
| `BLOQUEO_NO_PERMITIDO` | `ADMINISTRADOR_PROTEGIDO` | Retirado el mismo día en que se creó: la misma protección cubre también la baja y la revocación del rol |
| `PASSWORD_DEMASIADO_CORTA` y las cuatro hermanas | `POLITICA_INCUMPLIDA` + `errores[]` | Ver el recuadro de SPEC-03 |
| `PASSWORD_YA_UTILIZADA` | `POLITICA_INCUMPLIDA` con la regla `YA_UTILIZADA` | El historial es una regla más de la política: un código aparte obligaba al cliente a tratar dos errores para lo mismo |
| `TOKEN_RECUPERACION_USADO` | `TOKEN_RECUPERACION_INVALIDO` | Distinguir «usado» de «inexistente» dice si alguien pidió recuperación para ese correo |
| Cualquier código que empiece por `AUTH_` | El equivalente de este catálogo | El prefijo no aporta: toda la API es de autenticación |

> **Decisión del 17 de septiembre.** El login con una cuenta bloqueada, inactiva
> o sin verificar responde `401 CREDENCIALES_INVALIDAS`, igual que una
> contraseña incorrecta. Sustituye tanto al `423 Locked` de los borradores en PDF
> como al `403 CUENTA_NO_DISPONIBLE` que respondía el contrato hasta esa fecha.

---

## Cómo añadir un código

1. Compruébalo contra este catálogo: puede que ya exista uno que sirva.
2. Pregúntate si distingue dos situaciones que un atacante no debería poder
   distinguir. Si es así, no lo añadas.
3. Acuérdalo con el PO y añádelo aquí **y** en `specs/openapi.yaml`, en la misma
   sesión.
4. Anótalo en la sección *Impacto en el contrato* de tu spec.
