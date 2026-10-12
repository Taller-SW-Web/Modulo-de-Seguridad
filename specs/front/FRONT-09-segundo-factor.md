# FRONT-09 — Segundo factor: activar y desactivar

| Campo | Valor |
|---|---|
| **Responsable** | Luis David Morales Brenis |
| **Épica principal** | [EP-04](../epicas/EP-04-segundo-factor.md) |
| **Specs de backend que consume** | SPEC-10, capacidad de códigos SPEC-09, perfil SPEC-16 |
| **Wireframe** | Pendiente; layout y estados definidos aquí |
| **Estado** | Definida localmente; H-01/H-02 resueltos en contrato local; sin implementar |
| **Revisión** | Edición contractual delegada; publicación y revisión visual pendientes |

## Objetivo — ¿para qué sirve esta pantalla?

Desde Seguridad de Mi cuenta, el titular habilita o deshabilita MFA confirmando
un código de correo. Quien tenga cualquier rol de gestión ve MFA obligatorio.
Se implementa contra [v2](../contrato-api/openapi-v2.yaml); no usa las rutas
históricas v1 incompletas.

## Endpoints que usa

| Momento | Endpoint | Spec |
|---|---|---|
| Al abrir o tras confirmar | GET /api/v1/auth/me, revisión local 1.1.0 | 16, 10 |
| Solicitar activar/desactivar | POST /api/v2/auth/mfa/cambios: accion HABILITAR/DESHABILITAR | 10 |
| Reenviar | POST /api/v2/auth/mfa/cambios/reenviar: mfaChangeToken | 10 |
| Confirmar | POST /api/v2/auth/mfa/cambios/confirmar: mfaChangeToken, codigo | 10 |

Access token del titular en las tres llamadas de cambio. Token/código solo
en memoria y cuerpo definido, nunca URL/almacenamiento persistente.
No envía usuarioId, correo destino, propósito ni challengeToken de login.

## Estados de la pantalla

| Estado | Cuándo ocurre | Qué ve el usuario |
|---|---|---|
| Cargando | Abre o refresca perfil | Indicador, acciones deshabilitadas |
| Desactivado | mfaHabilitado false | Explicación y Activar |
| Activo | mfaHabilitado true sin rol de gestión | Aviso y Desactivar |
| Obligatorio | Cualquier rol de gestión | Activo, obligatorio para tu rol; sin botón de desactivar |
| Enviando | Solicita o reenvía | Enviando un código a tu correo, sin doble envío |
| Confirmando | 202 | Correo enmascarado, campo seis dígitos, Confirmar/Reenviar/Cancelar |
| Código rechazado | CODIGO_INVALIDO | Error y reintentos disponibles; estado anterior intacto |
| Código vencido/agotado | OTP_EXPIRADO/OTP_INTENTOS_AGOTADOS | Reenviar si cambio vigente y cuota disponible |
| Límite | 429 | Espera de Retry-After; reenvío deshabilitado, sin envío automático |
| Cambio vencido | cambioExpiraEn llega a cero | Reiniciar solicitud; eliminar token de cambio |
| Éxito | 204 | Cambio confirmado; volver a consultar perfil |
| Sesión inválida | TOKEN_INVALIDO | Borrar cambio/sesión y volver al login; no inferir cuenta bloqueada |
| Fallo de servicio | NO_DISPONIBLE o red | Estado no confirmado; permitir recuperación controlada |

La cuenta regresiva muestra min(expiraEn, cambioExpiraEn), calculado desde
instante de recepción. Reenvío no prolonga el cambio. Recarga exige login
(H-11). Cancelar cierra diálogo y borra token local; no llama a una ruta
de cancelación inexistente. La operación expira en servidor o queda
invalidada al iniciar otra solicitud.

## Validaciones del lado del cliente

Código `^[0-9]{6}$`, como string (conserva ceros iniciales). Validar no
autoriza: el servidor reevalúa titular, familia, cuenta, roles y propósito.
No cambiar estado optimistamente; solo tras 204 y nueva lectura del perfil.

## Textos y mensajes

| Código | Mensaje |
|---|---|
| VALIDACION | Revisa los datos o actualiza el estado del segundo factor. |
| CODIGO_INVALIDO | El código no es correcto. Revisa los seis dígitos. |
| OTP_INTENTOS_AGOTADOS | Agotaste los intentos; solicita otro código. |
| OTP_EXPIRADO | El código venció; solicita otro. |
| MFA_OBLIGATORIO | Tu rol exige el segundo factor; no puedes desactivarlo. |
| DEMASIADAS_SOLICITUDES | Espera el tiempo indicado antes de pedir otro código. |
| TOKEN_INVALIDO | Vuelve a iniciar sesión para continuar. |
| TOKEN_NO_APLICABLE | No se pudo autorizar esta operación. |
| NO_DISPONIBLE | FRONT-00: reintentar de forma controlada. |

Antes de solicitar desactivación: diálogo con efecto sobre el próximo login
y Cancelar/Continuar. El código confirma la decisión; el primer clic no la aplica.
Si la red falla después de confirmar, consultar perfil cuando sea posible;
no reenviar confirmación ciegamente con token que pudo consumirse.

## Escenarios de interfaz — ¿cómo verificamos?

| Caso | Dado | Cuando | Entonces |
|---|---|---|---|
| UI-09.1 | Perfil sin MFA | Activa y confirma código | 202, código, 204 y lectura true |
| UI-09.2 | Perfil con MFA voluntario | Desactiva y confirma | Diálogo, 202, código, 204 y lectura false |
| UI-09.3 | CLIENTE y ADMIN_SISTEMA | Abre pantalla | Obligatorio, sin Desactivar |
| UI-09.4 | Código incorrecto | Envía | Error, conserva estado anterior |
| UI-09.5 | OTP vencido, cambio vigente | Reenvía | Sustituye código; no amplía tiempo de cambio |
| UI-09.6 | Límite de envíos | Recibe 429 | Cuenta espera; no reenvía automáticamente |
| UI-09.7 | Confirmación en vuelo | Hace doble clic | Una llamada; control deshabilitado |
| UI-09.8 | Sesión revocada/cambio inválido | Recibe 401 | Borrar estado y volver al login |
| UI-09.9 | Red falla tras confirmar | Recupera conexión | Consulta perfil antes de nueva acción |
| UI-09.10 | Cambio termina | Expira contador o cancela | Borra token; nueva solicitud explícita |

## Accesibilidad y diseño adaptable

Campo con etiqueta; admite pegar seis dígitos y operar con teclado.
Foco al abrir diálogo; restaurarlo al cerrar. Error asociado al campo y
avisos accesibles sin anunciar cada segundo. Layout móvil sin desplazamiento
horizontal; botón ocupado anuncia progreso.

## Fuera de alcance — ¿qué NO hará?

No verifica contactos, no SMS real, no cambia MFA de terceros ni emite sesión.
Wireframe pendiente no acredita diseño visual aprobado ni implementación.

## Lista de completitud

- [ ] Estados y UI-09.1–10 verificados
- [ ] Contrato publicado y servicios implementados antes del consumo externo
- [ ] Wireframe revisado, teclado y mensajes comprobados
- [ ] Dependencia DAT-01 y auditoría implementadas con sus dueños
