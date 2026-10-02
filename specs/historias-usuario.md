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

> **Numeración del 20 de septiembre.** Las historias siguen a las 18 specs: la
> HU-05.2 pertenece a SPEC-05. Las historias no cambiaron de texto al dividir las
> specs; solo se repartieron. La equivalencia con los números antiguos está en
> [`trazabilidad.md`](trazabilidad.md) §8.

---

## SPEC-01 — Registro de clientes

| HU | Historia | Requisitos | Escenarios |
|---|---|---|---|
| HU-01.1 | Como **visitante**, quiero registrarme con mi correo y aceptar el tratamiento de mis datos, para comprar en el marketplace | RF-01.1 a 01.7 | ESC-01.1 a 01.4 |

## SPEC-02 — Verificación de correo

| HU | Historia | Requisitos | Escenarios |
|---|---|---|---|
| HU-02.1 | Como **cliente recién registrado**, quiero confirmar mi correo con un enlace y pedir otro si no me llegó, para poder iniciar sesión | RF-02.1 a 02.7 | ESC-02.1 a 02.5 |

## SPEC-03 — Alta y consulta administrativa de cuentas

| HU | Historia | Requisitos | Escenarios |
|---|---|---|---|
| HU-03.1 | Como **administrador del sistema**, quiero dar de alta vendedores y personal de gestión, para que operen sin autorregistrarse | RF-03.1 · 03.3 · 03.4 | ESC-03.1 · 03.3 · 03.4 |
| HU-03.2 | Como **administrador del sistema**, quiero listar y filtrar las cuentas, para saber quién opera y en qué estado está | RF-03.2 | ESC-03.2 |

## SPEC-04 — Baja y reactivación de cuentas

| HU | Historia | Requisitos | Escenarios |
|---|---|---|---|
| HU-04.1 | Como **administrador del sistema**, quiero dar de baja una cuenta y reactivarla después, para controlar quién opera sin perder su historial | RF-04.1 · 04.3 · 04.4 | ESC-04.1 · 04.3 |
| HU-04.2 | Como **responsable de seguridad**, quiero que nadie pueda dar de baja al último administrador ni a sí mismo, para no quedar sin control del módulo | RF-04.2 | ESC-04.2 |

## SPEC-05 — Inicio de sesión

| HU | Historia | Requisitos | Escenarios |
|---|---|---|---|
| HU-05.1 | Como **usuario**, quiero iniciar sesión con mi correo y contraseña, para usar cualquier canal del marketplace | RF-05.1 · 05.5 · 05.6 · 05.7 | ESC-05.1 |
| HU-05.2 | Como **usuario**, quiero que un error de acceso no revele si mi cuenta existe o está bloqueada, para que nadie pueda averiguarlo probando | RF-05.3 · 05.4 | ESC-05.2 · 05.3 · 05.4 |
| HU-05.3 | Como **usuario con segundo factor**, quiero que la contraseña correcta no baste para entrar, para que robármela no sea suficiente | RF-05.2 | ESC-05.5 |

## SPEC-06 — Renovación y cierre de sesión

| HU | Historia | Requisitos | Escenarios |
|---|---|---|---|
| HU-06.1 | Como **usuario**, quiero que mi sesión se renueve sola y que un token robado se detecte, para no tener que entrar a cada rato sin quedar expuesto | RF-06.1 · 06.3 · 06.4 | ESC-06.1 · 06.2 · 06.4 |
| HU-06.2 | Como **usuario**, quiero cerrar sesión, para que nadie siga usando mi cuenta en ese dispositivo | RF-06.2 · 06.5 | ESC-06.3 |

## SPEC-07 — Política y cambio de contraseña

| HU | Historia | Requisitos | Escenarios |
|---|---|---|---|
| HU-07.1 | Como **usuario**, quiero que el sistema me impida elegir una contraseña débil o repetida, para que mi cuenta no sea fácil de adivinar | RF-07.1 a 07.6 · 07.11 | ESC-07.1 a 07.6 |
| HU-07.2 | Como **usuario con sesión iniciada**, quiero cambiar mi contraseña confirmando la actual, para renovarla cuando lo necesite | RF-07.8 · 07.9 · 07.10 | ESC-07.8 · 07.9 |
| HU-07.3 | Como **responsable de seguridad**, quiero que las contraseñas del personal de gestión caduquen cada 90 días, para limitar el daño de una contraseña filtrada | RF-07.7 | ESC-07.7 |

## SPEC-08 — Recuperación de contraseña

| HU | Historia | Requisitos | Escenarios |
|---|---|---|---|
| HU-08.1 | Como **usuario que olvidó su contraseña**, quiero recuperarla con un enlace a mi correo, para volver a entrar sin ayuda | RF-08.1 a 08.7 | ESC-08.1 a 08.6 |

## SPEC-09 — Segundo factor en el inicio de sesión

| HU | Historia | Requisitos | Escenarios |
|---|---|---|---|
| HU-09.1 | Como **usuario con segundo factor**, quiero recibir un código de 6 dígitos por correo o SMS al iniciar sesión, para confirmar que soy yo | RF-09.1 · 09.2 · 09.3 · 09.4 · 09.8 · 09.9 · 09.10 · 09.11 | ESC-09.1 · 09.2 · 09.3 · 09.8 · 09.11 |
| HU-09.2 | Como **responsable de seguridad**, quiero limitar los intentos y las solicitudes de código, para que nadie lo adivine a fuerza de probar | RF-09.5 · 09.6 · 09.7 · 09.12 | ESC-09.4 · 09.5 · 09.6 · 09.7 · 09.9 · 09.10 |

## SPEC-10 — Activación y desactivación del segundo factor

| HU | Historia | Requisitos | Escenarios |
|---|---|---|---|
| HU-10.1 | Como **cliente o vendedor**, quiero activar o desactivar el segundo factor cuando quiera, para decidir cuánta protección necesito | RF-10.2 · 10.3 · 10.5 | ESC-10.1 · 10.2 |
| HU-10.2 | Como **responsable de seguridad**, quiero que el personal de gestión no pueda desactivar su segundo factor, para proteger las cuentas con más poder | RF-10.1 | ESC-10.3 · 10.5 |
| HU-10.3 | Como **canal de venta autorizado**, quiero validar el correo o el celular de un cliente con un código, para confirmar su contacto sin un mecanismo propio | RF-10.4 | ESC-10.4 |

## SPEC-11 — Roles y permisos

| HU | Historia | Requisitos | Escenarios |
|---|---|---|---|
| HU-11.1 | Como **administrador del sistema**, quiero asignar y revocar roles, para dar a cada persona solo el acceso que necesita | RF-11.1 · 11.2 · 11.3 · 11.5 · 11.6 · 11.8 | ESC-11.1 · 11.2 · 11.6 |
| HU-11.2 | Como **responsable de seguridad**, quiero que solo el administrador del sistema pueda cambiar roles y que nunca desaparezca el último, para no quedar sin control del módulo | RF-11.7 | ESC-11.3 · 11.5 |
| HU-11.3 | Como **módulo consumidor**, quiero recibir en el token los roles y permisos efectivos del usuario, para autorizar sin llamar a Seguridad | RF-11.4 · 11.9 · 11.10 | ESC-11.4 · 11.7 |

## SPEC-12 — Registro de auditoría

| HU | Historia | Requisitos | Escenarios |
|---|---|---|---|
| HU-12.1 | Como **responsable de seguridad**, quiero que cada acción sensible deje un registro que nadie pueda alterar ni que contenga secretos, para reconstruir lo que pasó | RF-12.1 a 12.6 | ESC-12.1 a 12.6 |
| HU-12.2 | Como **responsable de datos personales**, quiero que los registros se borren solos pasados 90 días, para no guardar más de lo necesario | RF-12.7 | ESC-12.7 |

## SPEC-13 — Consulta y exportación de la auditoría

| HU | Historia | Requisitos | Escenarios |
|---|---|---|---|
| HU-13.1 | Como **administrador del sistema**, quiero consultar y exportar la auditoría con filtros, para investigar un incidente | RF-13.1 a 13.4 | ESC-13.1 a 13.5 |
| HU-13.2 | Como **usuario**, quiero ver la actividad reciente de mi cuenta, para detectar un acceso que no hice | RF-13.5 | ESC-13.6 |

## SPEC-14 — Bloqueo automático por intentos fallidos

El recorrido completo de estados (`ACTIVO → BLOQUEADO → ACTIVO`) está en
[`docs/arquitectura/estados-usuario.md`](../docs/arquitectura/estados-usuario.md).

| HU | Historia | Requisitos | Escenarios |
|---|---|---|---|
| HU-14.1 | Como **usuario**, quiero que mi cuenta se bloquee tras varios intentos fallidos, para proteger mi información | RF-14.1 a 14.7 · 14.9 · 14.10 | ESC-14.1 a 14.11 · 14.16 |
| HU-14.2 | Como **usuario**, quiero desbloquear mi cuenta con un enlace de mi correo o restableciendo mi contraseña, para recuperar el acceso sin esperar a nadie | RF-14.8 · 14.11 · 14.12 | ESC-14.12 a 14.15 |

## SPEC-15 — Bloqueo y desbloqueo por un administrador

| HU | Historia | Requisitos | Escenarios |
|---|---|---|---|
| HU-15.1 | Como **administrador del sistema**, quiero bloquear una cuenta manualmente con un motivo, para cortar un acceso sospechoso | RF-15.1 · 15.2 · 15.4 · 15.5 · 15.7 | ESC-15.1 · 15.2 · 15.3 · 15.6 · 15.7 · 15.8 |
| HU-15.2 | Como **administrador del sistema**, quiero ver por qué y hasta cuándo está bloqueada una cuenta y poder desbloquearla, para atender a quien me lo pida | RF-15.3 · 15.6 | ESC-15.4 · 15.5 · 15.9 |

## SPEC-16 — Atributos de usuario

| HU | Historia | Requisitos | Escenarios |
|---|---|---|---|
| HU-16.1 | Como **usuario**, quiero ver y actualizar mis datos de perfil, para que el marketplace me identifique bien | RF-16.1 · 16.2 · 16.3 · 16.9 · 16.10 | ESC-16.1 · 16.2 · 16.6 |
| HU-16.2 | Como **usuario**, quiero cambiar mi correo o mi celular verificando el nuevo, para no perder el acceso si me equivoco | RF-16.6 · 16.7 | ESC-16.3 · 16.4 |
| HU-16.3 | Como **cliente**, quiero guardar mis direcciones de entrega y marcar una como predeterminada, para no escribirlas en cada compra | RF-16.5 | ESC-16.9 |
| HU-16.4 | Como **cliente**, quiero que mi número de documento se guarde cifrado y solo lo vea quien lo necesita, para proteger mis datos personales | RF-16.4 | ESC-16.5 · 16.7 · 16.8 |
| HU-16.5 | Como **administrador del sistema**, quiero editar los atributos de un vendedor, para mantener al día su tienda y su código | RF-16.8 | ESC-16.10 |

## SPEC-17 — Claves públicas, tokens de servicio e introspección

Aquí, y en SPEC-18, el usuario es **otro equipo del curso**: la consumen
máquinas, no personas.

| HU | Historia | Requisitos | Escenarios |
|---|---|---|---|
| HU-17.1 | Como **equipo de otro módulo**, quiero validar el token del usuario con una clave pública, para autorizar cada petición sin llamar a Seguridad | RF-17.1 · 17.2 · 17.3 | ESC-17.1 · 17.2 |
| HU-17.2 | Como **equipo de otro módulo**, quiero obtener un token de servicio con los scopes justos, para consultar datos de usuarios de forma controlada | RF-17.4 · 17.5 · 17.8 · 17.9 · 17.10 | ESC-17.3 · 17.4 · 17.7 · 17.8 |
| HU-17.3 | Como **módulo que hace operaciones sensibles**, quiero preguntar si una sesión sigue viva, para no aceptar a un usuario dado de baja o bloqueado | RF-17.6 · 17.7 | ESC-17.5 · 17.6 |
| HU-17.4 | Como **equipo de otro módulo**, quiero un entorno simulado con datos de prueba, para programar antes de que Seguridad esté implementado | RF-17.11 | ESC-17.9 |

## SPEC-18 — Consulta de identidad para los demás módulos

| HU | Historia | Requisitos | Escenarios |
|---|---|---|---|
| HU-18.1 | Como **equipo de otro módulo**, quiero consultar usuarios, direcciones y roles sin ver datos que mi scope no permite, para cumplir mi función sin exponer datos personales | RF-18.1 a 18.6 | ESC-18.1 a 18.7 |
