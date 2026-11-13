# FEAT-16 — Token de acceso con formato cifrado

Responsable: Luis David Morales Brenis. Corte: 10-nov-2026, America/Lima.
Estado: **T1 a T4 terminadas; recorrido desplegado comprobado en los seis puntos**.
Decisión en [ADR-009](../../arquitectura/adr/009-acceso-cifrado-opcional.md).

## Origen

Observación del profesor tras H4: en la pestaña Network de la SPA se leían
`roles`, `permisos` y `email` dentro del `accessToken`. Ahora la SPA pide
`formatoAcceso: CIFRADO` y recibe un JWE (`alg: dir`, `enc: A256GCM`, `cty: JWT`)
que envuelve el JWS RS256 de siempre. Los módulos que no piden el formato siguen
recibiendo el JWS y validándolo con el JWKS.

## Fuentes y versiones

| Componente | PR fusionado | Commit | URL |
|---|---|---|---|
| Documentación y contrato 1.2.0 | [#87](https://github.com/Taller-SW-Web/Modulo-de-Seguridad/pull/87) | `0213298d8d5123aadb66b7c188913ab3c41fb38f` en `main` | — |
| Backend | [#85](https://github.com/Taller-SW-Web/Modulo-de-Seguridad-Backend/pull/85) | `bc1e677d1c2301c7e76457f59bf8c2512bc53ad3` | https://g7-auth-service.onrender.com |
| Frontend | [#23](https://github.com/Taller-SW-Web/G7-Modulo-de-Seguridad-Frontend/pull/23) | `3c3aef1603b3d9dfe8474643b24504be421cbb2d` | https://g7-seguridad-spa.onrender.com |

David confirmó en Render que ambos commits están en Live. El log del backend al
arrancar muestra «identidad: cifrado opcional de accesos habilitado (dir/A256GCM)»,
lo que confirma que `AUTH_CLAVE_CIFRADO_ACCESO` está configurada. La clave no
aparece en el repositorio ni en esta evidencia.

## Pruebas automatizadas

- Backend: `AccesoCifradoTest` (nueva) cubre la emisión por defecto sin cambios,
  que el JWE no deja leer los claims sin la clave, que ambos formatos autorizan igual,
  y que se rechazan un JWE alterado, uno cifrado con otra clave y un JWE recibido
  sin clave configurada. `LoginHttpTest` añade el login con `formatoAcceso`.
  [CI del PR en verde](https://github.com/Taller-SW-Web/Modulo-de-Seguridad-Backend/actions/runs/42884223967).
- Frontend: `almacenSesion.test.ts` (nueva) y ajustes en `http.test.ts` y
  `mfa-api.test.ts`: permisos tomados de la respuesta, `formatoAcceso` en login,
  OTP y refresh, y vencimiento sin leer el token.
  [CI en verde sobre `3c3aef1`](https://github.com/Taller-SW-Web/G7-Modulo-de-Seguridad-Frontend/actions/runs/42891290472).
- E2E aislado: el job «E2E Playwright en entorno aislado» del backend pasa a usar
  la SPA de `develop` (`3c3aef1`) con una clave de cifrado temporal, de modo que
  los casos E2E recorren el acceso cifrado.
  [Backend #86, CI en verde](https://github.com/Taller-SW-Web/Modulo-de-Seguridad-Backend/actions/runs/42896109358).
- Contrato: `scripts/verificar-contratos.py` valida 1.2.0 y confirma que los únicos
  cambios en peticiones v1 son el campo opcional `formatoAcceso`.

## Recorrido desplegado

Ejecutor: David, 10-nov-2026, hora de Lima, con las herramientas de desarrollo
del navegador abiertas en la pestaña Network. Cuentas de prueba de G7: cuenta C
(UUID `00000000-0000-4000-8000-00000000f00f`, CLIENTE sin MFA) y una cuenta
ADMIN_SISTEMA con MFA.

Las capturas están en el reporte PDF de David, que se conserva fuera del repositorio
porque muestra contraseñas en el payload del login, un refresh token y correos de
terceros. En el repositorio solo se registran los resultados.

| # | Paso | Resultado observado |
|---|---|---|
| 1 | Login de C | `POST /auth/login` envía `formatoAcceso: "CIFRADO"`. La respuesta trae un `accessToken` de 5 partes con la segunda vacía (`..`) y `usuario.permisos` |
| 2 | Token ilegible | jwt.io no decodifica el token: solo admite JWS compacto de 3 partes (RFC 7515). No se leen `roles`, `permisos` ni `email` |
| 3 | Mi cuenta | `GET /auth/me` y `/direcciones` llevan el JWE en `Authorization: Bearer` y responden 200 con el perfil |
| 4 | Login ADMIN_SISTEMA | Después de login → solicitar → verificar OTP, la respuesta trae el JWE, el rol `ADMIN_SISTEMA` y los 8 permisos de administración. `/admin/usuarios` carga. La SPA eligió el destino con los permisos de la respuesta |
| 5 | Renovación | A los 15 min (`expiresIn: 900`), `GET /auth/me` responde 401 `TOKEN_INVALIDO`. El interceptor envía `POST /auth/refresh` con `formatoAcceso: "CIFRADO"`, que responde 200 con un JWE nuevo, y el reintento de `me` responde 200 |
| 6 | Compatibilidad | En PowerShell, un login sin `formatoAcceso` con la cuenta C devuelve un token de 3 partes (JWS) y `usuario.permisos`: `cliente.buscar, cliente.registrar` |

El punto 6 confirma que los consumidores que no piden el formato reciben el JWS
de siempre, que siguen validando con el JWKS.

## Despliegue por pipeline

En el run de `develop` [42889656042](https://github.com/Taller-SW-Web/Modulo-de-Seguridad-Backend/actions/runs/42889656042)
para `bc1e677` pasaron build, imagen y E2E aislado. El job «Backend - despliegue a
Render» falló porque Render respondió 202 y avisó que había otro despliegue en curso.
Coincidió con otro despliegue lanzado desde Render en ese momento. El
script lo rechaza a propósito para no verificar el despliegue de otro commit. David
desplegó `bc1e677` a mano y lo confirmó en Render. El smoke de ese run quedó
*skipped*: la evidencia de deploy y smoke por pipeline queda para el siguiente push
a `develop`.

## Límites

- La respuesta del login sigue mostrando al propio usuario su nombre, correo,
  roles y permisos (ADR-009, consecuencias). Lo que deja de verse es el contenido
  del token que viaja en cada petición.
- El JWE solo lo acepta la API de Seguridad. Los demás módulos siguen con el JWS
  hasta que decidan otra cosa.
- No se probó en nube un JWE alterado ni cifrado con otra clave. Esos rechazos
  están cubiertos por `AccesoCifradoTest`.
