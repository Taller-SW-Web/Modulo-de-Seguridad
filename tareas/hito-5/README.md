# Tareas del Hito 5

Se derivan del [plan H5](../../plan/hito-5/README.md) (10-nov-2026, propuesta pendiente de aprobación). Fecha límite: **lunes 23-nov**; congelamiento propuesto: **viernes 20-nov**.

**Regla de entrega:** una FEAT = una rama = un PR por repositorio hacia `develop`. Las tareas de este tablero son **commits dentro de la rama de su FEAT**. La documentación va en PR a `main`. Ningún PR lo fusiona su autor.

## Tableros

| Integrante | Tablero | FEAT | Prioridad |
|---|---|---|---|
| Eva Lucía Moreno Zevallos | [TAREAS](eva-moreno/TAREAS.md) | FEAT-13 alta administrativa v2 · FEAT-02 cierre | P1 · P2 |
| Jose Luis Limachi Sarmiento | [TAREAS](jose-limachi/TAREAS.md) | FEAT-12 E2E contra Render y prueba con G5 | P1 |
| Juan José Cano Vasquez | [TAREAS](juan-cano/TAREAS.md) | FEAT-15 revisión de seguridad en el pipeline | P1 |
| Luis David Morales Brenis | [TAREAS](luis-morales/TAREAS.md) | OPS desbloqueos · FEAT-16 token cifrado · FEAT-14 módulo de configuración · FEAT-20 compra por canal · FEAT-11 · FEAT-07 y FEAT-08 | P1 · P2 |
| Sergio Alejandro Osorio Montenegro | [TAREAS](sergio-osorio/TAREAS.md) | PO · FEAT-19 API Management · FEAT-05-T4 · FEAT-06-T4 | P1 · P2 |
| Christian Gabriel Arancivia Salas | [TAREAS](christian-arancivia/TAREAS.md) | FEAT-09-T3 despliegue del frontend · FEAT-18 mecanismo asincrónico · FEAT-10 | P1 · P2 |
| Valery Cristin Gutierrez Bendezu | [TAREAS](valery-gutierrez/TAREAS.md) | FEAT-17 pruebas de rendimiento · FEAT-04 cierre | P1 · P2 |

## Orden de trabajo y dependencias

```text
mar 10 y mié 11-nov
  David OPS-T1 (consultas Supabase) ──► Sergio PO-T2 · David FEAT-20
  David OPS-T2 (cuentas y cliente de prueba) ──► Jose Luis FEAT-12-T4 · Valery FEAT-17 · Sergio FEAT-06-T4
  David OPS-T3 (ajustes de repositorio y Render) ──► Christian FEAT-09-T3 · Jose Luis FEAT-12-T4 · Juan José FEAT-15
  David: propuestas de contrato de FEAT-14 y FEAT-16 · mensajes a G1, G2, G5 y G6
jue 12-nov
  Sergio PO-T1 (lote de contrato) · plataforma de API Management elegida
S1 (hasta dom 15-nov)
  Cada dueño: backend y pantallas de su FEAT, en PR con CI en verde
S2 (lun 16 a vie 20-nov)
  Recorridos desplegados, pruebas conjuntas y evidencia · vie 20: congelamiento
sáb 21 y dom 22-nov: ensayo · lun 23-nov: fecha límite
```

| Necesita | De | Entrega | Para |
|---|---|---|---|
| Resultado de las consultas de Supabase | David OPS-T1 | mar 10-nov | Sergio PO-T2 · David FEAT-20 |
| Cuentas de prueba y cliente técnico de prueba | David OPS-T2 | mié 11-nov | Jose Luis FEAT-12-T4 · Valery FEAT-17 · Sergio FEAT-06-T4 |
| Ajustes de repositorio y Render | David OPS-T3 | mié 11-nov | Christian FEAT-09-T3 · Jose Luis FEAT-12-T4 · Juan José FEAT-15 |
| Lote de contrato H5 | Sergio PO-T1 | jue 12-nov | David FEAT-14 y FEAT-16 |

Fuera de esta tabla, ninguna tarea P1 espera la entrega de otro integrante. Las dependencias externas no bloquean el cierre: el caso que no llegue se registra como «no probado», con su motivo.

## Formato de cada tarea

Cada tarea indica **resultado**, **depende de**, **primer paso**, **aceptación** (cómo se da por terminada) y **evidencia**. Una tarea con pantalla no se cierra solo con la API.

Estados: **Pendiente → En curso → En PR → Fusionada → Desplegada → Verificada**. «Desplegada» identifica la versión observada en Render; «Verificada» añade criterios y evidencia. **Bloqueada** solo si falta una entrega de otro; se anota cuál y se sigue con otra tarea.

Toda evidencia indica su nivel: **propia desplegada**, **CI**, **despliegue** u **otro módulo**.

## Cómo pedir ayuda a la IA

> Soy [nombre]. Lee AGENTS.md, el plan H5 y mi TAREAS.md de `tareas/hito-5`.
> Estoy en la rama `feat/...` de mi FEAT. Implementa únicamente [FEAT-xx-Tn],
> consultando sus fuentes. Respeta los ports y las tablas de cada dueño. Ejecuta las
> comprobaciones indicadas y deja el commit con el mensaje `FEAT-xx-Tn: …`, sin push.
