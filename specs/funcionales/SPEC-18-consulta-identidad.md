# SPEC-18 — Consulta de identidad para los demás módulos

| Campo | Valor |
|---|---|
| **Responsable** | Sergio Alejandro Osorio Montenegro (Product Owner) |
| **Hito objetivo** | Hito 1 (Sem. 4) — contrato · Hito 4 (Sem. 11) — implementación |
| **Estado** | Borrador |
| **Aprobada por** | Product Owner — pendiente de aprobación |

> **Origen.** Sale de dividir la antigua SPEC-09 «API de identidad para los demás módulos» el 20 de septiembre, por indicación del profesor: una spec por función. La equivalencia de números está en [`trazabilidad.md`](trazabilidad.md) §1.

---

## Contexto — ¿por qué?

El token que reciben los seis módulos consumidores dice quién es el usuario y qué roles tiene, pero no lleva todo lo que necesitan: Ventas necesita el nombre y el documento del cliente para emitir una boleta, Despacho la dirección de entrega, y cualquiera de ellos, qué significa un rol que le llegó en un token.

Si el módulo de despacho leyera nuestra tabla `usuario`, cualquier cambio de esquema nuestro rompería su despliegue, y una consulta suya mal escrita expondría hashes de contraseña. Esta spec define las consultas que evitan ese acoplamiento, y entrega a cada módulo **solo** los datos que su scope autoriza.

---

## Propósito — ¿para qué?

Permitir que un módulo consumidor, identificado con el token de servicio de SPEC-17, consulte los datos básicos de uno o varios usuarios, las direcciones de entrega de un cliente y los catálogos de roles y permisos.

El resultado observable es una respuesta que contiene exactamente los campos que el scope autoriza —con el documento enmascarado cuando no hay permiso para verlo en claro— y un registro de auditoría por cada acceso.

---

## Alcance — ¿hasta dónde?

Consulta de datos básicos de usuario, individual y por lote; consulta de
direcciones de entrega; consulta del catálogo de roles y de permisos; exposición
selectiva del documento según scope; y auditoría del acceso entre módulos.

### Superficie del contrato

| Método y ruta | Descripción | Acceso |
|---|---|---|
| `GET /usuarios/{id}` | Datos básicos de un usuario | `usuarios:leer` |
| `POST /usuarios/lote` | Consulta de hasta 100 usuarios en una llamada | `usuarios:leer` |
| `GET /usuarios/{id}/direcciones` | Direcciones de entrega del cliente | `direcciones:leer` |
| `GET /roles` | Catálogo de roles y sus permisos | `roles:leer` |
| `GET /permisos` | Catálogo de permisos por módulo | `roles:leer` |

Las reglas comunes —token de servicio obligatorio, `401` idéntico exista o no el
usuario, `403` sin revelar el scope, errores RFC 7807— son de SPEC-17
(RF-17.8 a RF-17.10) y aplican a todos estos endpoints.

---

## Requisitos — ¿qué debe hacer?

| Código | Requisito |
|---|---|
| RF-18.1 | La consulta de datos básicos debe devolver identificador, nombre, correo, teléfono, estado y roles, y **nunca** el hash de contraseña. |
| RF-18.2 | La consulta por lote debe aceptar hasta 100 identificadores, devolver los encontrados y los no hallados por separado, y no fallar entera por los que faltan. |
| RF-18.3 | El sistema debe exponer las direcciones de entrega de un cliente sin incluir su número de documento ni su fecha de nacimiento. |
| RF-18.4 | El número de documento debe entregarse en claro solo a un cliente con el scope `usuarios:leer:documento`; a los demás, enmascarado mostrando los últimos tres dígitos. |
| RF-18.5 | El sistema debe exponer el catálogo de roles con sus permisos y el catálogo de permisos por módulo, para que ningún consumidor los codifique a mano. |
| RF-18.6 | Cada acceso de un módulo consumidor debe registrarse en auditoría con el módulo solicitante, el endpoint, el identificador consultado, si el dato sensible se entregó en claro o enmascarado, y la fecha. El formato y la tabla los define **SPEC-12**; aquí se fija qué hechos hay que registrar. |

---

## Escenarios — ¿cómo verificamos?

Formato Dado / Cuando / Entonces. Los marcados como **caso borde** cubren
condiciones límite, de error o de seguridad.

### ESC-18.1 Consulta de datos básicos de un usuario

- **Dado** que el módulo de Ventas y Postventa necesita mostrar el nombre del cliente de un pedido,
- **Cuando** envía `GET /api/v1/usuarios/{id}` con un token de servicio con scope `usuarios:leer`,
- **Entonces** el sistema responde `200` con identificador, nombre, correo, teléfono, estado y roles, **sin** el hash de contraseña y sin ningún campo que su scope no autorice.

### ESC-18.2 Consulta por lote con identificadores inexistentes *(caso borde)*

- **Dado** que el módulo de Despacho necesita los datos de 10 usuarios y 2 de esos identificadores no corresponden a ninguna cuenta,
- **Cuando** envía `POST /api/v1/usuarios/lote` con los 10 identificadores,
- **Entonces** el sistema responde `200` con los 8 usuarios encontrados en `usuarios` y los 2 identificadores no hallados en `noEncontrados`, y la petición **no falla entera** por los que faltan.

> Un reporte de 50 clientes en el que uno se dio de baja sigue funcionando, en
> vez de quedarse en blanco.

### ESC-18.3 Lote por encima del límite *(caso borde)*

- **Dado** que un módulo envía 140 identificadores en una sola llamada,
- **Cuando** invoca `POST /api/v1/usuarios/lote`,
- **Entonces** el sistema responde `400` con `code: LOTE_DEMASIADO_GRANDE` y **no trunca la lista en silencio**; los identificadores duplicados dentro del límite se colapsan a una sola aparición, y un identificador malformado es `400 VALIDACION`, no un «no encontrado».

### ESC-18.4 Consulta de direcciones por el módulo de despacho

- **Dado** que el módulo de Despacho necesita la dirección de entrega de un pedido,
- **Cuando** envía `GET /api/v1/usuarios/{id}/direcciones` con scope `direcciones:leer`,
- **Entonces** el sistema responde `200` con las direcciones del cliente, **sin** el número de documento ni la fecha de nacimiento, que no hacen falta para entregar un paquete.

### ESC-18.5 Dato sensible con el scope que lo autoriza

- **Dado** que el módulo de Ventas debe emitir una boleta y necesita el documento del cliente,
- **Cuando** consulta sus datos con un token de servicio con scope `usuarios:leer:documento`,
- **Entonces** el sistema responde con el nombre completo y el número de documento **en claro**, y registra el acceso en auditoría con el módulo solicitante, el identificador consultado y la fecha.

### ESC-18.6 Dato sensible sin el scope que lo autoriza *(caso borde)*

- **Dado** que el módulo de Chatbot consulta los datos de un cliente y **no** tiene el scope `usuarios:leer:documento`,
- **Cuando** pide los datos de ese cliente,
- **Entonces** el sistema responde `200` con el documento **enmascarado**, mostrando solo los últimos tres dígitos, y **no rechaza la petición**: responde con menos, no con un error.

> Se enmascara en vez de devolver `403` porque el chatbot sí necesita confirmar
> ante el cliente los últimos dígitos de su documento. Un `403` le obligaría a
> pedir el scope completo para un caso que no lo justifica.

### ESC-18.7 Consulta del catálogo de roles

- **Dado** que un módulo consumidor necesita saber qué significa el rol `GESTOR_DESPACHO` que le llegó en un token,
- **Cuando** envía `GET /api/v1/roles` con scope `roles:leer`,
- **Entonces** el sistema responde `200` con cada rol, su descripción y sus permisos, de modo que el módulo resuelve los permisos sin consultar nuestra base de datos ni codificarlos a mano.

---

## Requisitos no funcionales — ¿con qué condiciones?

- La consulta por lote debe aceptar hasta **100 identificadores** y responder en menos de **1 segundo**.
- La consulta individual debe responder en menos de 200 ms en el percentil 95 con 50 usuarios concurrentes. *(valor propuesto al dividir la spec; lo confirma su responsable antes de aprobarla)*
- Ninguna respuesta a un módulo consumidor puede permitir enumerar qué cuentas existen.
- Ningún registro de auditoría puede contener contraseñas ni el número de documento en claro.
- El tratamiento de datos personales debe alinearse con la Ley N.º 29733 de Protección de Datos Personales.

---

## Fuera de alcance — ¿qué NO hará?

- El token de servicio, el JWKS, la introspección y las reglas comunes de error, que son de SPEC-17.
- **El contenido del catálogo de roles y permisos**, que es de SPEC-11. Esta spec fija **cómo se consulta**, no qué contiene.
- **Los atributos de perfil y las direcciones**, cuyo modelo define SPEC-16. Aquí solo se especifica cómo se consultan desde otro módulo.
- **Los eventos asíncronos**, que se especifican en [`catalogo-eventos.md`](catalogo-eventos.md).
- La modificación de datos de usuario por un módulo consumidor: solo leen.

---

## Dependencias conocidas

| Dependencia | Estado | Cómo se resuelve mientras tanto |
|---|---|---|
| El claim `permisos` presupone un catálogo de permisos por módulo (`pedido.crear`, `producto.editar`…) que define **SPEC-11** | Definido para este módulo; pendiente para los consumidores | Esta spec fija el **formato** —lista de códigos con punto, sin duplicados, unión de los roles del usuario—. Hoy `permisos` lleva los de este módulo; los de cada consumidor se añaden cuando se acuerden |
| La forma de los datos de usuario expuestos depende de **SPEC-01**, **SPEC-03** y **SPEC-16** | En redacción | El contrato fija los campos mínimos; añadir campos es compatible, renombrarlos no |
| Los `client_id` y `client_secret` de los seis módulos | Sin crear | El entorno simulado usa credenciales de prueba conocidas y publicadas |

---

## Impacto en el contrato

| Endpoint | Añade, modifica o elimina | Acordado |
|---|---|---|
| `GET /api/v1/usuarios/{id}` | Añade | ⬜ |
| `POST /api/v1/usuarios/lote` | Añade | ⬜ |
| `GET /api/v1/usuarios/{id}/direcciones` | Añade | ⬜ |
| `GET /api/v1/roles` | Añade | ⬜ |
| `GET /api/v1/permisos` | Añade | ⬜ |

---

## Lista de completitud

La spec se cierra cuando:

- [ ] Todos los requisitos están implementados
- [ ] Cada requisito clave tiene al menos dos escenarios automatizados, uno de ellos caso borde
- [ ] Los no funcionales están verificados, o la desviación está documentada y aprobada
- [ ] Los endpoints figuran en la documentación OpenAPI publicada (`specs/openapi.yaml`)
- [ ] Nada de lo declarado fuera de alcance se construyó de forma encubierta
- [ ] El código está en `main` con revisión aprobada y desplegado en nube
