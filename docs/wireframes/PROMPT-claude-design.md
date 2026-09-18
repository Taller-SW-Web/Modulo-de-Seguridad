# Prompt para Claude Design — Wireframes del Hito 1

**Para:** Valery (Frontend y Diseño) · **Apoyo en panel admin:** Christian

> **Actualizado el 18 de septiembre.** Ya hay una primera versión completa en
> Stitch (proyecto `889177073946089595`, 47 pantallas, todas las specs salvo
> SPEC-09). Este prompt se corrigió para que coincida con las specs actuales; si
> lo reutilizas, las specs mandan sobre él.

## Antes de pegarlo: dos cosas

1. **Esto son wireframes, no mockups.** El Hito 1 pide estructura: qué hay en
   cada pantalla, en qué orden y en qué estado. La paleta de colores, la
   tipografía y el sistema de diseño son del **Hito 2** y los decides tú. Por eso
   el prompt pide explícitamente escala de grises: si Claude Design propone
   colores ahora, condiciona tu trabajo de la semana 6.
2. **Después hay que pasarlo a Figma.** El curso exige Figma. Claude Design
   sirve para llegar a la reunión del sábado con las ocho pantallas resueltas y
   discutidas; el trabajo en Figma parte de ahí en vez de partir de cero.

---

## El prompt

Copia todo lo que sigue.

---

Necesito los wireframes de un módulo de seguridad y autenticación de usuarios
para un marketplace de productos deportivos. Es un trabajo universitario: el
entregable de esta semana son wireframes de baja fidelidad, no mockups.

**Restricciones de fidelidad, importantes:**

- Escala de grises únicamente. Nada de paleta de marca, gradientes ni sombras
  decorativas. El color se decide más adelante y no quiero que estos wireframes
  lo condicionen.
- Bloques, cajas de texto y etiquetas. Usa texto real en español, no lorem ipsum:
  los mensajes de error concretos son parte de lo que hay que revisar.
- Cada pantalla, un artboard de escritorio de 1440 px de ancho. Añade debajo de
  cada uno una variante móvil de 390 px solo para las pantallas 1, 2 y 4.
- Marca los estados alternativos como artboards aparte, no como notas al margen.

**Contexto del producto:**

Es el proveedor de identidad de todo el marketplace. Hay seis roles: cliente,
vendedor y cuatro roles de gestión (ADMIN_VENTAS, GESTOR_DESPACHO,
GESTOR_COMERCIAL y ADMIN_SISTEMA). El cliente se registra solo; al resto los da
de alta un ADMIN_SISTEMA. La autenticación es con correo y contraseña, con un
segundo factor por código de 6 dígitos que es obligatorio para los cuatro roles
de gestión y opcional para el resto.

**Las ocho pantallas:**

1. **Inicio de sesión.** Campos de correo y contraseña, botón de entrar, enlace
   de «olvidé mi contraseña» y enlace de registro. Tres artboards de estado:
   (a) normal, (b) credenciales inválidas — el mensaje debe ser genérico y no
   revelar si el correo existe, (c) contraseña caducada — solo para roles de
   gestión, con salida al flujo de recuperación. **No hay artboard de «cuenta
   bloqueada»**: una cuenta bloqueada, inactiva o sin verificar ve exactamente
   el mismo mensaje que (b); el titular se entera del bloqueo por correo.

2. **Registro de cliente.** Nombres, apellidos, correo, celular, contraseña y
   confirmación. Bajo el campo de contraseña, un medidor de fuerza con la lista
   de reglas que se van marcando: mínimo 10 caracteres, mayúscula, minúscula,
   dígito, carácter especial, que no sea común y que no contenga el nombre ni
   el correo. Dos artboards: (a) formulario vacío, (b)
   formulario con errores de validación por campo.

3. **Verificación de correo.** Tres artboards: (a) «revisa tu correo», con el
   correo enmascarado y un botón de reenviar; (b) verificación correcta con
   acceso al inicio de sesión; (c) enlace expirado a las 24 horas, con la opción
   de pedir uno nuevo y sin exponer datos de la cuenta.

4. **Desafío de código de un solo uso.** Seis casillas para el código, cuenta
   atrás de los 5 minutos de validez, enlace para reenviar y contador de
   intentos restantes sobre un máximo de 3. Dos artboards: (a) esperando el
   código, (b) código incorrecto con un intento consumido.

5. **Recuperar contraseña.** Dos artboards: (a) pedir el correo, con un mensaje
   de confirmación idéntico exista o no la cuenta; (b) definir la contraseña
   nueva, con el mismo medidor de fuerza de la pantalla 2.

6. **Mi cuenta.** Datos personales editables, número de documento mostrado
   enmascarado como `*****234` y no editable, teléfono verificado o sin
   verificar, un interruptor para activar el segundo factor, y una sección de
   direcciones con listado, dirección predeterminada y botón de añadir.

7. **Panel de administración — listado de usuarios.** Tabla con correo, nombre,
   rol y estado (sin «último acceso»: la API no lo devuelve). Filtros por rol y
   por estado, buscador y paginación. Acciones por fila: ver, bloquear,
   desbloquear, dar de baja y reactivar. Botón destacado de crear vendedor o
   personal de gestión. Los cuatro estados posibles de
   una cuenta deben distinguirse sin depender del color, porque estos wireframes
   son en gris: usa etiquetas de texto. Los estados son: activo, pendiente de
   verificación, bloqueado e inactivo.

8. **Panel de administración — detalle de usuario.** Datos de la cuenta, roles
   asignados con opción de asignar y revocar, historial de los últimos intentos
   de acceso con fecha, IP y resultado, historial de bloqueos con su motivo, y
   botones de bloquear manualmente y desbloquear. El bloqueo manual debe pedir un
   motivo obligatorio.

**Dos reglas de contenido que atraviesan todas las pantallas:**

- Ningún mensaje de error puede permitir averiguar si un correo está registrado.
  Redacta esos mensajes con cuidado: son parte del entregable.
- Ninguna pantalla muestra el número de documento completo ni nada parecido a
  una contraseña.

Empieza por las pantallas 1, 2 y 4, que son las que enseñamos primero.

---

## Después de generarlo

- [ ] Revisar con Juan José las pantallas 2 y 5 (su SPEC-03)
- [ ] Revisar con Luis David la pantalla 4 (su SPEC-04)
- [ ] Revisar con Christian las pantallas 7 y 8 (él las implementa)
- [ ] Exportar a PDF en esta carpeta, con el nombre `wireframes-hito-1.pdf`
- [ ] Crear el archivo de Figma y dejar el enlace en `docs/wireframes/README.md`
