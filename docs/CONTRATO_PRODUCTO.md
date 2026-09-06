# Contrato de producto

## Objetivo y alcance

Una app personal Android nativa para que el propietario consulte y controle su
DJI Mini 2 original desde un teléfono conectado al mando. Se priorizan conexión
comprensible, información fiable y control deliberado del usuario.

No se incluye iOS. No se necesitan servidor, registro de usuarios, suscripciones,
analítica externa ni publicación en tiendas para el primer uso personal.
Las funcionalidades concretas de vuelo se definirán con el propietario; esta
preparación del repo no equivale a autorización para ejecutar comandos en el dron.

## Entrega por etapas

| Etapa | Resultado comprobable |
| --- | --- |
| 0. Preparación | Entorno auditado, contratos compartidos y Git local |
| 1. Base simulada | App instalable, pantalla de conexión/telemetría con datos simulados identificados y pruebas sin DJI |
| 2. Viabilidad real | Registro SDK y conexión USB al conjunto teléfono/mando/Mini 2; lectura de estado en banco, sin activar motores |
| 3. Uso básico | Vídeo, cámara y gimbal que admita el modelo, con estados de error y verificación en hardware |
| 4. Control de vuelo | Solo funciones acordadas, con criterios y prueba manual específicos antes de considerarlas utilizables |

Cada etapa valida la anterior. Si el SDK falla en el teléfono, se resuelve o se
documenta esa limitación antes de ampliar funcionalidades dependientes.

## Comportamiento mínimo

- La interfaz diferencia simulación de hardware real y muestra conexión, permisos
  faltantes y errores de registro sin bloquearse.
- La telemetría presenta unidades y antigüedad. Un valor desconocido no se muestra
  como cero; un dato antiguo no aparece como información actual.
- Los controles no disponibles tienen una causa comprensible y no envían comandos.
- Una acción muestra si está pendiente, completada, rechazada o con resultado
  desconocido. Un envío aceptado no equivale a una maniobra completada.
- La app soporta desconexión, reconexión y cambios de ciclo de vida sin reenviar
  órdenes antiguas ni ocultar la pérdida de estado.

## Fuera del alcance inicial

Misiones autónomas, rutas/waypoints, seguimiento automático, múltiples drones,
integración con la nube y modificaciones de firmware. No se presume que las
misiones disponibles para otros modelos funcionen en el Mini 2.

## Cambios de alcance

Una petición nueva se convierte en una tarea pequeña con resultado observable y
forma de verificarlo. No hace falta un documento adicional para cada pantalla.
Si cambia este contrato, actualizarlo junto con la implementación y explicar por qué.
