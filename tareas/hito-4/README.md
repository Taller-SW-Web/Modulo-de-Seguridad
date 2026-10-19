# Tareas del Hito 4

Se derivan del [plan H4 v1.1](../../plan/hito-4/README.md) (19-oct-2026). Presentación:
**sábado 7-nov**; congelamiento: **jueves 5-nov**.

**Regla de entrega:** una FEAT = una rama = un PR por repo. Las tareas de este tablero
son **commits dentro de la rama de su FEAT**, no ramas ni PR propios (ver §6 del plan).
Un tablero detallado no obliga a abrir un PR por tarea.

## Tableros

| Integrante | Tablero | FEAT | Prioridad |
|---|---|---|---|
| Eva Lucía Moreno Zevallos | [TAREAS](eva-moreno/TAREAS.md) | FEAT-01 registro y verificación · FEAT-02 baja y reactivación | P1 · P2 |
| Jose Luis Limachi Sarmiento | [TAREAS](jose-limachi/TAREAS.md) | FEAT-12 seguridad de punta a punta (E2E, G5) | P1 |
| Juan José Cano Vasquez | [TAREAS](juan-cano/TAREAS.md) | FEAT-03 recuperación de contraseña | P1 |
| Luis David Morales Brenis | [TAREAS](luis-morales/TAREAS.md) | FEAT-11 integración G1 · FEAT-07 MFA v2 · FEAT-08 bloqueo manual | P1 · P2 · P2 |
| Sergio Alejandro Osorio Montenegro | [TAREAS](sergio-osorio/TAREAS.md) | FEAT-05 tokens de servicio · FEAT-06 consulta de identidad · PO | P1 |
| Christian Gabriel Arancivia Salas | [TAREAS](christian-arancivia/TAREAS.md) | FEAT-09 CI/CD · FEAT-10 auditoría admin | P1 · P2 |
| Valery Cristin Gutierrez Bendezu | [TAREAS](valery-gutierrez/TAREAS.md) | FEAT-04 direcciones y cambio de correo | P1 |

## Orden de trabajo y dependencias

```text
S1 (20–26 oct)
  Juan José FEAT-03-T1 (tokens de un uso, mié 22) ──► Eva FEAT-01-T2 · Valery FEAT-04-T3
  David FEAT-11-T1/T2 (prueba temprana G1, sáb 25)  ◄── despliegue actual de develop
  Christian FEAT-09-T1/T2 (deploy + smoke en el pipeline) ──► Jose Luis FEAT-12-T4
  Sergio FEAT-05-T1/T2 (clientes, scopes, /auth/token)
  Valery FEAT-04-T1/T2 (direcciones + puerto) ──► Sergio FEAT-06-T3
S2 (27 oct–2 nov)
  Pantallas, pruebas PostgreSQL y E2E de cada FEAT; Christian aplica V108+
  Eva FEAT-01-T3 (verificación) ──► Valery FEAT-04-T3 (cambio de correo)
S3 (3–7 nov)
  jue 5: PR de versión develop → main · despliegue · prueba conjunta G1 y G5 · ensayo
```

| Necesita | De | Entrega | Para |
|---|---|---|---|
| Propósitos `VERIFICAR_CORREO`, `CAMBIO_CORREO`, `RECUPERACION` | Juan José FEAT-03-T1 | mié 22-oct | Eva FEAT-01, Valery FEAT-04 |
| Puerto de lectura de direcciones | Valery FEAT-04-T2 | dom 26-oct | Sergio FEAT-06-T3 |
| Verificación de correo reutilizable | Eva FEAT-01-T3 | jue 30-oct | Valery FEAT-04-T3 |
| Job de despliegue y smoke test | Christian FEAT-09-T1/T2 | dom 26-oct | Jose Luis FEAT-12-T4 |
| Migraciones V108+ aplicadas | Christian FEAT-09-T4 | según cada FEAT | Todas las FEAT con datos |
| Cambios del OpenAPI | Sergio (PO) | antes de fusionar cada FEAT | Todos |
| Cliente técnico y token de servicio | Sergio FEAT-05-T2 | dom 26-oct | David FEAT-11-T4/T5 |

## Formato de cada tarea

Cada tarea indica **fuentes**, **resultado** (backend y frontend cuando corresponde),
**depende de**, **aceptación** y **evidencia**. Una tarea con pantalla no se cierra
solo con la API, y una FEAT cuenta para el 75 % solo con pantalla, API, despliegue y
evidencia (plan §1).

Estados: **Pendiente → En curso → En PR (borrador) → Fusionada → Desplegada**.
**Bloqueada** solo si falta una entrega de otro; se anota cuál y se sigue con otra tarea.

## Cómo pedir ayuda a la IA

Sustituir nombre, tablero e ID; una tarea por pedido:

> Soy [nombre]. Lee AGENTS.md, el plan H4 y mi TAREAS.md de `tareas/hito-4`.
> Estoy en la rama `feat/...` de mi FEAT. Implementa únicamente [FEAT-xx-Tn],
> consultando sus fuentes. Respeta los ports y las tablas de cada dueño. Ejecuta las
> comprobaciones indicadas y deja el commit con el mensaje `FEAT-xx-Tn: …`, sin push.

## Decisiones pendientes

Ver plan §8: «crear roles» (consulta al profesor, no bloquea), scopes de
`modulo-marketplace` con G6 y fusión del backend #55.
