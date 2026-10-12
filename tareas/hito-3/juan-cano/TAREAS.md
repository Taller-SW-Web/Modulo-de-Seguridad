# H3 — tareas de Juan José Cano Vasquez

**Dueño:** Juan José · **Épica:** EP-03 · **Incremento:** H3-JUAN.
**Base:** [PLAN aprobado 1.0.0](../../../plan/hito-3/juan-cano/PLAN.md),
12-oct-2026; aprobación comunicada por Luis.

**Resultado:** el usuario cambia su contraseña desde la pantalla, conserva su
familia actual, cierra las otras y recibe aviso real. Juan José también entrega
verificación/caducidad a login y OTP, y tokens de uso único al desbloqueo de Luis.

**Fuentes comunes:** [SPEC-07](../../../specs/funcionales/SPEC-07-politica-cambio-contrasena.md),
[composición](../../../plan/hito-3/ACUERDOS-INTERNOS.md),
[persistencia](../../../plan/hito-3/DISENO-PERSISTENCIA.md),
[entorno/React](../../../plan/hito-3/ENTORNO-Y-REACT.md),
[OpenAPI v1](../../../specs/contrato-api/openapi.yaml),
[errores](../../../specs/catalogo-errores.md).

**Límite:** recuperación/restablecimiento SPEC-08 posteriores. No programar
login, OTP, bloqueo, repositorios de usuario ni revocación de sesiones ajenos.
El token de desbloqueo es capacidad de persistencia; Luis conserva su regla.

## Orden de trabajo

T001–T004 entregan bases independientes del frontend. T006 puede avanzar con
la base React de Jose Luis mientras se compone T005. T007 une formulario/API;
T008–T009 verifican efectos y recorrido real con los proveedores entregados.

| ID | Resultado | Prioridad | Depende de | Estado |
|---|---|---|---|---|
| EP-03-T001 | Credencial versionada, hashing e historial | P0 | Esquema/historia revisados con Christian | Pendiente |
| EP-03-T002 | Política backend y endpoint de consulta | P0 | T001; datos de cuenta Eva para validación personal | Pendiente |
| EP-03-T003 | CredencialesPort y caducidad | P0 | T001–T002; roles Sergio y cuentas Eva | Pendiente |
| EP-03-T004 | TokensUnUsoPort para desbloqueo | P0 | Esquema/historia revisados con Christian | Pendiente |
| EP-03-T005 | Cambio de contraseña transaccional | P1 | T001–T003; EP-01-T002; EP-02-T003; auditoría/correo Christian | Pendiente |
| EP-03-T006 | Medidor y hook de política React | P1 | T002; EP-02-T001/T008; perfil Valery para orientación personal | Pendiente |
| EP-03-T007 | Formulario de cambio de contraseña | P1 | T005–T006; navegación/perfil Valery | Pendiente |
| EP-03-T008 | Pruebas de composición y atomicidad | P1 | T005; providers reales para pruebas conjuntas | Pendiente |
| EP-03-T009 | Verificar y defender cambio desplegado | P1 | T007–T008; login/MFA y despliegue reales | Pendiente |

Txxx pertenece a EP-03 salvo prefijo explícito. Los otros dueños entregan sus
capacidades según su plan; un doble solo acredita una prueba aislada.

## Tareas

### EP-03-T001 — Preparar credencial, hashing e historial

- **Fuentes:** RF-07.4/07.6, ESC-07.5; persistencia §§2/4/8, V101 reservada.
- **Implementar:** en `backend/src/main/java/com/marketplace/auth/`, adaptar
  entidades/repositorios propios de credencial/historial; añadir `version`
  mediante migración nueva y `@Version`. Nuevos hashes Argon2id con parámetros
  del diseño; verificar BCrypt existente solo con costo ≥12.
- **Cierre:** secreto exacto sin recorte/normalización; ninguna contraseña/hash
  en logs o DTO públicos. Historial de cinco incluyendo actual, orden estable;
  verificar reutilización con PasswordEncoder, no igualdad de hashes salados.
  La fixture inicial incluye su hash actual y no inventa historial anterior.
- **Comprobar:** tests de hash/verificación, hash inválido, historial completo/
  incompleto y revisión concurrente; migración y mapping en PostgreSQL de prueba.
- **Evidencia:** tests, versión/proveedor criptográfico compatible con Boot y
  migración revisada. No editar migraciones aplicadas ni cargar seeds a ciegas.

### EP-03-T002 — Implementar política y GET /api/v1/password/politica

- **Fuentes:** RF-07.1–07.5/07.11; ESC-07.1–07.6; OpenAPI política/errores.
- **Implementar:** servicio de política y controlador propios: mínimo 10,
  mayúscula/minúscula/dígito/especial, comunes, datos personales e historial.
  Publicar solo campos del esquema vigente, sin lista, hashes o endpoint nuevo.
- **Cierre:** backend decide aunque React se omita. Lista UTF-8 externa
  `/run/config/g7/password-comunes.txt`, recarga atómica 60 s y última versión
  válida ante fallo; sin lista válida inicial no habilitar establecer contraseña.
  Normalizar solo copias para comparación según diseño, nunca el secreto del hash.
- **Comprobar:** nueva válida, corta, complejidad incompleta, común, nombres/correo
  y una de las cinco anteriores; recarga válida/inválida. Respuesta de política
  y `422 POLITICA_INCUMPLIDA`/detalle de reglas conforme contrato.
- **Evidencia:** tests y respuestas sanitizadas; consumir datos de Eva por port,
  sin consultar su tabla. No agregar reglas públicas no previstas.

### EP-03-T003 — Entregar verificación y caducidad a autenticación

- **Fuentes:** RF-07.6/07.7, ESC-07.7; composición §2 `CredencialesPort` y §§3–4.
- **Implementar:** interfaz/DTO y adaptador propios para preverificación,
  confirmación de revisión, evaluación de caducidad y validación de nueva.
  Comparación equivalente para cuenta inexistente, revisión opaca id/version;
  nunca retornar credencial/hash ni aceptar reglas decididas por React.
- **Cierre:** cualquier rol de gestión obliga caducidad al instante de 90 días;
  CLIENTE/VENDEDOR sin gestión no caducan por esa regla. Una revisión previa
  deja de ser válida si cambió la credencial; callers revalidan bajo lock.
- **Comprobar:** antes/igual/después de 90 días, roles múltiples, cuenta
  inexistente y cambio entre preverificación y confirmación. Login Jose Luis y
  OTP Luis consumen el mismo resultado, sin duplicar la regla.
- **Entrega/evidencia:** tests y ejemplos internos para esos dueños. Juan José
  aporta credenciales reales al protocolo de fixtures de Eva; no implementa
  pantallas/controladores de login ni emisión de sesiones.

### EP-03-T004 — Entregar tokens de uso único vinculados al bloqueo

- **Fuentes:** composición §2 `TokensUnUsoPort`; persistencia §§6/8, V105;
  [SPEC-14 RF-14.11](../../../specs/funcionales/SPEC-14-bloqueo-automatico.md).
- **Implementar:** mapping/migración `token_un_uso.contexto_id` y port/adaptador
  de localizar/emitir/consumir/invalidar previos. Para
  `DESBLOQUEO_AUTOMATICO`, contexto identifica la revisión del bloqueo de Luis.
  Token aleatorio 32 bytes, SHA-256 persistido y TTL 30 minutos.
- **Cierre:** validar usuario, propósito, contexto, vigencia y uso único dentro
  de la transacción del productor; invalidar anteriores del mismo propósito.
  Token claro solo retorna al productor para correo, no a logs ni auditoría.
- **Comprobar:** token válido, vencido, usado, propósito/usuario/contexto distintos,
  invalidación y doble consumo concurrente; rollback no consume la prueba.
- **Entrega/evidencia:** tests y contrato a Luis. Juan José no levanta bloqueos
  ni crea ruta de recuperación como efecto de entregar este port.

### EP-03-T005 — Implementar POST /api/v1/password/cambiar

- **Fuentes:** RF-07.8–07.10; ESC-07.8/07.9; OpenAPI cambiar;
  composición §4 y [sesión Jose Luis](../../../plan/hito-3/jose-limachi/PLAN.md).
- **Implementar:** servicio/controlador/DTO propios: actor del acceso verificado,
  cuenta por Eva, familia actual por jti en SesionesPort. Lock cuenta→familias→
  credencial/historial; comprobar actual y nueva, guardar hash/fecha/historial,
  revocar otras familias, auditoría crítica y aviso durable en el mismo commit.
- **Cierre:** 204 conserva familia actual; actual incorrecta da
  `401 CREDENCIALES_INVALIDAS` sin cambios/cierre; nueva inválida 422 canónico.
  Sin familia activa resoluble no cambiar, sin inferir por sub ni aceptar
  usuarioId/familiaId/refresco extra en cuerpo. SMTP posterior fallido mantiene
  reintento y no deshace el cambio confirmado.
- **Comprobar:** dos familias, contraseña vieja/nueva, llamada directa sin React,
  acceso inválido y ausencia de identidad elegida por formulario. Sin auditoría
  crítica o persistencia durable, rollback conjunto.
- **Evidencia:** tests API/DB sanitizados. Solo escribir tablas de Juan José;
  revocaciones/auditoría/correo se realizan mediante sus proveedores reales.

### EP-03-T006 — Implementar medidor y consulta de política React

- **Fuentes:** [componentes FRONT-00 §3](../../../specs/componentes-react/FRONT-00-base-compartida.md);
  RF-07.11, FRONT-10. Consume base React/HTTP de Jose Luis y perfil de
  [Valery](../../../plan/hito-3/valery-gutierrez/PLAN.md).
- **Implementar:** `frontend/src/hooks/usePoliticaPassword.ts` y
  `frontend/src/componentes/PasswordStrengthMeter.tsx`, compartibles con futuros
  formularios sin implementar esos flujos.
- **Cierre:** reglas desde API; estados cumple/no cumple/se comprueba al guardar,
  texto/icono, no solo color. Personales orientativos desde `/auth/me`; comunes/
  historial los decide backend. Si política falla, avisar sin impedir envío;
  422 actualiza las reglas indicadas, sin una copia fija de política.
- **Comprobar/evidencia:** tests con políticas distintas, fallo de carga, datos
  personales ausentes y 422; navegación con teclado y captura sanitizada.

### EP-03-T007 — Construir CambiarContrasenaPage

- **Fuentes:** [FRONT-10 UI-10.1–10.4](../../../specs/front/FRONT-10-cambio-contrasena.md)
  y [componentes FRONT-10](../../../specs/componentes-react/FRONT-10-cambio-contrasena.md).
- **Implementar:** `frontend/src/pantallas/cambiar-contrasena/`:
  `CambiarContrasenaPage` y `useCambiarContrasena`; campos actual/nueva/repetir,
  medidor T006 y cliente/sesión compartidos. Enviar solo campos del contrato.
- **Cierre:** repetir coincide pero no se envía; impedir doble submit; actual
  equivocada se vacía/enfoca sin refresh/logout. 204 vuelve a Mi cuenta con aviso
  y sesión conservada; cancelar no muta. Mensaje de aviso no acredita entrega
  SMTP hasta comprobarla en T009.
- **Comprobar:** tests de estados, 401 semántico, 422 y fallo de red; formulario
  contra API real. Etiquetas, foco, mostrar/ocultar, autocomplete y móvil.
- **Evidencia:** tests/capturas; no ofrecer recuperación pendiente como disponible
  ni modificar la contraseña de A/B usados por Ventas.

### EP-03-T008 — Verificar atomicidad y efectos entre dueños

- **Fuentes:** PLAN §7; RF-07.4/07.8–07.10; composición §§3–4.
- **Inicio:** T005 y proveedores reales de Eva, Jose Luis y Christian;
  fixtures C separadas de A/B. Dobles identificados solo prueban aislamiento.
- **Cierre:** cambio desde C1 mantiene C1 y revoca C2; fallo de auditoría crítica
  revierte hash/historial/revocación/outbox. Dos cambios concurrentes no pisan
  versión ni dejan historial inconsistente; un desafío anterior al cambio ya
  no puede completar MFA con la revisión vieja.
- **Comprobar:** tests en PostgreSQL con concurrencia/fallos controlados y estado
  posterior; nuevo login acepta nueva, rechaza antigua; reintento de correo
  tras fallo SMTP no incluye contraseña ni revierte datos.
- **Evidencia:** comandos/resultados y limitaciones; revisar las capacidades
  compartidas con sus dueños, sin duplicar sus repositorios.

### EP-03-T009 — Defender el cambio de contraseña desplegado

- **Fuentes:** PLAN §7; FRONT-10 y escenarios de SPEC-07 cubiertos por este tablero.
- **Inicio:** T007–T008, login/MFA reales y despliegue/correo Christian.
- **Cierre:** pantalla→HTTP→validación→DB→respuesta; demostrar nueva inválida,
  actual incorrecta sin perder sesión, cambio válido, segunda familia revocada
  y llegada del aviso real. Caducidad de gestión se comprueba en login/OTP.
- **Comprobar/evidencia:** tests backend/React y recorrido en nube, versión/URL,
  resultados/capturas sin secretos y uso de IA trazado spec→plan→tarea→prueba.
  No declarar SPEC-08 ni toda EP-03 implementadas.

## Registro de ejecución y revisión

Cada tarea termina con sus criterios, evidencia y revisión de otro integrante.
Los ports compartidos se revisan con sus consumidores.

| Fecha | ID | Resultado / prueba / enlace de evidencia | Revisor | Estado |
|---|---|---|---|---|
| 12-oct-2026 | Todas | Tareas derivadas; sin implementación ni ejecución | Por asignar al revisar | Pendiente |
