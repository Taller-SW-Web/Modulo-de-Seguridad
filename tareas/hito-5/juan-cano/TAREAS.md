# H5 — tareas de Juan José Cano Vasquez

**Épica:** EP-03 Gestión de credenciales; revisión de seguridad del pipeline.
**Plan:** [H5 §3](../../../plan/hito-5/README.md).
**Rama:** `feat/TEC-INFRA-seguridad-pipeline` (backend y frontend).
Cada tarea es un commit `FEAT-xx-Tn: …` en su rama. Sus PR los revisa David; él revisa los de Sergio.

**Situación de partida:** el pipeline hace compilación, pruebas, imagen, despliegue y smoke, pero no tiene ningún análisis de seguridad. La rúbrica de H5 pide «CI/CD incluyendo revisión de seguridad».

**Cómo evitar choques:** el análisis va en un archivo de workflow propio, `seguridad.yml`, sin tocar `ci.yml`, que editan Christian y Jose Luis.

**Fuentes comunes:** `.github/workflows/ci.yml` y `.github/CI.md` de cada repositorio de código.
Referencia de otro grupo: `Taller-SW-Web/devsecops-templates` (G4 Retail). Está hecha para Maven, Railway y Vercel; sirve de ejemplo, no se reutiliza tal cual.

| ID | Resultado | Prioridad | Depende de | Estado |
|---|---|---|---|---|
| FEAT-15-T1 | Análisis estático de código en backend y frontend | P1 | David OPS-T3 (mié 11-nov) | Pendiente |
| FEAT-15-T2 | Análisis de dependencias | P1 | — | Pendiente |
| FEAT-15-T3 | Detección de secretos y escaneo de la imagen | P1 | — | Pendiente |
| FEAT-15-T4 | Política y tratamiento de hallazgos | P1 | T1–T3 | Pendiente |
| EP-03-T008 | Pendiente de H3: prueba PostgreSQL en CI | P2 | — | Por comprobar |

## FEAT-15 — Revisión de seguridad en el pipeline (P1)

### FEAT-15-T1 — Análisis estático
- **Resultado:** análisis de código Java y TypeScript en cada PR hacia `develop`.
- **Primer paso:** crear `seguridad.yml` en el backend con el análisis de Java.
- **Aceptación:** una ejecución con reporte en cada repositorio.

### FEAT-15-T2 — Dependencias
- **Resultado:** revisión de dependencias nuevas en cada PR y reporte de vulnerabilidades conocidas.
- **Aceptación:** reporte visible en un PR real.

### FEAT-15-T3 — Secretos e imagen
- **Resultado:** detección de secretos en el historial del PR y escaneo de la imagen Docker del backend.
- **Aceptación:** reportes en la ejecución.

### FEAT-15-T4 — Política y hallazgos
- **Resultado:** documento breve con qué bloquea un PR y qué solo informa, y una decisión para cada hallazgo de severidad alta: corregido o justificado.
- **Primer paso:** proponer la política al equipo con los primeros resultados.
- **Aceptación:** política publicada en `.github/CI.md`; ningún hallazgo alto sin decisión.
- **Evidencia:** `docs/evidencia/h5/FEAT-15.md`, con los identificadores de las ejecuciones. Nivel: CI.

## EP-03-T008 (P2)
- **Primer paso:** comprobar si la prueba PostgreSQL del cambio de contraseña ya corre en CI.
- **Aceptación:** en verde en CI o cerrada con su motivo en el tablero.
