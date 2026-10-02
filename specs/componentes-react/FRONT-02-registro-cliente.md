# FRONT-02 — Registro de cliente (componente React)

| Campo | Valor |
|---|---|
| Spec de interfaz | `specs/front/FRONT-02-registro-cliente.md` |
| Specs de backend | SPEC-01 (registro), SPEC-07 (política) · SPEC-02 al terminar |
| Responsable | Por asignar |

---

## Objetivo

Un visitante sin cuenta crea su cuenta de cliente. Al terminar **no** queda con
sesión (RF-01.7): pasa a FRONT-03 a esperar el correo. Solo clientes; vendedores
y gestión los da de alta un administrador (FRONT-11).

---

## Endpoints

| Momento | Endpoint | Spec |
|---|---|---|
| Al abrir (medidor) | `GET /api/v1/password/politica` | SPEC-07 |
| Al enviar | `POST /api/v1/auth/registro` con `canalOrigen: "WEB"` | SPEC-01, SPEC-02 |

---

## Archivos propuestos

```
frontend/src/pantallas/registro/
├── RegistroPage.tsx        # pantalla: orquesta estados y navegación
├── RegistroForm.tsx        # campos, resumen de errores, foco
├── CelularField.tsx        # prefijo +51 + 9 dígitos
├── TerminosCheckbox.tsx    # casilla + enlaces (Ley 29733)
├── useRegistro.ts          # estado, validación, envío
├── registro.types.ts       # RegistroRequest, RegistroResponse
└── registro.api.ts         # postRegistro()
```

Reutiliza de FRONT-00: `PasswordStrengthMeter`, `usePoliticaPassword`,
`httpClient`, `errores`.

---

## Componentes y responsabilidad

| Componente | Responsabilidad |
|---|---|
| `RegistroPage` | Carga política, maneja estado global (inicial/cargando/éxito), en 409 muestra mensaje + enlaces |
| `RegistroForm` | Campos (nombres, apellidos, correo, celular, contraseña + medidor, confirmación, términos); valida en blur; foco al primer error |
| `CelularField` | Prefijo `+51` fijo, 9 dígitos, `inputmode="numeric"` |
| `TerminosCheckbox` | Casilla nativa obligatoria con enlaces a términos y tratamiento de datos |
| `useRegistro` | Construye `RegistroRequest` (`canalOrigen: "WEB"`), llama `api.registro`, ramifica |

---

## Estados

| Estado | Disparador | Qué se muestra |
|---|---|---|
| Inicial | al abrir | formulario vacío + medidor |
| Escribiendo | blur | errores de campo puntuales |
| Errores validación | cliente o 400/422 | errores bajo campos + resumen + foco |
| Cargando | submit | «Creando tu cuenta…» |
| Correo no disponible | `409` | mensaje + enlaces «inicia sesión»/«recuperar contraseña», form intacto |
| Error servicio | `503`/sin red | FRONT-00 §2 |
| Éxito | `201` | navega a FRONT-03 con el correo en memoria |

---

## Validaciones del cliente

| Campo | Regla | Mensaje | Origen |
|---|---|---|---|
| Nombres | obligatorio | «Escribe tu nombre.» | `RegistroRequest.required` |
| Apellidos | obligatorio | «Escribe tus apellidos.» | `RegistroRequest.required` |
| Correo | obligatorio, email | «Escribe un correo válido…» | `format: email` |
| Celular | 9 dígitos tras `+51` | «Escribe los 9 dígitos de tu celular.» | RF-01.5 |
| Contraseña | medidor (no bloquea) | FRONT-00 §3 | RF-07.x |
| Datos personales | orientativa | «No puede contener tu nombre, apellido ni correo.» | RF-07.3 |
| Confirmación | igual a contraseña (no se envía) | «Las contraseñas no coinciden.» | interfaz |
| Términos | marcada | «Debes aceptar los términos…» | RF-01.4 |

Botón siempre habilitado; si hay error de cliente, no envía y muestra errores.

---

## Textos y mensajes

| Código | Mensaje |
|---|---|
| `VALIDACION` | cada `errores[]` bajo su `campo` |
| `POLITICA_INCUMPLIDA` | FRONT-00 §3, bajo contraseña |
| `CORREO_NO_DISPONIBLE` | «No pudimos completar el registro…» (sin afirmar que el correo existe) |
| `NO_DISPONIBLE` | FRONT-00 §2 |

---

## Fuera de alcance

Registro de vendedores/gestión; login social; documento/fecha de nacimiento/
direcciones (FRONT-06); abrir sesión tras el registro.

## Hueco detectado

- **H-12**: no hay de dónde leer el texto vigente de los términos ni su versión.
