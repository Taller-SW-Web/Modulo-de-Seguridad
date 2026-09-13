# ADR-001 — Firmar los tokens con RS256 y no con HS256

| Campo | Valor |
|---|---|
| **Estado** | Aceptada |
| **Fecha** | Semana 4 — septiembre de 2026 |
| **Decide** | Sergio Osorio (PO) con Jose Luis Limachi (Tech Lead) |

## Contexto

Los otros seis módulos del marketplace tienen que saber quién hace cada
petición, y la matriz cruzada del curso les prohíbe leer nuestra base de datos.
La única forma de propagar la identidad entre microservicios es un token
firmado que cada servicio pueda verificar por su cuenta.

Hay dos maneras de firmarlo:

- **HS256** usa una clave simétrica: la misma clave firma y verifica.
- **RS256** usa un par de claves: la privada firma, la pública verifica.

## Decisión

Los tokens de acceso se firman con **RS256**. La clave pública se publica en
`GET /api/v1/auth/.well-known/jwks.json`. La privada no sale nunca de este
servicio.

## Por qué

Con HS256 tendríamos que repartir la clave de firma a los seis equipos. Quien
puede verificar también puede firmar: **cualquiera de los seis equipos podría
emitir un token de administrador**, y ante un incidente sería imposible saber
cuál lo hizo. En un trabajo de curso con siete personas por grupo y la clave
circulando por chats, es cuestión de tiempo que acabe en un repositorio público.

Con RS256 los consumidores reciben solo la clave pública. Pueden comprobar que
un token es nuestro; no pueden fabricar uno.

## Consecuencias

**A favor**

- Ningún secreto compartido entre equipos.
- Verificación local sin llamarnos: sin latencia de red ni dependencia de
  nuestra disponibilidad.
- La rotación de clave se resuelve publicando la nueva en el JWKS, sin
  coordinar con nadie.

**En contra**

- Un token de acceso sigue siendo válido hasta que vence, aunque el usuario haya
  sido desactivado o bloqueado en el intervalo.
- Firmar con RSA es más costoso que con HMAC. En nuestra escala es irrelevante.

**Cómo compensamos el punto en contra**

1. La vigencia del token de acceso se limita a **15 minutos**: ese es el retraso
   máximo ante una revocación.
2. Los cambios de estado se propagan además por **evento asíncrono** en RabbitMQ.
3. Para operaciones sensibles —anulaciones, reembolsos, cambios de precio—
   ofrecemos **introspección remota**, que sí refleja el estado actual. Ver
   [ADR-002](002-validacion-local-frente-a-introspeccion.md).

## Alternativas descartadas

| Alternativa | Por qué no |
|---|---|
| HS256 con clave compartida | Cualquier equipo consumidor podría emitir tokens de administrador |
| Tokens opacos con introspección obligatoria | Una llamada de red en cada petición de los seis módulos; nos convierte en punto único de fallo del marketplace |
| Sesiones en servidor compartidas | Exigiría estado compartido entre módulos, que es justo lo que la arquitectura de microservicios del curso prohíbe |
