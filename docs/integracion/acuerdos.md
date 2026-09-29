# Acuerdos de integración con los otros módulos

| Campo | Valor |
|---|---|
| **Dueño** | Sergio Osorio — Product Owner del G7 |
| **Para qué** | Que un acuerdo entre equipos exista en un solo lugar, con estado y fecha |
| **Regla** | Un acuerdo que no está en esta tabla no existe. Los pedidos entran por issue con la etiqueta `integracion`, no por mensaje |

La numeración de specs cambió el 20 de septiembre, de 9 specs a 18. Si un
documento de otro equipo cita un número que no cuadra, la equivalencia está en
[`../../specs/trazabilidad.md`](../../specs/trazabilidad.md) §8.

---

## Acuerdos abiertos y cerrados

| # | Pide | Qué pide | Decisión | Estado | Quién lo ejecuta | Cuándo |
|---|---|---|---|---|---|---|
| **A1** | Chatbot | Que el enlace de verificación de correo lleve a su frontend cuando el registro se origine en el chat | **Aceptado, con nuestra forma.** `canalOrigen` de lista cerrada (`WEB`, `CHATBOT`, `RETAIL`, `MARKETPLACE`); la URL de cada canal la resolvemos nosotros. No aceptamos una URL en la petición | ✅ Decidido · en el contrato | Juan José (SPEC-02) | Hito 3 |
| **A2** | Chatbot | Endpoint y scope para validar un celular desde su canal (RF-10.4) | **Fuera de alcance de este ciclo.** El SMS es simulado hasta el final del curso y el correo ya queda verificado por el registro. Se reevalúa en el Hito 4 con un caso concreto que el registro no cubra | ✅ Decidido | — | Revisión Hito 4 |
| **A3** | Chatbot | Scope `tokens:introspeccion` para `modulo-chatbot` | **Concedido.** Es el caso de uso correcto: verificar la sesión antes de cobrar | ✅ Decidido · publicado en el kit §5 | Christian (aprovisionamiento) | Credenciales reales en Hito 4 |
| **A4** | Marketplace (su H-06) | Cliente `modulo-marketplace` con `cotizaciones:calcular` y `seguimientos:leer`, scopes de la API de Despacho | **Aceptado, y vale para cualquier scope de otro módulo.** El dueño de la API define el scope y valida el token; nosotros lo registramos y lo emitimos con `aud` = APIs dueñas, claim `scope` en texto, `iss` = `issuer` del descubrimiento y 1 hora de duración. Detalle en SPEC-17 | ✅ Decidido · en SPEC-17 y kit §5. Pendiente: que el PO de Despacho confirme `aud` y sus `401`/`403` | Sergio (contrato) · Christian (aprovisionamiento) | Prueba conjunta con Despacho en Hito 4 |
| **A5** | Despacho (su `api-contract.md` §3.3 y §4) | Rol `REPARTIDOR` en nuestro catálogo, claim `idRepartidor` y un endpoint para crear o vincular repartidores con su token de servicio | **Fuera del catálogo: el repartidor es un perfil de Despacho.** Los seis roles no cambian. Un `ADMIN_SISTEMA` crea la cuenta con `POST /usuarios` (puede no tener rol); Despacho guarda en su BD el `usuario_id` (el `sub` del token) y decide ahí qué ve cada repartidor. No hay endpoint de alta para tokens de servicio ni claim `idRepartidor` en este ciclo. Vale igual para cualquier perfil de otro módulo que no esté entre los seis roles | ✅ Decidido · en SPEC-11 y kit §3. Pendiente: que el PO de Despacho lo confirme | Sergio (contrato) · Despacho (su tabla de repartidores) | Antes del Hito 3 |

### Decisiones de fondo que afectan a todos

| Decisión | Dónde está | Qué significa para un consumidor |
|---|---|---|
| **No existe cuenta de invitado** | [ADR-005](../arquitectura/adr/005-sin-cuenta-de-invitado.md) | Quien necesite invitados los lleva en su módulo y enlaza el pedido cuando haya cuenta |
| **No hay consulta por correo ni celular** | [ADR-006](../arquitectura/adr/006-sin-consulta-por-correo-ni-celular.md) | Las consultas son por identificador de usuario. Ningún error ni latencia revela si una cuenta existe |
| **Los scopes se conceden por escrito** | Kit §5 | Se piden por issue; se publican en la tabla de scopes del kit |
| **Los roles son solo seis y los permisos, solo los nuestros** | SPEC-11, kit §3, A5 | Autoricen por `roles` y con sus propios datos. Un perfil propio (repartidor, cajero…) vive en su BD enlazado al `sub` |
| **El contrato cambia con aviso** | Kit §12 | Una semana de anticipación, y añadimos campos antes que renombrarlos |

---

## Cómo se pide algo

1. **Un issue** en `Taller-SW-Web/Modulo-de-Seguridad` con la etiqueta
   `integracion`, que diga: qué necesitan, para qué flujo y para qué hito.
2. Lo responde el PO en la misma semana: aceptado, aceptado con otra forma, o
   fuera de alcance con el motivo.
3. La decisión se escribe aquí y, si toca el contrato, en `openapi.yaml` y en la
   spec dueña.

**Lo que no aceptamos:** pedidos por mensaje privado. No porque sea informal,
sino porque ya nos pasó que un acuerdo viviera solo en el documento de otro
equipo, con los números de spec equivocados, y nadie pudiera comprobarlo.

---

## Glosario mínimo

Cinco términos que significan cosas distintas en cada equipo, y que causaron la
mitad de los malentendidos:

| Término | Qué significa aquí |
|---|---|
| **Cuenta** | Un registro en nuestra tabla `usuario`, con estado, roles y auditoría. Es lo único que existe para nosotros |
| **Invitado** | Alguien sin cuenta. **No existe en este módulo** (ADR-005) |
| **Verificar un contacto** | Probar que quien está del otro lado controla ese correo o celular. Hoy solo el correo, y solo por el registro |
| **Validar identidad** | En su documentación suele significar «confirmar que existe una cuenta con estos datos». Aquí eso no se puede preguntar (ADR-006): la identidad se prueba iniciando sesión |
| **Token de servicio** | Identifica a un **módulo**, no a una persona. Nunca hereda los permisos del usuario |
