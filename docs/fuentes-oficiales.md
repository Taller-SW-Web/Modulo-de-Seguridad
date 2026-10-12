# Fuentes oficiales y revisión de referencia

**Corte:** 11 de octubre de 2026. **Coordinación:** Sergio Osorio.
**Alcance:** revisión de épicas y decisiones contractuales locales; sin publicación ni integración real.

## Referencia pública e interna

| Referencia | Revisión comprobada | Uso |
|---|---|---|
| `main` público de Seguridad | `fb4f6c7f8aee6ad72411e6096b7cadfbf3860dca` | Base pública hasta que el PO publique una revisión nueva |
| `origin/main` local | Misma revisión que el público | Comparación reproducible |
| Rama local `main` | `d19f408ed2dd5c6262112cae846440285deae02a` | Desactualizada; no usar como último contrato |
| Rama de trabajo `brenis`, antes de este bloque | `5fff5cd4a24690354dadc7e1f7e63f9e5d4bbfed` | Preparación interna, aún no publicada |

La base pública `specs/openapi.yaml` y el contrato local anterior a esta
edición tenían el mismo blob `fbd55c9664d1afe5c82c59852a289adca5a56038`.
La reorganización había cambiado rutas, no contenido. Ese original se
conserva byte a byte en [histórico 1.0.0](../specs/contrato-api/historico/openapi-1.0.0.yaml),
SHA-256 `4E4420FD1395DBF2A3A0B19F6BB7C8DCAE9091547B0D77C16EA17E7AF2C946E7`.

La autorización de Sergio comunicada por Luis permitió editar las revisiones
locales [v1 1.1.0](../specs/contrato-api/openapi.yaml) y
[v2 parcial 2.0.0](../specs/contrato-api/openapi-v2.yaml).
[ADR-008](arquitectura/adr/008-evolucion-mfa-y-alta-administrativa.md) fija su
alcance. No sustituyen la revisión pública hasta publicación y aviso.

La rama ya incluía el traslado de las specs, una guía UX/UI complementaria y
un ajuste de DBML: son cambios anteriores a esta preparación. Formarán parte
del diff de la rama al publicar. Se corrigió la errata `enum canal_otp {q`
del DBML, sin cambiar el DDL ni las migraciones; DAT-01 sigue pendiente.

- [Contrato público fijado](https://github.com/Taller-SW-Web/Modulo-de-Seguridad/blob/fb4f6c7f8aee6ad72411e6096b7cadfbf3860dca/specs/openapi.yaml).
- [Kit público de esa revisión](https://github.com/Taller-SW-Web/Modulo-de-Seguridad/blob/fb4f6c7f8aee6ad72411e6096b7cadfbf3860dca/specs/kit-integracion.md).
- [Contrato local](../specs/contrato-api/openapi.yaml).

Los cambios sin commit se revisan en el diff de trabajo. No existe todavía un
SHA nuevo para consumidores. Después de la revisión del PO se registra aquí
el SHA publicado, sus rutas y cambios. La definición local por edición delegada no acredita una versión
publicada ni aceptación de los consumidores.

## Qué gobierna cada asunto

| Asunto | Fuente | Responsable |
|---|---|---|
| Reparto académico | [Épicas](../specs/epicas/README.md) y [responsabilidades](responsabilidades.md) | Dueños funcionales; Sergio consolida |
| Reglas y escenarios | [Specs funcionales](../specs/funcionales/) | Dueño de épica; PO aprueba cambios |
| Peticiones, respuestas y seguridad pública | [OpenAPI](../specs/contrato-api/openapi.yaml) | Sergio conserva propiedad; edición delegada en este corte |
| Códigos y errores uniformes | [Catálogo de errores](../specs/catalogo-errores.md) | Sergio |
| Eventos y cargas útiles | [Catálogo de eventos](../specs/catalogo-eventos.md) | Dueños y Sergio |
| Arquitectura | [ADR](arquitectura/adr/) y [estados](arquitectura/estados-usuario.md) | Sergio y Jose Luis |
| Acuerdos externos | [Acuerdos](integracion/acuerdos.md), issue y confirmación | Sergio |
| Interfaz y componentes | [Interfaz](../specs/front/) y [React](../specs/componentes-react/) | Dueño completo de la funcionalidad |
| Vocabulario | [CONTEXT](../CONTEXT.md) | Sergio; conserva identificadores contractuales |
| Persistencia | [Modelo](arquitectura/modelo-datos.md), [DDL](arquitectura/schema.sql) y [migraciones](../backend/src/main/resources/db/migration/) | Dueños de dominio; Jose Luis coordina |
| Tareas y evidencia | [Plan](../plan/README.md) y [seguimiento](../plan/seguimiento-epicas.md) | Cada dueño |
| Problemas vigentes | [Registro de inconsistencias](registro-inconsistencias.md) | Cada dueño; Sergio consolida |

Estas fuentes gobiernan dimensiones distintas. Un conflicto entre ellas se
registra con impacto y decisión del PO: no se supone que la más reciente
invalida automáticamente a las otras. Kit, mockup o documento de un consumidor
no puede aprobar una capacidad nueva.

Migraciones y DDL no prueban que exista una funcionalidad desplegada. Una
divergencia del modelo descriptivo se registra aquí; no se aplica una
migración como parte de esta revisión.

## Históricos

[Plan de Hito 1](plan-hito-1.md) y [auditoría de septiembre](revision-inconsistencias.md)
conservan contexto de sus fechas. No gobiernan reparto ni inventario vigente.
No se borran antecedentes ni se cierra un problema actual por un cierre
histórico sin comprobarlo.

## Organización consultada de Marketplace

Revisión: `64cc824ec47ad2758bd657f01e5881165189e1f1`.

- [Separación de specs y planes](https://github.com/Taller-SW-Web/G1-Marketplace-SPECS/blob/64cc824ec47ad2758bd657f01e5881165189e1f1/plan/README.md).
- [Seguimiento y evidencia](https://github.com/Taller-SW-Web/G1-Marketplace-SPECS/blob/64cc824ec47ad2758bd657f01e5881165189e1f1/plan/seguimiento/README.md).
- [Reparto de semana 8](https://github.com/Taller-SW-Web/G1-Marketplace-SPECS/blob/64cc824ec47ad2758bd657f01e5881165189e1f1/plan/repartici%C3%B3n%20por%20integrantes/README.md).

Se adopta separación entre descripción, tareas y evidencia. No se copia su
reparto por capas: G7 tiene ocho dueños funcionales. Su catálogo antiguo
`SPECS/README.md` enlaza specs depreciadas; la ejecución se consulta en los
planes actuales.

**INT-01:** el H3 publicado por Marketplace prioriza F-006/F-007 con Productos
y excluye login. El recorrido con Marketplace no se presenta como aceptado para H3; la demo
actual se acordó con Ventas. No se cambia el alcance general por deducción.

## Ventas y preparación actual de Hito 3

Revisión pública contrastada de G5:
`73e44a22f4ce0ba265b308c318ad01bfa3e9298f`.
[Contrato de Ventas](https://github.com/Taller-SW-Web/G5-Ventas-Postventas/blob/73e44a22f4ce0ba265b308c318ad01bfa3e9298f/specs/api/api-ventas.md)
§1.4b define `GET /api/v1/pedidos/me`. La revisión inicial solo cubrió el
repositorio de specs; su README declara implementación pendiente. En la
continuación se encontró un [backend separado](https://github.com/Taller-SW-Web/G5-Ventas-Postventas-Backend/tree/1f3e0b63ce446680b02f75384dd07af84b4d0bde),
con base de aplicación y controladores pendientes, sin `/me` en
`PedidoController`. `SecurityConfig` conserva scopes anteriores. No afirmar
que no existe código; tampoco acreditar el endpoint ni un despliegue por esa
base. G5 debe confirmar su rama efectiva de trabajo.

A10 se respalda en el [comentario final de Joseph](https://github.com/Taller-SW-Web/Modulo-de-Seguridad/issues/42#issuecomment-6116353534).
A9 conserva condición según #33. #35 es un PR fusionado que documenta la
condición, no confirmación de Productos. G6 #87 C1 vuelve a pedirla y no tenía
respuestas en el corte consultado. [Matriz](integracion/matriz-scopes.md).

El recorrido antiguo de Marketplace es alcance general, no la prioridad de
este Hito 3. [Ficha de Ventas](integracion/hito-3-ventas.md) y
[preparación interna](integracion/preparacion-interna.md) son documentos locales,
no una revisión contractual publicada ni prueba de integración.


## Identificación del contenido local sin commit

No son SHA publicados. Versiones/cambios en [índice contractual](../specs/contrato-api/README.md).
El PO debe registrar el commit y el aviso cuando publique; estos hashes solo
identifican el contenido de esta revisión local.

| Archivo | SHA-256 del corte |
|---|---|
| openapi.yaml | `08BBAEADA53DAC3011DF5572234C0E93C7F49AD145C4B2A3D9EB07DF45010C1E` |
| openapi-v2.yaml | `C2B3D4712FE481072E96F097A357408E8D0FDB35229BE4926EFE0F53407F9511` |


## Fuentes contrastadas para comunicaciones — continuación 12-oct

Las revisiones fijadas se consultaron por GitHub MCP. La CLI `gh` no estaba
disponible; no hubo operaciones de escritura ni actualización de issues.

| Equipo / repositorio | Rama | Commit fijado |
|---|---|---|
| G1-Marketplace-SPECS | main | `64cc824ec47ad2758bd657f01e5881165189e1f1` |
| G2-Despacho-Specs | main | `e727f41ce76f9434e2099e02f2653de3c257c16d` |
| G3-Chatbot-specs | main | `ad4e7b597c8e3f2d143b5e30e2eea72655fdadd0` |
| G4-Retail-specs | main | `99fb06d634e1642673c2577e61528f4ab3ac70a1` |
| G5-Ventas-Postventas | main | `73e44a22f4ce0ba265b308c318ad01bfa3e9298f` |
| G5-Ventas-Postventas-Backend | main | `1f3e0b63ce446680b02f75384dd07af84b4d0bde` |
| G6-Productos-Ofertas-docs | master | `be5c2db16aca6d6d18b3005a2da4205f724038e3` |

[Correcciones y mensajes](integracion/correcciones-modulos.md) enlaza cada
archivo/línea afectada y distingue decisiones ya confirmadas de documentación
que sigue contradiciéndolas. #18 confirma Despacho aud/401/403; #19 registra
A5/A6; #20 registra la confirmación del issuer de Chatbot, aunque sus docs
fijadas aún publican el literal. #42 confirma A10. No se infiere aceptación
de H3 ni aprobación de A9 a partir de esas confirmaciones.

[Validación](integracion/validacion-hito-3.md) identifica el kit corregido,
hashes, versiones y límites. El kit del corte de ejecución anterior a CSAT tiene SHA-256
`62BC920E4E38874C3EB38C602A9E5556CB1055F75A8097F4792A753DE42E7807`.
El kit preparado tras #44 tiene SHA-256
`88A7D6D3D59F0BB72A83D10147AC5F99A06CEB139EF08BD2FA6BC58F3A06D219`.
Sus bloques de validación coinciden con los ejecutados. Los hashes de ambos
OpenAPI permanecen iguales al corte anterior.


## Confirmación de la demo de Ventas — 12-oct

Se localizó y consultó [G5 Backend #2](https://github.com/Taller-SW-Web/G5-Ventas-Postventas-Backend/issues/2),
publicado por JosephPM11. Confirma el recorrido, disponibilidad de Michael,
coordinación JWT por Michael y entrega G5 como máximo jueves 15/10/2026.
El enlace de rama/código se añadirá cuando esté listo. Luis ya publicó
[el acuse de G7](https://github.com/Taller-SW-Web/G5-Ventas-Postventas-Backend/issues/2#issuecomment-6118366251).
Esta sesión solo consultó esa comunicación humana.

Luis aclaró aquí que la presentación es el sábado y que hasta el jueves
debería estar listo para dejar aproximadamente dos días o día y medio de
corrección. Se adopta objetivo G7 jueves 15 y presentación sábado 17; faltan
las horas. [A11](integracion/acuerdos.md) y [ficha](integracion/hito-3-ventas.md)
registran estados y responsabilidades. No confundir objetivo con disponibilidad,
ni aceptación del recorrido con aceptación de H-11/configuración.

## Preparación de publicación y ajuste CSAT — 12-oct

Se reconfirmó main público de G7 en fb4f6c7f8aee6ad72411e6096b7cadfbf3860dca;
la rama remota brenis sigue en 80280e29d78e37eecdee8596307036b6761b44de.
Estos SHA no son una publicación nueva. El plan de commits conserva la rama
de trabajo y prepara un PR hacia main; subir brenis no actualiza main.
[Comandos y agrupación de commits](../plan/commits-publicacion.md).

G5 actualizó main a a6468b18bf18d4004c29a430ef275e359055e07c.
[Acuerdo actualizado](https://github.com/Taller-SW-Web/G5-Ventas-Postventas/blob/a6468b18bf18d4004c29a430ef275e359055e07c/integraciones/ACUERDO_G7_SEGURIDAD.md)
y [CSAT en api-postventas](https://github.com/Taller-SW-Web/G5-Ventas-Postventas/blob/a6468b18bf18d4004c29a430ef275e359055e07c/specs/api/api-postventas.md#L40)
retiran CSAT como scope. [#44](https://github.com/Taller-SW-Web/Modulo-de-Seguridad/issues/44)
y [aceptación de Luis](https://github.com/Taller-SW-Web/Modulo-de-Seguridad/issues/44#issuecomment-6118403027)
respaldan el retiro de las tres concesiones, sin reemplazo. G7 lo refleja
localmente: siete scopes propios de Ventas/Postventa; aún sin provisión.
Los detalles customerId y 403 por scope retirado de /pedidos/me persisten en
esa revisión de G5, contrastados nuevamente.

La referencia SDD de Marketplace se reconfirmó en
[plan/README](https://github.com/Taller-SW-Web/G1-Marketplace-SPECS/blob/64cc824ec47ad2758bd657f01e5881165189e1f1/plan/README.md)
y [seguimiento](https://github.com/Taller-SW-Web/G1-Marketplace-SPECS/blob/64cc824ec47ad2758bd657f01e5881165189e1f1/plan/seguimiento/README.md):
entrada funcional/UI/API/React, plan por funcionalidad y tareas con dependencia,
criterio de terminado y evidencia. Se toma como referencia para el siguiente
bloque; no cambia el dueño completo de las ocho épicas de G7.

## Continuación SDD-1/2 y commits locales observados

Durante la planificación se observaron cuatro commits locales nuevos:
`d2c49139c5ebd98bc1353eb2d6c1133ff4438936`,
`4178f9ef089510ad46ae7702966452585982f88c`,
`7c2b7845c793d3b33f80e8cd9fcb2c898a88514c` y
`1f743bf76445bbc70e1dc3ec94a21818dec9492b` (HEAD comprobado).
El agente no ejecutó staging/commit/push; se conserva ese historial. Parte de
la estructura SDD quedó incluida en el último. El complemento sigue pendiente
en el árbol de trabajo según [guía de commits](../plan/commits-publicacion.md).
Observar estos commits locales no acredita una revisión nueva en main público.

El [flujo SDD](../plan/flujo-sdd.md) y el
[plan técnico H3](../plan/PLAN-IMPLEMENTACION.md) preparan dos puntos del nuevo
bloque. Los planes individuales y tareas incrementales aún se redactarán;
se conservan las ocho épicas y se registran condiciones técnicas TEC-01–04.
Los dos OpenAPI mantienen sus hashes: este bloque no los modifica.
