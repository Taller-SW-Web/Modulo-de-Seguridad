# FRONT-03 — Verificación de correo

| Campo | Valor |
|---|---|
| **Responsable** | Eva Lucía Moreno Zevallos |
| **Épica principal** | [EP-01](../epicas/EP-01-registro-gestion-usuarios.md) |
| **Specs de backend que consume** | SPEC-02 (y SPEC-16 para confirmar un cambio de correo) |
| **Wireframe** | Stitch, proyecto `889177073946089595`, pantalla 3 (numeración antigua; ver [`trazabilidad.md`](../trazabilidad.md) §1.1). Figma: pendiente |
| **Estado** | Borrador |
| **Aprobada por** | Pendiente |

---

## Objetivo — ¿para qué sirve esta pantalla?

Tiene dos entradas:

1. **Después de registrarse** (FRONT-02) o desde el enlace «¿No te llegó el
   correo de verificación?» de FRONT-01: el usuario espera el correo y puede
   pedir otro.
2. **Desde el enlace del correo**, que trae el token: la pantalla confirma el
   correo y deja pasar al inicio de sesión del canal donde se registró
   (ver «Retorno por canal»).

El mismo enlace sirve para confirmar un cambio de correo pedido en *Mi cuenta*
(RF-02.6, RF-16.6); la pantalla no distingue los dos casos.

---

## Endpoints que usa

| Momento | Endpoint | Spec de backend |
|---|---|---|
| Al abrir la pantalla con un token en la URL | `POST /api/v1/auth/verificar-correo` | SPEC-02, SPEC-16 |
| Al pulsar «Reenviar enlace» | `POST /api/v1/auth/verificar-correo/reenviar` | SPEC-02 |

---

## Estados de la pantalla

| Estado | Cuándo ocurre | Qué ve el usuario |
|---|---|---|
| Revisa tu correo | Llega desde FRONT-02 con el correo en memoria | «Te enviamos un enlace a m\*\*\*a@ejemplo.com.» con el correo enmascarado por la SPA, aviso de que vence en 24 horas, consejo de revisar la carpeta de spam y botón «Reenviar enlace» |
| Reenvío | Llega sin correo en memoria (enlace de FRONT-01, o recargó la página) | Campo de correo y botón «Enviar un enlace nuevo» |
| Enviando | Tras pulsar reenviar | Botón deshabilitado con «Enviando…» |
| Reenvío aceptado | `202` | Mensaje de confirmación neutro (ver textos). El botón vuelve a estar disponible |
| Demasiadas solicitudes | `429 DEMASIADAS_SOLICITUDES` | Mensaje y botón de reenvío deshabilitado mientras siga en la pantalla |
| Verificando | Llega con token; mientras responde el backend | Indicador «Estamos verificando tu correo…» sin formulario |
| Verificado | `204` | «Tu correo quedó verificado.» y botón «Iniciar sesión»: al login del canal si el enlace trae uno con URL permitida (ver «Retorno por canal»); si no, a FRONT-01 con su aviso. Si el usuario ya tenía sesión abierta, el botón es «Ir a Mi cuenta» |
| Enlace vencido | `410 ENLACE_EXPIRADO` | Explicación y el formulario de reenvío, sin datos de la cuenta |
| Enlace ya usado | `410 ENLACE_YA_USADO` | Explicación, botón «Iniciar sesión» (mismo destino que en «Verificado») y el formulario de reenvío por si hiciera falta |
| Enlace incompleto | La URL tiene el parámetro del token vacío | Mismo contenido que «Enlace vencido», sin llamar al backend |

---

## Retorno por canal (RF-02.8, FEAT-01-T4)

El enlace del correo es `{SPA}/verificar-correo?token=…`, y fuera de `WEB` añade
`&canal=<CANAL>` con el canal de origen del registro: `MARKETPLACE`, `RETAIL` o
`CHATBOT` (lista cerrada; sin parámetro vale `WEB`). **El enlace nunca trae una
URL**: la SPA traduce el canal a la URL de login con su propia lista, para que
no haya redirección abierta.

| Canal | Destino del botón «Iniciar sesión» | Configuración por entorno (SPA) |
|---|---|---|
| `WEB`, `CHATBOT` | FRONT-01 | — |
| `MARKETPLACE` | Login de Marketplace (G1) | `VITE_RETORNO_MARKETPLACE` |
| `RETAIL` | Login configurado para Retail* | `VITE_RETORNO_RETAIL` |

\* SPEC-02 RF-02.8 lo envía al login de Marketplace; la descripción de
`/auth/registro` en el OpenAPI, al de Retail (A1). Pendiente de que el PO lo
unifique; la variable propia sirve para cualquiera de los dos.

- La SPA solo usa una URL de esa lista: absoluta, `https` (o `http://localhost`
  en desarrollo) y sin parámetros. Canal desconocido o manipulado, variable vacía
  o URL inválida → FRONT-01.
- Al cargar, la SPA quita de la barra de direcciones el token **y** el canal
  (FRONT-00 §1.5) y conserva el canal solo en memoria.
- La URL de retorno se abre tal cual: **nunca** se le añade token, correo ni otro
  dato de la cuenta.
- El retorno no es automático: lo inicia el usuario con el botón, para que vea
  antes el resultado.
- `CHATBOT` usa por ahora nuestra pantalla; su pantalla propia (RF-02.8) queda
  pendiente de su URL. Los valores de cada entorno los coordina David (FEAT-11-T1).

---

## Validaciones del lado del cliente

| Campo | Regla | Mensaje | Origen |
|---|---|---|---|
| Correo (reenvío) | Obligatorio y con formato de correo | «Escribe un correo válido, como nombre@dominio.com.» | Esquema de `/verificar-correo/reenviar` |

**Enmascarado del correo.** La SPA lo enmascara con la misma forma que usa el
contrato para el OTP (`m****a@ejemplo.com`): primera y última letra de la parte
local y el dominio completo. El registro no devuelve el correo; la SPA usa el
que el usuario escribió.

---

## Textos y mensajes

| Código del backend | Mensaje en pantalla |
|---|---|
| `202` del reenvío | «Si hay una cuenta pendiente de verificar con ese correo, te enviamos un enlace nuevo. El anterior deja de funcionar.» |
| `DEMASIADAS_SOLICITUDES` | «Ya pediste varios enlaces en la última hora. Espera un rato antes de pedir otro.» |
| `204` | «Tu correo quedó verificado.» |
| `ENLACE_EXPIRADO` | «Este enlace venció: los enlaces de verificación duran 24 horas. Pide uno nuevo.» |
| `ENLACE_YA_USADO` | «Este enlace ya se usó. Si ya verificaste tu correo, puedes iniciar sesión.» |
| `VALIDACION`, `NO_DISPONIBLE` | Según FRONT-00 §2 |

> **Por qué el mensaje de reenvío es condicional.** El backend responde `202`
> exista o no la cuenta y esté o no verificada (RF-02.4). Si la pantalla dijera
> «te enviamos un enlace», afirmaría que existe una cuenta pendiente con ese
> correo.

---

## Escenarios de interfaz — ¿cómo verificamos?

### UI-03.1 Espera tras el registro
- **Dado** que el visitante acaba de registrarse con `maria@ejemplo.com`
- **Cuando** llega a esta pantalla
- **Entonces** ve «Te enviamos un enlace a m\*\*\*a@ejemplo.com.» y el botón
  «Reenviar enlace».

### UI-03.2 Verificación correcta
- **Dado** un enlace de verificación válido
- **Cuando** el usuario lo abre
- **Entonces** el token desaparece de la barra de direcciones, ve «Estamos
  verificando tu correo…» y luego «Tu correo quedó verificado.» con el botón
  «Iniciar sesión».

### UI-03.3 Enlace vencido *(caso borde)*
- **Dado** un enlace de hace más de 24 horas
- **Cuando** el usuario lo abre
- **Entonces** ve el mensaje de enlace vencido y el formulario para pedir uno
  nuevo, sin ningún dato de la cuenta.

### UI-03.4 Reenvío a un correo cualquiera *(caso borde de seguridad)*
- **Dado** un correo que no tiene cuenta
- **Cuando** se pide un reenvío con él
- **Entonces** la pantalla muestra el mismo mensaje que con una cuenta real.

### UI-03.5 Cuarto reenvío en una hora *(caso borde)*
- **Dado** que ya se pidieron tres reenvíos para ese correo en la última hora
- **Cuando** se pide el cuarto
- **Entonces** aparece el mensaje de demasiadas solicitudes y el botón queda
  deshabilitado.

### UI-03.6 Abrir dos veces el mismo enlace *(caso borde)*
- **Dado** un enlace que ya se usó
- **Cuando** el usuario vuelve a abrirlo
- **Entonces** ve «Este enlace ya se usó…» con el botón «Iniciar sesión».

### UI-03.8 Verificación desde Marketplace
- **Dado** una cuenta registrada desde Marketplace y `VITE_RETORNO_MARKETPLACE`
  configurada
- **Cuando** el usuario abre el enlace y la verificación responde `204`
- **Entonces** el token y el canal desaparecen de la barra de direcciones, y
  «Iniciar sesión» lleva al login de Marketplace sin token ni correo en la URL.

### UI-03.9 Canal manipulado en el enlace *(caso borde de seguridad)*
- **Dado** un enlace con `canal=https://otro-sitio.example` o un canal fuera de
  la lista
- **Cuando** la verificación responde `204`
- **Entonces** «Iniciar sesión» lleva a FRONT-01: la SPA nunca navega a una URL
  recibida en el enlace.

### UI-03.7 Recarga en la espera *(caso borde)*
- **Dado** el estado «Revisa tu correo»
- **Cuando** el usuario recarga la página
- **Entonces** la pantalla pasa al estado «Reenvío» con el campo de correo
  vacío: el correo no se guardó fuera de la memoria.

---

## Accesibilidad y diseño adaptable

- Se aplica FRONT-00 (incluido §1.5 sobre tokens en la URL).
- El paso de «Verificando» a su resultado se anuncia con `aria-live="polite"`
  y el foco va al título del resultado.
- El correo enmascarado se lee completo con un texto alternativo para lectores
  de pantalla: «correo terminado en ejemplo.com».

---

## Fuera de alcance — ¿qué NO hará?

- Mostrar cuánto falta para que venza el enlace o cuántos reenvíos quedan: el
  backend no lo devuelve y revelaría el estado de la cuenta.
- Verificar el celular (SPEC-10, acuerdo A2).
- Verificar el correo sin el enlace (por ejemplo, con un código escrito a mano).

---

## Huecos detectados en el backend

- **H-05** — `DEMASIADAS_SOLICITUDES` no dice cuándo se puede reintentar.
  *Resuelto en FEAT-01-T3:* el `429` del reenvío trae la cabecera `Retry-After`
  en segundos; la pantalla puede usarla para volver a habilitar el botón.

---

## Lista de completitud

- [ ] Cada estado de la pantalla está implementado
- [ ] El mensaje de reenvío no confirma que exista la cuenta
- [ ] El token y el canal se quitan de la URL al cargar
- [ ] El retorno por canal solo usa URLs de la lista del entorno
- [ ] Los escenarios de interfaz están automatizados o verificados a mano con evidencia
- [ ] El wireframe y la implementación coinciden
