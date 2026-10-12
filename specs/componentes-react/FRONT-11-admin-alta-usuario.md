# FRONT-11 — Panel de administración: alta de cuenta (componente React)

| Campo | Valor |
|---|---|
| Spec de interfaz | `specs/front/FRONT-11-admin-alta-usuario.md` |
| Specs de backend | SPEC-03 (alta y activación) |
| Wireframe | Sin wireframe — el listado (pantalla 7) tiene el botón que lleva aquí |
| **Responsable** | Eva Lucía Moreno Zevallos |
| **Épica principal** | [EP-01](../epicas/EP-01-registro-gestion-usuarios.md) |


> Revisión de trabajo 11-oct: alineada con A6; pendiente de revisión humana.

---

> **Corte 11-oct:** alta administrativa estricta en [v2 local](../contrato-api/openapi-v2.yaml).
> Las consultas, alta asistida de clientes y activación de cuenta siguen en v1.
> Publicación pendiente; términos/pantalla de activación A6 todavía requieren H-12.

## Objetivo

Implementar [FRONT-11](../front/FRONT-11-admin-alta-usuario.md): creación sin
contraseña inicial, cuenta pendiente y activación por enlace. Eva conserva
la funcionalidad completa y sus dependencias se consumen de sus dueños.

## Endpoints

`POST /api/v2/usuarios` con usuario.crear. Tras éxito, consultar/abrir detalle
solo con usuario.ver. No consulta password/politica ni solicita OTP en el alta.

## Archivos propuestos

`AdminAltaPage.tsx`: campos, opción «Sin rol», aviso de activación y UUID.
`useAltaUsuario.ts`: valida, construye request y maneja 400/409/503.
Ningún código de aplicación se crea en esta etapa documental.

## Estado, petición y resultado

Correo, nombres, apellidos y rol opcional. «Sin rol» **omite** rol.
Sin contraseña, repetir ni medidor. No enviar null ni roles[] en petición.
Roles y estados canónicos de CONTEXT; seis roles globales, cinco elegibles
en esta alta. REPARTIDOR permanece perfil de Despacho, no rol de Seguridad.
`201` muestra estado pendiente/enlace 72 h; nunca permite login inmediato.

## Validaciones y errores

Correo, nombres y apellidos obligatorios; las demás de FRONT-11. Roles de gestión avisan MFA y política vigente. CrearUsuarioRequest estricto en contrato local v2. Errores por code, sin revelar cuentas en flujo
público. Sin usuario.ver no buscar correo/listado ni consultar detalle.

## Escenarios y accesibilidad

UI-11.1–UI-11.6 de FRONT-11; select etiquetado, foco de errores, avisos accesibles.
Verificar alta sin rol, petición sin contraseña y ausencia de consulta sin permiso.

## Fuera de alcance y pendientes

Sin alta CLIENTE, perfiles de negocio ni contraseña inicial.
La activación A6 necesita pantalla especificada y H-12; FRONT-03 simple no
la cubre. Su implementación no se da por preparada mediante este componente.
