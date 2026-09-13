# Plan de implementación — SPEC-09 · API de identidad

| Campo | Valor |
|---|---|
| **Spec** | [`SPEC-09-api-identidad.md`](SPEC-09-api-identidad.md) |
| **Responsable** | Sergio Osorio (Product Owner) |
| **Este plan cubre** | Semana 4 (contrato) y Semana 11 (implementación) |

> **Regla de SDD que se aplica aquí.** Este plan no se ejecuta hasta que la spec
> esté aprobada. Si al escribir el contrato aparece un caso que la spec no
> contempla, se para, se actualiza la spec y se vuelve. No se resuelve en el
> YAML y se documenta después.

---

## 1. Qué se construye esta semana y qué no

SPEC-09 tiene dos entregas separadas por siete semanas, y confundirlas es el
riesgo principal de esta funcionalidad.

| | Semana 4 — **esta** | Semana 11 — Hito 4 |
|---|---|---|
| **Se entrega** | El contrato y un entorno simulado | La implementación real |
| **Artefactos** | `SPEC-09.md`, `openapi.yaml`, `kit-integracion.md` | Controladores, servicios, filtros de scope, auditoría |
| **Prueba de que está hecho** | Un equipo ajeno programa contra el mock y obtiene respuestas | Los escenarios ESC-09.1 a ESC-09.16 pasan como pruebas automatizadas |
| **No se toca** | Ninguna línea de código de producción | — |

**El rubro del Hito 1 no pide código.** Escribir el backend ahora tendría un
coste concreto: el tiempo que hace falta para aprobar las ocho specs de los
demás, que sí es camino crítico de seis personas.

---

## 2. Stack de esta entrega

| Pieza | Herramienta | Por qué |
|---|---|---|
| Contrato | OpenAPI 3.1 escrito a mano en `specs/openapi.yaml` | No hay código del que generarlo todavía. Desde el Hito 4 se genera con `springdoc-openapi` para que no se desincronice |
| Entorno simulado | `@stoplight/prism-cli` vía `npx` | Cero instalación, cero infraestructura, cero costo. Sirve los ejemplos del propio contrato, así que nunca se desincroniza de él |
| Validación del contrato | `npx @redocly/cli lint specs/openapi.yaml` | Detecta ejemplos que no cuadran con su esquema antes de que lo haga un equipo consumidor |
| Colección de pruebas | Bruno o Postman, compartida en el repo | Los seis equipos la importan y prueban sin escribir `curl` a mano |

Nada de esto se instala: todo corre con `npx`. Un equipo consumidor solo
necesita Node 18 o superior.

---

## 3. Orden de trabajo de esta semana

Las cinco tareas de la tarjeta, en el único orden en que tienen sentido.

### Paso 1 — Aprobar la spec *(bloquea todo lo demás)*

`SPEC-09-api-identidad.md` está en borrador. Antes de escribir el YAML hay que
cerrar tres cosas con el equipo:

- [ ] **Jose Luis** revisa que `tipo: "servicio"` y el manejo de scopes encajen con cómo va a montar Spring Security en SPEC-02
- [ ] **Eva Lucía** confirma que los campos de `GET /usuarios/{id}` existen en su modelo de datos de SPEC-01 y SPEC-08
- [ ] **Christian** confirma que puede aprovisionar seis pares `client_id` / `client_secret`

Sin esos tres visto bueno, el contrato se publica con supuestos y se rompe en la
semana 11.

### Paso 2 — Escribir `specs/openapi.yaml`

Ocho endpoints, en este orden, porque cada uno depende del anterior:

| # | Endpoint | Depende de |
|---|---|---|
| 1 | `GET /auth/.well-known/jwks.json` | Nada. Es el único sin autenticación |
| 2 | `POST /auth/token` | Nada. Es la puerta de todos los demás |
| 3 | `POST /auth/introspeccion` | Del token de servicio del 2 |
| 4 | `GET /usuarios/{id}` | Del esquema `Usuario` |
| 5 | `POST /usuarios/lote` | Del esquema `Usuario` del 4 |
| 6 | `GET /usuarios/{id}/direcciones` | Del esquema `Direccion` |
| 7 | `GET /roles` | Del esquema `Rol` |
| 8 | `GET /permisos` | Del esquema `Permiso` del 7 |

**Cuatro reglas al escribirlo, y las cuatro tienen consecuencia práctica:**

1. **Cada respuesta lleva un `examples` real.** Prism sirve exactamente eso: un
   endpoint sin ejemplo devuelve un objeto vacío y el equipo consumidor no puede
   hacer nada con él. El ejemplo *es* el entregable, no una decoración.
2. **Los caminos de error también llevan ejemplo.** `401`, `403`, `400` y `404`
   con su `application/problem+json` completo. Un consumidor que solo pueda
   probar el camino feliz descubrirá sus errores en producción.
3. **Claves y códigos en ASCII** (`introspeccion`, `contrasena`), contenido con
   acentos. Ver [ADR-004](../docs/arquitectura/adr/004-idioma-del-contrato.md).
4. **Cada `operationId` cita su escenario** en la descripción (`ESC-09.5`), para
   que en la semana 11 la prueba de integración y el endpoint se encuentren
   solos.

### Paso 3 — Levantar el entorno simulado

```bash
npx @redocly/cli lint specs/openapi.yaml          # que valide antes de servirlo
npx @stoplight/prism-cli mock specs/openapi.yaml -p 4010
curl http://localhost:4010/auth/.well-known/jwks.json
```

**Prism sirve las rutas sin el prefijo `/api/v1`**, porque lo toma del
documento tal cual y no del `server`. En el backend real sí lo lleva. Hay que
decirlo en el kit o será la primera pregunta de los seis equipos.

Prism valida también las **peticiones** contra el esquema, así que un equipo que
envíe un cuerpo mal formado recibe un `400` real, no un éxito falso. Es la
diferencia entre un mock y un simulacro.

Credenciales y datos de prueba publicados en el contrato, iguales para todos:

| Concepto | Valor de prueba |
|---|---|
| `client_id` de cada módulo | `modulo-ventas`, `modulo-despacho`, `modulo-productos`, `modulo-marketplace`, `modulo-chatbot`, `modulo-retail` |
| `client_secret` de prueba | `secreto-de-prueba` (el mismo para los seis; los reales los aprovisiona Christian) |
| Usuario cliente | `11111111-1111-1111-1111-111111111111` · rol `CLIENTE` |
| Usuario vendedor | `22222222-2222-2222-2222-222222222222` · rol `VENDEDOR` |
| Usuario desactivado | `33333333-3333-3333-3333-333333333333` · para probar ESC-09.6 |
| Identificador inexistente | `99999999-9999-9999-9999-999999999999` · para probar ESC-09.8 |

Que el usuario desactivado y el identificador inexistente estén **publicados** es
lo que permite a un equipo consumidor probar sus caminos de error sin pedirnos
nada.

### Paso 4 — Escribir `specs/kit-integracion.md`

La guía práctica para los otros seis equipos. No repite el contrato: responde
las preguntas que van a hacer por chat.

- [ ] Cómo levantar el mock en un comando
- [ ] Cómo validar un token en local, con ejemplo en Java y en JavaScript
- [ ] Cuándo usar introspección y cuándo no *(la pregunta que más va a costar)*
- [ ] Qué scopes pedir según el módulo
- [ ] Qué hacer cuando el JWKS no responde
- [ ] A quién preguntar y cómo se avisan los cambios del contrato

### Paso 5 — Aprobar las ocho specs de los demás

Es la tarea que se cae cuando el resto se alarga, y es la que evalúa el
profesor. Criterio de aprobación, el mismo para las ocho:

- [ ] Las siete secciones están completas
- [ ] Cada requisito clave tiene dos escenarios y uno es caso borde
- [ ] No contradice el contrato de SPEC-09
- [ ] Lo que declara fuera de alcance no se lo asigna en silencio a otra spec

### Paso 6 — Comunicar el contrato a los seis equipos

El viernes, no el sábado. Un mensaje corto con: la URL del repositorio, el
comando del mock, el enlace al kit, las credenciales de prueba y la fecha de
congelamiento.

---

## 4. Lo que se construye en el Hito 4

No se toca ahora; queda registrado para que la semana 11 no empiece de cero.

### 4.1 Tablas que esta spec necesita y que ninguna otra spec cubre

Las demás tablas son de SPEC-01, SPEC-05 y SPEC-08. Estas dos son propias:

| Tabla | Propósito | Campos relevantes |
|---|---|---|
| `cliente_servicio` | Los seis módulos consumidores | `id`, `client_id`, `secret_hash`, `nombre_modulo`, `activo`, `fecha_creacion` |
| `cliente_servicio_scope` | Scopes concedidos a cada módulo | `cliente_servicio_id`, `scope` |

La auditoría del acceso entre módulos reutiliza `auditoria_seguridad`, cuyo
dueño es **SPEC-06**: escribimos con `actorTipo: "MODULO"` y el `client_id` del
módulo solicitante en `actorId`, con las acciones `MODULO_CONSULTO_USUARIO` y
`MODULO_OBTUVO_DOCUMENTO` del catálogo de esa spec. El formato del registro no
se decide aquí.

### 4.2 Estructura del backend

```
integration/
├── web/         IntegrationController, JwksController, ServiceTokenController
├── service/     TokenIntrospectionService, UserQueryService, ServiceTokenService
├── security/    ServiceTokenFilter, ScopeAuthorizationManager
├── mapper/      Exposición selectiva por scope: enmascarado del documento
└── audit/       CrossModuleAuditLogger
```

El punto delicado es `mapper/`: es el único sitio donde se decide qué campos ve
cada scope. Si esa decisión se reparte entre los controladores, el enmascarado
del documento se olvidará en alguno.

### 4.3 Una prueba por escenario

Los dieciséis escenarios de la spec son el plan de pruebas. No hay que
redactarlo aparte.

| Escenarios | Tipo de prueba | Herramienta |
|---|---|---|
| ESC-09.1, ESC-09.2 | Unitaria — verificación de firma y selección por `kid` | JUnit 5 |
| ESC-09.3, ESC-09.4 | Integración — emisión de token de servicio y sus límites | Spring Boot Test |
| ESC-09.5, ESC-09.6 | Integración contra base real — estado actual frente a estado del token | Testcontainers |
| ESC-09.7, ESC-09.10, ESC-09.15 | Integración — forma de la respuesta y campos ausentes | Spring Boot Test |
| ESC-09.8, ESC-09.9 | Integración — lote parcial, límite de 100, duplicados, malformados | Spring Boot Test |
| ESC-09.11, ESC-09.12 | Integración + auditoría — enmascarado según scope | Testcontainers |
| ESC-09.13, ESC-09.14 | Seguridad — anti-enumeración y scope insuficiente | Pruebas dedicadas |
| ESC-09.16 | Contrato — la respuesta real coincide con el OpenAPI publicado | Colección Bruno en el pipeline |

Rendimiento (RNF): introspección por debajo de 200 ms en p95 con 50 usuarios
concurrentes, medido con JMeter en el Hito 5.

---

## 5. Riesgos asumidos

| Riesgo | Por qué lo aceptamos | Señal de que se materializó |
|---|---|---|
| El contrato se escribe sin implementación detrás y algo resulta inviable en la semana 11 | El coste de bloquear seis equipos siete semanas es mayor. Los cambios se absorben añadiendo campos, no renombrando | Un endpoint que exige una consulta que el modelo de datos no permite |
| Los seis equipos no levantan el mock y esperan a la implementación real | No podemos obligarlos. El kit reduce la fricción a un comando | Nadie pregunta nada en el canal de integración durante dos semanas: no lo están usando |
| `permisos` viaja vacío hasta que exista SPEC-05 | Los consumidores autorizan por `roles` mientras tanto, y añadir contenido a una lista vacía es compatible | Un equipo codifica permisos a mano en su módulo |
| Prism no cubre lógica, solo ejemplos | Un mock que ejecutara reglas sería una segunda implementación que mantener | Un equipo asume que el mock valida credenciales de verdad |
| Aprobar ocho specs se come el tiempo de escribir el contrato | Es real y es el riesgo principal de la semana | Llegar al jueves con el YAML a medias |

---

## 6. Definición de hecho para esta semana

- [ ] `SPEC-09-api-identidad.md` aprobada, con el visto bueno de Jose Luis, Eva Lucía y Christian
- [ ] `specs/openapi.yaml` pasa `redocly lint` sin errores
- [ ] Los ocho endpoints tienen ejemplo de éxito **y** de error
- [ ] `npx prism mock` levanta y responde los ocho
- [ ] `specs/kit-integracion.md` publicado
- [ ] Las ocho specs de los demás integrantes, revisadas y aprobadas
- [ ] Los seis equipos avisados, con el comando y las credenciales de prueba
