# FEAT-07 — Configuración del segundo factor H4

Responsable: Luis David Morales Brenis. Corte: 20-oct-2026, America/Lima.
Estado: implementación publicada y correcciones frontend locales; **pendiente de aceptación desplegada**.

## Fuentes y cambios

Fuentes SDD: main 029171b61335ec0412af777c2d8a0559d8dadb16 (#64), SPEC-10,
FRONT-09 y contrato publicado v2. Se respetan acciones HABILITAR/DESHABILITAR y EMAIL;
no se agregan campos de reautenticación/canal del resumen H4 ni se modifica OpenAPI.

- [Backend #57](https://github.com/Taller-SW-Web/Modulo-de-Seguridad-Backend/pull/57):
  T1/T2 solicitan, reenvían y confirman; T3 añade receptor SMS exclusivamente simulado.
  Cambio de 600 s, OTP de 300 s, tres intentos y cuota compartida de tres envíos/900 s.
  Estado, consumo, auditoría y aviso cifrado se confirman de forma atómica.
- [Frontend #11](https://github.com/Taller-SW-Web/G7-Modulo-de-Seguridad-Frontend/pull/11):
  T4 configura el segundo factor desde Mi cuenta. Tokens y códigos solo en memoria;
  aplica estado únicamente tras 204 y nueva lectura del perfil.

Bases de código: backend develop 4bd7b61165ff2e4940c68a8b0f050ba395b7b7ad;
frontend develop 82c94149c8e2326d3d6013a167026a6b38d75b4a.
Implementación frontend publicada: a40c7c28202d51398f52883c03920803eba133f5;
las correcciones de auditoría descritas abajo todavía no tienen commit propio.

## Auditoría frontend de Sergio

La revisión del PR #11 confirmó el contrato e identificó un cierre de sesión indebido
con access vencido. Solicitar ahora permite refresh reactivo y una repetición.
Reenviar/confirmar renuevan antes de enviar cuando restan 60 s o menos, o exp no es
legible, mediante la renovación compartida. No repiten una mutación por TOKEN_INVALIDO.

X, Escape, clic exterior y Cancelar están bloqueados mientras hay petición en vuelo.
Cancelar limpia errores; una URL base mal configurada no se presenta como fallo de red.
Se corrigieron el nombre del test 204 y el estado histórico de la evidencia local.
El cuerpo del PR necesita quitar la línea final de triple acento grave desde GitHub;
no se editó el PR ni se enviaron mensajes desde esta sesión.

## Verificación

Frontend: Node 22.22.3; typecheck y lint correctos; **79/79 pruebas, 13 archivos**,
con un worker. Trece casos de pantalla y diez de API MFA; cubren access vencido,
refresh preventivo compartido, rechazo/red al renovar, logout durante refresh,
cambio inválido sin repetición, modal ocupado, cancelación, cuotas y vencimientos.
API simulada: no acredita navegador contra backend desplegado ni recepción de correo.
Build de producción correcto; se conserva advertencia de Vite por chunk JS de 537,59 kB.

Backend: selección inicial 157 casos distintos aprobados, incluidos 62 PostgreSQL,
arquitectura y bootJar. El primer CI ejecutó 794 pruebas y falló solo el conteo de
MigracionesAlineadasTest: esperaba siete migraciones y V108 es la octava.
Corrección publicada por David en 8a653a599baa9914f6c4f0cd6dc116194cac33a2.
[CI Backend 205](https://github.com/Taller-SW-Web/Modulo-de-Seguridad-Backend/actions/runs/39502445692)
completado con success; test de alineación también aprobado localmente.

Un intento local de build completo quedó interrumpido después de un fallo en la
preparación de familia revocada de CambioContrasenaPostgresTest; no se cuenta como
aprobado. Su repetición aislada no pudo inicializar Testcontainers porque Docker
estaba apagado. No se modificó código ni pruebas de contraseña para ocultarlo.
El CI aprobado corresponde al SHA publicado indicado, no al entorno local interrumpido.

## Despliegue y aceptación pendientes

Orden requerido: backend #57 revisado/fusionado, V108 aplicada y permisos de runtime
comprobados por Christian, y después frontend #11. El CI aprobado no sustituye revisión.

V108__cambio_mfa.sql conserva copias idénticas en recursos Flyway y propuesta backend:
SHA-256 d1cb23c5538c988c247c181dd674b64e865d53bc08b7a4ff15834293a7f1eda8.
Christian debe confirmar reserva/historial, esquema, ventana y permisos del rol JDBC.
No se aplicó ninguna migración remota ni se presupone preparado el entorno.

Registrar URLs y SHAs realmente desplegados; probar activar/desactivar con correo real,
roles obligatorios, OTP incorrecto/vencido/agotado, cuota, refresh, logout/revocación
y ausencia de secretos en trazas. Medir los criterios de rendimiento pendientes.
Solo después evaluar el estado Verificada.

FEAT-11 conserva prioridad y queda En curso a la espera de BFF/readiness, SHAs desplegados,
transporte/retorno confirmados, cuentas/datos A/B y horario de G1. Su cookie opaca sigue
siendo propuesta sin implementación/prueba conjunta acreditada; no se implementó Marketplace.
