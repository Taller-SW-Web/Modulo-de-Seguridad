# FEAT-15 — Revisión de seguridad del pipeline

Responsable: Juan José Cano Vasquez. Corte: 27-nov-2026, America/Lima.
Rama de código: `feat/TEC-INFRA-seguridad-pipeline` en backend y frontend.
Estado: **T1–T4 implementadas localmente; T4 espera CI posterior en PR y activación de checks requeridos**.

## Ejecuciones revisadas

| Repositorio | PR | Ejecución de seguridad | Resultado observado |
|---|---:|---:|---|
| Backend | [#98](https://github.com/Taller-SW-Web/Modulo-de-Seguridad-Backend/pull/98) | [45941230331](https://github.com/Taller-SW-Web/Modulo-de-Seguridad-Backend/actions/runs/45941230331) | Jobs verdes. Gitleaks: 0 hallazgos. Trivy: 11 HIGH y 7 CRITICAL en librerías Java; el reporte se descargó como artefacto `trivy-backend-45941230331-1` (ID `15172716643`). |
| Frontend | [#29](https://github.com/Taller-SW-Web/G7-Modulo-de-Seguridad-Frontend/pull/29) | [45945578698](https://github.com/Taller-SW-Web/G7-Modulo-de-Seguridad-Frontend/actions/runs/45945578698) | Jobs verdes. Gitleaks: 0 hallazgos. CodeQL y revisión de dependencias terminaron correctamente. |

Los checks de PR #98 y #29 revisados estaban exitosos. Los checks omitidos de
deploy, smoke y E2E tenían condiciones de ejecución, por lo que `skipped` no se
interpretó como prueba funcional ejecutada. Esos resultados corresponden a los
commits anteriores a T4; la verificación local posterior se registra abajo.

## Política T4

- **Bloquean en backend:** el destino/build/pruebas existentes; cualquier
  secreto nuevo detectado por Gitleaks; y vulnerabilidades Trivy `HIGH` o
  `CRITICAL` de la imagen, salvo excepción acotada, justificada y con vencimiento.
- **Bloquean en frontend:** destino/build/pruebas y cualquier secreto detectado
  por Gitleaks.
- **Solo informan:** hallazgos CodeQL, Dependency Review y Trivy `MEDIUM`, `LOW`
  o `UNKNOWN`. Se revisan antes del merge cuando afectan código o dependencias
  del cambio. Estos workflows no están configurados como gates de rama.

Un estado verde significa que el job terminó; no significa que el reporte no
tenga hallazgos. La política y sus gates están descritos en el `.github/CI.md`
de cada repositorio de código. Para que GitHub bloquee técnicamente el merge,
un administrador debe agregar los checks Gitleaks del backend/frontend y Trivy
del backend a la protección de `develop`; el estado documentado antes de este
cambio solo hacía requeridos los checks de destino/build. Esa configuración no
se puede cambiar desde el repositorio y queda pendiente de aplicación.

## Decisión de los hallazgos altos y críticos del reporte del backend

| CVE | Paquete/version observada | Severidad en el artefacto | Decisión T4 |
|---|---|---|---|
| CVE-2026-89407, CVE-2026-89425 | Jackson Core 2.21.4 | HIGH | Corregir actualizando Jackson a 2.21.7. |
| CVE-2026-68497, CVE-2026-91776, CVE-2026-91777 | Jackson Databind 2.21.4 | HIGH | Corregir actualizando Jackson Databind a 2.21.7. |
| CVE-2026-63337, CVE-2026-69219, CVE-2026-69220, CVE-2026-75516 | RabbitMQ AMQP client 5.25.0 | HIGH | Corregir actualizando el cliente a 5.38.0. |
| CVE-2026-55276, CVE-2026-65182, CVE-2026-65637, CVE-2026-65905, CVE-2026-68525 | Tomcat 10.1.55 | CRITICAL | Corregir actualizando Tomcat a 10.1.62. |
| CVE-2026-53404 | Tomcat 10.1.55 | HIGH | Corregir con la misma actualización a 10.1.62. |
| CVE-2026-54291 | PostgreSQL JDBC 42.7.11 | HIGH | Corregir actualizando el driver a 42.7.12. |
| CVE-2026-47884 | Spring Web MVC 6.2.19 | CRITICAL en Trivy; advisory Spring: MEDIUM | Justificación temporal: el advisory requiere `XsltView`, un mapping `/**` que resuelva una vista y que no se especifique su nombre. El código actual es API REST y no define `XsltView` ni resolución de vistas. No se ignora globalmente: vence 2027-02-27 en `.trivyignore`. |
| CVE-2026-47890 | Spring Web MVC 6.2.19 | CRITICAL en Trivy; advisory Spring: LOW | Justificación temporal: requiere SSE con fragmentos de vista y datos controlados por atacante. El backend no implementa SSE de fragmentos. Vence 2027-02-27 en `.trivyignore`. |
| CVE-2026-59901 | Netty Codec 4.1.135.Final | HIGH en el escaneo local posterior | Corregir actualizando la línea Netty a 4.2.21.Final, que cumple la versión corregida publicada para el hallazgo. |
| CVE-2026-75595 | Netty Handler 4.1.135.Final | CRITICAL en el escaneo local posterior | Corregir actualizando la línea Netty a 4.2.21.Final, que cumple la versión corregida publicada para el hallazgo. |

La clasificación del proveedor y la severidad del escáner se conservan ambas:
la diferencia de severidad no elimina el registro ni la reevaluación. El código
de producción no se modificó para falsear o suprimir resultados; el ignore solo
cubre esos dos CVE concretos y expirables.

## Cambios de T4 y límites de verificación

- El backend fija versiones del BOM de Spring Boot para Jackson, RabbitMQ, Tomcat,
  PostgreSQL y Netty; Trivy
  bloquea HIGH/CRITICAL y Gitleaks bloquea secretos. Las dos excepciones se
  registran en `.trivyignore` con vencimiento. Netty se actualizó a 4.2.21.Final
  al aparecer dos hallazgos en la base de vulnerabilidades más reciente.
- Frontend y backend documentan en `.github/CI.md` qué jobs bloquean y cuáles
  informan.
- Validación local posterior a las actualizaciones: JDK 21.0.12.1,
  `gradlew.bat --no-daemon --console=plain clean build` terminó `BUILD SUCCESSFUL`,
  1220 pruebas, 0 fallos, 0 errores y 0 omitidas.
- `docker build --pull --tag auth-service:feat15-t4 .` terminó correctamente;
  digest local `sha256:a458177c8fd47587e5f91484ebfd5816c1db72ecf9d1113d68bd0ad49a2b165b`.
  Trivy 0.76.2, con base actualizada, escaneó la imagen con severidad
  `HIGH,CRITICAL` y `--exit-code 1`: 0 hallazgos bloqueantes, 2 CVE suprimidos
  exactamente por `.trivyignore` (CVE-2026-47884 y CVE-2026-47890).
- Los checks de PR #98 pertenecen al código anterior a estos cambios y no se
  presentan como evidencia de remediación. La nueva CI debe ejecutarse al
  incorporar estos cambios.
- No se conectó a Supabase ni se alteró configuración de despliegue.
