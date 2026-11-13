# Propuesta de contrato — FEAT-07-T5, código vencido y reutilizado en el login

| Campo | Valor |
|---|---|
| **Spec** | [SPEC-09](../../funcionales/SPEC-09-mfa-inicio-sesion.md), ESC-09.6 y ESC-09.10 |
| **Propone** | Luis David Morales Brenis, 12-nov-2026 |
| **Publica** | Sergio (PO) |
| **Destino** | `openapi.yaml`, `POST /auth/otp/verificar`. Solo cambian descripciones |
| **Compatibilidad** | Sin cambio de rutas, esquemas ni códigos. Ningún módulo consumidor llama a este endpoint: solo la SPA de Seguridad |

## Por qué

En el despliegue, el 12-nov, un código correcto presentado después de 5 minutos
y un código ya canjeado respondieron `401 TOKEN_INVALIDO`
([evidencia](../../../docs/evidencia/h5/FEAT-07.md)). Las descripciones actuales
del contrato llevan a pensar otra cosa:

- **Código vencido.** El `410` dice «El código tiene más de 5 minutos». Pero en el
  login el desafío dura 300 s desde el login y el código 300 s desde que se pide,
  así que el desafío siempre vence primero. El `410 OTP_EXPIRADO` se sigue
  usando en el cambio del segundo factor (SPEC-10, ESC-10.15).
- **Código reutilizado.** Un código correcto consume también el desafío, así que
  el segundo intento falla por el desafío.

La SPA ya trata `401 TOKEN_INVALIDO` en esta pantalla como desafío vencido y
vuelve al login (FRONT-04, estado «Desafío vencido»). No requiere cambios.

## Fragmento para `openapi.yaml`, en `/auth/otp/verificar`

Respuesta `401`, la descripción completa:

```yaml
          description: |
            Código incorrecto (`CODIGO_INVALIDO`), se agotaron los 3 intentos del
            código (`OTP_INTENTOS_AGOTADOS`), o el `challengeToken` no existe,
            venció, ya se canjeó o fue manipulado (`TOKEN_INVALIDO`, ESC-09.11).
            Un código correcto presentado después de 5 minutos (ESC-09.6) o por
            segunda vez (ESC-09.10) recibe `TOKEN_INVALIDO`: el desafío vence
            antes que el código y se consume con la primera verificación correcta.
```

Respuesta `410`, la descripción completa:

```yaml
          description: |
            El código tiene más de 5 minutos y el desafío sigue vigente. No cambia
            el contador de la cuenta. En el inicio de sesión no se alcanza, porque
            el desafío vence antes (`401 TOKEN_INVALIDO`, ESC-09.6). Se conserva
            por compatibilidad.
```

Ya está corregido en esta misma rama: SPEC-09 y el catálogo de errores.
