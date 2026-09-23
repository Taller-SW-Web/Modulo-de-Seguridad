# ADR-005 — No existe la cuenta de invitado

| Campo | Valor |
|---|---|
| **Estado** | Aceptada |
| **Fecha** | 23 de septiembre de 2026 |
| **Decide** | Sergio Osorio (PO) |

## Contexto

El canal Chatbot planteó un checkout conversacional en el que el comprador
entrega su correo y su celular sin crear cuenta, y pidió que Seguridad
«registrara los datos temporalmente como cliente invitado».

Es una necesidad real: pedirle a alguien que elija contraseña en medio de una
conversación de chat hace que abandone la compra. La pregunta es dónde vive ese
invitado.

## Decisión

**Para este módulo, una persona tiene cuenta o no existe.** No creamos ni
almacenamos identidades temporales, anónimas o de invitado.

Un módulo que necesite operar con alguien sin cuenta lo hace con sus propios
datos, y nos enlaza el pedido por el identificador de usuario cuando esa
persona llegue a tener cuenta.

## Por qué

**Una identidad sin cuenta no se puede gobernar.** Todo lo que este módulo
ofrece cuelga de una cuenta: estados (`ACTIVO`, `BLOQUEADO`, `INACTIVO`), roles
y permisos, auditoría con actor identificado, bloqueo por intentos fallidos,
derechos ARCO de la Ley N.º 29733. Un invitado no tiene ninguno de esos
mecanismos: no se puede bloquear, no se puede auditar como actor y no se puede
atender una solicitud de rectificación sobre él.

**Sería una segunda entidad usuario con la mitad de las reglas.** El curso
prohíbe que otro módulo guarde datos primarios de usuario precisamente para que
la identidad tenga un solo dueño. Un «usuario invitado» dentro de Seguridad
crea una identidad de segunda clase que después habría que fusionar con la real,
con todo lo que eso trae: dos identificadores para la misma persona, historial
partido y eventos duplicados hacia los seis módulos.

**El costo de no tenerlo lo absorbe quien lo necesita.** Un invitado en el
módulo de Chatbot es una fila en su base de datos con un correo y un celular.
Ahí es barato. Aquí obligaría a revisar el ciclo de vida completo de la cuenta.

## Alternativa para quien la necesite

El registro con enlace: el chat le envía al comprador un enlace a nuestra
pantalla de registro, y el correo queda verificado por nuestro flujo normal
(SPEC-01 y SPEC-02). El marketplace gana un cliente registrado en lugar de un
anónimo, que vale más para todos los módulos.

## Consecuencias

- **Para los módulos consumidores:** quien quiera invitados los gestiona en su
  propio módulo. Documentado en el kit de integración, sección 8.
- **Para nosotros:** `POST /auth/registro` es el único camino de creación de
  cuentas de cliente, y nace `PENDIENTE_VERIFICACION`.
- **Si esto cambiara**, no sería un campo nuevo: sería una entidad nueva, con su
  spec y su ciclo de vida. Se evaluaría como cambio mayor, no como ajuste.
