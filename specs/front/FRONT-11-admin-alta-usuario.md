# FRONT-11 — Panel de administración: alta de cuenta

| Campo | Valor |
|---|---|
| **Responsable** | Eva Lucía Moreno Zevallos |
| **Épica principal** | [EP-01](../epicas/EP-01-registro-gestion-usuarios.md) |
| **Specs de backend que consume** | SPEC-03 (alta y activación) |
| **Wireframe** | Sin wireframe — el listado (pantalla 7) tiene el botón que lleva aquí |
| **Estado** | Borrador |
| **Aprobada por** | Pendiente |


> **Revisión de trabajo 11-oct:** alineación con A6/SPEC-03; pendiente de
> revisión humana. La aprobación histórica de backend no aprueba esta pantalla.

---

> **Corte 11-oct:** alta administrativa estricta en [v2 local](../contrato-api/openapi-v2.yaml).
> Las consultas, alta asistida de clientes y activación de cuenta siguen en v1.
> Publicación pendiente; términos/pantalla de activación A6 todavía requieren H-12.

## Objetivo — ¿para qué sirve esta pantalla?

Un ADMIN_SISTEMA con `usuario.crear` crea una cuenta administrativa con rol
opcional. La cuenta nace `PENDIENTE_VERIFICACION`, recibe un enlace de activación
de 72 horas y su titular define contraseña y acepta términos al activarla.
El administrador nunca pide, define ni comunica una contraseña inicial.
Sin rol (por ejemplo un repartidor de Despacho), la respuesta lleva `roles: []`;
el perfil de negocio se mantiene en el módulo dueño. Clientes usan su alta propia.

## Endpoints que usa

| Momento | Endpoint | Spec |
|---|---|---|
| Al enviar | `POST /api/v2/usuarios` | SPEC-03 |
| Al abrir detalle tras crear | `GET /api/v1/usuarios/{id}` si tiene `usuario.ver` | SPEC-03 |

Sin `usuario.crear` no envía petición. `usuario.ver` no se infiere del permiso
de creación: sin él se muestra el resultado de alta y no se abre el detalle.
No se consulta la política de contraseña desde esta pantalla.

## Estados de la pantalla

| Estado | Disparador | Resultado |
|---|---|---|
| Inicial | Abrir | Correo, nombres, apellidos; rol con opción «Sin rol»; Crear/Cancelar |
| Gestión elegida | Cualquiera de los cuatro roles de gestión | «Al activar la cuenta, este rol exige segundo factor y caducidad de contraseña según la política vigente.» |
| Validación | Local o `400 VALIDACION` | Errores de campo |
| Cargando | Enviar | Botón deshabilitado, «Creando…» |
| Correo no disponible | `409 CORREO_NO_DISPONIBLE` | Mensaje genérico de correo no utilizable |
| Creada pendiente | `201` | «Cuenta creada, pendiente de activación. Su titular recibirá un enlace válido por 72 horas.»; UUID visible |
| Sin rol | Rol omitido | Se muestra «Sin rol»; nunca se inventa REPARTIDOR |

## Validaciones del lado del cliente

| Campo | Regla | Petición |
|---|---|---|
| Correo | Obligatorio y formato válido | `correo` |
| Nombres/apellidos | Obligatorios; no enviar vacíos | Según CrearUsuarioRequest |
| Rol | Opcional; VENDEDOR, ADMIN_VENTAS, GESTOR_DESPACHO, GESTOR_COMERCIAL o ADMIN_SISTEMA | Si «Sin rol», **omitir** `rol`; no mandar null ni roles[] |

No se envían contraseña, confirmación, CLIENTE ni perfil de repartidor.
H-10 definido en contrato local v2 con schema específico; CodigoRol global conserva seis roles.

## Textos y mensajes

`201` no indica «Ya puede iniciar sesión». `CORREO_NO_DISPONIBLE`:
«Ese correo no se puede usar para una cuenta nueva.»
`VALIDACION`, `SCOPE_INSUFICIENTE` y `NO_DISPONIBLE`: FRONT-00.
Si no tiene `usuario.ver` no se invita a buscar el correo en el listado.

## Escenarios de interfaz — ¿cómo verificamos?

### UI-11.1 Alta de vendedor
Dado un administrador autorizado, cuando crea con VENDEDOR sin contraseña,
entonces `201` y cuenta pendiente; no hay sesión hasta activar.

### UI-11.2 Alta de gestión
Dado el formulario, cuando elige GESTOR_DESPACHO, entonces ve la nota de MFA
obligatorio y política vigente; el administrador no elige contraseña.

### UI-11.3 Correo no utilizable *(caso borde)*
Dado `409 CORREO_NO_DISPONIBLE`, cuando responde el alta, entonces se muestra
el mensaje genérico y no se afirma públicamente que existe cuenta.

### UI-11.4 Alta sin rol *(caso borde)*
Dado «Sin rol», cuando envía, entonces omite `rol`; la respuesta pending
puede traer `roles: []` y muestra UUID para vincular el perfil en Despacho.

### UI-11.5 CLIENTE excluido *(caso borde)*
Dado el formulario, cuando abre opciones, entonces CLIENTE no aparece.
Petición manipulada: `400 VALIDACION` según contrato local v2; publicación pendiente.

### UI-11.6 Sin permiso de consulta *(caso borde)*
Dado `usuario.crear` sin `usuario.ver`, cuando crea, entonces muestra UUID
y aviso de activación sin consultar el detalle ni abrir el listado.

## Accesibilidad y diseño adaptable

Se aplica FRONT-00. Lista de rol con etiqueta y opción explícita «Sin rol».
Errores asociados a campos; avisos anunciados; no autocompletar datos del admin.

## Fuera de alcance — ¿qué NO hará?

Crear clientes; perfiles de negocio; asignar varios roles; pedir contraseña.
La **pantalla de activación A6** exige spec propia antes de implementarse:
recibe enlace, contraseña/política de SPEC-07 y aceptación H-12, con
`POST /auth/activar-cuenta` de SPEC-03. FRONT-03 de verificación simple no
la sustituye. Es pendiente de EP-01 y queda fuera de la demo mínima con Ventas.

## Huecos y cierre

H-10: schema de rol de alta definido localmente en v2, sin publicar.
H-12: texto, versión y fuente de términos todavía pendientes.
La pantalla de activación no se considera especificada por este formulario.

- [ ] UI-01 alineada con A6 y revisión humana aprobada
- [ ] Rol opcional, sin CLIENTE ni contraseña en petición
- [ ] Estado pendiente y enlace de 72 horas explicados
- [ ] Detalle solo con usuario.ver
- [ ] Pantalla de activación especificada antes de implementarla
- [ ] Escenarios y accesibilidad verificados con evidencia
