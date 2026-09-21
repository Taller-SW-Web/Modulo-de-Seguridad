# SPEC-0X — <Nombre de la funcionalidad>

| Campo | Valor |
|---|---|
| **Responsable** | <Nombre del integrante> |
| **Hito objetivo** | Hito X (Sem. X) |
| **Estado** | Borrador / En revisión / **Aprobada** |
| **Aprobada por** | <PO> el <fecha> |

> **Plantilla de specs de backend.** Las de interfaz usan [`front/_PLANTILLA-FRONT.md`](front/_PLANTILLA-FRONT.md).
>
> **Cómo usar esta plantilla.** Copia este archivo a
> `specs/SPEC-0X-nombre-corto.md`, borra los comentarios en cursiva y rellena las
> siete secciones. **Una spec a la que le falte una sección no se aprueba.**
> Si al escribirla no puedes imaginar una demostración de dos minutos que la
> muestre funcionando, es demasiado grande: pártela. Si la implementarías en
> menos de un día, probablemente es un requisito de otra spec y no una propia.

---

## Contexto — ¿por qué?

*Por qué existe esta capacidad dentro del marketplace y qué problema aparecería
si no existiera. Conecta con la matriz cruzada del curso: qué otro módulo se ve
afectado si esto falta o falla.*

---

## Propósito — ¿para qué?

*Qué resuelve, quién la usa y cuál es el resultado observable que produce. Dos o
tres frases, no una lista.*

---

## Alcance — ¿hasta dónde?

*Qué queda dentro de esta unidad de trabajo. Enumera los flujos concretos que
cubre.*

---

## Requisitos — ¿qué debe hacer?

*Afirmaciones verificables, numeradas y sin ambigüedad. Nada de «el sistema debe
ser rápido» o «debe ser seguro»: eso va en no funcionales y con un número.*

| Código | Requisito |
|---|---|
| RF-0X.1 | El sistema debe… |
| RF-0X.2 | El sistema debe… |
| RF-0X.3 | El sistema debe… |

---

## Escenarios — ¿cómo verificamos?

*Formato Dado / Cuando / Entonces. **Mínimo dos escenarios por requisito clave,
y al menos uno de ellos debe ser un caso borde** (condición límite, error,
concurrencia, dato ausente o entrada maliciosa). Estos escenarios son, tal cual,
el plan de pruebas del Hito 5: se traducen casi línea a línea a pruebas
automatizadas.*

### ESC-0X.1 <Nombre del camino feliz>
- **Dado** que …
- **Cuando** …
- **Entonces** el sistema … y responde 2XX con …

### ESC-0X.2 <Nombre del caso borde> *(caso borde)*
- **Dado** que …
- **Cuando** …
- **Entonces** el sistema … y responde 4XX con …

### ESC-0X.3 …

---

## Requisitos no funcionales — ¿con qué condiciones?

*Rendimiento con número y percentil, seguridad, límites operativos y
restricciones legales. Los tres primeros aplican a casi todas las specs de este
módulo:*

- El tiempo de respuesta debe ser menor a <N> ms en el percentil 95.
- Los mensajes de error no deben permitir enumerar cuentas existentes.
- Ningún registro de bitácora debe contener contraseñas, códigos OTP ni el
  número de documento en claro.
- El tratamiento de datos personales debe alinearse con la Ley N.º 29733 de
  Protección de Datos Personales.

---

## Fuera de alcance — ¿qué NO hará?

*Lo que se decide explícitamente no construir, para que ningún otro equipo lo
asuma por interpretación. Si dudas si algo entra o no, escríbelo aquí y
pregúntale al PO.*

- …
- …

---

## Impacto en el contrato

*¿Esta spec añade, modifica o elimina endpoints de `specs/openapi.yaml`? ¿Cambia
algún claim del JWT? ¿Publica algún evento nuevo en RabbitMQ?*

**Si la respuesta a cualquiera es sí, el cambio lo aplica el Product Owner, no
el responsable de la spec.** El contrato tiene dueño único porque seis equipos
programan contra él.

| Endpoint / evento | Añade, modifica o elimina | Acordado con el PO |
|---|---|---|
| `POST /api/v1/…` | Añade | ⬜ |

---

## Lista de completitud

La spec se cierra cuando:

- [ ] Todos los requisitos están implementados
- [ ] Cada requisito clave tiene al menos dos escenarios automatizados, uno de ellos caso borde
- [ ] Los no funcionales están verificados, o la desviación está documentada y aprobada
- [ ] Los endpoints nuevos figuran en la documentación OpenAPI publicada
- [ ] Nada de lo declarado fuera de alcance se construyó de forma encubierta
- [ ] El código está en `main` con revisión aprobada y desplegado en nube
