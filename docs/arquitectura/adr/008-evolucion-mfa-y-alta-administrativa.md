# ADR-008 — Evolución segura de MFA y alta administrativa

**Fecha:** 11-oct-2026. **Estado:** decisión de trabajo local por edición delegada.
**PO:** Sergio Osorio. Luis David comunicó en esta conversación que Sergio le
autorizó a trabajar por él. Se registra esa autorización para esta revisión;
no aprobación individual de los siete estudiantes ni aceptación externa.

## Contexto

H-01/H-02 impedían definir FRONT-09: falta estado y confirmación MFA.
Deshabilitar sin OTP contradice SPEC-10. H-10 permite CLIENTE en un schema
de alta que funcionalmente lo excluye. Los consumidores tienen una base v1
publicada; ADR-003 exige versionar incompatibilidades y aviso de una semana.

## Decisión

1. Conservar [base 1.0.0](../../../specs/contrato-api/historico/openapi-1.0.0.yaml)
   byte a byte como referencia histórica; no describe una implementación segura de MFA.
2. [v1 1.1.0 local](../../../specs/contrato-api/openapi.yaml): correcciones
   descriptivas, campos privados adicionales de perfil y Retry-After. Peticiones,
   rutas, roles y autenticación pública conservan su forma.
3. [v2 parcial 2.0.0 local](../../../specs/contrato-api/openapi-v2.yaml):
   solicitud/reenvío/confirmación MFA con código y alta administrativa estricta.
   No duplica login, refresh, activación de cuenta ni JWKS. Ambas versiones
   usan identidad e issuer del mismo entorno; no se añade audiencia de usuario.
4. Las rutas MFA v1 incompletas quedan deprecadas e identificadas como
   bloqueadas por seguridad. No se implementan/despliegan sin confirmación.
   No se modifica silenciosamente su petición publicada para que exija OTP.
5. Alta v2 excluye CLIENTE y contraseña inicial; rol opcional, nombres/apellidos
   y correo obligatorios. Activación posterior continúa en v1 según A6.
6. FRONT-09 queda especificada sin huecos de interfaz H-01/H-02, pendiente de diseño
   visual, DAT-01 y servicios. Publicación/disponibilidad externa pendientes.

## Consecuencias

- Luis David conserva EP-04 completa; Eva conserva alta EP-01; Jose Luis
  provee resolución de sesión/familia y Christian auditoría. Sin reparto por capas.
- mfaChangeToken vincula usuario, familia y acción, resueltos en servidor.
  No se incorpora claim público nuevo: DAT-01 exige diseñar persistencia y
  asociación interna de jti con familia antes de implementar ese vínculo.
- El contrato se valida con scripts/verificar-contratos.py: estructura,
  referencias, ejemplos y conservación de peticiones v1. No demuestra servidor
  ni propiedades criptográficas; esas pruebas siguen en el punto 9.
- H-12 texto/versión de términos y H-14 contacto de soporte siguen abiertos.
  Luis no identifica una definición existente; no se inventan contenidos ni aceptación.

## Publicación y Hito 3

Se mantiene ADR-003 y el aviso de una semana: no se acuerda una excepción.
Las revisiones locales no se anuncian disponibles ni se usan para exigir una
migración a consumidores dentro de cinco días. El candidato Ventas usa login
y JWKS v1 existentes; no necesita estas nuevas operaciones. El recorrido, Michael y fecha G5 están confirmados en
[G5 Backend #2](https://github.com/Taller-SW-Web/G5-Ventas-Postventas-Backend/issues/2),
con acuse de Luis publicado. Implementación, configuración, despliegue y prueba
conjunta reales siguen pendientes.

No se hicieron commits, publicación ni comunicaciones externas.
