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
