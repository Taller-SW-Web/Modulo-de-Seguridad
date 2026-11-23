# Propuesta de contrato — FEAT-14, gestión de clientes técnicos

| Campo | Valor |
|---|---|
| **Spec** | [SPEC-19](../../funcionales/SPEC-19-clientes-tecnicos.md) · pantalla [FRONT-15](../../front/FRONT-15-admin-clientes-tecnicos.md) |
| **Propone** | Luis David Morales Brenis, 10-nov-2026 |
| **Publica** | Sergio (PO), en el lote de contrato H5 (PO-T1) |
| **Destino** | `openapi-v2.yaml`: rutas bajo `/api/v2` |

## Por qué en v2

`scripts/verificar-contratos.py` exige que el conjunto de rutas de v1 sea igual
al de la base histórica 1.0.0. Las rutas nuevas de administración van en v2,
igual que el alta administrativa estricta (`POST /api/v2/usuarios`). Ningún
módulo consumidor usa estas rutas: solo la SPA de Seguridad.

## Decisiones

| Tema | Decisión |
|---|---|
| Permiso | `integracion.gestionar`, solo `ADMIN_SISTEMA`. Uno solo para leer y escribir: hoy no hay un perfil que deba ver los clientes sin poder cambiarlos |
| Secreto | Lo genera el servidor (256 bits, base64url). Se guarda como SHA-256 hex, el formato que ya verifica `POST /auth/token` |
| Entrega | Solo en la respuesta del alta (`201`) y de la rotación (`200`), con `Cache-Control: no-store` |
| Estado | Acciones explícitas `desactivar` y `reactivar`, como `/usuarios/{id}/reactivar` en v1 |
| Scopes | `POST .../scopes` con `{scope}` y `DELETE .../scopes/{scope}`, como los roles en v1. Idempotentes |
| Error nuevo | `CLIENTE_SERVICIO_EXISTENTE` (409). Un scope fuera del catálogo es `VALIDACION` |
| Auditoría | `CLIENTE_SERVICIO_CREADO`, `CLIENTE_SERVICIO_DESACTIVADO`, `CLIENTE_SERVICIO_REACTIVADO`, `SECRETO_SERVICIO_ROTADO`, `SCOPE_CONCEDIDO`, `SCOPE_REVOCADO`; todas críticas |
| Eventos | Ninguno |

## Fragmento para `openapi-v2.yaml`

Se añade bajo `paths:` y `components.schemas:`. Comprobado con
`scripts/verificar-contratos.py` sobre una copia del contrato con el fragmento aplicado.

```yaml
paths:
  /clientes-servicio:
    get:
      tags: [Configuración]
      operationId: listarClientesServicio
      summary: Lista los clientes técnicos con sus scopes
      description: |-
        Definido por SPEC-19. Permiso `integracion.gestionar` (solo `ADMIN_SISTEMA`).
        Nunca devuelve el secreto ni su hash.
      responses:
        '200':
          description: Clientes ordenados por `clientId`
          content:
            application/json:
              schema:
                type: array
                items:
                  $ref: '#/components/schemas/ClienteServicio'
              examples:
                uno:
                  value:
                  - clientId: modulo-despacho
                    nombre: G2 Despacho y entrega
                    activo: true
                    scopes:
                    - codigo: usuarios:leer
                      audiencia: api-seguridad
                    - codigo: direcciones:leer
                      audiencia: api-seguridad
        '401':
          $ref: ./openapi.yaml#/components/responses/NoAutenticado
        '403':
          $ref: ./openapi.yaml#/components/responses/ScopeInsuficiente
    post:
      tags: [Configuración]
      operationId: crearClienteServicio
      summary: Da de alta un cliente técnico y entrega su secreto una sola vez
      description: |-
        Definido por SPEC-19 (RF-19.4, RF-19.5, RF-19.7). El servidor genera el
        secreto y guarda solo su hash. La respuesta lleva `Cache-Control: no-store`
        y es la única vez que se puede leer `clientSecret`.
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/CrearClienteServicioRequest'
            examples:
              conScope:
                value:
                  clientId: modulo-prueba-h5
                  nombre: Cliente de prueba H5
                  scopes:
                  - usuarios:leer
      responses:
        '201':
          description: Cliente creado activo
          headers:
            Cache-Control:
              schema:
                type: string
                const: no-store
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ClienteServicioConSecreto'
        '400':
          $ref: ./openapi.yaml#/components/responses/Validacion
        '401':
          $ref: ./openapi.yaml#/components/responses/NoAutenticado
        '403':
          $ref: ./openapi.yaml#/components/responses/ScopeInsuficiente
        '409':
          description: Ya existe un cliente con ese `clientId`
          content:
            application/problem+json:
              schema:
                $ref: ./openapi.yaml#/components/schemas/Problem
              examples:
                existente:
                  value:
                    type: https://g7.unmsm.pe/errores/cliente-servicio-existente
                    title: Ya existe un cliente con ese identificador
                    status: 409
                    code: CLIENTE_SERVICIO_EXISTENTE
  /clientes-servicio/{clientId}/secreto:
    post:
      tags: [Configuración]
      operationId: rotarSecretoClienteServicio
      summary: Reemplaza el secreto de un cliente técnico
      description: |-
        Definido por SPEC-19 (RF-19.6, RF-19.7). El secreto anterior deja de
        servir al instante; los tokens ya emitidos mueren en su `exp`.
      parameters:
      - $ref: '#/components/parameters/ClientId'
      responses:
        '200':
          description: Secreto nuevo, visible solo en esta respuesta
          headers:
            Cache-Control:
              schema:
                type: string
                const: no-store
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ClienteServicioConSecreto'
        '401':
          $ref: ./openapi.yaml#/components/responses/NoAutenticado
        '403':
          $ref: ./openapi.yaml#/components/responses/ScopeInsuficiente
        '404':
          $ref: ./openapi.yaml#/components/responses/NoEncontrado
  /clientes-servicio/{clientId}/desactivar:
    post:
      tags: [Configuración]
      operationId: desactivarClienteServicio
      summary: Impide que el cliente obtenga tokens nuevos
      description: Definido por SPEC-19 (RF-19.8). Idempotente.
      parameters:
      - $ref: '#/components/parameters/ClientId'
      responses:
        '204':
          description: Cliente inactivo
        '401':
          $ref: ./openapi.yaml#/components/responses/NoAutenticado
        '403':
          $ref: ./openapi.yaml#/components/responses/ScopeInsuficiente
        '404':
          $ref: ./openapi.yaml#/components/responses/NoEncontrado
  /clientes-servicio/{clientId}/reactivar:
    post:
      tags: [Configuración]
      operationId: reactivarClienteServicio
      summary: Permite de nuevo que el cliente obtenga tokens
      description: Definido por SPEC-19 (RF-19.8). Idempotente; conserva sus scopes.
      parameters:
      - $ref: '#/components/parameters/ClientId'
      responses:
        '204':
          description: Cliente activo
        '401':
          $ref: ./openapi.yaml#/components/responses/NoAutenticado
        '403':
          $ref: ./openapi.yaml#/components/responses/ScopeInsuficiente
        '404':
          $ref: ./openapi.yaml#/components/responses/NoEncontrado
  /clientes-servicio/{clientId}/scopes:
    post:
      tags: [Configuración]
      operationId: concederScopeClienteServicio
      summary: Concede un scope del catálogo
      description: |-
        Definido por SPEC-19 (RF-19.9, RF-19.10). Idempotente. Solo scopes del
        catálogo; la siguiente emisión de token ya lo incluye.
      parameters:
      - $ref: '#/components/parameters/ClientId'
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/ConcederScopeRequest'
            examples:
              rolesLeer:
                value:
                  scope: roles:leer
      responses:
        '204':
          description: Scope concedido
        '400':
          $ref: ./openapi.yaml#/components/responses/Validacion
        '401':
          $ref: ./openapi.yaml#/components/responses/NoAutenticado
        '403':
          $ref: ./openapi.yaml#/components/responses/ScopeInsuficiente
        '404':
          $ref: ./openapi.yaml#/components/responses/NoEncontrado
  /clientes-servicio/{clientId}/scopes/{scope}:
    delete:
      tags: [Configuración]
      operationId: revocarScopeClienteServicio
      summary: Revoca un scope concedido
      description: Definido por SPEC-19 (RF-19.9). Idempotente.
      parameters:
      - $ref: '#/components/parameters/ClientId'
      - name: scope
        in: path
        required: true
        schema:
          type: string
          minLength: 1
          maxLength: 50
      responses:
        '204':
          description: Scope revocado
        '401':
          $ref: ./openapi.yaml#/components/responses/NoAutenticado
        '403':
          $ref: ./openapi.yaml#/components/responses/ScopeInsuficiente
        '404':
          $ref: ./openapi.yaml#/components/responses/NoEncontrado
  /scopes:
    get:
      tags: [Configuración]
      operationId: listarCatalogoScopes
      summary: Catálogo de scopes que se pueden conceder
      description: Definido por SPEC-19 (RF-19.3). Permiso `integracion.gestionar`.
      responses:
        '200':
          description: Scopes ordenados por audiencia y código
          content:
            application/json:
              schema:
                type: array
                items:
                  $ref: '#/components/schemas/ScopeCatalogo'
              examples:
                dos:
                  value:
                  - codigo: cotizaciones:calcular
                    audiencia: api-despacho
                    descripcion: Calcular cobertura, costo y plazo de envío
                  - codigo: usuarios:leer
                    audiencia: api-seguridad
                    descripcion: Datos básicos de usuario, individuales y por lote
        '401':
          $ref: ./openapi.yaml#/components/responses/NoAutenticado
        '403':
          $ref: ./openapi.yaml#/components/responses/ScopeInsuficiente
components:
  parameters:
    ClientId:
      name: clientId
      in: path
      required: true
      schema:
        type: string
        pattern: ^[a-z0-9][a-z0-9-]{2,49}$
  schemas:
    ScopeConcedido:
      type: object
      required: [codigo, audiencia]
      properties:
        codigo:
          type: string
        audiencia:
          type: string
    ScopeCatalogo:
      type: object
      required: [codigo, audiencia]
      properties:
        codigo:
          type: string
        audiencia:
          type: string
        descripcion:
          type: [string, 'null']
    ClienteServicio:
      type: object
      required: [clientId, nombre, activo, scopes]
      properties:
        clientId:
          type: string
          pattern: ^[a-z0-9][a-z0-9-]{2,49}$
        nombre:
          type: [string, 'null']
          maxLength: 100
        activo:
          type: boolean
        scopes:
          type: array
          items:
            $ref: '#/components/schemas/ScopeConcedido'
    ClienteServicioConSecreto:
      allOf:
      - $ref: '#/components/schemas/ClienteServicio'
      - type: object
        required: [clientSecret]
        properties:
          clientSecret:
            type: string
            description: 256 bits en base64url sin relleno. Solo aparece en esta respuesta.
            minLength: 43
    CrearClienteServicioRequest:
      type: object
      additionalProperties: false
      required: [clientId, nombre]
      properties:
        clientId:
          type: string
          pattern: ^[a-z0-9][a-z0-9-]{2,49}$
        nombre:
          type: string
          minLength: 1
          maxLength: 100
        scopes:
          type: array
          uniqueItems: true
          items:
            type: string
            minLength: 1
            maxLength: 50
    ConcederScopeRequest:
      type: object
      additionalProperties: false
      required: [scope]
      properties:
        scope:
          type: string
          minLength: 1
          maxLength: 50
```

## Cambios fuera de `openapi-v2.yaml`

- [SPEC-11](../../funcionales/SPEC-11-roles-permisos.md): permiso `integracion.gestionar` para `ADMIN_SISTEMA`. En el backend se añade a `R__catalogo_autorizacion.sql` y a `CatalogoAutorizacion.java`.
- [SPEC-12](../../funcionales/SPEC-12-registro-auditoria.md): las seis acciones auditables, críticas. En el backend, en `AccionAuditable`.
- [Catálogo de errores](../../catalogo-errores.md): `CLIENTE_SERVICIO_EXISTENTE`.
- Sin migración de esquema: `cliente_servicio`, `cliente_servicio_scope` y `scope` ya tienen lo necesario.
