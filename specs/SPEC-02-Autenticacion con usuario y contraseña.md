# SPEC-02 — Autenticación mediante usuario/contraseña

| Campo | Valor |
|---|---|
| **Responsable** | Jose Luis Limachi Sarmiento |
| **Hito objetivo** | Hito 3 (Sem. 8) |
| **Estado** | Borrador |
| **Aprobada por** | Product Owner — pendiente de aprobación |

---

## Contexto — ¿por qué?

El Marketplace Multicanal de Productos Deportivos cuenta con diferentes canales de acceso, como Marketplace, Retail y Chatbot, además de módulos internos como Ventas, Despacho y Productos.

Todos estos componentes necesitan identificar al usuario que realiza una determinada acción. Por ello, el módulo de seguridad y autenticación centraliza la gestión de credenciales.

La autenticación mediante correo electrónico y contraseña constituye el mecanismo principal de acceso al sistema. Este módulo es el responsable de validar las credenciales de los usuarios.

---

## Propósito — ¿para qué?

Permitir que los usuarios del sistema, incluyendo clientes, vendedores y administradores, puedan identificarse mediante su correo electrónico y contraseña.

Cuando la autenticación sea exitosa, el sistema deberá proporcionar un par de tokens compuesto por:

- `accessToken`
- `refreshToken`

Estos tokens permitirán mantener la sesión del usuario y acceder posteriormente a los recursos correspondientes.

---

## Alcance — ¿hasta dónde?

Esta especificación comprende:

- Inicio de sesión mediante correo electrónico y contraseña.
- Validación de las credenciales del usuario.
- Generación de un `accessToken` con duración de 15 minutos.
- Generación de un `refreshToken` con duración de 7 días.
- Rotación de `refreshToken`.
- Revocación del `refreshToken` al cerrar sesión.
- Detección del reutilizamiento de un `refreshToken`.
- Revocación de la familia completa de tokens cuando se detecte reutilización.
- Validación del estado de la cuenta antes de emitir tokens.
- Aviso de cada intento fallido a SPEC-07, que lleva el contador y decide el bloqueo.
- Integración con MFA cuando la cuenta tenga MFA habilitado.

---

## Requisitos — ¿qué debe hacer?
| Código | Requisito |
|---|---|
| RF-02.1 | El sistema debe permitir autenticar a un usuario utilizando su correo electrónico y contraseña. |
| RF-02.2 | Si el usuario tiene MFA habilitado y las credenciales proporcionadas son correctas, el sistema no debe entregar directamente los tokens de acceso. |
| RF-02.3 | El sistema debe rotar el refreshToken en cada uso válido, entregando un nuevo par de tokens e invalidando el refreshToken utilizado. |
| RF-02.4 | El sistema debe revocar el refreshToken correspondiente a la sesión cuando el usuario realiza logout. |
| RF-02.5 | Si el sistema detecta que un refreshToken previamente utilizado vuelve a ser presentado, debe revocar todos los refreshToken pertenecientes a la familia de esa sesión y rechazar la solicitud. |
| RF-02.6 | Cuando se produzca un intento de inicio de sesión con credenciales incorrectas, el sistema debe registrarlo como intento fallido según SPEC-07, que es la dueña del contador y del bloqueo. |
| RF-02.7 | El sistema debe rechazar el inicio de sesión cuando la cuenta se encuentre bloqueada, inactiva o pendiente de verificación, no debe emitir tokens y debe responder `401 CREDENCIALES_INVALIDAS`, igual que ante una contraseña incorrecta. |

---

## Escenarios — ¿cómo verificamos?

### ESC-02.1 Inicio de sesión exitoso sin MFA

**Dado** un usuario activo con correo `juan@correo.com`, contraseña correcta y MFA deshabilitado.

**Cuando** realiza una solicitud `POST /auth/login`.

**Entonces** el sistema debe responder con código HTTP `200` y retornar:

- `accessToken`
- `refreshToken`
- Datos básicos del usuario.

---

### ESC02.2 Contraseña incorrecta

**Dado** un usuario activo registrado en el sistema.

**Cuando** proporciona una contraseña incorrecta.

**Entonces** el sistema debe responder con código HTTP `401`.

Además:

- Debe mostrar un mensaje genérico.
- Debe registrar el intento fallido, que SPEC-07 cuenta según sus reglas.
- No debe entregar tokens.

---

### ESC02.3 Correo electrónico inexistente

**Dado** que el correo electrónico proporcionado no pertenece a ningún usuario registrado.

**Cuando** intenta iniciar sesión.

**Entonces** el sistema debe responder con código HTTP `401`.

El mensaje debe ser equivalente al utilizado para una contraseña incorrecta y no debe revelar que la cuenta no existe.

---

### ESC02.4 Cuenta bloqueada

**Dado** un usuario cuya cuenta tiene estado `BLOQUEADO`.

**Cuando** proporciona credenciales correctas.

**Entonces** el sistema debe responder `401 CREDENCIALES_INVALIDAS`, exactamente igual que ante una contraseña incorrecta.

No se deben generar ni entregar `accessToken` ni `refreshToken`.

No se debe revelar que la cuenta está bloqueada ni el motivo: el titular se entera por el correo de aviso (SPEC-07).

---

### ESC02.5 Usuario con MFA habilitado

**Dado** un usuario activo con `mfa_habilitado = true`.

**Cuando** proporciona correctamente su correo electrónico y contraseña.

**Entonces** el sistema debe responder con código HTTP `200` y retornar:

{
  "mfa_requerido": true,
  "challengeToken": "..."
}

---

### ESC02.6 Rotación del refresh token

Dado un refreshToken válido y vigente.

Cuando el usuario realiza una solicitud POST /auth/refresh.

Entonces el sistema debe:

Generar un nuevo accessToken.
Generar un nuevo refreshToken.
Revocar el refreshToken utilizado.

El token anterior no debe poder utilizarse nuevamente.

---

### ESC02.7 Reutilización de un refresh token

Dado un refreshToken que ya fue utilizado y posteriormente revocado.

Cuando dicho token vuelve a utilizarse en POST /auth/refresh.

Entonces el sistema debe:

Detectar la reutilización.
Revocar toda la familia de tokens asociada a la sesión.
Responder con código HTTP 401.

---

### ESC02.8 Cierre de sesión

Dado un usuario con una sesión activa.

Cuando realiza una solicitud POST /auth/logout enviando su refreshToken.

Entonces el sistema debe revocar el refreshToken correspondiente.

La operación debe finalizar con código HTTP 204.

---

### ESC02.9 Refresh token expirado

Dado un refreshToken cuya fecha de expiración ya fue superada.

Cuando el usuario intenta realizar una solicitud POST /auth/refresh.

Entonces el sistema debe responder con código HTTP 401.

La sesión debe considerarse expirada y no deben generarse nuevos tokens.

---

## Requisitos no funcionales — ¿con qué condiciones?

- La operación de inicio de sesión debe responder en menos de:

800 ms en P95 con 50 usuarios concurrentes
- Los logs no deben contener información sensible como:

Contraseñas.
Tokens.
Códigos OTP.
Información sensible de autenticación.

- No debe registrar contraseñas en los logs.
- Debe utilizar mensajes de error genéricos para credenciales inválidas.
- Los tokens deben poder ser verificados por terceros mediante el JWKS público.

---

## Fuera de alcance — ¿qué NO hará?


- Autenticación mediante proveedores externos como Google o Facebook.
- Autorización de las operaciones de negocio según el rol del usuario dentro de cada módulo.
- Renovación automática del refreshToken sin intervención del cliente.

---

## Impacto en el contrato

La implementación de esta especificación requiere considerar los endpoints relacionados con la autenticación:

| Endpoint / evento | Añade, modifica o elimina | Acordado con el PO |
|---|---|---|
| `POST /api/v1/auth/login` | Añade | ⬜ |
| `POST /api/v1/auth/refresh` | Añade | ⬜ |
| `POST /api/v1/auth/logout` | Añade | ⬜ |

---

## Lista de completitud

La spec se cierra cuando:

- [ ] Todos los requisitos están implementados
- [ ] Cada requisito clave tiene al menos dos escenarios automatizados, uno de ellos caso borde
- [ ] Los no funcionales están verificados, o la desviación está documentada y aprobada
- [ ] Los endpoints nuevos figuran en la documentación OpenAPI publicada
- [ ] Nada de lo declarado fuera de alcance se construyó de forma encubierta
- [ ] El código está en `main` con revisión aprobada y desplegado en nube
