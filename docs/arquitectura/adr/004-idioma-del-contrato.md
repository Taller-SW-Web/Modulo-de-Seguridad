# ADR-004 — Rutas y códigos del contrato en español

| Campo | Valor |
|---|---|
| **Estado** | Aceptada |
| **Fecha** | Semana 4 — septiembre de 2026 |
| **Decide** | Sergio Osorio (PO) |

## Contexto

La convención habitual en este equipo es escribir el código en inglés y la
documentación en español. Aplicada al pie de la letra, daría rutas como
`/users`, `/auth/register`, `/auth/introspect` y códigos de rol `BUYER`,
`SELLER`, `SYSTEM_ADMIN`.

Pero hay tres documentos que ya están delante de los otros seis equipos y del
profesor, y los tres usan español:

- El plan de desarrollo del módulo, con la tabla de endpoints `/auth/registro`,
  `/auth/login`, `/usuarios`, `/password/recuperar`.
- El diagrama de contexto y el de componentes, que rotulan
  `POST /auth/introspeccion`.
- El modelo de datos, cuyas tablas y columnas están en español
  (`usuario`, `token_refresco`, `codigo_otp`, `hash_contrasena`).

Los códigos de rol, además, **no son una convención interna**: viajan dentro del
claim `roles` del token que leen los seis módulos consumidores.

## Decisión

El contrato público se escribe en **español**: rutas, nombres de campo JSON,
códigos de rol, scopes y códigos de error.

| Elemento | Forma |
|---|---|
| Rutas | `/api/v1/usuarios`, `/api/v1/auth/introspeccion` |
| Roles | `CLIENTE`, `VENDEDOR`, `ADMIN_VENTAS`, `GESTOR_DESPACHO`, `GESTOR_COMERCIAL`, `ADMIN_SISTEMA` |
| Scopes | `usuarios:leer`, `tokens:introspeccion`, `direcciones:leer` |
| Códigos de error | `TOKEN_INVALIDO`, `SCOPE_INSUFICIENTE`, `LOTE_DEMASIADO_GRANDE` |
| Claims propios del token | `roles`, `permisos`, `tipo` |

Los claims estándar de JWT (`sub`, `iss`, `iat`, `exp`, `jti`, `aud`) se
mantienen tal cual: son de la especificación RFC 7519, no nuestros.

**Los identificadores internos del código Java siguen en inglés** — clases,
métodos, variables, paquetes. La frontera es exacta: lo que cruza la API va en
español, lo que vive dentro del servicio va en inglés.

## Por qué

Cambiarlo ahora obligaría a re-comunicar el contrato a seis equipos que ya vieron
los diagramas, tres días antes de congelarlo. El coste de la incoherencia con la
convención de código es que un desarrollador lea `usuarioRepository.findById()`
sirviendo la ruta `/usuarios/{id}`, lo cual no rompe nada.

Hay además un argumento que no es de conveniencia: el contrato lo leen seis
equipos hispanohablantes que están aprendiendo, y sus propios módulos están
descritos en español en los lineamientos del curso. Un contrato que se lee en el
mismo idioma en que está escrito el enunciado genera menos malentendidos, y los
malentendidos en la frontera entre módulos son exactamente lo que esta
arquitectura existe para evitar.

## Consecuencias

- El `openapi.yaml` del repositorio personal `gestor-accesos` **no se reutiliza
  tal cual**: hay que migrar rutas, campos y códigos de rol.
- Aparecen acentos y `ñ` en el contenido de las respuestas, no en las claves.
  Las claves JSON y los códigos usan solo ASCII (`introspeccion`, no
  `introspección`; `contrasena`, no `contraseña`), para que ningún consumidor
  tenga problemas de codificación al deserializar.
- La convención de código del equipo debe anotar esta excepción explícitamente,
  o alguien la «corregirá» en un PR dentro de tres semanas.

## Alternativas descartadas

| Alternativa | Por qué no |
|---|---|
| Todo en inglés, como la convención de código | Contradice tres documentos ya publicados al grupo y al profesor, y obliga a re-comunicar a seis equipos a tres días del congelamiento |
| Rutas en español, campos JSON en inglés | Coherencia parcial que hay que explicar en cada conversación; el consumidor tendría que recordar de qué lado está cada cosa |
| Aplazar la decisión hasta la sincronización entre módulos | El contrato se congela el jueves 17. Aplazarlo es no publicar |
