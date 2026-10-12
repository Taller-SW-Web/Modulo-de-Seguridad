# Estado de los issues consultados — 12-oct-2026

Se leyeron cuerpo, estado y comentarios de los 14 issues de la captura.
**Todos siguen abiertos** en GitHub al consultar. Se contrasta su alcance con
main `5796622f7c039021e03dd25b2338c6a208320e05`, fusionado por
[PR #46](https://github.com/Taller-SW-Web/Modulo-de-Seguridad/pull/46),
y con el árbol de trabajo posterior identificado aquí. No se cerraron,
reasignaron ni comentaron issues desde esta sesión.

«Cubierto documentalmente» no equivale a implementación, pruebas actuales,
credenciales provisionadas o integración en nube. Los comentarios históricos
son evidencia de su fecha, no una ejecución de pruebas en esta revisión.

## Cobertura y siguiente condición de cierre

| Issue | Cobertura encontrada | Qué falta / tratamiento |
|---|---|---|
| [#44, retirar CSAT](https://github.com/Taller-SW-Web/Modulo-de-Seguridad/issues/44) | **Pedido documental cubierto en main:** kit, acuerdos y matriz retiraron las tres concesiones sin reemplazo; token CLIENTE/sub en Postventa | Candidato a cierre del ajuste de catálogo tras revisión del PO. No acredita cambios de una provisión real inexistente |
| [#42, A10](https://github.com/Taller-SW-Web/Modulo-de-Seguridad/issues/42) | Acuerdo confirmado, siete scopes propios y dos clientes nuevos previstos; body original superado por comentarios finales | Último comentario retira también documento para Ventas: ajuste local de este bloque, aún por publicar. Provisión/credenciales/pruebas siguen pendientes; no cerrar ejecución de A10 como realizada |
| [#37, SPEC-03 ampliada](https://github.com/Taller-SW-Web/Modulo-de-Seguridad/issues/37) | RF/ESC de A6/A7 en SPEC-03; alta estricta v2 y activación v1 definidos; clasificación H3/H4 preservada | Revisión actual por Eva, término H-12 y activación UI; controladores/servicios no implementados. Búsqueda y limpieza de 30 días posteriores. Asignación antigua a Christian |
| [#36, pantallas A6](https://github.com/Taller-SW-Web/Modulo-de-Seguridad/issues/36) | FRONT-11 y React ya sin contraseña inicial, rol opcional, cuenta pendiente/enlace | **Parcial:** falta nueva spec UI/React de activación con errores/política/términos y su índice/mockup. Dueña actual Eva; asignación antigua Valery. No cerrar |
| [#33, scopes Productos para Chatbot](https://github.com/Taller-SW-Web/Modulo-de-Seguridad/issues/33) | Siete scopes y audiencia identificados, A9 condicionado en matriz/kit | En este issue no hay confirmación del PO de Productos: mantener condición y obtener evidencia antes de conceder/provisionar |
| [#32, URL Chatbot](https://github.com/Taller-SW-Web/Modulo-de-Seguridad/issues/32) | Ruta /verificar-correo?token=... aceptada por Sergio; política A1 definida | Bases de desarrollo/producción no aportadas en comentarios; configurarlas/probar. Dueña actual Eva, no Juan José |
| [#25, Figma/UX](https://github.com/Taller-SW-Web/Modulo-de-Seguridad/issues/25) | **Entregables documentales H2 presentes:** guía con biblioteca/responsable, sin marcador literal [PENDIENTE]; índice no vacío, seis pares PNG y tres propuestas. Último comentario reporta pendientes resueltos | Candidato a cierre H2 tras revisión visual/publicación de biblioteca por el equipo. Esta consulta no verifica estado actual de Figma ni aprueba los mockups frente a los cambios de H3 |
| [#24, Docker/CI/auditoría/clientes](https://github.com/Taller-SW-Web/Modulo-de-Seguridad/issues/24) | Docker Compose, V4 y entidades existen; comentario histórico registra integración y validación Hibernate | **Parcial:** no existe .github/workflows/ci.yml, faltan repositorios de esas entidades y exportaciones FRONT-07/08/11/14; prueba Docker/CI reproducible pendiente. No cerrar |
| [#23, credenciales/mockups](https://github.com/Taller-SW-Web/Modulo-de-Seguridad/issues/23) | V2, entidades, cuatro repositorios y pruebas están presentes; issue tiene tres criterios marcados; índice PNG incluye recuperación/cambio | Confirmar ejecución PostgreSQL real y evidencia/mapping de los dos mockups FRONT-05/10. No se ejecutaron pruebas backend en esta revisión; candidato solo al comprobar su criterio H2 |
| [#22, cuentas/roles/datos](https://github.com/Taller-SW-Web/Modulo-de-Seguridad/issues/22) | Modelo, V1/V100 existen; comentario de revisión H2 acepta esos artefactos | **Parcial:** faltan entidades/repositorios/pruebas de las ocho tablas pedidas; UsuarioEstadoEfectivo no las sustituye. V100 ya incluye 8 permisos admin + 2 vendedor; body original conserva recuento anterior. Cadena/mapping real por probar |
| [#18, Marketplace→Despacho](https://github.com/Taller-SW-Web/Modulo-de-Seguridad/issues/18) | Contrato/acuerdo y catálogo; comentario confirma aud api-despacho y 401/403, pedido formalizado | Creación/concesión del cliente y prueba conjunta H4 pendientes; el propio issue exige esa prueba para cierre |
| [#11, contacto A2](https://github.com/Taller-SW-Web/Modulo-de-Seguridad/issues/11) | **Decisión de alcance cubierta:** fuera del ciclo, sin endpoint/scope; specs alineadas para no prometer flujo inexistente | Mantener como reevaluación H4 si se aporta caso concreto; no tratarlo como fallo pendiente que obligue a implementar verificación externa |
| [#10, aprovisionamiento](https://github.com/Taller-SW-Web/Modulo-de-Seguridad/issues/10) | Tablas de V4 y matriz de concesiones existen; A3/A4/A8/A9/A10 trazados | Catálogo semilla actualizado, ocho clientes previstos, secretos/concesiones y entrega segura sin evidencia. H4; Sergio catálogo y Christian operación. Demo H3 con acceso de usuario no necesita estos secretos |
| [#9, canalOrigen](https://github.com/Taller-SW-Web/Modulo-de-Seguridad/issues/9) | Política especificada en SPEC-02/contrato/kit; retorno de Retail corregido a su canal configurado | Configuración y enlaces/correo/retorno reales, URL faltantes y pruebas. Dueña actual Eva; comentario histórico que remite Retail a Marketplace ya no gobierna |

No todos los issues son «errores»: unos registran acuerdos, otros preparación
H2, otros trabajo H3/H4. El criterio de cierre se toma del pedido final y la
evidencia correspondiente, no de la mera existencia de una spec.

## Evidencias de las novedades y del código presente

- A10 sin documento:
  [comentario final de Joseph](https://github.com/Taller-SW-Web/Modulo-de-Seguridad/issues/42#issuecomment-6118843852).
  [Matriz](matriz-scopes.md), [acuerdos](acuerdos.md) y
  [kit](../../specs/kit-integracion.md) actualizados localmente en este bloque.
- CSAT aceptado:
  [acuse humano #44](https://github.com/Taller-SW-Web/Modulo-de-Seguridad/issues/44#issuecomment-6118403027).
- Pendientes H2 de UX reportados resueltos:
  [#25, último comentario](https://github.com/Taller-SW-Web/Modulo-de-Seguridad/issues/25#issuecomment-5966236203);
  [guía](../diseno/sistema-diseno.md), [mockups](../mockups/README.md),
  [propuestas](../ux/propuestas.md), [wireframes](../wireframes/README.md).
- Infraestructura parcial:
  [#24, revisión](https://github.com/Taller-SW-Web/Modulo-de-Seguridad/issues/24#issuecomment-5969915384),
  [Compose](../../docker-compose.yml) y
  [V4](../../backend/src/main/resources/db/migration/V4__auditoria_y_integracion.sql).
- Credenciales:
  [V2](../../backend/src/main/resources/db/migration/V2__credenciales_otp_tokens.sql)
  y [PasswordHistorialRepositoryTest](../../backend/src/test/java/com/marketplace/auth/credencial/repository/PasswordHistorialRepositoryTest.java).
- Cuentas:
  [#22, revisión](https://github.com/Taller-SW-Web/Modulo-de-Seguridad/issues/22#issuecomment-5945902800),
  [V1](../../backend/src/main/resources/db/migration/V1__usuario_y_roles.sql)
  y [V100](../../backend/src/main/resources/db/migration/V100__datos_semilla.sql).
- [SPEC-03](../../specs/funcionales/SPEC-03-alta-consulta-cuentas.md),
  [FRONT-11](../../specs/front/FRONT-11-admin-alta-usuario.md) y
  [React FRONT-11](../../specs/componentes-react/FRONT-11-admin-alta-usuario.md)
  cubren solo las partes indicadas de #37/#36.

## Responsables y próximo uso

#9/#32/#36/#37 conservan asignaciones históricas incompatibles con el reparto
actual. Se recomienda actualizar su responsable a Eva cuando el equipo gestione
los issues; no repartir la nueva pantalla entre frontend/backend ajenos.
#22/#24 siguen siendo evidencia histórica H2: sus dependencias nuevas respetan
los dueños completos de las épicas actuales.

El [registro único](../registro-inconsistencias.md) gobierna inconsistencias.
Esta es una fotografía de evidencia y condiciones de cierre, no otro tablero.
[Preparación por integrante](../../plan/preparacion-por-integrante.md) identifica
planes/decisiones/dependencias que faltan antes de derivar tareas.
