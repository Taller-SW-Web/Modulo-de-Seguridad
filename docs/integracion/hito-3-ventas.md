# Hito 3 — Seguridad con Ventas

**Decisión del equipo G7:** priorizar únicamente Ventas para la integración de
este hito. **Estado del recorrido:** confirmado por Ventas en
[G5 Backend #2](https://github.com/Taller-SW-Web/G5-Ventas-Postventas-Backend/issues/2)
y aceptado como alcance por G7. Michael confirmó disponibilidad para F1 y
coordina JWT del lado de Ventas. **Objetivo de disponibilidad:** jueves
15/10/2026. Ventas lo compromete en el issue; Luis adopta ese objetivo para
Seguridad en esta conversación. [Acuse de G7 publicado por Luis](https://github.com/Taller-SW-Web/G5-Ventas-Postventas-Backend/issues/2#issuecomment-6118366251).
Marketplace y los otros cinco consumidores conservan su alcance general;
Marketplace ya no es el recorrido de salida de Hito 3.

**Actualización 16-oct-2026:** ambos servicios están en nube y la prueba
conjunta por API se ejecutó con tokens de usuario; resultados en
[Prueba conjunta en nube](#prueba-conjunta-en-nube--16-oct-2026). No acredita
el frontend de G5, una pantalla de pedidos en la SPA de G7, los tokens de
servicio de A10 ni el cierre de H3. El plan de nueve
puntos sigue vigente en [plan](../../plan/README.md).

El [plan técnico del recorrido](../../plan/PLAN-IMPLEMENTACION.md) organiza
capacidades, fases y casos de aceptación. EP-02 y TEC-IDENTIDAD tienen
planes preparados para revisión; tareas todavía no generadas. Esta ficha
conserva la coordinación bilateral.

## Recorrido confirmado

Frontend de demostración de Ventas → `POST /api/v1/auth/login` de Seguridad →
token de usuario RS256 → `GET /api/v1/pedidos/me` de Ventas → validación por
issuer/JWKS → autorización `CLIENTE` y consulta persistida filtrada por `sub`.

Usar cuentas CLIENTE activas y sin MFA habilitado, con pedidos precargados en
la base de Ventas. Esto evita ejecutar checkout, stock, despacho y pagos en la
demo. Los pedidos son fixtures persistidos, no respuestas simuladas. MFA sigue
siendo obligatorio para cualquier rol de gestión.

El token de usuario no requiere `client_id`, `client_secret` ni scopes de A10.
La integración de servicio de A10 sigue siendo otro recorrido: token de
servicio + consulta de identidad antes de operaciones sensibles. Su acuerdo
está confirmado; no demuestra la ejecución. La confirmación de esta demo
se registra por separado como A11, sin alterar A10.

## Ficha que deben completar ambos equipos

| Dato | Estado / responsable |
|---|---|
| Aceptación del recorrido H3 | Ventas confirmó en G5 Backend #2; Luis publicó el acuse de G7. Prueba por API ejecutada el 16-oct |
| Disponibilidad de Ventas | Comprometida por Joseph: como máximo jueves 15/10/2026; backend en nube el 16-oct |
| Objetivo de disponibilidad de Seguridad | Jueves 15/10/2026, indicado por Luis; desplegado y usado el 16-oct |
| Responsable de F1 de Ventas | Michael, disponibilidad confirmada en #2; incluye el recorrido de pedidos |
| Responsable del validador JWT de Ventas | Michael, confirmado en #2 |
| Rama/ubicación de /pedidos/me | `main` de G5 Backend, revisión comunicada `03c7fda` (PR #35) |
| URL HTTPS de Seguridad y issuer exacto | https://g7-auth-service.onrender.com; issuer `https://g7-auth-service.onrender.com/api/v1/auth` (publicado así en el descubrimiento, comprobado 17-oct UTC) |
| URL de descubrimiento y JWKS | `…/api/v1/auth/.well-known/openid-configuration` y `…/api/v1/auth/.well-known/jwks.json` del entorno real |
| URL HTTPS de Ventas y origen del frontend | Backend https://g5-servicio-ventas.onrender.com; frontend de G5 pendiente según su equipo |
| Orígenes CORS autorizados | G7: `https://g7-seguridad-spa.onrender.com`. Las llamadas G5→G7 de servidor no usan CORS |
| Revisión de contrato/kit consumida | Publicado main 5796622f7c039021e03dd25b2338c6a208320e05, v1 1.1.0/v2 parcial; G5 confirma revisión efectivamente consumida |
| UUID de clientes A/B y vendedor de prueba | A `…f00a`, B `…f00b` (CLIENTE), V `…f00c` (VENDEDOR), prefijo `00000000-0000-4000-8000-00000000`; activos según consulta de solo lectura |
| Pedidos de A, ningún pedido de B | A con dos pedidos (`PED_2026-00012`, `PED_2026-00013`), B sin pedidos |
| Sesión | Memoria en esta versión; recargar exige login. Iniciar sesión en G7 no transfiere el token a G5 |
| Prueba conjunta en nube | Ejecutada el 16-oct-2026 (Lima) por API; ver sección siguiente |
| Presentación | Sábado 17/10/2026 según la aclaración de Luis; hora no indicada |
| Margen de correcciones | Objetivo aproximado de dos días, o día y medio según las horas; no afirmar 48 horas exactas sin fijarlas |

No publicar secretos en este documento ni incluir tokens en URLs o registros.

## Coordinación con Ventas

Luis confirmó el recorrido y el objetivo jueves 15 en
[su comentario en G5 Backend #2](https://github.com/Taller-SW-Web/G5-Ventas-Postventas-Backend/issues/2#issuecomment-6118366251),
consultado 12-oct. Este acuse es una comunicación humana ya publicada; no
acredita endpoints implementados ni disponibilidad comprobada de G7.

La coordinación continúa en ese issue: URLs, issuer/JWKS, revisión consumida,
hora de prueba, UUID de fixtures y aceptación de H-11. Las credenciales se
entregan por canal privado. A10 conserva el recorrido de servicio; su catálogo
se ajusta por #44 retirando CSAT y por #42 retirando documento para Ventas,
sin afectar esta demo. El segundo ajuste está localmente preparado, por publicar.

## Trabajo que puede comenzar — sin cambiar dueños

| Dueño | Entrega necesaria para esta demo | Evidencia antes de prueba conjunta |
|---|---|---|
| Jose Luis — EP-02 | Login y sesión de SPEC-05/06 con reglas vigentes; consume roles, credenciales, bloqueo y auditoría de sus dueños | Login real exitoso/fallido, cuenta no disponible sin enumeración y token de acceso de 15 minutos |
| Sergio — EP-05 + SPEC-17/18 | Rol CLIENTE, firma RS256, descubrimiento/JWKS y configuración por entorno | Issuer exacto, JWKS del entorno y validación con el kit; no exigir aud nueva al usuario |
| Eva — EP-01 | Dos cuentas CLIENTE activas de pruebas, sin MFA voluntario; identidad UUID compartida con Ventas | Existencia y login comprobados; no inventar aceptación de términos; ningún secreto publicado |
| Christian — EP-06 e infraestructura | Despliegue, salud, comprobación y auditoría consumida por los dueños | HTTPS accesible, configuración aplicada, evidencia de eventos de login y prueba reproducible |
| Michael — Ventas F1/JWT | `/pedidos/me`, pantalla, pedidos persistidos y autorización | Rama enlazada en #2, identidad solo por sub, rol CLIENTE y servicio en nube |

Las dependencias siguen perteneciendo a sus dueños: política de Juan José,
bloqueo de Luis David y demás capacidades de las ocho épicas. La demo mínima
no cierra esas épicas ni divide su implementación por capas. Aquí se documenta
el orden; esta actualización no implementa capacidades ni crea cuentas.

**Orden:** fijar revisión contractual/configuración y responsables de entrega;
comprobar capacidades internas; entregar UUID y precargar pedidos; desplegar;
ejecutar prueba conjunta; corregir y ensayar. No esperar de nuevo una
confirmación del recorrido que ya dio Ventas. H-11, URLs, rama y hora siguen
requiriendo su información concreta.

## Aceptación de la demo

| Caso | Resultado esperado |
|---|---|
| Cliente A autenticado | 200; solo pedidos de A |
| Cliente B sin pedidos | 200; `items: []`, `total: 0`, paginación de su contrato |
| Sin token, vencido o firma alterada | 401 en Ventas |
| Usuario válido sin rol CLIENTE | 403 en Ventas |
| Identificador ajeno enviado por navegador | Nunca cambia el titular derivado de `sub`; no muestra datos ajenos |
| Recarga de página | Se pierde sesión en memoria; nuevo login |

Las [pruebas locales del kit con tokens firmados](validacion-hito-3.md)
pasaron en el punto 9. No ejecutan este recorrido ni prueban el filtrado de
pedidos; las llamadas en nube se registran en la sección siguiente.

## Prueba conjunta en nube — 16-oct-2026

Ejecución manual del equipo, documentada en `G7_G5_Integracion_API_H3.pdf`
(6 páginas, adjunto por el equipo; sin contraseñas, tokens ni claves).

| Componente | URL | Revisión |
|---|---|---|
| SPA G7 | https://g7-seguridad-spa.onrender.com | Contenido igual al árbol de `82c9414`; SHA del panel no obtenido |
| Backend G7 | https://g7-auth-service.onrender.com | Reportada `4bd7b61` (`develop`); el PR #55 no forma parte |
| Backend G5 | https://g5-servicio-ventas.onrender.com | Comunicada `03c7fda` (`main`) |

**Recorrido:** login real en la SPA de G7 → `accessToken` de la respuesta de
login → introducido de forma oculta en PowerShell → `GET /api/v1/pedidos/me`
en G5 con `Authorization: Bearer`. El token nunca fue en la URL. No había
pantalla de pedidos integrada en la SPA de G7. Se usaron **tokens de usuario**:
es independiente de los tokens de servicio y de los scopes pendientes de #51.

| Caso | Esperado | Observado | Evidencia |
|---|---|---|---|
| Sin token | 401 | 401 | E01 |
| Cliente A autenticado | 200; solo pedidos de A | 200; dos pedidos de A | E02 |
| Cliente B sin pedidos | 200; `items: []`, `total: 0` | 200; `items: []`, `total: 0`, `page: 1`, `pageSize: 10` | E03 |
| Usuario válido sin rol CLIENTE (V) | 403 | 403 | E04 |
| Firma alterada | 401 | 401; el token original dio 200 antes y después | E05 |
| Token vencido de B | 401 | 401 en el reintento; el primer intento tuvo un fallo de transporte y fue **no concluyente** | E06–E07 |
| B enviando `clienteId` de A | No cambia el titular | 200 con la lista vacía de B | E08 |
| Recarga de página | Nuevo login | No ejecutado en esta ronda | — |

**Alcance.** El caso E08 acredita ese parámetro en `/pedidos/me`; no demuestra
todas las rutas posibles de acceso a pedidos ajenos. La captura E07 no
conserva el claim `exp` ni el cuerpo del error; la atribución al vencimiento se
apoya en la secuencia. Las capturas prueban la ejecución del 16-oct, no
disponibilidad continua.

**Observación posterior (17-oct-2026, 04:48 y 04:54 UTC).** El backend G5
desplegado respondió `GET /api/v1/pedidos/me` con **200** y lista vacía tanto
**sin token** como con un token inválido (`Bearer abc.def.ghi`), en lugar del
401 de E01. Para entonces `main` de G5 ya había avanzado sobre `03c7fda`
(PRs #36–#38, fusionados entre 04:14 y 04:38 UTC). Los resultados de la tabla
corresponden a la revisión probada el 16-oct, no a la desplegada después. Se
debe avisar a G5 en G5 Backend #2 y repetir al menos los casos negativos antes
de presentar la integración como vigente.

## Evidencia revisada

- [Contrato Ventas §1.4b](https://github.com/Taller-SW-Web/G5-Ventas-Postventas/blob/73e44a22f4ce0ba265b308c318ad01bfa3e9298f/specs/api/api-ventas.md).
- [README de specs](https://github.com/Taller-SW-Web/G5-Ventas-Postventas/blob/73e44a22f4ce0ba265b308c318ad01bfa3e9298f/README.md#estado-del-proyecto) todavía dice implementación pendiente.
- [Backend separado](https://github.com/Taller-SW-Web/G5-Ventas-Postventas-Backend/tree/1f3e0b63ce446680b02f75384dd07af84b4d0bde): base publicada, con métodos pendientes y sin `/me` declarado en PedidoController. [Contraste puntual](correcciones-modulos.md#g5--ventas-y-postventa). No acredita despliegue.
- [A10 confirmado por Joseph](https://github.com/Taller-SW-Web/Modulo-de-Seguridad/issues/42#issuecomment-6116353534).
- [Acuerdo de G5 con Seguridad](https://github.com/Taller-SW-Web/G5-Ventas-Postventas/blob/73e44a22f4ce0ba265b308c318ad01bfa3e9298f/integraciones/ACUERDO_G7_SEGURIDAD.md).
- [Confirmación H3 de Joseph, Michael y fecha objetivo](https://github.com/Taller-SW-Web/G5-Ventas-Postventas-Backend/issues/2), consultada 12-oct; acuse de Luis enlazado en esta ficha.
- Aclaración de Luis en esta conversación: presentación el sábado y objetivo de disponibilidad el jueves para dejar margen de corrección.
- [Ficha del entorno G7](https://github.com/Taller-SW-Web/Modulo-de-Seguridad-Backend/blob/develop/docs/despliegue/DESPLIEGUE-T005.md#entorno-real-h3) y [evidencia TEC-INFRA-T005](https://github.com/Taller-SW-Web/Modulo-de-Seguridad-Backend/blob/develop/docs/evidencia/TEC-INFRA-T005.md) (B#56).
- `G7_G5_Integracion_API_H3.pdf` y `G7_Evidencias_Pruebas_H3.pdf`, ejecución manual del 16-oct-2026, adjuntos por el equipo.


## Revisión interna posterior — interfaces del recorrido conservadas

La revisión local 11-oct [v1 1.1.0/v2 parcial](../../specs/contrato-api/README.md)
corrige huecos de Seguridad; no se anuncia publicada. El recorrido propuesto
sigue consumiendo login/JWKS v1 existentes, sin depender de alta/MFA v2.
La aceptación del recorrido proviene de G5 Backend #2 y la decisión de G7;
esta revisión y A10 no acreditan ejecución.

Para las pantallas propias que consuman Retry-After desde otro origen, el
despliegue debe exponer esa cabecera mediante CORS. Configurar CORS solo para
orígenes acordados; no acredita configuración ni servicio ya desplegado.
