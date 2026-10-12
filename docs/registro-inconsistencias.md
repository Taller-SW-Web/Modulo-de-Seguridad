# Registro vigente de inconsistencias

**Corte:** 11-oct-2026. **Coordinación:** Sergio Osorio. Inventario iniciado en los puntos 1/2 y actualizado con preparación interna
3–6. Continuación 12-oct: puntos 8/9, [mensajes preparados](integracion/correcciones-modulos.md)
y [validación local](integracion/validacion-hito-3.md). Actualización 12-oct:
Ventas aceptó el recorrido en [G5 Backend #2](https://github.com/Taller-SW-Web/G5-Ventas-Postventas-Backend/issues/2), confirmó a Michael y disponibilidad
el jueves 15; G7 adopta ese objetivo por indicación de Luis. La demo no está
implementada ni comprobada.
Definición del corte anterior publicada por PR #45/#46, main
5796622f7c039021e03dd25b2338c6a208320e05 (PUB-01). La publicación no acredita
servicio, aviso/adopción ni cierre de pruebas. Ajustes DOC-02/03 y SCP-03 de
esta revisión siguen locales. [Auditoría de 14 issues](integracion/estado-issues-20261012.md)
y [fuentes SDD](sdd-fuentes-y-aplicacion.md).

**Prioridad:** P0 bloquea el recorrido o su acuerdo; P1 bloquea una parte de una
épica; P2 permite avanzar con una limitación explícita. **Estados:** Resuelto
documental, Definido documental, Propuesta preparada (falta revisión/publicación),
Aplazado, Abierto, Pendiente externo y Por contrastar.

Los nombres abreviados corresponden a los dueños completos de
[responsabilidades](responsabilidades.md). Sergio publica los cambios de
contrato; el dueño funcional prepara la solución. Nadie queda asignado solo a
una capa. Para cerrar: enlazar decisión, revisión y comprobación reproducible;
una recomendación o un mock no bastan. No hay cierre automático por plazo.

## Correcciones de esta etapa

| ID | Prioridad | Evidencia y problema | Dueño | Tratamiento aplicado | Estado / criterio de cierre |
|---|---|---|---|---|---|
| ORG-01 | P0 | El reparto anterior de [responsabilidades](responsabilidades.md) trataba specs y capas como unidades de asignación | Sergio y los siete estudiantes | Ocho épicas; Luis David tiene dos, cada otro estudiante una | Resuelto documental: índice de ocho fichas y tareas verticales; revisión humana pendiente |
| ORG-02 | P1 | SPEC-02/03/11/16 y pantallas tenían responsables incompatibles con el nuevo reparto | Eva, Sergio y Valery; cada dueño revisa lo suyo | Metadatos, pantallas, componentes y trazabilidad reasignados, conservando RF/ESC y aprobaciones originales | Resuelto documental: comprobación de correspondencia épica/spec/pantalla |
| REF-01 | P1 | La reorganización dejó enlaces a OpenAPI y specs en sus rutas antiguas | Sergio | Enlaces y referencias de ruta actualizados; comandos de mock apuntan al contrato actual | Resuelto documental: comprobación de enlaces locales y revisión de comandos |
| BASE-01 | P0 | La rama local main estaba atrasada y la ruta del contrato difería de main público | Sergio | [Referencias fijadas](fuentes-oficiales.md): en el corte inicial, main público y origin/main coincidían y OpenAPI tenía el mismo blob en ambas rutas | Resuelto documental: SHA públicos y locales comprobados; actualización publicada en PUB-01; referencias locales no actualizadas con fetch |
| PUB-01 | P1 | PR #45/#46 fusionados; índices aún decían sin publicar | Sergio | Fuente pública fijada en 5796622f7c039021e03dd25b2338c6a208320e05; índices/estados documentales alineados; SHA local b9f7673 conservado sin fetch/merge automático | Publicación anterior comprobada; ajuste de índices local por publicar. Aviso/adopción externos siguen GOV-01/INT-05 |
| DOC-01 | P2 | Glosario, orientación, históricos y modelo descriptivo conservaban reparto/estado anterior | Sergio y dueños | Glosario épica/spec, fuentes por dimensión, banners históricos y descripción de A6/A7 alineados | Resuelto documental: enlaces válidos; cambios físicos fuera de esta etapa |
| DOC-02 | P1 | SPEC-18 prometía permisos de APIs consumidoras en contradicción con RF-11.9/A5 | Sergio | Dependencias/catálogo de SPEC-18 limitados a permisos de Seguridad; sin cambio de wire ni claims | Resuelto documental local 12-oct; por publicar. Consultas/servicios conservan dependencias de implementación |
| DOC-03 | P1 | ESC-17.5 usaba Ventas con introspección y activo:true tras cambios de rol; contradice A10/RF-17.7 | Sergio | Escenario de consumidor autorizado como Productos, con roles sin cambios; cambio de rol aplica activo:false de RF-17.7; OpenAPI generado se contrasta con fuente aprobada | Resuelto documental local 12-oct; por publicar, sin cambio de wire |

## Huecos internos: diagnóstico y trabajo pendiente

Los H-01 a H-15 conservan el diagnóstico de origen de
[interfaz](../specs/front/README.md#huecos-detectados-en-el-backend-y-el-contrato).
Este registro gobierna su seguimiento. Los [contratos](../specs/contrato-api/README.md)
contienen definición publicada v1 1.1.0 y v2 parcial. La base 1.0.0 permanece intacta en histórico.

| ID | Prioridad | Evidencia / contradicción | Dueño | Tratamiento previsto | Estado / criterio de cierre |
|---|---|---|---|---|---|
| H-01 | P1 | Usuario omite mfaHabilitado y atributos definidos por SPEC-10/16 | Luis David (MFA), Valery (atributos), Sergio (contrato) | PerfilUsuario exige mfaHabilitado; atributos privados al titular/admin por rol; servicio los omite (v1 1.1.0 publicada) | Definido documental: contratos y ejemplos validados; implementación pendiente; definición publicada |
| H-02 | P1 | SPEC-10 exige confirmar activación/desactivación; el contrato no completa ambas confirmaciones | Luis David; Sergio publica | MFA seguro v2: solicitud/reenvío/confirmación vinculados a usuario/familia/acción; rutas v1 históricas no desplegables | Definido documental en SPEC-10/FRONT-09 y ADR-008; DAT-01, servicios pendientes; definición publicada |
| H-03 | P1 | Cambio de celular en SPEC-16 remite a desafío de login; A2 excluye verificación externa | Valery y Luis David | SPEC-10/16 y contrato alineados con A2: celular no verificado; OTP de login no verifica contactos | Resuelto documental de alcance; sin verificación externa |
| H-04 | P2 | SPEC-16/FRONT-06 esperan gestión de direcciones más amplia que GET/POST publicados | Valery | Solo listar/crear y predeterminada al crear; pantalla y spec coherentes | Aplazado: edición/borrado fuera de primera versión; conservan dueño sin endpoints inventados |
| H-05 | P1 | ESC-09.7 necesita tiempo de reintento; 429 no lo proporciona | Luis David; Eva/Juan consumen límites | Retry-After definido en todas las respuestas 429 de v1 y v2 según ventanas existentes | Definido documental y validado; comportamiento del servidor pendiente; definición publicada |
| H-06 | P1 | SPEC-09 no precisa invalidación por reenvío ni SMS sin celular | Luis David | Reenvío invalida OTP anterior sin extender desafío; SMS sin celular 400 VALIDACION tras validar desafío y sin envío/cuota | Definido documental en SPEC-09 y v1; servidor pendiente |
| H-07 | P1 | SPEC-12 no fija detalle de bloqueo; consulta permite una acción por petición | Christian y Luis David | SPEC-12: hasta null para manual indefinido o automático desde cuarto consecutivo; filtro de una acción | Definido documental; persistencia/auditoría en implementación pendientes |
| H-08 | P2 | SPEC-13 pide filtros equivalentes; exportación omite actorId/resultado/ip | Christian | Exportación aplazada de H3; sin botones ni llamadas; consulta conserva filtros publicados | Aplazado a preparación Hito 4: alinear filtros antes de implementar exportación; no se completa la épica |
| H-09 | P2 | FRONT-07/08 necesitan etiquetas; GET /roles solo admite servicio | Sergio | Pantallas usan catálogo canónico; ninguna credencial de servicio en navegador | Resuelto documental; comprobar código cuando exista |
| H-10 | P1 | CrearUsuarioRequest.rol permite CLIENTE, pero RF-03.1 lo excluye | Eva; Sergio publica | Schema estricto de alta en v2: cinco roles, opcional, sin CLIENTE/contraseña; CodigoRol global conservado | Definido documental; schema probado; activación/H-12 y servidor pendientes; definición publicada |
| H-11 | P1 | SPEC-06 no fija persistencia del refresco; componentes prescriben memoria sin decisión común | Jose Luis; Sergio coordina con Ventas | SPEC-06/FRONT-00/kit: tokens e identidad en memoria, recarga exige login, refresh serializado | Definido documental para esta versión; aceptación de consumo por Ventas pendiente en INT-05 |
| H-12 | P1 | RF-01.4 guarda aceptación/versionado de términos; no existe fuente común documentada | Eva; Sergio coordina | No se aportó definición; Luis no identifica una fuente; texto/versión/fuente pendientes; registro y activación fuera de demo mínima con Ventas; no inventar consentimiento de fixtures | Abierto: PO define términos antes de implementar registro/activación; no bloquea login de cuentas de prueba ya preparadas |
| H-13 | P1 | RF-16.2 limita edición propia; AtributosUsuario acepta documento | Valery | Titular edita solo nombres/apellidos/celular; documento y atributos de rol con atributos.editar; SPEC-11/16 y contrato alineados | Definido documental; prueba real de denegación/privacidad pendiente |
| H-14 | P2 | SPEC-15/FRONT-12 no definen indicación de soporte para bloqueo manual | Luis David | Texto provisional de contacto con administrador en flujo adecuado; no modificar login genérico | Abierto: No se aportó contacto; confirmar canal de soporte; no inventar correo/URL |
| H-15 | P2 | FRONT-05 no puede consultar vigencia del enlace antes de enviar | Juan José | Vencimiento/uso se maneja al enviar formulario, con errores de recuperación existentes; no preconsulta | Resuelto documental de alcance; verificación de UI en implementación |
| UI-01 | P1 | [FRONT-11](../specs/front/FRONT-11-admin-alta-usuario.md) pide contraseña/alta activa; [A6](integracion/acuerdos.md) exige enlace y cuenta pendiente | Eva | FRONT-11/componentes sin contraseña, rol opcional, cuenta pendiente/enlace 72 h; detalle solo con usuario.ver | Resuelto documental para alta; pantalla de activación A6 aún necesita spec antes de implementarse |
| DAT-01 | P1 | Modelo/DDL/DBML/migraciones no acreditan esquema aplicado; diferencias de datos y capacidades internas | Dueños de tablas; Christian coordina cadena | [Diseño H3 cerrado](../plan/hito-3/DISENO-PERSISTENCIA.md): mappings, familia/acceso, OTP/revisión, huellas, contexto de enlace y outbox; versiones previstas posteriores a V100 | Diseño H3 resuelto por delegación; contraste de Supabase, migraciones/servicios/pruebas pendientes. Cambio MFA y equivalencia del modelo completo posteriores |

## Kit, arquitectura y acuerdos

| ID | Prioridad | Evidencia / contradicción | Dueño | Tratamiento previsto | Estado / criterio de cierre |
|---|---|---|---|---|---|
| KIT-01 | P0 | [Kit §1](../specs/kit-integracion.md) declara issuer de descubrimiento, pero Node/.NET validan auth-service | Sergio | Tres ejemplos con issuer configurado, RS256 y perfil usuario; .NET usa descubrimiento, no JWKS en MetadataAddress | Resuelto documental y comprobado: Node ejecutado, Spring/.NET compilados; 88 casos firmados pasaron. Servidor pendiente; definición publicada |
| KIT-02 | P1 | Kit/contrato describen reúso revocando todas las sesiones; [SPEC-06](../specs/funcionales/SPEC-06-renovacion-cierre-sesion.md) y glosario limitan a familia/sesión | Jose Luis; Sergio publica | Kit y v1 corrigen revocación de familia/sesión afectada, conservando sesiones independientes | Resuelto documental; prueba real con otra sesión pendiente; definición publicada |
| KIT-03 | P1 | Tabla de errores del kit atribuye CUENTA_NO_DISPONIBLE a login, aunque su nota final y catálogo lo excluyen | Sergio y Jose Luis | Tabla corregida: CUENTA_NO_DISPONIBLE administrativo, login 401 CREDENCIALES_INVALIDAS | Resuelto documental, sin enumeración |
| KIT-04 | P1 | Retorno de Retail en ejemplos apunta a Marketplace, frente a retorno por canal de A1 | Eva; Sergio publica | Kit y v1: retorno al login configurado de cada canal, sin token en URL | Resuelto documental; pruebas reales de retornos pendientes |
| KIT-05 | P1 | Pruebas firmadas: ejemplos aceptaban jti vacío/no textual, iat/jti ausentes o roles mal tipados según librería | Sergio | Node exige jti textual; Spring comprueba iat antes de conversión; .NET valida JSON original, incluyendo roles array vacío | Resuelto documental: 88 resultados esperados en cuatro recorridos; detalles en informe del punto 9. Solo ejemplos, servidor pendiente |
| JWT-01 | P0 | SPEC-17 define aud para servicio; consumidores validan usuario sin política documentada de aud | Sergio y Jose Luis | Perfil usuario sin aud nueva exigida; UUID/tipo/issuer/RS256; servicio sí aud de API, validadores separados | Definido documental y probado en kit: 88 resultados esperados, incluidos servicio/refresco como usuario; validación del servidor pendiente; definición publicada |
| EVT-01 | P1 | [Trazabilidad](../specs/trazabilidad.md) enumera consumidores previstos; esto no confirma todas sus suscripciones | Christian; Sergio coordina | Matriz distingue catálogo de suscripción; no tratar consumidores previstos como suscritos | Pendiente externo: subconjunto/issue/aprobación/cola por consumidor |
| SCP-01 | P1 | A4 crece a scopes de APIs ajenas; A9 está condicionado y A10 aparece en informe de Ventas | Sergio | Matriz por concesión; A10 respaldado #42, A9 condicionado #33; documento para Ventas retirado en SCP-03; fuentes de Despacho y catálogo de Productos contrastados | Definido documental: aprobación separada de provisión; Despacho confirma en contrato y #18; A9 pendiente en #33/G6 #87 C1; ningún cliente creado |
| SCP-03 | P1 | Joseph retiró usuarios:leer:documento de modulo-ventas en [#42](https://github.com/Taller-SW-Web/Modulo-de-Seguridad/issues/42#issuecomment-6118843852), después del corte publicado | Sergio | Fila provisional retirada sin reemplazo; kit/matriz/acuerdos/SPEC-18 alineados; catálogo genérico conservado | Resuelto documental local 12-oct; ajuste por publicar, no afecta demo H3 ni acredita provisión |
| SCP-02 | P1 | #44 retira postventa:csat:escribir; antes estaba concedido a Marketplace, Chatbot y Retail | Sergio; PO de Ventas define su API | Retirar las tres concesiones sin reemplazo; siete scopes propios; CSAT usa token CLIENTE y propiedad por sub en Postventa | Resuelto documental por [aceptación de Luis en #44](https://github.com/Taller-SW-Web/Modulo-de-Seguridad/issues/44#issuecomment-6118403027); catálogo/kit publicados por PR #45; provisión no acreditada |
| GOV-01 | P1 | [Kit §12](../specs/kit-integracion.md) exige una semana de aviso; usuario indica presentación en cinco días | Sergio (PO) | ADR-008 mantiene aviso de una semana; sin excepción. Demo Ventas usa login/JWKS v1 existentes | Definido documental; definición publicada; aviso/coordinación externos pendientes |

## Coordinación con los seis consumidores

Los seis equipos se contrastaron con archivos públicos y revisiones fijadas
en [fuentes](fuentes-oficiales.md). El informe aportado por el usuario conserva
su valor como diagnóstico inicial; no se repite una observación sin contraste.
[Mensajes del punto 8](integracion/correcciones-modulos.md) distingue fuentes
ya alineadas, contradicciones vigentes y aceptación/provisión pendientes.
El agente no publicó mensajes ni issues. A10 no confirma por sí solo la demo
H3; la confirmación posterior de Ventas y el acuse humano de G7 se registran
en INT-05 y A11.

| ID | Prioridad | Evidencia / expectativa que comprobar | Dueño | Tratamiento previsto | Estado / criterio de cierre |
|---|---|---|---|---|---|
| INT-01 | P1 | Mocks G1: register/recover-password/reset-password, email/password, HS256 y CUST-8842; plan H3 prioriza Productos | Sergio y PO de Marketplace | Mensaje G1 preparado con rutas/campos/RS256/UUID y recorrido general; no exigir demo G7 en su H3 | Contrastado: envío y corrección pendientes; fuera del recorrido H3 de Ventas |
| INT-02 | P1 | Contrato principal G2 alineado; ES-F05-05 L21, ES-F03-01 L17 y modelo L207 aún contradicen A5/A6 | Sergio y PO de Despacho | Mensaje G2 preparado: perfil local, alta por administrador, roles vacíos, vínculo/activación y suscripciones aparte | Contrastado: #18 confirma aud/401/403 y #19 A5/A6; correcciones y suscripciones pendientes |
| INT-03 | P1 | #20 registra corrección confirmada, pero contratos G3 L24/spec inicio-sesion L166 publican auth-service; A4/A11 esperan rol ficticio | Sergio y PO de Chatbot | Mensaje G3 preparado: alinear docs con confirmación, retirar SERVICIO_INTEGRACION; separar A3, A9 y A10 | Contrastado: corrección documental/envío pendientes; no se afirma fallo de su implementación |
| INT-04 | P1 | Contrato consolidado G4 reconoce A7; generales/api-contracts y F03 mantienen formatos/rutas/estado y búsqueda contradictorios | Sergio y PO de Retail | Mensaje G4 preparado: declarar fuente vigente, completar payload/MFA, activar por enlace y no delegar RUC | Contrastado: envío y correcciones pendientes; ocho horas no corroborado en fuentes fijadas |
| INT-05 | P0 | G5 Backend #2 confirma recorrido, Michael y entrega jueves 15; A10 sigue separado; rama de /pedidos/me pendiente | Sergio/Joseph; Jose Luis/Eva/Christian y Michael | Ficha A11 actualizada; acuse humano de G7 publicado; G7 adopta objetivo jueves 15 para presentar sábado 17, sin acreditar disponibilidad | Recorrido y acuse de G7 confirmados con evidencia; rama, URLs, hora, H-11, implementación y prueba en nube pendientes |
| INT-06 | P1 | OpenAPI 0.5.0 de G6 publica 16 scopes pendientes, incluidos siete A9 y productos:fisicos:leer; #87 C1 pide confirmación sin respuesta | Sergio y PO de Productos | Mensaje G6 preparado; confirmación trazable del PO por cliente/scope/aud antes de provisión | Contrastado: condición A9 y otras concesiones siguen pendientes; catálogo no acredita grant |

## Pendientes técnicos identificados al planificar — 12-oct

El [plan H3](../plan/PLAN-IMPLEMENTACION.md) define el recorrido, no crea
recursos ni acredita configuración. Estas decisiones se mantienen aquí;
la [vista del plan](../plan/DECISIONES-Y-BLOQUEOS.md) solo las referencia.

| ID | Prioridad | Evidencia / pendiente | Dueño | Tratamiento | Estado / criterio de cierre |
|---|---|---|---|---|---|
| TEC-01 | P0 | Supabase confirmado por Luis como DB G7; proyecto/esquema/historial accesibles y URLs de servicios no comprobados | Christian; Jose Luis; Sergio coordina G5 | [JDBC/entorno cerrado](../plan/hito-3/ENTORNO-Y-REACT.md), runtime/migrador separados y revisión de baseline-on-migrate actual antes de nube | Proveedor y diseño resueltos; conexión al proyecto correcto, grants/configuración y despliegue pendientes. No se creó ni modificó recurso |
| TEC-02 | P0 | V100 no demuestra credenciales válidas ni fixtures A/B/V cargadas; no mezclar ensayos de bloqueo/cambio con cuentas G5 | Eva; Juan José/Sergio/Luis por capacidades; Christian coordina | [Protocolo cerrado](../plan/hito-3/ENTORNO-Y-REACT.md): UUID estables, escritura de cada tabla por su dueño, credenciales privadas y cuentas separadas, sin inventar consentimiento | Diseño resuelto; carga autorizada, login real y coordinación UUID con G5 pendientes |
| TEC-03 | P0 | No se acredita par RSA/kid, issuer ni custodia efectiva de secretos de backend | Sergio; Christian en custodia/despliegue | [Diseño cerrado](../plan/hito-3/ENTORNO-Y-REACT.md): RSA3072 persistente/PKCS8, kid público, Nimbus RS256 y claves externas separadas para HMAC/AEAD | Diseño resuelto; claves/configuración reales y continuidad/reinicio pendientes; rotación completa posterior |
| TEC-04 | P1 | Java21/Boot3.3.5 del repo difieren del runtime de pruebas previas del kit; frontend aún no tiene manifiesto | Jose Luis/Sergio; Christian verifica | [Stack cerrado](../plan/hito-3/ENTORNO-Y-REACT.md): BOM existente, JOSE/Mail/AMQP; Node/React/TS/Vite/Mantine elegidos y lockfile en arranque | Diseño resuelto; dependency:tree/build y pruebas del código propio en runtime efectivo pendientes, sin atribuirle pruebas de JDK23 |
| EVT-02 | P1 | H3 adelanta mutaciones de roles/bloqueo/perfil y sus RF de eventos respecto al calendario anterior | Sergio; Christian y productores | Calendario del incremento aprobado según comunicación de Luis; outbox, worker/confirmación y payloads canónicos cerrados en planes | Diseño H3 aprobado; publicación/implementación real y suscripciones externas siguen pendientes, sin cambiar payload |

## Revisión y cierre de esta etapa

El [verificador documental](../scripts/verificar-documentacion.py) comprueba
referencias locales y correspondencia del reparto. Las comprobaciones no
resuelven huecos funcionales ni prueban tokens/despliegues. La validación
adicional del punto 9 sí comprobó tokens firmados contra ejemplos del kit y
formas mediante mocks; definición anterior publicada, implementación y servicios siguen pendientes. La referencia pública sigue siendo el SHA fijado en fuentes:
parte de la preparación ya consta en los cuatro commits locales observados en
[fuentes](fuentes-oficiales.md). Los ajustes de SDD de este bloque siguen en
el árbol de trabajo; un commit local no acredita publicación o servicio.


## Evidencia del bloque 8/9 — 12-oct

[Informe](integracion/validacion-hito-3.md) y
[casos](integracion/evidencia-validacion-hito-3.md): tres documentos OpenAPI
3.1 válidos, 178 ejemplos, 42 operaciones v1 conservadas, 15 casos de schema,
68 peticiones HTTP mock y 88 comprobaciones de validadores con fixtures firmadas.

Seis comunicaciones preparadas, ninguna enviada. DAT-01/H-12/H-14 y pendientes
externos mantienen sus estados. No hay cierre de pruebas reales de login,
OTP, auditoría, aislamiento de pedidos ni despliegue por estos resultados.

## Cierre técnico del plan H3 — 12-oct-2026

Luis comunica aprobación formal de los siete incrementos y delegación de detalles
internos. [Cierre del plan](../plan/hito-3/CIERRE-PLAN.md) registra alcance y
[antes de codificar](../plan/hito-3/ANTES-DE-CODIFICAR.md) distingue diseño
resuelto de pruebas/configuración pendientes. No se cerraron issues remotos ni
se crearon tareas o migraciones por esta actualización.
