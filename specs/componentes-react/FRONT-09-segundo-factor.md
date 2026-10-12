# FRONT-09 — Segundo factor (componente React)

| Campo | Valor |
|---|---|
| **Responsable** | Luis David Morales Brenis |
| **Épica principal** | [EP-04](../epicas/EP-04-segundo-factor.md) |
| Spec de interfaz | [FRONT-09](../front/FRONT-09-segundo-factor.md) |
| Specs de backend | SPEC-10, SPEC-09 y SPEC-16 |
| Estado | Definido localmente; sin implementar ni publicar |
| Wireframe | Pendiente |

## Objetivo

Luis David implementa la pantalla completa de FRONT-09 y sus capacidades de
MFA. Consume sesión de Jose Luis, roles de Sergio, contactos de Valery y
auditoría de Christian; esos apoyos no reparten su épica por capas.

## Endpoints

GET /api/v1/auth/me para estado. POST /api/v2/auth/mfa/cambios y sus operaciones
reenviar/confirmar según [OpenAPI v2](../contrato-api/openapi-v2.yaml).
No llamar /api/v1/auth/otp/habilitar ni /deshabilitar.

## Archivos propuestos

- `SegundoFactorPage.tsx`: carga perfil, estado efectivo y acciones.
- `ConfirmarCodigo.tsx`: diálogo de seis dígitos y vencimientos.
- `useSegundoFactor.ts`: solicitud/reenvío/confirmación, errores y concurrencia.

Reutiliza api, sesion y errores de FRONT-00 y OtpInput de FRONT-04.
Estas rutas de archivos describen implementación futura, no código creado.

## Estado y responsabilidad

Solo memoria: acción, mfaChangeToken, correo enmascarado y vencimientos
absolutos calculados al recibir 202. No usar el estado de desafío de login.
Tres llamadas con accessToken vigente; serializar doble clic. No cambiar
bandera hasta 204 y nueva lectura; Cancelar limpia estado local sin endpoint.
Un refresh exitoso de la misma familia mantiene vínculo; recarga vuelve a login.

## Validaciones y errores

Código string de seis dígitos. El contador usa menor vencimiento entre código
y cambio. Retry-After impide reenvío manual hasta liberar cuota; ningún envío
automático. MFA_OBLIGATORIO pasa a estado obligatorio; TOKEN_INVALIDO limpia
sesión y operación. Red incierta tras confirmar: consultar estado antes de
repetir. Los demás mensajes son los de FRONT-09 y FRONT-00.

## Escenarios y accesibilidad

UI-09.1–UI-09.10 de FRONT-09: ambos cambios, roles múltiples, errores, reenvío,
límite, doble clic, sesión vencida y recuperación tras red. Foco de diálogo,
pegado de código, etiquetas, avisos y teclado; contador sin anuncios repetidos.

## Fuera de alcance y cierre

No contactos externos, SMS real, MFA ajeno ni llamadas históricas incompletas.
Implementación, wireframe y comprobación de seguridad de servidor pendientes.
La definición contractual local no demuestra consumo único ni auditoría real.
