# ADR-006 — No exponemos ninguna consulta por correo ni por celular

| Campo | Valor |
|---|---|
| **Estado** | Aceptada |
| **Fecha** | 23 de septiembre de 2026 |
| **Decide** | Sergio Osorio (PO) |

## Contexto

Varios módulos consumidores necesitan saber si la persona con la que están
tratando ya es cliente del marketplace. La forma natural de pedirlo es
«¿existe una cuenta con este correo?» o «¿este celular pertenece a alguien?».

El canal Chatbot lo pidió de forma explícita en su especificación de validación
de identidad, y es previsible que Retail y Marketplace lo pidan también.

## Decisión

**No existe ni existirá un endpoint que responda si un correo o un celular
pertenecen a una cuenta.** Nuestras consultas son por identificador de usuario:
`GET /usuarios/{id}` y `POST /usuarios/lote`.

Esta regla alcanza también a las respuestas: ningún código de error, ningún
tiempo de respuesta y ningún límite de tasa puede permitir deducirlo. Por eso
el registro responde `409 CORREO_NO_DISPONIBLE` con un texto que no confirma
nada, el reenvío de verificación responde `202` exista o no la cuenta, y el
login responde `401 CREDENCIALES_INVALIDAS` para cuenta inexistente, bloqueada,
inactiva o sin verificar.

## Por qué

**Un endpoint así es un enumerador de cuentas.** Con una lista de correos
filtrada de cualquier otro sitio, quien tenga credenciales de un módulo
consumidor puede saber en minutos cuáles pertenecen a clientes del marketplace.
Eso es un dato personal por sí mismo —revela que esa persona compra aquí— y es
la antesala del relleno de credenciales dirigido.

**El riesgo no lo corre quien lo pide.** El módulo que consulta obtiene una
comodidad; el daño, si esas credenciales se filtran, lo recibe el titular de la
cuenta y lo responde el marketplace completo.

**Es coherente con todo lo demás.** El módulo ya paga el costo de no enumerar en
cuatro flujos distintos. Abrir una consulta directa haría inútiles esas cuatro
defensas.

## Qué ofrecemos en su lugar

| Necesidad real | Cómo se resuelve |
|---|---|
| Saber si quien habla conmigo ya es cliente | Que inicie sesión: el token lo identifica |
| Vincular un pedido a una cuenta | Por identificador de usuario, después de que inicie sesión o se registre |
| Confiar en un correo | El registro con verificación (SPEC-01 y SPEC-02) lo deja verificado |
| Saber si un token sigue vivo | `POST /auth/introspeccion` |

## Consecuencias

- **Para los módulos consumidores:** no pueden preguntar por contacto. Está
  documentado en el kit, sección 8, junto con las alternativas.
- **Para nosotros:** cada vez que se pida, la respuesta es este documento. No se
  vuelve a discutir caso por caso.
- **El filtro por correo de `GET /usuarios`** sigue existiendo, pero es de
  administración: exige el permiso `usuario.ver`, que solo tiene `ADMIN_SISTEMA`,
  y no se concede a ningún módulo.
