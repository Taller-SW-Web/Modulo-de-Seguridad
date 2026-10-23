# FEAT-07 — Configuración del segundo factor H4

Responsable: Luis David Morales Brenis. Corte: 23-oct-2026, America/Lima.
Estado: **T1–T4 implementadas y fusionadas; recorrido principal EMAIL comprobado en el despliegue**.
Este registro no certifica todos los RF ni marca toda la FEAT como Verificada.

## Fuentes y alcance

Fuentes SDD consumidas inicialmente: main `029171b61335ec0412af777c2d8a0559d8dadb16`
(#64), SPEC-10, FRONT-09 y contrato publicado v2. Se respetan acciones
HABILITAR/DESHABILITAR y EMAIL; no se modifica OpenAPI ni se agregan campos de
reautenticación/canal del resumen H4.

T1/T2: solicitar, reenviar y confirmar cambios; cambio de 600 s, OTP de 300 s,
tres intentos y cuota compartida de tres envíos/900 s. Estado, consumo, auditoría
crítica y aviso cifrado se confirman atómicamente. T3 añade un receptor SMS
exclusivamente simulado. T4 configura MFA desde Mi cuenta, con tokens/códigos en
memoria, lectura del perfil tras 204, renovación compartida y modal protegido
mientras hay petición en vuelo. Las correcciones de la revisión frontend de Sergio
quedaron integradas en #11.

## Fusión, preparación y despliegue

Orden realizado: infraestructura V108 de Christian (#63) → backend #57 → frontend #11
→ despliegue backend → despliegue frontend → recorrido manual.

| Componente | PR fusionado | SHA desplegado | URL |
|---|---|---|---|
| Backend | [#57](https://github.com/Taller-SW-Web/Modulo-de-Seguridad-Backend/pull/57) | `1ac02a707d8c24c9c37d725c3d860a3660390c19` | https://g7-auth-service.onrender.com |
| Frontend | [#11](https://github.com/Taller-SW-Web/G7-Modulo-de-Seguridad-Frontend/pull/11) | `62f704c16fd8971c70dfff22a08d4c5f2a44833a` | https://g7-seguridad-spa.onrender.com |

David aportó capturas de Render el 23-oct: backend `1ac02a7` con «Deploy succeeded / Live»
y arranque correcto; frontend `62f704c` con igual estado y publicación a las 12:22 de Lima.
Ambos servicios usan develop. Fueron despliegues manuales de SHA específico: Render
avisa que esta modalidad deshabilita Auto-Deploy. No se acredita su reactivación
posterior ni un pipeline automático H4.

Christian informó la aplicación de V108 y grants en el marco de #63. Esta sesión no
aplicó migraciones ni inspeccionó remotamente su historial. Las dos copias de
`V108__cambio_mfa.sql` conservan SHA-256
`d1cb23c5538c988c247c181dd674b64e865d53bc08b7a4ff15834293a7f1eda8`.
El recorrido posterior acredita acceso funcional del runtime para ese flujo;
no certifica todos los permisos del esquema.

## Verificación automatizada

Backend integrado para #57: build completo local con **836 pruebas, cero fallos y
cero omitidas**, arquitectura y bootJar correctos;
[CI aprobado](https://github.com/Taller-SW-Web/Modulo-de-Seguridad-Backend/actions/runs/39913457248).
Incluye alineación de V108 y actualización de develop.

Frontend: Node 22.22.3; **79/79 pruebas en 13 archivos**, typecheck, lint y build correctos.
Los dobles cubren renovación, errores, cancelación, modal ocupado, cuotas y vencimientos.
El build conserva advertencia de Vite por chunk JS de 537,59 kB. Esas pruebas no
acreditan por sí solas navegador contra backend desplegado ni recepción SMTP.

## Recorrido desplegado con cuenta propia C

Ejecutor: David, 23-oct-2026, hora de Lima. Cuenta propia C,
UUID `00000000-0000-4000-8000-00000000f00f`; no se usaron cuentas de otros módulos.
Capturas y confirmaciones recibidas en la conversación de trabajo. No se publican
credenciales, OTP, tokens ni volcados completos de la base.

| Caso | Resultado observado | Evidencia aportada |
|---|---|---|
| Estado inicial | ACTIVO, solo CLIENTE, correo verificado, credencial presente, sin cambio obligatorio y MFA false | Captura de consulta de solo lectura |
| Habilitar EMAIL | Código recibido y confirmado; pantalla «Segundo factor activo» | Captura FRONT-09 y confirmación de David |
| Login con MFA | Tras salir exige código antes de abrir sesión; correo recibido y código aceptado | Confirmación explícita de David |
| Deshabilitar | Confirmado por código; siguiente login sin OTP | Confirmación explícita de David |
| Auditoría | MFA_ACTIVADO / EXITO a las 12:37:22.548826 y MFA_DESACTIVADO / EXITO a las 12:42:48.877286 | Captura de auditoría del 23-oct-2026, hora de Lima |

C quedó restaurada sin MFA. Estos resultados acreditan el recorrido principal
positivo y su auditoría; no son una prueba conjunta con G1.

## Límites y pendientes de aceptación

No se ejecutaron individualmente en el despliegue todos los casos negativos:
roles de gestión/MFA obligatorio, código erróneo/vencido/agotado, cuota, cambio
usado o de otra familia, renovación durante el cambio, revocación y concurrencia.
Tampoco se midió rendimiento ni se inspeccionaron exhaustivamente las trazas en nube.
Su cobertura automatizada no equivale a evidencia desplegada. Conservar estos casos
pendientes antes de declarar todos los RF de FEAT-07 Verificados. SMS sigue simulado.

FEAT-11 sigue En curso, pendiente de datos y prueba conjunta G1. La sesión opaca
del BFF sigue como propuesta sin implementación/prueba conjunta acreditada;
este registro no implementa ni declara completada la integración Marketplace.
