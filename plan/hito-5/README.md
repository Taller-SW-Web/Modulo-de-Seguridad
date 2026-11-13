# Hito 5 — repartición por FEAT

**Corte:** 10-nov-2026. **Fecha límite:** lunes 23-nov-2026 (indicada por David). **Estado:** v1.0 aprobada por el PO (Sergio) el 10-nov-2026; ver [decisiones del PO](DECISIONES-PO.md).
Continúa el [plan H4 v1.2](../hito-4/README.md). Tareas detalladas en los [tableros H5](../../tareas/hito-5/README.md).

**Fuentes:** documentación `main` `37c6e`; backend `develop` `2d7f385`; frontend `develop` `7fc2dbe`; PR, issues y ejecuciones de GitHub Actions al 9-nov. La rúbrica de H5 y las observaciones del profesor las comunicó David; no hay otro documento oficial en el repositorio.

**Por confirmar** (no son decisiones aprobadas):
- Los pesos de cada criterio de la rúbrica y el formato de la presentación.
- Los dueños de las FEAT nuevas (13 a 20) y las parejas de revisión.
- El contrato de FEAT-14 y FEAT-16, que publica el PO.
- La plataforma de API Management.

---

## 1. Dónde estamos: relevo de H4

H4 se cerró con alcance ajustado ([FEAT-11](../../docs/evidencia/h4/FEAT-11.md) y plan H4 §3). Cada evidencia indica su nivel:

- **Propia desplegada:** recorrido hecho por alguien de G7 en el entorno desplegado.
- **CI:** pruebas automatizadas en GitHub Actions; no es el entorno desplegado.
- **Despliegue:** versión observada en Render o verificada por el script del pipeline.
- **Otro módulo:** resultado reportado por otro grupo, no inspeccionado por G7.

| Trabajo H4 | Cerrado en H4 | Evidencia y nivel | Pasa a H5 | Motivo |
|---|---|---|---|---|
| FEAT-07 MFA v2 | Activar, ingresar con código por correo y desactivar, con auditoría | Propia desplegada, CI y despliegue ([FEAT-07](../../docs/evidencia/h4/FEAT-07.md)) | Casos negativos en despliegue | No se ejecutaron; el SMS es simulado |
| FEAT-08 Bloqueo manual | Bloquear, rechazo de ingreso, desbloquear, correos y auditoría | Propia desplegada y CI ([FEAT-08](../../docs/evidencia/h4/FEAT-08.md)) | Revocación en nube, versión desplegada, contacto de soporte (H-14) | La revocación solo se probó en PostgreSQL local |
| FEAT-11 identidad con G1 | Registro, verificación, ingreso con y sin MFA, cierre y recuperación con retorno | Propia desplegada | — | — |
| FEAT-11 sesión y recuperación por API | Renovación, 202/204, rechazos | Otro módulo (G1) | Casos de sesión no acreditados | Reportado por G1, sin versión por solicitud |
| FEAT-11 cliente `modulo-marketplace` | Emisión de token HTTP 200 | Propia desplegada, solo emisión | Llamada real a G6 con 200 y 403 | Emitir el token no prueba que G6 lo valide |
| FEAT-11 favoritos A/B | 30/30 pruebas automatizadas de G1 | Otro módulo (G1); **no es prueba desplegada** | Prueba A/B en despliegue | G1 no desplegó su integración con G6 antes del hito |
| FEAT-01 Registro y verificación | Código fusionado; recorrido del 6-nov | Propia desplegada, en un PR de documentación sin fusionar | Fusionar la evidencia | Tablero desactualizado |
| FEAT-02 Baja y reactivación | Backend fusionado | CI | Pantalla y recorrido desplegado | La pantalla sigue en PR |
| FEAT-03 Recuperación | Recorrido con retorno por canal | Propia desplegada y otro módulo (G1); evidencia en los repos de código | — | — |
| FEAT-04 Direcciones, correo y edición admin | Backend y pantallas fusionados | CI; evidencia de T1/T2 en el repo backend | Evidencia desplegada de T3–T6; PR pendientes | Sin recorrido desplegado documentado |
| FEAT-05 Tokens de servicio | Emisión, introspección y catálogo | Propia desplegada (descubrimiento y 401) | T4: rotación de clave | No se ejecutó |
| FEAT-06 Consulta de identidad | Consulta individual, por lote y direcciones | Propia desplegada (solo 401) | T4: 200 con scope | No se probó el camino positivo |
| FEAT-09 CI/CD | T1 despliegue, T2 smoke, T5 mensajería real | CI y despliegue (evidencia en el repo backend) | T3: despliegue del frontend | Bloqueada por permisos |
| FEAT-10 Auditoría admin | — | Ninguna | Toda la FEAT | No se inició (P2) |
| FEAT-12 E2E | T1–T3; T4 con 6/6 en entorno aislado | CI en entorno aislado | T4 contra Render; T5 con G5 | Alcance ajustado |
| G6 validación JWT/JWKS | Prueba local con 200/401/403 | Otro módulo (G6), local, sin registro en los repos | Homologación desplegada | No es una prueba desplegada ni de G7 |

**Ramas y despliegue al 9-nov.** `main` del backend y del frontend siguen en la versión H3; todo H4 está en `develop`. La versión desplegada no se deduce de `develop`: se anota la que muestre Render en cada evidencia.

**Inconsistencias que H5 debe corregir:** tablero H4 de Jose Luis sin filas T1–T4; tableros de Eva, Valery y Christian en «Pendiente» con trabajo fusionado; FEAT-05-T4 como «Hecho» sin rotación; la matriz de scopes no refleja los clientes ya usados; el alta administrativa figura como implementada pero no hay alta en el backend ni pantalla FRONT-11.

## 2. Rúbrica del Hito 5 → quién la cubre

Rúbrica comunicada por David (25 % de la nota).

| Criterio | Cómo lo cubrimos | Situación al 9-nov | FEAT y dueño |
|---|---|---|---|
| CI/CD incluyendo revisión de seguridad | Análisis de código, dependencias, secretos e imagen en el pipeline; despliegue del frontend; E2E contra Render | El pipeline no tiene análisis de seguridad; el frontend no despliega desde CI | FEAT-15 Juan José · FEAT-09-T3 Christian · FEAT-12-T4 Jose Luis |
| Evidencia de pruebas de integración | Pruebas conjuntas desplegadas con G5, G1 y por canal de compra | Identidad con G1; G5 solo por API en H3 | FEAT-12-T5 Jose Luis · FEAT-20 y FEAT-11 David |
| Evidencia de pruebas de performance | Carga en la nube de ingreso, renovación, token de servicio e introspección | Solo carga local de H3 | FEAT-17 Valery |
| Uso de API Management | Publicar, asegurar, limitar y monitorear la API de G7 en una plataforma de gestión de API | Nada implementado | FEAT-19 Sergio |
| Mecanismo asincrónico entre microservicios | Un módulo consumidor real de los eventos `seguridad.usuarios` | Se publica el evento, pero ninguna cola está suscrita | FEAT-18 Christian |

**Observaciones del profesor al presentar H4** (comunicadas por David):

| Observación | Respuesta | FEAT |
|---|---|---|
| Falta un módulo de configuración para crear clientes técnicos y sus secretos | Pantalla y API de gestión de clientes técnicos, dentro del backend y la SPA actuales | FEAT-14 David |
| No se ve un superadministrador que dé roles de gestor | El rol existe (`ADMIN_SISTEMA`, permiso `rol.asignar`). Falta el alta administrativa de un trabajador con su rol | FEAT-13 Eva |
| Los roles se ven en el token | Formato cifrado opcional del token de acceso | FEAT-16 David |

Decisiones de diseño de esta propuesta:
- **El módulo de configuración no es un microservicio aparte.** Las tablas de clientes ya existen en el backend; un servicio separado exigiría otro despliegue y acceso a tablas con dueño. El criterio «entre microservicios» se cubre con los demás módulos.
- **No se crea un rol nuevo.** El catálogo cerrado de seis roles es contrato con los otros módulos.
- **El token actual no cambia para quien ya integró.** El formato cifrado se añade como opción; la SPA de G7 lo usa. En los flujos de otros módulos el formato actual sigue vigente hasta que migren.

## 3. Reparto por integrante

Las FEAT 02 a 12 conservan su dueño. Las FEAT 13 a 20 son nuevas. David pidió asumir lo más difícil y lo que más bloquea, para que el resto avance sin esperas.

| Integrante | FEAT en H5 | Prioridad |
|---|---|---|
| Eva | FEAT-13 alta administrativa v2 · FEAT-02 cierre | P1 · P2 |
| Jose Luis | FEAT-12 (T4 E2E contra Render, T5 G5) | P1 |
| Juan José | FEAT-15 revisión de seguridad en el pipeline | P1 |
| Luis David | Desbloqueos (OPS) · FEAT-16 token cifrado · FEAT-14 módulo de configuración · FEAT-20 compra por canal · FEAT-11 pendientes · FEAT-07 y FEAT-08 cierre | P1 · P1 · P1 · P1 · P1 · P2 |
| Sergio | PO (lote de contrato) · FEAT-19 API Management · FEAT-05-T4 · FEAT-06-T4 | P1 · P1 · P2 · P2 |
| Christian | FEAT-09-T3 despliegue del frontend · FEAT-18 mecanismo asincrónico · FEAT-10 | P1 · P1 · P2 |
| Valery | FEAT-17 pruebas de rendimiento · FEAT-04 cierre | P1 · P2 |

**P1** cubre la rúbrica y las observaciones del profesor. **P2** es deuda de H4 que no bloquea la rúbrica.

**Riesgo conocido:** David concentra dos FEAT de código grandes y la coordinación externa. Respaldo: Sergio, que revisa ambas por ser dueño del paquete de identidad.

## 4. Calendario

| Fecha | Hito interno |
|---|---|
| mar 10 y mié 11-nov | Desbloqueos de David (OPS-T1 a T3); mensajes a G1, G2, G5 y G6; propuestas de contrato; todos abren su rama |
| jue 12-nov | Lote de contrato H5 publicado (PO-T1); plataforma de API Management elegida |
| dom 15-nov | Fin de S1: backend y pantallas de cada FEAT en PR, con CI en verde |
| lun 16 a vie 20-nov | S2: recorridos desplegados, pruebas conjuntas y evidencia |
| vie 20-nov | Congelamiento propuesto |
| sáb 21 y dom 22-nov | Ensayo y correcciones |
| lun 23-nov | Fecha límite |

## 5. Dependencias

**Regla:** cada FEAT P1 la termina su dueño con trabajo propio. Las entregas internas que otra persona espera se hacen el 10, 11 y 12-nov, y nadie espera a más de una persona.

| Necesita | De | Entrega | Para |
|---|---|---|---|
| Resultado de las consultas de solo lectura de Supabase | David OPS-T1 | mar 10-nov | Sergio PO-T2 · David FEAT-20 |
| Cuentas de prueba y cliente técnico de prueba | David OPS-T2 | mié 11-nov | Jose Luis FEAT-12-T4 · Valery FEAT-17 · Sergio FEAT-06-T4 |
| Ajustes de repositorio y Render (enlace de despliegue del frontend, variable del E2E, análisis de código) | David OPS-T3 | mié 11-nov | Christian FEAT-09-T3 · Jose Luis FEAT-12-T4 · Juan José FEAT-15 |
| Lote de contrato H5 | Sergio PO-T1 | jue 12-nov | David FEAT-14 y FEAT-16 (se trabaja contra la propuesta, sin esperar) |

**Dependencias externas.** Ninguna bloquea el cierre de una FEAT: lo propio se verifica y el caso externo se registra como «no probado», con su motivo.

| Depende de | Qué | Para |
|---|---|---|
| G5 | Cómo recibe `api-ventas` la identidad del comprador; versión desplegada | FEAT-20 · FEAT-12-T5 |
| G1 | Integración con G6 desplegada | FEAT-11-T6 y T7 |
| G2 | Consumidor de eventos; cuentas y permisos del issue #80 | FEAT-18 |
| G6 | Registro de su prueba JWT/JWKS; grants del issue #78 | PO-T2 |
| G3 y G4 | Compra por canal | FEAT-20 |

Detalle de clientes técnicos, consultas y grants: [verificación de clientes técnicos H5](../../docs/integracion/hito-5-clientes-tecnicos.md).

## 6. Cómo trabajamos

- **Una FEAT = una rama `feat/…` = un PR por repositorio hacia `develop`.** Nada se fusiona a `main` en los repositorios de código. Las tareas son commits `FEAT-xx-Tn: …`.
- **Documentación:** los PR van a `main`, como en H4.
- **Revisión cruzada obligatoria:** ningún PR lo fusiona su autor. Parejas propuestas: Eva ↔ Valery; Jose Luis ↔ Christian; los PR de David los revisa Sergio, los de Sergio los revisa Juan José y los de Juan José los revisa David.
- **El contrato lo edita solo Sergio.** Los dueños proponen en `specs/contrato-api/propuestas/` y trabajan contra la propuesta.
- **Supabase y Render los opera David**, con SQL y pasos preparados. Nadie publica secretos, tokens ni códigos.
- **Estados:** Pendiente → En curso → En PR → Fusionada → Desplegada → Verificada. Solo «Verificada» acredita.

## 7. Definición de terminado de una FEAT

- [ ] Endpoints del contrato implementados, con errores `problem+json` y sin enumeración de cuentas.
- [ ] Pantallas de la spec conectadas al backend real.
- [ ] Pruebas unitarias, PostgreSQL y de arquitectura en verde en el CI.
- [ ] PR revisado por otra persona.
- [ ] Recorrido probado en el despliegue, con evidencia sanitizada en `docs/evidencia/h5/`.
- [ ] La evidencia indica su nivel (propia desplegada, CI, despliegue u otro módulo) y la versión desplegada observada.
- [ ] Tablero actualizado y contrato publicado si cambió.

## 8. Decisiones pendientes

| Tema | Situación | Quién |
|---|---|---|
| Forma exacta del formato cifrado del token (FEAT-16) | **Decidido 10-nov:** ratificado ADR-009 y contrato v1 1.2.0 | Sergio |
| Permiso y rutas del módulo de configuración (FEAT-14) | **Decidido 10-nov:** OpenAPI v2 2.1.0 y permiso `integracion.gestionar` | Sergio |
| Alcance de SPEC-03 para FEAT-13 | **Decidido 10-nov:** alta y activación entran; registro en tienda, búsqueda por documento y eliminación a 30 días no | Sergio con Eva |
| Plataforma de API Management | **Decidido 10-nov:** Azure API Management, plan Consumption ([ADR-010](../../docs/arquitectura/adr/010-plataforma-api-management.md)) | Sergio |
| G3 y G4 en la compra por canal | **Decidido 10-nov:** G1 obligatorio; G3 y G4 entran si confirman hasta el vie 13-nov | Sergio y David |
| Consumidor de eventos si G2 no llega a tiempo | **Decidido 10-nov:** G2 hasta el vie 13-nov; después G5; último recurso, consumidor propio de demostración | Christian y Sergio |
| Persistencia de la sesión al recargar | **Decidido 10-nov:** sin cambios en H5 | Sergio |
| Términos de servicio (H-12) y contacto de soporte (H-14) | **Decidido 10-nov:** términos `2026-11-v1`; soporte por el buzón remitente, que David debe comprobar | Sergio |
