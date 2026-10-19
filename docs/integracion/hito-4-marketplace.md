# H4 — paquete inicial de integración G1–G7

**Corte:** 19-oct-2026. **Responsable técnico G7:** Luis David Morales Brenis (FEAT-11).
**Contrato y scopes:** Sergio Osorio (PO). Jose Luis conserva FEAT-12 (E2E y G5).
Este documento inicia FEAT-11-T3; no acredita cuentas entregadas, mensajes enviados,
acuerdos externos ni integración probada. Estado de T3: **En curso**.

## 1. Fuentes y alcance

- [Plan H4 v1.2](../../plan/hito-4/README.md) y [tablero de David](../../tareas/hito-4/luis-morales/TAREAS.md).
- [OpenAPI v1 1.1.0](../../specs/contrato-api/openapi.yaml), [estado de versiones](../../specs/contrato-api/README.md), [kit](../../specs/kit-integracion.md) y [matriz de scopes](matriz-scopes.md).
- Solicitud recibida «Coordinación G1–G7 Seguridad H4», 19-oct: F-001 registro/verificación, F-002 login/sesión, F-003 cierre, F-004 recuperación y F-005 restablecimiento; identidad técnica para Productos y posterior lectura autorizada de direcciones. El [plan público G1 S02](https://github.com/Taller-SW-Web/G1-Marketplace-SPECS/blob/11e7da0/plan/iteraciones/S02-semanas-09-11.md) sirve de referencia compartida. Este resumen no sustituye sus specs.

Revisiones fuente comprobadas con referencias remotas (no SHAs certificados de despliegue):

| Repositorio | develop | main |
|---|---|---|
| Documentación G7, base de estos ajustes | — | `f8ad6bab01119e9de08ae65e0b56ce79bf2cd0d9` |
| Backend G7 | `4bd7b61165ff2e4940c68a8b0f050ba395b7b7ad` | `d51ac2eb1fb469c0a19eb26c5d0bb53dde199d3e` |
| Frontend G7 | `82c94149c8e2326d3d6013a167026a6b38d75b4a` | `a7e9947afbaaf277a0f18dee2f3b3bccc637d8e9` |
| Backend G1 | `f821e9e98f545a586f8ff201bda55d2a7261bee2` | `c7e8e8e73816206efb9b0e675f15099b49138d96` |
| Frontend G1 | `1f22cf0667a64218b746d2461c4c9cd909ef0cd1` | `ec1acfba35f692afc726efb21fc704f89c5c502d` |

La inspección previa de G1 main encontró login simulado y un guard RS256/JWKS.
Sus referencias develop se registran como dato, sin acreditar aquí su contenido ni ejecución.

## 2. Entorno y matriz inicial por operación

| Dato | Valor de referencia | Comprobación pendiente |
|---|---|---|
| SPA G7 | https://g7-seguridad-spa.onrender.com | SHA desplegado y recorrido funcional |
| Base API G7 | https://g7-auth-service.onrender.com/api/v1 | SHA del servicio y prueba autenticada |
| Issuer | `https://g7-auth-service.onrender.com/api/v1/auth` | Coincidencia exacta con token y discovery del entorno |
| Discovery | https://g7-auth-service.onrender.com/api/v1/auth/.well-known/openid-configuration | Respuesta y campos del entorno |
| JWKS | https://g7-auth-service.onrender.com/api/v1/auth/.well-known/jwks.json | Respuesta, claves públicas y firma válida |
| Salud | https://g7-auth-service.onrender.com/actuator/health | Respuesta del entorno |
| Frontend/BFF G1 y origen/retorno | Por confirmar por Jim/Andres | URL pública, SHA, transporte y retorno exactos |

**Última comprobación de este corte:** GET públicos de salud, JWKS y discovery
agotaron 20 segundos de espera sin obtener respuesta HTTP. No determina la causa
ni prueba caída permanente. Las respuestas 200 de una comprobación anterior del
19-oct son históricas; no acreditan disponibilidad actual ni login funcional.
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

## 3. Datos necesarios antes de la primera prueba

| Información/entrega | Responsable | Estado |
|---|---|---|
| Frontend/BFF G1: URLs, SHAs y operación propia protegida elegida | Jim/Andres con dueños funcionales G1 | Pendiente de respuesta |
| Sesión navegador/Next/BFF: almacenamiento, recarga, refresh y logout | G1; David revisa compatibilidad | Pendiente de acuerdo |
| Origen web y URL exacta de login/retorno por entorno | G1; David coordina y Christian configura | Pendiente; no modificar CORS todavía |
| Confirmación del SHA G7 realmente desplegado y salud/JWKS/login | Christian con David | Pendiente |
| Cuentas A/B y disponibilidad de datos propios en G1 | David + G1 | Pendiente de confirmar/coordinar |
| Casos pendiente de verificar, bloqueado y MFA, con correo observable | Dueños G7 con David/Christian | Pendiente según capacidad disponible |
| Horario y ejecutores de prueba conjunta | David + Jim/Andres | Pendiente de acuerdo |

Credenciales por canal privado; el documento registra solo referencia de entrega,
no contraseñas, OTP, JWT, refresh ni enlaces sensibles. Una fixture creada manualmente
no acredita autorregistro. No crear cuentas ni emitir secretos durante este ajuste documental.

CORS aplica a peticiones directas del navegador; servidor a servidor desde BFF no
requiere CORS. Si G1 utiliza cookies, debe definir atributos de seguridad y defensa
CSRF según su diseño. No imponerle la sesión en memoria de la SPA G7.

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
3. Desde la web desplegada de G1, A consulta o modifica un recurso propio (p. ej., favoritos).
4. Repetir la petición contra la API de G1: sin token y firma alterada → 401; B no puede
   leer ni modificar el recurso de A manipulando IDs, según el error contractual G1.
5. Probar recarga, renovación con rotación y logout; el refresco cerrado debe rechazarse.
   No prometer revocación instantánea de un token de acceso validado sin conexión.
6. Incorporar registro/verificación, recuperación y cliente técnico al desplegar cada FEAT.
   Para este último comprobar audiencia/tipo, scopes concedidos y denegación por scope.
7. Registrar fecha, ejecutores, SHAs G7/G1, URL, caso, resultado y evidencia sanitizada en
   `docs/evidencia/h4/FEAT-11-temprana.md` y luego `FEAT-11.md`, cuando existan resultados.

Solo `/auth/me` correcto acredita conectividad; la integración de dominio necesita
la operación de G1 y sus denegaciones. No marcar como pasados casos sin ejecutar.

## 5. Mensaje de seguimiento propuesto (pendiente de envío)

> Mano, ya dejamos el plan H4 y las tareas de integración coordinados. David será el
> contacto técnico con G1; Sergio conserva contrato/scopes y Jose Luis lleva E2E/G5.
> Para arrancar F-002/F-003, ¿Jim/Andres nos confirman URLs y commits desplegados del
> frontend/BFF, cómo conservarán la sesión al recargar/renovar/cerrar, la URL de retorno
> al login y una operación protegida propia de G1 (por ejemplo favoritos)?
> Proponemos probar A/B desde la web y la API para demostrar que cada uno accede solo
> a sus datos. Necesitamos acordar horario y datos de prueba. Antes confirmaremos el
> entorno y SHA de G7; registro, recuperación, direcciones y tokens técnicos continúan
> pendientes hasta publicarse y probarse sus bloques. Las credenciales van por privado.

No enviar automáticamente este borrador ni crear un issue externo con él sin instrucción humana.
