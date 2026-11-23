# Hito 5 — decisiones del Product Owner

**Fecha:** 10-nov-2026. **Decide:** Sergio Alejandro Osorio Montenegro (PO).
Cierra las tareas PO-T1 y PO-T3 de su [tablero](../../tareas/hito-5/sergio-osorio/TAREAS.md)
y las decisiones pendientes del [plan H5 §8](README.md#8-decisiones-pendientes).

## 1. Plan H5

El plan H5 v1.0 queda **aprobado** con el reparto y el calendario propuestos.

Dos reglas del plan no se cumplieron en los primeros PR del 10-nov y se
mantienen sin excepciones desde ahora:

- **El contrato lo edita solo el PO.** El contrato v1 1.2.0 (FEAT-16) lo publicó
  David en el PR #87. Se revisó y se ratifica dentro del lote de contrato; no
  hace falta rehacerlo.
- **Ningún PR lo fusiona su autor.** Aplica también a los PR de documentación.

## 2. Lote de contrato H5 (PO-T1)

Todo es aditivo. Nada de lo que ya consumen los otros módulos cambia.

| Cambio | Dónde | Decisión |
|---|---|---|
| `formatoAcceso` opcional y `usuario.permisos` (FEAT-16) | OpenAPI v1 1.2.0, [ADR-009](../../docs/arquitectura/adr/009-acceso-cifrado-opcional.md) | Ratificado |
| Gestión de clientes técnicos (FEAT-14) | OpenAPI v2 2.1.0, [SPEC-19](../../specs/funcionales/SPEC-19-clientes-tecnicos.md) | Publicado. El contrato sigue a la implementación ya fusionada |
| Permiso `integracion.gestionar` | [SPEC-11](../../specs/funcionales/SPEC-11-roles-permisos.md) | Aprobado, solo para `ADMIN_SISTEMA` |
| Seis acciones auditables y el código `CLIENTE_SERVICIO_EXISTENTE` | [SPEC-12](../../specs/funcionales/SPEC-12-registro-auditoria.md), [catálogo de errores](../../specs/catalogo-errores.md) | Aprobados |

Una diferencia con la propuesta: un token de servicio que llama a las rutas de
clientes técnicos recibe `403 TOKEN_NO_APLICABLE`, no `SCOPE_INSUFICIENTE`. Así
lo hace el backend y así responden las demás rutas de persona. SPEC-19 se
corrigió.

## 3. Alcance de SPEC-03 para FEAT-13 (issues #36 y #37)

El objetivo de FEAT-13 es que el profesor vea a un administrador crear a un
gestor y a ese gestor iniciar sesión. Para eso hacen falta el alta y la
activación; lo demás de SPEC-03 no.

| Requisito | En H5 | Motivo |
|---|---|---|
| RF-03.1 Alta administrativa (`POST /api/v2/usuarios`), con rol o sin rol | **Sí** | Es la observación del profesor |
| RF-03.5 Enlace de activación y `POST /api/v1/auth/activar-cuenta` | **Sí** | Sin activación, la cuenta creada no puede iniciar sesión |
| RF-03.3 Política de contraseña al activar | **Sí** | Reutiliza SPEC-07 |
| RF-03.4 Auditoría del alta y de la activación | **Sí** | `USUARIO_CREADO` y `CUENTA_ACTIVADA` |
| FRONT-11 sin contraseña inicial y con opción «sin rol»; pantalla de activación | **Sí** | Issue #36 |
| RF-03.2 Listado y consulta | Ya está | Implementado en H4 |
| RF-03.6 Registro asistido en tienda | No | Depende de Retail y no está en la rúbrica |
| RF-03.7 y RF-03.8 Búsqueda por documento y HMAC | No | Igual que el anterior |
| RF-03.9 Eliminación a los 30 días de cuentas sin activar | No | Ver el límite que sigue |

**Límite que se declara:** sin RF-03.9, una cuenta creada y nunca activada no
se elimina sola. En H5 el alta administrativa solo guarda correo, nombres y
apellidos, y las cuentas son de prueba. La tarea de eliminación queda como
deuda registrada, no como algo resuelto.

El issue #36 se cierra con FEAT-13-T2. El issue #37 queda abierto con los RF
que no entran.

## 4. Decisiones abiertas (PO-T3)

### Términos de servicio (H-12)

- Versión única **`2026-11-v1`**, con el texto de [términos de uso](../../docs/legal/terminos-2026-11-v1.md).
- El registro y la activación exigen `aceptaTerminos: true`, como ya dice el
  contrato, y guardan la versión aceptada y la fecha.
- La SPA muestra el texto en una página propia enlazada desde ambos formularios.
- Las cuentas de prueba sembradas no llevan aceptación inventada: quedan con la
  versión vacía.

### Contacto de soporte (H-14)

- No se publica un correo fijo en el código ni en las specs.
- El canal es el **buzón remitente de la plataforma**, el que ya envía el aviso
  de bloqueo (`G7_CORREO_REMITENTE`). El texto de la pantalla y del correo es:
  «Para revisar tu cuenta, responde al correo de aviso o pide al administrador
  de la plataforma que la revise.»
- Queda una comprobación para David, que opera Render: que ese buzón recibe
  respuestas y que alguien lo lee. Si no las recibe, el texto se queda solo con
  la segunda parte.
- El login sigue genérico: no revela que la cuenta está bloqueada.

### G3 y G4 en la compra por canal (FEAT-20)

- **Marketplace (G1) es el canal obligatorio.** Es el único con integración
  desplegada.
- Retail (G4) y Chatbot (G3) entran si confirman hasta el **vie 13-nov** una URL
  desplegada y una fecha de prueba. Los tres canales ya tienen concedido
  `ventas:pedidos:crear`.
- El canal que no confirme se registra como «no probado», con su motivo. No
  bloquea el cierre de FEAT-20.

### Consumidor de eventos si G2 no llega (FEAT-18)

- G2 tiene hasta el **vie 13-nov** para confirmar que consume `usuario.bloqueado`
  y `usuario.desactivado` (issue #80, sección 5).
- Si no confirma, el lun 16-nov se ofrece la misma suscripción a G5 Ventas, que
  ya valida el estado de la cuenta antes de anular o reembolsar.
- Si ningún módulo llega, Christian despliega un consumidor propio y separado
  del backend, que solo registra los eventos recibidos. Se presenta como lo que
  es: un consumidor de demostración de G7, no una integración con otro módulo.

### Persistencia de la sesión al recargar

Sin cambios en H5. Los tokens siguen en memoria y recargar la página exige
iniciar sesión otra vez (FRONT-00, H-11). No está en la rúbrica y FEAT-16 ya
tocó el almacenamiento de sesión; cambiarlo ahora arriesga todas las pantallas.

## 5. Cliente técnico de prueba (OPS-T2)

Aprobado. David lo crea desde el módulo de configuración, no con SQL:

- `clientId`: `modulo-prueba-h5`.
- Scopes: `usuarios:leer` y `direcciones:leer`.
- Uso: FEAT-06-T4, FEAT-17 y la demostración de FEAT-14 y FEAT-19.
- Se desactiva al cerrar H5.

## 6. Plataforma de API Management (FEAT-19-T1)

Azure API Management en plan Consumption. Motivo, alternativas y lo que falta
comprobar: [ADR-010](../../docs/arquitectura/adr/010-plataforma-api-management.md).

## 7. Matriz de scopes (PO-T2)

Dos concesiones de `modulo-retail` no tienen respaldo (`ventas:pedidos:consultar`
y `ventas:pedidos:estado`). Se pide confirmación a G5 y G4; si no llega antes
del congelamiento del vie 20-nov, se revocan desde el módulo de configuración.
El detalle está en la [matriz](../../docs/integracion/matriz-scopes.md).
