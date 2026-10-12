# Registro vigente de inconsistencias

**Corte:** 11-oct-2026. **Coordinación:** Sergio Osorio. Inventario iniciado en los puntos 1/2 y actualizado con preparación interna
3–6. Continuación 12-oct: puntos 8/9, [mensajes preparados](integracion/correcciones-modulos.md)
y [validación local](integracion/validacion-hito-3.md). Actualización 12-oct:
Ventas aceptó el recorrido en [G5 Backend #2](https://github.com/Taller-SW-Web/G5-Ventas-Postventas-Backend/issues/2), confirmó a Michael y disponibilidad
el jueves 15; G7 adopta ese objetivo por indicación de Luis. La demo no está
implementada ni comprobada.
Edición contractual local delegada según autorización comunicada por Luis; no acredita publicación, servicio ni aceptación externa.

**Prioridad:** P0 bloquea el recorrido o su acuerdo; P1 bloquea una parte de una
épica; P2 permite avanzar con una limitación explícita. **Estados:** Resuelto
local (sin publicar), Propuesta preparada (falta aprobación/publicación),
Aplazado, Abierto, Pendiente externo y Por contrastar.

Los nombres abreviados corresponden a los dueños completos de
[responsabilidades](responsabilidades.md). Sergio publica los cambios de
contrato; el dueño funcional prepara la solución. Nadie queda asignado solo a
una capa. Para cerrar: enlazar decisión, revisión y comprobación reproducible;
una recomendación o un mock no bastan. No hay cierre automático por plazo.

## Correcciones de esta etapa

| ID | Prioridad | Evidencia y problema | Dueño | Tratamiento aplicado | Estado / criterio de cierre |
|---|---|---|---|---|---|
| ORG-01 | P0 | El reparto anterior de [responsabilidades](responsabilidades.md) trataba specs y capas como unidades de asignación | Sergio y los siete estudiantes | Ocho épicas; Luis David tiene dos, cada otro estudiante una | Resuelto local: índice de ocho fichas y tareas verticales; revisión humana pendiente |
| ORG-02 | P1 | SPEC-02/03/11/16 y pantallas tenían responsables incompatibles con el nuevo reparto | Eva, Sergio y Valery; cada dueño revisa lo suyo | Metadatos, pantallas, componentes y trazabilidad reasignados, conservando RF/ESC y aprobaciones originales | Resuelto local: comprobación de correspondencia épica/spec/pantalla |
| REF-01 | P1 | La reorganización dejó enlaces a OpenAPI y specs en sus rutas antiguas | Sergio | Enlaces y referencias de ruta actualizados; comandos de mock apuntan al contrato actual | Resuelto local: comprobación de enlaces locales y revisión de comandos |
| BASE-01 | P0 | La rama local main estaba atrasada y la ruta del contrato difería de main público | Sergio | [Referencias fijadas](fuentes-oficiales.md): main público y origin/main coinciden; OpenAPI tiene el mismo blob en ambas rutas | Resuelto local: SHA públicos y locales comprobados; nueva publicación pendiente |
| DOC-01 | P2 | Glosario, orientación, históricos y modelo descriptivo conservaban reparto/estado anterior | Sergio y dueños | Glosario épica/spec, fuentes por dimensión, banners históricos y descripción de A6/A7 alineados | Resuelto local: enlaces válidos; cambios físicos fuera de esta etapa |

## Huecos internos: diagnóstico y trabajo pendiente

Los H-01 a H-15 conservan el diagnóstico de origen de
[interfaz](../specs/front/README.md#huecos-detectados-en-el-backend-y-el-contrato).
Este registro gobierna su seguimiento. Los [contratos](../specs/contrato-api/README.md)
contienen revisión local v1 1.1.0 y v2 parcial. La base 1.0.0 permanece intacta en histórico.

| ID | Prioridad | Evidencia / contradicción | Dueño | Tratamiento previsto | Estado / criterio de cierre |
|---|---|---|---|---|---|
| H-01 | P1 | Usuario omite mfaHabilitado y atributos definidos por SPEC-10/16 | Luis David (MFA), Valery (atributos), Sergio (contrato) | PerfilUsuario exige mfaHabilitado; atributos privados al titular/admin por rol; servicio los omite (v1 1.1.0 local) | Definido local: contratos y ejemplos validados; implementación/publicación pendientes |
| H-02 | P1 | SPEC-10 exige confirmar activación/desactivación; el contrato no completa ambas confirmaciones | Luis David; Sergio publica | MFA seguro v2: solicitud/reenvío/confirmación vinculados a usuario/familia/acción; rutas v1 históricas no desplegables | Definido local en SPEC-10/FRONT-09 y ADR-008; DAT-01, publicación y servicios pendientes |
| H-03 | P1 | Cambio de celular en SPEC-16 remite a desafío de login; A2 excluye verificación externa | Valery y Luis David | SPEC-10/16 y contrato alineados con A2: celular no verificado; OTP de login no verifica contactos | Resuelto local de alcance; sin verificación externa |
| H-04 | P2 | SPEC-16/FRONT-06 esperan gestión de direcciones más amplia que GET/POST publicados | Valery | Solo listar/crear y predeterminada al crear; pantalla y spec coherentes | Aplazado: edición/borrado fuera de primera versión; conservan dueño sin endpoints inventados |
| H-05 | P1 | ESC-09.7 necesita tiempo de reintento; 429 no lo proporciona | Luis David; Eva/Juan consumen límites | Retry-After definido en todas las respuestas 429 de v1 y v2 según ventanas existentes | Definido local y validado; comportamiento del servidor/publicación pendientes |
| H-06 | P1 | SPEC-09 no precisa invalidación por reenvío ni SMS sin celular | Luis David | Reenvío invalida OTP anterior sin extender desafío; SMS sin celular 400 VALIDACION tras validar desafío y sin envío/cuota | Definido local en SPEC-09 y v1; servidor pendiente |
| H-07 | P1 | SPEC-12 no fija detalle de bloqueo; consulta permite una acción por petición | Christian y Luis David | SPEC-12: hasta null para manual indefinido o automático desde cuarto consecutivo; filtro de una acción | Definido local; persistencia/auditoría en implementación pendientes |
| H-08 | P2 | SPEC-13 pide filtros equivalentes; exportación omite actorId/resultado/ip | Christian | Exportación aplazada de H3; sin botones ni llamadas; consulta conserva filtros publicados | Aplazado a preparación Hito 4: alinear filtros antes de implementar exportación; no se completa la épica |
| H-09 | P2 | FRONT-07/08 necesitan etiquetas; GET /roles solo admite servicio | Sergio | Pantallas usan catálogo canónico; ninguna credencial de servicio en navegador | Resuelto local documental; comprobar código cuando exista |
| H-10 | P1 | CrearUsuarioRequest.rol permite CLIENTE, pero RF-03.1 lo excluye | Eva; Sergio publica | Schema estricto de alta en v2: cinco roles, opcional, sin CLIENTE/contraseña; CodigoRol global conservado | Definido local; schema probado; publicación y activación/H-12 pendientes |
| H-11 | P1 | SPEC-06 no fija persistencia del refresco; componentes prescriben memoria sin decisión común | Jose Luis; Sergio coordina con Ventas | SPEC-06/FRONT-00/kit: tokens e identidad en memoria, recarga exige login, refresh serializado | Definido local para esta versión; aceptación de consumo por Ventas pendiente en INT-05 |
| H-12 | P1 | RF-01.4 guarda aceptación/versionado de términos; no existe fuente común documentada | Eva; Sergio coordina | No se aportó definición; Luis no identifica una fuente; texto/versión/fuente pendientes; registro y activación fuera de demo mínima con Ventas; no inventar consentimiento de fixtures | Abierto: PO define términos antes de implementar registro/activación; no bloquea login de cuentas de prueba ya preparadas |
| H-13 | P1 | RF-16.2 limita edición propia; AtributosUsuario acepta documento | Valery | Titular edita solo nombres/apellidos/celular; documento y atributos de rol con atributos.editar; SPEC-11/16 y contrato alineados | Definido local; prueba real de denegación/privacidad pendiente |
| H-14 | P2 | SPEC-15/FRONT-12 no definen indicación de soporte para bloqueo manual | Luis David | Texto provisional de contacto con administrador en flujo adecuado; no modificar login genérico | Abierto: No se aportó contacto; confirmar canal de soporte; no inventar correo/URL |
| H-15 | P2 | FRONT-05 no puede consultar vigencia del enlace antes de enviar | Juan José | Vencimiento/uso se maneja al enviar formulario, con errores de recuperación existentes; no preconsulta | Resuelto local de alcance; verificación de UI en implementación |
| UI-01 | P1 | [FRONT-11](../specs/front/FRONT-11-admin-alta-usuario.md) pide contraseña/alta activa; [A6](integracion/acuerdos.md) exige enlace y cuenta pendiente | Eva | FRONT-11/componentes sin contraseña, rol opcional, cuenta pendiente/enlace 72 h; detalle solo con usuario.ver | Resuelto local para alta; pantalla de activación A6 aún necesita spec antes de implementarse |
| DAT-01 | P1 | [Modelo](arquitectura/modelo-datos.md) conserva numeración incluyendo vista y descripciones anteriores; DDL/DBML/migraciones necesitan contraste completo | Dueños de datos; Jose Luis coordina | [Divergencias concretas](integracion/revision-datos.md): V5 declarada ausente, OTP/desafío distintos, propósito de cambio MFA no modelado | Abierto: nueva migración/diseño por dueño; sin editar SQL existente ni acreditar base aplicada |

## Kit, arquitectura y acuerdos

| ID | Prioridad | Evidencia / contradicción | Dueño | Tratamiento previsto | Estado / criterio de cierre |
|---|---|---|---|---|---|
| KIT-01 | P0 | [Kit §1](../specs/kit-integracion.md) declara issuer de descubrimiento, pero Node/.NET validan auth-service | Sergio | Tres ejemplos con issuer configurado, RS256 y perfil usuario; .NET usa descubrimiento, no JWKS en MetadataAddress | Resuelto local y comprobado: Node ejecutado, Spring/.NET compilados; 88 casos firmados pasaron. Publicación y servidor pendientes |
| KIT-02 | P1 | Kit/contrato describen reúso revocando todas las sesiones; [SPEC-06](../specs/funcionales/SPEC-06-renovacion-cierre-sesion.md) y glosario limitan a familia/sesión | Jose Luis; Sergio publica | Kit y v1 corrigen revocación de familia/sesión afectada, conservando sesiones independientes | Resuelto local documental; prueba real con otra sesión y publicación pendientes |
| KIT-03 | P1 | Tabla de errores del kit atribuye CUENTA_NO_DISPONIBLE a login, aunque su nota final y catálogo lo excluyen | Sergio y Jose Luis | Tabla corregida: CUENTA_NO_DISPONIBLE administrativo, login 401 CREDENCIALES_INVALIDAS | Resuelto local documental, sin enumeración |
| KIT-04 | P1 | Retorno de Retail en ejemplos apunta a Marketplace, frente a retorno por canal de A1 | Eva; Sergio publica | Kit y v1: retorno al login configurado de cada canal, sin token en URL | Resuelto local documental; pruebas reales de retornos pendientes |
| KIT-05 | P1 | Pruebas firmadas: ejemplos aceptaban jti vacío/no textual, iat/jti ausentes o roles mal tipados según librería | Sergio | Node exige jti textual; Spring comprueba iat antes de conversión; .NET valida JSON original, incluyendo roles array vacío | Resuelto local: 88 resultados esperados en cuatro recorridos; detalles en informe del punto 9. Solo ejemplos, servidor pendiente |
| JWT-01 | P0 | SPEC-17 define aud para servicio; consumidores validan usuario sin política documentada de aud | Sergio y Jose Luis | Perfil usuario sin aud nueva exigida; UUID/tipo/issuer/RS256; servicio sí aud de API, validadores separados | Definido local y probado en kit: 88 resultados esperados, incluidos servicio/refresco como usuario; validación del servidor y publicación pendientes |
| EVT-01 | P1 | [Trazabilidad](../specs/trazabilidad.md) enumera consumidores previstos; esto no confirma todas sus suscripciones | Christian; Sergio coordina | Matriz distingue catálogo de suscripción; no tratar consumidores previstos como suscritos | Pendiente externo: subconjunto/issue/aprobación/cola por consumidor |
| SCP-01 | P1 | A4 crece a scopes de APIs ajenas; A9 está condicionado y A10 aparece en informe de Ventas | Sergio | Matriz por concesión; A10 respaldado #42, A9 condicionado #33; documento provisional; fuentes de Despacho y catálogo de Productos contrastados | Definido local: aprobación separada de provisión; Despacho confirma en contrato y #18; A9 pendiente en #33/G6 #87 C1; ningún cliente creado |
| SCP-02 | P1 | #44 retira postventa:csat:escribir; antes estaba concedido a Marketplace, Chatbot y Retail | Sergio; PO de Ventas define su API | Retirar las tres concesiones sin reemplazo; siete scopes propios; CSAT usa token CLIENTE y propiedad por sub en Postventa | Resuelto local por [aceptación de Luis en #44](https://github.com/Taller-SW-Web/Modulo-de-Seguridad/issues/44#issuecomment-6118403027); catálogo/kit alineados; publicación pendiente, sin provisión |
| GOV-01 | P1 | [Kit §12](../specs/kit-integracion.md) exige una semana de aviso; usuario indica presentación en cinco días | Sergio (PO) | ADR-008 mantiene aviso de una semana; sin excepción. Demo Ventas usa login/JWKS v1 existentes | Definido local; publicación y coordinación externa pendientes |

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

## Revisión y cierre de esta etapa

El [verificador documental](../scripts/verificar-documentacion.py) comprueba
referencias locales y correspondencia del reparto. Las comprobaciones no
resuelven huecos funcionales ni prueban tokens/despliegues. La validación
adicional del punto 9 sí comprobó tokens firmados contra ejemplos del kit y
formas mediante mocks; publicación, implementación y servicios siguen pendientes. La referencia pública sigue siendo el SHA fijado en fuentes:
este inventario y reparto permanecen como cambios locales sin commit.


## Evidencia del bloque 8/9 — 12-oct

[Informe](integracion/validacion-hito-3.md) y
[casos](integracion/evidencia-validacion-hito-3.md): tres documentos OpenAPI
3.1 válidos, 178 ejemplos, 42 operaciones v1 conservadas, 15 casos de schema,
68 peticiones HTTP mock y 88 comprobaciones de validadores con fixtures firmadas.

Seis comunicaciones preparadas, ninguna enviada. DAT-01/H-12/H-14 y pendientes
externos mantienen sus estados. No hay cierre de pruebas reales de login,
OTP, auditoría, aislamiento de pedidos ni despliegue por estos resultados.
