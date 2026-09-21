# SPEC-07 — Política y cambio de contraseña

| Campo | Valor |
|---|---|
| **Responsable** | Juan José Cano Vasquez |
| **Hito objetivo** | Hito 3 (Sem. 8) — política · Hito 4 (Sem. 11) — cambio |
| **Estado** | Borrador |
| **Aprobada por** | Product Owner — pendiente de aprobación |

> **Origen.** Sale de dividir la antigua SPEC-03 «Gestión de credenciales y contraseñas» el 20 de septiembre, por indicación del profesor: una spec por función. La política de contraseñas sigue sin ser una spec suelta, como pidió el profesor el 13 de septiembre: va junto al cambio autenticado, que es el flujo que más la ejercita. La recuperación pasó a SPEC-08. La equivalencia de números está en [`trazabilidad.md`](trazabilidad.md) §1.

---

## Contexto — ¿por qué?

Las credenciales son un control fundamental para proteger las cuentas. Una política de contraseñas débil facilita el acceso no autorizado, y una política duplicada en cada flujo que la usa —registro, alta, cambio, restablecimiento— acaba aplicándose distinto en cada uno.

Esta spec centraliza las reglas de robustez, el historial y la caducidad en un único sitio que invocan SPEC-01, SPEC-03 y SPEC-08, y define el cambio de contraseña de un usuario con sesión iniciada.

---

## Propósito — ¿para qué?

Garantizar que toda contraseña del sistema cumple una política mínima común, y permitir que un usuario autenticado cambie la suya confirmando la actual.

El resultado observable es un único `422 POLITICA_INCUMPLIDA` con las reglas falladas en cualquier flujo que establezca una contraseña, y un cambio de contraseña notificado por correo y auditado.

---

## Alcance — ¿hasta dónde?

- Longitud, complejidad, contraseñas comunes y datos personales.
- Historial de las últimas cinco contraseñas.
- Caducidad de 90 días para las cuentas con rol de gestión.
- Publicación de la política con `GET /api/v1/password/politica` para el medidor de fuerza de la interfaz.
- Cambio de contraseña autenticado con `POST /api/v1/password/cambiar`.
- Notificación por correo y auditoría del cambio.

---

## Requisitos — ¿qué debe hacer?

| Código | Requisito |
|---|---|
| RF-07.1 | El sistema debe exigir una longitud mínima de **10 caracteres** para las contraseñas. |
| RF-07.2 | El sistema debe exigir al menos una letra mayúscula, una letra minúscula, un dígito y un carácter especial. |
| RF-07.3 | El sistema debe rechazar contraseñas comunes y aquellas que contengan datos personales del usuario, como nombre, apellido o correo electrónico. |
| RF-07.4 | El sistema debe impedir la reutilización de cualquiera de las **últimas cinco contraseñas** utilizadas. |
| RF-07.5 | Las reglas de validación deben aplicarse obligatoriamente en el **backend**, independientemente del cliente. |
| RF-07.6 | Las contraseñas deben almacenarse mediante un mecanismo de **hash seguro** y nunca en texto claro. |
| RF-07.7 | Las contraseñas de las cuentas con algún rol de gestión (`ADMIN_VENTAS`, `GESTOR_DESPACHO`, `GESTOR_COMERCIAL`, `ADMIN_SISTEMA`) deben expirar cada **90 días**. Al completar la autenticación con una contraseña caducada no se emiten tokens: se responde `403 PASSWORD_CADUCADA` y el titular la restablece con el flujo de recuperación (RF-08.1 a RF-08.5). |
| RF-07.8 | El cambio de contraseña de un usuario autenticado debe requerir la verificación de su contraseña actual y validar la nueva contraseña. |
| RF-07.9 | El sistema debe notificar al usuario por correo después de un cambio de contraseña exitoso. |
| RF-07.10 | El cambio de contraseña debe registrarse mediante SPEC-12 como `CONTRASENA_CAMBIADA` (acción crítica). |
| RF-07.11 | El sistema debe publicar la política vigente en `GET /api/v1/password/politica`, para que la interfaz construya el medidor de fuerza con **estas** reglas y no con una copia propia. |

---

## Escenarios — ¿cómo verificamos?

Formato Dado / Cuando / Entonces. Los marcados como **caso borde** cubren
condiciones límite, de error o de seguridad.

### ESC-07.1 Contraseña válida

**Dado** que el usuario proporciona una contraseña que cumple la longitud y complejidad requeridas,
**Cuando** el sistema la valida,
**Entonces** la contraseña es aceptada.

### ESC-07.2 Contraseña demasiado corta

**Dado** que la contraseña tiene menos de 10 caracteres,
**Cuando** el usuario intenta establecerla,
**Entonces** el sistema la rechaza.

### ESC-07.3 Complejidad insuficiente

**Dado** que la contraseña no contiene alguno de los tipos de caracteres requeridos,
**Cuando** se ejecuta la validación,
**Entonces** el sistema la rechaza.

### ESC-07.4 Contraseña común o con datos personales

**Dado** que la contraseña pertenece a una lista de contraseñas comunes o contiene datos personales del usuario,
**Cuando** se ejecuta la validación,
**Entonces** el sistema la rechaza.

### ESC-07.5 Reutilización de contraseña

**Dado** que el usuario intenta utilizar una de sus últimas cinco contraseñas,
**Cuando** solicita el cambio,
**Entonces** el sistema rechaza la nueva contraseña.

### ESC-07.6 Validación en backend

**Dado** que un cliente intenta omitir las validaciones de contraseña,
**Cuando** envía directamente una contraseña inválida al backend,
**Entonces** el backend rechaza la operación.

### ESC-07.7 Contraseña administrativa expirada

**Dado** que una cuenta con un rol de gestión tiene una contraseña con 90 días o más de antigüedad,
**Cuando** el usuario completa la autenticación —con el segundo factor, que para ese rol es obligatorio—,
**Entonces** el sistema responde `403 PASSWORD_CADUCADA`, no emite tokens, y el usuario debe restablecer la contraseña mediante `/password/recuperar`.

### ESC-07.8 Cambio autenticado

**Dado** que el usuario está autenticado y proporciona correctamente su contraseña actual,
**Cuando** proporciona una nueva contraseña válida,
**Entonces** el sistema realiza el cambio y registra `CONTRASENA_CAMBIADA`.

### ESC-07.9 Contraseña actual incorrecta

**Dado** que el usuario proporciona una contraseña actual incorrecta,
**Cuando** intenta cambiarla,
**Entonces** el sistema rechaza la operación y no modifica la contraseña.

---

## Requisitos no funcionales — ¿con qué condiciones?

- La validación de la política debe ejecutarse en menos de **10 ms** en condiciones normales.
- La lista de contraseñas comunes debe poder actualizarse sin modificar el código ni desplegar de nuevo.
- Las contraseñas se almacenan solo como hash seguro, nunca en texto claro.
- El envío de correos debe ser **asíncrono** para no bloquear la operación principal.

---

## Fuera de alcance — ¿qué NO hará?

- La recuperación y el restablecimiento por correo, que son de SPEC-08.
- El cambio de dirección de correo, que es de SPEC-16.
- La implementación del segundo factor (SPEC-09 y SPEC-10).
- Expiración general de sesiones o tokens de autenticación.
- Autenticación biométrica o mediante hardware.

---

## Decisiones de diseño registradas

1. **La política no es una spec suelta.** El profesor indicó el 13 de septiembre que no se sostiene sola; al dividir el set se mantuvo junto al cambio de contraseña, y la recuperación (SPEC-08) la invoca.
2. **La validación se realiza en backend.** Las validaciones del cliente no sustituyen las comprobaciones del servidor.
3. **Un solo código de error para todas las reglas.** Cuál falló va en `errores`, para que añadir una regla no obligue a los consumidores a conocer un código más.

---

## Impacto en el contrato

| Endpoint / evento | Añade, modifica o elimina | Acordado con el PO |
|---|---|---|
| `GET /api/v1/password/politica` | Utiliza contrato existente | ⬜ |
| `POST /api/v1/password/cambiar` | Utiliza contrato existente | ⬜ |
| `422 POLITICA_INCUMPLIDA` | Lo devuelven también `/auth/registro`, `/usuarios` y `/password/restablecer` | ⬜ |
| `CONTRASENA_CAMBIADA` | Acción del catálogo de auditoría de SPEC-12 | ⬜ |

---

## Lista de completitud

La spec se cierra cuando:

- [ ] Todos los requisitos están implementados
- [ ] Cada requisito clave tiene al menos dos escenarios automatizados, uno de ellos caso borde
- [ ] Los no funcionales están verificados, o la desviación está documentada y aprobada
- [ ] Los endpoints figuran en la documentación OpenAPI publicada (`specs/openapi.yaml`)
- [ ] Nada de lo declarado fuera de alcance se construyó de forma encubierta
- [ ] El código está en `main` con revisión aprobada y desplegado en nube
