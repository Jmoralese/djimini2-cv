# Contrato de integración y operación del dron

Estas reglas forman parte de la calidad de una app que actúa sobre hardware real.
No sustituyen la validación del propietario ni atribuyen capacidades al Mini 2.

## Conexión y capacidades

- Confirmar modelo, mando, Android y firmware en el primer ensayo real. Mini 2,
  Mini 2 SE y Mini 4K no son intercambiables a efectos de soporte SDK.
- La simulación tiene un indicador visible y no activa el adaptador real.
- Resolver permisos, registro SDK y conexión antes de habilitar operaciones.
  Distinguir enlace al mando de conexión efectiva a la aeronave.
- Obtener capacidades del producto/API y mostrar funciones ausentes como no
  disponibles. No deducir soporte de waypoints o Virtual Stick por ejemplos de otros modelos.

## Órdenes

- Cada orden real parte de una acción explícita del usuario en la app. Conectar,
  abrir una pantalla, rotar el móvil o restaurar estado no ejecuta movimientos.
- Antes de enviar, validar conexión, capacidad, estado requerido y frescura de
  los datos usados para decidir. Definir y probar el umbral de caducidad por función.
- Para funciones de vuelo, definir confirmación deliberada, precondiciones y
  señalización del resultado en su tarea antes de implementarlas.
- Impedir envíos duplicados mientras una orden equivalente está pendiente.
  No reintentar automáticamente órdenes físicas no idempotentes.
- Un timeout deja resultado desconocido si no puede confirmarse. Reconectar no
  reenvía la cola anterior; reconciliar primero con el estado real recibido.
- Ante pérdida de enlace o datos caducados, deshabilitar órdenes nuevas que
  dependan de esos datos y mostrar la causa. No improvisar RTH, aterrizaje ni
  cambios de failsafe como reacción de software no validada.
- Mantener el mando físico como vía de intervención; comprobar su comportamiento
  durante pruebas manuales. No prometer que cerrar la app detiene el dron.

## Pruebas escalonadas

1. **Sin hardware:** lógica, UI y fallos simulados; no se cargan librerías DJI.
2. **Banco:** teléfono/mando/dron, hélices retiradas cuando proceda y sin activar
   motores. Verificar registro, lectura, vídeo y pérdida/reconexión del enlace.
   Cerrar DJI Fly u otra app que esté ocupando el USB y comprobar selección de app.
3. **Funciones de cámara/gimbal:** ensayo manual específico con el equipo estable.
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
