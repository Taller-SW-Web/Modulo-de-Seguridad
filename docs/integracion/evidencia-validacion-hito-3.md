# Evidencia de validación local — 12-oct-2026

[Informe y límites](validacion-hito-3.md). Datos sintéticos; no servidores de Seguridad/Ventas.

## Validadores con tokens firmados

22 casos; cuatro recorridos de validación; 88 resultados esperados. “Acepta”
indica validación del token, no acceso a datos. Solo CLIENTE cumple la política
de roles de /pedidos/me; su implementación y filtrado no se ejecutaron.

| Caso | Esperado | Node | Spring | .NET JsonWebToken | .NET JwtSecurityToken | Cumple CLIENTE |
|---|---|---|---|---|---|---|
| valido_cliente | Acepta | Acepta | Acepta | Acepta | Acepta | Sí |
| valido_multirrol | Acepta | Acepta | Acepta | Acepta | Acepta | Sí |
| valido_sin_roles | Acepta | Acepta | Acepta | Acepta | Acepta | No |
| valido_vendedor | Acepta | Acepta | Acepta | Acepta | Acepta | No |
| vencido | Rechaza | Rechaza | Rechaza | Rechaza | Rechaza | No |
| issuer_incorrecto | Rechaza | Rechaza | Rechaza | Rechaza | Rechaza | No |
| firma_incorrecta | Rechaza | Rechaza | Rechaza | Rechaza | Rechaza | No |
| servicio | Rechaza | Rechaza | Rechaza | Rechaza | Rechaza | No |
| servicio_con_uuid | Rechaza | Rechaza | Rechaza | Rechaza | Rechaza | No |
| refresco | Rechaza | Rechaza | Rechaza | Rechaza | Rechaza | No |
| sub_no_uuid | Rechaza | Rechaza | Rechaza | Rechaza | Rechaza | No |
| sin_exp | Rechaza | Rechaza | Rechaza | Rechaza | Rechaza | No |
| sin_iat | Rechaza | Rechaza | Rechaza | Rechaza | Rechaza | No |
| sin_jti | Rechaza | Rechaza | Rechaza | Rechaza | Rechaza | No |
| sin_roles | Rechaza | Rechaza | Rechaza | Rechaza | Rechaza | No |
| roles_texto | Rechaza | Rechaza | Rechaza | Rechaza | Rechaza | No |
| roles_numericos | Rechaza | Rechaza | Rechaza | Rechaza | Rechaza | No |
| jti_numerico | Rechaza | Rechaza | Rechaza | Rechaza | Rechaza | No |
| jti_vacio | Rechaza | Rechaza | Rechaza | Rechaza | Rechaza | No |
| HS256 | Rechaza | Rechaza | Rechaza | Rechaza | Rechaza | No |
| payload_alterado | Rechaza | Rechaza | Rechaza | Rechaza | Rechaza | No |
| sin_token | Rechaza | Rechaza | Rechaza | Rechaza | Rechaza | No |

Node descargó JWKS una vez. .NET leyó descubrimiento una vez para el
conjunto. Las firmas usan RSA 2048; RS256. La privada solo existe en memoria.

## Mocks HTTP

Prism 5.16.0 con --errors true; 61 respuestas seleccionadas y 7 entradas
inválidas. “Schema” contrasta la respuesta prevista con el contrato; en
entradas inválidas registra rechazo de Prism, sin atribuirlo al backend real.

| Contrato | Petición | Resultado solicitado / ejemplo | HTTP observado | Comprobación |
|---|---|---|---|---|
| v1 | GET /auth/.well-known/openid-configuration | 200 / descubrimiento | 200 | Schema válido |
| v1 | GET /auth/.well-known/jwks.json | 200 / rotacion | 200 | Schema válido |
| v1 | GET /auth/.well-known/jwks.json | 503 / noDisponible | 503 | Schema válido |
| v1 | POST /auth/login | 200 / sinMfa | 200 | Schema válido |
| v1 | POST /auth/login | 200 / conMfa | 200 | Schema válido |
| v1 | POST /auth/login | 401 / credencialesInvalidas | 401 | Schema válido |
| v1 | POST /auth/login | 403 / passwordCaducada | 403 | Schema válido |
| v1 | POST /auth/refresh | 200 / renovada | 200 | Schema válido |
| v1 | POST /auth/refresh | 401 / reusoDetectado | 401 | Schema válido |
| v1 | POST /auth/logout | 204 | 204 | Sin cuerpo |
| v1 | POST /auth/logout | 400 | 400 | Schema válido |
| v1 | GET /auth/me | 200 / propio | 200 | Schema válido |
| v1 | GET /auth/me | 401 | 401 | Schema válido |
| v1 | GET /auth/me | 403 / tokenDeServicio | 403 | Schema válido |
| v1 | POST /auth/otp/solicitar | 202 / enviado | 202 | Schema válido |
| v1 | POST /auth/otp/solicitar | 400 / smsSinCelular | 400 | Schema válido |
| v1 | POST /auth/otp/solicitar | 429 / demasiadasSolicitudes | 429 | Schema válido |
| v1 | POST /auth/otp/solicitar | 401 / desafioInvalido | 401 | Schema válido |
| v1 | POST /auth/otp/verificar | 200 / verificado | 200 | Schema válido |
| v1 | POST /auth/otp/verificar | 401 / desafioInvalido | 401 | Schema válido |
| v1 | POST /auth/otp/verificar | 401 / codigoIncorrecto | 401 | Schema válido |
| v1 | POST /auth/otp/verificar | 401 / intentosAgotados | 401 | Schema válido |
| v1 | POST /auth/otp/verificar | 410 / codigoVencido | 410 | Schema válido |
| v1 | POST /auth/otp/verificar | 403 / passwordCaducada | 403 | Schema válido |
| v1 | POST /auth/token | 200 / despacho | 200 | Schema válido |
| v1 | POST /auth/token | 401 / credencialesInvalidas | 401 | Schema válido |
| v1 | POST /auth/introspeccion | 200 / activo | 200 | Schema válido |
| v1 | POST /auth/introspeccion | 200 / usuarioDesactivado | 200 | Schema válido |
| v1 | POST /auth/introspeccion | 200 / tokenVencido | 200 | Schema válido |
| v1 | POST /auth/introspeccion | 401 | 401 | Schema válido |
| v1 | POST /auth/introspeccion | 403 | 403 | Schema válido |
| v1 | POST /auth/login | Entrada inválida | 422 | Rechazo mock |
| v1 | POST /auth/otp/verificar | Entrada inválida | 422 | Rechazo mock |
| v2 | POST /auth/mfa/cambios | 400 / validacion | 400 | Schema válido |
| v2 | POST /auth/mfa/cambios | 401 / token_invalido | 401 | Schema válido |
| v2 | POST /auth/mfa/cambios | 403 / token_no_aplicable | 403 | Schema válido |
| v2 | POST /auth/mfa/cambios | 422 / mfa_obligatorio | 422 | Schema válido |
| v2 | POST /auth/mfa/cambios | 429 / demasiadas_solicitudes | 429 | Schema válido |
| v2 | POST /auth/mfa/cambios | 503 / no_disponible | 503 | Schema válido |
| v2 | POST /auth/mfa/cambios | 202 / enviado | 202 | Schema válido |
| v2 | POST /auth/mfa/cambios/reenviar | 400 / validacion | 400 | Schema válido |
| v2 | POST /auth/mfa/cambios/reenviar | 401 / token_invalido | 401 | Schema válido |
| v2 | POST /auth/mfa/cambios/reenviar | 403 / token_no_aplicable | 403 | Schema válido |
| v2 | POST /auth/mfa/cambios/reenviar | 422 / mfa_obligatorio | 422 | Schema válido |
| v2 | POST /auth/mfa/cambios/reenviar | 429 / demasiadas_solicitudes | 429 | Schema válido |
| v2 | POST /auth/mfa/cambios/reenviar | 503 / no_disponible | 503 | Schema válido |
| v2 | POST /auth/mfa/cambios/reenviar | 202 / enviado | 202 | Schema válido |
| v2 | POST /auth/mfa/cambios/confirmar | 400 / validacion | 400 | Schema válido |
| v2 | POST /auth/mfa/cambios/confirmar | 401 / token_invalido | 401 | Schema válido |
| v2 | POST /auth/mfa/cambios/confirmar | 401 / codigo_invalido | 401 | Schema válido |
| v2 | POST /auth/mfa/cambios/confirmar | 401 / otp_intentos_agotados | 401 | Schema válido |
| v2 | POST /auth/mfa/cambios/confirmar | 403 / token_no_aplicable | 403 | Schema válido |
| v2 | POST /auth/mfa/cambios/confirmar | 422 / mfa_obligatorio | 422 | Schema válido |
| v2 | POST /auth/mfa/cambios/confirmar | 429 / demasiadas_solicitudes | 429 | Schema válido |
| v2 | POST /auth/mfa/cambios/confirmar | 503 / no_disponible | 503 | Schema válido |
| v2 | POST /auth/mfa/cambios/confirmar | 204 | 204 | Sin cuerpo |
| v2 | POST /auth/mfa/cambios/confirmar | 410 / otp_expirado | 410 | Schema válido |
| v2 | POST /usuarios | 201 | 201 | Schema válido |
| v2 | POST /usuarios | 401 | 401 | Schema válido |
| v2 | POST /usuarios | 403 | 403 | Schema válido |
| v2 | POST /usuarios | 409 / correoNoDisponible | 409 | Schema válido |
| v2 | POST /usuarios | 400 / validacion | 400 | Schema válido |
| v2 | POST /usuarios | 503 / no_disponible | 503 | Schema válido |
| v2 | POST /auth/mfa/cambios | Entrada inválida | 422 | Rechazo mock |
| v2 | POST /auth/mfa/cambios | Entrada inválida | 422 | Rechazo mock |
| v2 | POST /auth/mfa/cambios/confirmar | Entrada inválida | 422 | Rechazo mock |
| v2 | POST /usuarios | Entrada inválida | 400 | Rechazo mock |
| v2 | POST /usuarios | Entrada inválida | 400 | Rechazo mock |

Entradas inválidas: login con email/password sin correo/contrasena; código
OTP no numérico; cambio MFA con challengeToken de login; acción desconocida;
código como número; alta administrativa con CLIENTE o contraseña inicial.

Cuatro respuestas 429 incluyeron Retry-After conforme a su schema. Las
respuestas 204 tuvieron cuerpo vacío. Las rutas de mock no llevan /api/v1 ni
/api/v2, porque Prism usa los paths del contrato.

## Comprobación estructural

OpenAPI 3.1 y referencias: base histórica, v1 local y v2 parcial correctos.
178 ejemplos: 73 + 75 + 30. Quince casos de schema. Las 42 operaciones v1
conservan peticiones/parámetros/security/operationId y sus respuestas anteriores.
No hay un nuevo SHA publicado. Hashes del contenido en el informe.

## Alcance de esta evidencia

Los procesos temporales de JWKS y Prism se cerraron al terminar. No se
crearon cuentas, sesiones ni pedidos reales. No se enviaron comunicaciones ni
se acreditó integración en nube. Publicación y aceptación externa de la revisión
contractual/kit pendientes. El recorrido de Hito 3 sí tiene confirmación
posterior en G5 Backend #2; consultar la ficha de coordinación vigente.
