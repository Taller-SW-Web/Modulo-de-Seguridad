# H5 — tareas de Jose Luis Limachi Sarmiento

**Épica:** EP-02 Autenticación y sesiones; seguridad de punta a punta.
**Plan:** [H5 §3](../../../plan/hito-5/README.md).
**Rama:** `feat/EP-02-e2e-render` (backend y frontend).
Cada tarea es un commit `FEAT-xx-Tn: …` en su rama. Revisión cruzada con Christian.

**Situación de partida:** los seis casos E2E pasan en un entorno aislado de CI. El job contra Render existe, pero queda omitido: depende de la variable `E2E_RENDER_HABILITADO` y toma el frontend de una rama antigua en lugar de `develop`.

**Fuentes comunes:** [SPEC-05](../../../specs/funcionales/SPEC-05-inicio-sesion.md),
[SPEC-06](../../../specs/funcionales/SPEC-06-renovacion-cierre-sesion.md),
[ficha de la prueba con Ventas](../../../docs/integracion/hito-3-ventas.md),
[tablero H4](../../hito-4/jose-limachi/TAREAS.md).

| ID | Resultado | Prioridad | Depende de | Estado |
|---|---|---|---|---|
| FEAT-12-T4 | Suite E2E contra Render dentro del pipeline | P1 | David OPS-T2 y OPS-T3 (mié 11-nov) | Pendiente |
| FEAT-12-T5 | Prueba conjunta con G5 | P1 | Versión desplegada de G5 (externo) | Pendiente |
| — | Reparar el tablero H4 (filas T1–T4) | P1 | — | Pendiente |

## FEAT-12 — Seguridad de punta a punta (P1)

### FEAT-12-T4 — E2E contra Render
- **Resultado:** los seis casos se ejecutan contra el entorno desplegado después del despliegue y del smoke.
- **Primer paso:** hacer que el job tome el frontend de `develop` y entregar a David la lista de cuentas de prueba y la forma de recibir el código OTP desde el CI.
- **Aceptación:** una ejecución de `develop` con el job E2E contra Render en verde, no omitido.
- **Evidencia:** `docs/evidencia/h5/FEAT-12.md`, con el identificador de la ejecución y la versión desplegada. Nivel: CI contra despliegue.

### FEAT-12-T5 — Prueba conjunta con G5
- **Resultado:** repetir los casos de la prueba del 16-oct: sin token 401, cuenta A 200 con pedidos, cuenta B 200 vacío, vendedor 403, firma alterada 401 y token vencido 401.
- **Primer paso:** pedir a G5 su versión desplegada. En la última comprobación su servicio respondía 200 sin token.
- **Aceptación:** los seis resultados registrados con fecha y versiones de ambos lados. Si G5 no corrige, se registra como «no probado».
- **Evidencia:** `docs/evidencia/h5/FEAT-12.md`.

### Tablero H4
- **Resultado:** restituir las filas T1–T4 de FEAT-12 con el estado real: T4 completada para H4 en entorno aislado; contra Render pasa a H5.
