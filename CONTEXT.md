# Módulo de Seguridad y Autenticación

El proveedor de identidad del Marketplace Multicanal de Productos Deportivos:
es dueño de la entidad usuario y resuelve toda decisión de acceso del sistema.
Los otros seis módulos dependen de él y ninguno puede leer su base de datos.

Este glosario fija el vocabulario del dominio. Los términos que aparecen en
`specs/openapi.yaml` son parte del contrato y **no se renombran
unilateralmente**: seis equipos ajenos programan contra ellos.

## Language

### El módulo y su método

**Proveedor de identidad**:
El papel de este módulo dentro del marketplace: el único servicio que guarda
credenciales, emite tokens y responde quién es alguien y qué se le permite.
_Avoid_: servidor de autenticación, gestor de accesos, servicio de login

**Módulo consumidor**:
Uno de los otros seis módulos del marketplace, construido por otro equipo, que
depende de este para identificar a sus usuarios.
_Avoid_: cliente, consumidor a secas, módulo externo, tercero

**Contrato**:
La descripción pública y versionada de la API en `specs/openapi.yaml`, junto con
los códigos de rol, scopes, códigos de error y eventos que la acompañan. Es lo
único que los módulos consumidores necesitan conocer, y tiene dueño único.
_Avoid_: la API a secas, el swagger, la documentación

**SPEC**:
Una unidad de funcionalidad especificada con las siete secciones de
`specs/_PLANTILLA.md`, numerada y con un responsable único. Es la unidad de
reparto del trabajo y la unidad de aprobación.
_Avoid_: historia de usuario, requerimiento, feature, épica

### Tokens

**Token de acceso**:
La credencial de vida corta que un usuario presenta en cada petición para
demostrar quién es. Viaja como `accessToken` en el contrato.
_Avoid_: access token, `access_token`, el JWT, el bearer

**Token de refresco**:
La credencial de vida larga que sirve para obtener un token de acceso nuevo sin
volver a introducir la contraseña. Viaja como `refreshToken` en el contrato.
_Avoid_: refresh token, `refresh_token`, «el refresco» a secas

**Familia**:
El conjunto de tokens de refresco encadenados que nacen de un mismo inicio de
sesión, cada uno rotado a partir del anterior. Presentar uno ya rotado revoca la
familia entera.
_Avoid_: cadena, linaje, sesión, árbol de tokens

**Token de servicio**:
La credencial propia de un módulo consumidor, que lo identifica a **él** y no a
una persona. Cada uno de los seis módulos tiene el suyo.
_Avoid_: service token, token de máquina, token de aplicación, token interno

**Desafío**:
El paso intermedio del inicio de sesión de una cuenta con segundo factor: no hay
tokens todavía, solo un comprobante de vida corta con el que canjear el código
de un solo uso. Viaja como `challengeToken` en el contrato.
_Avoid_: challenge, reto, token MFA, token temporal

### Cuentas

**Estado de cuenta**:
La situación de una cuenta frente al acceso, y una de exactamente cuatro:
`PENDIENTE_VERIFICACION`, `ACTIVO`, `BLOQUEADO`, `INACTIVO`. Las tres últimas que
no son `ACTIVO` impiden iniciar sesión y responden el mismo error.
_Avoid_: situación, estatus, flag de activo

**Bloqueo**:
La suspensión temporal o indefinida del acceso a una cuenta. Es **automático**
cuando lo dispara una racha de intentos fallidos y **manual** cuando lo decide un
administrador; en ambos casos la cuenta queda en el mismo estado y solo cambia
si el bloqueo vence.
_Avoid_: suspensión, baneo, cuenta cerrada, dos términos distintos para el
automático y el manual

**Baja lógica**:
La desactivación de una cuenta que la deja inutilizable sin borrarla de la base
de datos. Ninguna cuenta se elimina físicamente.
_Avoid_: eliminar, borrar, dar de baja, soft delete

**Verificación de correo**:
La confirmación, mediante un enlace de un solo uso, de que quien se registró
controla realmente la dirección que declaró.
_Avoid_: activación, confirmación de cuenta, validación de correo

**Segundo factor**:
La comprobación adicional al inicio de sesión mediante un código de un solo uso,
de modo que conocer la contraseña no baste. Obligatorio para administradores,
opcional para el resto.
_Avoid_: MFA a secas, 2FA, doble autenticación, autenticación en dos pasos

### Roles y permisos

**Rol**:
Uno de los seis perfiles fijos del sistema: `CLIENTE`, `VENDEDOR`,
`ADMIN_VENTAS`, `GESTOR_DESPACHO`, `GESTOR_COMERCIAL`, `ADMIN_SISTEMA`. Un
usuario puede tener varios a la vez y el conjunto es cerrado.
_Avoid_: perfil, tipo de usuario, grupo, y los códigos en inglés (`BUYER`,
`SELLER`, `SYSTEM_ADMIN`), descartados en
[ADR-004](docs/arquitectura/adr/004-idioma-del-contrato.md)

**Permiso**:
Una capacidad concreta que un rol concede a una **persona**, nombrada por módulo
y acción (`pedido:crear`, `producto:editar`).
_Avoid_: privilegio, autorización, capacidad, y usar «permiso» para un scope

**Permisos efectivos**:
La unión sin duplicados de los permisos de todos los roles de un usuario, que es
lo que viaja en su token y lo que un módulo consumidor evalúa.
_Avoid_: permisos del usuario, permisos totales, permisos resueltos

**Scope**:
Lo que un **módulo consumidor** puede leer de esta API, concedido a su token de
servicio (`usuarios:leer`, `tokens:introspeccion`). Un scope habla de módulos y
de lectura; un permiso habla de personas y de acciones.
_Avoid_: permiso del módulo, alcance, autorización de servicio

### Integración entre módulos

**Validación local**:
La comprobación de la firma de un token que un módulo consumidor hace por su
cuenta, con la clave pública y sin llamar a este servicio. Es la vía ordinaria y
la que se usa en casi todo el tráfico.
_Avoid_: validar el token a secas, verificación offline, comprobación de firma

**Introspección**:
La consulta a este servicio del estado **actual** de un token y de su usuario, no
del que tenía cuando se emitió. Es lo que se usa antes de una operación
sensible, y es un concepto distinto de la validación local.
_Avoid_: validar contra el servidor, verificación remota, consultar el token

**Evento**:
Un aviso asíncrono que este módulo publica cuando algo cambia en una cuenta
(`usuario.bloqueado`, `usuario.roles_cambiados`), para que quien cachea datos se
entere sin preguntar. Es eventualmente consistente y **no es un control de
acceso**.
_Avoid_: mensaje, notificación, webhook, señal

**JWKS**:
El documento público que contiene la clave con la que cualquiera puede verificar
que un token lo emitimos nosotros. Es lo único que un módulo consumidor necesita
para la validación local.
_Avoid_: clave pública a secas, certificado, llavero

**Entorno simulado**:
La instancia que responde el contrato con datos de prueba conocidas, publicada
antes de que exista la implementación para que los seis equipos no esperen.
_Avoid_: preferimos este término sobre «mock» como sustantivo, aunque «mock
server» aparece en el README y en el plan del Hito 1 y ahí no es un error

**Ventana de incoherencia**:
El intervalo durante el cual un token sigue siendo válido aunque el estado real
del usuario ya haya cambiado. Es el precio deliberado de la validación local, no
una carencia.
_Avoid_: desfase, retraso de propagación, inconsistencia, latencia de revocación
