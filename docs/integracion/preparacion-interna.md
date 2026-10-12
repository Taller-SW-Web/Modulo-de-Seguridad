# Preparación interna — revisión de épicas y cierre contractual local

**Corte:** 11-oct-2026. Los dos bloques acordados se completan documentalmente:
[revisión por épica](revision-por-epica.md) y aplicación de decisiones de contrato
por delegación del PO, según autorización comunicada por Luis David.
Sergio conserva propiedad; [ADR-008](../arquitectura/adr/008-evolucion-mfa-y-alta-administrativa.md)
distingue edición local, publicación y despliegue.

No se hicieron commits ni comunicaciones. No se espera a Joseph para definir
capacidades internas; sí para declarar acordada la demo conjunta.

## Segundo factor

H-01/H-02/H-05/H-06 definidos en contratos locales. FRONT-09 ya tiene flujo
completo de estado/solicitud/reenvío/confirmación, con código separado del login.
MFA efectivo obligatorio si existe un rol de gestión. Cambio asociado a
usuario, familia y acción; vigencia 600 s, código 300 s, tres intentos y cuota
compartida de tres envíos en 15 min. Confirmación crítica y de uso único.

[OpenAPI v2](../../specs/contrato-api/openapi-v2.yaml) define el cambio seguro.
Las rutas antiguas MFA v1 quedan deprecadas y bloqueadas para implementación;
no se despliega desactivación sin OTP. FRONT-09 está lista como definición
local, pendiente de DAT-01, wireframe, servicios y publicación.
A2: sin verificación externa de contactos; SMS sigue simulado.

## Kit, tokens y sesión

- Issuer exacto del descubrimiento, configurado por entorno; JWKS y
  descubrimiento distintos. Ejemplos Node, Spring y .NET alineados.
- RS256, UUID en sub, roles múltiples y tipo acceso para usuario. No exigir
  aud de servicio al usuario; servicio sí valida aud de API dueña.
- Reúso de refresh revoca familia y sesión asociada; otras sesiones permanecen.
- Tokens solo en memoria para esta versión; recarga exige login. No token
  en URL ni almacenamiento persistente. Refresh serializado, sin reintento
  ciego si su resultado se desconoce.
- Login mantiene CREDENCIALES_INVALIDAS genérico; CUENTA_NO_DISPONIBLE solo
  corresponde al bloqueo administrativo. Retorno al login configurado del canal.
- Ejemplos ilustrativos del mock no prueban firma. Punto 9 conserva las pruebas
  con JWT RS256 firmados y compilación de Spring/.NET.

## Otras decisiones

| Asunto | Tratamiento local | Pendiente |
|---|---|---|
| UI-01/A6/H-10 | Alta sin contraseña inicial, pendiente, rol opcional; cinco roles admitidos en POST /api/v2/usuarios; nombres/apellidos obligatorios | Pantalla de activación y H-12; publicación |
| H-01 atributos/H-13 | Titular/admin lee campos privados según roles, documento enmascarado; servicio omite privados. Edición propia solo nombres/apellidos/celular; otros atributos con atributos.editar | Implementación y pruebas de privacidad/denegación |
| H-03 | Celular queda no verificado, sin OTP de login para verificarlo | A2 conserva limitación |
| H-04 | Listar/crear direcciones; predeterminada al crear | Editar/borrar aplazados |
| H-07 | hasta null también para automático indefinido desde cuarto consecutivo; filtro de una acción | Implementación transaccional y auditoría |
| H-08 | Exportación fuera de H3; no prometer filtros distintos a consulta | Alinear filtros antes de implementar exportación |
| H-09 | Etiquetas de seis roles; ninguna credencial de servicio en navegador | Implementación |
| H-12 | Texto, versión y fuente de términos no definidos según respuesta de Luis | Definir contenido antes de registro/activación; no inventar aceptación de fixtures |
| H-14 | Mensaje provisional de contactar administrador; no correo/URL ficticios, no revela bloqueo en login | Definir contacto real de soporte |
| H-15 | Enlace vencido/usado detectado al enviar formulario | Implementación; sin preconsulta |

## Scopes y coordinación

[Matriz](matriz-scopes.md): aprobaciones separadas de provisión; A10 confirmado
por #42, permiso de documento provisional, A9 condicionado. Seis roles,
perfiles de negocio locales, búsquedas restringidas, sin invitados. Scopes de
API ajena no añaden endpoints especiales de Seguridad.

Pedidos por issues y aprovisionamiento genérico conservados. Suscripciones
de eventos requieren confirmación individual. No se comunicaron correcciones.

## Salida y límites

El aviso contractual de una semana se mantiene (ADR-003); ninguna excepción
por plazo. Nuevas revisiones locales no se anuncian disponibles a consumidores.
Ventas candidato H3 consume login/JWKS v1 existentes; Marketplace y los demás
siguen en alcance general. [Ficha](hito-3-ventas.md): recorrido y Michael
confirmados en G5 Backend #2; objetivo jueves 15. Configuración, rama, hora y
prueba conjunta pendientes.

Comprobaciones reproducibles: estructura/referencias/ejemplos/schema de los
contratos y conservación de peticiones v1, enlaces y reparto.
Esos verificadores por sí solos no validan autenticación, auditoría ni
concurrencia. La [continuación del punto 9](validacion-hito-3.md) sí probó
fixtures JWT firmadas contra los ejemplos del kit, además de mocks HTTP;
no acreditó servidores de Seguridad/Ventas ni integración en nube.
DAT-01 continúa abierto; no se generó migración ni se modificó Java/SQL.
Sin cuentas, credenciales, pedidos o despliegues creados por esta preparación.
