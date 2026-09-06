# Contrato de visión por computador en el móvil

## Responsabilidad

El teléfono recibe vídeo del Mini 2, lo decodifica, ejecuta **Google MediaPipe** y
produce observaciones. Una política separada convierte observaciones válidas en
intenciones de control limitadas. El adaptador DJI es el único que envía órdenes.

No se presupone un algoritmo concreto: detección de objetos, poses o seguimiento
necesitan tarea/modelo y criterios propios. La primera rutina se elegirá con el
propietario; instalar la biblioteca no crea por sí mismo un controlador de vuelo.

## Biblioteca y modelo

- Usar MediaPipe Tasks Vision precompilado, con versión fija y modelos compatibles
  empaquetados como assets. El APK instala ambos en el móvil. No instalar MediaPipe
  como otra app, ni Python/pip, ni OpenCV como dependencia directa del proyecto.
- Registrar para cada modelo: tarea, URL de origen, versión o hash, licencia,
  entradas, etiquetas y parámetros relevantes. No copiar un `latest.release`
  de ejemplos oficiales al build reproducible.
- Validar carga, recursos y modelo antes de ofrecer la activación. Liberar tareas,
  imágenes y buffers al cerrar; respetar el hilo/contexto que requiera el delegado.
- Empezar con CPU. GPU es una optimización con pruebas propias de estabilidad,
  temperatura y latencia. Un cambio de delegado durante control requiere detener
  la sesión y volver a validar; no ocultar un fallo de inferencia.

## Entrada de vídeo

- El flujo DJI comprimido no es una imagen lista para MediaPipe. Decodificar con
  las APIs V4 comprobadas y convertir al formato de `MPImage` que acepte la tarea.
  Verificar color, dimensiones, stride, recorte, rotación y coordenadas de salida.
- `DJICodecManager` ofrece callbacks YUV, pero deben validarse en Mini 2/móvil,
  incluida su convivencia con la previsualización. No asumir que todo callback
  equivale a un formato YUV concreto o a un buffer que puede retenerse sin copia.
- Cada frame/resultado conserva sesión, identificador y tiempos monotónicos.
  Los timestamps de MediaPipe deben cumplir el orden requerido por la API. El
  tiempo de recepción en el móvil no certifica el tiempo de captura en el dron.
- Acotar la cola de frames **decodificados** y preferir el más reciente. No tirar
  bytes H.264 arbitrariamente; respetar el decodificador. MediaPipe LIVE_STREAM
  puede ignorar una entrada si está ocupado: no esperar un callback por cada frame.
- La prueba sin hardware usa imágenes/clips representativos y conocidos; no exige
  permiso de cámara del móvil solo por analizar vídeo recibido del DJI.

## De la observación a una acción

- Conservar objetivo/identidad cuando proceda, confianza, transformación geométrica
  y antigüedad. Rechazar resultados caducados, fuera de orden, de otra sesión o sin
  objetivo válido. Una caja en píxeles no es una distancia en metros sin calibración.
- Cada rutina define condiciones de entrada, umbrales de confianza/estabilidad,
  caducidad, unidades, ejes/signos, límites de velocidad/duración y protocolo de
  salida. No decidir estos límites mediante supuestos silenciosos al implementar.
- La rutina empieza en **solo análisis**. El usuario activa una sesión de control
  con objetivo y límites visibles; esa sesión autoriza órdenes sucesivas dentro
  de su alcance. Debe poder detenerla sin esperar a que termine la inferencia.
- Separar FPS de vídeo, inferencias por segundo y frecuencia de órdenes. El emisor
  no depende de que llegue un nuevo resultado para detectar una intención caducada.
- Pérdida de vídeo/objetivo, bloqueo de inferencia, datos antiguos, pausa de app o
  pérdida de enlace invalidan la intención y revocan la sesión. Aplicar únicamente
  la salida previamente validada según [CONTRATO_DRON.md](CONTRATO_DRON.md).
  Reconectar o recuperar una detección no reactiva control automáticamente.

## Criterios antes del control real

Primero verificar inferencia con assets de prueba y decisiones con dron simulado.
Después comprobar vídeo DJI + MediaPipe en banco en modo solo análisis. Medir en
el móvil resolución, FPS, latencia de cada tramo y extremo a extremo cuando sea
observable, variación, descartes, memoria y carga térmica. Definir límites de
aceptación de la rutina y probar pérdida de objetivo/vídeo antes de habilitarla.

Tests JVM verifican la política usando observaciones falsas. Pruebas Android con
el modelo real verifican inferencia, conversiones y carga nativa. Un emulador no
certifica todas las tareas MediaPipe, GPU ni rendimiento con el dron.

## Fuentes técnicas

- [Google: instalación Android y bibliotecas precompiladas](https://developers.google.com/edge/mediapipe/solutions/setup_android).
- [Google: vídeo, LIVE_STREAM y callbacks](https://developers.google.com/edge/mediapipe/solutions/vision/object_detector/android).
- [DJI V4: decodificador y salida YUV](https://developer.dji.com/api-reference/android-api/Components/CodecManager/DJICodecManager.html).
- [DJI V4: contrato del callback YUV](https://developer.dji.com/api-reference/android-api/Components/CodecManager/DJICodecManager_YuvDataCallbackInterface.html).

Las reglas de sesiones, caducidad y límites son decisiones del proyecto, no
garantías ofrecidas por MediaPipe o DJI.
