# SPEC-10 — Activación y desactivación del segundo factor

| Campo | Valor |
|---|---|
| **Responsable** | Luis David Morales Brenis |
| **Épica** | [EP-04 — Segundo factor: OTP y MFA](../epicas/EP-04-segundo-factor.md) |
| **Reparto vigente** | 11-oct-2026; dueño completo de interfaz, backend, datos y pruebas |
| **Primera versión** | Alcance/dependencias en ficha de épica; no integra la demo mínima de Ventas |
| **Hito objetivo** | Hito 4 (Sem. 11) |
| **Estado** | Definida localmente con edición delegada; definición publicada por PR #45/#46; implementación y adopción pendientes |
| **Base histórica aprobada por** | Sergio Alejandro Osorio Montenegro (PO), 2-oct-2026; no constituye aprobación de esta revisión |
| **Revisión contractual** | 11-oct: autorización de Sergio comunicada por Luis; [decisión y alcance](../../docs/arquitectura/adr/008-evolucion-mfa-y-alta-administrativa.md) |

Origen: antigua SPEC-04 dividida en SPEC-09/10. Ocho épicas y dieciocho specs
se conservan; el segundo factor completo pertenece a Luis David.
Contrato seguro publicado: [OpenAPI v2](../contrato-api/openapi-v2.yaml).
La base histórica v1 incompleta no es el flujo a implementar.

## Contexto — ¿por qué?

Las cuentas de gestión necesitan segundo factor obligatorio. Clientes y
vendedores pueden elegirlo, pero cambiarlo exige demostrar acceso al correo.
El contrato v1 enviaba un código para habilitar sin completar su confirmación
y permitía deshabilitar sin código. Esa contradicción se resuelve en v2.
A2 conserva SMS simulado y excluye verificación externa de contactos.

## Propósito — ¿para qué?

Permitir que el titular active o desactive MFA después de confirmar un código
de correo, manteniendo su obligatoriedad si existe cualquier rol de gestión.
El resultado es un estado efectivo consultable, actualizado y auditado.

## Alcance — ¿hasta dónde?

- Lectura efectiva `mfaHabilitado` en `GET /api/v1/auth/me` (revisión local 1.1.0).
- Solicitud, reenvío y confirmación por EMAIL en `/api/v2/auth/mfa/cambios`.
- Generación/envío/verificación de código mediante la capacidad de SPEC-09,
  con operación independiente del desafío de login.
- Estado, sesión y roles comprobados en servidor; auditoría mediante SPEC-12.
- Ningún cambio del login, issuer o audiencia pública para la demo de Ventas.

## Requisitos — ¿qué debe hacer?

| Código | Requisito |
|---|---|
| RF-10.1 | MFA obligatorio cuando exista ADMIN_VENTAS, GESTOR_DESPACHO, GESTOR_COMERCIAL o ADMIN_SISTEMA. Basta uno entre múltiples roles. El estado efectivo leído es true incluso si una bandera histórica quedó false. |
| RF-10.2 | El titular con MFA desactivado solicita HABILITAR; 202 envía EMAIL y devuelve mfaChangeToken, canal, destinoEnmascarado, expiraEn y cambioExpiraEn. Solo confirmar correctamente actualiza estado; no crea sesión. |
| RF-10.3 | El titular permitido solicita DESHABILITAR y confirma código. Cualquier rol de gestión impide solicitar o confirmar con 422 MFA_OBLIGATORIO. Se revalidan roles y cuenta al confirmar. |
| RF-10.4 | No se ofrece endpoint ni scope de verificación externa de correo/celular en este ciclo (A2). Un OTP de login o de cambio MFA no verifica contactos. |
| RF-10.5 | Solicitud/verificación producen OTP_SOLICITADO, OTP_VERIFICADO u OTP_FALLIDO con propósito; cambio/rechazo producen MFA_ACTIVADO o MFA_DESACTIVADO y resultado según SPEC-12. Estado, consumo y auditoría crítica se persisten atómicamente; fallo de auditoría: 503 NO_DISPONIBLE sin cambio. |
| RF-10.6 | Cambio vence a los 600 s; cada código a los 300 s. Confirmar exige ambos vigentes; reenviar no extiende cambio y sustituye código atómicamente. UI muestra el menor tiempo restante. Solo un cambio pendiente por usuario; otro invalida el anterior. |
| RF-10.7 | mfaChangeToken opaco, aleatorio y almacenado por hash, asociado a usuario, familia de sesión y acción en servidor. Nunca acepta usuarioId/destino/propósito en confirmación, ni challengeToken en lugar del token de cambio. JWT renovado en la misma familia conserva vínculo; otra sesión no. |
| RF-10.8 | Máximo tres intentos por código. Código incorrecto: 401 CODIGO_INVALIDO con intentosRestantes; tercer fallo: 401 OTP_INTENTOS_AGOTADOS. Cambio/sesión inválido, vencido o consumido: 401 TOKEN_INVALIDO antes de comparar OTP. Solo código vencido, con cambio vigente: 410 OTP_EXPIRADO. Servicio: 403 TOKEN_NO_APLICABLE. |
| RF-10.9 | Tres envíos por usuario en ventana móvil de 15 minutos compartidos entre login y cambios. Cuarto: 429 DEMASIADAS_SOLICITUDES con Retry-After positivo. Pedir el estado ya vigente: 400 VALIDACION sin envío ni cuota. |
| RF-10.10 | Consumo de cambio/OTP y transición son de uso único y atómicos. Dos confirmaciones simultáneas no cambian dos veces ni emiten sesión. No devolver códigos, tokens o contactos sin máscara en errores/auditoría. |

Confirmación exige token de acceso vigente y familia no revocada. Jose Luis
provee resolución interna `jti → usuario/familia/vigencia`, sin claim nuevo
ni credencial de servicio en navegador; Luis consume esa capacidad.
La persistencia pendiente está trazada en [DAT-01](../../docs/integracion/revision-datos.md).

## Escenarios — ¿cómo verificamos?

Todos usan Dado / Cuando / Entonces. Cada requisito clave tiene éxito y borde.
Son casos para implementar; la validación de schemas no demuestra su ejecución.

| Caso | RF | Dado | Cuando | Entonces |
|---|---|---|---|---|
| ESC-10.1 | 10.2 | CLIENTE autenticado sin MFA | Solicita HABILITAR y confirma código correcto | 202 seguido de 204, estado true y auditoría; sin nuevos tokens |
| ESC-10.2 | 10.3 | VENDEDOR autenticado con MFA | Solicita DESHABILITAR y confirma | 202 seguido de 204, estado false y auditoría |
| ESC-10.3 | 10.1, 10.3 | ADMIN_VENTAS | Intenta desactivar | 422 MFA_OBLIGATORIO, estado true, rechazo auditado |
| ESC-10.4 | 10.4, 10.7 | challengeToken de login | Lo presenta como mfaChangeToken | 401 TOKEN_INVALIDO; no cambia MFA ni verifica contactos |
| ESC-10.5 | 10.1 | Roles VENDEDOR y GESTOR_COMERCIAL, bandera histórica false | Consulta /auth/me o intenta desactivar | Lectura true; desactivación 422 |
| ESC-10.6 | 10.2 | Cambio HABILITAR vigente | Presenta código incorrecto | 401 CODIGO_INVALIDO, contador actualizado; estado false |
| ESC-10.7 | 10.4 | Celular recién editado no verificado | Confirma OTP de login/cambio MFA | Celular continúa no verificado; ningún flujo externo disponible |
| ESC-10.8 | 10.5 | Cambio válido y persistencia disponible | Confirma correctamente | Consumo, estado y auditoría crítica persistidos conjuntamente |
| ESC-10.9 | 10.5 | Cambio válido | Falla persistencia de auditoría | 503 NO_DISPONIBLE, transición no aplicada; reintento controlado |
| ESC-10.10 | 10.6 | Cambio con 240 s restantes y OTP anterior | Reenvía | 202, nuevo OTP, cambio sigue 240 s; código anterior no verifica |
| ESC-10.11 | 10.6 | Cambio vencido o reemplazado | Confirma aunque OTP no haya vencido | 401 TOKEN_INVALIDO |
| ESC-10.12 | 10.7 | Cambio de sesión A y acceso renovado en familia A | Confirma | Vínculo válido; se aplican las demás validaciones |
| ESC-10.13 | 10.7 | Cambio de otro usuario o familia B | Confirma con acceso de A | 401 TOKEN_INVALIDO, sin consumir operación ajena |
| ESC-10.14 | 10.8 | Dos fallos de OTP | Envía tercer código incorrecto | 401 OTP_INTENTOS_AGOTADOS; OTP invalidado |
| ESC-10.15 | 10.8 | Cambio vigente, OTP vencido | Confirma | 410 OTP_EXPIRADO; puede reenviar dentro de límites |
| ESC-10.16 | 10.9 | Ya hubo tres envíos, incluyendo login | Solicita cuarto envío MFA | 429 y Retry-After, sin nuevo código |
| ESC-10.17 | 10.9 | MFA ya habilitado | Solicita HABILITAR | 400 VALIDACION, ningún envío ni cuota |
| ESC-10.18 | 10.10 | Dos confirmaciones correctas simultáneas | Compiten por el mismo cambio | Solo una 204; segunda 401 TOKEN_INVALIDO, una transición exitosa |
| ESC-10.19 | 10.10 | Cambio ya confirmado | Repite confirmación | 401 TOKEN_INVALIDO, sin segunda transición |
| ESC-10.20 | 10.3, 10.8 | Solicitud de desactivación; luego sesión revocada al añadir rol de gestión | Confirma con acceso anterior | 401 TOKEN_INVALIDO; si sesión válida con rol de gestión, 422 MFA_OBLIGATORIO; MFA permanece obligatorio |

## Requisitos no funcionales — ¿con qué condiciones?

- Confirmación menor a 300 ms p95, excluyendo latencia de correo.
- Generador criptográfico, almacenamiento de hashes y comparaciones resistentes
  a diferencias de tiempo; no registrar OTP, tokens ni documento en claro.
- Correo se envía asíncronamente; publicar 202 exige persistir operación y
  entrega pendiente, sin cambiar MFA antes de confirmar.
- Contactos enmascarados y control transaccional frente a concurrencia.

## Fuera de alcance — ¿qué NO hará?

SMS real, verificación externa de contactos (A2), TOTP, códigos de respaldo,
desactivación por administrador de otra cuenta, sesión persistente en navegador
o excepción al aviso contractual de ADR-003.

## Impacto en el contrato

| Endpoint / evento | Tratamiento | Estado |
|---|---|---|
| GET /api/v1/auth/me | Perfil con mfaHabilitado; campos privados adicionales | Publicado 1.1.0; servidor pendiente |
| POST /api/v2/auth/mfa/cambios | Solicitar estado objetivo | Local 2.0.0 |
| POST /api/v2/auth/mfa/cambios/reenviar | Reenvío dentro de vigencia | Local 2.0.0 |
| POST /api/v2/auth/mfa/cambios/confirmar | Confirmación con código | Local 2.0.0 |
| POST /api/v1/auth/otp/habilitar y /deshabilitar | Históricos deprecados; implementación bloqueada por seguridad | No desplegar |
| MFA_ACTIVADO / MFA_DESACTIVADO | Catálogo de auditoría existente, no eventos RabbitMQ nuevos | SPEC-12 |

## Lista de completitud

- [ ] Requisitos y escenarios implementados por Luis David
- [ ] Persistencia, asociación con familia y concurrencia verificadas
- [ ] Auditoría crítica y errores comprobados
- [ ] FRONT-09 y su componente implementados
- [ ] Contrato publicado con versionado y aviso de ADR-003
- [ ] Revisado, desplegado y demostrado; mock no acredita integración real
