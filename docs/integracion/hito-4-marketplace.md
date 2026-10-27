# H4 — paquete inicial de integración G1–G7

**Corte:** 26-oct-2026. **Responsable técnico G7:** Luis David Morales Brenis (FEAT-11).
**Contrato y scopes:** Sergio Osorio (PO). Jose Luis conserva FEAT-12 (E2E y G5).
Este documento reutiliza el paquete publicado en los PR documentales #64/#65 e
incorpora los resultados que G1 comunicó a David. No declara cerrada la integración.
Estado de T3: **En curso**. No se enviaron mensajes desde esta sesión.

## 0. Actualización del 26-oct: primera prueba reportada por G1

G1 confirma frontend https://g1-marketplace-frontend.vercel.app/ y BFF
https://g1-marketplace-backend.onrender.com/, ambos con autodespliegue desde develop.
Reporta login/sesión/cierre con B sin MFA; login con OTP EMAIL, sesión, renovación y
cierre con A, que después queda anónima; favoritos sin sesión → 401 y con B → 200.
**Aislamiento de favoritos A/B pendiente**: necesita productos para crear datos.
El mensaje no incluye SHAs efectivos, evidencia por caso ni confirmación de todos
los detalles de la sesión propuesta. No se sustituye esa información por HEAD de develop.

La [evidencia temprana](../evidencia/h4/FEAT-11-temprana.md) distingue lo reportado,
lo comprobado en GitHub y lo no ejecutado; contiene la matriz de disponibilidad
actualizada y el seguimiento de un 503/timeout del 25-oct, aún sin causa confirmada.
Registro/verificación backend #61 ya está fusionado; frontend #16 sigue abierto.
Recuperación backend #64 y frontend #13 están fusionados. **Ninguno de esos merges
acredita por sí solo su disponibilidad desplegada para G1.** El registro de Sergio
del 25-oct reporta G7 `c90037b`, anterior a #61. Una captura Render aportada el 26-oct
muestra el despliegue #64 `7142a03` como Live. La cabecera de Events confirma esa
versión activa y Auto-Deploy desactivado por commit específico: el reporte de
`c90037b` no queda corroborado y #61 no se publicó automáticamente. Los logs muestran arranque de G7 el
25-oct de 12:29:32 a 12:32:03 (Lima), cercano al incidente; su causa sigue sin confirmar.

Las referencias de código y la matriz inicial de §1–§2 siguientes se conservan
como **antecedente del 20-oct**, no como inventario actual de disponibilidad.
Para comunicar el estado vigente usar esta actualización y la evidencia enlazada.

## 1. Fuentes y alcance

- [Plan H4 v1.2](../../plan/hito-4/README.md) y [tablero de David](../../tareas/hito-4/luis-morales/TAREAS.md).
- [OpenAPI v1 1.1.0](../../specs/contrato-api/openapi.yaml), [estado de versiones](../../specs/contrato-api/README.md), [kit](../../specs/kit-integracion.md) y [matriz de scopes](matriz-scopes.md).
- Solicitud recibida «Coordinación G1–G7 Seguridad H4», 19-oct: F-001 registro/verificación, F-002 login/sesión, F-003 cierre, F-004 recuperación y F-005 restablecimiento; identidad técnica para Productos y posterior lectura autorizada de direcciones. El [plan público G1 S02](https://github.com/Taller-SW-Web/G1-Marketplace-SPECS/blob/11e7da0/plan/iteraciones/S02-semanas-09-11.md) sirve de referencia compartida. Este resumen no sustituye sus specs.

Respuesta recibida: «Respuesta para iniciar la integración de Marketplace con Seguridad»,
G1 a G7, 20-oct-2026, archivo `Respuesta-G1-a-G7-Integracion-Seguridad-H4.docx`.
G1 acepta comenzar F-002/F-003 con login, MFA EMAIL cuando aplique, perfil,
refresh/logout y favoritos con usuarios A/B. Su documento aún nombra a Jose Luis:
el contacto vigente es **David**, conforme al reparto H4; Sergio conserva contrato/scopes.

Al corte histórico del 20-oct, PR documental #64 fusionado y `main` en
`029171b61335ec0412af777c2d8a0559d8dadb16`. H3 está publicado en `main`
de backend (#47) y frontend (#10); esto no certifica los SHAs desplegados.

Revisiones fuente comprobadas con referencias remotas (no SHAs certificados de despliegue):

| Repositorio | develop | main |
|---|---|---|
| Documentación G7, base de estos ajustes | — | `029171b61335ec0412af777c2d8a0559d8dadb16` |
| Backend G7 | `4bd7b61165ff2e4940c68a8b0f050ba395b7b7ad` | `d51ac2eb1fb469c0a19eb26c5d0bb53dde199d3e` |
| Frontend G7 | `82c94149c8e2326d3d6013a167026a6b38d75b4a` | `a7e9947afbaaf277a0f18dee2f3b3bccc637d8e9` |
| Backend G1 | `f821e9e98f545a586f8ff201bda55d2a7261bee2` | `c7e8e8e73816206efb9b0e675f15099b49138d96` |
| Frontend G1 | `1f22cf0667a64218b746d2461c4c9cd909ef0cd1` | `ec1acfba35f692afc726efb21fc704f89c5c502d` |

La inspección previa de G1 main encontró login simulado y un guard RS256/JWKS.
Sus referencias develop se registran como dato, sin acreditar aquí su contenido ni ejecución.

## 2. Entorno y matriz inicial por operación (histórico del 20-oct)

| Dato | Valor de referencia | Comprobación pendiente |
|---|---|---|
| SPA G7 | https://g7-seguridad-spa.onrender.com | SHA desplegado y recorrido funcional |
| Base API G7 | https://g7-auth-service.onrender.com/api/v1 | SHA del servicio y prueba autenticada |
| Issuer | `https://g7-auth-service.onrender.com/api/v1/auth` | Coincidencia exacta con token y discovery del entorno |
| Discovery | https://g7-auth-service.onrender.com/api/v1/auth/.well-known/openid-configuration | Respuesta y campos del entorno |
| JWKS | https://g7-auth-service.onrender.com/api/v1/auth/.well-known/jwks.json | Respuesta, claves públicas y firma válida |
| Salud | https://g7-auth-service.onrender.com/actuator/health | Respuesta del entorno |
| Frontend G1 | https://g1-marketplace-frontend.vercel.app/ | SHA desplegado y sesión real pendientes |
| Retorno propuesto G1 | `https://g1-marketplace-frontend.vercel.app/login` | Confirmar e incorporar a lista permitida por entorno con Eva/Christian |
| BFF G1 | Por confirmar por Andres/Leonidas | URL pública, readiness, SHAs efectivos FE/BE y run de despliegue |

**Comprobación del 20-oct desde esta sesión:** `/login` de G1 respondió 200;
solo acredita la pantalla. Salud, discovery y JWKS de G7 agotaron 25 segundos
sin respuesta HTTP; no determina la causa ni prueba caída permanente. G1 informa
discovery/JWKS 200 ese mismo día: es evidencia atribuida a su documento, distinta
de esta comprobación. El login de G1 main sigue simulado y no crea sesión real.
No se usaron credenciales ni se hicieron cambios de configuración.

Rutas relativas a la base `/api/v1`. «Implementada» es evidencia de código, no de
adopción en G1. Para filas H3 la fuente backend es `4bd7b61`; el **SHA desplegado
está por confirmar en todas las filas**. v2 es parcial y no reemplaza estos flujos.

| Operación | Ruta v1 | Implementación revisada | Despliegue/prueba G1 | Responsable |
|---|---|---|---|---|
| F-001 registro | `POST /auth/registro` | Pendiente H4, sin SHA de implementación acreditado | Pendiente | Eva FEAT-01 |
| Verificación/reenvío | `POST /auth/verificar-correo`, `POST /auth/verificar-correo/reenviar` | Pendiente H4 | Pendiente | Eva FEAT-01 |
| Política pública | `GET /password/politica` | Implementada H3 | Revalidar entorno | Juan José; David verifica consumo |
| F-002 login | `POST /auth/login` | Implementada H3 | Pendiente prueba conjunta | David FEAT-11 |
| OTP de login | `POST /auth/otp/solicitar`, `POST /auth/otp/verificar` | Implementada H3; correo, SMS no acreditado como proveedor real | Pendiente prueba conjunta y entrega de correo | David; dependencia MFA/correo |
| Perfil | `GET /auth/me` | Implementada H3 | Conectividad inicial; no sustituye operación propia de G1 | David |
| Renovación | `POST /auth/refresh` | Implementada H3 | Probar transporte, recarga, rotación/reúso | David; Jose Luis FEAT-12 |
| F-003 cierre | `POST /auth/logout` | Implementada H3; auditoría adicional pendiente FEAT-12-T2 | Probar revocación de refresco y limpieza G1 | David; Jose Luis |
| F-004/F-005 | `POST /password/recuperar`, `POST /password/restablecer` | Pendiente H4 | Pendiente | Juan José FEAT-03 |
| Servicio e introspección | `POST /auth/token`, `POST /auth/introspeccion` | Pendiente H4 | Grants, provisión y runtime pendientes | Sergio FEAT-05 |
| Identidad/direcciones para servicios | `GET /usuarios/{id}`, `POST /usuarios/lote`, `GET /usuarios/{id}/direcciones` | Consulta administrativa H3 no acredita el acceso de servicio; ampliación pendiente H4 | Pendiente; G1 no tiene grant acreditado de direcciones | Sergio FEAT-06; Valery FEAT-04 |

Actualizar por fila **fuente fusionada → SHA/URL desplegados → caso ejecutado y
resultado → evidencia y aviso**. No comunicar disponibilidad basándose solo en un merge.

## 3. Datos recibidos y pendientes para completar la prueba

| Información/entrega | Responsable | Estado |
|---|---|---|
| Frontend/BFF G1: URLs, SHAs y operación propia protegida elegida | Jim/Andres/Leonidas | URLs recibidas; favoritos elegidos (`GET /api/v1/wishlist`) y casos iniciales reportados. Faltan SHAs efectivos y referencia del despliegue probado |
| Sesión navegador/Next/BFF: almacenamiento, recarga, refresh y logout | G1; David revisa compatibilidad | Login, sesión, refresh y logout reportados. Falta confirmar el diseño implementado, recarga/concurrencia y rechazo del refresco cerrado |
| Origen web y URL exacta de login/retorno por entorno | G1; David coordina y Christian configura | Origen recibido y retorno propuesto; falta confirmación/lista permitida. No modificar CORS todavía |
| Confirmación del SHA G7 realmente desplegado y salud/JWKS/login | Christian con David | Registro de Sergio del 25-oct reporta c90037b. Falta SHA efectivo de las pruebas y actual; incidencia de disponibilidad abierta |
| Cuentas A/B y disponibilidad de datos propios en G1 | David + G1 | G1 confirma uso de ambas cuentas CLIENTE dedicadas. Faltan productos/datos para aislamiento de favoritos |
| Casos pendiente de verificar, bloqueado y MFA, con correo observable | Dueños G7 con David/Christian | Pendiente según capacidad disponible |
| Horario y ejecutores de prueba conjunta | David + Jim/Andres | Faltan fecha/ejecutores del recorrido reportado y ventana para completar los casos |

Credenciales por canal privado; el documento registra solo referencia de entrega,
no contraseñas, OTP, JWT, refresh ni enlaces sensibles. Una fixture creada manualmente
no acredita autorregistro. No crear cuentas ni emitir secretos durante este ajuste documental.

CORS aplica a peticiones directas del navegador; servidor a servidor desde BFF no
requiere CORS. Si G1 utiliza cookies, debe definir atributos de seguridad y defensa
CSRF según su diseño. No imponerle la sesión en memoria de la SPA G7.

### Propuesta inicial de sesión: detalles implementados por confirmar

Navegador → Next en el mismo origen G1; cookie de identificador opaco HttpOnly,
Secure y SameSite=Lax. Next/BFF custodiarán los tokens G7 en servidor, con
almacenamiento compartido y caducidad, y refresh cifrado. No memoria de proceso
como única persistencia en Vercel ni JWT/refresh en localStorage. El diseño es
compatible con G7. Los resultados funcionales del nuevo mensaje no acreditan por
sí solos estos detalles de almacenamiento, cookie o protección CSRF.

G1 propone resolver perfil/sesión al recargar, coordinar renovaciones concurrentes
y reemplazar el refresh rotado; ante rechazo, caducidad o revocación limpiará
la sesión y pedirá login. No repetirá mutaciones automáticamente. Logout invocará
G7 e invalidará sesión/cookie G1; si G7 falla, informará el cierre remoto pendiente.
Aplicará CSRF y validación de origen a mutaciones por cookie; el BFF validará
el Bearer de usuario en cada operación protegida. Todo queda sujeto a prueba.

G7 mantiene una identidad central (`sub` UUID); cada módulo conserva sus datos de
negocio. Misma cuenta no implica sesión de navegador compartida automáticamente.
Usuario: firma RS256, issuer, tiempos, `tipo=acceso`, identidad y autorización de la
ruta, sin audiencia adicional inventada. Servicio: identidad técnica, `tipo=servicio`,
audiencia y scope concedido; su `sub` no identifica al comprador. Secretos técnicos
solo en el BFF. G6 confirma los grants de G1 para `api-productos`; no se heredan del
Chatbot. `direcciones:leer` para G1 exige acuerdo explícito y titularidad del recorrido.

## 4. Guion de prueba y aceptación

1. Confirmar URLs, SHAs efectivos, operación G1 y transporte; entregar cuentas por privado.
2. Verificar salud, discovery y JWKS. Login real desde G1, y `/auth/me` como comprobación inicial.
3. Desde la web desplegada de G1 y su API, A y B consultan sus favoritos con
   `GET /api/v1/wishlist`; el titular se deriva del `sub` validado, nunca de un ID elegido por el cliente.
4. Repetir la petición contra la API de G1: sin token y firma alterada → 401; B no puede
   leer ni modificar el recurso de A manipulando IDs. Con el token B, la mutación
   del mismo `productId` solo afecta a B y deja intacto A. DELETE idempotente sin
   elemento propio puede devolver 204: no exigir un 403 que la ruta no define.
   Incluir vencido, issuer/tipo inválidos y token técnico; fallo de claves sigue contrato G1.
5. Probar recarga, renovación concurrente sin duplicar rotación y logout; el refresco cerrado debe rechazarse.
   No prometer revocación instantánea de un token de acceso validado sin conexión.
6. Incorporar registro/verificación, recuperación y cliente técnico al desplegar cada FEAT.
   Para este último comprobar audiencia/tipo, scopes concedidos y denegación por scope.
7. Registrar fecha, ejecutores, SHAs G7/G1, URL, caso, resultado y evidencia sanitizada en
   `docs/evidencia/h4/FEAT-11-temprana.md` y luego `FEAT-11.md`, cuando existan resultados.

Si G6 utiliza fixtures para enriquecimiento comercial, rotularlos y limitar
la conclusión a autenticación y aislamiento G1–G7, sin declarar integración G6.

Solo `/auth/me` correcto acredita conectividad; la integración de dominio necesita
la operación de G1 y sus denegaciones. No marcar como pasados casos sin ejecutar.

## 5. Seguimiento que debe coordinar David

Pedir a G1 los SHAs efectivos FE/BFF de la ejecución y evidencia sanitizada por caso;
confirmación del retorno exacto y de la sesión implementada; productos/datos y horario
para completar aislamiento A/B y denegaciones; y el detalle sanitizado del 503 del
25-oct (hora, ruta, duración y correlación). David/Christian revisan logs y SHA de G7.
No volver a pedir la URL del BFF ni nuevas cuentas: ambas entregas ya están reportadas.
Comunicar registro y recuperación solo tras confirmar su despliegue y comprobación.

No enviar mensajes ni crear un issue externo sin instrucción humana.
