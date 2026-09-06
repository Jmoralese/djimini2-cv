# Contrato de producto

## Objetivo y alcance

Una app personal Android nativa cuyo objetivo principal es ejecutar rutinas de
visión por computador con **Google MediaPipe en el teléfono** sobre imágenes del
DJI Mini 2 original. El móvil interpreta los resultados y envía instrucciones
concretas al dron mediante el SDK y el mando. Las rutinas propias no necesitan
ejecutarse a bordo del dron. Se priorizan datos fiables y control del propietario.

La biblioteca MediaPipe y sus modelos compatibles se empaquetan con la app.
La instalación de nuestra app prepara también la visión en el móvil. No se
requiere instalar otra aplicación MediaPipe ni mantener conexión a un servidor
para la inferencia local. MediaPipe sustituye a OpenCV en el alcance acordado.

No se incluye iOS. No se necesitan servidor, registro de usuarios, suscripciones,
analítica externa ni publicación en tiendas para el primer uso personal.
Las funcionalidades concretas de vuelo se definirán con el propietario; esta
preparación del repo no equivale a autorización para ejecutar comandos en el dron.

## Entrega por etapas

| Etapa | Resultado comprobable |
| --- | --- |
| 0. Preparación | Entorno auditado, contratos compartidos y Git local |
| 1. Visión sin dron | App Kotlin instalable; modelo/tarea MediaPipe definidos; análisis de imágenes o clips de prueba, resultados visibles y decisiones simuladas |
| 2. Viabilidad real | Registro SDK y conexión USB teléfono/mando/Mini 2; lectura de estado en banco, sin activar motores |
| 3. Visión sobre vídeo DJI | Frames decodificados analizados por MediaPipe en el móvil; resultados visibles, medidas de latencia/carga y sin órdenes de movimiento |
| 4. Control visual | Una rutina acordada convierte observaciones en órdenes limitadas durante una sesión activada por el usuario, con parada y prueba manual específicas |

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
- La UI diferencia análisis visual de control visual activo. Muestra objetivo,
  observación y pérdida de detección sin presentar inferencias como certezas.
- La visión puede funcionar y probarse sin mover el dron. Cada rutina define qué
  detecta, con qué modelo y qué acciones puede producir, antes de habilitar control.

## Fuera del alcance inicial

Navegación autónoma general, rutas/waypoints a bordo, múltiples drones, nube,
entrenamiento de modelos y modificaciones de firmware. Una rutina concreta de
seguimiento visual puede proponerse como tarea; no se presume ya implementada ni
se excluye del objetivo de control desde el móvil. No se atribuye evitación de
obstáculos o navegación segura a una detección visual.

## Cambios de alcance

Una petición nueva se convierte en una tarea pequeña con resultado observable y
forma de verificarlo. No hace falta un documento adicional para cada pantalla.
Si cambia este contrato, actualizarlo junto con la implementación y explicar por qué.
