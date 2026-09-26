# FRONT-06 — Mi cuenta: perfil y direcciones

| Campo | Valor |
|---|---|
| **Responsable** | Por asignar |
| **Specs de backend que consume** | SPEC-16 (enlaza a FRONT-09, FRONT-10 y FRONT-13) |
| **Wireframe** | Stitch, proyecto `889177073946089595`, pantalla 6 (numeración antigua; ver [`trazabilidad.md`](../trazabilidad.md) §1.1). Figma: pendiente |
| **Estado** | Borrador |
| **Aprobada por** | Pendiente |

---

## Objetivo — ¿para qué sirve esta pantalla?

Cualquier usuario con sesión ve y actualiza sus datos: nombre, celular,
correo, documento (enmascarado) y, si es cliente, sus direcciones de entrega.
Es el destino por defecto tras iniciar sesión para quien no administra (FRONT-00
§1.4). También es la puerta a la seguridad de la cuenta: segundo factor
(FRONT-09), cambio de contraseña (FRONT-10), actividad reciente (FRONT-13) y
cerrar sesión.

---

## Endpoints que usa

| Momento | Endpoint | Spec de backend |
|---|---|---|
| Al abrir la pantalla | `GET /api/v1/auth/me` | SPEC-16 (RF-16.1) |
| Al abrir, si la cuenta tiene rol `CLIENTE` | `GET /api/v1/usuarios/{id}/direcciones` con el `id` de `/auth/me` | SPEC-16, SPEC-18 |
| Al guardar datos personales o celular | `PATCH /api/v1/usuarios/{id}/atributos` | SPEC-16 (RF-16.2) |
| Al confirmar el cambio de correo | `POST /api/v1/usuarios/{id}/correo` | SPEC-16 (RF-16.6) |
| Al guardar una dirección nueva | `POST /api/v1/usuarios/{id}/direcciones` | SPEC-16 (RF-16.5) |

---

## Secciones

| Sección | Contenido | ¿Editable? |
|---|---|---|
| Datos personales | Nombres, apellidos | Sí, en línea, con «Guardar» y «Cancelar» |
| Contacto | Correo con etiqueta «Verificado»; celular con etiqueta «Verificado» o «Sin verificar» (`celularVerificado`) | Correo: con su propio flujo (diálogo). Celular: sí |
| Documento | Tipo de documento y `documentoEnmascarado` (`*****234`) | No, si ya existe. Ver H-13 si no existe |
| Seguridad | Enlaces a «Segundo factor» (FRONT-09), «Cambiar contraseña» (FRONT-10), «Actividad reciente» (FRONT-13) y botón «Cerrar sesión» | — |
| Direcciones (solo `CLIENTE`) | Lista con etiqueta, dirección, distrito, provincia, departamento, referencia; la predeterminada marcada con el texto «Predeterminada»; botón «Añadir dirección» | Solo añadir (H-04) |

El interruptor del segundo factor que pide el wireframe se muestra en FRONT-09
y aquí solo como enlace, porque `/auth/me` no dice si está activo (H-01).

---

## Estados de la pantalla

| Estado | Cuándo ocurre | Qué ve el usuario |
|---|---|---|
| Cargando | Al abrir, hasta que responde `/auth/me` | Esqueleto de las secciones |
| Datos cargados | `200` de `/auth/me` | Las secciones anteriores |
| Error de carga | `/auth/me` falla con algo que no es `401` | Mensaje de FRONT-00 §2 y botón «Reintentar» |
| Editando | Pulsa «Editar» en una sección | Campos editables con «Guardar» y «Cancelar». Solo una sección en edición a la vez |
| Guardando | Tras «Guardar» | Botón deshabilitado con «Guardando…» |
| Guardado | `200` del `PATCH` | La sección vuelve a modo lectura con los datos que devuelve el backend y un aviso breve «Cambios guardados.» |
| Celular cambiado | `200` del `PATCH` con celular nuevo | La etiqueta pasa a «Sin verificar» (RF-16.7) |
| Cambio de correo — diálogo | Pulsa «Cambiar correo» | Diálogo con «Correo nuevo» y «Contraseña actual», y botón «Enviar enlace» |
| Cambio de correo — pendiente | `202` de `/correo` | Aviso persistente en la sección Contacto hasta recargar (ver textos) |
| Direcciones vacías | `direcciones: []` | «Aún no tienes direcciones guardadas.» y botón «Añadir dirección» |
| Nueva dirección | Pulsa «Añadir dirección» | Formulario: etiqueta, departamento, provincia, distrito, dirección, referencia y casilla «Usar como predeterminada» |
| Dirección guardada | `201` | Se recarga la lista; si era predeterminada, la anterior pierde la marca (ESC-16.9) |

---

## Validaciones del lado del cliente

| Campo | Regla | Mensaje | Origen |
|---|---|---|---|
| Nombres, apellidos | No vacíos | «Este campo no puede quedar vacío.» | Interfaz; el backend valida |
| Celular | 9 dígitos tras `+51` fijo | «Escribe los 9 dígitos de tu celular.» | `AtributosUsuario.celular` (`^\+51[0-9]{9}$`) |
| Correo nuevo | Obligatorio y con formato; distinto del actual | «Escribe un correo válido.» / «Es el mismo correo que ya usas.» | Esquema de `/usuarios/{id}/correo` |
| Contraseña actual (cambio de correo) | Obligatoria | «Escribe tu contraseña actual.» | RF-16.6 |
| Departamento, provincia, distrito, dirección | Obligatorios | «Este campo es obligatorio.» | `Direccion.required` |

El `PATCH` envía **solo los campos que cambiaron**: el evento
`usuario.atributos_actualizados` lista los campos modificados (RF-16.9) y un
campo reenviado sin cambios lo ensuciaría.

---

## Textos y mensajes

| Código del backend | Mensaje en pantalla |
|---|---|
| `202` de `/correo` | «Te enviamos un enlace a {correo nuevo}. Hasta que lo confirmes, sigues entrando con {correo actual}. También avisamos a tu correo actual.» |
| `CREDENCIALES_INVALIDAS` en `/correo` | «La contraseña actual no es correcta.» (no cierra la sesión: FRONT-00 §2) |
| `CORREO_NO_DISPONIBLE` en `/correo` | «No puedes usar ese correo. Prueba con otro.» |
| `ATRIBUTO_NO_APLICABLE` | «No pudimos guardar ese dato en tu cuenta.» (no debería ocurrir: la pantalla solo ofrece los atributos de su rol) |
| `SCOPE_INSUFICIENTE` | Según FRONT-00 §2 (no debería ocurrir: la pantalla solo usa el `id` propio) |
| `VALIDACION`, `NO_DISPONIBLE` | Según FRONT-00 §2 |

---

## Escenarios de interfaz — ¿cómo verificamos?

### UI-06.1 Ver el perfil
- **Dado** un cliente con sesión y DNI registrado
- **Cuando** abre *Mi cuenta*
- **Entonces** ve su nombre, su correo «Verificado», su celular y el documento
  como `*****234`, sin opción de editarlo.

### UI-06.2 Editar el nombre
- **Dado** un cliente en *Mi cuenta*
- **Cuando** cambia sus nombres y pulsa «Guardar»
- **Entonces** el `PATCH` lleva solo `nombres`, la sección muestra el valor
  que devolvió el backend y aparece «Cambios guardados.».

### UI-06.3 Cambiar el celular
- **Dado** un celular verificado
- **Cuando** el usuario guarda uno nuevo
- **Entonces** la etiqueta pasa a «Sin verificar».

### UI-06.4 Cambiar el correo
- **Dado** un usuario con `actual@correo.com`
- **Cuando** pide cambiarlo a `nuevo@correo.com` con su contraseña correcta
- **Entonces** ve el aviso de enlace enviado y su correo visible sigue siendo
  `actual@correo.com`.

### UI-06.5 Cambiar el correo con la contraseña equivocada *(caso borde de seguridad)*
- **Dado** una sesión abierta en un equipo compartido
- **Cuando** alguien intenta cambiar el correo con una contraseña incorrecta
- **Entonces** el diálogo muestra «La contraseña actual no es correcta.», el
  correo no cambia y la sesión **no** se cierra.

### UI-06.6 Nueva dirección predeterminada
- **Dado** un cliente con una dirección predeterminada
- **Cuando** añade otra marcando «Usar como predeterminada»
- **Entonces** la lista recargada muestra la nueva como «Predeterminada» y la
  anterior sin la marca.

### UI-06.7 Vendedor *(caso borde)*
- **Dado** un usuario con rol `VENDEDOR` y sin rol `CLIENTE`
- **Cuando** abre *Mi cuenta*
- **Entonces** no ve la sección de direcciones ni la pide al backend.

### UI-06.8 Doble guardado *(caso borde)*
- **Dado** una sección en edición
- **Cuando** el usuario pulsa «Guardar» dos veces
- **Entonces** se envía un solo `PATCH`.

---

## Accesibilidad y diseño adaptable

- Se aplica FRONT-00. Además:
- Cada sección es un `<section>` con su encabezado, navegable por
  encabezados con un lector de pantalla.
- El diálogo de cambio de correo atrapa el foco, se cierra con Escape y
  devuelve el foco al botón que lo abrió.
- «Verificado», «Sin verificar» y «Predeterminada» son texto, no solo iconos.
- El documento enmascarado se anuncia como «documento terminado en 234».
- En 390 px las secciones se apilan y las direcciones pasan a tarjetas.

---

## Fuera de alcance — ¿qué NO hará?

- Cambiar la contraseña desde el perfil (FRONT-10; SPEC-16 lo excluye).
- Cambiar roles o el estado de la cuenta.
- Darse de baja a sí mismo: pendiente de decisión (Q10 en SPEC-01 y SPEC-04).
- Editar o eliminar direcciones, o cambiar la predeterminada sin añadir otra (H-04).
- Verificar el celular (H-03).
- Mostrar el documento completo.

---

## Huecos detectados en el backend

- **H-01** — `Usuario` (respuesta de `/auth/me`) no incluye `mfaHabilitado`,
  `fechaNacimiento` ni los atributos de vendedor (`codigoVendedor`, `tienda`,
  `fechaIngreso`). La pantalla no puede mostrarlos aunque SPEC-16 los defina.
- **H-03** — Tras cambiar el celular no hay manera de verificarlo: el contrato
  remite a `/auth/otp/solicitar` y `/auth/otp/verificar`, pero ambos exigen un
  `challengeToken` que solo emite el login.
- **H-04** — Las direcciones solo se pueden listar y añadir.
- **H-13** — No está decidido si el titular puede registrar o corregir su
  documento: RF-16.2 limita la edición propia a nombres, apellidos y teléfono,
  pero `AtributosUsuario` acepta `tipoDocumento` y `numeroDocumento`. Hasta que
  se decida, la pantalla lo muestra solo de lectura y, si no existe, dice
  «Sin registrar».

---

## Lista de completitud

- [ ] Cada estado de la pantalla está implementado
- [ ] Cada código de error del backend tiene su mensaje
- [ ] El documento solo se muestra enmascarado
- [ ] El `PATCH` envía solo los campos modificados
- [ ] La sección de direcciones solo existe para `CLIENTE`
- [ ] Los escenarios de interfaz están automatizados o verificados a mano con evidencia
- [ ] El wireframe y la implementación coinciden, salvo el interruptor del segundo factor (H-01)
