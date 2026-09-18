# Historias de usuario

| Campo | Valor |
|---|---|
| **Dueño** | Product Owner |
| **Para qué** | La vista de quien usa el sistema. Cada historia apunta a los requisitos (RF) y escenarios (ESC) de su spec, que son los que se implementan y se prueban |
| **Regla** | Las specs no se reescriben desde aquí. Si una historia necesita algo que su spec no tiene, se cambia la spec y después esta tabla |

Las historias siguen el formato *Como… quiero… para…*. Los criterios de
aceptación **no se repiten**: son los escenarios *Dado / Cuando / Entonces* que
cita cada historia, y por eso una historia se da por terminada cuando pasan sus
escenarios.

---

## SPEC-01 — Registro y gestión de usuarios

| HU | Historia | Requisitos | Escenarios |
|---|---|---|---|
| HU-01.1 | Como **visitante**, quiero registrarme con mi correo y aceptar el tratamiento de mis datos, para comprar en el marketplace | RF-01.1 · 01.2 · 01.3 · 01.16 · 01.17 | ESC-01.1 · 01.2 · 01.13 |
| HU-01.2 | Como **cliente recién registrado**, quiero confirmar mi correo con un enlace y pedir otro si no me llegó, para poder iniciar sesión | RF-01.4 · 01.5 · 01.6 · 01.7 · 01.9 · 01.15 | ESC-01.3 · 01.4 · 01.5 · 01.6 · 01.7 |
| HU-01.3 | Como **administrador del sistema**, quiero dar de alta vendedores y personal de gestión, para que operen sin autorregistrarse | RF-01.8 · 01.11 | ESC-01.8 · 01.9 |
| HU-01.4 | Como **administrador del sistema**, quiero dar de baja una cuenta y reactivarla después, para controlar quién opera sin perder su historial | RF-01.10 · 01.12 · 01.13 · 01.14 | ESC-01.10 · 01.11 · 01.12 |

## SPEC-02 — Autenticación

| HU | Historia | Requisitos | Escenarios |
|---|---|---|---|
| HU-02.1 | Como **usuario**, quiero iniciar sesión con mi correo y contraseña, para usar cualquier canal del marketplace | RF-02.1 · 02.8 | ESC-02.1 |
| HU-02.2 | Como **usuario**, quiero que un error de acceso no revele si mi cuenta existe o está bloqueada, para que nadie pueda averiguarlo probando | RF-02.6 · 02.7 | ESC-02.2 · 02.3 · 02.4 |
| HU-02.3 | Como **usuario con segundo factor**, quiero que la contraseña correcta no baste para entrar, para que robármela no sea suficiente | RF-02.2 | ESC-02.5 |
| HU-02.4 | Como **usuario**, quiero que mi sesión se renueve sola y que un token robado se detecte, para no tener que entrar a cada rato sin quedar expuesto | RF-02.3 · 02.5 | ESC-02.6 · 02.7 · 02.9 |
| HU-02.5 | Como **usuario**, quiero cerrar sesión, para que nadie siga usando mi cuenta en ese dispositivo | RF-02.4 | ESC-02.8 |

## SPEC-03 — Gestión de credenciales y contraseñas

| HU | Historia | Requisitos | Escenarios |
|---|---|---|---|
| HU-03.1 | Como **usuario**, quiero que el sistema me impida elegir una contraseña débil o repetida, para que mi cuenta no sea fácil de adivinar | RF-03.1 · 03.2 · 03.3 · 03.4 · 03.5 · 03.6 | ESC-03.1 · 03.2 · 03.3 · 03.4 · 03.5 · 03.6 |
| HU-03.2 | Como **usuario que olvidó su contraseña**, quiero recuperarla con un enlace a mi correo, para volver a entrar sin ayuda | RF-03.8 · 03.9 · 03.10 · 03.11 · 03.12 · 03.14 | ESC-03.8 · 03.9 · 03.10 · 03.11 · 03.12 · 03.15 |
| HU-03.3 | Como **usuario con sesión iniciada**, quiero cambiar mi contraseña confirmando la actual, para renovarla cuando lo necesite | RF-03.13 · 03.14 · 03.15 | ESC-03.13 · 03.14 |
| HU-03.4 | Como **responsable de seguridad**, quiero que las contraseñas del personal de gestión caduquen cada 90 días, para limitar el daño de una contraseña filtrada | RF-03.7 | ESC-03.7 |

## SPEC-04 — OTP y segundo factor

| HU | Historia | Requisitos | Escenarios |
|---|---|---|---|
| HU-04.1 | Como **usuario con segundo factor**, quiero recibir un código de 6 dígitos por correo o SMS al iniciar sesión, para confirmar que soy yo | RF-04.1 · 04.2 · 04.3 · 04.4 · 04.8 · 04.9 · 04.10 · 04.16 | ESC-04.1 · 04.2 · 04.3 · 04.8 · 04.14 |
| HU-04.2 | Como **responsable de seguridad**, quiero limitar los intentos y las solicitudes de código, para que nadie lo adivine a fuerza de probar | RF-04.5 · 04.6 · 04.7 · 04.15 | ESC-04.4 · 04.5 · 04.6 · 04.7 · 04.9 · 04.10 |
| HU-04.3 | Como **cliente o vendedor**, quiero activar o desactivar el segundo factor cuando quiera, para decidir cuánta protección necesito | RF-04.12 · 04.13 | ESC-04.11 · 04.12 |
| HU-04.4 | Como **responsable de seguridad**, quiero que el personal de gestión no pueda desactivar su segundo factor, para proteger las cuentas con más poder | RF-04.11 | ESC-04.13 · 04.16 |
| HU-04.5 | Como **canal de venta autorizado**, quiero validar el correo o el celular de un cliente con un código, para confirmar su contacto sin un mecanismo propio | RF-04.14 | ESC-04.15 |

## SPEC-05 — Roles y permisos

| HU | Historia | Requisitos | Escenarios |
|---|---|---|---|
| HU-05.1 | Como **administrador del sistema**, quiero asignar y revocar roles, para dar a cada persona solo el acceso que necesita | RF-05.1 · 05.2 · 05.3 · 05.5 · 05.6 · 05.8 | ESC-05.1 · 05.2 · 05.6 |
| HU-05.2 | Como **responsable de seguridad**, quiero que solo el administrador del sistema pueda cambiar roles y que nunca desaparezca el último, para no quedar sin control del módulo | RF-05.7 | ESC-05.3 · 05.5 |
| HU-05.3 | Como **módulo consumidor**, quiero recibir en el token los roles y permisos efectivos del usuario, para autorizar sin llamar a Seguridad | RF-05.4 · 05.9 · 05.10 | ESC-05.4 · 05.7 |

## SPEC-06 — Auditoría

| HU | Historia | Requisitos | Escenarios |
|---|---|---|---|
| HU-06.1 | Como **responsable de seguridad**, quiero que cada acción sensible deje un registro que nadie pueda alterar ni que contenga secretos, para reconstruir lo que pasó | RF-06.1 · 06.2 · 06.3 · 06.4 · 06.5 · 06.6 | ESC-06.1 · 06.2 · 06.3 · 06.4 · 06.10 · 06.11 |
| HU-06.2 | Como **administrador del sistema**, quiero consultar y exportar la auditoría con filtros, para investigar un incidente | RF-06.7 · 06.8 · 06.9 · 06.10 | ESC-06.5 · 06.6 · 06.7 · 06.8 · 06.9 |
| HU-06.3 | Como **usuario**, quiero ver la actividad reciente de mi cuenta, para detectar un acceso que no hice | RF-06.12 | ESC-06.12 |
| HU-06.4 | Como **responsable de datos personales**, quiero que los registros se borren solos pasados 90 días, para no guardar más de lo necesario | RF-06.11 | ESC-06.13 |

## SPEC-07 — Bloqueo y desbloqueo de cuentas

El recorrido completo de estados (`ACTIVO → BLOQUEADO → ACTIVO`) está en
[`docs/arquitectura/estados-usuario.md`](../docs/arquitectura/estados-usuario.md).

| HU | Historia | Requisitos | Escenarios |
|---|---|---|---|
| HU-07.1 | Como **usuario**, quiero que mi cuenta se bloquee tras varios intentos fallidos, para proteger mi información | RF-07.1 · 07.2 · 07.3 · 07.4 · 07.5 · 07.6 · 07.7 · 07.11 · 07.13 · 07.14 | ESC-07.1 a 07.7 · 07.14 · 07.15 · 07.17 · 07.18 · 07.23 |
| HU-07.2 | Como **administrador del sistema**, quiero bloquear una cuenta manualmente con un motivo, para cortar un acceso sospechoso | RF-07.8 · 07.9 · 07.11 · 07.12 · 07.15 | ESC-07.8 · 07.9 · 07.10 · 07.13 · 07.16 · 07.24 |
| HU-07.3 | Como **usuario**, quiero desbloquear mi cuenta con un enlace de mi correo o restableciendo mi contraseña, para recuperar el acceso sin esperar a nadie | RF-07.12 · 07.16 · 07.17 | ESC-07.19 · 07.20 · 07.21 · 07.22 |
| HU-07.4 | Como **administrador del sistema**, quiero ver por qué y hasta cuándo está bloqueada una cuenta y poder desbloquearla, para atender a quien me lo pida | RF-07.10 · 07.18 | ESC-07.11 · 07.12 · 07.25 |

## SPEC-08 — Atributos de usuario

| HU | Historia | Requisitos | Escenarios |
|---|---|---|---|
| HU-08.1 | Como **usuario**, quiero ver y actualizar mis datos de perfil, para que el marketplace me identifique bien | RF-08.1 · 08.2 · 08.3 · 08.9 · 08.10 | ESC-08.1 · 08.2 · 08.6 |
| HU-08.2 | Como **usuario**, quiero cambiar mi correo o mi celular verificando el nuevo, para no perder el acceso si me equivoco | RF-08.6 · 08.7 | ESC-08.3 · 08.4 |
| HU-08.3 | Como **cliente**, quiero guardar mis direcciones de entrega y marcar una como predeterminada, para no escribirlas en cada compra | RF-08.5 | ESC-08.9 |
| HU-08.4 | Como **cliente**, quiero que mi número de documento se guarde cifrado y solo lo vea quien lo necesita, para proteger mis datos personales | RF-08.4 | ESC-08.5 · 08.7 · 08.8 |
| HU-08.5 | Como **administrador del sistema**, quiero editar los atributos de un vendedor, para mantener al día su tienda y su código | RF-08.8 | ESC-08.10 |

## SPEC-09 — API de identidad para los demás módulos

Aquí el usuario es **otro equipo del curso**: la consumen máquinas, no personas.

| HU | Historia | Requisitos | Escenarios |
|---|---|---|---|
| HU-09.1 | Como **equipo de otro módulo**, quiero validar el token del usuario con una clave pública, para autorizar cada petición sin llamar a Seguridad | RF-09.1 · 09.1b · 09.2 | ESC-09.1 · 09.2 |
| HU-09.2 | Como **equipo de otro módulo**, quiero obtener un token de servicio con los scopes justos, para consultar datos de usuarios de forma controlada | RF-09.3 · 09.4 · 09.11 · 09.12 · 09.14 | ESC-09.3 · 09.4 · 09.13 · 09.14 |
| HU-09.3 | Como **módulo que hace operaciones sensibles**, quiero preguntar si una sesión sigue viva, para no aceptar a un usuario dado de baja o bloqueado | RF-09.5 · 09.6 | ESC-09.5 · 09.6 |
| HU-09.4 | Como **equipo de otro módulo**, quiero consultar usuarios, direcciones y roles sin ver datos que mi scope no permite, para cumplir mi función sin exponer datos personales | RF-09.7 · 09.8 · 09.9 · 09.10 · 09.13 · 09.15 | ESC-09.7 a 09.12 · 09.15 |
| HU-09.5 | Como **equipo de otro módulo**, quiero un entorno simulado con datos de prueba, para programar antes de que Seguridad esté implementado | RF-09.16 | ESC-09.16 |
