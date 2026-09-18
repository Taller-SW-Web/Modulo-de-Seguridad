# SPEC-03 — Gestión de credenciales y contraseñas

| Campo             | Valor                                                                                |
| ----------------- | ------------------------------------------------------------------------------------ |
| **Responsable**   | Juan José Cano Vasquez                                                                        |
| **Hito objetivo** | Hito 3 (Sem. 8) — política de contraseñas · Hito 4 (Sem. 11) — recuperación y cambio |
| **Estado**        | Borrador - pendiente de revisión del equipo                                          |
| **Aprobada por**  | Product owner - pendiente de revision                                                                                    |

> **Origen de esta spec.** La política de contraseñas fue fusionada dentro de SPEC-03 por indicación del profesor. Esta reorganización dejó libre el número SPEC-06, utilizado posteriormente para auditoría y trazabilidad. Por ello, SPEC-03 concentra la gestión del ciclo de vida de las credenciales: política, historial, cambio, recuperación y expiración.

---

## Contexto — ¿por qué?

Las credenciales constituyen un control fundamental para proteger las cuentas del sistema. Una política de contraseñas débil o un proceso de recuperación inseguro puede facilitar accesos no autorizados.

Esta especificación centraliza las reglas para crear, cambiar y recuperar contraseñas, además de establecer los controles necesarios para evitar su reutilización y el uso indebido de tokens de recuperación.

SPEC-03 también se integra con la auditoría de seguridad mediante los eventos `RECUPERACION_SOLICITADA`, `CONTRASENA_RESTABLECIDA` y `CONTRASENA_CAMBIADA`.

---

## Propósito — ¿para qué?

Definir una gestión segura y consistente del ciclo de vida de las credenciales, garantizando que las contraseñas cumplan una política mínima de seguridad y que los procesos de cambio y recuperación protejan la cuenta del usuario.

---

## Alcance — ¿hasta dónde?

Esta especificación cubre:

* Validación de complejidad de contraseñas.
* Rechazo de contraseñas comunes y relacionadas con datos personales.
* Historial de las últimas cinco contraseñas.
* Expiración de contraseñas administrativas.
* Cambio de contraseña para usuarios autenticados.
* Recuperación mediante correo electrónico.
* Tokens de recuperación temporales y de un solo uso.
* Invalidación de sesiones o tokens anteriores después de un restablecimiento.
* Notificaciones por correo.
* Registro de eventos relacionados con las credenciales en auditoría.

---

## Requisitos — ¿qué debe hacer?

| Código       | Requisito                                                                                                                                               |
| ------------ | ------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **RF-03.1**  | El sistema debe exigir una longitud mínima de **10 caracteres** para las contraseñas.                                                                   |
| **RF-03.2**  | El sistema debe exigir al menos una letra mayúscula, una letra minúscula, un dígito y un carácter especial.                                             |
| **RF-03.3**  | El sistema debe rechazar contraseñas comunes y aquellas que contengan datos personales del usuario, como nombre, apellido o correo electrónico.         |
| **RF-03.4**  | El sistema debe impedir la reutilización de cualquiera de las **últimas cinco contraseñas** utilizadas.                                                 |
| **RF-03.5**  | Las reglas de validación deben aplicarse obligatoriamente en el **backend**, independientemente del cliente.                                            |
| **RF-03.6**  | Las contraseñas deben almacenarse mediante un mecanismo de **hash seguro** y nunca en texto claro.                                                      |
| **RF-03.7**  | Las contraseñas de las cuentas administrativas deben expirar cada **90 días**.                                                                          |
| **RF-03.8**  | El sistema debe permitir solicitar la recuperación mediante el correo asociado a la cuenta sin revelar si dicho correo está registrado.                 |
| **RF-03.9**  | El sistema debe generar un **token de recuperación de un solo uso** con una vigencia máxima de **30 minutos**.                                          |
| **RF-03.10** | Una nueva solicitud de recuperación debe invalidar los tokens anteriores asociados a la cuenta.                                                         |
| **RF-03.11** | El sistema debe rechazar tokens de recuperación expirados o ya utilizados.                                                                              |
| **RF-03.12** | Después de un restablecimiento exitoso, el sistema debe validar la nueva contraseña e invalidar los tokens o sesiones anteriores asociados a la cuenta. Si la cuenta tiene un bloqueo automático, el restablecimiento lo levanta (ver SPEC-07). La recuperación funciona aunque la cuenta esté bloqueada. |
| **RF-03.13** | El cambio de contraseña de un usuario autenticado debe requerir la verificación de su contraseña actual y validar la nueva contraseña.                  |
| **RF-03.14** | El sistema debe notificar al usuario mediante correo después de un cambio o restablecimiento exitoso.                                                   |
| **RF-03.15** | El sistema debe generar los eventos de auditoría `RECUPERACION_SOLICITADA`, `CONTRASENA_RESTABLECIDA` y `CONTRASENA_CAMBIADA`.                          |

---

## Escenarios — ¿cómo verificamos?

### ESC-03.1 — Contraseña válida

**Dado** que el usuario proporciona una contraseña que cumple la longitud y complejidad requeridas,
**Cuando** el sistema la valida,
**Entonces** la contraseña es aceptada.

### ESC-03.2 — Contraseña demasiado corta

**Dado** que la contraseña tiene menos de 10 caracteres,
**Cuando** el usuario intenta establecerla,
**Entonces** el sistema la rechaza.

### ESC-03.3 — Complejidad insuficiente

**Dado** que la contraseña no contiene alguno de los tipos de caracteres requeridos,
**Cuando** se ejecuta la validación,
**Entonces** el sistema la rechaza.

### ESC-03.4 — Contraseña común o con datos personales

**Dado** que la contraseña pertenece a una lista de contraseñas comunes o contiene datos personales del usuario,
**Cuando** se ejecuta la validación,
**Entonces** el sistema la rechaza.

### ESC-03.5 — Reutilización de contraseña

**Dado** que el usuario intenta utilizar una de sus últimas cinco contraseñas,
**Cuando** solicita el cambio,
**Entonces** el sistema rechaza la nueva contraseña.

### ESC-03.6 — Validación en backend

**Dado** que un cliente intenta omitir las validaciones de contraseña,
**Cuando** envía directamente una contraseña inválida al backend,
**Entonces** el backend rechaza la operación.

### ESC-03.7 — Contraseña administrativa expirada

**Dado** que una cuenta administrativa tiene una contraseña con 90 días o más de antigüedad,
**Cuando** el usuario intenta autenticarse,
**Entonces** el sistema solicita actualizar la contraseña.

### ESC-03.8 — Recuperación con correo registrado

**Dado** que existe una cuenta asociada al correo proporcionado,
**Cuando** el usuario solicita recuperar su contraseña,
**Entonces** el sistema procesa la solicitud y genera un mecanismo de recuperación.

### ESC-03.9 — Recuperación con correo no registrado

**Dado** que no existe una cuenta asociada al correo proporcionado,
**Cuando** se solicita la recuperación,
**Entonces** el sistema responde de forma equivalente sin revelar que la cuenta no existe.

### ESC-03.10 — Token válido

**Dado** que el usuario posee un token válido y no utilizado,
**Cuando** lo utiliza dentro de los 30 minutos y proporciona una contraseña válida,
**Entonces** el sistema permite restablecer la contraseña.

### ESC-03.11 — Token expirado o utilizado

**Dado** que el token ha expirado o ya fue utilizado,
**Cuando** el usuario intenta utilizarlo,
**Entonces** el sistema rechaza la operación.

### ESC-03.12 — Nueva solicitud de recuperación

**Dado** que el usuario solicita la recuperación dos veces,
**Cuando** se genera el segundo token,
**Entonces** el token anterior queda invalidado.

### ESC-03.13 — Cambio autenticado

**Dado** que el usuario está autenticado y proporciona correctamente su contraseña actual,
**Cuando** proporciona una nueva contraseña válida,
**Entonces** el sistema realiza el cambio y registra `CONTRASENA_CAMBIADA`.

### ESC-03.14 — Contraseña actual incorrecta

**Dado** que el usuario proporciona una contraseña actual incorrecta,
**Cuando** intenta cambiarla,
**Entonces** el sistema rechaza la operación y no modifica la contraseña.

### ESC-03.15 — Restablecimiento e invalidación

**Dado** que el usuario completa correctamente una recuperación,
**Cuando** se establece la nueva contraseña,
**Entonces** el sistema invalida las sesiones o tokens anteriores y registra `CONTRASENA_RESTABLECIDA`.

---

## Requisitos no funcionales — ¿con qué condiciones?

* La validación de complejidad debe ejecutarse en menos de **10 ms** en condiciones normales.
* Los tokens de recuperación deben almacenarse de forma protegida y no en texto claro.
* El envío de correos debe realizarse de forma **asíncrona** para no bloquear la operación principal.
* La lista de contraseñas comunes debe poder actualizarse sin modificar el código ni realizar un nuevo despliegue.
* La respuesta del proceso de recuperación debe mantener un comportamiento uniforme para cuentas existentes y no existentes, evitando la enumeración de usuarios.

---

## Fuera de alcance — ¿qué NO hará?

* Recuperación mediante SMS.
* Recuperación mediante preguntas secretas.
* Cambio de dirección de correo electrónico.
* Administración general de usuarios, roles y permisos.
* Expiración general de sesiones o tokens de autenticación.
* Autenticación biométrica o mediante hardware.
* Implementación del segundo factor de autenticación (MFA/OTP).
* Consulta y administración de la bitácora de auditoría.

---

## Dependencias conocidas

| SPEC        | Dependencia                                            |
| ----------- | ------------------------------------------------------ |
| **SPEC-01** | Gestión de usuarios y datos asociados a la cuenta.     |
| **SPEC-02** | Autenticación de usuarios mediante credenciales.       |
| **SPEC-04** | MFA/OTP, cuando corresponda al flujo de autenticación. |
| **SPEC-06** | Registro y trazabilidad de eventos de seguridad.       |
| **SPEC-07** | Gestión del bloqueo de cuentas.                        |
| **SPEC-09** | Contrato común de la API de identidad.                 |

---

## Decisiones de diseño registradas

1. **SPEC-03 concentra el ciclo de vida de las credenciales.** La política de contraseñas fue fusionada en esta SPEC por indicación del profesor.

2. **La validación se realiza en backend.** Las validaciones del cliente no sustituyen las comprobaciones del servidor.

3. **La recuperación evita la enumeración de cuentas.** El sistema no debe revelar si el correo utilizado pertenece a un usuario registrado.

4. **Los tokens de recuperación son temporales y de un solo uso.** Una nueva solicitud invalida el mecanismo de recuperación anterior.

5. **Los eventos de credenciales se integran con auditoría.** Los eventos generados son `RECUPERACION_SOLICITADA`, `CONTRASENA_RESTABLECIDA` y `CONTRASENA_CAMBIADA`.

---

## Impacto en el contrato

| Endpoint / evento              | Cambio                                         | Acordado con PO |
| ------------------------------ | ---------------------------------------------- | --------------- |
| Recuperación de contraseña     | Añade / modifica operación de recuperación     | ⬜               |
| Restablecimiento de contraseña | Añade / modifica operación de restablecimiento | ⬜               |
| Cambio de contraseña           | Añade / modifica operación de cambio           | ⬜               |
| `RECUPERACION_SOLICITADA`      | Evento de auditoría                            | ⬜               |
| `CONTRASENA_RESTABLECIDA`      | Evento de auditoría                            | ⬜               |
| `CONTRASENA_CAMBIADA`          | Evento de auditoría                            | ⬜               |

> Las rutas, métodos HTTP y esquemas concretos deberán corresponder al `openapi.yaml` aprobado. Esta SPEC no define rutas que todavía no estén establecidas en el contrato.

---

## Lista de completitud

* [ ] Política de contraseñas implementada y validada.
* [ ] Historial de las últimas cinco contraseñas implementado.
* [ ] Expiración de contraseñas administrativas implementada.
* [ ] Cambio autenticado implementado.
* [ ] Recuperación y restablecimiento implementados.
* [ ] Tokens temporales y de un solo uso verificados.
* [ ] Invalidación de sesiones/tokens anteriores verificada.
* [ ] Notificaciones por correo implementadas.
* [ ] Eventos de auditoría integrados.
* [ ] Escenarios funcionales y casos borde automatizados.
* [ ] Contrato OpenAPI actualizado y revisado.
