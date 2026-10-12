# Flujo SDD — specs, plan, tareas y código

**Corte:** 12-oct-2026. Base publicada por PR #46; complemento local con
[siete planes H3 aprobados](hito-3/README.md) y [siete tableros de tareas](../tareas/hito-3/README.md), sin implementación acreditada. Se conserva el
[plan de nueve puntos](README.md) y el [reparto de ocho épicas](../specs/epicas/README.md).

## Secuencia de trabajo

1. **Leer entradas:** épica, specs funcionales, UI/UX, OpenAPI, React,
   catálogos/ADR aplicables y su revisión.
2. **Planificar el incremento:** alcance H3 y posterior, RF/ESC/UI, diseño de
   datos/interfaz, decisiones internas, dependencias, fases y comprobaciones.
   Una nueva regla vuelve primero a la spec; contrato con Sergio.
3. **Revisar y aclarar:** el dueño revisa su plan y acuerda interfaces con
   proveedores/consumidores. No inventar aprobación ni resolver contradicción
   funcional programando una interpretación.
4. **Derivar tareas:** resultados pequeños con fuente, dueño, dependencia,
   aceptación y evidencia. Sin títulos genéricos «hacer todo el backend».
5. **Implementar y verificar:** una tarea lista por vez; pruebas proporcionales,
   revisión y evidencia. Los criterios de integración requieren capacidad real.
6. **Planificar siguiente incremento:** conservar dueño/alcance restante;
   terminar H3 no cierra la épica completa.

[Fuentes primarias SDD](../docs/sdd-fuentes-y-aplicacion.md) registra GitHub
Spec Kit/Kiro. Se adapta el método al curso; no se instalaron esas herramientas.
La referencia previa de Marketplace se conserva sin reabrir su auditoría.
[Cierre del plan H3](hito-3/CIERRE-PLAN.md): aprobación formal comunicada y
las decisiones técnicas delegadas concretadas; ejecutar ahora una tarea lista
del tablero propio, con sus fuentes y criterios.

## Estructura actual

```text
specs/                         Funcional, UI, OpenAPI, React y catálogos
plan/
  README.md                    Preparación y navegación
  PLAN-IMPLEMENTACION.md       Demo conjunta G7/G5
  DECISIONES-Y-BLOQUEOS.md      Referencias del registro único
  hito-3/
    README.md                  Los siete estudiantes y sus incrementos
    ACUERDOS-INTERNOS.md        Capacidades y composición cerradas
    DISENO-PERSISTENCIA.md      Tablas, mappings, concurrencia y migraciones
    ENTORNO-Y-REACT.md          Supabase, runtime, firma y composición SPA
    CIERRE-PLAN.md              Aprobación y traspaso a tareas
    ANTES-DE-CODIFICAR.md       Condiciones de inicio y entrega
    eva-moreno/PLAN.md
    jose-limachi/PLAN.md
    juan-cano/PLAN.md
    luis-morales/PLAN.md        EP-04 y EP-07, evidencias separadas
    sergio-osorio/PLAN.md
    christian-arancivia/PLAN.md
    valery-gutierrez/PLAN.md
  funcionalidades/             Índice por épica y diseño detallado EP-02
  transversales/               TEC-IDENTIDAD y TEC-INFRA
  seguimiento/                 Plantilla y reglas de seguimiento
  seguimiento-epicas.md        48 filas previas de cobertura
tareas/hito-3/                 Índice y siete TAREAS.md por estudiante
```

La carpeta por estudiante es entrada al incremento, no una épica nueva.
Planes técnicos/detallados se enlazan como diseño adicional; no se duplican
tableros. Al siguiente hito se crea su incremento y se conserva evidencia H3.

## Tareas H3 disponibles

La restricción inicial de no generar tareas durante el cierre del plan fue
superada por la instrucción posterior de Luis: se derivaron por bloques los
siete tableros H3 a partir de planes aprobados y diseño cerrado por delegación.
El [índice de tareas](../tareas/hito-3/README.md) enlaza un único
`tareas/hito-3/<integrante>/TAREAS.md` por dueño/incremento, también desde su
PLAN.md. Todos comienzan pendientes; redactarlos no acredita código ni pruebas.

Las 48 filas anteriores son cobertura general. Las tareas H3 nuevas
mantienen referencias de épica, por ejemplo EP-02-T001; técnicas,
TEC-IDENTIDAD-T001. Luis puede tener tareas de EP-04 y EP-07 en un mismo
seguimiento, con criterios y evidencia separados. No se reutilizan IDs viejos
EP-02-01 ni se contabiliza la cobertura como tareas adicionales.

## Dueño y condiciones de inicio

El dueño implementa cada capa de su incremento. Un bloque del panel compartido
no transfiere frontend/backend. Sergio conserva contrato/identidad técnica;
Christian infraestructura. [Interfaces H3](hito-3/ACUERDOS-INTERNOS.md).

Tarea lista: fuentes coherentes, plan revisado, decisión técnica necesaria
concreta, comprobación escrita. Su ejecución integrada requiere dependencia
disponible. No hace falta terminar las ocho épicas ni cerrar pendientes
fuera de su recorrido.

Terminado: criterios pasan en entorno indicado, evidencia/revisión y ningún
bloqueo aplicable. Separar documental, mock/fixture, servicio real local y
conjunto en nube. [Condiciones](hito-3/ANTES-DE-CODIFICAR.md).

[Registro único](../docs/registro-inconsistencias.md) conserva problemas;
[decisiones H3](DECISIONES-Y-BLOQUEOS.md) los referencia, sin otro tablero.

## Referencia previa consultada

Marketplace, revisión 64cc824ec47ad2758bd657f01e5881165189e1f1:
[plan](https://github.com/Taller-SW-Web/G1-Marketplace-SPECS/blob/64cc824ec47ad2758bd657f01e5881165189e1f1/plan/funcionalidades/F-002-iniciar-sesion.md)
y [tareas](https://github.com/Taller-SW-Web/G1-Marketplace-SPECS/blob/64cc824ec47ad2758bd657f01e5881165189e1f1/plan/seguimiento/F-002-iniciar-sesion.md).
Se conserva su separación de entradas/diseño/tareas/evidencia, adaptada a G7.
