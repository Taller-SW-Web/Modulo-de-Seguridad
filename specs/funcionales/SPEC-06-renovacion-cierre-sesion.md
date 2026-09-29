# SPEC-06 — Renovación y cierre de sesión

| Campo | Valor |
|---|---|
| **Responsable** | Jose Luis Limachi Sarmiento |
| **Hito objetivo** | Hito 3 (Sem. 8) |
| **Estado** | Borrador |
| **Aprobada por** | Product Owner — pendiente de aprobación |

> **Origen.** Sale de dividir la antigua SPEC-02 «Autenticación usuario/contraseña» el 20 de septiembre, por indicación del profesor: una spec por función. La equivalencia de números está en [`trazabilidad.md`](trazabilidad.md) §1.

---

## Contexto — ¿por qué?

El `accessToken` dura 15 minutos a propósito: es lo que tarda un cambio de estado en llegar a los módulos que validan en local (SPEC-17). Para no pedir la contraseña cada 15 minutos existe el `refreshToken`, que dura 7 días y por eso es el blanco más valioso para un atacante.

Si un `refreshToken` robado pudiera usarse indefinidamente, el ladrón tendría una sesión de siete días renovable. La rotación en cada uso y la detección de reutilización convierten ese robo en algo que se descubre la primera vez que el titular legítimo renueva.

---

## Propósito — ¿para qué?

Permitir que una sesión se mantenga viva renovando el par de tokens, que se cierre a petición del usuario, y que un `refreshToken` robado se detecte y se neutralice.

El resultado observable es un par de tokens nuevo en cada renovación, un `401` ante cualquier token usado, vencido o revocado, y la revocación de la familia entera de la sesión cuando se detecta reutilización.

---

## Alcance — ¿hasta dónde?

- Renovación con `POST /api/v1/auth/refresh` y rotación del `refreshToken` en cada uso.
- Detección de reutilización y revocación de la familia de tokens de esa sesión.
- Cierre de sesión con `POST /api/v1/auth/logout`.
- Rechazo de tokens de refresco vencidos o revocados.
- El mecanismo de revocación que usan otras specs: cambio de rol (SPEC-11), baja (SPEC-04), bloqueo manual (SPEC-15) y restablecimiento de contraseña (SPEC-08).

---

## Requisitos — ¿qué debe hacer?

| Código | Requisito |
|---|---|
| RF-06.1 | El sistema debe rotar el refreshToken en cada uso válido, entregando un nuevo par de tokens e invalidando el refreshToken utilizado. |
| RF-06.2 | El sistema debe revocar el refreshToken correspondiente a la sesión cuando el usuario realiza logout. |
| RF-06.3 | Si el sistema detecta que un refreshToken previamente utilizado vuelve a ser presentado, debe revocar todos los refreshToken pertenecientes a la familia de esa sesión y rechazar la solicitud. |
| RF-06.4 | Un `refreshToken` vencido o revocado debe responder `401` sin emitir tokens nuevos. |
| RF-06.5 | El cierre de sesión y la detección de reutilización deben registrarse mediante SPEC-12 (`SESION_CERRADA`, `REFRESCO_REUTILIZADO`). |

---

## Escenarios — ¿cómo verificamos?

Formato Dado / Cuando / Entonces. Los marcados como **caso borde** cubren
condiciones límite, de error o de seguridad.

### ESC-06.1 Rotación del token de refresco

Dado un refreshToken válido y vigente.

Cuando el usuario realiza una solicitud POST /auth/refresh.

Entonces el sistema debe:

Generar un nuevo accessToken.
Generar un nuevo refreshToken.
Revocar el refreshToken utilizado.

El token anterior no debe poder utilizarse nuevamente.

### ESC-06.2 Reutilización de un token de refresco

Dado un refreshToken que ya fue utilizado y posteriormente revocado.

Cuando dicho token vuelve a utilizarse en POST /auth/refresh.

Entonces el sistema debe:

Detectar la reutilización.
Revocar toda la familia de tokens asociada a la sesión.
Responder con código HTTP 401.

### ESC-06.3 Cierre de sesión

Dado un usuario con una sesión activa.

Cuando realiza una solicitud POST /auth/logout enviando su refreshToken.

Entonces el sistema debe revocar el refreshToken correspondiente.

La operación debe finalizar con código HTTP 204.

### ESC-06.4 Token de refresco expirado

Dado un refreshToken cuya fecha de expiración ya fue superada.

Cuando el usuario intenta realizar una solicitud POST /auth/refresh.

Entonces el sistema debe responder con código HTTP 401.

La sesión debe considerarse expirada y no deben generarse nuevos tokens.

---

## Requisitos no funcionales — ¿con qué condiciones?

- La renovación debe responder en menos de 300 ms en el percentil 95 con 50 usuarios concurrentes. *(valor propuesto al dividir la spec; lo confirma su responsable antes de aprobarla)*
- Dos renovaciones simultáneas con el mismo `refreshToken` no pueden emitir dos pares válidos: la rotación debe ser atómica.
- Los tokens de refresco no deben aparecer completos en logs ni en auditoría; se referencian por su `jti`.
- Revocar la familia de una sesión no debe cerrar las demás sesiones del mismo usuario.

---

## Fuera de alcance — ¿qué NO hará?

- El inicio de sesión, que es de SPEC-05.
- Decidir **cuándo** se revocan todas las sesiones de un usuario: lo deciden SPEC-04, SPEC-08, SPEC-11 y SPEC-15; esta spec solo aporta el mecanismo.
- Renovación automática del `refreshToken` sin intervención del cliente.

---

## Impacto en el contrato

| Endpoint / evento | Añade, modifica o elimina | Acordado con el PO |
|---|---|---|
| `POST /api/v1/auth/refresh` | Añade | ⬜ |
| `POST /api/v1/auth/logout` | Añade | ⬜ |

---

## Lista de completitud

La spec se cierra cuando:

- [ ] Todos los requisitos están implementados
- [ ] Cada requisito clave tiene al menos dos escenarios automatizados, uno de ellos caso borde
- [ ] Los no funcionales están verificados, o la desviación está documentada y aprobada
- [ ] Los endpoints figuran en la documentación OpenAPI publicada (`specs/openapi.yaml`)
- [ ] Nada de lo declarado fuera de alcance se construyó de forma encubierta
- [ ] El código está en `main` con revisión aprobada y desplegado en nube
