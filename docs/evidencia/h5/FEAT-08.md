# FEAT-08 — Revocación de la sesión al bloquear, en el despliegue

Responsable: Luis David Morales Brenis. Corte: 12-nov-2026, America/Lima.
Estado: **FEAT-08-T3: revocación comprobada en el despliegue. Falta confirmar
el canal de soporte (H-14).**

Continúa la [evidencia H4](../h4/FEAT-08.md). Ahí quedó un límite: la revocación
de las sesiones al bloquear solo se había probado en PostgreSQL local, y recargar
la SPA con F5 no la demostraba.

## Fuentes y versiones

| Componente | Referencia |
|---|---|
| Especificación | [SPEC-15](../../../specs/funcionales/SPEC-15-bloqueo-manual.md) RF-15.4 y RF-15.7, y [SPEC-06](../../../specs/funcionales/SPEC-06-renovacion-cierre-sesion.md) |
| Backend desplegado | `44f7d2953b4e8727c4f566a3be849ab25171c66a` en `develop` (merge del backend [#90](https://github.com/Taller-SW-Web/Modulo-de-Seguridad-Backend/pull/90)), desplegado por el [run 43109130867](https://github.com/Taller-SW-Web/Modulo-de-Seguridad-Backend/actions/runs/43109130867) |
| Frontend desplegado | `6e27a78db9832081a0b640daaa12bffeeb667611` (frontend [#24](https://github.com/Taller-SW-Web/G7-Modulo-de-Seguridad-Frontend/pull/24)). Los commits posteriores de `develop` solo cambian `e2e/` |
| URL | https://g7-auth-service.onrender.com/api/v1 · https://g7-seguridad-spa.onrender.com |
| Guion | [`guiones/FEAT-08-revocacion-bloqueo.ps1`](guiones/FEAT-08-revocacion-bloqueo.ps1) |

## Ejecución

**Nivel de evidencia:** propia desplegada. **Fecha:** 12-nov-2026, de 15:28:57 a
15:30:26, America/Lima. **Ejecutó:** David.

- **Cuenta probada:** una `CLIENTE` desechable sin segundo factor, dedicada a esta
  prueba. No es la del E2E ni la de la demo.
- **Bloqueo y desbloqueo:** David los hizo desde la SPA con una cuenta
  `ADMIN_SISTEMA`, con el motivo «Prueba FEAT-08-T3», mientras el guion esperaba.
- **Qué se muestra:** el guion lee la contraseña con `Read-Host -AsSecureString` y
  solo imprime estados HTTP y códigos de error. No hay contraseñas ni tokens en
  esta evidencia.

| Hora | Paso | Esperado | Obtenido |
|---|---|---|---|
| 15:28:58 | Login de la cuenta | `200` | `200` |
| 15:28:58 | Renovar la sesión antes del bloqueo | `200` | `200` |
| 15:28:59 | `GET /auth/me` antes del bloqueo | `200` | `200` |
| — | **Bloqueo manual desde la SPA** | — | — |
| 15:29:39 | **Renovar la sesión abierta antes del bloqueo** | `401 REFRESCO_INVALIDO` | `401 REFRESCO_INVALIDO` |
| 15:29:39 | `GET /auth/me` con el token de acceso emitido antes del bloqueo | informativo | `200` |
| 15:29:40 | Login durante el bloqueo, con la contraseña correcta | `401 CREDENCIALES_INVALIDAS` | `401 CREDENCIALES_INVALIDAS` |
| — | **Desbloqueo manual desde la SPA** | — | — |
| 15:30:24 | Renovar con el refresco revocado, ya desbloqueada | `401 REFRESCO_INVALIDO` | `401 REFRESCO_INVALIDO` |
| 15:30:26 | Login después del desbloqueo | `200` | `200` |
| 15:30:26 | Cerrar la sesión nueva | `204` | `204` |

Llegaron a la cuenta los dos avisos de RF-15.7: el de bloqueo a las 15:29 y el de
desbloqueo a las 15:30. Ninguno muestra el motivo del bloqueo.

**Resultado:** cumple la parte técnica de FEAT-08-T3. El bloqueo manual revoca
la sesión que estaba abierta: el siguiente refresco se rechaza, y desbloquear
no la restituye. La cuenta quedó activa y sin sesiones abiertas.

### El token de acceso hasta que vence

El token de acceso emitido antes del bloqueo siguió autorizando `GET /auth/me`
(`200`). Es lo esperado: el token de acceso no se revoca, dura 15 minutos y es
lo que tarda un cambio de estado en llegar a quien valida en local
([SPEC-06](../../../specs/funcionales/SPEC-06-renovacion-cierre-sesion.md),
[ADR-002](../../arquitectura/adr/002-validacion-local-frente-a-introspeccion.md)).
El bloqueo corta la sesión en la siguiente renovación, como mucho 15 minutos después.

## Contacto de soporte (H-14)

Sergio lo decidió en [decisiones del PO](../../../plan/hito-5/DECISIONES-PO.md),
«Contacto de soporte»:
- el canal es el buzón remitente de la plataforma;
- si ese buzón no recibe respuestas, el texto se queda solo con «pide al
  administrador de la plataforma que la revise».

El aviso de bloqueo y la pantalla de desbloqueo ya usan esa segunda forma.

- Correo, en `OperacionManualAdapter` del backend: «Solicita al administrador de
  la plataforma que revise tu cuenta.»
- Pantalla, en `DesbloqueoPage` del frontend: «Si no puedes entrar, solicita al
  administrador de la plataforma que revise tu cuenta.»

**Pendiente:** David comprueba si el buzón remitente recibe las respuestas.

## Límites

- Solo se probó el bloqueo manual. El bloqueo automático no cierra las sesiones
  (RF-15.4), así que no aplica esta prueba.
- No se probaron en el despliegue el autobloqueo, el bloqueo del último
  administrador, el motivo vacío ni la concurrencia. Siguen cubiertos por las
  pruebas automatizadas de la [evidencia H4](../h4/FEAT-08.md).
- La entrega de los eventos `usuario.bloqueado` y `usuario.desbloqueado` al broker
  no se inspeccionó.
