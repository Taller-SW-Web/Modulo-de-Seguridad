# SPEC-05 — Inicio de sesión con correo y contraseña

| Campo | Valor |
|---|---|
| **Responsable** | Jose Luis Limachi Sarmiento |
| **Hito objetivo** | Hito 3 (Sem. 8) |
| **Estado** | Borrador |
| **Aprobada por** | Product Owner — pendiente de aprobación |

> **Origen.** Sale de dividir la antigua SPEC-02 «Autenticación usuario/contraseña» el 20 de septiembre, por indicación del profesor: una spec por función. Conserva el inicio de sesión; la renovación y el cierre de sesión pasaron a SPEC-06. La equivalencia de números está en [`trazabilidad.md`](trazabilidad.md) §1.

---

## Contexto — ¿por qué?

El Marketplace Multicanal de Productos Deportivos cuenta con diferentes canales de acceso, como Marketplace, Retail y Chatbot, además de módulos internos como Ventas, Despacho y Productos. Todos necesitan identificar al usuario que realiza cada acción.

La autenticación con correo y contraseña es el mecanismo principal de acceso, y este módulo es el único que valida las credenciales. Un error aquí —un mensaje que delate que una cuenta existe, un token emitido a una cuenta bloqueada— afecta a los siete módulos a la vez.

---

## Propósito — ¿para qué?

Permitir que clientes, vendedores y administradores se identifiquen con su correo y su contraseña.

El resultado observable es un par de tokens —`accessToken` de 15 minutos y `refreshToken` de 7 días— cuando la autenticación es correcta, o un `challengeToken` cuando la cuenta tiene segundo factor (SPEC-09).

---

## Alcance — ¿hasta dónde?

- Inicio de sesión con `POST /api/v1/auth/login`.
- Validación de las credenciales y del estado de la cuenta antes de emitir tokens.
- Emisión del `accessToken` (15 minutos) y del `refreshToken` (7 días).
- Respuesta genérica ante cualquier fallo, sin revelar si la cuenta existe o está bloqueada.
- Aviso de cada intento fallido a SPEC-14, que lleva el contador y decide el bloqueo.
- Derivación al segundo factor (SPEC-09) cuando la cuenta lo tiene habilitado.
- Comprobación de caducidad de contraseña (SPEC-07).

---

## Requisitos — ¿qué debe hacer?

| Código | Requisito |
|---|---|
| RF-05.1 | El sistema debe permitir autenticar a un usuario utilizando su correo electrónico y contraseña. |
| RF-05.2 | Si el usuario tiene MFA habilitado y las credenciales proporcionadas son correctas, el sistema no debe entregar directamente los tokens de acceso. |
| RF-05.3 | Cuando se produzca un intento de inicio de sesión con credenciales incorrectas, el sistema debe registrarlo como intento fallido según SPEC-14, que es la dueña del contador y del bloqueo. |
| RF-05.4 | El sistema debe rechazar el inicio de sesión cuando la cuenta se encuentre bloqueada, inactiva o pendiente de verificación, no debe emitir tokens y debe responder `401 CREDENCIALES_INVALIDAS`, igual que ante una contraseña incorrecta. |
| RF-05.5 | Al completar la autenticación, el sistema debe aplicar la comprobación de caducidad de contraseña de SPEC-07: si caducó, responde `403 PASSWORD_CADUCADA` en vez de emitir tokens. SPEC-05 no reimplementa la regla: la invoca. |
| RF-05.6 | Un inicio de sesión correcto sin segundo factor debe emitir un `accessToken` de 15 minutos, firmado con RS256 y con los claims `roles` y `permisos` que calcula SPEC-11, y un `refreshToken` de 7 días que abre una familia de sesión nueva (SPEC-06). |
| RF-05.7 | Cada inicio de sesión, correcto o fallido, debe registrarse mediante SPEC-12 (`SESION_INICIADA`, `SESION_FALLIDA`). |

---

## Escenarios — ¿cómo verificamos?

Formato Dado / Cuando / Entonces. Los marcados como **caso borde** cubren
condiciones límite, de error o de seguridad.

### ESC-05.1 Inicio de sesión exitoso sin MFA

**Dado** un usuario activo con correo `juan@correo.com`, contraseña correcta y MFA deshabilitado.

**Cuando** realiza una solicitud `POST /auth/login`.

**Entonces** el sistema debe responder con código HTTP `200` y retornar:

- `accessToken`
- `refreshToken`
- Datos básicos del usuario.

### ESC-05.2 Contraseña incorrecta

**Dado** un usuario activo registrado en el sistema.

**Cuando** proporciona una contraseña incorrecta.

**Entonces** el sistema debe responder con código HTTP `401`.

Además:

- Debe mostrar un mensaje genérico.
- Debe registrar el intento fallido, que SPEC-14 cuenta según sus reglas.
- No debe entregar tokens.

### ESC-05.3 Correo electrónico inexistente

**Dado** que el correo electrónico proporcionado no pertenece a ningún usuario registrado.

**Cuando** intenta iniciar sesión.

**Entonces** el sistema debe responder con código HTTP `401`.

El mensaje debe ser equivalente al utilizado para una contraseña incorrecta y no debe revelar que la cuenta no existe.

### ESC-05.4 Cuenta bloqueada

**Dado** un usuario cuya cuenta tiene estado `BLOQUEADO`.

**Cuando** proporciona credenciales correctas.

**Entonces** el sistema debe responder `401 CREDENCIALES_INVALIDAS`, exactamente igual que ante una contraseña incorrecta.

No se deben generar ni entregar `accessToken` ni `refreshToken`.

No se debe revelar que la cuenta está bloqueada ni el motivo: el titular se entera por el correo de aviso (SPEC-14 y SPEC-15).

### ESC-05.5 Usuario con MFA habilitado

**Dado** un usuario activo con `mfa_habilitado = true`.

**Cuando** proporciona correctamente su correo electrónico y contraseña.

**Entonces** el sistema debe responder con código HTTP `200` y retornar:

{
  "mfa_requerido": true,
  "challengeToken": "..."
}

---

## Requisitos no funcionales — ¿con qué condiciones?

- El inicio de sesión debe responder en menos de 800 ms en el percentil 95 con 50 usuarios concurrentes.
- Las respuestas ante contraseña incorrecta, correo inexistente y cuenta bloqueada, inactiva o pendiente deben ser idénticas en cuerpo y en latencia.
- Los logs no deben contener contraseñas, tokens, códigos OTP ni otra información sensible de autenticación.
- Los tokens deben poder ser verificados por terceros mediante el JWKS público (SPEC-17).

---

## Fuera de alcance — ¿qué NO hará?

- La renovación y el cierre de sesión, que son de SPEC-06.
- El desafío del segundo factor, que es de SPEC-09: aquí solo se decide que hace falta.
- El contador de fallos y el bloqueo, que son de SPEC-14.
- Autenticación mediante proveedores externos como Google o Facebook.
- Autorización de las operaciones de negocio según el rol del usuario dentro de cada módulo.

---

## Impacto en el contrato

| Endpoint / evento | Añade, modifica o elimina | Acordado con el PO |
|---|---|---|
| `POST /api/v1/auth/login` | Añade | ⬜ |

---

## Lista de completitud

La spec se cierra cuando:

- [ ] Todos los requisitos están implementados
- [ ] Cada requisito clave tiene al menos dos escenarios automatizados, uno de ellos caso borde
- [ ] Los no funcionales están verificados, o la desviación está documentada y aprobada
- [ ] Los endpoints figuran en la documentación OpenAPI publicada (`specs/openapi.yaml`)
- [ ] Nada de lo declarado fuera de alcance se construyó de forma encubierta
- [ ] El código está en `main` con revisión aprobada y desplegado en nube
