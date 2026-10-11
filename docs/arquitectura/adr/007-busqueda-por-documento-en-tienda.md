# ADR-007 — Búsqueda de clientes por documento, solo en tienda

| Campo | Valor |
|---|---|
| **Estado** | Aceptada |
| **Fecha** | 2 de octubre de 2026 |
| **Decide** | Sergio Osorio (PO) |
| **Matiza** | [ADR-006](006-sin-consulta-por-correo-ni-celular.md) |
| **Acuerdo** | A7, a pedido de Retail |

## Contexto

ADR-006 cerró cualquier consulta por correo o por celular, porque convierte la
API en un enumerador de cuentas. Retail respondió el 2 de octubre que en la
tienda física el vendedor necesita encontrar a un cliente que ya existe, y que
el dato que siempre le pide es su documento. Las alternativas que propusimos
—que el cliente inicie sesión en un dispositivo de la tienda o muestre un
código— no le sirven a quien compra en mostrador.

## Decisión

Se permite **una** búsqueda por documento, `POST /api/v1/usuarios/busqueda-documento`,
con estas condiciones, que son las que la separan de un enumerador:

| Condición | Por qué |
|---|---|
| Solo con **token de usuario** con el permiso `cliente.buscar` (`VENDEDOR`). Un token de servicio recibe `403` | La auditoría dice qué persona buscó a quién; una credencial de módulo filtrada no sirve para recorrer documentos |
| Tipo y número **exactos**, nunca parciales ni con comodines | No se puede barrer un rango |
| Solo cuentas `CLIENTE` en `ACTIVO` o `PENDIENTE_VERIFICACION`; el resto responde `404`, igual que una inexistente | No revela personal ni cuentas dadas de baja |
| Respuesta mínima: `id`, nombre, documento enmascarado y estado | Ni correo, ni celular, ni el documento en claro |
| 60 búsquedas por hora y vendedor | Acota el daño de una sesión robada |
| Cada búsqueda auditada como acción crítica (`BUSQUEDA_POR_DOCUMENTO`) | Si no se puede auditar, no se responde |
| Por `POST`, nunca en la URL | El número no queda en registros de acceso ni en el historial del navegador |

El documento se guarda cifrado (SPEC-16) y, además, como **HMAC** de tipo y
número (`perfil_cliente.documento_hmac`, único). Es lo que permite buscarlo sin
descifrar nada y lo que impide dos cuentas con el mismo documento.

**`RUC` queda fuera.** Identifica a una empresa y sirve para facturar, que es
dominio de Ventas. Nuestros usuarios son personas.

## Lo que no cambia

ADR-006 sigue vigente para correo y celular: no hay ni habrá búsqueda por
contacto, y el registro, el reenvío de verificación y el login siguen sin
revelar si una cuenta existe.

## Consecuencias

- **Para Retail:** encuentra clientes con el token de su vendedor, no con el de
  servicio. Si el documento no existe, registra al cliente con
  `POST /usuarios/clientes` (A6).
- **Para nosotros:** una columna y un índice más, un permiso nuevo para
  `VENDEDOR` y tres escenarios de seguridad en SPEC-03.
- **Riesgo aceptado:** un vendedor puede saber si una persona concreta, de la
  que conoce el documento, es cliente. Se acepta porque es su trabajo, queda
  auditado y está limitado.
