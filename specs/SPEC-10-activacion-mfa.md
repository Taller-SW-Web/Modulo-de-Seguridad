# SPEC-10 — Activación y desactivación del segundo factor

| Campo | Valor |
|---|---|
| **Responsable** | Luis David Morales Brenis |
| **Hito objetivo** | Hito 4 (Sem. 11) |
| **Estado** | Borrador |
| **Aprobada por** | Product Owner — pendiente de aprobación |

> **Origen.** Sale de dividir la antigua SPEC-04 «Autenticación por OTP y MFA» el 20 de septiembre, por indicación del profesor: una spec por función. La equivalencia de números está en [`trazabilidad.md`](trazabilidad.md) §1.

---

## Contexto — ¿por qué?

Las cuentas administrativas requieren una protección adicional que no puede depender de la voluntad de su titular; para clientes y vendedores, en cambio, el segundo factor es una decisión propia.

Esta spec decide quién puede activar o desactivar el segundo factor y cómo se confirma cada cambio. Reutiliza el mecanismo de códigos de SPEC-09, que también sirve para que un canal autorizado —el Chatbot, por ejemplo— verifique el correo o el celular de un cliente.

---

## Propósito — ¿para qué?

Permitir que un cliente o un vendedor active o desactive su segundo factor confirmándolo con un código, e impedir que lo desactive quien tenga un rol de gestión.

El resultado observable es la bandera `mfa_habilitado` cambiada y auditada tras un código correcto, o un `422 MFA_OBLIGATORIO` para el personal de gestión.

---

## Alcance — ¿hasta dónde?

- Obligatoriedad del segundo factor para los cuatro roles de gestión.
- Activación con `POST /api/v1/auth/otp/habilitar`, confirmada con un código.
- Desactivación con `POST /api/v1/auth/otp/deshabilitar`, confirmada con un código.
- Validación del correo o el celular de un cliente por un canal autorizado.
- Auditoría de activaciones y desactivaciones mediante SPEC-12.

---

## Requisitos — ¿qué debe hacer?

| Código | Requisito |
|---|---|
| RF-10.1 | El segundo factor debe ser obligatorio para quien tenga algún rol de gestión (`ADMIN_VENTAS`, `GESTOR_DESPACHO`, `GESTOR_COMERCIAL`, `ADMIN_SISTEMA`) y opcional para `CLIENTE` y `VENDEDOR`. Basta **un** rol de gestión para que sea obligatorio. |
| RF-10.2 | Un usuario autenticado debe poder solicitar la habilitación de MFA y confirmar su activación mediante un código de prueba. |
| RF-10.3 | Un usuario permitido debe poder deshabilitar MFA después de autenticarse y verificar un código de confirmación. Quien tenga algún rol de gestión no puede deshabilitarlo (`422 MFA_OBLIGATORIO`). |
| RF-10.4 | El sistema debe permitir validar un correo o celular mediante este mecanismo cuando exista una solicitud autorizada del canal consumidor. |
| RF-10.5 | La activación y la desactivación deben registrarse mediante SPEC-12 (`MFA_ACTIVADO`, `MFA_DESACTIVADO`), también los intentos rechazados. Ambas son acciones **críticas**. |

---

## Escenarios — ¿cómo verificamos?

Formato Dado / Cuando / Entonces. Los marcados como **caso borde** cubren
condiciones límite, de error o de seguridad.

### ESC-10.1 Habilitación voluntaria de MFA

- **Dado** un cliente autenticado con MFA deshabilitado,
- **Cuando** solicita `POST /api/v1/auth/otp/habilitar`,
- **Entonces** el sistema envía un código de confirmación y, después de su verificación correcta, marca `mfa_habilitado` como verdadero y registra `MFA_ACTIVADO`.

### ESC-10.2 Deshabilitación permitida de MFA

- **Dado** un vendedor autenticado con MFA habilitado,
- **Cuando** solicita `POST /api/v1/auth/otp/deshabilitar` y verifica correctamente el código de confirmación,
- **Entonces** el sistema deshabilita MFA, registra `MFA_DESACTIVADO` y exige contraseña solamente en el siguiente inicio de sesión.

### ESC-10.3 Deshabilitación del segundo factor con un rol de gestión *(caso borde)*

- **Dado** un usuario con un rol de gestión —por ejemplo `ADMIN_VENTAS`— y MFA habilitado,
- **Cuando** intenta deshabilitar MFA,
- **Entonces** el sistema responde `422 MFA_OBLIGATORIO`, mantiene MFA activo y registra el intento rechazado.

### ESC-10.4 Validación de contacto solicitada por un canal autorizado

- **Dado** una solicitud autorizada para verificar el correo o celular de un cliente,
- **Cuando** el usuario recibe y verifica correctamente el código,
- **Entonces** el sistema marca el canal correspondiente como verificado, registra `OTP_VERIFICADO` y devuelve el resultado al consumidor autorizado.

### ESC-10.5 Basta un rol de gestión *(caso borde)*

- **Dado** un usuario con los roles `VENDEDOR` y `GESTOR_COMERCIAL`,
- **Cuando** intenta deshabilitar su segundo factor,
- **Entonces** el sistema responde `422 MFA_OBLIGATORIO`: aunque `VENDEDOR` lo permitiría, `GESTOR_COMERCIAL` lo exige, y manda el más estricto.

---

## Requisitos no funcionales — ¿con qué condiciones?

- La confirmación de la activación o desactivación debe responder en menos de 300 ms en el percentil 95, sin incluir la latencia del proveedor de correo o SMS.
- La regla de obligatoriedad se evalúa sobre **todos** los roles de la cuenta: manda el más estricto.
- Los datos de contacto deben mostrarse enmascarados en las respuestas.
- El tratamiento de datos personales debe alinearse con la Ley N.º 29733 de Protección de Datos Personales.

---

## Fuera de alcance — ¿qué NO hará?

- La generación, el envío y la verificación de los códigos, que son de SPEC-09: aquí se reutilizan.
- El cambio de celular, que es de SPEC-16: esta spec solo aporta la verificación posterior.
- Códigos de respaldo para recuperar el segundo factor.
- Que un administrador desactive el segundo factor de otra cuenta.

---

## Impacto en el contrato

| Endpoint / evento | Añade, modifica o elimina | Acordado con el PO |
|---|---|---|
| `POST /api/v1/auth/otp/habilitar` | Utiliza contrato existente | ⬜ |
| `POST /api/v1/auth/otp/deshabilitar` | Utiliza contrato existente | ⬜ |
| Validación de correo/celular por consumidor autorizado | Requiere confirmar payload y scope con el PO | ⬜ |
| `MFA_ACTIVADO` · `MFA_DESACTIVADO` | Catálogo de auditoría de SPEC-12 | ⬜ |

---

## Lista de completitud

La spec se cierra cuando:

- [ ] Todos los requisitos están implementados
- [ ] Cada requisito clave tiene al menos dos escenarios automatizados, uno de ellos caso borde
- [ ] Los no funcionales están verificados, o la desviación está documentada y aprobada
- [ ] Los endpoints figuran en la documentación OpenAPI publicada (`specs/openapi.yaml`)
- [ ] Nada de lo declarado fuera de alcance se construyó de forma encubierta
- [ ] El código está en `main` con revisión aprobada y desplegado en nube
