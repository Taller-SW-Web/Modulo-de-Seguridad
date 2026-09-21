# SPEC-13 — Consulta y exportación de la auditoría

| Campo | Valor |
|---|---|
| **Responsable** | Christian Gabriel Arancivia Salas |
| **Hito objetivo** | Hito 5 (Sem. 14) |
| **Estado** | Borrador |
| **Aprobada por** | Product Owner — pendiente de aprobación |

> **Origen.** Sale de dividir la antigua SPEC-06 «Auditoría y trazabilidad» el 20 de septiembre, por indicación del profesor: una spec por función. La equivalencia de números está en [`trazabilidad.md`](trazabilidad.md) §1.

---

## Contexto — ¿por qué?

El registro de SPEC-12 solo sirve si alguien puede leerlo cuando hace falta. El wireframe 8 del Hito 1 —*Panel admin: detalle de usuario, roles, bloqueos e historial de accesos*— dibuja esa lectura, y un incidente entre equipos exige poder entregar los hechos en un archivo.

Leer la auditoría es, a su vez, un privilegio delicado: `ADMIN_SISTEMA` es el rol más poderoso del sistema y el que nadie más vigila. Y hay un segundo lector, más barato y más frecuente: el propio usuario que quiere saber si alguien entró en su cuenta.

---

## Propósito — ¿para qué?

Permitir que un administrador consulte y exporte la auditoría con filtros, y que cualquier usuario vea la actividad de su propia cuenta.

El resultado observable es una consulta paginada y filtrable, un archivo CSV o JSON con exactamente los mismos registros, y un historial propio que nunca muestra eventos de otra cuenta. Cada lectura queda, a su vez, auditada.

---

## Alcance — ¿hasta dónde?

- Consulta filtrada y paginada con `GET /api/v1/auditoria`.
- Exportación del resultado de un filtro con `GET /api/v1/auditoria/exportar`, en CSV o JSON.
- Protección con el permiso `auditoria.ver` (catálogo de SPEC-11).
- Auditoría de la propia consulta y exportación.
- Historial propio del usuario con `GET /api/v1/auth/me/actividad`.

---

## Requisitos — ¿qué debe hacer?

| Código | Requisito |
|---|---|
| RF-13.1 | Un administrador debe poder consultar la auditoría filtrando por usuario afectado, actor, acción, resultado, rango de fechas y dirección IP, con resultados paginados y ordenados de más reciente a más antiguo. |
| RF-13.2 | Un administrador debe poder exportar el resultado de un filtro en formato CSV o JSON, con el mismo criterio de filtrado que la consulta. |
| RF-13.3 | El acceso a la consulta y a la exportación de la auditoría debe exigir el permiso `auditoria.ver`, que solo posee el rol `ADMIN_SISTEMA`. Cualquier otro rol recibe `403`. |
| RF-13.4 | La consulta y la exportación de la auditoría deben, a su vez, quedar registradas en la auditoría: quién consultó los registros de quién también es un hecho auditable. |
| RF-13.5 | Un usuario debe poder consultar los eventos de **su propia** cuenta —inicios de sesión, cambios de contraseña, bloqueos— sin permiso de administrador y sin ver los de nadie más. |

---

## Escenarios — ¿cómo verificamos?

Formato Dado / Cuando / Entonces. Los marcados como **caso borde** cubren
condiciones límite, de error o de seguridad.

### ESC-13.1 Una consulta filtrada

- **Dado** un `ADMIN_SISTEMA` investigando una cuenta concreta,
- **Cuando** envía `GET /auditoria?objetivoUsuarioId={id}&accion=SESION_FALLIDA&desde=2026-09-01&hasta=2026-09-13&pagina=1&tamano=50`,
- **Entonces** el sistema responde `200` con los registros que coinciden, ordenados de más reciente a más antiguo, paginados, y con el total de coincidencias para que la interfaz pueda paginar.

### ESC-13.2 Un rol sin privilegios no entra *(caso borde)*

- **Dado** un usuario autenticado con rol `CLIENTE` o `VENDEDOR`,
- **Cuando** intenta `GET /auditoria`,
- **Entonces** el sistema responde `403` con `code: SCOPE_INSUFICIENTE`, no devuelve ningún registro, y **deja un registro `ACCESO_DENEGADO`**: el intento de leer la auditoría sin permiso es, él mismo, un evento de seguridad.

### ESC-13.3 Consultar la auditoría es auditable

- **Dado** un `ADMIN_SISTEMA` con permiso `auditoria.ver`,
- **Cuando** consulta los registros de una cuenta ajena,
- **Entonces** además de recibir los resultados se escribe un registro `AUDITORIA_CONSULTADA` con el filtro aplicado en `detalle`, de modo que el uso del privilegio de auditoría queda sujeto a la propia auditoría.

### ESC-13.4 Exportación del resultado de un filtro

- **Dado** un `ADMIN_SISTEMA` que acaba de aplicar un filtro,
- **Cuando** envía `GET /auditoria/exportar?formato=csv` con los mismos parámetros de filtro,
- **Entonces** el sistema responde `200` con `Content-Type: text/csv`, una cabecera `Content-Disposition` con nombre de archivo fechado, y **exactamente los mismos registros** que devolvería la consulta con ese filtro, sin el límite de paginación.

### ESC-13.5 Exportación de un filtro demasiado amplio *(caso borde)*

- **Dado** un filtro que abarca más de 100 000 registros,
- **Cuando** se solicita su exportación,
- **Entonces** el sistema responde `413` con `code: EXPORTACION_DEMASIADO_GRANDE` e indica en `detail` que se acote el rango de fechas, en lugar de intentar materializar el archivo completo en memoria.

### ESC-13.6 Un usuario consulta su propio historial

- **Dado** un usuario autenticado sin rol de administración,
- **Cuando** envía `GET /auth/me/actividad`,
- **Entonces** recibe únicamente los eventos de su propia cuenta —inicios de sesión con su IP y fecha, cambios de contraseña, bloqueos— y ningún registro cuyo `objetivoUsuarioId` sea otro.

---

## Requisitos no funcionales — ¿con qué condiciones?

- La consulta filtrada debe responder en menos de **300 ms** en el percentil 95 sobre una tabla de un millón de registros. Exige índices sobre `objetivoUsuarioId`, `actorId`, `accion` y `fecha`.
- La exportación no debe materializar en memoria más de 100 000 registros; por encima responde `413`.
- Las fechas se entregan en **UTC**; la conversión a hora local es responsabilidad de la interfaz.
- El tratamiento de datos personales debe alinearse con la Ley N.º 29733 de Protección de Datos Personales.

---

## Fuera de alcance — ¿qué NO hará?

- La escritura del registro, el catálogo y la retención, que son de SPEC-12.
- Las alertas en tiempo real ante patrones sospechosos.
- El acceso de los módulos consumidores a la auditoría: **ningún módulo consumidor la lee**. Si alguno lo pide, se evalúa como cambio de contrato.
- El análisis forense a largo plazo: la retención es de 90 días; quien necesite más, exporta.

---

## Decisiones de diseño registradas

**Auditar la consulta de la auditoría.** Puede sonar recursivo, pero es la única
forma de que el uso del privilegio `auditoria.ver` deje rastro (RF-13.4).

**El historial propio del usuario (RF-13.5) no es un capricho.** Es la forma más
barata de que un cliente detecte un acceso que no reconoce, y convierte la
auditoría en algo que también sirve a quien no es administrador.

---

## Impacto en el contrato

| Endpoint / evento | Añade, modifica o elimina | Acordado con el PO |
|---|---|---|
| `GET /api/v1/auditoria` | Añade | ⬜ |
| `GET /api/v1/auditoria/exportar` | Añade | ⬜ |
| `GET /api/v1/auth/me/actividad` | Añade | ⬜ |
| Permiso `auditoria.ver` en el catálogo de SPEC-11 | Añade | ⬜ |
| Código de error `EXPORTACION_DEMASIADO_GRANDE` | Añade | ⬜ |

---

## Lista de completitud

La spec se cierra cuando:

- [ ] Todos los requisitos están implementados
- [ ] Cada requisito clave tiene al menos dos escenarios automatizados, uno de ellos caso borde
- [ ] Los no funcionales están verificados, o la desviación está documentada y aprobada
- [ ] Los endpoints figuran en la documentación OpenAPI publicada (`specs/openapi.yaml`)
- [ ] Nada de lo declarado fuera de alcance se construyó de forma encubierta
- [ ] El código está en `main` con revisión aprobada y desplegado en nube
