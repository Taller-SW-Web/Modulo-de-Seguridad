# SPEC-02 — Verificación de correo

| Campo | Valor |
|---|---|
| **Responsable** | Juan José Cano Vasquez |
| **Hito objetivo** | Hito 3 (Sem. 8) |
| **Estado** | Borrador |
| **Aprobada por** | Product Owner — pendiente de aprobación |

> **Origen.** Sale de dividir la antigua SPEC-01 «Registro y gestión de usuarios» el 20 de septiembre, por indicación del profesor: una spec por función. Pasa a Juan José porque depende del adaptador de correo (outbox) que él ya construye para SPEC-07, SPEC-08 y SPEC-09. La equivalencia de números está en [`trazabilidad.md`](trazabilidad.md) §1.

---

## Contexto — ¿por qué?

El correo es a la vez el identificador con el que se inicia sesión y el canal por el que se recupera la contraseña. Si una cuenta pudiera operar con un correo que no controla su titular, cualquiera podría registrarse con la dirección de otra persona y recibir después sus enlaces de recuperación.

Por eso ninguna cuenta creada por registro público puede iniciar sesión hasta demostrar que controla su correo, y los demás módulos no se enteran de que existe un cliente nuevo hasta ese momento.

---

## Propósito — ¿para qué?

Confirmar que quien se registró controla la dirección de correo declarada, mediante un enlace de un solo uso.

El resultado observable es la transición de la cuenta de `PENDIENTE_VERIFICACION` a `ACTIVO` y la publicación de `usuario.creado` para el resto del marketplace. El mismo mecanismo confirma también un cambio de correo (SPEC-16).

---

## Alcance — ¿hasta dónde?

- Generación del token de verificación, de un solo uso y con vigencia de 24 horas.
- Envío asíncrono del enlace por SMTP.
- Confirmación con `POST /api/v1/auth/verificar-correo` y paso a `ACTIVO`.
- Rechazo del inicio de sesión mientras la cuenta esté `PENDIENTE_VERIFICACION`.
- Reenvío del enlace con límite de tasa, sin revelar qué cuentas existen.
- Publicación de `usuario.creado`.
- Reutilización del mecanismo para confirmar un cambio de correo (SPEC-16).

---

## Requisitos — ¿qué debe hacer?

| Código | Requisito |
|---|---|
| RF-02.1 | Tras un registro exitoso, el sistema debe generar un token de verificación de correo de un solo uso con vigencia de 24 horas y enviar un mensaje asíncrono vía SMTP. |
| RF-02.2 | El sistema debe permitir confirmar la cuenta mediante la verificación del token enviado por correo, cambiando el estado del usuario de `PENDIENTE_VERIFICACION` a `ACTIVO`. |
| RF-02.3 | El sistema debe rechazar la autenticación y emisión de tokens a cualquier usuario que se encuentre en estado `PENDIENTE_VERIFICACION`. |
| RF-02.4 | El sistema debe permitir solicitar el reenvío del correo de verificación (`POST /api/v1/auth/verificar-correo/reenviar`), invalidando el enlace anterior. Responde `202` exista o no la cuenta, y admite 3 solicitudes por hora **por dirección de correo**, para que ni la respuesta ni el límite revelen qué cuentas existen. |
| RF-02.5 | El sistema debe publicar el evento `usuario.creado` en RabbitMQ inmediatamente después de que la cuenta pase al estado `ACTIVO`. |
| RF-02.6 | El token de verificación de correo es el **único** mecanismo de confirmación de correo del módulo. SPEC-16 lo reutiliza para confirmar un cambio de correo, con otro propósito, a través del mismo `POST /api/v1/auth/verificar-correo`. |
| RF-02.7 | La verificación correcta y los intentos con enlaces vencidos o usados deben registrarse mediante SPEC-12 (`USUARIO_VERIFICADO`). |

---

## Escenarios — ¿cómo verificamos?

Formato Dado / Cuando / Entonces. Los marcados como **caso borde** cubren
condiciones límite, de error o de seguridad.

### ESC-02.1 Verificación exitosa de correo electrónico

**Dado** un usuario registrado con estado `PENDIENTE_VERIFICACION` y un token de verificación válido no expirado.

**Cuando** envía una solicitud `POST /api/v1/auth/verificar-correo` con el token.

**Entonces** el sistema:
1. Cambia el estado de la cuenta a `ACTIVO`.
2. Invalida el token de verificación utilizado.
3. Responde con código HTTP `204 No Content`.
4. Publica el evento `usuario.creado` en el broker de mensajería.

### ESC-02.2 Uso de token de verificación expirado o reusado *(caso borde)*

**Dado** un token de verificación con más de 24 horas de generación o que ya fue consumido previamente.

**Cuando** se envía a `POST /api/v1/auth/verificar-correo`.

**Entonces** el sistema responde `410 ENLACE_EXPIRADO` o `410 ENLACE_YA_USADO`, mantiene la cuenta en `PENDIENTE_VERIFICACION` y no publica ningún evento.

### ESC-02.3 Intento de inicio de sesión de cuenta no verificada

**Dado** un usuario en estado `PENDIENTE_VERIFICACION`.

**Cuando** intenta autenticarse mediante `POST /api/v1/auth/login` con sus credenciales correctas.

**Entonces** el sistema responde `401 CREDENCIALES_INVALIDAS`, exactamente igual que ante una contraseña incorrecta, y no emite `accessToken` ni `refreshToken`.

### ESC-02.4 Reenvío de token con límite de tasa excedido *(caso borde)*

**Dado** un usuario que ya solicitó 3 reenvíos de correo de verificación en la última hora.

**Cuando** solicita un cuarto reenvío a través de `POST /api/v1/auth/verificar-correo/reenviar`.

**Entonces** el sistema responde con código HTTP `429 DEMASIADAS_SOLICITUDES` y no genera un nuevo correo ni invalida el último token activo.

### ESC-02.5 Reenvío a un correo que no existe *(caso borde de seguridad)*

**Dado** un correo que no pertenece a ninguna cuenta.

**Cuando** se solicita `POST /api/v1/auth/verificar-correo/reenviar` con ese correo.

**Entonces** el sistema responde `202`, igual que con una cuenta real, y no envía nada. A partir de la cuarta solicitud en una hora responde `429 DEMASIADAS_SOLICITUDES`, también igual.

---

## Requisitos no funcionales — ¿con qué condiciones?

- Los tokens de verificación deben generarse con aleatoriedad criptográfica (`SecureRandom`) de al menos 32 bytes (256 bits), codificados en Base64 URL-safe.
- El envío del correo debe ser asíncrono y no bloquear la respuesta del registro ni del reenvío.
- La respuesta del reenvío debe ser idéntica, en cuerpo y en límite, exista o no la cuenta.
- Los tokens no deben aparecer en logs ni en eventos.
- El tratamiento de datos personales debe alinearse con la Ley N.º 29733 de Protección de Datos Personales.

---

## Fuera de alcance — ¿qué NO hará?

- El registro de la cuenta, que es de SPEC-01.
- La solicitud de cambio de correo, que es de SPEC-16: esta spec solo aporta el mecanismo de confirmación.
- La verificación del celular por código, que es de SPEC-10.
- Las cuentas creadas por un administrador, que nacen `ACTIVO` y no se verifican (SPEC-03).

---

## Impacto en el contrato

| Endpoint / evento | Añade, modifica o elimina | Acordado con el PO |
|---|---|---|
| `POST /api/v1/auth/verificar-correo` | Utiliza contrato existente; lo reutiliza SPEC-16 | ⬜ |
| `POST /api/v1/auth/verificar-correo/reenviar` | Añade | ⬜ |
| `usuario.creado` | Publica evento | ⬜ |

---

## Lista de completitud

La spec se cierra cuando:

- [ ] Todos los requisitos están implementados
- [ ] Cada requisito clave tiene al menos dos escenarios automatizados, uno de ellos caso borde
- [ ] Los no funcionales están verificados, o la desviación está documentada y aprobada
- [ ] Los endpoints figuran en la documentación OpenAPI publicada (`specs/openapi.yaml`)
- [ ] Nada de lo declarado fuera de alcance se construyó de forma encubierta
- [ ] El código está en `main` con revisión aprobada y desplegado en nube
