# FRONT-15 — Configuración: clientes técnicos

| Campo | Valor |
|---|---|
| **Responsable** | Luis David Morales Brenis |
| **Specs de backend que consume** | [SPEC-19](../funcionales/SPEC-19-clientes-tecnicos.md), catálogo de scopes de [SPEC-17](../funcionales/SPEC-17-tokens-servicio.md) |
| **Wireframe** | Sin wireframe; sigue el [sistema de diseño](../../docs/diseno/sistema-diseno.md) y el listado de FRONT-07 |
| **Estado** | En revisión (FEAT-14, H5) |
| **Aprobada por** | Pendiente |

---

## Objetivo — ¿para qué sirve esta pantalla?

Un `ADMIN_SISTEMA` configura los módulos que se conectan a la plataforma: ve
cada cliente técnico con sus scopes, da de alta uno nuevo, rota un secreto
filtrado, desactiva un cliente y concede o revoca scopes. Llega desde el menú
del panel, entrada «Configuración → Clientes técnicos», ruta `/admin/clientes-tecnicos`.

---

## Endpoints que usa

Permiso necesario: `integracion.gestionar`. Todas las rutas son de `/api/v2` (SPEC-19).

| Momento | Endpoint | Spec de backend |
|---|---|---|
| Al abrir la pantalla y después de cada cambio | `GET /clientes-servicio` | RF-19.2 |
| Al abrir el formulario de alta o el de conceder scope (una vez por visita) | `GET /scopes` | RF-19.3 |
| Al confirmar el alta | `POST /clientes-servicio` | RF-19.4 |
| Al confirmar la rotación | `POST /clientes-servicio/{clientId}/secreto` | RF-19.6 |
| Al confirmar desactivar o reactivar | `POST /clientes-servicio/{clientId}/desactivar` o `/reactivar` | RF-19.8 |
| Al conceder un scope | `POST /clientes-servicio/{clientId}/scopes` | RF-19.9 |
| Al confirmar la revocación de un scope | `DELETE /clientes-servicio/{clientId}/scopes/{scope}` | RF-19.9 |

---

## Contenido

**Tabla de clientes:** `clientId`, nombre, estado («Activo» o «Inactivo»), scopes
como etiquetas agrupadas por API dueña, y un menú de acciones por fila: «Gestionar
scopes», «Rotar secreto», «Desactivar» o «Reactivar». Botón «Nuevo cliente» encima de la tabla.

**Alta:** formulario con `clientId`, nombre y selección múltiple de scopes del
catálogo, agrupados por audiencia y con su descripción.

**Diálogo del secreto:** después del alta o de una rotación, un diálogo modal
muestra el `clientSecret` con un botón «Copiar», el aviso «Este secreto solo se
muestra ahora. Entrégalo al equipo dueño por un canal privado.» y la casilla
«Ya guardé el secreto», que habilita «Cerrar». Al cerrarlo, el secreto se borra
del estado de la pantalla; no se guarda en `localStorage`, en la URL ni en logs.

**Gestionar scopes:** panel lateral con los scopes concedidos, cada uno con
«Revocar», y un selector con los del catálogo que el cliente aún no tiene, con «Conceder».

---

## Estados de la pantalla

| Estado | Cuándo ocurre | Qué ve el usuario |
|---|---|---|
| Sin permiso | La sesión no tiene `integracion.gestionar` | «No tienes permiso para ver esta página.» (FRONT-00), sin llamar al endpoint; la entrada del menú no aparece |
| Cargando | Mientras llega `GET /clientes-servicio` | Esqueleto de la tabla |
| Vacío | No hay clientes | «Todavía no hay clientes técnicos.» y el botón «Nuevo cliente» |
| Lista | Hay clientes | La tabla |
| Error de carga | `503` o fallo de red | «No pudimos cargar los clientes técnicos.» y «Reintentar» |
| Enviando | Durante cualquier cambio | El botón del diálogo, deshabilitado y con indicador; no admite doble envío |
| Secreto visible | Tras `201` del alta o `200` de la rotación | El diálogo del secreto |
| Éxito sin secreto | Tras `204` | Notificación breve («Scope concedido», «Cliente desactivado»…) y la tabla recargada |

---

## Validaciones del lado del cliente

| Campo | Regla | Mensaje | Origen |
|---|---|---|---|
| `clientId` | `^[a-z0-9][a-z0-9-]{2,49}$` | «Usa de 3 a 50 caracteres: minúsculas, números y guiones; empieza con letra o número.» | RF-19.5 |
| Nombre | De 1 a 100 caracteres | «Escribe un nombre de hasta 100 caracteres.» | RF-19.5 |
| Scopes | Solo los del catálogo, sin repetir; puede ir vacío | El selector no permite otros | RF-19.10 |

---

## Textos y mensajes

| Código del backend | Mensaje en pantalla |
|---|---|
| `VALIDACION` | El mensaje del campo indicado; si no lo trae, «Revisa los datos del formulario.» |
| `CLIENTE_SERVICIO_EXISTENTE` | «Ya existe un cliente con ese identificador.» |
| `NO_ENCONTRADO` | «Ese cliente ya no existe. Recargamos la lista.» y recarga |
| `SCOPE_INSUFICIENTE` | «No tienes permiso para gestionar clientes técnicos.» |
| `TOKEN_INVALIDO` | Comportamiento común de FRONT-00: renovar y, si falla, volver al login |
| `NO_DISPONIBLE` y fallo de red | «No pudimos completar el cambio. Inténtalo de nuevo.» En una rotación con resultado incierto, recarga la lista y avisa: «Si el cambio se aplicó, rota el secreto otra vez para obtener uno que puedas ver.» |

**Confirmaciones:**
- Rotar: «El secreto actual dejará de funcionar ahora. El módulo no podrá pedir tokens hasta que configure el nuevo.»
- Desactivar: «El módulo no podrá pedir tokens nuevos. Los que ya tiene siguen valiendo hasta una hora.»
- Revocar: «El próximo token del módulo ya no incluirá este scope.»

---

## Escenarios de interfaz — ¿cómo verificamos?

### UI-15.1 Alta de un cliente
- **Dado** un administrador en la pantalla,
- **Cuando** crea `modulo-prueba-h5` con el scope `usuarios:leer`,
- **Entonces** ve el diálogo con el secreto, puede copiarlo, solo puede cerrar tras marcar «Ya guardé el secreto», y la tabla muestra el cliente activo con su scope.

### UI-15.2 El secreto no persiste *(caso borde)*
- **Dado** que se cerró el diálogo del secreto,
- **Cuando** se recarga la lista o se vuelve a abrir el cliente,
- **Entonces** el secreto ya no aparece en la pantalla, en el almacenamiento del navegador ni en la URL.

### UI-15.3 Conceder y revocar
- **Dado** un cliente con `usuarios:leer`,
- **Cuando** concede `roles:leer` y luego revoca `usuarios:leer` confirmando,
- **Entonces** la fila muestra solo `roles:leer` después de recargar.

### UI-15.4 Identificador repetido *(caso borde)*
- **Dado** que existe `modulo-marketplace`,
- **Cuando** se intenta crear otro con ese identificador,
- **Entonces** el formulario sigue abierto con «Ya existe un cliente con ese identificador.» y no se muestra ningún secreto.

### UI-15.5 Sin permiso *(caso borde)*
- **Dado** un usuario sin `integracion.gestionar`,
- **Cuando** entra a `/admin/clientes-tecnicos` escribiendo la ruta,
- **Entonces** ve «No tienes permiso para ver esta página.» y no se llama a ningún endpoint.

### UI-15.6 Doble envío *(caso borde)*
- **Dado** el diálogo de rotación,
- **Cuando** se pulsa «Rotar» dos veces seguidas,
- **Entonces** se envía una sola petición.

---

## Accesibilidad y diseño adaptable

- El diálogo del secreto atrapa el foco, se anuncia con `role="alertdialog"` y el botón «Copiar» confirma con un mensaje en una región `aria-live`.
- El secreto se muestra en fuente monoespaciada y con salto de línea, sin desbordar en móvil.
- En móvil la tabla pasa a tarjetas por cliente, con el menú de acciones en cada tarjeta.
- Las etiquetas de scope tienen texto, no solo color.

---

## Fuera de alcance — ¿qué NO hará?

- Borrar clientes, editar el nombre o editar el catálogo de scopes (fuera de alcance de SPEC-19).
- Volver a mostrar un secreto ya entregado.
- Probar la emisión de un token desde la pantalla.

---

## Lista de completitud

- [ ] Cada estado de la pantalla está implementado
- [ ] Cada código de error del backend tiene su mensaje
- [ ] Las validaciones del cliente coinciden con las de SPEC-19
- [ ] Los escenarios de interfaz están automatizados o verificados a mano con evidencia
- [ ] Ninguna prueba ni captura deja un secreto real en el repositorio
