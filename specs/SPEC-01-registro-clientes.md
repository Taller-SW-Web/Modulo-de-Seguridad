# SPEC-01 — Registro de clientes

| Campo | Valor |
|---|---|
| **Responsable** | Eva Lucía Moreno Zevallos |
| **Hito objetivo** | Hito 3 (Sem. 8) |
| **Estado** | Borrador |
| **Aprobada por** | Product Owner — pendiente de aprobación |

> **Origen.** Sale de dividir la antigua SPEC-01 «Registro y gestión de usuarios» el 20 de septiembre, por indicación del profesor: una spec por función. Conserva el registro público; la verificación de correo pasó a SPEC-02, el alta administrativa a SPEC-03 y la baja y reactivación a SPEC-04. La equivalencia de números está en [`trazabilidad.md`](trazabilidad.md) §1.

---

## Contexto — ¿por qué?

El Marketplace Multicanal de Productos Deportivos necesita que un cliente pueda crear su cuenta sin intervención de nadie. Como proveedor de identidad del sistema, este módulo es el único dueño de la entidad usuario: ningún módulo consumidor guarda credenciales ni administra datos primarios de usuario.

Por eso el registro debe garantizar desde el primer momento la unicidad del correo, el almacenamiento seguro de la contraseña y el consentimiento expreso para tratar los datos personales, que exige la Ley N.º 29733.

---

## Propósito — ¿para qué?

Permitir que un visitante cree su cuenta de cliente con su correo, sus datos básicos y una contraseña que cumpla la política de SPEC-07.

El resultado observable es una cuenta nueva con el rol `CLIENTE` y el estado `PENDIENTE_VERIFICACION`, sin sesión abierta, y el envío del enlace de verificación que define SPEC-02.

---

## Alcance — ¿hasta dónde?

- Registro público de clientes con `POST /api/v1/auth/registro`.
- Asignación del rol `CLIENTE` y del estado inicial `PENDIENTE_VERIFICACION`.
- Validación de formato y unicidad del correo, sin revelar si ya existe.
- Almacenamiento de la contraseña con hashing resistente (la política la define SPEC-07).
- Aceptación expresa de términos y del tratamiento de datos personales.
- Formato del celular.
- Registro del hecho en auditoría (SPEC-12).

---

## Requisitos — ¿qué debe hacer?

| Código | Requisito |
|---|---|
| RF-01.1 | El sistema debe permitir el registro público de nuevos usuarios asignándoles por defecto el rol `CLIENTE` y el estado de cuenta `PENDIENTE_VERIFICACION`. |
| RF-01.2 | El sistema debe validar la unicidad del correo electrónico antes de registrar al usuario, rechazando duplicados sin revelar información sensible. |
| RF-01.3 | El sistema debe almacenar la contraseña aplicando un algoritmo de hashing seguro (BCrypt con costo >= 12 o Argon2id) y nunca en texto plano. |
| RF-01.4 | El registro debe exigir la aceptación expresa de los términos y del tratamiento de datos personales (`aceptaTerminos: true`), conforme a la Ley N.º 29733. Sin ella responde `400 VALIDACION` y no crea la cuenta. El sistema guarda la fecha de aceptación y la versión del texto aceptado, para poder demostrar el consentimiento. |
| RF-01.5 | El celular debe registrarse en formato internacional peruano: `+51` seguido de 9 dígitos. Otro formato responde `400 VALIDACION`. |
| RF-01.6 | El registro, correcto o rechazado, debe quedar registrado mediante el catálogo de auditoría de SPEC-12 (`USUARIO_CREADO`). |
| RF-01.7 | Tras crear la cuenta, el sistema debe iniciar la verificación de correo de SPEC-02 y **no** debe emitir tokens de sesión. |

---

## Escenarios — ¿cómo verificamos?

Formato Dado / Cuando / Entonces. Los marcados como **caso borde** cubren
condiciones límite, de error o de seguridad.

### ESC-01.1 Registro público de cliente exitoso

**Dado** un visitante no autenticado que proporciona un correo no registrado `cliente@correo.com` y una contraseña válida.

**Cuando** envía una solicitud `POST /api/v1/auth/registro`.

**Entonces** el sistema responde con código HTTP `201 Created`, crea la cuenta con estado `PENDIENTE_VERIFICACION`, genera el token de verificación y envía el correo electrónico asíncronamente sin emitir tokens de sesión.

### ESC-01.2 Intentos de registro con correo duplicado *(caso borde de seguridad)*

**Dado** que el correo `existente@correo.com` ya pertenece a una cuenta en el sistema.

**Cuando** se intenta registrar una nueva cuenta con ese mismo correo.

**Entonces** el sistema responde con código HTTP `409 CORREO_NO_DISPONIBLE` indicando que la solicitud no puede ser procesada, sin revelar datos del propietario previo y registrando el evento en auditoría.

### ESC-01.3 Registro sin aceptar los términos *(caso borde legal)*

**Dado** un visitante que completa el formulario de registro correctamente.

**Cuando** envía `POST /api/v1/auth/registro` con `aceptaTerminos: false` o sin ese campo.

**Entonces** el sistema responde `400 VALIDACION` con el error en el campo `aceptaTerminos`, no crea la cuenta y no envía ningún correo. Con `aceptaTerminos: true`, la cuenta guarda la fecha de aceptación y la versión de los términos vigente.

### ESC-01.4 Celular con formato no peruano *(caso borde)*

**Dado** un visitante que completa el formulario de registro correctamente.

**Cuando** envía `POST /api/v1/auth/registro` con el celular `987654321` (sin `+51`) o `+1 555 0100`.

**Entonces** el sistema responde `400 VALIDACION` con el error en el campo `celular`, no crea la cuenta y no envía ningún correo.

---

## Requisitos no funcionales — ¿con qué condiciones?

- El hashing de contraseña debe tardar al menos 200 ms por diseño para frenar ataques de fuerza bruta.
- El endpoint de registro debe responder en menos de 600 ms en el percentil 95, sin contar el envío del correo, que es asíncrono.
- Las contraseñas en texto plano nunca deben aparecer en logs ni en eventos de RabbitMQ.
- Los mensajes de error no deben permitir enumerar cuentas existentes.
- El tratamiento de datos personales debe alinearse con la Ley N.º 29733 de Protección de Datos Personales.

---

## Fuera de alcance — ¿qué NO hará?

- La verificación del correo y su reenvío, que son de SPEC-02.
- El alta de vendedores y personal de gestión, que es de SPEC-03.
- Las reglas de la política de contraseñas, que son de SPEC-07: aquí solo se invocan.
- Autenticación inmediata tras el registro sin verificación previa.
- Autenticación por proveedores OAuth2 / Social Login.
- Asignación pública de roles privilegiados.
- Los atributos de perfil, el documento de identidad y las direcciones, que son de SPEC-16.
- La baja por decisión del propio titular: está pendiente de decisión (Q10).

---

## Impacto en el contrato

| Endpoint / evento | Añade, modifica o elimina | Acordado con el PO |
|---|---|---|
| `POST /api/v1/auth/registro` | Modifica: añade `aceptaTerminos` obligatorio y el formato del celular (RF-01.4, RF-01.5) | ⬜ |

---

## Lista de completitud

La spec se cierra cuando:

- [ ] Todos los requisitos están implementados
- [ ] Cada requisito clave tiene al menos dos escenarios automatizados, uno de ellos caso borde
- [ ] Los no funcionales están verificados, o la desviación está documentada y aprobada
- [ ] Los endpoints figuran en la documentación OpenAPI publicada (`specs/openapi.yaml`)
- [ ] Nada de lo declarado fuera de alcance se construyó de forma encubierta
- [ ] El código está en `main` con revisión aprobada y desplegado en nube
