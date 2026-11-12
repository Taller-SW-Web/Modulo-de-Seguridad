# H5 — tareas de Luis David Morales Brenis

**Épicas:** integración entre módulos, identidad técnica (por delegación de Sergio), EP-04 Segundo factor y EP-07 Bloqueo.
**Plan:** [H5 §3](../../../plan/hito-5/README.md).
**Ramas:** `feat/TEC-IDENTIDAD-token-cifrado` y `feat/TEC-IDENTIDAD-clientes-tecnicos` (backend y frontend); ramas `docs/…` para evidencia.
Cada tarea es un commit `FEAT-xx-Tn: …` en su rama. Los PR de David los revisa Sergio.

David asume lo más difícil y lo que más bloquea al resto. **Orden:** OPS primero (10 y 11-nov), después FEAT-16, después FEAT-14.

**Fuentes comunes:** [verificación de clientes técnicos H5](../../../docs/integracion/hito-5-clientes-tecnicos.md),
[matriz de scopes](../../../docs/integracion/matriz-scopes.md),
[SPEC-17](../../../specs/funcionales/SPEC-17-tokens-servicio.md),
[SPEC-05](../../../specs/funcionales/SPEC-05-inicio-sesion.md),
[SPEC-11](../../../specs/funcionales/SPEC-11-roles-permisos.md),
[ADR-002](../../../docs/arquitectura/adr/002-validacion-local-frente-a-introspeccion.md),
[kit de integración](../../../specs/kit-integracion.md),
[cierre H4 de FEAT-11](../../../docs/evidencia/h4/FEAT-11.md).

| ID | Resultado | Prioridad | Depende de | Estado |
|---|---|---|---|---|
| OPS-T1 | Consultas de solo lectura en Supabase | P1 · mar 10-nov | — | Pendiente |
| OPS-T2 | Cuentas de prueba y cliente técnico de prueba | P1 · mié 11-nov | OPS-T1; aprobación de Sergio para el cliente nuevo | Pendiente |
| OPS-T3 | Ajustes de repositorio y Render | P1 · mié 11-nov | — | Pendiente |
| FEAT-16-T1 | Decisión y propuesta de contrato del token cifrado | P1 · mié 11-nov | — | Hecho |
| FEAT-16-T2 | Backend emite el formato cifrado y acepta ambos | P1 | T1 como propuesta | Hecho |
| FEAT-16-T3 | SPA sin leer el contenido del token | P1 | T2 | Hecho |
| FEAT-16-T4 | Evidencia desplegada y kit actualizado | P1 | T2, T3 | Hecho ([evidencia](../../../docs/evidencia/h5/FEAT-16.md)) |
| FEAT-14-T1 | Spec, spec de pantalla y propuesta de contrato | P1 · mié 11-nov | — | Hecho |
| FEAT-14-T2 | Backend de gestión de clientes técnicos | P1 | T1 como propuesta | Hecho |
| FEAT-14-T3 | Pantalla «Configuración → Clientes técnicos» | P1 | T2 | Hecho |
| FEAT-14-T4 | Recorrido desplegado y evidencia | P1 | T2, T3 | Hecho ([evidencia](../../../docs/evidencia/h5/FEAT-14.md)) |
| FEAT-20-T1 | Confirmar con G5 la prueba conjunta | P1 | G5 (externo) | En curso |
| FEAT-20-T2 | Compra desde Marketplace (G1) | P1 | T1; G1 | Pendiente |
| FEAT-20-T3 | Compra desde Retail (G4) | P1 | T1; G4 | Pendiente |
| FEAT-20-T4 | Compra desde Chatbot (G3) | P1 | T1; G3 | Pendiente |
| FEAT-11-T6 | Favoritos A/B en despliegue | P1 | G1 (externo) | Pendiente |
| FEAT-11-T7 | Llamada real de `modulo-marketplace` a G6 | P1 | G1 y G6 (externo) | Pendiente |
| FEAT-07-T5 | Casos negativos de MFA en despliegue | P2 | — | Hecho (12-nov): [evidencia](../../../docs/evidencia/h5/FEAT-07.md) |
| FEAT-08-T3 | Revocación en nube y contacto de soporte | P2 | Sergio (H-14) | Pendiente |

## OPS — desbloqueos (P1, 10 y 11-nov)

### OPS-T1 — Consultas de solo lectura
- **Resultado:** ejecutar las consultas Q0 a Q6 del documento de verificación y compartir solo cliente, scopes, audiencias y conteos.
- **Aceptación:** resultado registrado en el documento, sin secretos ni hashes. No se afirma que un grant falta o existe sin esta consulta.

### OPS-T2 — Cuentas y cliente de prueba
- **Resultado:** cuentas dedicadas para el E2E contra Render y para la carga; un cliente técnico de prueba si Sergio lo aprueba.
- **Primer paso:** pedir a Jose Luis y a Valery la lista exacta de cuentas que necesitan.
- **Aceptación:** cuentas entregadas por canal privado; las cuentas de carga no son las de la demo, porque el bloqueo automático puede bloquearlas.

### OPS-T3 — Ajustes de repositorio y Render
- **Resultado:** enlace de despliegue del sitio estático en Render y su secreto en el repositorio frontend; variable `E2E_RENDER_HABILITADO` en el backend; análisis de código habilitado en ambos repositorios.
- **Aceptación:** Christian, Jose Luis y Juan José confirman que ya pueden avanzar. Si David no administra algún repositorio, se anota quién lo hace.

También en estos dos días: mensajes a G5, G1, G2 (issue #80) y G6 (issue #78), y cierre del PR de documentación #79, superado por el cierre H4.

## FEAT-16 — Token de acceso con formato cifrado (P1)

**Situación:** el token de acceso lleva en claro `email`, `roles` y `permisos`. La SPA lee `permisos` del token. Los otros módulos lo validan con JWKS y leen `roles`.

**Decisión (10-nov, David por delegación del equipo):** [ADR-009](../../../docs/arquitectura/adr/009-acceso-cifrado-opcional.md). El token se sigue firmando igual y, cuando el cliente pide `formatoAcceso: CIFRADO`, se envuelve en JWE (`dir`/`A256GCM`). Sin esa petición, la respuesta es idéntica a la actual. Contrato v1 1.2.0, aditivo.

### FEAT-16-T1 — Decisión y propuesta
- **Resultado:** ADR nuevo y propuesta de contrato en `specs/contrato-api/propuestas/`: opción de formato en ingreso, verificación de OTP y renovación; `permisos` en el objeto `usuario` de la respuesta.
- **Aceptación:** el valor por defecto conserva el comportamiento actual; propuesta entregada a Sergio el mié 11-nov.

### FEAT-16-T2 — Backend
- **Resultado:** cifrado posterior a la firma con una clave tomada de una variable de entorno; la API acepta el formato actual y el cifrado.
- **Fuentes:** `identidad/adapter/out/crypto/NimbusFirmadorJws.java` del repo backend.
- **Aceptación:** pruebas en CI: emisión por defecto sin cambios; el token cifrado no deja leer sus claims; ambos formatos autorizan igual; un token cifrado alterado se rechaza.

### FEAT-16-T3 — SPA
- **Resultado:** la SPA pide el formato cifrado y toma los permisos de la respuesta, no del token.
- **Fuentes:** `src/shared/jwt.ts` y `src/shared/almacenSesion.ts` del repo frontend.
- **Aceptación:** guards y pruebas en verde. Se fusiona en S1, porque toca archivos compartidos por todas las pantallas.

### FEAT-16-T4 — Evidencia
- **Resultado:** recorrido desplegado y kit de integración actualizado.
- **Aceptación:** captura de la pestaña de red donde el token no muestra roles; comprobación de que el formato actual sigue validando.
- **Límite que se declara:** la respuesta de sesión sigue indicando al propio usuario sus roles y permisos, y los flujos de otros módulos conservan el formato actual hasta que migren.
- **Evidencia:** `docs/evidencia/h5/FEAT-16.md`.

## FEAT-14 — Módulo de configuración: clientes técnicos (P1)

**Situación:** un cliente técnico se crea hoy con SQL, siguiendo el runbook del repo backend. SPEC-17 deja el aprovisionamiento fuera de alcance.

### FEAT-14-T1 — Spec y propuesta
- **Resultado:** spec funcional nueva con las siete secciones de la plantilla, spec de pantalla y propuesta de contrato. Incluye el permiso nuevo para `ADMIN_SISTEMA`.
- **Aceptación:** propuesta entregada a Sergio el mié 11-nov.

### FEAT-14-T2 — Backend
- **Resultado:** listar clientes; crear (el secreto se muestra una sola vez y solo se guarda su hash); activar y desactivar; rotar el secreto; conceder y revocar scopes del catálogo. Cada acción se audita.
- **Aceptación:** pruebas PostgreSQL en CI; sin el permiso responde 403; ninguna lectura devuelve secreto ni hash; solo se conceden scopes que estén en el catálogo.

### FEAT-14-T3 — Pantalla
- **Resultado:** sección «Configuración → Clientes técnicos» en la SPA, visible solo con el permiso.
- **Aceptación:** pruebas y verificación de tipos en CI.

### FEAT-14-T4 — Recorrido desplegado
- **Resultado:** crear un cliente de prueba, concederle un scope, emitir un token 200, revocar el scope y comprobar que la siguiente emisión ya no lo incluye.
- **Evidencia:** `docs/evidencia/h5/FEAT-14.md`, con versión desplegada y sin secretos.

## FEAT-20 — Compra por canal con una misma cuenta (P1)

**Objetivo:** una cuenta de G7 compra desde Marketplace, Retail y Chatbot y ve sus pedidos en Ventas. Una cuenta común no es inicio de sesión único.

### FEAT-20-T1 — Confirmar la prueba conjunta con G5
- **Situación:** el mecanismo ya está definido por G5 (`G5-Ventas-Postventas`, `specs/api/api-ventas.md` y `integraciones/ACUERDO_G7_SEGURIDAD.md`): el canal crea el pedido con su token de servicio (`ventas:pedidos:crear`) y `contacto.clienteId` = `sub` de G7; el cliente lo ve en `GET /pedidos/me` con su propio token. `ventas:pedidos:crear` está concedido a los tres canales (10-nov).
- **Pendiente de G5:** URL, SHA, issuer y JWKS desplegados; fecha de prueba. Se les sugieren tres mejoras: validar que `clienteId` exista, atar `canal` al `client_id` y registrar ese `client_id` (su RNF-03).
- **Aceptación:** fecha y versiones confirmadas por G5.

### FEAT-20-T2 a T4 — Un canal por tarea
- **Resultado por canal:** pedido creado desde el canal y visible en Ventas para el mismo `sub`; otra cuenta no lo ve.
- **Aceptación:** cada canal se cierra por separado. Un canal que no llegue se registra como «no probado».
- **Evidencia:** `docs/evidencia/h5/FEAT-20.md`, una sección por canal, con versiones de ambos lados y nivel de evidencia.

## FEAT-11 — Pendientes con G1 (P1)

### FEAT-11-T6 — Favoritos A/B en despliegue
- **Depende de:** que G1 despliegue su integración con G6.
- **Aceptación:** A accede a lo suyo; B no lee, modifica ni borra lo de A.

### FEAT-11-T7 — Llamada real a G6
- **Aceptación:** `modulo-marketplace` obtiene 200 con `catalogo:leer` y 403 fuera de scope, con fecha y versiones.
- **Evidencia de ambas:** `docs/evidencia/h5/FEAT-11.md`.

## FEAT-07 y FEAT-08 — Cierre (P2)

### FEAT-07-T5 — Casos negativos de MFA
- **Aceptación:** código vencido, usado y erróneo devuelven el error del catálogo en el despliegue.

### FEAT-08-T3 — Revocación en nube
- **Aceptación:** tras el bloqueo, la renovación de una sesión abierta se rechaza en el despliegue; versión desplegada anotada; contacto de soporte definido por Sergio.
