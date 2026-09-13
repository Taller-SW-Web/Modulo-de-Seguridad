# Matriz de trazabilidad

| Campo | Valor |
|---|---|
| **Dueño** | Product Owner |
| **Actualización** | En cada aprobación de spec y en cada cambio de contrato |
| **Para qué** | Que ninguna pantalla, endpoint o evento exista sin spec que lo origine, y ninguna spec sin forma de verificarla |

Este documento nació de una omisión: la auditoría llevaba seis menciones en
cuatro specs, un requisito de SPEC-09 que dependía de ella y una pantalla en los
wireframes, y aun así no tenía spec. Nadie lo vio porque no había ninguna tabla
donde el hueco se hiciera visible. Esta es esa tabla.

---

## 1. Índice de las nueve specs

| SPEC | Funcionalidad | Responsable | Archivo | Estado |
|---|---|---|---|---|
| 01 | Registro y gestión de usuarios | Eva Lucía | `SPEC-01-registro.md` | ⬜ Por redactar |
| 02 | Autenticación usuario/contraseña | Jose Luis | `SPEC-02-autenticacion.md` | ⬜ Por redactar |
| 03 | Gestión de credenciales y contraseñas | Juan José | `SPEC-03-credenciales.md` | ⬜ Por redactar |
| 04 | Autenticación por OTP y MFA | Luis David | `SPEC-04-otp-mfa.md` | ⬜ Por redactar |
| 05 | Gestión de roles y permisos | Eva Lucía | `SPEC-05-roles-permisos.md` | ⬜ Por redactar |
| 06 | Auditoría y trazabilidad | Christian | [`SPEC-06-auditoria.md`](SPEC-06-auditoria.md) | 🔶 Borrador |
| 07 | Bloqueo y desbloqueo de cuentas | Luis David | `SPEC-07-bloqueo-cuentas.md` | ⬜ Por redactar |
| 08 | Gestión de atributos de usuarios | Eva Lucía | `SPEC-08-atributos.md` | ⬜ Por redactar |
| 09 | API de identidad para los demás módulos | Sergio | [`SPEC-09-api-identidad.md`](SPEC-09-api-identidad.md) | 🔶 Borrador · contrato publicado |

> **Cambio de numeración del 13 de septiembre.** La política de contraseñas dejó
> de ser spec propia y se fusionó dentro de SPEC-03 por indicación del profesor;
> la auditoría ocupó el número 06 que quedó libre. **La antigua SPEC-06,
> «Recuperación y cambio de contraseña», ahora es parte de SPEC-03.** Si ves esa
> referencia en un documento viejo, es esto. SPEC-09 no cambió.

---

## 2. Specs ↔ endpoints del contrato

✅ publicado en `openapi.yaml` · ⬜ falta añadirlo antes del jueves 17

| Endpoint | SPEC | Estado |
|---|---|---|
| `GET /auth/.well-known/openid-configuration` | 09 | ✅ |
| `GET /auth/.well-known/jwks.json` | 09 | ✅ |
| `POST /auth/token` | 09 | ✅ |
| `POST /auth/introspeccion` | 09 | ✅ |
| `GET /usuarios/{id}` | 09 | ✅ |
| `POST /usuarios/lote` | 09 | ✅ |
| `GET /usuarios/{id}/direcciones` | 08, 09 | ✅ |
| `GET /roles` | 05, 09 | ✅ |
| `GET /permisos` | 05, 09 | ✅ |
| `POST /auth/registro` | 01, 03 | ✅ |
| `POST /auth/login` | 02 | ✅ |
| `POST /auth/refresh` | 02 | ✅ |
| `POST /auth/logout` | 02 | ✅ |
| `GET /auth/me` | 01 | ✅ |
| `POST /auth/otp/solicitar` | 04 | ✅ |
| `POST /auth/otp/verificar` | 04 | ✅ |
| `POST /auth/verificar-correo` | 01 | ⬜ |
| `POST /usuarios` | 01 | ⬜ |
| `DELETE /usuarios/{id}` | 01 | ⬜ |
| `GET /password/politica` | 03 | ⬜ |
| `POST /password/recuperar` | 03 | ⬜ |
| `POST /password/restablecer` | 03 | ⬜ |
| `POST /password/cambiar` | 03 | ⬜ |
| `POST /auth/otp/habilitar` | 04 | ⬜ |
| `POST /auth/otp/deshabilitar` | 04 | ⬜ |
| `POST /usuarios/{id}/roles` | 05 | ⬜ |
| `DELETE /usuarios/{id}/roles/{rol}` | 05 | ⬜ |
| `GET /auditoria` | 06 | ⬜ |
| `GET /auditoria/exportar` | 06 | ⬜ |
| `GET /auth/me/actividad` | 06 | ⬜ |
| `POST /usuarios/{id}/bloquear` | 07 | ⬜ |
| `POST /usuarios/{id}/desbloquear` | 07 | ⬜ |
| `PATCH /usuarios/{id}/atributos` | 08 | ⬜ |
| `POST /usuarios/{id}/direcciones` | 08 | ⬜ |

**Ninguna spec puede quedarse sin endpoint.** Si al redactar la tuya no
encuentras aquí ninguno que la haga observable desde fuera, díselo al PO: o
falta un endpoint, o la spec no es una funcionalidad.

---

## 3. Specs ↔ pantallas

Las ocho del Hito 1, según `docs/plan-hito-1.md` §5.

| # | Pantalla | SPEC | Interfaz |
|---|---|---|---|
| 1 | Inicio de sesión | 02, 07 | Valery |
| 2 | Registro de cliente, con medidor de fuerza | 01, 03 | Valery |
| 3 | Verificación de correo | 01 | Valery |
| 4 | Desafío de código OTP | 04 | Luis David |
| 5 | Recuperar contraseña | 03 | Juan José / Valery |
| 6 | Mi cuenta — perfil y direcciones | 08 | Christian |
| 7 | Panel admin — listado de usuarios | 01, 05, 07 | Christian |
| 8 | Panel admin — detalle: roles, bloqueos, historial | 05, 06, 07 | Christian |

SPEC-09 no tiene pantalla y es correcto: la consumen máquinas. Su equivalente a
una demo es el `curl` al JWKS.

---

## 4. Specs ↔ eventos publicados

Detalle en [`catalogo-eventos.md`](catalogo-eventos.md).

| Evento | Publica | Consumen |
|---|---|---|
| `usuario.creado` | 01 | Los seis módulos |
| `usuario.desactivado` | 01 | Los seis módulos |
| `usuario.bloqueado` | 07 | Los seis módulos |
| `usuario.desbloqueado` | 07 | Los seis módulos |
| `usuario.roles_cambiados` | 05 | Los seis módulos |
| `usuario.atributos_actualizados` | 08 | Marketplace, Retail, Despacho |

SPEC-02, SPEC-03, SPEC-04 y SPEC-06 no publican eventos. Es intencionado: un
inicio de sesión o un cambio de contraseña no cambia nada que otro módulo
necesite saber.

---

## 5. Specs ↔ tablas de datos

Para que Eva pueda cerrar `docs/arquitectura/modelo-datos.md` sin perseguir a
nadie.

| Tabla | Dueña | Comparten |
|---|---|---|
| `usuario` | 01 | 02, 03, 07, 08 |
| `credencial` · `password_historial` | 03 | 01, 02 |
| `token_refresco` | 02 | 01, 03, 05 |
| `otp` · `desafio_mfa` | 04 | 02 |
| `rol` · `permiso` · `usuario_rol` | 05 | 01, 09 |
| `auditoria_seguridad` | **06** | Las nueve |
| `intento_login` · `bloqueo` | 07 | 02 |
| `perfil_cliente` · `perfil_vendedor` · `direccion` | 08 | 01, 09 |
| `cliente_servicio` · `cliente_servicio_scope` | 09 | — |

---

## 6. Specs ↔ verificación

Los escenarios *Dado / Cuando / Entonces* de cada spec **son** el plan de
pruebas del Hito 5. La regla de la plantilla: mínimo dos por requisito clave,
uno de ellos caso borde.

| SPEC | Requisitos | Escenarios | Cumple la regla |
|---|---|---|---|
| 06 | 12 | 13 | ✅ |
| 09 | 16 | 16 | ✅ |
| 01 a 05, 07, 08 | — | — | ⬜ Al redactarlas |

---

## 7. Huecos abiertos

Lo que hoy sabemos que falta. Se cierra o se convierte en decisión escrita.

| Hueco | Quién | Para cuándo |
|---|---|---|
| 18 endpoints sin publicar en `openapi.yaml` | Sergio | Jue 17 — congelamiento |
| `permisos` viaja vacío hasta que SPEC-05 defina el catálogo | Eva Lucía | Hito 4 |
| El permiso `auditoria:leer` no existe aún en el catálogo de SPEC-05 | Eva Lucía / Christian | Hito 3 |
| SPEC-02 y SPEC-07 en PDF dicen `423 Locked`; el contrato dice `403 CUENTA_NO_DISPONIBLE` | Jose Luis / Luis David | Al redactar — ver [`catalogo-errores.md`](catalogo-errores.md) |
| Los `client_id` y `client_secret` de los seis módulos no están creados | Christian | Hito 4 |
| El catálogo de eventos no está acordado con los seis equipos | Sergio | Vie 18 |
