# FEAT-12 — Seguridad de punta a punta en H5

Responsable: Jose Luis Limachi Sarmiento. Por su ausencia, T4 la cerró David,
que dependía de él para OPS-T2 y OPS-T3. Corte: 11-nov-2026, America/Lima.
Estado: **T4 cumplida: el E2E contra Render pasa en el pipeline de `develop`.
T5 (prueba conjunta con G5) pendiente.**

## Fuentes y versiones

| Componente | PR fusionado | Commit | URL |
|---|---|---|---|
| Suite E2E (frontend) | [#26](https://github.com/Taller-SW-Web/G7-Modulo-de-Seguridad-Frontend/pull/26) (frontend) | `722193ab08536fdcb9e6bd6ec0cee59579cac80f` en `develop` | https://g7-seguridad-spa.onrender.com |
| Job del pipeline (backend) | [#91](https://github.com/Taller-SW-Web/Modulo-de-Seguridad-Backend/pull/91) (backend) | — | — |
| Backend desplegado en esa ejecución | [#90](https://github.com/Taller-SW-Web/Modulo-de-Seguridad-Backend/pull/90) (backend) | `44f7d2953b4e8727c4f566a3be849ab25171c66a` en `develop` | https://g7-auth-service.onrender.com |

## FEAT-12-T4 — E2E contra Render

**Nivel de evidencia:** CI contra despliegue. **Ejecución:**
[run 43109130867](https://github.com/Taller-SW-Web/Modulo-de-Seguridad-Backend/actions/runs/43109130867),
push a `develop` del 11-nov-2026 a las 17:07 (America/Lima). El job
[«E2E Playwright (FEAT-12-T4)»](https://github.com/Taller-SW-Web/Modulo-de-Seguridad-Backend/actions/runs/43109130867/job/130824736548)
corrió después del despliegue a Render y del smoke, los dos en verde.

### Qué hubo que corregir para activarlo

Hasta el 11-nov el job estaba omitido por falta de `E2E_RENDER_HABILITADO`, y
activarlo así habría fallado siempre:

1. Descargaba la suite de la rama `feat/EP-02-seguridad-e2e`, del 5-nov, y no la de `develop`.
2. El caso del administrador lee el OTP en Mailpit (`E2E_CORREO_API`). Contra
   Render el correo es real y no hay receptor que leer.
3. Exigía cuatro secretos, dos de ellos solo útiles en local.

Corrección:
- Contra Render, el caso del administrador se omite cuando no hay receptor de
  correo (frontend #26). Sigue cubierto completo en el job «E2E Playwright en
  entorno aislado», que levanta Mailpit.
- El job usa la suite de `develop`, solo la cuenta CLIENTE y un único worker (backend #91).

### Cuenta de prueba (OPS-T2)

Una cuenta `CLIENTE` dedicada al E2E, creada desde el registro público de la
SPA desplegada, con el correo verificado y sin segundo factor. El buzón lo
controla el equipo, porque el caso de *refresh* envía dos correos reales por
ejecución. El correo y la contraseña están solo en los secretos
`E2E_CORREO_CLIENTE` y `E2E_CUENTAS_CLAVE` del repositorio backend, y no
aparecen en esta evidencia.

### Resultado

```
Running 6 tests using 1 worker
  ✓  1 [chromium] › e2e/inicio-sesion.spec.ts:18:1 › login cliente abre Mi cuenta (3.2s)
  -  2 [chromium] › e2e/inicio-sesion.spec.ts:24:1 › login admin completa el segundo factor con el código del receptor de correo
  ✓  3 [chromium] › e2e/inicio-sesion.spec.ts:45:1 › ruta privada sin sesión lleva al login y recuerda el destino (1.8s)
  ✓  4 [chromium] › e2e/sesion-y-permisos.spec.ts:12:1 › refresh silencioso: la mutación renueva la sesión sin salir de la pantalla (8.3s)
  ✓  5 [chromium] › e2e/sesion-y-permisos.spec.ts:63:1 › logout cierra la sesión y la ruta autenticada vuelve a pedir login (2.4s)
  ✓  6 [chromium] › e2e/sesion-y-permisos.spec.ts:82:1 › ruta de administración con sesión de CLIENTE queda bloqueada sin pedir datos (2.5s)
  1 skipped
  5 passed (19.1s)
```

Llegaron al buzón de la cuenta los dos correos del caso de *refresh*, los del
código para cambiar el segundo factor.

**Cumple la aceptación de T4:** una ejecución de `develop` con el job contra
Render en verde, no omitido.

### Límites

- El segundo factor del administrador no se prueba contra Render. Lo cubre el E2E
  aislado de la misma ejecución, también en verde.
- Cada push a `develop` envía dos correos a la cuenta de prueba.

## FEAT-12-T5 — Prueba conjunta con G5

Pendiente: depende de la versión desplegada de G5.
