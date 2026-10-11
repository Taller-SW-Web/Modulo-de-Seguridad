# **Guía UX/UI del Módulo de Seguridad y Autenticación (G7)**

| Campo | Valor |
|---|---|
| **Autor** | Luis David Morales Brenis |
| Responsable | Valery Cristin Gutierrez Bendezu — sistema de diseño y biblioteca en Figma (responsabilidades.md) |
| Estado | Biblioteca en Figma publicada, mockups exportados y propuestas de UX completadas. |
| Figma | https://www.figma.com/design/J95xYLdhSO9rzyO1i5awnn/Inka-Athletics-%E2%80%93-Design-System?node-id=45-3 |
| Se implementa en | specs/componentes-react/ |

## *Referencia práctica para el proyecto del curso*

Esta guía reúne las reglas de UX/UI que deben consultar los compañeros antes de diseñar o implementar una pantalla del proyecto. Su objetivo es mantener una experiencia consistente entre todos los módulos del marketplace; este módulo, como **proveedor de identidad**, adopta la misma base compartida y solo define lo específico de sus pantallas (autenticación, gestión de cuenta y panel de administración).

El archivo central de Figma funciona como fuente de referencia para foundations, componentes y patrones. La biblioteca se consume desde los archivos de cada módulo; los cambios en los componentes maestros se realizan mediante el proceso descrito en la sección de gobernanza.

**Centro de Seguridad**

1. # **Overview y Readme**

   1. ## **Propósito del sistema**

El sistema de diseño centraliza las decisiones visuales y de interacción del marketplace. Sirve para evitar que cada módulo cree soluciones diferentes para una misma necesidad, acelerar el trabajo del curso y facilitar que diseño y desarrollo utilicen los mismos criterios.

La guía cubre identidad, foundations, redacción de interfaz, componentes reutilizables, patrones comunes y reglas para mantener la biblioteca central. No reemplaza los requerimientos funcionales de cada módulo; los complementa con una base compartida de UX/UI.

2. ## **Cómo usar esta guía**

* Revisar Foundations antes de crear una pantalla nueva.  
* Usar los componentes publicados en la biblioteca central de Figma en lugar de dibujar copias locales.  
* Seleccionar la variante y el estado que correspondan al caso de uso.  
* Usar los patrones comunes cuando una pantalla repita estructuras como tarjetas, navegación, filtros, modales o skeletons.  
* Consultar las reglas de UX Writing antes de escribir botones, alertas o mensajes de error.  
* Proponer los cambios desde el archivo o canal indicado, sin editar directamente los componentes maestros.

  3. ## **Convenciones generales**

* Los nombres de componentes, variantes y propiedades deben ser consistentes en Figma y en el código.  
* Cada componente debe incluir su propósito, variantes, estados y reglas de uso.  
* Los elementos responsive deben mostrar cómo cambian entre web y móvil.  
* Las decisiones que afecten a varios módulos se documentan en la biblioteca central.  
* Los componentes maestros no se separan ni modifican dentro de los archivos de cada módulo.
* Convención de nombres: Categoría / Componente / Variante / Estado en Figma (ej. Atoms / Button / Primary / Default) correspondiente a <Button variant="filled" color="orange"> en React.

  4. ## **Recursos del proyecto**

**Archivo central de Figma:**

[https://www.figma.com/design/rKPdRQHLLUkYk5VdiEErqZ/Sistema-De-Dise%C3%B1o?node-id=57-15\&t=eO6m1Plz1xQ5sFgn-1](https://www.figma.com/design/rKPdRQHLLUkYk5VdiEErqZ/Sistema-De-Dise%C3%B1o?node-id=57-15&t=eO6m1Plz1xQ5sFgn-1) 

2. # **Foundations e identidad**

   1. ## **Identidad de Marca**

      1. ### **Nombre**

El nombre del módulo en sus pantallas es **Centro de Seguridad**.

Es un nombre neutro y descriptivo: identifica la SPA del proveedor de identidad del marketplace sin asociarse a una marca de consumo. Convive con la identidad del marketplace padre, que se mantiene como contexto de marca cuando la pantalla lo requiere.

2. ### **Propósito**

El Centro de Seguridad existe para que cualquier persona del marketplace gestione su identidad con confianza: crear su cuenta, iniciar sesión, protegerla y, cuando haga falta, recuperarla.

El módulo debe transmitir que la identidad y el acceso están en buenas manos, guiando al usuario con claridad y sin fricción, incluso en los momentos sensibles (bloqueo, recuperación, verificación).

3. ### **Personalidad**

La personalidad del Centro de Seguridad se define como:

* Confiable.  
* Claro.  
* Discreto.  
* Tranquilizador.  
* Neutral.

El módulo transmite seguridad sin alarmar: acompaña al usuario en los momentos delicados (un bloqueo, una contraseña olvidada, un acceso sospechoso) y explica qué pasó y qué hacer, sin tecnicismos ni dramatismo. No usa un tono festivo ni de marketing agresivo.

2. ## **Logo y variantes**

El Centro de Seguridad no tiene un logo de marca propio: reutiliza la identidad del marketplace padre cuando la pantalla necesita contexto de marca (por ejemplo, en el encabezado del login). Para marcar el módulo se usa un **icono neutro de candado/escudo** de Tabler (p. ej. `IconLock`, `IconShieldLock`).

Reglas:

* No se deforma, rota ni recolorea el icono fuera del token aprobado.  
* El icono no sustituye a un icono funcional ni se usa para comunicar estado.  
* Cuando sea informativo o de navegación, lleva un nombre accesible equivalente a «Centro de Seguridad».  
* Si es decorativo y el nombre del módulo ya es visible, se oculta para tecnologías de asistencia.

1. ### **Accesibilidad del logo/icono**

El icono se toma del set de Tabler Icons (`@tabler/icons-react`), igual que el resto de la iconografía; no hay archivos de logo propios que copiar.

3. ## **Paleta de colores**

La paleta de colores del módulo se organiza en colores de acción, acentos de marca, superficies, texto y colores semánticos de estado. Los colores se basan en la paleta de Mantine 9.6.2 y en colores personalizados del proyecto para mantener correspondencia directa entre el sistema de diseño en Figma y la implementación en React.

El naranja representa la acción principal de la interfaz y se utiliza en llamadas a la acción, botones principales y elementos interactivos prioritarios. El color volt, de tonalidad verde lima, funciona como acento de alto impacto para promociones, disponibilidad, destacados y momentos de celebración visual. El color signal, de tonalidad azul índigo, se utiliza para el foco de teclado, la confirmación de identidad y, en este módulo, como acción principal de las pantallas de autenticación.

Las superficies principales del sistema se construyen con ink y cloud. cloud es el fondo claro predeterminado de las pantallas transaccionales, mientras que ink se utiliza en secciones de mayor contraste visual como heroes, banners y footer.

Los colores semánticos de éxito, alerta, error e información mantienen una función independiente de los colores de marca. Los colores volt y signal no sustituyen a los colores semánticos cuando se necesita comunicar un estado del sistema.

En Figma, todos los colores se registrarán como Variables utilizando nombres semánticos que describan su función y no únicamente su apariencia. En desarrollo se utilizarán los colores equivalentes del tema de Mantine.

| Variable de Figma | Función | HEX | RGB | Equivalente Mantine |
| ----- | ----- | ----- | ----- | ----- |
| color/action/primary | Acción principal: CTA, botones, enlaces activos | \#F76707 | 247, 103, 7 | orange.7 |
| color/action/primary-hover | Hover de la acción principal | \#C2410C | 194, 65, 12 | naranja personalizado (ver 2.2.3) |
| color/action/primary-soft | Fondo suave de la acción principal sobre fondos claros | \#FCE3D0 | 252, 227, 208 | naranja personalizado |
| color/accent/volt | Acento de alto impacto: promociones, disponibilidad, destacados y celebración visual. | \#C3E504 | 195, 229, 4 | color personalizado "volt" |
| color/accent/volt-soft | Fondo suave del acento volt | \#EEF7B0 | 238, 247, 176 | color personalizado "volt" (tono claro) |
| color/accent/signal | Acento de confianza: foco de teclado, confirmación de identidad y acción principal de autenticación. | \#4361EE | 67, 97, 238 | indigo.6 (aprox.) |
| color/accent/signal-soft | Fondo suave del acento signal | \#E1E6FB | 225, 230, 251 | indigo.0 (aprox.) |
| color/surface/ink | Fondo oscuro para secciones de alto contraste (hero, headers) | \#1B1812 | 27, 24, 18 | color personalizado "ink" |
| color/surface/ink-soft | Superficie elevada sobre ink (cards e inputs en modo oscuro) | \#26221A | 38, 34, 26 | color personalizado "ink" (tono claro) |
| color/surface/cloud | Fondo claro principal de páginas | \#F7F5F0 | 247, 245, 240 | color personalizado "cloud" |
| color/surface/cloud-subtle | Fondo secundario sobre cloud | \#EDEAE2 | 237, 234, 226 | color personalizado "cloud" (tono oscuro) |
| color/text/primary | Texto principal sobre fondo claro | \#1B1812 | 27, 24, 18 | — |
| color/text/inverse | Texto principal sobre fondo oscuro (ink) | \#F7F5F0 | 247, 245, 240 | — |
| color/text/secondary | Texto secundario y descripciones | \#495057 | 73, 80, 87 | gray.7 |
| color/text/disabled | Texto deshabilitado o de baja prioridad | \#868E96 | 134, 142, 150 | gray.6 |
| color/border/default | Bordes sobre fondo claro | \#DEE2E6 | 222, 226, 230 | gray.3 |
| color/border/inverse | Bordes sobre fondo oscuro (ink) | \#3A362C | 58, 54, 44 | color personalizado "ink" (tono medio) |
| color/success/default | Indicadores de éxito y confirmación | \#2F9E44 | 47, 158, 68 | green.8 |
| color/success/background | Fondo de mensajes de éxito | \#EBFBEE | 235, 251, 238 | green.0 |
| color/warning/default | Alertas y advertencias | \#F08C00 | 240, 140, 0 | yellow.8 |
| color/warning/background | Fondo de alertas | \#FFF9DB | 255, 249, 219 | yellow.0 |
| color/error/default | Errores y acciones destructivas | \#E03131 | 224, 49, 49 | red.8 |
| color/error/background | Fondo de mensajes de error | \#FFF5F5 | 255, 245, 245 | red.0 |
| color/info/default | Información y mensajes informativos | \#1971C2 | 25, 113, 194 | blue.8 |
| color/info/background | Fondo de mensajes informativos | \#E7F5FF | 231, 245, 255 | blue.0 |

1. ### **Uso de los colores**

**Color de acción según el tipo de pantalla.** Este módulo distingue dos familias de pantallas con dos colores de acción principales:

* **Pantallas de autenticación** (inicio de sesión, registro, desafío OTP, recuperación de contraseña): la acción principal usa **color/accent/signal** (índigo), que comunica confianza y protección.
* **Resto del módulo** (panel de administración, «Mi cuenta», gestión de roles): la acción principal usa **color/action/primary** (naranja), igual que el resto del marketplace.

El color **color/action/primary** se utiliza para la acción principal de una pantalla o sección administrativa. Ejemplos: Guardar cambios, Crear cuenta, Bloquear cuenta y Aplicar filtros.

El color **color/action/primary-hover** se utiliza únicamente cuando el usuario posiciona el cursor sobre una acción principal.

El color **color/action/primary-soft** se utiliza como fondo de elementos seleccionados, mensajes informativos o componentes que necesiten comunicar relación con el color principal sin utilizar un fondo intenso.

El color **color/accent/volt** se reserva para elementos de alto impacto visual: promociones, indicadores de disponibilidad y momentos donde se busca energía o celebración. No sustituye a color/action/primary como color de interacción principal.

El color **color/accent/signal** se utiliza para el indicador de foco de teclado en todo el sitio, para la acción principal de las pantallas de autenticación y para la confirmación de identidad. Es un color de marca y no un color semántico de estado; no debe confundirse con color/info/default.

Uso de color en el logotipo:

* El logo principal utiliza surface/ink.  
* El logo Volt utiliza accent/volt.  
* El logo Signal utiliza accent/signal.  
* Estas variantes pertenecen a la identidad de marca y no sustituyen automáticamente los colores de acciones, alertas o estados de la interfaz.  
* Solo deben utilizarse las combinaciones aprobadas en Figma.

**color/surface/ink** y **color/surface/cloud** son los dos fondos base del sistema. Las pantallas del módulo (autenticación, «Mi cuenta», panel de administración) usan cloud por defecto, priorizando legibilidad. El encabezado de login o del panel puede usar ink, siempre con texto en color/text/inverse. Alternar ink y cloud entre secciones crea ritmo visual y evita que el naranja sea el único elemento con contraste en la pantalla; ambos fondos no deben combinarse dentro de un mismo componente o tarjeta.

Los colores neutros se utilizan para contenido general. **color/text/primary** es el color predeterminado del texto; **color/text/secondary** se utiliza para información complementaria; **color/surface/cloud** se utiliza como fondo principal; **color/surface/cloud-subtle** permite separar secciones sin introducir un nuevo color; y **color/border/default** se utiliza en inputs, tarjetas y divisores.

Los colores de éxito, alerta, error e información tienen significado semántico y no deben utilizarse con fines decorativos. Un estado nunca se comunicará únicamente mediante color: deberá acompañarse de texto y, cuando corresponda, iconografía.

**Estados de cuenta.** El módulo muestra el estado de una cuenta con badges semánticos, siempre con texto además de color:

| Estado de cuenta | Token / badge | Texto |
| --- | --- | --- |
| `ACTIVO` | color/success | «Activa» |
| `PENDIENTE_VERIFICACION` | color/warning | «Pendiente de verificación» |
| `BLOQUEADO` | color/error | «Bloqueada» |
| `INACTIVO` | neutral (cloud-subtle) | «Inactiva» |

Los roles (`CLIENTE`, `VENDEDOR`, `ADMIN_VENTAS`, `GESTOR_DESPACHO`, `GESTOR_COMERCIAL`, `ADMIN_SISTEMA`) se muestran con un badge neutral y su etiqueta en texto plano, no con color de marca.

2. ### **Combinaciones permitidas de fondo y texto**

Para garantizar legibilidad, las combinaciones de texto y fondo utilizadas por el sistema deberán cumplir como mínimo con el nivel AA de WCAG para texto normal.

| Fondo | Texto | Uso recomendado | Contraste aproximado |
| ----- | ----- | ----- | ----- |
| \#F76707 action/primary | \#1B1812 ink | Botón principal y CTA | 5.82:1 |
| \#C2410C action/primary-hover | \#F7F5F0 cloud | Hover de botón principal | 4.75:1 |
| \#1B1812 ink | \#F7F5F0 cloud | Texto sobre secciones oscuras | 16.25:1 |
| \#F7F5F0 cloud | \#1B1812 ink | Texto sobre secciones claras | 16.25:1 |
| \#EDEAE2 cloud-subtle | \#1B1812 ink | Superficies secundarias | 14.73:1 |
| \#C3E504 volt | \#1B1812 ink | Promociones, disponibilidad y destacados | 12.25:1 |
| \#4361EE signal | \#FFFFFF blanco | Confirmación de identidad y autenticación | 5.02:1 |
| \#EBFBEE success/background | \#1B1812 ink | Mensajes de éxito | 16.50:1 |
| \#FFF9DB warning/background | \#1B1812 ink | Mensajes de alerta | 16.72:1 |
| \#FFF5F5 error/background | \#1B1812 ink | Mensajes de error | 16.55:1 |
| \#E7F5FF info/background | \#1B1812 ink | Mensajes informativos | 15.94:1 |

El botón principal no utilizará texto blanco sobre color/action/primary para etiquetas de tamaño normal, ya que la combinación no alcanza el contraste AA requerido. El texto predeterminado sobre el naranja principal será color/text/primary (ink).

Cuando el botón principal cambie a color/action/primary-hover, se utilizará color/text/inverse debido a que el fondo es más oscuro.

Los elementos con fondo color/accent/volt utilizarán siempre texto color/text/primary. No se utilizará texto blanco sobre volt.

Los elementos con fondo color/accent/signal podrán utilizar texto blanco debido a que la combinación cumple el contraste requerido.

Los fondos semánticos de éxito, alerta, error e información utilizarán preferentemente color/text/primary para el contenido y el color semántico correspondiente para iconos o indicadores.

Los estados nunca se comunicarán únicamente mediante color. Deberán acompañarse de texto y, cuando corresponda, iconografía.

3. ### **Correspondencia con Mantine 9.6.2**

Mantine organiza cada familia de colores en diez tonalidades numeradas de 0 a 9, desde las más claras hasta las más oscuras. Además, primaryColor establece la familia principal y primaryShade determina qué tonalidad utiliza normalmente el componente principal.

Para este proyecto, la familia principal será orange y la tonalidad principal será orange.7, equivalente a color/action/primary.

Los colores volt, signal, ink y cloud se registrarán como colores personalizados del tema de Mantine.

| const theme \= createTheme({   primaryColor: 'orange',   primaryShade: 7,   autoContrast: true,   colors: {     // Reemplazar los valores de ejemplo por las     // 10 tonalidades definitivas del proyecto.     volt: \[ '\#FBFEE0', '...', '\#C3E504', '...', '\#5C6B00', \],     signal: \[ '\#EEF0FE', '...', '\#4361EE', '...', '\#1B2A99', \],     ink: \[ '\#F5F4F2', '...', '\#26221A', '...', '\#1B1812', \],     cloud: \[ '\#FFFFFF', '...', '\#EDEAE2', '...', '\#D6D2C4', \],   }, }); |
| :---- |

De esta forma, un componente como: 

| \<Button\>Iniciar sesión\</Button\> |
| :---- |

utilizará el naranja principal del proyecto como color de acción. El texto deberá mantener el contraste definido para color/action/primary.

Cuando se necesite una acción de autenticación o confirmación de identidad se utilizará signal:

| \<Button color="signal"\>   Verificar mi correo \</Button\> |
| :---- |

Cuando se necesiten colores semánticos se utilizará sus colores correspondientes:

| \<Button color="red"\>   Bloquear cuenta \</Button\> \<Badge color="green"\>   Activa \</Badge\> \<Alert color="yellow"\>   Revisa tu correo \</Alert\> |
| :---- |

La ventaja de plantearlo así es que en Figma y en React estaremos hablando del mismo sistema.

| Figma | React / Mantine |
| ----- | ----- |
| color/action/primary | orange.7 |
| color/action/primary-hover | Naranja personalizado \#C2410C |
| color/action/primary-soft | Naranja personalizado \#FCE3D0 |
| color/accent/volt | volt personalizado |
| color/accent/signal | signal personalizado |
| color/surface/ink | ink personalizado |
| color/surface/cloud | cloud personalizado |
| color/success/default | green.8 |
| color/warning/default | yellow.8 |
| color/error/default | red.8 |
| color/info/default | blue.8 |
| color/border/default | gray.3 |

4. ## **Tipografía**

La tipografía del módulo utiliza una jerarquía común para títulos, subtítulos, cuerpo, etiquetas y textos auxiliares. El sistema utiliza dos familias con roles distintos: Oswald para títulos (H1, H2, H3) y elementos de alto impacto, e Inter para todo el texto de cuerpo, formularios y contenido general. Oswald es una tipografía condensada de alto impacto, reservada a los títulos; Inter se mantiene como base del cuerpo por su legibilidad en interfaces digitales y su variedad de pesos. Esta combinación es una decisión deliberada de marca y no debe extenderse: labels de formulario, texto de cuerpo y etiquetas auxiliares siempre usan Inter, nunca Oswald.

Como fuentes de respaldo se utilizarán las fuentes del sistema operativo para evitar problemas de visualización en caso de que la fuente principal no pueda cargarse.

La familia de cuerpo utilizada en desarrollo será:

| Inter, system-ui, \-apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif |
| :---- |

La familia de títulos (H1, H2, H3) utilizada en desarrollo será:

| Oswald, "Segoe UI", sans-serif |
| :---- |

Ambas fuentes se cargan desde Google Fonts mediante el siguiente enlace, agregado en el head del documento: https://fonts.googleapis.com/css2?family=Oswald:wght@500;700\&family=Inter:wght@400;500;600;700\&display=swap

Los títulos en Oswald se escriben en mayúscula y en peso 700\. Este tratamiento es exclusivo de H1, H2 y H3; no debe aplicarse a subtítulos, labels, badges de texto largo ni contenido de cuerpo.

Los pesos permitidos serán:

| Peso | Nombre | Uso |
| :---- | :---- | :---- |
| 400 | Regular | Texto de cuerpo, descripciones y contenido general |
| 500 | Medium | Elementos que requieren énfasis moderado |
| 600 | SemiBold | Etiquetas, botones y subtítulos |
| 700 | Bold | Títulos y encabezados |

No se utilizarán otros pesos salvo que se incorpore una nueva necesidad al sistema de diseño.

1. ### **Escala tipográfica**

| Estilo de Figma | Tamaño | Peso | Interlineado | Espaciado | Uso |
| :---- | :---- | :---- | :---- | :---- | :---- |
| Typography/Heading/H1 | 32 px | 700 | 40 px | 0 | Título principal de página |
| Typography/Heading/H2 | 28 px | 700 | 36 px | 0 | Secciones principales |
| Typography/Heading/H3 | 24 px | 700 | 32 px | 0 | Subsecciones |
| Typography/Heading/H4 | 20 px | 700 | 28 px | 0 | Tarjetas, modales y bloques |
| Typography/Subtitle | 18 px | 600 | 26 px | 0 | Subtítulos y encabezados secundarios |
| Typography/Body | 16 px | 400 | 24 px | 0 | Texto principal de la interfaz |
| Typography/Body/Small | 14 px | 400 | 20 px | 0 | Información secundaria y contenido compacto |
| Typography/Label | 14 px | 600 | 20 px | 0 | Labels de formularios y controles |
| Typography/Auxiliary | 12 px | 400 | 16 px | 0 | Ayuda, metadatos y texto auxiliar |

La jerarquía tipográfica debe conservarse entre los módulos. No se utilizará un tamaño únicamente por preferencia visual; cada estilo debe corresponder al propósito definido anteriormente.

Los títulos de página utilizarán preferentemente **H1**. Las secciones internas utilizarán **H2** o **H3**. **H4** podrá utilizarse para títulos de tarjetas, modales o bloques pequeños.

El texto de cuerpo utilizará 16 px como tamaño predeterminado. Los textos de 14 px se reservarán para información secundaria y componentes de mayor densidad. El tamaño de 12 px se utilizará únicamente para información auxiliar y no para contenido principal o acciones.

En Mantine 9.6.2, la tipografía se configurará mediante el objeto global del tema, que permite definir **fontFamily**, **headings** y tamaños tipográficos.

Implementación propuesta: 

| import { createTheme } from '@mantine/core'; export const theme \= createTheme({   fontFamily:     'Inter, system-ui, \-apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif',   headings: {     fontFamily:       'Oswald, "Segoe UI", sans-serif',     fontWeight: '700',     textTransform: 'uppercase',     sizes: {       h1: {         fontSize: '2rem',         lineHeight: '2.5rem',       },       h2: {         fontSize: '1.75rem',         lineHeight: '2.25rem',       },       h3: {         fontSize: '1.5rem',         lineHeight: '2rem',       },       h4: {         fontSize: '1.25rem',         lineHeight: '1.75rem',       },     },   },   fontSizes: {     xs: '0.75rem',     sm: '0.875rem',     md: '1rem',     lg: '1.125rem',     xl: '1.25rem',   }, }); |
| :---- |

5. ## **Espaciado y grilla**

El módulo utilizará una **escala de espaciado basada en 4 px**. Los márgenes, paddings y separaciones entre elementos deberán construirse utilizando los valores definidos por el sistema de diseño.

El objetivo es evitar valores arbitrarios entre componentes y mantener consistencia entre Figma y la implementación.

1. ### **Escala de espaciado**

| Token | Valor | Uso recomendado |
| :---- | :---- | :---- |
| spacing/xs | 4 px | Separación mínima entre elementos relacionados |
| spacing/sm | 8 px | Separación entre icono y texto, controles relacionados |
| spacing/md | 16 px | Padding y separación estándar |
| spacing/lg | 24 px | Separación entre grupos o bloques |
| spacing/xl | 32 px | Separación entre secciones |

El valor predeterminado para la separación interna de componentes será 16 px cuando no exista una necesidad específica.

No deberán introducirse valores arbitrarios como 7 px, 13 px, 19 px o 27 px. Cuando aparezca una necesidad que no pueda cubrirse con la escala existente, deberá revisarse primero si corresponde ampliar el sistema de tokens.

En Mantine, estos valores se configurarán mediante **theme.spacing**, que controla paddings, márgenes y otras propiedades de espaciado utilizadas por los componentes.

| const theme \= createTheme({   spacing: {     xs: '0.25rem', // 4 px     sm: '0.5rem',  // 8 px     md: '1rem',    // 16 px     lg: '1.5rem',  // 24 px     xl: '2rem',    // 32 px   }, }); |
| :---- |

2. ### **Grilla responsive**

El layout utilizará una grilla de 12 columnas para escritorio, tablet y móvil. Los componentes podrán ocupar diferente cantidad de columnas según el ancho disponible.

Los breakpoints se basarán en los valores predeterminados de Mantine 9.6.2. Mantine utiliza **em** para los breakpoints y establece equivalencias de 576, 768, 992, 1200 y 1408 px.

| Breakpoint | Ancho de referencia | Columnas | Margen lateral | Gutter | Ancho máximo |
| :---- | :---- | :---- | :---- | :---- | :---- |
| Base | \<576 px | 12 | 16 px | 16 px | Fluido |
| **xs** | ≥576 px | 12 | 20 px | 16 px | 540 px |
| **sm** | ≥768 px | 12 | 24 px | 20 px | 720 px |
| **md** | ≥992 px | 12 | 32 px | 24 px | 960 px |
| **lg** | ≥1200 px | 12 | 32 px | 24 px | 1140 px |
| **xl** | ≥1408 px | 12 | 40 px | 24 px | 1320 px |

La cantidad de columnas permanece en 12 en todos los tamaños; lo que cambia es cuánto ocupa cada elemento. Por ejemplo, una fila de la tabla del panel podrá ocupar:

* Móvil:      12 columnas → 1 tarjeta por fila  
* Tablet:      6 columnas → 2 tarjetas por fila  
* Desktop:     3 columnas → 4 tarjetas por fila

Ejemplo en Mantine:

| \<Grid gutter={{ base: 16, md: 24 }}\>   \<Grid.Col span={{ base: 12, sm: 6, lg: 3 }}\>     Cuenta   \</Grid.Col\>   \<Grid.Col span={{ base: 12, sm: 6, lg: 3 }}\>     Cuenta   \</Grid.Col\> \</Grid\> |
| :---- |

Los diseños en Figma deberán mostrar al menos una versión móvil y una versión de escritorio de los componentes o patrones cuyo comportamiento cambie de forma relevante.

6. ## **Radios de borde**

Los radios de borde se definirán mediante tokens compartidos para evitar valores diferentes entre botones, campos, tarjetas, badges y modales.

1. ### **Tokens de border radius**

| Token | Valor | Uso |
| :---- | :---- | :---- |
| **radius/xs** | 4 px | Elementos pequeños y controles compactos |
| **radius/sm** | 8 px | Botones, inputs, dropdowns y controles |
| **radius/md** | 12 px | Tarjetas y contenedores |
| **radius/lg** | 16 px | Modales y superficies destacadas |
| **radius/full** | 999 px | Badges, tags y elementos tipo pill |

El radio predeterminado del sistema será 8 px.

Los botones, inputs, checkboxes personalizados y dropdowns utilizarán generalmente radius/sm.

Las tarjetas utilizarán **radius/md**.

Los modales o paneles destacados podrán utilizar **radius/lg**.

Los badges, tags y otros componentes completamente redondeados utilizarán **radius/full**.

No se utilizarán valores intermedios arbitrarios como 5 px, 10 px o 14 px.

Mantine permite definir estos tokens mediante **theme.radius**; los componentes que exponen la propiedad radius utilizan los valores establecidos en el tema.

| const theme \= createTheme({   radius: {     xs: '0.25rem', // 4 px     sm: '0.5rem',  // 8 px     md: '0.75rem', // 12 px     lg: '1rem',    // 16 px     xl: '999px',   },   defaultRadius: 'sm', }); |
| :---- |

Ejemplo:

| \<Button radius="sm"\>   Crear cuenta \</Button\> \<Card radius="md"\>   ... \</Card\> \<Badge radius="xl"\>   Activa \</Badge\> |
| :---- |

En Figma se utilizarán los mismos nombres conceptuales y valores para conservar correspondencia con el desarrollo. 

7. ## **Iconografía**

El módulo utilizará **Tabler Icons** como set único de iconografía.

Tabler Icons utiliza un lienzo base de 24 × 24 px y un trazo estándar de 2 px, lo que permite mantener consistencia visual entre los diferentes iconos. Tabler

En desarrollo se utilizará el paquete:

| @tabler/icons-react |
| :---- |

1. ### **Tamaños aprobados**

| Tamaño | Uso |
| :---- | :---- |
| 16 × 16 px | Controles pequeños, badges y elementos compactos |
| 20 × 20 px | Botones, inputs y controles estándar |
| 24 × 24 px | Iconos independientes, navegación y acciones destacadas |

El tamaño estándar dentro de botones e inputs será 20 × 20 px.

El tamaño oficial del lienzo de los iconos será 24 × 24 px.

El grosor de trazo estándar será 2 px.

La separación entre un icono y su texto será de 8 px.

2. ### **Color**

Los iconos deberán utilizar preferentemente **currentColor**, de forma que hereden el color del elemento que los contiene. Esta técnica también es compatible con los SVG de Tabler. Tabler

No se utilizarán colores diferentes para iconos pertenecientes a un mismo control salvo que exista un significado semántico definido.

3. ### **Iconos decorativos**

Los iconos que solo acompañen visualmente un texto no deberán recibir foco ni anunciarse de forma independiente mediante tecnologías de asistencia.

Ejemplo:

| \<Button leftSection={\<IconLock size={20} /\>}\>   Iniciar sesión \</Button\> |
| :---- |

4. ### **Iconos interactivos**

Cuando un icono represente por sí solo una acción, deberá utilizarse dentro de un componente interactivo y disponer de un nombre accesible.

Ejemplo:

| \<ActionIcon   aria-label="Cerrar sesión"   variant="subtle" \>   \<IconLogout size={20} /\> \</ActionIcon\> |
| :---- |

No se utilizarán iconos sueltos con eventos **onClick** si pueden representarse mediante **Button**, ActionIcon u otro control accesible.

Los nombres utilizados en Figma deberán corresponder, siempre que sea posible, al nombre del icono utilizado en **@tabler/icons-react**.

8. ## **Superficies, alternancia y motivo gráfico**

El módulo usa **color/surface/cloud** como fondo base en todas sus pantallas (autenticación, «Mi cuenta» y panel de administración), priorizando legibilidad y foco en la tarea. El **ink** se reserva para un encabezado de login o del panel que necesite contraste, siempre con texto en color/text/inverse.

Reglas:

* No se alternan fondos dentro de un mismo componente o tarjeta.  
* El panel de administración puede usar **cloud-subtle** para separar el menú lateral del contenido.

**Motivo gráfico — cuadrícula sutil:**

Se aprueba el uso de una cuadrícula de puntos o líneas muy tenues en color/text/disabled como recurso decorativo de fondo en el login o el encabezado del panel. Este recurso es puramente decorativo y no debe usarse para comunicar estado, jerarquía o información: los estados siguen comunicándose con los colores semánticos definidos en 2.2.

* Componente de motivo gráfico en Figma: https://www.figma.com/design/J95xYLdhSO9rzyO1i5awnn/Inka-Athletics-%E2%80%93-Design-System?node-id=45-3 (Cuadrícula de puntos tenues sobre fondo color/surface/cloud).

3. # **Brand y UX Writing**

   1. ## **Tono de voz**

El Centro de Seguridad comunica de forma clara, cercana y tranquilizadora. Explica qué pasó y qué hacer, sin alarmar ni presionar. Acompaña al usuario en lugar de culparlo.

La personalidad de marca es:

* Confiable.  
* Clara.  
* Discreta.  
* Tranquilizadora.  
* Neutral.

  2. ## **Reglas generales de redacción**

* Escribir acciones con verbos concretos: Iniciar sesión, Crear, Guardar, Verificar, Bloquear o Volver.  
* Mantener el mismo término para una misma acción en todas las pantallas.  
* Explicar primero qué ocurrió y después qué puede hacer el usuario.  
* Evitar mensajes técnicos, códigos internos, culpas y signos de exclamación innecesarios.  
* No depender solo del color para comunicar éxito, alerta o error.  
* Usar mayúscula inicial en frases y evitar escribir botones completos en mayúsculas.

Debe sonar:

* Claro: «Tu cuenta fue bloqueada. Usa el enlace que te enviamos para recuperarla.»  
* Cercano: «Te enviamos un enlace a tu correo para verificar tu cuenta.»  
* Seguro: «Por seguridad, tu contraseña debe cumplir estos requisitos.»  
* Tranquilizador: «Si no reconoces este acceso, cambia tu contraseña y cierra las demás sesiones.»

Debe evitar:

* Exceso de palabras en inglés.  
* Frases alarmistas o culpabilizadoras.  
* Expresiones técnicas o códigos internos.  
* Promesas exageradas o absolutas.  
* Mensajes que revelen si una cuenta existe o no.

  3. ## **Botones y llamados a la acción**

| Situación | Hacer | No hacer |
| :---- | :---- | :---- |
| Acceso | Iniciar sesión | Entrar |
| Cuenta nueva | Crear cuenta | Registrarse |
| Contraseña olvidada | Recuperar contraseña | Resetear |
| Verificar correo | Verificar mi correo | Activar |
| Guardar | Guardar cambios | Enviar |
| Acción de seguridad | Bloquear cuenta / Desbloquear cuenta | Banear |
| Salir | Cerrar sesión | Logout |

4. ## **Mensajes de error**

| Caso | Hacer | No hacer |
| :---- | :---- | :---- |
| Credenciales no válidas | Correo o contraseña incorrectos. | Distinguir si el correo no existe, la cuenta está bloqueada o la contraseña es incorrecta (anti-enumeración) |
| Campo obligatorio | Ingresa tu correo electrónico. | Campo inválido. |
| Contraseña débil | La contraseña debe tener al menos 10 caracteres e incluir mayúscula, minúscula, número y un carácter especial. | Contraseña inválida. |
| Enlace vencido | El enlace ya no es válido. Pide uno nuevo. | Enlace expirado. |
| Problema de conexión | No pudimos cargar la información. Inténtalo nuevamente. | Error 500. |

> **Regla de seguridad.** Ningún mensaje puede revelar si una cuenta existe, está bloqueada o sin verificar. Ante credenciales incorrectas, correo inexistente o cuenta bloqueada se usa siempre el mismo mensaje genérico.

5. ## **Alertas y confirmaciones**

| Tipo | Ejemplo recomendado | Flujo del proyecto |
| :--- | :--- | :--- |
| **Información** | Te enviamos un código de verificación de 6 dígitos a tu correo electrónico. | Registro / Verificación de correo |
| **Alerta** | Tu sesión expirará en 2 minutos por inactividad. Guarda tus cambios. | Sesión activa en el panel de administración |
| **Éxito** | Tu contraseña se ha actualizado correctamente. Ya puedes iniciar sesión. | Recuperación / Cambio de contraseña |
| **Error** | El código ingresado es incorrecto o ha expirado. Solicita uno nuevo. | Desafío de autenticación OTP |
| **Confirmación** | ¿Estás seguro de que deseas bloquear la cuenta seleccionada? Esta acción suspenderá sus accesos inmediatamente. | Gestión de usuarios (Panel Admin) |

4. # **Componentes reutilizables UI Kit**

   1. ## **Base de implementación**

La implementación del UI Kit utilizará Mantine 9.6.2 sobre React y TypeScript.

Mantine será la biblioteca base para los componentes visuales y permitirá centralizar colores, tipografía, espaciado, radios, breakpoints y configuración global mediante MantineProvider y el objeto de tema.

La configuración principal del sistema de diseño se almacenará en:

| src/theme/theme.ts |
| :---- |

Los componentes de Figma deberán representar las mismas propiedades, tamaños, variantes y estados disponibles en la implementación.

Siempre que Mantine disponga de un componente que cubra la necesidad, se utilizará dicho componente antes de crear uno personalizado.

Los componentes personalizados se crearán únicamente cuando:

* Exista un patrón reutilizable específico del proyecto;  
* El comportamiento requerido no pueda representarse directamente con un componente de Mantine;  
* O sea necesario combinar varios componentes básicos para formar un componente propio del módulo.

La personalización de componentes se realizará mediante el sistema de temas y la Styles API de Mantine, evitando modificar directamente la implementación interna de la biblioteca. La Styles API permite personalizar los elementos internos de los componentes y aplicar estas reglas desde el tema.

La configuración inicial utilizará:

* Mantine: 9.6.2  
* Tema: claro  
* Color de acción principal: orange.7 — \#F76707  
* Hover de acción principal: \#C2410C  
* Acento de alto impacto: volt — \#C3E504  
* Acento secundario: signal — \#4361EE  
* Superficie oscura principal: ink — \#1B1812  
* Superficie clara principal: cloud — \#F7F5F0  
* Tipografía de cuerpo: Inter  
* Tipografía de títulos H1, H2 y H3: Oswald  
* Radio predeterminado: 8 px  
* Contraste automático de Mantine: habilitado

Los colores semánticos de éxito, alerta, error e información continuarán utilizando las familias green, yellow, red y blue de Mantine, respectivamente. Estos colores no deberán sustituirse por los acentos de marca volt o signal.

Enlace a la configuración del tema del proyecto:

Enlace directo al archivo theme.ts: https://github.com/Taller-SW-Web/Modulo-de-Seguridad/blob/main/src/theme/theme.ts

2. ## **Reglas comunes de los componentes**

Todos los componentes reutilizables deberán cumplir las siguientes reglas:

* Cada componente de Figma deberá crearse como componente principal y utilizar Component Properties para representar variantes y estados.  
* Las propiedades de Figma deberán utilizar nombres equivalentes a las propiedades utilizadas en código siempre que sea posible.  
* Se utilizarán exclusivamente los tokens definidos en Foundations para colores, tipografía, espaciado y radios.  
* No se utilizarán colores, radios o espacios arbitrarios directamente dentro de un componente.  
* color/action/primary se utilizará para las acciones principales de la interfaz.  
* color/accent/volt se utilizará para promociones, disponibilidad, destacados y momentos de celebración visual.  
* color/accent/signal se utilizará para el indicador de foco de teclado, la acción principal de autenticación y la confirmación de identidad.  
* Los colores success, warning, error e info se reservarán para comunicar estados semánticos y no se sustituirán por colores de marca.  
* Todos los componentes interactivos deberán incluir los estados aplicables: default, hover, focus, disabled, loading, selected, open y error.  
* No todos los estados aplican a todos los componentes. Por ejemplo, error corresponde principalmente a campos y controles de formulario, mientras que loading corresponde a acciones o componentes que esperan una operación.  
* Los componentes deberán conservar un indicador de foco visible cuando se utilicen mediante teclado. El indicador global de foco utilizará color/accent/signal.  
* Los estados de error, alerta o éxito no dependerán únicamente del color.  
* Los controles que no tengan texto visible deberán disponer de un nombre accesible mediante aria-label u otro mecanismo apropiado.  
* El estado loading deberá impedir acciones repetidas cuando la operación no pueda ejecutarse varias veces.  
* Los componentes deberán documentar su comportamiento responsive.  
* No se duplicará un componente existente únicamente para cambiar una propiedad visual.  
* Los cambios que afecten a varios módulos deberán realizarse desde el tema o desde el componente reutilizable correspondiente.

La nomenclatura de propiedades utilizará preferentemente términos equivalentes a Mantine:

* variant  
* size  
* color  
* radius  
* disabled  
* loading  
* error  
* checked  
* searchable  
* clearable

Por ejemplo, para un botón:

| Button variant: \- filled \- outline \- subtle intent: \- primary \- destructive size: \- sm \- md \- lg state: \- default \- hover \- focus \- disabled \- loading icon: \- none \- left \- right |
| :---- |

La propiedad intent representa la función del botón y se traduce a los siguientes colores:

* primary      → color/action/primary  
* confirmation → color/accent/signal  
* destructive  → color/error/default

El objetivo es que al revisar un componente en Figma sea posible identificar directamente cómo debe construirse en React.

3. ## **Botones**

Los botones comunican acciones. Su etiqueta deberá comenzar preferentemente con un verbo concreto y deberá existir una jerarquía visual clara entre la acción principal y las acciones alternativas, de acuerdo con las reglas de UX Writing del proyecto.

1. ### **Variantes aprobadas**

Se utilizarán las siguientes variantes:

| Uso en UI Kit | Mantine | Color | Texto | Uso |
| ----- | ----- | ----- | ----- | ----- |
| Acción de autenticación | variant="filled" | color/accent/signal | Blanco | Acción principal en login, registro, OTP y recuperación |
| Acción principal (resto) | variant="filled" | color/action/primary | color/text/primary | Acción de mayor importancia en el resto del módulo |
| Acción secundaria | variant="outline" | color/action/primary-hover | color/action/primary-hover | Alternativa a la acción principal |
| Acción terciaria | variant="subtle" | color/action/primary-hover | color/action/primary-hover | Acción de menor jerarquía |
| Acción destructiva | variant="filled" | color/error/default | Blanco | Bloquear cuenta, cerrar sesión en otros dispositivos |

El botón principal utilizará el naranja color/action/primary (\#F76707). Debido al contraste de la nueva paleta, el texto del botón principal será color/text/primary (\#1B1812) y no blanco.

En el estado hover, el botón principal utilizará color/action/primary-hover (\#C2410C) y color/text/inverse.

Las variantes outline y subtle utilizarán el naranja oscuro color/action/primary-hover para garantizar suficiente contraste sobre color/surface/cloud.

Las acciones de autenticación o confirmación de identidad que necesiten diferenciarse de la acción principal podrán utilizar color/accent/signal.

El color color/accent/volt no se utilizará como botón principal. Se reserva para promociones, disponibilidad y destacados.

2. ### **Tamaños**

Se utilizarán tres tamaños dentro del UI Kit:

| Tamaño del sistema | Mantine | Uso |
| :---- | :---- | :---- |
| Small | sm | Tablas, filtros y espacios compactos |
| Medium | md | Tamaño predeterminado |
| Large | lg | Acciones destacadas o pantallas de conversión |

**md** será el tamaño estándar.

No se utilizarán **xs** o **xl** salvo que aparezca una necesidad concreta y se incorpore al sistema de diseño.

3. ### **Iconos**

Los botones podrán tener:

* icon \= none  
* icon \= left  
* icon \= right

El tamaño recomendado será:

* sm → 16 px  
* md → 20 px  
* lg → 20 px

La separación entre icono y texto será de 8 px.

Los iconos heredarán el color del contenido del botón mediante currentColor.

4. ### **Ancho**

El ancho del botón se ajustará normalmente a su contenido.

Se recomienda un ancho mínimo visual aproximado de 96 px para botones con texto, siempre que el contexto lo permita.

En pantallas móviles, las acciones principales de los formularios y flujos de autenticación podrán ocupar todo el ancho disponible.

| \<Button fullWidth\>   Iniciar sesión \</Button\> |
| :---- |

Mantine implementa este comportamiento mediante **fullWidth**.

5. ### **Estados**

Cada variante deberá mostrar los siguientes estados en Figma:

* default  
* hover  
* focus  
* disabled  
* loading

El estado focus utilizará color/accent/signal como indicador de foco visible.

El estado error no se considera un estado propio del botón. Cuando la acción sea destructiva deberá utilizarse el intent="destructive"; cuando una operación produzca un error, este se comunicará mediante un mensaje asociado.

El estado loading conservará el contexto visual de la acción y deberá impedir ejecuciones repetidas.

6. ### **Propiedades en Figma**

| Component: Button variant \= filled | outline | subtle intent \= primary | destructive size \= sm | md | lg state \= default | hover | focus | disabled | loading icon \= none | left | right fullWidth \= true | false |
| :---- |

Correspondencia en intent:

| primary → color/action/primary confirmation → color/accent/signal destructive → color/error/default |
| :---- |

4. ## **Campos de texto y búsqueda**

Los campos deberán incluir una etiqueta visible que describa qué información se solicita. El placeholder solo se utilizará como información complementaria y no sustituirá la etiqueta.

La guía requiere contemplar etiqueta, valor, placeholder, texto auxiliar, error y las particularidades de los campos de búsqueda.

1. ### **Tipos aprobados**

| Necesidad | Componente Mantine |
| :---- | :---- |
| Texto general | TextInput |
| Contraseña | PasswordInput |
| Números | NumberInput |
| Texto de varias líneas | Textarea |
| Búsqueda | TextInput con IconSearch |

TextInput admite de forma nativa label, descripción, error, estado deshabilitado, tamaño y radio.

2. ### **Tamaños**

* sm → interfaces compactas y filtros  
* md → formularios y tamaño predeterminado  
* lg → casos especiales que requieran mayor énfasis

El tamaño estándar será **md**.

3. ### **Labels**

Los campos de formularios deberán utilizar labels visibles.

Ejemplo:

| \<TextInput   label="Correo electrónico"   placeholder="nombre@correo.com" /\> |
| :---- |

No se recomienda (cuando el placeholder sea la única identificación del campo):

| \<TextInput   placeholder="Correo electrónico" /\> |
| :---- |

4. ### **Campos obligatorios**

Los campos obligatorios deberán indicarse visualmente y también configurarse como requeridos cuando corresponda.

| \<TextInput   label="Correo electrónico"   required /\> |
| :---- |

Mantine diferencia **required**, que añade el atributo HTML correspondiente, de **withAsterisk**, que únicamente muestra el indicador visual.

5. ### **Texto auxiliar**

El texto auxiliar podrá utilizarse para explicar una restricción antes de que ocurra un error.

6. ### **Errores**

Los errores deberán indicar qué ocurrió y, cuando sea posible, cómo corregirlo.

| \<TextInput   label="Correo electrónico"   error="Ingresa un correo electrónico válido" /\> |
| :---- |

Mantine permite pasar directamente el mensaje mediante la propiedad **error**, además de representar visualmente el estado.

No utilices **“Campo inválido”**, mejor usa **“Ingresa tu correo electrónico”**.

7. ### **Longitudes recomendadas**

Estas longitudes son criterios iniciales del UI Kit y pueden ajustarse cuando los requisitos funcionales de cada módulo estén definidos.

| Campo | Longitud recomendada |
| :---- | :---- |
| Nombre o título | Hasta 100 caracteres |
| Búsqueda | Hasta 120 caracteres |
| Correo electrónico | Hasta 254 caracteres |
| Texto descriptivo corto | Hasta 250 caracteres |

Las restricciones que representen reglas de negocio deberán definirse posteriormente con los responsables de cada módulo y no únicamente desde UX/UI.

8. ### **Búsqueda**

El campo de búsqueda utilizará **IconSearch** a la izquierda.

| \<TextInput   label="Buscar usuarios"   placeholder="Correo, nombre o rol"   leftSection={\<IconSearch size={20} /\>} /\> |
| :---- |

Cuando exista contenido escrito podrá ofrecerse una acción para limpiar la búsqueda.

Durante una consulta asíncrona podrá mostrarse un loader en la sección derecha.

Al no encontrar coincidencias se utilizará un mensaje claro como **“No encontramos usuarios para esta búsqueda”**.

9. ### **Estados que deben diseñarse en Figma**

* default  
* hover  
* focus  
* filled  
* disabled  
* loading  
* error

**loading** aplica principalmente a búsqueda u otros inputs con operaciones asíncronas.

10. ### **Propiedades sugeridas en Figma**

| Component: TextInput size \= sm | md | lg state \= default | hover | focus | filled | disabled | error required \= true | false description \= true | false leftIcon \= true | false rightAction \= none | clear | loading |
| :---- |

    5. ## **Checkboxes**

Los checkboxes se utilizarán cuando el usuario pueda activar o desactivar una opción independiente o seleccionar varias opciones dentro de un conjunto.

No deberán utilizarse para seleccionar una única alternativa mutuamente excluyente; en dicho caso corresponderá utilizar un **Radio**.

1. ### **Tamaños**

Se utilizarán:

* sm → interfaces compactas  
* md → tamaño predeterminado

El tamaño estándar será **md**.

Mantine dispone de tamaños desde **xs** hasta **xl**, estados **disabled** e **indeterminate**, además de label, descripción y error.

2. ### **Separación**

La separación visual entre el control y su etiqueta será de aproximadamente 8 px.

La etiqueta completa deberá poder activar o desactivar el checkbox.

3. ### **Estados de selección**

Los estados principales serán:

* unchecked  
* checked  
* indeterminate

**indeterminate** se utilizará cuando un control represente un grupo parcialmente seleccionado.

Ejemplo:

| ☐ Ninguna cuenta seleccionada ☑ Todas las cuentas seleccionadas − Algunas cuentas seleccionadas |
| :---- |

En código:

| \<Checkbox   label="Seleccionar todos"   indeterminate /\> |
| :---- |

4. ### **Estados visuales**

En Figma deberán representarse:

* default  
* hover  
* focus  
* disabled  
* error

combinados con:

* unchecked  
* checked  
* indeterminate

El estado seleccionado deberá reconocerse mediante el símbolo del checkbox y no únicamente mediante el cambio de color.

5. ### **Error**

Cuando el checkbox sea obligatorio dentro de un formulario, deberá mostrar un mensaje que indique claramente qué acción se requiere.

Por ejemplo:

* Debes aceptar los términos para continuar.

No utilizar únicamente:

* Campo obligatorio.

  6. ### **Propiedades en Figma**

| Component: Checkbox checked \= false | true | indeterminate size \= sm | md state \= default | hover | focus | disabled | error label \= true | false description \= true | false |
| :---- |

  6. ## **Dropdowns**

Los dropdowns se utilizarán para seleccionar una o varias opciones de un conjunto conocido.

No se utilizará un único componente para todos los casos; el componente de Mantine dependerá del tipo de selección.

1. ### **Tipos**

| Necesidad | Componente Mantine |
| :---- | :---- |
| Selección única | Select |
| Selección múltiple | MultiSelect |
| Texto libre con sugerencias | Autocomplete |
| Comportamientos avanzados | Combobox |

**Select** permite seleccionar únicamente valores de una lista y puede habilitar búsqueda. Para necesidades más avanzadas Mantine recomienda construir sobre **Combobox**.

**MultiSelect** proporciona selección múltiple y también puede habilitar búsqueda.

2. ### **Tamaños**

* sm → filtros o interfaces compactas  
* md → tamaño predeterminado  
* lg → casos especiales

El tamaño estándar será **md**.

3. ### **Selección simple**

Ejemplo:

| \<Select   label="Marca"   placeholder="Selecciona una marca"   data={\[     'Adidas',     'Nike',     'Puma',     'Under Armour',   \]} /\> |
| :---- |

4. ### **Selección múltiple**

Ejemplo:

| \<MultiSelect   label="Categorías"   placeholder="Selecciona categorías"   data={\[     'Fútbol',     'Running',     'Ciclismo',     'Fitness',   \]} /\> |
| :---- |

5. ### **Búsqueda**

Para listas pequeñas de hasta aproximadamente 8 opciones, la búsqueda será opcional.

Para listas extensas se utilizará **searchable**.

| \<Select   label="Marca"   searchable   data={brands} /\> |
| :---- |

Mantine permite habilitar el filtrado mediante **searchable** tanto en **Select** como en **MultiSelect**.

6. ### **Limpiar selección**

Los dropdowns opcionales o utilizados como filtros podrán utilizar clearable.

| \<Select   label="Marca"   clearable   data={brands} /\> |
| :---- |

Mantine oculta automáticamente la acción de limpiar cuando no existe un valor o cuando el control está deshabilitado o en modo de solo lectura.

7. ### **Estado sin resultados**

Cuando una búsqueda no produzca coincidencias deberá mostrarse:

* Sin resultados

Mantine dispone de **nothingFoundMessage** para este caso.

| \<Select   searchable   nothingFoundMessage="Sin resultados"   data={brands} /\> |
| :---- |

8. ### **Listas grandes**

En conjuntos de datos muy grandes deberá limitarse la cantidad de opciones renderizadas simultáneamente mediante **limit**, evitando renderizar miles de elementos innecesariamente. La documentación de Mantine recomienda esta estrategia para grandes conjuntos de opciones.

9. ### **Comportamiento móvil**

En móvil, los dropdowns utilizarán el ancho disponible del contenedor.

La lista deberá permanecer dentro del viewport y permitir desplazamiento vertical cuando existan muchas opciones.

No se deberá obligar al usuario a interactuar con opciones demasiado pequeñas.

10. ### **Estados en Figma**

* default  
* hover  
* focus  
* open  
* selected  
* disabled  
* loading  
* error

**loading** se utilizará cuando las opciones provengan de una petición asíncrona.

11. ### **Propiedades de Select en Figma**

| Component: Select size \= sm | md | lg state \= default | hover | focus | open | selected | disabled | loading | error searchable \= true | false clearable \= true | false required \= true | false |
| :---- |

    12. ### **Propiedades de MultiSelect en Figma**

| Component: MultiSelect size \= sm | md | lg state \= default | hover | focus | open | selected | disabled | loading | error searchable \= true | false clearable \= true | false |
| :---- |

    7. ## **Badges y tags**

Los badges y tags tendrán funciones diferentes.

Un badge comunica información breve de estado, categoría o característica y normalmente no es interactivo.

Un tag seleccionable representa una opción que el usuario puede activar o desactivar.

Un tag removible representa un elemento seleccionado que puede eliminarse.

1. ### **Componentes**

| Tipo | Componente Mantine | Uso |
| :---- | :---- | :---- |
| Badge informativo | **Badge** | Estado o categoría |
| Tag seleccionable | **Chip** | Filtro seleccionable |
| Tag removible | **Pill** | Filtro o valor seleccionado |
| Entrada de múltiples tags | **PillsInput** / **TagsInput** | Ingreso de etiquetas |

Mantine proporciona **Badge** para badges, **Chip** para valores seleccionables y **Pill** para etiquetas removibles. PillsInput sirve como base para entradas de tags y multiselecciones personalizadas.

2. ### **Badges semánticos**

Los badges utilizarán colores semánticos cuando comuniquen estados del sistema y colores de marca cuando comuniquen promociones, disponibilidad o elementos nuevos.

Se utilizarán las siguientes variantes:

| Tipo | Fondo / color principal | Texto | Ejemplo |
| ----- | ----- | ----- | ----- |
| Neutral | **color/surface/cloud-subtle** | color/text/primary | Inactiva |
| Información | **color/info/background** | color/text/primary | Rol asignado |
| Éxito | **color/success/background** | color/text/primary | Activa |
| Alerta | **color/warning/background** | color/text/primary | Pendiente de verificación |
| Error | **color/error/background** | color/text/primary | Bloqueada |

Los badges de información, éxito, alerta y error representan estados semánticos y deberán utilizar los tokens correspondientes. volt y signal no deberán sustituir esos estados.

color/accent/volt se utilizará para promociones y elementos de disponibilidad que necesiten alto impacto visual. Debido a su alta luminosidad, el contenido sobre volt utilizará siempre color/text/primary.

color/accent/signal se utilizará para elementos identificados como nuevos y podrá utilizar texto blanco.

Los badges informativos utilizarán preferentemente fondos suaves para evitar competir visualmente con las acciones principales.

Ejemplo:

| \<Badge color="green" variant="light"\>   Activa \</Badge\> \<Badge color="yellow" variant="light"\>   Pendiente de verificación \</Badge\> \<Badge color="red" variant="light"\>   Bloqueada \</Badge\> |
| :---- |

3. ### **Tamaños**

Los tamaños aprobados serán:

* sm  
* md

**sm** será utilizado en tarjetas, tablas y otros contextos compactos.

**md** se utilizará cuando el badge necesite mayor visibilidad.

4. ### **Iconos**

Los badges podrán incorporar un icono únicamente cuando aporte significado.

Tamaño recomendado:

* 14–16 px

No se utilizarán iconos meramente decorativos si dificultan la lectura del contenido.

5. ### **Límite de texto**

Los badges y tags deberán contener textos breves.

Se recomienda un máximo aproximado de 24 caracteres. Si el contenido necesita una frase extensa, probablemente no corresponda utilizar un badge o tag.

6. ### **Badge**

Un badge meramente informativo no deberá tener apariencia de botón.

Por tanto, sus estados serán principalmente:

* default

y sus variantes semánticas.

No necesita estados **hover**, **focus**, **selected** o **loading** mientras no sea interactivo.

7. ### **Chip seleccionable**

Para filtros seleccionables se utilizará Chip.

| \<Chip\>   Nike \</Chip\> |
| :---- |

Mantine permite controlar su selección mediante **checked** y **onChange**.

Sus estados serán:

* default  
* hover  
* focus  
* selected  
* disabled

  8. ### **Pill removible**

Un filtro aplicado podrá representarse mediante **Pill**.

| \<Pill withRemoveButton\>   Nike \</Pill\> |
| :---- |

Mantine dispone de **Pill** con botón de eliminación y está pensado también para utilizarse dentro de inputs.

El botón para eliminar deberá ser accesible y no depender únicamente de una “X” sin contexto para tecnologías de asistencia.

9. ### **Loading y error**

Los estados **loading** y **error** no corresponden normalmente a un badge informativo.

Sí podrán existir en un componente de entrada de tags o selección múltiple cuando dependa de una operación asíncrona. **PillsInput**, por ejemplo, dispone de estado **loading** para búsquedas, validaciones o llamadas a API.

10. ### **Propiedades en Figma**

Para **Badge**:

| Component: Badge semantic \= neutral | info | success | warning | error | promotion size \= sm | md icon \= none | left |
| :---- |

Correspondencia de variantes:

| neutral → color/surface/cloud-subtle info → color/info/background \+ color/info/default success → color/success/background \+ color/success/default warning → color/warning/background \+ color/warning/default error → color/error/background \+ color/error/default promotion → color/accent/volt availability → color/accent/volt-soft new → color/accent/signal |
| :---- |

El estado focus utilizará color/accent/signal.

Para **Chip**:

| Component: Chip size \= sm | md state \= default | hover | focus | selected | disabled icon \= none | left |
| :---- |

Para **Pill**:

| Component: Pill size \= sm | md removable \= true | false state \= default | hover | focus | disabled |
| :---- |

El estado focus utilizará color/accent/signal.

8. ## **Componentes específicos del módulo**

Son componentes propios del Centro de Seguridad, construidos sobre los componentes base de Mantine. No existen en el marketplace y se reutilizan entre las pantallas del módulo.

### Medidor de fuerza de contraseña (PasswordStrengthMeter)

Muestra las reglas vigentes de la contraseña, descargadas de `GET /password/politica`, en tres estados por regla: **cumple**, **no cumple** y **se comprueba al guardar** (contraseña común, historial y datos personales). Informa con texto e icono, nunca solo con color; usa `aria-live="polite"` y no bloquea el envío.

| Estado | Semántica |
| --- | --- |
| Cumple | color/success |
| No cumple | color/error |
| Se comprueba al guardar | neutral (cloud-subtle) |

### Entrada de código OTP (OtpInput)

Entrada de **6 dígitos** de un solo uso. Usa `autocomplete="one-time-code"`, una casilla por dígito o un input único con separación visual, y se acompaña del canal de envío (EMAIL/SMS). Estados: vacío, parcial, incorrecto, agotado (3 intentos) y expirado (5 min).

### Campo de celular (CelularField)

Prefijo `+51` fijo fuera del input y 9 dígitos escritos por el usuario; se envía como `+51XXXXXXXXX`. Usa `inputmode="numeric"`.

### Casilla de términos (TerminosCheckbox)

Casilla nativa obligatoria con enlaces a los términos y al tratamiento de datos personales (Ley N.º 29733). El mensaje de error indica la acción requerida: «Debes aceptar los términos y el tratamiento de tus datos personales para crear tu cuenta.».

### Badge de estado de cuenta (EstadoCuentaBadge)

Badge semántico que traduce el estado de la cuenta (ver tabla de «Estados de cuenta» en Paleta de colores). No es interactivo.

### Tabla de datos del panel (DataTable)

Tabla reutilizable para el panel de administración (listado de usuarios, detalle, auditoría) con encabezado fijo, ordenamiento, paginación, estados de vacío, carga (skeleton) y error. En móvil se permite desplazamiento horizontal dentro de la tabla.

### Confirmación de contraseña

Campo adicional (solo en la interfaz, no se envía) que verifica que la contraseña coincida; su error es «Las contraseñas no coinciden.».

5. # **Organismos y patrones comunes**

   1. ## **Formulario de autenticación**

Agrupa las pantallas de inicio de sesión, registro, verificación de correo, OTP y recuperación de contraseña. Comparten estructura: título de página, campos, medidor o código, acción principal (en signal), acción secundaria y enlaces de apoyo.

* La acción principal de estas pantallas usa **color/accent/signal**.  
* Los errores aparecen bajo cada campo y con un resumen arriba; el foco va al primer campo con error.  
* El botón principal se deshabilita y muestra un indicador mientras espera la respuesta (nunca doble envío).  
* Al terminar el registro no se abre sesión: se lleva a la pantalla de verificación.

  2. ## **Navegación del módulo**

En pantallas públicas (login, registro, OTP, recuperación) la navegación es mínima: icono del módulo y enlaces de apoyo («¿Ya tienes cuenta?», «¿Olvidaste tu contraseña?»). En el panel de administración y «Mi cuenta», la navegación es un menú lateral sobre cloud-subtle, con el elemento activo marcado y un indicador de foco signal.

* Orden del menú del panel (Desktop): 1. Usuarios, 2. Roles y Permisos, 3. Auditoría, 4. Mi Cuenta.
* Versión móvil: Menú desplegable lateral (Drawer) activado mediante el icono IconMenu2.
  
3. ## **Panel de administración (listado, detalle y auditoría)**

El panel organiza cuentas y auditoría con una tabla `DataTable` y filtros (estado, rol, correo, rango de fechas). Los filtros muestran qué está activo y permiten limpiarlos sin perder el contexto; el total de resultados se muestra junto a la tabla.

* Filtros de Usuarios: Estado (ACTIVO, PENDIENTE_VERIFICACION, BLOQUEADO, INACTIVO), Rol y Búsqueda por texto (Nombre/Correo).
* Filtros de Auditoría: Rango de fechas, Tipo de evento e ID/Correo del usuario.

4. ## **Modales**

Los modales se reservan para acciones que requieren atención antes de continuar. Incluyen título, contenido, acción principal, alternativa o cancelación y un método claro para cerrarlos cuando la tarea lo permita.

* Reglas de modales: Tamaños sm (400 px), md (600 px), lg (800 px). Se cierran mediante botón X, tecla Esc o clic en el backdrop (deshabilitado si hay un proceso destructivo en curso). En móvil se adaptan a pantalla completa o estilo Bottom Sheet.
* Variantes de modales: Confirmación (acciones destructivas/bloqueos con botón rojo), Información (avisos del sistema) y Error (fallos de red o validación grave).
  
5. ## **Pantallas de carga y skeletons**

Los skeletons representan la estructura que aparecerá cuando termine la carga. Deben aproximarse al tamaño del contenido final para reducir movimientos inesperados y mantener visible el contexto de la pantalla.

* Skeletons del listado: Bloques grises animados que imitan la forma de las filas y columnas de la tabla de usuarios mientras se cargan los datos.
* Criterios de carga: Usar Skeleton para carga inicial de datos/pantallas completas; Spinner dentro del botón para acciones de guardado; y Mensaje de progreso si la carga supera los 8 segundos.
  
6. # **Reglas de trabajo y gobernanza**

   1. ## **Biblioteca central**

El archivo central se publica como Team Library de Figma. Los archivos de retail, ventas, inventario y otros módulos consumen los componentes publicados, pero no crean versiones maestras independientes de un mismo componente.

* La biblioteca central contiene foundations, componentes y patrones compartidos.  
* Cada módulo mantiene sus pantallas y flujos en su propio archivo de trabajo.  
* Las instancias se actualizan desde la biblioteca cuando se publica una nueva versión.  
* Los componentes específicos de un único módulo permanecen locales hasta demostrar que son reutilizables.

Team Library en Figma: https://www.figma.com/design/J95xYLdhSO9rzyO1i5awnn/Inka-Athletics-%E2%80%93-Design-System?node-id=45-3

2. ## **Responsabilidad de edición**

La biblioteca central tiene una sola persona responsable de editar, aprobar y publicar sus componentes. Los compañeros utilizan la biblioteca y envían propuestas; no modifican directamente los componentes maestros.

Responsable de la biblioteca: Valery Cristin Gutierrez Bendezu (encargada de aprobaciones, edición y publicaciones en Figma).

3. ## **Propuesta de un componente nuevo**

1. Comprobar que la necesidad no esté cubierta por un componente o variante existente.  
2. Describir el problema, las pantallas afectadas y por qué la solución puede reutilizarse.  
3. Adjuntar una propuesta visual dentro del archivo del módulo, sin incorporarla todavía a la biblioteca central.  
4. Incluir variantes, estados, comportamiento responsive, contenido de ejemplo y consideraciones de accesibilidad.  
5. Enviar la propuesta mediante el canal acordado para su revisión.  
6. Después de la aprobación, incorporar el componente a la biblioteca, documentarlo y publicar la actualización.

   4. ## **Criterios para aceptar una propuesta**

* Resuelve una necesidad real de una o varias pantallas.  
* Puede reutilizarse o extiende un componente existente sin duplicarlo.  
* Respeta Foundations, UX Writing y los patrones documentados.  
* Incluye los estados necesarios y el comportamiento responsive.  
* Puede utilizarse con teclado y comunica sus estados de forma comprensible.  
* Tiene equivalencia clara entre el componente de Figma y su implementación.

  5. ## **Actualización de un componente existente**

* Explicar qué problema resuelve el cambio y qué módulos se verán afectados.  
* Evitar eliminar propiedades o variantes que ya estén en uso sin revisar sus instancias.  
* Probar el cambio en al menos una pantalla representativa antes de publicarlo.  
* Registrar qué cambió y qué debe revisar cada módulo después de actualizar la biblioteca.  
* Comunicar la publicación en el canal acordado.

Formato de versionado: Registro de cambios bajo Semantic Versioning (vX.Y.Z) documentado en el archivo CHANGELOG.md de la raíz del repositorio.

6. ## **Lista de revisión antes de publicar**

* El nombre del componente sigue la convención acordada.  
* Las propiedades y variantes están ordenadas y tienen nombres comprensibles.  
* Los estados necesarios están representados.  
* El componente utiliza estilos o variables de Foundations.  
* El contenido de ejemplo sigue las reglas de UX Writing.  
* El comportamiento responsive está documentado.  
* La propuesta fue probada en una pantalla real del proyecto.  
* La actualización fue comunicada a los módulos afectados.
