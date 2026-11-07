# FEAT-11 — Cierre H4 de integración G1–G7

**Responsable:** Luis David Morales Brenis · **Corte:** 6-nov-2026, hora de Lima.
**Estado:** cerrada para H4 con alcance ajustado; aislamiento de favoritos A/B
en despliegue pasa a H5. Sergio autorizó a David a resolver el ajuste de alcance.
Este cierre no cambia OpenAPI ni convierte en probadas las operaciones pendientes.

## Entornos y procedencia

- G1: [frontend](https://g1-marketplace-frontend.vercel.app/) y
  [BFF](https://g1-marketplace-backend.onrender.com/). G1 reportó frontend
  `c29ffd36a5592118bca390e89142a32ba938caac` y backend
  `cae04f78f40fd10fa3106aca38d25488d8181497` para su corte de prueba; luego
  reportó frontend `5f33e457bb3e2a8c9750783f16c81f5fc7ba0ddc` para la entrada
  a recuperación. No se atribuyen esos SHA a cada solicitud sin evidencia.
- G7: [API](https://g7-auth-service.onrender.com/api/v1) y
  [SPA](https://g7-seguridad-spa.onrender.com). Captura de Render: frontend
  `cbe2f00` Live tras el PR #20; backend `92086c1` Live el 6-nov. Son versiones
  observadas en esos momentos, no prueba de la versión usada en cada caso anterior.
- Fuentes: capturas y resultados aportados por David y mensajes de G1 en la
  coordinación; [evidencia temprana](FEAT-11-temprana.md) conserva el corte del
  26-oct. No se incluyen contraseñas, códigos, tokens ni enlaces de correo.

## Resultados comprobados y reportados

| Recorrido | Resultado y procedencia | Límite |
|---|---|---|
| Registro y verificación | David registró una cuenta desde G1, recibió el correo, verificó en G7 y volvió al login de G1. | No acredita todos los casos de error o canales. |
| Login y MFA | David ingresó en G1 sin MFA y, tras activar MFA en su cuenta G7, recibió el código por correo e ingresó en G1. | No acredita SMS real ni todos los errores de OTP. |
| Cierre | David comprobó el mensaje de sesión cerrada y la desaparición de su nombre en G1. | No acredita por sí solo todos los casos de revocación. |
| Sesión y renovación | G1 reportó consulta de sesión y renovación de A; B pasó sesión y cierre. | Resultados reportados por G1; no se dispone de SHA por solicitud ni de todas las pruebas negativas. |
| Recuperación | G1 reportó solicitud 202, restablecimiento 204, ingreso con contraseña nueva y rechazo de la anterior y del token reutilizado por API. David probó recuperación iniciada en G1, cambio de contraseña, retorno a su login e ingreso con la nueva. | El rechazo visual del enlace reutilizado en SPA de G7 para esa prueba de G1 no fue acreditado. |
| Cliente técnico G1 | David comprobó emisión HTTP 200 de `modulo-marketplace`, con `catalogo:leer` y `api-productos`; después también emitió con permisos de Despacho. | La emisión no acredita una llamada real ni autorización contra G6. |
| Favoritos | G1 reportó sin sesión 401 y con B 200; reporta además 30/30 tests automatizados de aislamiento, incluido rechazo de borrado por B de un favorito de A, commit `3ec390c`. | Reporte de G1; no se inspeccionó aquí el run. No acredita aislamiento A/B desplegado. |

## Pendiente para H5

G1 confirmó que no puede crear favoritos en producción para esta prueba: su
integración con G6 depende de [backend #239](https://github.com/Taller-SW-Web/G1-Marketplace-Backend/pull/239),
configuración y aprobación de contrato con G6; responsable indicado por G1: Diego.
Una vez publicada, coordinar datos para A y B y verificar acceso al recurso propio,
que B no lea ni modifique o borre el de A, y las denegaciones contractuales.
Registrar fecha, responsables, versiones efectivamente desplegadas y evidencia
sanitizada. También queda pendiente la validación real del cliente técnico de G1
contra G6 y los casos de sesión o seguridad no acreditados arriba.

El cierre de H4 describe la integración de identidad demostrada; no declara
probado el aislamiento de favoritos ni la integración completa G1–G6.
