# Decisiones y dependencias del recorrido H3

**Vista del corte:** 12-oct-2026. La fuente de estado es el
[registro único](../docs/registro-inconsistencias.md); la configuración bilateral
se concreta en [la ficha de Ventas](../docs/integracion/hito-3-ventas.md).
Esta vista orienta los planes, sin cerrar pendientes ni crear otro registro.

## Qué habilita cada decisión

| Referencia | Decisión / evidencia necesaria | Dueño | Antes de qué |
|---|---|---|---|
| INT-05 / A11 | Recorrido y Michael ya confirmados; completar URLs, rama G5, origen, revisión consumida, transporte y hora | Sergio/Joseph/Michael | Conectar entornos y ejecutar prueba conjunta |
| H-11 / INT-05 | G7 define tokens en memoria; G5 confirma su consumo y recarga con nuevo login | Jose Luis/Sergio; Michael del lado G5 | Declarar acordado el comportamiento de sesión del frontend G5 |
| GOV-01 | Publicación/aviso del contrato; no hay excepción automática al plazo de una semana | Sergio | Anunciar cambios disponibles; la demo usa operaciones v1 existentes |
| TEC-01 | Supabase confirmado; conexión/runtime diseñados; proyecto/esquema real, secretos y host de despliegue por comprobar | Christian/Jose Luis | Preparar configuración y desplegar |
| TEC-02 | Protocolo reproducible de fixtures, UUID A/B/V, credenciales privadas y validaciones reales | Eva; Juan José y Sergio en sus capacidades | Crear cuentas y precargar pedidos asociados |
| TEC-03 | Par RSA/kid del entorno, custodia, persistencia entre reinicios y entrega pública de JWKS | Sergio/Christian | Firmar tokens reales y desplegar |
| TEC-04 | Dependencias efectivas de Java/Spring y pruebas en ese runtime | Jose Luis/Sergio/Christian | Acreditar que el código propio valida/emite correctamente |
| DAT-01 | Diseño H3 cerrado de mappings, familia/acceso, OTP/huellas y outbox; contrastar esquema real e implementar migraciones; cambio MFA posterior | Dueños del dominio | Implementar la parte afectada; no bloquea automáticamente todo login/JWKS |
| H-12 | Texto/versión de términos y aceptación, todavía sin definir | Eva/Sergio | Registro/activación; fixtures no pueden inventar esa aceptación |
| H-14 | Contacto real de soporte | Luis David/Sergio | Completar la indicación de soporte del bloqueo manual |
| DOC-02 | Reconciliación documental preparada: SPEC-18 limita permisos a Seguridad, sin agregar los de consumidores | Sergio | Ajuste local por publicar; datos/servicios de consulta conservan sus dependencias posteriores |
| EVT-02 | Calendario de eventos H3 aprobado en el incremento: mutaciones de roles/bloqueo/perfil con efectos completos | Sergio/Christian/productores | Tareas de mutación/outbox; sin cambiar payload ni dar por confirmadas suscripciones externas |

TEC-01–04 conservan configuración/pruebas de ejecución pendientes; su diseño
H3 ya está cerrado por delegación de Luis. Están registrados
una sola vez en el inventario. No son autorizaciones para crear recursos de
nube, credenciales o cuentas durante esta preparación documental.

## Trabajo que sigue disponible

Se pueden redactar los planes y derivar tareas con contratos y casos definidos,
identificando qué dependencia requiere cada una. Las configuraciones finales,
fixtures y prueba en nube esperan sus datos concretos. Resolver H-12/H-14,
A9 o todas las suscripciones no se convierte en requisito de JWKS por deducción.

La demo usa CLIENTE sin MFA voluntario. No habilita emitir tokens directamente
a una cuenta con MFA efectivo, ni aplicar estados/roles de prueba a datos reales.
La integración de A10 y las seis conexiones del alcance general conservan sus
pendientes; no se exigen para demostrar login/mis pedidos.
