# H5 — tareas de Christian Gabriel Arancivia Salas

**Épicas:** TEC-INFRA y EP-06 Auditoría.
**Plan:** [H5 §3](../../../plan/hito-5/README.md).
**Ramas:** `feat/TEC-INFRA-cd-frontend` (frontend), `feat/TEC-INFRA-eventos-consumidor` y `feat/EP-06-auditoria-admin`.
Cada tarea es un commit `FEAT-xx-Tn: …` en su rama. Revisión cruzada con Jose Luis.

**Situación de partida:** el backend despliega desde el pipeline y la publicación de eventos está activa en el despliegue. Faltan el despliegue del frontend desde el pipeline y un consumidor real de los eventos: hoy `usuario.creado` se publica y se descarta porque ninguna cola está suscrita.

**Fuentes comunes:** [catálogo de eventos](../../../specs/catalogo-eventos.md),
[SPEC-13](../../../specs/funcionales/SPEC-13-consulta-auditoria.md),
[FRONT-14](../../../specs/front/FRONT-14-admin-auditoria.md),
[tablero H4](../../hito-4/christian-arancivia/TAREAS.md),
evidencia H4 de FEAT-09 en el repo backend (`docs/evidencia/h4/FEAT-09.md`).

| ID | Resultado | Prioridad | Depende de | Estado |
|---|---|---|---|---|
| FEAT-09-T3 | Frontend desplegado desde el pipeline | P1 | OPS-T3 (hecho 12-nov) | Bloqueada |
| FEAT-18-T1 | Consumidor real de eventos acordado | P1 | — | Verificada |
| FEAT-18-T2 | Acceso del consumidor al broker | P1 | T1 | Verificada |
| FEAT-18-T3 | Prueba conjunta del evento | P1 | T2 | Verificada |
| FEAT-10 | Auditoría administrativa y FRONT-14 | P2 | — | Pendiente |

## Estado al 26-nov-2026

- **FEAT-18-T1 — verificada.** El acuerdo se cerró con **G1 Marketplace**, no con
  G2/Despacho: G1 declara su cola `marketplace.usuarios` y la enlaza a
  `seguridad.usuarios` con `usuario.*`. El catálogo de eventos todavía atribuye el
  consumo a Despacho y no registra a Marketplace; esa fila la actualiza el PO.
- **FEAT-18-T2 — verificada.** La cola de G1 quedó enlazada al exchange
  `seguridad.usuarios`. Por el límite del plan gratuito de CloudAMQP, G7 y G1
  compartieron la credencial del broker de pruebas; G1 la rotó al terminar y G7 dejó
  de usarla.
- **FEAT-18-T3 — verificada (publicación y recepción).** Se publicó `usuario.creado`
  desde una instancia local de G7 y G1 confirmó el mismo `eventoId` en su cola.
  Quedan como pendientes declarados el procesamiento por el consumidor de G1, la
  deduplicación y la prueba de reintentos/DLQ. Evidencia:
  [`docs/evidencia/h5/FEAT-18.md`](https://github.com/Taller-SW-Web/Modulo-de-Seguridad-Backend/blob/develop/docs/evidencia/h5/FEAT-18.md).
- **FEAT-09-T3 — bloqueada.** OPS-T3 quedó cerrado el 12-nov y dejó el secreto
  `RENDER_DEPLOY_HOOK_FRONTEND` en el repositorio frontend. Falta permiso de escritura
  en ese repositorio y añadir el job de deploy con `VITE_API_BASE_URL` por entorno; su
  CI hoy termina en `npm run build`.
- **FEAT-10 — pendiente (P2).** Sin implementar: no existen `/api/v1/auditoria` ni
  `/auditoria/exportar` en `develop`.

## FEAT-09-T3 — Despliegue del frontend (P1)
- **Resultado:** job que despliega la SPA al fusionar en `develop` y comprueba la versión publicada, como ya hace el backend.
- **Primer paso:** OPS-T3 entregó el secreto el 12-nov; falta el permiso de escritura
  en el repo frontend y el job de deploy.
- **Aceptación:** una ejecución de `develop` que despliega la SPA y verifica su versión.
- **Evidencia:** `docs/evidencia/h5/FEAT-09.md`. Nivel: CI y despliegue.

## FEAT-18 — Mecanismo asincrónico con un consumidor real (P1)

### FEAT-18-T1 — Acuerdo
- **Resultado:** módulo consumidor, cola y eventos acordados. El acuerdo se cerró con
  G1 Marketplace (cola `marketplace.usuarios`, binding `usuario.*`); el catálogo de
  eventos debe reflejarlo.
- **Primer paso:** mensaje a G2 el mar 10-nov; al no concretarse, se acordó con G1.
- **Aceptación:** acuerdo registrado. Si G2 no puede, se decide la alternativa con Sergio.

### FEAT-18-T2 — Acceso al broker
- **Resultado:** el consumidor declara su cola con credenciales propias, entregadas por canal privado.
- **Aceptación:** la cola aparece enlazada al exchange `seguridad.usuarios`.

### FEAT-18-T3 — Prueba conjunta
- **Resultado:** una acción en G7, por ejemplo bloquear una cuenta de prueba, llega como evento al otro módulo.
- **Aceptación:** el mismo identificador de evento aparece publicado en G7 y recibido en el consumidor; un mensaje repetido no duplica el efecto.
- **Estado:** publicación y recepción verificadas con el mismo `eventoId`. Quedan
  pendientes el procesamiento por el consumidor, la deduplicación y los reintentos/DLQ.
- **Evidencia:** `docs/evidencia/h5/FEAT-18.md`, con versiones de ambos lados y nivel de evidencia.

## FEAT-10 — Auditoría administrativa (P2)
- **Resultado y aceptación:** los del [tablero H4](../../hito-4/christian-arancivia/TAREAS.md).
- **Evidencia:** `docs/evidencia/h5/FEAT-10.md`.

El tablero H4 ya quedó actualizado (PR de documentación #115): FEAT-09-T6 verificada.
