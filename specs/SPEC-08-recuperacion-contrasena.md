# SPEC-08 — Recuperación de contraseña

| Campo | Valor |
|---|---|
| **Responsable** | Juan José Cano Vasquez |
| **Hito objetivo** | Hito 4 (Sem. 11) |
| **Estado** | Borrador |
| **Aprobada por** | Product Owner — pendiente de aprobación |

> **Origen.** Sale de dividir la antigua SPEC-03 «Gestión de credenciales y contraseñas» el 20 de septiembre, por indicación del profesor: una spec por función. La equivalencia de números está en [`trazabilidad.md`](trazabilidad.md) §1.

---

## Contexto — ¿por qué?

Un usuario que olvidó su contraseña necesita volver a entrar sin ayuda de nadie; un proceso de recuperación inseguro, en cambio, es la puerta trasera más usada para tomar cuentas ajenas.

Por eso la recuperación no puede revelar si un correo está registrado, y el enlace que envía debe ser corto, de un solo uso y sustituible. Además, es la salida para dos situaciones de otras specs: la contraseña caducada del personal de gestión (SPEC-07) y la cuenta con bloqueo automático (SPEC-14).

---

## Propósito — ¿para qué?

Permitir que un usuario restablezca su contraseña mediante un enlace enviado a su correo.

El resultado observable es una contraseña nueva que cumple la política de SPEC-07, las sesiones anteriores cerradas, un bloqueo automático levantado si lo había, y un correo que avisa del cambio.

---

## Alcance — ¿hasta dónde?

- Solicitud de recuperación con `POST /api/v1/password/recuperar`, sin revelar si el correo existe.
- Token de recuperación de un solo uso con vigencia de 30 minutos.
- Invalidación de los tokens anteriores con cada solicitud nueva.
- Restablecimiento con `POST /api/v1/password/restablecer`, aplicando la política de SPEC-07.
- Cierre de las sesiones anteriores y levantamiento del bloqueo automático (SPEC-14).
- Notificación por correo y auditoría.

---

## Requisitos — ¿qué debe hacer?

| Código | Requisito |
|---|---|
| RF-08.1 | El sistema debe permitir solicitar la recuperación mediante el correo asociado a la cuenta sin revelar si dicho correo está registrado. |
| RF-08.2 | El sistema debe generar un **token de recuperación de un solo uso** con una vigencia máxima de **30 minutos**. |
| RF-08.3 | Una nueva solicitud de recuperación debe invalidar los tokens anteriores asociados a la cuenta. |
| RF-08.4 | El sistema debe rechazar tokens de recuperación expirados o ya utilizados. |
| RF-08.5 | Después de un restablecimiento exitoso, el sistema debe validar la nueva contraseña e invalidar los tokens o sesiones anteriores asociados a la cuenta. Si la cuenta tiene un bloqueo automático, el restablecimiento lo levanta (ver SPEC-14). La recuperación funciona aunque la cuenta esté bloqueada. |
| RF-08.6 | El sistema debe notificar al usuario por correo después de un restablecimiento exitoso. |
| RF-08.7 | La solicitud y el restablecimiento deben registrarse mediante SPEC-12 (`RECUPERACION_SOLICITADA`, `CONTRASENA_RESTABLECIDA`). |

---

## Escenarios — ¿cómo verificamos?

Formato Dado / Cuando / Entonces. Los marcados como **caso borde** cubren
condiciones límite, de error o de seguridad.

### ESC-08.1 Recuperación con correo registrado

**Dado** que existe una cuenta asociada al correo proporcionado,
**Cuando** el usuario solicita recuperar su contraseña,
**Entonces** el sistema procesa la solicitud y genera un mecanismo de recuperación.

### ESC-08.2 Recuperación con correo no registrado

**Dado** que no existe una cuenta asociada al correo proporcionado,
**Cuando** se solicita la recuperación,
**Entonces** el sistema responde de forma equivalente sin revelar que la cuenta no existe.

### ESC-08.3 Token válido

**Dado** que el usuario posee un token válido y no utilizado,
**Cuando** lo utiliza dentro de los 30 minutos y proporciona una contraseña válida,
**Entonces** el sistema permite restablecer la contraseña.

### ESC-08.4 Token expirado o utilizado

**Dado** que el token ha expirado o ya fue utilizado,
**Cuando** el usuario intenta utilizarlo,
**Entonces** el sistema rechaza la operación.

### ESC-08.5 Nueva solicitud de recuperación

**Dado** que el usuario solicita la recuperación dos veces,
**Cuando** se genera el segundo token,
**Entonces** el token anterior queda invalidado.

### ESC-08.6 Restablecimiento e invalidación

**Dado** que el usuario completa correctamente una recuperación,
**Cuando** se establece la nueva contraseña,
**Entonces** el sistema invalida las sesiones o tokens anteriores y registra `CONTRASENA_RESTABLECIDA`.

---

## Requisitos no funcionales — ¿con qué condiciones?

- Los tokens de recuperación deben almacenarse de forma protegida y no en texto claro.
- La respuesta de la solicitud debe ser uniforme en cuerpo y latencia para cuentas existentes y no existentes, evitando la enumeración.
- El envío de correos debe ser **asíncrono**.
- El tratamiento de datos personales debe alinearse con la Ley N.º 29733 de Protección de Datos Personales.

---

## Fuera de alcance — ¿qué NO hará?

- Las reglas de la política de contraseñas, que son de SPEC-07: aquí se invocan.
- El cambio de contraseña con sesión iniciada, que es de SPEC-07.
- Recuperación mediante SMS o preguntas secretas.
- Levantar un bloqueo **manual**: solo lo hace un administrador (SPEC-15).

---

## Impacto en el contrato

| Endpoint / evento | Añade, modifica o elimina | Acordado con el PO |
|---|---|---|
| `POST /api/v1/password/recuperar` | Utiliza contrato existente | ⬜ |
| `POST /api/v1/password/restablecer` | Utiliza contrato existente | ⬜ |
| `RECUPERACION_SOLICITADA` · `CONTRASENA_RESTABLECIDA` | Acciones del catálogo de auditoría de SPEC-12 | ⬜ |

---

## Lista de completitud

La spec se cierra cuando:

- [ ] Todos los requisitos están implementados
- [ ] Cada requisito clave tiene al menos dos escenarios automatizados, uno de ellos caso borde
- [ ] Los no funcionales están verificados, o la desviación está documentada y aprobada
- [ ] Los endpoints figuran en la documentación OpenAPI publicada (`specs/openapi.yaml`)
- [ ] Nada de lo declarado fuera de alcance se construyó de forma encubierta
- [ ] El código está en `main` con revisión aprobada y desplegado en nube
