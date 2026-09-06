# Contrato de arquitectura

## Base y decisiones todavía abiertas

- Android nativo, Kotlin y Android Studio. Jetpack Compose es la opción inicial
  para UI nueva; una vista Android tradicional puede integrar vídeo del SDK.
- DJI Mobile SDK V4, referencia inicial 4.18. No sustituirlo por V5 por ser más nuevo.
- Google MediaPipe Tasks Vision, dependencia `com.google.mediapipe:tasks-vision`
  con versión exacta validada al integrar. Biblioteca precompilada y modelos en
  la app; acceso desde Kotlin a sus APIs Android. No se requiere un runtime aparte.
- Android API 24 como mínimo de partida para MediaPipe Tasks; el mínimo efectivo
  será el mayor que exijan las bibliotecas/funciones elegidas y el móvil validado.
- Un solo módulo `app` al empezar, organizado por responsabilidades; crear módulos
  adicionales solo si simplifican una necesidad demostrada.
- Fijar JDK, Gradle Wrapper, Android Gradle Plugin, Kotlin, compile/target/min SDK
  y dependencias cuando se cree y compruebe el primer proyecto. La instalación
  más reciente del PC no demuestra compatibilidad del SDK DJI.

## Guía de referencia DJI

Consultar la documentación oficial antes de implementar o modificar integración
DJI. Referencias verificadas el 2026-09-06:

| Referencia | Uso en este proyecto |
| --- | --- |
| [Mobile SDK Tutorial, indicado por el propietario](https://developer.dji.com/doc/mobile-sdk-tutorial/en/) | Guía oficial de consulta; actualmente documenta MSDK V5 |
| [Guía de desarrollo V4](https://developer.dji.com/mobile-sdk/documentation/introduction/index.html) | Integración y flujo de desarrollo aplicables a la línea del Mini 2 |
| [SDK y ejemplos oficiales Android V4](https://github.com/dji-sdk/Mobile-SDK-Android) | Contrastar versión, dependencias e implementación de APIs V4 |

El portal aportado presenta notas de MSDK 5.18.0 y tutoriales V5. No copiar sus
dependencias, clases o capacidades a V4 por analogía. Para implementar prevalecen
las referencias de la versión y modelo usados; consultar también las APIs V4
enlazadas en los contratos de [dron](CONTRATO_DRON.md) y [visión](CONTRATO_VISION.md).
Si una función solo está documentada en V5, verificar su equivalente y soporte
V4/Mini 2 o dejarla como no confirmada. El enlace no cambia la decisión D-002.

## Límites del código

```text
UI -> ViewModel / estado de pantalla -> lógica de aplicación -> DroneGateway
                                                            |-> simulación
                                                            |-> adaptador DJI V4

Vídeo DJI / clips de prueba -> FrameSource -> VisionProcessor (MediaPipe)
                                                  | observaciones propias
                                                  v
                       política de control -> validación -> DroneGateway
```

Los nombres son orientativos; los límites son el contrato:

- La UI representa estado y recoge intenciones. No llama directamente a DJI.
- La lógica de aplicación decide si una acción está permitida. No depende de
  clases DJI ni del ciclo de vida de una Activity.
- `DroneGateway` expone operaciones necesarias, capacidades, conexión y telemetría
  con tipos propios. No convertirlo en copia de toda la API DJI.
- El adaptador real concentra registro SDK, USB, callbacks, errores y traducciones.
  El simulado implementa el mismo comportamiento observable, incluidos fallos.
- El modo simulado no carga bibliotecas nativas DJI. La separación se hará con
  variantes/source sets u otra solución pequeña comprobada al crear la app.
- MediaPipe queda detrás de `VisionProcessor`. Sus resultados se traducen a
  observaciones propias; la política de control no depende de clases MediaPipe.
  El procesador no envía órdenes. `FrameSource` permite probar con clips/imágenes
  sin iniciar el SDK DJI ni usar obligatoriamente la cámara del teléfono.
- Simular el dron no implica simular MediaPipe: probar inferencia real sobre
  datos de prueba en Android y usar dobles de visión para los tests JVM de control.
- Mantener estos límites en paquetes del módulo inicial; no crear un framework
  de pipelines. Los detalles verificables están en [CONTRATO_VISION.md](CONTRATO_VISION.md).

## Estado y concurrencia

- Estados explícitos: desconectado, conectando, conectado y error, distinguiendo
  inicialización/registro de conexión física cuando corresponda.
- Datos inmutables hacia UI, unidades explícitas y tiempo de actualización para
  telemetría. Usar tiempo monotónico para caducidad y timeouts.
- Coroutines/Flow para trabajo asíncrono cuando resulten útiles; sin `GlobalScope`,
  bloqueo del hilo principal ni listeners que sobrevivan a su propietario.
- Registrar y retirar callbacks de forma simétrica; descartar callbacks de una
  sesión antigua tras reconexión. Las órdenes no son estado persistente reejecutable.
- Modelar error y resultado incierto. No tragar excepciones ni simular éxito.
- Decodificación e inferencia fuera del hilo UI y callbacks DJI. Acotar el trabajo
  pendiente y descartar frames decodificados antiguos; no acumular retardo.
- Separar cadencias de visión y control. Un emisor único vuelve a validar cada
  intención antes de enviar; nunca mantiene indefinidamente una orden antigua.

## Configuración y dependencias

- Versiones exactas en un único lugar, preferiblemente catálogo Gradle; sin `+`
  ni actualizaciones masivas mezcladas con funcionalidad.
- Versionar el wrapper completo y su checksum de distribución. Registrar la
  combinación de herramientas que realmente compila.
- Evitar frameworks de inyección al empezar: composición explícita suficiente.
- Clave DJI fuera de Git; documentar la inyección local al crear el proyecto.
  Si falta, el modo real falla de forma clara y la simulación sigue disponible.
- La clave incrustada en un APK no es un secreto imposible de extraer. Nunca
  incrustar credenciales de cuenta DJI ni claves privadas de firma.
- Persistir solo preferencias necesarias. No guardar órdenes pendientes de vuelo.
- Seleccionar una tarea MediaPipe y modelo compatibles, con origen, versión/hash,
  licencia, etiquetas y parámetros registrados. Modelo ausente/inválido impide
  activar control visual; no se descarga silenciosamente durante una sesión.
- CPU como referencia inicial. GPU se activa únicamente tras comprobar compatibilidad
  y rendimiento en el móvil. NDK/CMake/Bazel no son requisitos de partida para
  consumir el AAR precompilado; incorporarlos si se decide compilar código nativo propio.

## Cómo evoluciona

Una decisión que cambie estos límites o la compatibilidad se registra en
[DECISIONES.md](DECISIONES.md), con evidencia y consecuencias. No crear capas,
interfaces o documentos adicionales solo para parecer un proyecto empresarial.
