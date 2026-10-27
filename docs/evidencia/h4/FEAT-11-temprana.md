# FEAT-11 — Prueba temprana G1–G7

Responsable G7: Luis David Morales Brenis. Corte: 26-oct-2026, America/Lima.
Estado: **T1/T2/T3/T4 En curso; T5 pendiente**. No se declara cerrada FEAT-11.

## Fuentes y procedencia

- [Plan H4](../../../plan/hito-4/README.md), [tablero de David](../../../tareas/hito-4/luis-morales/TAREAS.md)
  y [paquete G1–G7](../../integracion/hito-4-marketplace.md).
- Base documental `6409c22732633a8fefbdb9daf183b5d5e67c59b2` de main.
- Mensaje de G1 aportado por David en esta conversación el 26-oct: pruebas con
  cuentas CLIENTE A/B desde sus servicios desplegados e incidente del 25-oct.
  No se aportaron aquí capturas, identificadores de ejecución ni SHAs de esas pruebas.
- Estado de PRs consultado en GitHub el 26-oct. El merge acredita código integrado,
  no identifica por sí solo el código ejecutado en Render/Vercel.

Los resultados siguientes son **reportados por G1**, no una ejecución independiente
de David ni una prueba conjunta presenciada desde esta sesión. No se incluyen
contraseñas, códigos, tokens, cookies ni enlaces de verificación.

## Entornos y cuentas

| Componente | URL | Versión efectiva |
|---|---|---|
| Frontend G1 | https://g1-marketplace-frontend.vercel.app/ | G1 informa autodespliegue desde develop; falta SHA de la ejecución |
| BFF G1 | https://g1-marketplace-backend.onrender.com/ | G1 informa autodespliegue desde develop; falta SHA de la ejecución |
| API G7 v1 | https://g7-auth-service.onrender.com/api/v1 | Falta confirmar SHA efectivo durante las pruebas de G1 y actual |
| SPA G7 | https://g7-seguridad-spa.onrender.com | Falta confirmar SHA efectivo actual |

A/B son las cuentas CLIENTE de prueba dedicadas a Marketplace coordinadas previamente
por David; G1 confirma su uso. No son la cuenta administrativa de Jim ni las fixtures
de Ventas/Postventas. A usa segundo factor por correo; B no lo usa, según su prueba.
No se volvió a consultar ni modificar su estado en esta sesión.

El BFF ya tiene URL pública y hay resultados funcionales reportados. Eso no acredita
todos los detalles de la propuesta del 20-oct: faltan confirmación del almacenamiento
de tokens en servidor, cookie opaca HttpOnly/Secure/SameSite=Lax, protección CSRF,
persistencia de sesión y coordinación de renovaciones. No se cambia CORS sin confirmar
si existe alguna llamada directa del navegador a G7. El tráfico BFF→G7 no requiere CORS.
El retorno `/login` de G1 continúa propuesto, pendiente de confirmación exacta por entorno.

## Casos reportados y aceptación pendiente

| Caso | Resultado aportado por G1 | Límite de la conclusión |
|---|---|---|
| B: login sin MFA, sesión y cierre | Correcto | Falta fecha de ejecución, SHAs y evidencia sanitizada |
| A: login, envío/verificación OTP por correo y sesión | Correcto | Igual; no acredita otros estados o errores del desafío |
| A: renovación y cierre | Correcto; después anónima | No demuestra rechazo del refresco revocado ni reutilización del rotado |
| Favoritos sin sesión | 401 | Solo este caso de denegación |
| Favoritos con B | 200 | Acceso autenticado; no demuestra aislamiento entre usuarios |
| Favoritos A/B con datos propios | No ejecutado completamente | G1 necesita productos para crear los datos de prueba |

Antes de cerrar T2: A y B crean/consultan datos propios y no pueden leer ni modificar
los del otro manipulando IDs; firma alterada → 401; registrar recarga, renovación
concurrente y rechazo del refresco después del cierre. Los demás casos del
[guion acordado](../../integracion/hito-4-marketplace.md#4-guion-de-prueba-y-aceptación)
conservan su estado pendiente hasta ejecutarse. No se promete revocación instantánea
del token de acceso validado localmente (ADR-002).

## Seguimiento de disponibilidad G7 (T4)

| Bloque | Código integrado comprobado | Despliegue y prueba con G1 |
|---|---|---|
| Registro/verificación/reenvío | Backend [#61](https://github.com/Taller-SW-Web/Modulo-de-Seguridad-Backend/pull/61), merge `d60befab174e7ce7c717b88947456f8dcbd65b9d`; frontend [#16](https://github.com/Taller-SW-Web/G7-Modulo-de-Seguridad-Frontend/pull/16) abierto y en borrador, head `242accfcf38827a585c691db8cfaf4d8c1bbc8f3` | No confirmados; no anunciar el bloque desplegado por el merge |
| Recuperación/restablecimiento | Backend [#64](https://github.com/Taller-SW-Web/Modulo-de-Seguridad-Backend/pull/64), merge `7142a03f0aee70f1d969cb572b74e5b546b80e35`; frontend [#13](https://github.com/Taller-SW-Web/G7-Modulo-de-Seguridad-Frontend/pull/13), merge `e9cd51dbf25a72ace05570c460d32ee34e08578b` | Falta confirmar versión/configuración activa y ejecutar recorrido desplegado |
| Login/MFA/sesión/renovación/cierre v1 | Capacidades H3 reutilizadas; no reimplementadas | Resultados de G1 en la tabla anterior, aún sin SHA efectivo |
| Identidad técnica y direcciones | [Registro de Sergio del 25-oct](FEAT-05-06.md): reporta despliegue G7 `c90037b3d2c098aeef10e80676737d9024f39696`, anterior al merge #61 | Registro histórico atribuido a esa fuente; falta cliente de servicio/grant G1 provisionado y prueba positiva. Lote pendiente según la fuente |

La variable `AUTH_SOLICITUD_LIMITADA_HMAC_KEY` fue cargada por David en Render con
«Save only» durante la preparación. No se expone su valor ni se afirma que el proceso
actual ya la tenga activa. Confirmarla en el siguiente despliegue de registro/recuperación.
Una versión de develop actual no sustituye el SHA del servicio desplegado.

**Captura Render aportada por David el 26-oct:** el despliegue de backend #64 figura
`Deploy succeeded | Live`, Source `7142a03`, fecha 23-oct, 20:24:08 GMT-5 y disparo
manual. El SHA completo del merge es `7142a03f0aee70f1d969cb572b74e5b546b80e35`.
Esto acredita ese despliegue, no la prueba funcional de recuperación ni el SHA
de todas las solicitudes de G1. Contradice el reporte documental de `c90037b`
como desplegado. Una segunda captura de Events del 26-oct confirma en la cabecera
del servicio `develop · 7142a03 · Live` y **Auto-Deploy desactivado por despliegue
de un commit específico**. El último despliegue visible es #64 del 23-oct, iniciado
a las 20:24 y Live a las 20:27; debajo figuran #65 y #57 anteriores. No hay un deploy
del 25-oct en el tramo visible. Se toma `7142a03` como versión activa observada el
26-oct; el reporte de `c90037b` no queda corroborado por Render. Registro #61 no
se desplegó automáticamente con su merge. No se cambió el servicio, su rama ni
Auto-Deploy durante esta comprobación. FEAT-06-T1/T3 posteriores a #64 no se anuncian
como disponibles en esta versión.

## Incidencia de disponibilidad

G1 reporta un login posterior de B con 503 y un timeout del JWKS público desde su
equipo el **25-oct, 12:28–12:29 de Lima (17:28–17:29 UTC)**. No está establecida
la relación causal entre ambos hechos, ni si el 503 lo generó G1 o G7.

**Logs Render del 25-oct aportados por David en captura (GMT-5):**

| Hora de Lima | Registro visible |
|---|---|
| 12:29:32,854 | `Starting AuthServiceApplication`, instancia `[474tn]` |
| 12:30:30,355 | Hikari inicia el pool; advertencia de `idleTimeout` frente a `maxLifetime` |
| 12:30:37,555–556 | Conexión de base añadida; `Start completed` del pool |
| 12:32:03,259 | Tomcat iniciado en puerto 10000 |
| 12:32:03,658 | Aplicación iniciada en `161.004 seconds` |
| 12:32:06,953 | Conexión RabbitMQ creada |

Se confirma un arranque de la aplicación cercano al incidente, con unos 2 min 41 s
de inicialización. Es compatible con que las consultas de G1 agotaran su espera
mientras el servicio iniciaba. La captura no demuestra qué provocó el arranque
(reactivación por inactividad, reinicio u otro evento) ni vincula una petición 503
específica con G7. El aviso de Render sobre suspensión por inactividad apoya una
hipótesis, no la confirma. La advertencia de Hikari no es por sí sola un fallo de
conexión: después se observa el pool iniciado. La segunda captura de Logs, ampliada
a 12:24–12:38, comienza con opciones de Java a las 12:28:11 y el banner de Spring
a las 12:29:31; no muestra una causa de terminación anterior. Events solo muestra
los despliegues del 23-oct en su tramo visible. Falta correlacionar el error de G1;
no se modifica configuración ni código a partir de esta hipótesis.

Comprobación de lectura desde esta sesión el 26-oct, iniciada aproximadamente a las
20:46 de Lima, con límite de 20 segundos por ruta:

| GET público | Resultado observado |
|---|---|
| `/actuator/health` | Timeout, 20,009 s, 0 bytes; sin respuesta HTTP |
| `/api/v1/auth/.well-known/openid-configuration` | Timeout, 20,002 s, 0 bytes; sin respuesta HTTP |
| `/api/v1/auth/.well-known/jwks.json` | Timeout, 20,006 s, 0 bytes; sin respuesta HTTP |

`curl` terminó con código 28; `HTTP 000` es ausencia de respuesta, no un estado
HTTP del servicio. El primer intento en el entorno de ejecución restringido falló
en DNS; los tiempos de esta tabla proceden de la repetición con acceso de red.
Estos resultados solo describen esta ruta de acceso y no prueban una caída general.
Una comprobación actual no reconstruye el estado del 25-oct. No se hicieron intentos
de login ni se usaron secretos.

Para completar la investigación: eventos de despliegue/reinicio y líneas anteriores
al arranque de Render G7 entre 12:25 y 12:35
de Lima del 25-oct, y de G1 ruta, hora exacta, duración, identificador de correlación
y respuesta 503 sanitizada. Sin esos registros no dar por confirmada la causa.
Los logs históricos aquí registrados proceden de la captura de David; no de acceso
directo a Render desde los conectores de esta sesión.

## Entregas pendientes y responsables

- **G1:** SHAs efectivos FE/BFF y referencia de despliegue de las pruebas; fecha,
  ejecutores y evidencia por caso; confirmar sesión implementada y retorno exacto;
  preparar productos y datos para aislamiento A/B; aportar detalle sanitizado del 503.
- **David/Christian:** confirmar SHA G7 actual y durante las pruebas, configuración
  activa y logs históricos; coordinar y registrar la repetición de los casos pendientes.
- **Eva:** frontend de registro/verificación y retorno por entorno; **Juan José/David**:
  comprobación desplegada de recuperación/restablecimiento. No dependen de productos
  de G1 para probar primero su recorrido G7.
- **Sergio/Christian:** aprobación de scopes y provisión segura del cliente técnico
  de G1 conforme al runbook; David registra la prueba conjunta cuando esté disponible.

No se envió ningún mensaje a otros equipos desde esta sesión, no se cambió OpenAPI,
no se aplicó SQL remoto y no se implementó código de Marketplace. Este corte permite
continuar T1–T4; no cierra T2 ni la prueba conjunta final T5.
