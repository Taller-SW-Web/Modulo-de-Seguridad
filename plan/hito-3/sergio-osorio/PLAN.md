# H3 — Sergio Alejandro Osorio Montenegro

**Ejecución:** [tablero de tareas H3](../../../tareas/hito-3/sergio-osorio/TAREAS.md).
Una tarea lista por vez; distinguir EP-05 de TEC-IDENTIDAD sin duplicar tableros.

## Ubicación y piezas del backend nuevo

Aplica [BACKEND-HEXAGONAL 1.1.0](../../../plan/hito-3/BACKEND-HEXAGONAL.md).
Backend: `Modulo-de-Seguridad-Backend`, clon actual `Backend-auth-service/`;
Java 21/Boot 3.5.16/Gradle 8.14.3, paquete `com.marketplace.auth_service`.
Rutas Java relativas a la raíz de ese repo; rutas React `src/...` relativas al
repo frontend independiente. Documentación y contrato permanecen en este repo.
No ejecutar ni reutilizar como base el antiguo `backend/` Maven.

| Pieza propia | Ubicación / resultado |
|---|---|
| Capacidades | `autorizacion e identidad` bajo el paquete raíz |
| Dominio | `domain.model`: Rol / Permiso / UsuarioRol, sin JPA/Spring/HTTP |
| Casos de uso y DTO | `application.service` + `application.dto`: AsignarRol / RevocarRol / FirmarAcceso; commands/resultados inmutables |
| Puertos de entrada | `application.port.in`: AutorizacionPort, FirmaPort; firmas de composición conservadas |
| Puertos de salida | `application.port.out`: persistencia propia y necesidades de proveedores |
| Persistencia | `adapter.out.persistence`: entidades `...JpaEntity`, repositorios `...JpaRepository` y mappers manuales `...PersistenceMapper` |
| HTTP | `adapter.in.web`: controller, Request/Response y mapper `...WebMapper`, según OpenAPI |
| Transacciones/composición | `adapter.in.transaction`: decorador del caso de uso; `adapter.out.capacidad`: puente a puertos exportados del otro dueño |

Estas piezas forman parte de **EP-05-T001/T002; TEC-IDENTIDAD-T001** y de las tareas de endpoints ya
existentes; no son otra funcionalidad ni duplican IDs. Cada tarea de endpoint
entrega caso de uso, DTO/mapping HTTP y adaptadores necesarios, además de su UI.
Los tests de mappers excluyen secretos y distinguen omitido/null; los tests del
núcleo usan puertos dobles, sin Spring/DB. La aceptación usa PostgreSQL y
proveedores reales. Reglas ArchUnit de TEC-INFRA-T001 verifican los límites.
El esqueleto compartido se prepara en TEC-INFRA-T001; puede adelantarse el
núcleo aislado, pero no cerrar persistencia/HTTP sin las dependencias reales.

## 1. Metadatos y entradas

| Campo | Valor |
|---|---|
| Dueño completo | Sergio Alejandro Osorio Montenegro |
| Épica académica | EP-05 — Roles y permisos; SPEC-11 |
| Capacidad adicional | Identidad técnica SPEC-17/18; no es otra épica |
| Incremento / versión | H3-SERGIO / 1.1.0 / 12-oct-2026 |
| Estado | Aprobado H3; diseño cerrado para tareas; implementación/evidencia pendientes |
| Revisión | Aprobación formal del dueño comunicada por Luis el 12-oct-2026; detalles técnicos delegados |
| Referencia | main 5796622f7c039021e03dd25b2338c6a208320e05 + complemento local; v1 1.1.0 |

| Entrada | Fuentes |
|---|---|
| Funcional | [EP-05](../../../specs/epicas/EP-05-roles-permisos.md), [SPEC-11](../../../specs/funcionales/SPEC-11-roles-permisos.md), [SPEC-17](../../../specs/funcionales/SPEC-17-tokens-servicio.md), [SPEC-18](../../../specs/funcionales/SPEC-18-consulta-identidad.md) |
| UI/UX | Bloque roles de [FRONT-08](../../../specs/front/FRONT-08-admin-detalle-usuario.md), permisos de [FRONT-00](../../../specs/front/FRONT-00-comportamiento-comun.md)/[07](../../../specs/front/FRONT-07-admin-listado-usuarios.md); [diseño](../../../docs/diseno/sistema-diseno.md) |
| API | [OpenAPI v1](../../../specs/contrato-api/openapi.yaml): POST /usuarios/{id}/roles, DELETE /usuarios/{id}/roles/{rol}, descubrimiento/JWKS y perfiles JWT; [errores](../../../specs/catalogo-errores.md) |
| React | SeccionRoles de [FRONT-08 React](../../../specs/componentes-react/FRONT-08-admin-detalle-usuario.md) y permisos de [base](../../../specs/componentes-react/FRONT-00-base-compartida.md); identidad técnica sin pantalla propia |

Diseño adicional en [TEC-IDENTIDAD](../../transversales/TEC-IDENTIDAD.md),
[ADR-001](../../../docs/arquitectura/adr/001-rs256-frente-a-hs256.md),
[ADR-002](../../../docs/arquitectura/adr/002-validacion-local-frente-a-introspeccion.md)
e [interfaces H3](../ACUERDOS-INTERNOS.md).

**Diseño común cerrado:** [interfaces/locks](../ACUERDOS-INTERNOS.md),
[propiedad y mappings](../DISENO-PERSISTENCIA.md) y [entorno/React](../ENTORNO-Y-REACT.md).
Concretan las decisiones internas de este incremento sin cambiar sus RF/UI ni
OpenAPI. No queda otra revisión técnica rutinaria previa a derivar tareas;
las dependencias se implementan y comprueban en su dueño. [Salida a tareas](../ANTES-DE-CODIFICAR.md).

## 2. Objetivo y límites

**Recorrido académico:** ADMIN_SISTEMA que completó MFA abre detalle de cuenta,
asigna un rol y después lo revoca, de uno en uno. Persisten asociaciones,
se revocan los refrescos del objetivo, se audita y publica el hecho. Al
autenticarse de nuevo el objetivo recibe roles/permisos efectivos actuales.
Un actor sin permiso no puede mutar, ni se quita el último administrador.

Sergio implementa SeccionRoles, sus controladores/servicios/datos y pruebas.
Eva entrega el contenedor/detalle; Jose Luis la revocación; Luis MFA efectivo;
Christian auditoría/transporte. Firma/JWKS por sí solos no acreditan EP-05.

**Adicional H3:** firmador y validación de acceso, issuer, descubrimiento y JWKS
reales para Jose Luis y Ventas. Sin pantalla nueva para las máquinas.

**Después:** rotación completa de claves, clientes/scopes de servicio,
introspección y consultas de servicio SPEC-18. A10/A9 conservan condiciones,
incluidos retiros CSAT/documento para Ventas; no provisionarlos para /pedidos/me.
Los permisos de Seguridad siguen siendo exclusivos del catálogo G7.

## 3. Cobertura y criterios

| Fuente | Operación/capacidad | Resultado H3 |
|---|---|---|
| RF-11.1/11.2/11.3; ESC-11.1/11.2/11.3/11.6; UI-08.3 | Asignar/revocar de uno en uno | 200 asignación/204 revocación; rol inválido 400, actor sin permiso 403 |
| RF-11.4/11.9; ESC-11.4/11.7 | Cálculo de permisos | Unión sin duplicados; solo G7; admin+vendedor tiene diez permisos |
| RF-11.5/11.6/11.8 | Mutación y efectos | Revocación real de refrescos, auditoría crítica y evento transaccional |
| RF-11.7; ESC-11.5; UI-08.4 | Protección de administrador | No quitar propio ADMIN_SISTEMA ni último activo; 422 sin cambios |
| RF-11.10 | Autorización transversal | Acciones propias por sub; campos privilegiados necesitan atributos.editar |
| RF-10.1; RF-09.8 | Cualquier rol de gestión | MFA efectivo obligatorio al siguiente login, aunque bandera false |
| RF-17.1/17.2/17.14; RF-05.6 | Firma, descubrimiento y JWKS | RS256/kid, sub UUID, tipo acceso, iss exacto, 900 s; validable en G5 |

## 4. Decisiones técnicas

### Roles/permisos y transacción

V1 y V100 definen rol/permiso/rol_permiso/usuario_rol. Revisar mapping real
y consultas: no existe implementación completa por tener las migraciones.
Códigos cerrados y etiquetas canónicas; no roles REPARTIDOR ni servicio.
No conceder un scope de servicio como permiso de usuario.

Mutar una asociación por petición, sin reemplazar array completo. Restricción
única evita duplicados; DTO no concede roles fuera del catálogo. Autorizar
antes de buscar objetivo. Para proteger último admin, lock del catálogo fijo
ADMIN_SISTEMA y cuenta objetivo según orden común cerrado; contar estado efectivo
ACTIVO y evitar carrera de dos revocaciones simultáneas.

Mutación + revocación de familias del objetivo + auditoría crítica +
outbox de usuario.roles_cambiados se confirman juntos. Payload contiene la
lista resultante completa, no una contraseña ni token. Si no se puede auditar,
revertir mutación y revocación. Evento se publica después del commit.

No afirmar que JWT antiguos desaparezcan inmediatamente de G5 por revocar
refrescos. El nuevo login calcula roles desde DB; el backend G7 conserva
controles de las operaciones según sus fuentes. Revalidar roles para decidir MFA.

### Interfaz

SeccionRoles muestra catálogo cerrado con etiquetas de FRONT-00, asignados
y disponibles; llamadas de mutación y recarga de detalle. Sergio entrega
componente/hooks de su bloque y lo integra con Eva, no solo un endpoint.

No llamar GET /roles o /permisos con client_secret desde React: esos endpoints
de servicio son posteriores. El catálogo de UI procede de la constante
canónica revisada, y backend valida de nuevo. Ocultar «Revocar» no sustituye
la protección del servidor.

### Firma e identidad técnica

Consumir [TEC-IDENTIDAD](../../transversales/TEC-IDENTIDAD.md) para claves
persistentes del entorno, serialización de claims y verificación de acceso.
Sergio recibe claims calculados, no autentica contraseña ni abre familias.
RS256, kid público, issuer idéntico a descubrimiento; no valor auth-service.

UUID en sub, roles como array incluso vacío, permisos G7, tipo acceso e
iat/exp/jti conformes al contrato; no imponer aud de servicio a persona.
Distinguir token servicio/refresco del acceso. Revisar dependencia JOSE real
del Gradle nuevo bajo TEC-04; el kit en otro runtime no prueba el backend.

## 5. Dependencias y condiciones de inicio

| Dependencia | Dueño | Antes de |
|---|---|---|
| Cuenta/detalle y contenedor | Eva | SeccionRoles con objetivos reales |
| Login/MFA vigente de administrador | Jose Luis/Luis | Mutación end-to-end sin bypass de gestión |
| Revocación serializada de familias | Jose Luis | Cambios transaccionales y nuevas autenticaciones |
| Auditoría/outbox/broker | Christian; Sergio productor | Efectos completos de rol |
| RSA/issuer/kid y runtime | Sergio/Christian/Jose Luis | Firma y prueba en G5, persistencia tras reinicio |
| Configuración G5/aviso | Sergio con Michael/Joseph | INT-05 y publicación/adopción de la revisión |

Interfaz y locks están cerrados para H3; cálculo de permisos y componente
pueden probarse aislados. La firma real no requiere secretos A10 ni concluir
SPEC-18. No inventar confirmación de Productos para A9.

## 6. Fases y entregables

| Fase | Entrega de Sergio | Verificación |
|---|---|---|
| Diseño | Catálogo/mapping/autorización, locks y firma | Fuentes y consumidores de capacidades acordados |
| Capacidad académica | Mutación y cálculo de permisos con efectos | DB real, protección admin, revocación/auditoría/evento |
| Bloque UI | SeccionRoles integrado en detalle de Eva | Asignar/revocar y denegaciones desde pantalla |
| Identidad adicional | Firmador/verificador y descubrimiento/JWKS | Token real, reinicio y validador G5 |
| Evidencia | Defensa de roles + evidencia técnica separada | No confundir entrega adicional con su épica |

## 7. Pruebas y terminado

Asignar/revocar, rol inválido, actor CLIENTE/ADMIN_VENTAS sin rol.asignar,
propio/último ADMIN protegido y dos revocaciones concurrentes. Unión de
roles/permisos sin duplicados, atributos propios sin privilegios extra.

Dos sesiones del objetivo dejan de renovar tras cambio; nuevo acceso contiene
lista actual. Asignar gestión exige desafío/MFA en nuevo login. Fallo de
auditoría crítica conserva asociación y sesiones anteriores. Evento real con
payload resultante y eventoId.

Firma/validación: válido, vencido, issuer incorrecto, firma alterada y token
servicio como usuario; jwks_uri correcto y solo material público. Probar en
Java 21 real y G5; no volver a presentar fixtures del kit como login integrado.

Terminado académico: bloque UI→HTTP→DB→respuesta y efectos probados/revisados.
Terminado técnico H3: G5 verifica acceso real mediante las claves del entorno.
SPEC-17/18 y rotación siguen abiertos. Cálculo de permisos objetivo <15 ms y
JWT <4 KB; registrar medición real, no asumir por diseño.

## 8. Riesgos y recuperación

Claves no se regeneran en cada arranque. Si JWKS/issuer/firma fallan, denegar.
No publicar privada, client_secret o JWT completos. Restaurar pruebas de roles
en cuentas dedicadas, conservando A/B y un administrador de prueba operativo.

Concurrencia que permite quitar los dos últimos administradores impide cerrar
la capacidad; no sustituir el lock por contar primero sin transacción.
El aviso/adopción contractual y A9/A10 se mantienen pendientes independientes.
