# Catálogo de eventos asíncronos

| Campo | Valor |
|---|---|
| **Dueño** | Product Owner — es parte del contrato |
| **Transporte** | RabbitMQ |
| **Consumen** | Los seis módulos del marketplace |
| **Estado** | Borrador — pendiente de acuerdo con los seis equipos |

`SPEC-09` declara los eventos fuera de su alcance y dice que «se especifican por
separado». Este documento es ese aparte. Hasta que existiera, los eventos
viajaban nombrados en cuatro specs distintas sin que nadie hubiera definido su
carga útil ni sus garantías.

---

## Para qué sirven

Es la **vía 3** de integración, la que existe para que un módulo no tenga que
preguntarnos. Las otras dos están en SPEC-09:

| Vía | Cuándo | Coste |
|---|---|---|
| 1. Validación local con JWKS | Cada petición ordinaria | Ninguna llamada |
| 2. Introspección síncrona | Antes de una operación sensible | Una llamada y una dependencia de nuestra disponibilidad |
| **3. Eventos** | **Para enterarse de bajas, bloqueos y cambios de rol sin preguntar** | **Consistencia eventual** |

Un módulo que cachea datos de usuario se suscribe a estos eventos e invalida su
caché cuando llegan. Sin ellos, la única forma de enterarse de que una cuenta
fue desactivada es esperar a que venza su token —hasta 15 minutos— o llamar a
la introspección en cada petición, que nos convierte en punto único de fallo.

---

## Topología

| Elemento | Valor |
|---|---|
| **Exchange** | `seguridad.usuarios` |
| **Tipo** | `topic`, duradero |
| **Routing key** | El nombre del evento, tal cual: `usuario.creado` |
| **Cola por consumidor** | Cada módulo declara la suya: `<modulo>.usuarios`, duradera |
| **Binding sugerido** | `usuario.*` para recibirlos todos |
| **Cola de descarte** | `seguridad.usuarios.dlq`, tras 3 reintentos |

Cada módulo consumidor **declara y es dueño de su propia cola**. Nosotros solo
publicamos en el exchange: no sabemos quién escucha ni nos enteramos si alguien
deja de hacerlo.

---

## Forma del mensaje

Todos los eventos comparten sobre. Lo que cambia es `datos`.

```json
{
  "eventoId": "0f8f6b4e-2c1a-4b7e-9a3d-5e6f7a8b9c0d",
  "tipo": "usuario.bloqueado",
  "version": 1,
  "fecha": "2026-09-13T14:32:07.482Z",
  "usuarioId": "11111111-1111-1111-1111-111111111111",
  "datos": { }
}
```

| Campo | Contenido |
|---|---|
| `eventoId` | Identificador único del mensaje. **Sirve para deduplicar** |
| `tipo` | El nombre del evento, igual que la routing key |
| `version` | Versión del esquema de `datos`. Empieza en 1 |
| `fecha` | Instante del hecho, UTC con milisegundos |
| `usuarioId` | La cuenta afectada. Presente en todos los eventos |
| `datos` | Carga específica del evento. Puede estar vacía |

**El evento notifica un hecho, no transporta el estado completo.** Quien
necesite los datos actuales del usuario los pide por `GET /usuarios/{id}`. Esto
es deliberado: si el evento llevara el estado, dos eventos desordenados dejarían
al consumidor con datos viejos creyendo que son nuevos.

---

## Los siete eventos

### `usuario.creado`

Publica **SPEC-01**, al crearse una cuenta con el correo ya verificado.

```json
"datos": { "roles": ["CLIENTE"] }
```

No se publica al registrarse, sino al verificar el correo: una cuenta en
`PENDIENTE_VERIFICACION` todavía no puede operar y anunciarla induce a los
consumidores a crear registros que quizá nunca se usen.

### `usuario.desactivado`

Publica **SPEC-01**, tras una baja lógica.

```json
"datos": { "motivo": "BAJA_ADMINISTRATIVA" }
```

Quien reciba esto debe dejar de aceptar el token de ese usuario aunque no haya
vencido.

### `usuario.reactivado`

Publica **SPEC-01**, cuando un administrador reactiva una cuenta dada de baja.

```json
"datos": { "roles": ["CLIENTE"] }
```

**No es `usuario.creado`**, aunque lo parezca: la cuenta ya existía, con el
mismo identificador. Un consumidor que al recibir `usuario.creado` cree un
registro local lo duplicaría. Quien haya marcado la cuenta como inactiva al
recibir `usuario.desactivado` la vuelve a marcar como activa.

### `usuario.bloqueado`

Publica **SPEC-07**, tanto en el bloqueo automático como en el manual.

```json
"datos": { "automatico": true, "hasta": "2026-09-13T15:02:07.482Z" }
```

`hasta` es `null` cuando el bloqueo no vence: el manual, y el automático a partir
del cuarto seguido.

**Si `hasta` tiene fecha, a partir de ese instante la cuenta ya no está
bloqueada, y no llegará ningún `usuario.desbloqueado` que lo avise.** El
vencimiento no es un suceso: el estado se calcula comparando la hora. El
consumidor que cachee el estado debe guardar `hasta` y dejar de considerar
bloqueada la cuenta cuando pase.

### `usuario.desbloqueado`

Publica **SPEC-07**, solo cuando alguien levanta el bloqueo de forma explícita:
un administrador, o el titular con el enlace de desbloqueo o restableciendo su
contraseña. **El vencimiento de un bloqueo no lo publica** (ver
`usuario.bloqueado`).

```json
"datos": { "via": "ADMINISTRADOR" }
```

`via` es `ADMINISTRADOR`, `ENLACE` o `RESTABLECIMIENTO`.

### `usuario.roles_cambiados`

Publica **SPEC-05**, tras asignar o revocar un rol.

```json
"datos": { "roles": ["VENDEDOR", "GESTOR_COMERCIAL"] }
```

Lleva **la lista completa resultante**, no el delta. Un consumidor que reciba
dos eventos desordenados se queda con el estado del último, no con una suma
incoherente de cambios.

Las sesiones activas del usuario ya se revocaron antes de publicar, así que el
token anterior deja de ser aceptado por introspección aunque el consumidor tarde
en procesar el evento.

### `usuario.atributos_actualizados`

Publica **SPEC-08**, al cambiar datos de perfil.

```json
"datos": { "campos": ["celular", "direcciones"] }
```

Un cambio de correo confirmado también lo publica, con `"campos": ["correo"]`.

Lleva **qué campos cambiaron, nunca sus valores**. Un evento con el número de
documento dentro acabaría replicando datos personales en seis bases de datos
ajenas, y la Ley N.º 29733 nos hace responsables de esa copia.

---

## Garantías, y lo que no garantizamos

| Garantía | Qué significa para el consumidor |
|---|---|
| **Al menos una vez** | Un evento puede llegar repetido. **Deduplica por `eventoId`** |
| **Sin orden global** | Dos eventos de usuarios distintos pueden llegar en cualquier orden |
| **Orden por usuario, no garantizado** | Usa `fecha` para descartar un evento más viejo que el último procesado de esa cuenta |
| **Sin entrega garantizada** | Si tu cola estuvo caída más allá de la retención, perdiste eventos. Reconcilia con `POST /usuarios/lote` |
| **Publicación tras confirmar** | Solo publicamos después de que la transacción se confirmó. Nunca llega un evento de algo que no ocurrió |

**El evento es una optimización, no la fuente de verdad.** La fuente de verdad
es la API. Un consumidor que no tolere perder un evento debe reconciliar
periódicamente; uno que necesite certeza inmediata usa la introspección.

---

## Versionado

Mismas reglas que el contrato REST:

- Añadir un campo a `datos` es compatible y **no** sube `version`.
- Quitar o renombrar un campo sube `version` y obliga a publicar los dos
  esquemas durante al menos una semana.
- Un evento nuevo no rompe a nadie: quien no lo entienda lo ignora.
- Los consumidores deben **ignorar los campos que no conocen**, no fallar.

---

## Estado de implementación

| Qué | Cuándo |
|---|---|
| Este catálogo acordado con los seis equipos | Antes del viernes 18 |
| Topología en `docker-compose.yml` | Hito 2 |
| Publicación real de los siete eventos | Hito 4 (Sem. 11), junto con la implementación de SPEC-09 |

Hasta el Hito 4 no se publica nada. Los consumidores que necesiten enterarse de
un cambio antes de esa fecha usan la introspección, y la ventana de incoherencia
es de 15 minutos.

---

## Relación con la auditoría

No confundir. **SPEC-06 registra hacia dentro; estos eventos anuncian hacia
fuera.** Un bloqueo produce las dos cosas: un registro en `auditoria_seguridad`
que nadie fuera del módulo ve, y un `usuario.bloqueado` que los seis módulos
consumen. Ni el registro viaja por RabbitMQ ni el evento se guarda como
auditoría.
