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
| Cuenta bloqueada | `403 CUENTA_NO_DISPONIBLE` |
| Cuenta inactiva | `403 CUENTA_NO_DISPONIBLE` — idéntico |
| Cuenta sin verificar | `403 CUENTA_NO_DISPONIBLE` — idéntico |
| Recuperación con correo existente | `202` sin cuerpo |
| Recuperación con correo inexistente | `202` sin cuerpo — idéntico, y con la misma latencia |

---

## Códigos ya publicados en el contrato

No se renombran: hay seis equipos que van a ramificar por ellos.

| Código | HTTP | Spec | Cuándo se devuelve |
|---|---|---|---|
| `VALIDACION` ✅ | 400 | Todas | El cuerpo de la petición no cumple el esquema: falta un campo, el tipo no corresponde, el formato del correo es inválido |
| `LOTE_DEMASIADO_GRANDE` ✅ | 400 | 09 | La consulta por lote trae más de 100 identificadores |
| `TOKEN_INVALIDO` ✅ | 401 | Todas | Falta el token, la firma no valida o ya venció |
| `CREDENCIALES_INVALIDAS` ✅ | 401 | 02 | Correo o contraseña incorrectos, **o el correo no existe** |
| `REFRESCO_INVALIDO` ✅ | 401 | 02 | El token de refresco no existe, ya se usó o fue revocado |
| `CODIGO_INVALIDO` ✅ | 401 | 04 | El código OTP no coincide con el desafío |
| `CLIENTE_INVALIDO` ✅ | 401 | 09 | El `client_id` o el `client_secret` del módulo consumidor no son válidos |
| `CUENTA_NO_DISPONIBLE` ✅ | 403 | 02, 07 | La cuenta está bloqueada, inactiva o pendiente de verificación. **No se revela cuál** |
| `TOKEN_NO_APLICABLE` ✅ | 403 | 09 | Un token de servicio intenta una operación que actúa en nombre de una persona |
| `SCOPE_INSUFICIENTE` ✅ | 403 | 05, 06, 09 | El token es válido pero no tiene el permiso necesario. **No se revela cuál haría falta** |
| `NO_ENCONTRADO` ✅ | 404 | 01, 08, 09 | No existe el recurso. Solo se llega aquí con token y permiso válidos |
| `CORREO_NO_DISPONIBLE` ✅ | 409 | 01 | El correo ya está registrado. El texto no confirma ni niega la existencia de la cuenta |
| `DEMASIADAS_SOLICITUDES` ✅ | 429 | 03, 04 | Se superó el límite de solicitudes: más de 3 OTP en 15 min, o recuperaciones repetidas |
| `NO_DISPONIBLE` ✅ | 503 | Todas | Dependencia caída: base de datos, cola de correo o auditoría crítica |

---

## Códigos que faltan y hay que añadir al contrato

Los piden las specs pero todavía no están en `openapi.yaml`. Se añaden antes del
congelamiento del jueves.

### SPEC-01 — Registro

| Código | HTTP | Cuándo |
|---|---|---|
| `ENLACE_EXPIRADO` | 410 | El enlace de verificación de correo tiene más de 24 h |
| `ENLACE_YA_USADO` | 410 | El enlace de verificación ya se consumió |

### SPEC-03 — Credenciales y contraseñas

| Código | HTTP | Cuándo |
|---|---|---|
| `POLITICA_INCUMPLIDA` | 422 | La contraseña no cumple la política. **Un solo código para las cinco reglas**; cuál falló va en el array `errores` |
| `PASSWORD_YA_UTILIZADA` | 422 | Está entre las últimas 5 del historial |
| `PASSWORD_CADUCADA` | 403 | Un administrador con contraseña de más de 90 días inicia sesión: debe cambiarla antes de continuar |
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
| `MFA_OBLIGATORIO` | 422 | Un `ADMIN_SISTEMA` intenta desactivar su segundo factor |

### SPEC-06 — Auditoría

| Código | HTTP | Cuándo |
|---|---|---|
| `EXPORTACION_DEMASIADO_GRANDE` | 413 | El filtro abarca más de 100 000 registros |

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
| `423 Locked` con el motivo del bloqueo | `403 CUENTA_NO_DISPONIBLE` | Un `423` confirma que la cuenta existe **y** que está bloqueada. Con eso se enumera y además se sabe a quién se ha conseguido bloquear |
| `AUTH_EMAIL_YA_REGISTRADO` | `CORREO_NO_DISPONIBLE` | Mismo significado, nombre que afirma de más |
| `PASSWORD_DEMASIADO_CORTA` y las cuatro hermanas | `POLITICA_INCUMPLIDA` + `errores[]` | Ver el recuadro de SPEC-03 |
| `TOKEN_RECUPERACION_USADO` | `TOKEN_RECUPERACION_INVALIDO` | Distinguir «usado» de «inexistente» dice si alguien pidió recuperación para ese correo |
| Cualquier código que empiece por `AUTH_` | El equivalente de este catálogo | El prefijo no aporta: toda la API es de autenticación |

> **Nota para SPEC-02 y SPEC-07.** Los dos borradores en PDF especifican
> `423 Locked` en sus escenarios de cuenta bloqueada. Al redactarlas hay que
> cambiarlo por `403 CUENTA_NO_DISPONIBLE`, que es lo que ya responde el
> contrato publicado. Si alguien cree que el `423` es preferible, es una
> discusión legítima, pero hay que tenerla **antes** del jueves y cambiar el
> contrato, no después y cambiar el código.

---

## Cómo añadir un código

1. Compruébalo contra este catálogo: puede que ya exista uno que sirva.
2. Pregúntate si distingue dos situaciones que un atacante no debería poder
   distinguir. Si es así, no lo añadas.
3. Acuérdalo con el PO y añádelo aquí **y** en `specs/openapi.yaml`, en la misma
   sesión.
4. Anótalo en la sección *Impacto en el contrato* de tu spec.
