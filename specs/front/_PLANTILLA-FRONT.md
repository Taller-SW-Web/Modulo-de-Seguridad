# FRONT-NN — <Nombre de la pantalla>

| Campo | Valor |
|---|---|
| **Responsable** | <Nombre del integrante> |
| **Specs de backend que consume** | SPEC-XX, SPEC-YY |
| **Wireframe** | <enlace a Figma> |
| **Estado** | Borrador / En revisión / **Aprobada** |
| **Aprobada por** | <PO> el <fecha> |

> **Cómo usar esta plantilla.** Copia este archivo a
> `specs/front/FRONT-NN-nombre-corto.md`, borra los comentarios en cursiva y
> rellena las secciones. Una spec de interfaz **no inventa reglas de negocio**:
> las toma de sus specs de backend. Si la pantalla necesita algo que el backend
> no ofrece, primero se cambia la spec de backend.

---

## Objetivo — ¿para qué sirve esta pantalla?

*Quién llega a esta pantalla, qué quiere conseguir y desde dónde llega. Dos o
tres frases.*

---

## Endpoints que usa

*Qué endpoints llama la pantalla y en qué momento. Los contratos están en
`specs/openapi.yaml`; aquí solo se dice cuándo se llama cada uno.*

| Momento | Endpoint | Spec de backend |
|---|---|---|
| Al enviar el formulario | `POST /api/v1/…` | SPEC-XX |

---

## Estados de la pantalla

*Cada estado en el que puede estar la pantalla, con lo que ve el usuario.
Mínimo: inicial, cargando, error y éxito.*

| Estado | Cuándo ocurre | Qué ve el usuario |
|---|---|---|
| Inicial | … | … |
| Cargando | … | … |
| Error | … | … |
| Éxito | … | … |

---

## Validaciones del lado del cliente

*Solo ayudan al usuario a no equivocarse; el backend vuelve a validar siempre.
Cada regla sale de una spec de backend, que se cita.*

| Campo | Regla | Mensaje | Origen |
|---|---|---|---|
| … | … | … | RF-XX.N |

---

## Textos y mensajes

*Los mensajes que ve el usuario para cada código de error que puede devolver el
backend. **Ningún mensaje puede revelar si una cuenta existe o está bloqueada**:
se usan los mismos mensajes genéricos que exige el backend.*

| Código del backend | Mensaje en pantalla |
|---|---|
| `CREDENCIALES_INVALIDAS` | … |

---

## Escenarios de interfaz — ¿cómo verificamos?

*Formato Dado / Cuando / Entonces, desde el punto de vista de quien usa la
pantalla. Mínimo uno por estado y al menos uno de caso borde (sin conexión,
doble envío, sesión vencida…).*

### UI-NN.1 <Nombre del camino feliz>
- **Dado** que …
- **Cuando** …
- **Entonces** la pantalla muestra …

### UI-NN.2 <Nombre del caso borde> *(caso borde)*
- **Dado** que …
- **Cuando** …
- **Entonces** la pantalla muestra …

---

## Accesibilidad y diseño adaptable

*Contraste, navegación con teclado, etiquetas para lectores de pantalla, y cómo
se ve en móvil y en escritorio.*

- …

---

## Fuera de alcance — ¿qué NO hará?

- …

---

## Lista de completitud

- [ ] Cada estado de la pantalla está implementado
- [ ] Cada código de error del backend tiene su mensaje, sin revelar datos de cuentas
- [ ] Las validaciones del cliente coinciden con las de la spec de backend
- [ ] Los escenarios de interfaz están automatizados o verificados a mano con evidencia
- [ ] El wireframe y la implementación coinciden
