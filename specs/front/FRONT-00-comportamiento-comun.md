# FRONT-00 — Comportamiento común de la SPA

| Campo | Valor |
|---|---|
| **Responsable** | Por asignar |
| **Specs de backend que consume** | SPEC-06, SPEC-07, SPEC-11 · catálogo de errores |
| **Wireframe** | No aplica: no es una pantalla, es lo que comparten todas |
| **Estado** | Borrador |
| **Aprobada por** | Pendiente |

> **Por qué existe esta spec.** SPEC-06 (renovación y cierre de sesión) no
> tiene pantalla propia, pero cada pantalla autenticada depende de ella. Lo
> mismo pasa con los errores transversales del catálogo y con el medidor de
> fuerza de contraseña, que usan cuatro pantallas. Aquí se definen una vez y
> las demás specs de interfaz los citan en vez de repetirlos.

---

## Objetivo — ¿para qué sirve?

Fijar el comportamiento que toda pantalla de la SPA hereda: cómo se guarda,
renueva y cierra la sesión; cómo se protegen las rutas según el rol; cómo se
traduce cada error transversal a un mensaje; y cómo funciona el medidor de
fuerza de contraseña. Si una spec de pantalla no dice otra cosa, se aplica esto.

---

## Endpoints que usa

| Momento | Endpoint | Spec de backend |
|---|---|---|
| Una petición autenticada recibe `401 TOKEN_INVALIDO`, o el token de acceso está a punto de vencer | `POST /api/v1/auth/refresh` | SPEC-06 |
| El usuario pulsa «Cerrar sesión» | `POST /api/v1/auth/logout` | SPEC-06 |
| Se monta un formulario que establece una contraseña | `GET /api/v1/password/politica` | SPEC-07 |

---

## 1. Sesión

### 1.1 Qué se guarda y dónde

| Dato | Origen | Dónde se guarda |
|---|---|---|
| `accessToken` | `SesionIniciada` | **Solo en memoria.** Nunca en `localStorage`, en la URL ni en registros de consola |
| `refreshToken` | `SesionIniciada` | Pendiente de decisión (ver H-11). Hasta que exista, en memoria: recargar la página obliga a iniciar sesión |
| `usuario` (`id`, `nombreCompleto`, `correo`, `roles`) | `SesionIniciada` | En memoria, junto a la sesión |
| `permisos` | Claim `permisos` del `accessToken` | Se lee decodificando la carga útil del token, **sin** verificar la firma: solo sirve para mostrar u ocultar opciones. Quien decide es el backend |
| `challengeToken` | `DesafioMfa` | Solo en memoria, mientras dura el desafío (FRONT-04) |

### 1.2 Renovación (SPEC-06)

- El token de acceso dura `expiresIn` segundos (900). La SPA lo renueva con
  `POST /auth/refresh` cuando falta menos de un minuto para que venza, o al
  recibir `401 TOKEN_INVALIDO` en una petición autenticada.
- **Una sola renovación en vuelo.** Si varias peticiones reciben `401` a la
  vez, esperan todas a la misma renovación. El token de refresco se rota en
  cada uso (RF-06.1): dos renovaciones en paralelo con el mismo token se
  interpretan como reutilización y **revocan la sesión entera** (RF-06.3).
- Si hay varias pestañas compartiendo sesión, se aplica la misma regla entre
  pestañas (bloqueo compartido, p. ej. `navigator.locks` o `BroadcastChannel`).
  Depende de la decisión H-11.
- Tras renovar, la petición original se reintenta **una sola vez**. Si vuelve
  a fallar con `401`, se cierra la sesión local.
- `401 REFRESCO_INVALIDO`: se borra la sesión local y se lleva al usuario a
  FRONT-01 con el aviso «Tu sesión terminó. Inicia sesión de nuevo.». No se
  distingue entre vencida, revocada o reutilizada: el usuario no puede hacer
  nada distinto en cada caso.

### 1.3 Cierre de sesión

- «Cerrar sesión» llama a `POST /auth/logout` con el `refreshToken` y **borra
  la sesión local pase lo que pase**: si la red falla, el usuario igualmente
  queda fuera en ese dispositivo.
- Después lleva a FRONT-01 con el aviso «Cerraste sesión.».

### 1.4 Rutas protegidas

| Tipo de ruta | Pantallas | Regla |
|---|---|---|
| Pública | FRONT-01, 02, 03, 04, 05, 12 | Accesible sin sesión. Con sesión abierta, FRONT-01 y FRONT-02 redirigen al destino por defecto |
| Autenticada | FRONT-06, 09, 10, 13 | Sin sesión, lleva a FRONT-01 recordando la ruta pedida |
| Administración | FRONT-07, 08, 11, 14 | Además exige el permiso de la tabla de cada spec (`usuario.ver`, `auditoria.ver`…). Sin él, muestra «No tienes permiso para ver esta página.» y no llama al endpoint |

- **Destino tras iniciar sesión.** La ruta recordada, si la había; si no, el
  panel de administración (FRONT-07) para quien tenga `usuario.ver`, y
  *Mi cuenta* (FRONT-06) para el resto.
- La ruta recordada viaja como parámetro `volver` y **solo se acepta si es una
  ruta interna relativa** (empieza por `/` y no por `//`). Cualquier otra se
  ignora: es una redirección abierta, el mismo riesgo que RF-02.8 evita en el
  backend.

### 1.5 Enlaces que llegan por correo

Las pantallas FRONT-03, FRONT-05 y FRONT-12 reciben un token en la URL.

- La URL de cada pantalla debe coincidir con la que el backend tiene
  configurada para el canal `WEB` (RF-02.8). Se acuerda con quien configura el
  servicio.
- Al cargar, la SPA lee el token y **lo quita de la barra de direcciones**
  (`history.replaceState`), para que no quede en el historial ni se comparta
  al copiar la URL.
- Esas pantallas se sirven con `Referrer-Policy: no-referrer` y no cargan
  recursos de terceros, para que el token no se filtre por la cabecera
  `Referer`.

---

## 2. Errores transversales

La SPA **ramifica por `code`, nunca por `detail`** (catálogo de errores,
convenciones). Los mensajes son de la interfaz: el texto de `detail` no se
muestra salvo que una spec de pantalla lo diga.

| Código del backend | Qué hace la SPA | Mensaje en pantalla |
|---|---|---|
| `VALIDACION` | Pinta cada elemento de `errores[]` bajo su campo (`campo`). Si un `campo` no corresponde a ningún control, lo muestra arriba del formulario | El `mensaje` de cada error; si falta, «Revisa este campo.» |
| `TOKEN_INVALIDO` en una ruta autenticada | Renueva (§1.2) | — |
| `SCOPE_INSUFICIENTE` | No reintenta | «No tienes permiso para realizar esta acción.» |
| `NO_ENCONTRADO` | Según la pantalla | «No encontramos lo que buscas.» |
| `DEMASIADAS_SOLICITUDES` | Deshabilita la acción que lo provocó hasta que el usuario recargue o cambie de pantalla | Cada pantalla tiene su texto. No se promete un tiempo concreto de espera (H-05) |
| `NO_DISPONIBLE` | Ofrece reintentar | «El servicio no está disponible en este momento. Intenta de nuevo en unos minutos.» |
| Sin respuesta (red caída, tiempo agotado) | Ofrece reintentar. No reenvía solo un formulario que cambia datos | «No pudimos conectar. Revisa tu conexión e intenta de nuevo.» |
| Cualquier código que la SPA no conozca | Lo trata como error genérico y registra el `instance` para soporte | «Algo salió mal. Intenta de nuevo.» |

> **Cuidado con `401`.** No todo `401` es una sesión vencida:
> `CREDENCIALES_INVALIDAS` en `/password/cambiar` o `/usuarios/{id}/correo`
> significa «la contraseña actual no coincide», y `CODIGO_INVALIDO` es un
> código OTP incorrecto. Solo `TOKEN_INVALIDO` dispara la renovación.

---

## 3. Medidor de fuerza de contraseña

Lo usan FRONT-02, FRONT-05, FRONT-10 y FRONT-11.

- Se construye con **la respuesta de `GET /password/politica`**, nunca con
  reglas escritas en el código de la SPA (RF-07.11). La política se descarga
  una vez por visita y se reutiliza.
- Muestra una lista de reglas; cada una en uno de tres estados, **con texto y
  con icono, nunca solo con color**:

| Estado | Cuándo |
|---|---|
| Cumple | La SPA puede comprobarla y se cumple |
| No cumple | La SPA puede comprobarla y no se cumple |
| Se comprueba al guardar | La SPA no puede comprobarla: contraseña común, historial y, cuando no conoce los datos del usuario, datos personales |

| Campo de la política | Regla que muestra | ¿La comprueba la SPA? |
|---|---|---|
| `longitudMinima` | «Al menos {longitudMinima} caracteres» | Sí |
| `requiereMayuscula` | «Una letra mayúscula» | Sí |
| `requiereMinuscula` | «Una letra minúscula» | Sí |
| `requiereDigito` | «Un número» | Sí |
| `requiereCaracterEspecial` | «Un carácter especial, como ! @ # $» | Sí |
| `rechazaComunes` | «Que no sea una contraseña común» | No: la lista vive en el backend |
| `rechazaDatosPersonales` | «Que no contenga tu nombre, tu apellido ni tu correo» | Orientativa, solo si la pantalla conoce esos datos. El backend decide |
| `historial` | «Que no sea ninguna de tus últimas {historial}» | No. Solo en FRONT-05 y FRONT-10, donde ya hay contraseñas anteriores |

- Un campo booleano en `false` o ausente oculta su regla.
- **Si la política no carga**, el medidor muestra «No pudimos cargar los
  requisitos de la contraseña; los comprobaremos al guardar.» y el formulario
  sigue funcionando. Nunca se usa una copia local de las reglas.
- El medidor no bloquea el envío: el backend es quien valida (RF-07.5).
- Cambios anunciados con `aria-live="polite"`, sin robar el foco.

### Mensajes de `POLITICA_INCUMPLIDA`

Cuando el backend responde `422 POLITICA_INCUMPLIDA`, cada elemento de
`errores[]` marca su regla como «No cumple» en el medidor y añade su mensaje
bajo el campo de contraseña.

| `regla` | Mensaje |
|---|---|
| `LONGITUD_MINIMA` | «Debe tener al menos {esperado} caracteres.» |
| `MAYUSCULA` | «Falta una letra mayúscula.» |
| `MINUSCULA` | «Falta una letra minúscula.» |
| `DIGITO` | «Falta un número.» |
| `CARACTER_ESPECIAL` | «Falta un carácter especial.» |
| `CONTRASENA_COMUN` | «Es una contraseña demasiado común. Elige otra.» |
| `DATOS_PERSONALES` | «No puede contener tu nombre, tu apellido ni tu correo.» |
| `YA_UTILIZADA` | «Ya usaste esta contraseña hace poco. Elige una distinta.» |
| Regla desconocida | «La contraseña no cumple los requisitos.» |

---

## 4. Vocabulario que ve el usuario

Los códigos del contrato no se muestran tal cual. Las etiquetas salen de aquí,
porque el catálogo de roles (`GET /roles`) no es accesible con un token de
usuario (H-09).

| Estado de cuenta | Etiqueta |
|---|---|
| `ACTIVO` | Activa |
| `PENDIENTE_VERIFICACION` | Pendiente de verificación |
| `BLOQUEADO` | Bloqueada |
| `INACTIVO` | Inactiva |

| Rol | Etiqueta |
|---|---|
| `CLIENTE` | Cliente |
| `VENDEDOR` | Vendedor |
| `ADMIN_VENTAS` | Administrador de ventas |
| `GESTOR_DESPACHO` | Gestor de despacho |
| `GESTOR_COMERCIAL` | Gestor comercial |
| `ADMIN_SISTEMA` | Administrador del sistema |

- Las fechas llegan en UTC y se muestran en la hora de Lima con el formato
  `dd/mm/aaaa hh:mm`.
- El número de documento se muestra solo como llega (`documentoEnmascarado`).
  Ninguna pantalla muestra una contraseña, un token ni un código OTP fuera
  del campo donde se escribe.

---

## Escenarios de interfaz — ¿cómo verificamos?

### UI-00.1 Renovación transparente
- **Dado** un usuario con sesión cuyo token de acceso venció hace un momento
- **Cuando** abre una pantalla que hace una petición autenticada
- **Entonces** la SPA renueva la sesión, repite la petición y la pantalla se
  muestra sin avisos ni saltos al inicio de sesión.

### UI-00.2 Dos peticiones a la vez con el token vencido *(caso borde)*
- **Dado** una pantalla que lanza tres peticiones en paralelo con un token vencido
- **Cuando** las tres reciben `401 TOKEN_INVALIDO`
- **Entonces** se hace **una sola** llamada a `/auth/refresh`, las tres se
  reintentan con el token nuevo y la sesión sigue abierta.

### UI-00.3 Sesión revocada *(caso borde)*
- **Dado** que un administrador cambió los roles del usuario (sus sesiones se revocan, RF-11.5)
- **Cuando** la SPA intenta renovar y recibe `401 REFRESCO_INVALIDO`
- **Entonces** se borra la sesión local y se muestra FRONT-01 con «Tu sesión
  terminó. Inicia sesión de nuevo.».

### UI-00.4 Cerrar sesión sin conexión *(caso borde)*
- **Dado** un usuario con sesión y sin conexión
- **Cuando** pulsa «Cerrar sesión»
- **Entonces** la sesión local se borra igualmente y se muestra FRONT-01.

### UI-00.5 Redirección abierta *(caso borde de seguridad)*
- **Dado** un enlace a `/login?volver=https://sitio-ajeno.com`
- **Cuando** el usuario inicia sesión
- **Entonces** la SPA ignora `volver` y lleva al destino por defecto.

### UI-00.6 Ruta de administración sin permiso *(caso borde)*
- **Dado** un cliente con sesión
- **Cuando** escribe a mano la ruta del panel de administración
- **Entonces** la SPA muestra «No tienes permiso para ver esta página.» sin
  llamar a `GET /usuarios`.

### UI-00.7 La política no carga *(caso borde)*
- **Dado** que `GET /password/politica` responde `503`
- **Cuando** el usuario abre un formulario con medidor de fuerza
- **Entonces** el medidor muestra el aviso de §3 y el formulario se puede
  enviar; el backend valida.

---

## Accesibilidad y diseño adaptable

Reglas que valen para todas las pantallas:

- WCAG 2.1 nivel AA: contraste mínimo 4.5:1 en texto, foco visible en todo
  control, orden de tabulación que sigue el orden visual.
- Todo campo tiene `<label>` visible. Los errores de campo se enlazan con
  `aria-describedby` y el resumen de errores del formulario se anuncia con
  `aria-live="assertive"`; al fallar un envío, el foco va al primer campo con error.
- Los estados de cuenta y de reglas se distinguen **por texto**, no solo por
  color: los wireframes son en gris y así debe seguir funcionando.
- Los campos usan el `autocomplete` correcto: `email`, `current-password`,
  `new-password`, `one-time-code`, `tel`, `given-name`, `family-name`.
- Todo botón que envía queda deshabilitado y muestra un indicador mientras
  espera respuesta: nunca se envía dos veces.
- Diseño para 390 px (móvil) y 1440 px (escritorio), las dos anchuras de los
  wireframes. Sin desplazamiento horizontal en 390 px salvo dentro de tablas.
- Ningún dato sensible (contraseña, token, código, documento completo) se
  escribe en la consola, en analítica ni en mensajes de error.

---

## Fuera de alcance — ¿qué NO hará?

- Decidir dónde se guarda el token de refresco: es una decisión de
  arquitectura pendiente (H-11).
- «Cerrar sesión en los demás dispositivos»: no hay endpoint.
- Validar la firma del token en la SPA: eso es de los módulos consumidores
  (SPEC-17). La SPA solo lee sus claims para la interfaz.
- El sistema de diseño (colores, tipografía): es del Hito 2.

---

## Huecos detectados en el backend

Se anotan aquí y no se inventa una solución. Lista completa en el
[README](README.md#huecos-detectados-en-el-backend-y-el-contrato).

- **H-05** — `DEMASIADAS_SOLICITUDES` no dice cuándo se puede reintentar.
- **H-09** — `GET /roles` solo acepta token de servicio.
- **H-11** — No hay decisión sobre dónde guarda la SPA el token de refresco.

---

## Lista de completitud

- [ ] La renovación nunca lanza dos `/auth/refresh` en paralelo, tampoco entre pestañas
- [ ] Cerrar sesión borra la sesión local aunque falle la red
- [ ] Las rutas de administración comprueban el permiso antes de pedir datos
- [ ] `volver` solo acepta rutas internas
- [ ] Los tokens de los enlaces de correo se quitan de la URL al cargar
- [ ] Cada error transversal tiene su mensaje y ninguno muestra `detail` sin querer
- [ ] El medidor se construye con `GET /password/politica` y funciona si no carga
