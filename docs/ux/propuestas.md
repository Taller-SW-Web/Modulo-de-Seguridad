# Propuestas de UX: Flujo Principal (Login + MFA)

## Propuesta A: Navegación Secuencial (Pantalla Completa)
El usuario ingresa sus credenciales en FRONT-01 y es redirigido a una nueva pantalla dedicada FRONT-04 para ingresar el código OTP.
- **Ventajas:** Enfoca al usuario en una sola tarea, ideal para dispositivos móviles.
- **Desventajas:** Requiere navegación de página completa en la SPA.

## Propuesta B: Modal Superpuesto
Al validar credenciales en FRONT-01, se despliega una ventana modal centrado sobre la pantalla con las casillas del OTP.
- **Ventajas:** Mantiene el contexto visual, flujo rápido sin cambiar de URL.
- **Desventajas:** Puede saturar pantallas móviles muy pequeñas.

## Propuesta C: Despliegue Inline (Expansión)
La misma tarjeta de login se expande verticalmente mostrando las casillas del OTP en el mismo contenedor.
- **Ventajas:** Transición fluida dentro del mismo elemento visual.
- **Desventajas:** Puede confundir al usuario si intenta modificar el correo ingresado.
