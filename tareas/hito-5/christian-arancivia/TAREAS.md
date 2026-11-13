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
| FEAT-09-T3 | Frontend desplegado desde el pipeline | P1 | David OPS-T3 (mié 11-nov) | Pendiente |
| FEAT-18-T1 | Consumidor real de eventos acordado | P1 | G2 (externo) | Pendiente |
| FEAT-18-T2 | Acceso del consumidor al broker | P1 | T1 | Pendiente |
| FEAT-18-T3 | Prueba conjunta del evento | P1 | T2 | Pendiente |
| FEAT-10 | Auditoría administrativa y FRONT-14 | P2 | — | Pendiente |

## FEAT-09-T3 — Despliegue del frontend (P1)
- **Resultado:** job que despliega la SPA al fusionar en `develop` y comprueba la versión publicada, como ya hace el backend.
- **Primer paso:** pedir a David el enlace de despliegue y el secreto del repositorio.
- **Aceptación:** una ejecución de `develop` que despliega la SPA y verifica su versión.
- **Evidencia:** `docs/evidencia/h5/FEAT-09.md`. Nivel: CI y despliegue.

## FEAT-18 — Mecanismo asincrónico con un consumidor real (P1)

### FEAT-18-T1 — Acuerdo
- **Resultado:** módulo consumidor, cola y eventos acordados. El catálogo registra que Despacho consume los eventos de baja, bloqueo, reactivación y desbloqueo.
- **Primer paso:** mensaje a G2 el mar 10-nov.
- **Aceptación:** acuerdo registrado. Si G2 no puede, se decide la alternativa con Sergio.

### FEAT-18-T2 — Acceso al broker
- **Resultado:** el consumidor declara su cola con credenciales propias, entregadas por canal privado.
- **Aceptación:** la cola aparece enlazada al exchange `seguridad.usuarios`.

### FEAT-18-T3 — Prueba conjunta
- **Resultado:** una acción en G7, por ejemplo bloquear una cuenta de prueba, llega como evento al otro módulo.
- **Aceptación:** el mismo identificador de evento aparece publicado en G7 y recibido en el consumidor; un mensaje repetido no duplica el efecto.
- **Evidencia:** `docs/evidencia/h5/FEAT-18.md`, con versiones de ambos lados y nivel de evidencia.

## FEAT-10 — Auditoría administrativa (P2)
- **Resultado y aceptación:** los del [tablero H4](../../hito-4/christian-arancivia/TAREAS.md).
- **Evidencia:** `docs/evidencia/h5/FEAT-10.md`.

También: actualizar el tablero H4, que sigue en «Pendiente» con FEAT-09 verificada.
