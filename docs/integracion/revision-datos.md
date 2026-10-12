# DAT-01 — contraste de persistencia previo a implementar

Revisión local 11-oct-2026. No modifica SQL, DBML, Java ni base de datos.
No se ha comprobado qué migraciones están aplicadas en un entorno real.

| Evidencia | Divergencia | Dueño / acción antes de implementar |
|---|---|---|
| [Modelo descriptivo](../arquitectura/modelo-datos.md), tabla de versiones | Declara V5__fk_otp_desafio.sql; el directorio real solo contiene V1, V2, V3, V4 y V100 | Jose Luis/Luis David: reconciliar cadena real con relación OTP/desafío; no afirmar V5 ejecutada |
| [V2](../../backend/src/main/resources/db/migration/V2__credenciales_otp_tokens.sql) vs [DDL](../arquitectura/schema.sql) y DBML | V2 otp no contiene desafio_id y usa hash_codigo/expira_en/usado; DDL contiene desafio_id y otra representación del código/consumo | Luis David revisa OTP y Jose Luis la relación con desafio_mfa; acordar representación y migración nueva |
| [V3](../../backend/src/main/resources/db/migration/V3__sesion_y_bloqueo.sql) | desafio_mfa representa solo challengeToken de login, sin operación separada de cambio MFA | Luis David prepara entidad de cambio; Jose Luis conserva login/sesión; propósito y usuario vinculados en servidor |
| [DBML](../arquitectura/schema.dbml) y DDL | OTP con desafio_id null se describe para verificar contactos sin login; A2 no ofrece ese flujo | Revisar descripción/uso real; no implementar verificación externa por existencia de campo nullable |
| V2 credencial.password_hash NOT NULL y A6 | Cuenta pendiente no tiene contraseña inicial; no crear credencial ficticia para satisfacer NOT NULL | Eva crea cuenta; Juan José provee credencial cuando titular activa. La ausencia de fila hasta activar no exige hash nullable |

Una nueva migración requiere revisión de las entidades/repositorios ya
existentes y prueba del esquema resultante. Si V1–V4 ya fueron aplicadas, no
editarlas para reescribir historia. Este contraste identifica pendientes; no
certifica exhaustivamente equivalencia de todas las columnas/constraints.


## Capacidad interna de sesión requerida por SPEC-10

Jose Luis provee resolución `jti → usuarioId/familiaId/vigencia` y comprobación
de que la familia no esté revocada. Luis almacena el vínculo del cambio MFA
con usuario/familia/acción. Una renovación en la misma familia conserva vínculo;
un acceso de otra familia no lo satisface. El cliente no elige esos valores.

Debe diseñarse persistencia o mecanismo interno que permita resolver cada
accessToken vigente; no inferir familia solo por sub. No se añade un claim
público ni endpoint externo. La versión local define la dependencia, no
acredita que la relación exista en entidades/migraciones actuales.

## Partes necesarias para los incrementos H3 — actualización 12-oct

La selección de [planes por estudiante](../../plan/hito-3/README.md) requiere
resolver estas partes antes de sus tareas dependientes; no se modificó SQL/Java.

| Parte | Contraste / propuesta técnica | Dueño / comprobación antes de implementar |
|---|---|---|
| Sesión actual en cambio de contraseña | OpenAPI /password/cambiar conserva una familia y revoca otras; V3 no acredita vínculo del jti de acceso a familia | Jose Luis con Juan José: asociación persistida, locks y dos sesiones; no inferir por sub ni añadir familia al cuerpo HTTP |
| OTP/desafío | V2/entidad OTP frente a DesafioMfa/V3; falta relación inequívoca coherente con el DDL | Luis/Jose Luis: propósito, expiración, reenvío y consumo único con familia real; migración nueva si se requiere |
| Fallos distintos | V1 tiene contadores y V3/IntentoLogin no guardan huella de contraseña incorrecta | Luis/Eva: propuesta HMAC por usuario/racha con clave externa y persistencia acotada; misma entrada no suma, concurrencia y reinicio no pierden deduplicación |
| Entrega sensible | V4 outbox.payload genérico no prueba protección del código/enlace que debe entregarse | Christian/productores: payload cifrado con clave externa y purga tras entrega; tabla OTP/token conserva solo hash, sin logs de secretos |
| Último administrador | Rol/asociación existen, no se acredita lock que serialice revocaciones de dos cuentas diferentes | Sergio/Eva/Luis: estado efectivo y lock compartido del catálogo; proteger último activo/prohibición propia en transacción |

[Diseño de composición](../../plan/hito-3/ACUERDOS-INTERNOS.md) detalla
decisiones cerradas y responsables. Cambio MFA v2 permanece posterior; esa parte
pendiente no se exige para el OTP de login seleccionado.

## Diseño de persistencia H3 cerrado por delegación

El contraste anterior queda como evidencia de divergencias. Su tratamiento H3
está concreto en [propiedad/mappings/migraciones](../../plan/hito-3/DISENO-PERSISTENCIA.md)
y [locks/transacciones](../../plan/hito-3/ACUERDOS-INTERNOS.md). Sesión raíz
separada, acceso asociado, OTP/revisión, huellas por racha, enlace contextual y
outbox cifrado ya no esperan decisión rutinaria de dueños. La revisión del
esquema real en Supabase y ejecución/pruebas de las migraciones quedan en
implementación; no se afirma que esos objetos existan hoy en la base.
