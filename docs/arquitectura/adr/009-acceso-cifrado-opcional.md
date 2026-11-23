# ADR-009 — Acceso cifrado opcional para la SPA de Seguridad

| Campo | Valor |
|---|---|
| **Estado** | Aceptada |
| **Fecha** | 10-nov-2026 (H5, FEAT-16) |
| **Decide** | Luis David Morales Brenis, por delegación del equipo |

## Contexto

En la presentación de H4 el profesor observó que, en la pestaña de red del
navegador, el `accessToken` deja leer `roles`, `permisos` y `email`. Alterar
esos claims no sirve de nada, porque rompe la firma RS256 y el token se
rechaza ([ADR-001](001-rs256-frente-a-hs256.md)), pero pidió que no se vean.

Seis módulos validan hoy ese mismo token en local con el JWKS
([ADR-002](002-validacion-local-frente-a-introspeccion.md)) y leen `roles`.
Cifrarlo para todos rompería esa validación y obligaría a `/api/v2`.

## Decisión

El cifrado es **opcional y lo pide el cliente**:

- `POST /auth/login`, `/auth/otp/verificar` y `/auth/refresh` aceptan
  `formatoAcceso: CIFRADO`. Sin el campo, o con `JWS`, la respuesta no cambia.
- Con `CIFRADO`, el JWS RS256 de siempre se envuelve en un JWE compacto
  (`alg: dir`, `enc: A256GCM`, `cty: JWT`). La clave es simétrica, de 256 bits,
  y sale de `AUTH_CLAVE_CIFRADO_ACCESO`; solo esta API la conoce.
- La API acepta ambos formatos en todas sus rutas protegidas y en la
  introspección: descifra y verifica la firma interna con las mismas reglas.
- `SesionIniciada.usuario` añade `permisos`, para que el cliente no necesite
  leer el token.
- La SPA de Seguridad pide siempre `CIFRADO`.

## Consecuencias

- **Compatible con lo publicado.** Los módulos consumidores no cambian: no piden
  el formato y siguen recibiendo y validando el JWS. Es un cambio aditivo de v1
  (contrato 1.2.0).
- **Un acceso cifrado solo lo valida Seguridad.** No sirve para la validación
  local de otro módulo; si un módulo lo necesitara, debería usar la
  introspección o seguir pidiendo el JWS.
- **No oculta los datos al propio usuario.** La respuesta del login sigue
  mostrando su nombre, correo, roles y permisos: son suyos y la interfaz los
  necesita. Lo que deja de verse es el contenido del token que viaja en cada
  petición.
- **Degradación segura.** Si falta la clave de cifrado, se emite el JWS y se
  registra un aviso al arrancar; el login no se bloquea. Un JWE recibido sin
  clave configurada es un token inválido.
- **Tamaño.** El JWE crece alrededor de un tercio respecto del JWS; sigue lejos
  del límite de 4 KB de [SPEC-11](../../../specs/funcionales/SPEC-11-roles-permisos.md).

## Alternativas descartadas

- **Cifrar siempre:** rompe la validación local de los seis módulos; exigiría v2.
- **Token mínimo sin roles ni permisos:** obliga a consultar los permisos en cada
  petición y cambia lo que ya consumen los módulos.
- **Cifrado asimétrico (`RSA-OAEP`):** solo tendría sentido si otro módulo
  necesitara descifrar; hoy nadie lo necesita.
