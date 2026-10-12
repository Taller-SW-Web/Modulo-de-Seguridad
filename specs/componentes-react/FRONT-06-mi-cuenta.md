# FRONT-06 — Mi cuenta: perfil y direcciones (componente React)

| Campo | Valor |
|---|---|
| Spec de interfaz | `specs/front/FRONT-06-mi-cuenta.md` |
| Specs de backend | SPEC-16 (atributos) · SPEC-18 (direcciones) · enlaza a SPEC-10/07/13 |
| **Responsable** | Valery Cristin Gutierrez Bendezu |
| **Épica principal** | [EP-08](../epicas/EP-08-atributos-perfil.md) |

> **Corte de versión, 11-oct-2026:** H-03: celular cambiado queda no verificado por A2; no ofrecer OTP de login para verificarlo. H-04: direcciones solo listado/creación. H-13: documento no editable por titular sin atributos.editar. [Tratamiento vigente](../../docs/integracion/preparacion-interna.md).

---

## Objetivo

Cualquier usuario con sesión ve y actualiza sus datos: nombre, celular, correo,
documento (enmascarado) y, si es cliente, sus direcciones. Es el destino por
defecto tras iniciar sesión para quien no administra (FRONT-00 §1.4) y la puerta
a la seguridad de la cuenta (FRONT-09, 10, 13) y a cerrar sesión.

---

## Endpoints

| Momento | Endpoint | Spec |
|---|---|---|
| Al abrir | `GET /api/v1/auth/me` | SPEC-16 (RF-16.1) |
| Al abrir, si `CLIENTE` | `GET /api/v1/usuarios/{id}/direcciones` (id de `/auth/me`) | SPEC-16, SPEC-18 |
| Al guardar datos/celular | `PATCH /api/v1/usuarios/{id}/atributos` | SPEC-16 (RF-16.2) |
| Al confirmar cambio de correo | `POST /api/v1/usuarios/{id}/correo` | SPEC-16 (RF-16.6) |
| Al guardar dirección | `POST /api/v1/usuarios/{id}/direcciones` | SPEC-16 (RF-16.5) |

---

## Archivos propuestos

```
frontend/src/pantallas/mi-cuenta/
├── MiCuentaPage.tsx            # orquesta secciones y estados
├── SeccionDatosPersonales.tsx  # nombres, apellidos (edición en línea)
├── SeccionContacto.tsx         # correo (diálogo) + celular
├── SeccionDocumento.tsx        # tipo + documentoEnmascarado (solo lectura)
├── SeccionSeguridad.tsx        # enlaces a FRONT-09/10/13 + «Cerrar sesión»
├── SeccionDirecciones.tsx      # solo CLIENTE
├── DialogoCambioCorreo.tsx     # correo nuevo + contraseña actual
├── FormularioDireccion.tsx     # nueva dirección
└── useMiCuenta.ts              # carga /auth/me, direcciones, PATCH, correo, dirección
```

Reutiliza de FRONT-00: `api`, `errores`, `sesion` (cerrar sesión), `CelularField`
(de FRONT-02), `EstadoCuentaBadge`.

---

## Componentes y responsabilidad

| Componente | Responsabilidad |
|---|---|
| `MiCuentaPage` | Carga `/auth/me` (+ direcciones si `CLIENTE`); una sola sección en edición a la vez; avisa «Cambios guardados.» |
| `SeccionDatosPersonales` | Nombres/apellidos con «Editar»/«Guardar»/«Cancelar»; el `PATCH` envía **solo los campos cambiados** |
| `SeccionContacto` | Muestra correo «Verificado» y celular «Verificado»/«Sin verificar»; celular editable; lanza el diálogo de correo |
| `DialogoCambioCorreo` | Correo nuevo + contraseña actual; atrapa el foco, cierra con Escape, devuelve foco |
| `SeccionDocumento` | Tipo y `documentoEnmascarado` (`*****234`), solo lectura; «Sin registrar» si no existe (H-13) |
| `SeccionSeguridad` | Enlaces a FRONT-09/10/13 y botón «Cerrar sesión» |
| `SeccionDirecciones` | Lista (etiqueta, dirección, distrito, provincia, departamento, referencia; «Predeterminada») y «Añadir dirección» (solo añadir, H-04) |
| `FormularioDireccion` | Campos + casilla «Usar como predeterminada» |
| `useMiCuenta` | Lógica de carga, `PATCH` de campos modificados, cambio de correo y alta de dirección |

---

## Estados

| Estado | Disparador | Qué se muestra |
|---|---|---|
| Cargando | al abrir | esqueleto de secciones |
| Datos cargados | `200` `/auth/me` | secciones |
| Error de carga | fallo ≠ `401` | FRONT-00 §2 + «Reintentar» |
| Editando | «Editar» | campos + «Guardar»/«Cancelar» (una sección a la vez) |
| Guardando | «Guardar» | «Guardando…» |
| Guardado | `200` PATCH | vuelve a lectura con datos del backend + «Cambios guardados.» |
| Celular cambiado | `200` PATCH con celular nuevo | etiqueta «Sin verificar» (RF-16.7) |
| Cambio de correo — diálogo | «Cambiar correo» | diálogo |
| Cambio de correo — pendiente | `202` `/correo` | aviso persistente hasta recargar |
| Direcciones vacías | `direcciones: []` | «Aún no tienes direcciones guardadas.» + «Añadir dirección» |
| Nueva dirección | «Añadir dirección» | formulario |
| Dirección guardada | `201` | recarga lista; la anterior pierde «Predeterminada» (ESC-16.9) |

---

## Validaciones del cliente

| Campo | Regla | Mensaje | Origen |
|---|---|---|---|
| Nombres/apellidos | no vacíos | «Este campo no puede quedar vacío.» | interfaz |
| Celular | 9 dígitos tras `+51` | «Escribe los 9 dígitos de tu celular.» | `^\+51[0-9]{9}$` |
| Correo nuevo | obligatorio, email, distinto del actual | «Escribe un correo válido.» / «Es el mismo correo que ya usas.» | `/usuarios/{id}/correo` |
| Contraseña actual | obligatoria | «Escribe tu contraseña actual.» | RF-16.6 |
| Departamento/provincia/distrito/dirección | obligatorios | «Este campo es obligatorio.» | `Direccion.required` |

El `PATCH` envía **solo los campos que cambiaron** (RF-16.9).

---

## Textos y mensajes

| Código | Mensaje |
|---|---|
| `202` `/correo` | «Te enviamos un enlace a {nuevo}. Hasta confirmarlo, entras con {actual}. También avisamos a tu correo actual.» |
| `CREDENCIALES_INVALIDAS` `/correo` | «La contraseña actual no es correcta.» (no cierra sesión) |
| `CORREO_NO_DISPONIBLE` | «No puedes usar ese correo. Prueba con otro.» |
| `ATRIBUTO_NO_APLICABLE` | «No pudimos guardar ese dato en tu cuenta.» |
| `SCOPE_INSUFICIENTE` / `VALIDACION` / `NO_DISPONIBLE` | FRONT-00 §2 |

---

## Accesibilidad

- Cada sección `<section>` con encabezado, navegable por encabezados.
- El diálogo de correo atrapa el foco, cierra con Escape, devuelve el foco.
- «Verificado»/«Sin verificar»/«Predeterminada» son texto, no solo icono.
- El documento enmascarado se anuncia «documento terminado en 234».
- 390 px: secciones apiladas; direcciones como tarjetas.

## Fuera de alcance

Cambiar contraseña (FRONT-10); cambiar roles/estado; autobaja (Q10);
editar/eliminar direcciones (H-04); verificar celular (H-03); documento completo.

## Límites y estado contractual vigente

- **H-01:** contrato local v1 1.1.0 devuelve mfaHabilitado y atributos aplicables
  al titular. Mostrar fechaNacimiento para CLIENTE y codigoVendedor/tienda/
  fechaIngreso para VENDEDOR como lectura; ambos conjuntos si tiene ambos roles.
  Ausente/null se muestra «Sin registrar». Publicación y servidor pendientes.
- **H-03:** celular cambiado no verificado; A2 excluye verificación externa.
- **H-04:** solo listado/creación de direcciones.
- **H-13:** titular modifica nombres/apellidos/celular. Documento, fechaNacimiento
  y atributos de vendedor requieren edición administrativa con atributos.editar.
  En esta pantalla se leen, no se editan. Documento enmascarado.
- Segundo factor se modifica mediante FRONT-09 y contrato v2; no interruptor
  conectado a las rutas históricas v1 incompletas.

## Selección de alcance H3 — 12-oct

H3 muestra perfil propio y edición nombres/apellidos/celular, más documento protegido de solo lectura. No carga direcciones automáticamente ni presenta cambio de correo/alta dirección/configuración MFA pendientes. Los enlaces de contraseña, actividad y logout consumen las capacidades reales de sus dueños. El comportamiento completo de estas secciones sigue para incrementos posteriores.

[Planes por estudiante](../../plan/hito-3/README.md). Selección H3 aprobada por los dueños según comunicación de Luis el
12-oct; no cambia reglas aplicables ni acredita implementación de la UI.
