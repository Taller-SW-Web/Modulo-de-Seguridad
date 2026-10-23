# FEAT-08 — Bloqueo y desbloqueo manual

Responsable: Luis David Morales Brenis. Corte: 23-oct-2026, America/Lima.
Estado: **T1/T2 implementadas y fusionadas; recorrido principal desplegado comprobado**.
No se declara toda la FEAT ni todos sus RF Verificados: los límites de aceptación
se enumeran al final.

## Fuentes y versiones

SPEC-15, FRONT-08 (acciones de FRONT-07), OpenAPI v1 y catálogos de errores/eventos.
Evidencia documental preparada desde main
`a0b4bb83250da7d33a3d1d530e76cf8e0603898b`, que incluye el cierre documental #66
de FEAT-07. Se reutiliza la consulta de bloqueo entregada en backend #55.

| Componente | PR fusionado | Commit de merge seleccionado para despliegue | URL |
|---|---|---|---|
| Backend | [#65](https://github.com/Taller-SW-Web/Modulo-de-Seguridad-Backend/pull/65) | `16ad6dd4ad75c301b68c6419fd51fc8f274d1aef` | https://g7-auth-service.onrender.com |
| Frontend | [#14](https://github.com/Taller-SW-Web/G7-Modulo-de-Seguridad-Frontend/pull/14) | `2de682b0926c9329c810166c53f7eb3877f92be2` | https://g7-seguridad-spa.onrender.com |

David confirmó ambos despliegues antes de ejecutar el recorrido. Los SHAs anteriores
son los merges indicados para desplegar; no se adjuntó una captura de Render que
confirme independientemente su versión efectiva. La aplicación desplegada sí fue
ejercitada en las URLs indicadas. No se cambiaron OpenAPI, migraciones ni configuración
remota desde esta sesión.

## Implementación y pruebas automatizadas

- T1: endpoints v1 de bloqueo/desbloqueo por ADMIN_SISTEMA con permiso actual
  `cuenta.bloquear`; motivo obligatorio hasta 500 caracteres, protección de cuenta
  propia/último administrador, revocación de sesiones, auditoría crítica, eventos y
  correo mediante el outbox existente. Efectos en una transacción, locks ordenados
  y sustitución de bloqueo automático por manual sin vencimiento.
- T2: acciones en el detalle administrativo, validación y confirmación, recarga del
  estado, errores publicados, bloqueo de doble envío y consulta ante resultado de
  red incierto. No repite automáticamente la mutación tras TOKEN_INVALIDO.
- Backend: **859 pruebas, cero fallos y cero omitidas**, bootJar correcto. Incluye
  15 pruebas PostgreSQL de composición de protección (cinco ejecuciones nuevas de
  FEAT-08: revocación/desbloqueo, sustitución, rollback de auditoría/outbox y concurrencia).
  Se corrigió únicamente el fixture de CambioContrasenaPostgresTest para impedir
  una fecha de revocación anterior a la creación por desfase de relojes.
  [CI verde sobre 6f5a343](https://github.com/Taller-SW-Web/Modulo-de-Seguridad-Backend/actions/runs/40014928427).
  Sergio aprobó ese commit antes del merge.
- Frontend `4ee704b`: auditoría local, **87/87 pruebas en 15 archivos**, typecheck,
  lint y build correctos. Siete pruebas de pantalla de bloqueo y una de API nuevas.
  [CI verde](https://github.com/Taller-SW-Web/G7-Modulo-de-Seguridad-Frontend/actions/runs/40004529095).
  Advertencia no bloqueante de Vite por chunk JS de 544,12 kB. Las pruebas UI usan
  dobles; no sustituyen el recorrido desplegado siguiente.

## Recorrido desplegado

Ejecutor: David, 23-oct-2026, hora de Lima. Cuenta propia C,
UUID `00000000-0000-4000-8000-00000000f00f`, CLIENTE sin MFA.
Actor: cuenta de prueba ADMIN_SISTEMA de G7. No se modificaron cuentas de otros módulos.
Capturas y confirmaciones recibidas en la conversación de trabajo; no se incorporan
volcados con teléfonos, IP, correos de terceros ni credenciales al repositorio.

| Paso | Resultado observado | Evidencia |
|---|---|---|
| Estado previo | C activa y con sesión iniciada | Listado y confirmación del ejecutor |
| Bloquear con motivo «Prueba controlada FEAT-08» | Aviso «Cuenta bloqueada», bloqueo Manual / Sin vencimiento y motivo en el detalle administrativo | Capturas de diálogo y detalle |
| Aviso de bloqueo | Correo recibido sin motivo privado ni enlace de desbloqueo | Captura del correo recibido |
| Intentar login durante el bloqueo | Rechazo con mensaje genérico de credenciales | Captura de login; auditoría de intento fallido |
| Recargar la ventana de C con F5 | Vuelve al login | Confirmación de David; no demuestra por sí sola revocación, porque los tokens de la SPA están en memoria |
| Desbloquear y confirmar | Aviso «Cuenta desbloqueada», estado ACTIVA y sin bloque rojo | Capturas de diálogo y detalle posterior |
| Nuevo login | Ingreso permitido con la misma contraseña | Confirmación de David y captura de Actividad de mi cuenta |
| Aviso de desbloqueo | Correo recibido indicando que puede iniciar sesión | Captura del correo recibido |
| Auditoría | Bloqueo correcto 16:44; login fallido 16:45; desbloqueo correcto y nuevo login correcto 16:48 | Captura de Actividad de mi cuenta, 23-oct-2026, hora de Lima |

C quedó operativa, con rol CLIENTE y segundo factor inactivo. El recorrido acredita
bloqueo/desbloqueo, mensajes, entrega de correo y auditoría visibles en nube.

## Límites de aceptación

- Revocación de familias y rechazo del refresco anterior comprobados en PostgreSQL
  local; **no se probó directamente el refresco de la sesión anterior en nube**.
  F5 no acredita esa propiedad ni revocación instantánea de un JWT validado localmente.
- No se ejecutaron individualmente en nube los rechazos por falta de permiso,
  autobloqueo/último administrador, cuenta no operativa, motivo vacío/largo,
  sustitución automática→manual, idempotencia, concurrencia, fallos de efectos
  críticos ni privacidad ante token de servicio. Hay cobertura automatizada,
  pero no equivale a evidencia desplegada de cada caso.
- La entrega de eventos al broker/consumidores no se inspeccionó en este recorrido;
  la auditoría visible no la sustituye. Versión efectiva de Render pendiente de
  adjuntar si se requiere trazabilidad independiente del despliegue comunicado.
- Observaciones no bloqueantes de Sergio: diagnóstico del 503 y revisión de un
  caso límite concurrente de desbloqueo. No se introducen cambios de código aquí.

FEAT-11 permanece En curso y pendiente de los datos de G1 y su prueba conjunta.
Este registro no implementa código de Marketplace ni acredita esa integración.
