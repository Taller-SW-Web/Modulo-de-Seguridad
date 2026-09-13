# ADR-003 — Publicar el contrato en la semana 4, antes de implementar

| Campo | Valor |
|---|---|
| **Estado** | Aceptada |
| **Fecha** | Semana 4 — septiembre de 2026 |
| **Decide** | Sergio Osorio (PO) |

## Contexto

Según la matriz cruzada del curso, los seis módulos restantes dependen de este y
ninguno puede acceder a su base de datos. Nuestro primer demo funcional está
previsto para la semana 8 (Hito 3), pero los otros equipos empiezan a necesitar
autenticación mucho antes.

Si esperan a que tengamos algo funcionando, seis equipos pierden cuatro semanas.

## Decisión

El contrato se publica en la **semana 4**, antes de escribir el código que lo
implementa:

1. `specs/openapi.yaml` con todos los endpoints, los esquemas de petición y
   respuesta, y los caminos de error.
2. Un **mock server** que responde ese contrato con datos y credenciales de
   prueba conocidas.
3. El aviso a los seis equipos con la URL del contrato y del mock.

El contrato queda **congelado** salvo cambios acordados con una semana de
anticipación en el canal de integración.

## Por qué

Nuestro problema no es terminar antes que los demás: es **definir** antes que
los demás. Programar contra un contrato estable que todavía no tiene
implementación es perfectamente posible; programar contra una implementación que
todavía no tiene contrato, no.

El mock también nos disciplina a nosotros: si un endpoint es difícil de
describir en OpenAPI, casi siempre es porque está mal diseñado, y es mucho más
barato descubrirlo en la semana 4 que en la 11.

## Consecuencias

**A favor**

- Los seis equipos avanzan desde la semana 4 sin depender de nuestro calendario.
- Los desacuerdos de diseño salen en la revisión del contrato, no en la
  integración del Hito 4.
- El frontend de nuestro propio módulo también se desarrolla contra el mock, así
  que Valery no espera al backend.
- El contrato es el entregable que ningún otro grupo podrá enseñar el sábado.

**En contra**

- Cambiar el contrato después cuesta caro: obliga a avisar y a convivir dos
  versiones.
- Hay que mantener el mock sincronizado con el contrato hasta la semana 8.

**Reglas que asumimos**

| Regla | Detalle |
|---|---|
| Versionado | Todo cuelga de `/api/v1`. Un cambio incompatible obliga a `v2`, nunca a modificar `v1` |
| Aviso | Todo cambio del contrato se comunica con al menos una semana de anticipación |
| Preferencia | Añadir campos antes que renombrar o eliminar |
| Dueño único | El contrato lo modifica el Product Owner, no el responsable de cada SPEC |
| Errores | Formato uniforme `application/problem+json` (RFC 7807) en toda la API |

## Riesgo aceptado

El mayor riesgo del ciclo no es entregar tarde: es **llegar a la semana 4 con
implementación avanzada y sin contrato publicado**. En ese caso el proyecto
colectivo se retrasa. Si llegamos con el contrato publicado y ninguna línea de
código, no se retrasa nadie.
