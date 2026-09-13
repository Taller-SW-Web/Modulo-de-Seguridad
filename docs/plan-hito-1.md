# Plan de ataque — Hito 1 (Semana 4)

**Presentación:** sábado 19 de septiembre de 2026
**Hoy:** sábado 12 de septiembre de 2026 — quedan **7 días**
**Peso:** 25% del Componente 2 (que a su vez vale 40% de la nota)

---

## 1. Qué evalúa exactamente el profesor

El rubro del Hito 1 tiene cuatro líneas. Nada más. Todo lo que no esté en esta
tabla **no suma** esta semana, y hacerlo ahora resta tiempo a lo que sí suma.

| # | Rubro | Responsable | Estado |
|---|---|---|---|
| 1 | Arquitectura del módulo preliminar | Sergio (con Jose Luis) | ⬜ |
| 2 | Funcionalidades identificadas y distribuidas por cada integrante | Sergio | ✅ `docs/responsabilidades.md` |
| 3 | Especificaciones de requerimientos en detalle — SDD | Los 7, una cada uno | ⬜ |
| 4 | Diseño de wireframes | Valery | ⬜ |

> **Lo que NO toca esta semana:** escribir código de producción, montar la base
> de datos, configurar CI/CD o desplegar en nube. Eso es Hito 2 y Hito 3. La
> tentación de llegar el sábado con código funcionando y sin specs es el error
> clásico: el rubro no lo pide y el contrato sí lo esperan seis equipos.

---

## 2. El entregable invisible: el contrato

Hay una quinta cosa que el rubro no menciona y que es **la más importante del
ciclo para este equipo**: el contrato OpenAPI publicado con un mock server.

Los seis módulos restantes dependen de nosotros y no pueden avanzar sin saber
cómo nos van a llamar. Si llegamos al sábado con el contrato publicado y cero
líneas de código, no retrasamos a nadie. Si llegamos con la implementación a
medias y sin contrato, retrasamos al curso entero.

**Dueño:** Sergio. **Fecha límite:** jueves 17, para comunicarlo a los seis
equipos el viernes 18 y no el mismo día de la presentación.

---

## 3. Calendario de la semana

| Día | Qué pasa | Quién |
|---|---|---|
| **Dom 13** | Reunión de arranque (1 h). Se reparte este documento, cada quien acepta su SPEC y crea su rama. Se define el proveedor de nube. | Los 7 |
| **Lun 14** | Borrador de arquitectura: diagrama de contexto y de componentes. Valery arranca paleta y sistema de diseño. | Sergio, Jose Luis, Valery |
| **Mar 15** | Primer borrador de las 9 specs (secciones 1 a 4: contexto, propósito, alcance, requisitos). Wireframes de login y registro. | Los 7 |
| **Mié 16** | Revisión cruzada de specs. Escenarios *Dado/Cuando/Entonces* completos. Diagramas de secuencia de login y refresh. | Los 7 |
| **Jue 17** | **Congelamiento del contrato.** OpenAPI publicado + mock server levantado. Specs aprobadas por el PO. Wireframes completos. | Sergio, Valery |
| **Vie 18** | Ensayo de la presentación (cronometrado). Comunicación del contrato a los 6 equipos. Corrección de lo que falle en el ensayo. | Los 7 |
| **Sáb 19** | Presentación. | Los 7 |

---

## 4. Tarea concreta por integrante

### Sergio — Product Owner y Arquitecto de solución
- [ ] `docs/arquitectura/` — diagramas de contexto, componentes, secuencia de login y de renovación de token
- [ ] `docs/arquitectura/adr/` — 3 ADRs: RS256 frente a HS256, validación local frente a introspección, integración asíncrona por eventos
- [ ] `specs/SPEC-09-api-identidad.md`
- [ ] `specs/openapi.yaml` — todos los endpoints del contrato
- [ ] Mock server levantado y documentado en el README
- [ ] Aprobar las 8 specs de los demás
- [ ] Comunicar el contrato a los 6 equipos (viernes)

### Jose Luis — Tech Lead, Backend
- [ ] `specs/SPEC-02-autenticacion.md`
- [ ] Estructura de paquetes del backend documentada en `docs/arquitectura/implementacion.md`
- [ ] Prueba de concepto de login con Spring Security + JWT, **sin base de datos** (para descubrir los problemas ahora, no en la semana 7)
- [ ] Configurar la protección de `main` y la plantilla de PR
- [ ] Revisar las specs de backend de Eva, Juan José y Luis David

### Eva Lucía — Backend
- [ ] `specs/SPEC-01-registro.md`
- [ ] `specs/SPEC-05-roles-permisos.md`
- [ ] `specs/SPEC-08-atributos.md`
- [ ] `docs/arquitectura/modelo-datos.md` — las 15 tablas con sus relaciones (diagrama entidad-relación)

### Juan José — Full Stack
- [ ] `specs/SPEC-03-politica-contrasenas.md`
- [ ] `specs/SPEC-06-recuperacion-contrasena.md`
- [ ] Revisar los wireframes de sus dos pantallas con Valery

### Luis David — Full Stack
- [ ] `specs/SPEC-04-otp-mfa.md`
- [ ] `specs/SPEC-07-bloqueo-cuentas.md`
- [ ] Revisar los wireframes de sus dos pantallas con Valery

### Valery — Frontend y Diseño
- [ ] Paleta de colores y sistema de diseño (tipografía, escala de espaciado, estados de componente, contraste accesible)
- [ ] Wireframes de las **8 pantallas** en Figma (ver sección 5)
- [ ] Exportar a PDF en `docs/wireframes/`
- [ ] Enlace de Figma con permiso de lectura para el equipo y el profesor

### Christian — DevOps, QA y Frontend de administración
- [ ] Crear la cuenta de nube del grupo y documentar límites del nivel gratuito
- [ ] `docs/arquitectura/despliegue.md` — cómo se despliega el módulo (aunque todavía no se despliegue)
- [ ] Esqueleto del `docker-compose.yml` (backend, frontend, PostgreSQL, RabbitMQ) — sin implementación, solo la topología
- [ ] Wireframes del panel de administración, coordinados con Valery
- [ ] Montar el tablero de GitHub Projects con las 9 specs como épicas

---

## 5. Las 8 pantallas que necesitan wireframe

| # | Pantalla | SPEC que la origina |
|---|---|---|
| 1 | Inicio de sesión (con estado de error genérico y de cuenta bloqueada) | SPEC-02, SPEC-07 |
| 2 | Registro de cliente (con medidor de fuerza de contraseña) | SPEC-01, SPEC-03 |
| 3 | Verificación de correo (esperando / éxito / enlace vencido) | SPEC-01 |
| 4 | Desafío de código OTP (6 dígitos, reenvío, intentos restantes) | SPEC-04 |
| 5 | Recuperar contraseña — solicitud y nueva contraseña | SPEC-06 |
| 6 | Mi cuenta — datos de perfil y direcciones | SPEC-08 |
| 7 | Panel admin — listado de usuarios con filtros y acciones | SPEC-01, SPEC-05, SPEC-07 |
| 8 | Panel admin — detalle de usuario: roles, bloqueos, historial de accesos | SPEC-05, SPEC-07 |

---

## 6. Guion de la presentación (15 minutos)

| Min | Bloque | Quién |
|---|---|---|
| 0–2 | Qué es el módulo y por qué los otros seis dependen de él | Sergio |
| 2–6 | Arquitectura: contexto, componentes y las dos formas de validar un token | Sergio y Jose Luis |
| 6–8 | Cómo repartimos las 9 funcionalidades entre los 7 | Sergio |
| 8–11 | SDD: una spec completa de ejemplo, de requisito a escenario a prueba | Eva Lucía |
| 11–14 | Wireframes y sistema de diseño | Valery |
| 14–15 | Contrato publicado y mock server en vivo: `curl` al endpoint de JWKS | Sergio |

**El cierre con el `curl` al mock importa.** Es la prueba de que los otros seis
equipos ya pueden programar contra nosotros. Es lo único que ningún otro grupo
podrá enseñar el sábado.

---

## 7. Criterio de «entregado»

El Hito 1 está listo cuando:

- [ ] Las 9 specs están en `main`, con las 7 secciones, aprobadas por el PO
- [ ] Cada integrante tiene al menos un commit propio en `main`
- [ ] `openapi.yaml` valida sin errores y el mock responde
- [ ] Los diagramas de arquitectura están en el repo y exportados para la PPT
- [ ] Los wireframes de las 8 pantallas están exportados y enlazados
- [ ] La presentación se ensayó completa y cabe en 15 minutos
- [ ] Los seis equipos recibieron la URL del contrato y del mock
