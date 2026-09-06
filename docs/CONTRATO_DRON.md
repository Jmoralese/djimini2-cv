# Contrato de integración y operación del dron

Estas reglas forman parte de la calidad de una app que actúa sobre hardware real.
No sustituyen la validación del propietario ni atribuyen capacidades al Mini 2.

La [guía oficial Mobile SDK aportada por el propietario](https://developer.dji.com/doc/mobile-sdk-tutorial/en/)
es referencia de consulta del desarrollo. Su contenido corresponde a V5; para
conexión, vídeo y órdenes del Mini 2 se verifican las APIs y restricciones V4
según el [contrato de arquitectura](CONTRATO_ARQUITECTURA.md). Un ejemplo de esa
guía no demuestra por sí solo compatibilidad con este dron.

## Conexión y capacidades

- Confirmar modelo, mando, Android y firmware en el primer ensayo real. Mini 2,
  Mini 2 SE y Mini 4K no son intercambiables a efectos de soporte SDK.
- La simulación tiene un indicador visible y no activa el adaptador real.
- Resolver permisos, registro SDK y conexión antes de habilitar operaciones.
  Distinguir enlace al mando de conexión efectiva a la aeronave.
- Obtener capacidades del producto/API y mostrar funciones ausentes como no
  disponibles. No deducir soporte de waypoints o Virtual Stick por ejemplos de otros modelos.

## Órdenes

- Cada orden real parte de una acción manual explícita o de una sesión de control
  visual activada deliberadamente por el usuario y dentro de su alcance. La sesión
  permite órdenes sucesivas sin una pulsación por frame. Conectar, abrir una
  pantalla, rotar el móvil o restaurar estado no ejecuta ni reactiva movimientos.
- Antes de enviar, validar conexión, capacidad, estado requerido y frescura de
  los datos usados para decidir. Definir y probar el umbral de caducidad por función.
- Para funciones de vuelo, definir confirmación deliberada, precondiciones y
  señalización del resultado en su tarea antes de implementarlas.
- Para acciones discretas, impedir duplicados mientras una orden equivalente está pendiente.
  No reintentar automáticamente órdenes físicas no idempotentes.
- Para consignas continuas, usar un emisor único a la cadencia requerida por el
  SDK con unidades/modos/límites definidos; repetir una consigna vigente no es un
  reintento de despegue. No usar la frecuencia de inferencia como reloj de control.
- Un timeout deja resultado desconocido si no puede confirmarse. Reconectar no
  reenvía la cola anterior; reconciliar primero con el estado real recibido.
- Ante pérdida de enlace o datos caducados, deshabilitar órdenes nuevas que
  dependan de esos datos y mostrar la causa. No improvisar RTH, aterrizaje ni
  cambios de failsafe como reacción de software no validada.
- Mantener el mando físico como vía de intervención; comprobar su comportamiento
  durante pruebas manuales. No prometer que cerrar la app detiene el dron.

## Control visual desde el teléfono

MediaPipe produce observaciones; la política decide y el SDK DJI ejecuta. Antes
de activar una rutina, verificar modelo/capacidad, modo de control, ejes/signos,
telemetría y estado de vuelo requeridos. Un cambio de sesión invalida callbacks,
objetivos y consignas antiguas. El usuario dispone de una parada visible.

Virtual Stick es una vía para Mini 2 confirmada por soporte DJI. La API V4 indica
envío entre 5 y 25 Hz y disponibilidad del modo como precondición. Fijar una
cadencia dentro de ese rango y comprobarla en la implementación; validar de nuevo
los modos tras reconectar. [Soporte DJI sobre Mini 2](https://github.com/dji-sdk/Mobile-SDK-Android/issues/1282#issuecomment-2041366665),
[FlightController V4](https://developer.dji.com/api-reference/android-api/Components/FlightController/DJIFlightController.html).

Ante parada, objetivo perdido, vídeo congelado, bloqueo de inferencia o salida a
segundo plano, revocar la sesión y ejecutar la salida validada para ese modo si
hay enlace. No suponer que dejar de enviar o mandar ceros equivale siempre a
frenar: depende del modo y estado. Si no hay enlace, no puede garantizarse la
ejecución de una orden. Diferenciar pérdida app/mando de pérdida mando/dron.

El [contrato de visión](CONTRATO_VISION.md) define la vigencia de observaciones y
sesiones. El control se ensaya después del análisis visual sin movimientos.

## Pruebas escalonadas

1. **Sin hardware:** lógica, UI y fallos simulados; no se cargan librerías DJI.
2. **Banco:** teléfono/mando/dron, hélices retiradas cuando proceda y sin activar
   motores. Verificar registro, lectura, vídeo y pérdida/reconexión del enlace.
   Cerrar DJI Fly u otra app que esté ocupando el USB y comprobar selección de app.
3. **Visión:** vídeo DJI + MediaPipe en solo análisis, sin órdenes de movimiento;
   probar pérdida de vídeo/objetivo y carga sostenida. Cámara/gimbal, si la rutina
   los necesita, se verifican mediante ensayo manual específico.
4. **Vuelo:** solo con el propietario operando, prueba acordada para una función,
   condiciones adecuadas, posibilidad de abortar y comprobaciones previas del equipo.

Los agentes de desarrollo no mandan órdenes de motores o vuelo mediante scripts,
ADB ni tests automáticos. No cambiar firmware, límites, geofencing ni parámetros
de seguridad para hacer pasar una prueba de integración.

## Criterios para abortar un ensayo

Estado inesperado, pérdida de telemetría, respuesta incoherente, comando duplicado
o pérdida de control: detener la prueba y registrar la incidencia. El propietario
decide cómo intervenir físicamente; la app no inventa una maniobra correctora.

Para afirmar que una función funciona, registrar versión de app/SDK, modelos,
Android, firmware, procedimiento y resultado. Una prueba de banco no se etiqueta
como prueba de vuelo. Consultar [entorno y compatibilidad](ENTORNO_ANDROID.md).
