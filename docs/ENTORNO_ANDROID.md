# Entorno y compatibilidad: DJI Mini 2 y MediaPipe

El inventario del PC (Java, SDK, emulador, ADB) y cómo activarlo por sesión
están en `../../../operacion/ENTORNO_ANDROID.md`, común a todos los proyectos
de apps móviles. Este documento guarda lo que solo importa aquí: la
compatibilidad del DJI Mini 2 con el MSDK y la preparación de MediaPipe.
Auditado el **2026-09-06**.

Los scripts de este proyecto siguen siendo la vía para activar y diagnosticar
el entorno desde PowerShell:

```powershell
. .\scripts\Use-AndroidEnv.ps1
powershell -NoProfile -File .\scripts\Test-AndroidEnvironment.ps1
```

`Test-AndroidEnvironment.ps1` se ejecuta como proceso, no con dot-sourcing, y
termina con código 0 si están las herramientas básicas. El 0 no certifica
compatibilidad DJI ni que haya un teléfono disponible.

## Compatibilidad del DJI Mini 2

- **Modelo:** DJI confirma Mobile SDK para Mini 2 original; Mini 2 SE y Mini 4K
  figuran sin soporte. [Tabla de compatibilidad DJI](https://repair.dji.com/help/content?customId=01700000763&lang=en&paperDocType=ARTICLE&re=US&spaceId=17).
- **SDK:** la referencia oficial V4 es **4.18**. Mini 2 no aparece en la lista
  de V5. [Repositorio V4](https://github.com/dji-sdk/Mobile-SDK-Android),
  [repositorio V5](https://github.com/dji-sdk/Mobile-SDK-Android-V5).
- **Confirmación específica V4/Mini 2:** las notas Android SDK **4.18** incluyen
  expresamente **DJI Mini 2** en `Supported Products`. Se volvió a leer el contenido
  oficial el 2026-09-06; no se deduce el soporte solo del nombre del portal V4.
  [Notas oficiales de la versión](https://developer.dji.com/document/122b5969-d64d-4752-8452-400c41240aa5).
- **Android:** V4.18, publicada el 24-10-2024, anuncia adaptación a Android 14.
  Lista firmware Mini 2 `01.07.0200`; es una referencia de la release, no una
  orden de actualizar el dron. [Notas oficiales DJI](https://developer.dji.com/document/122b5969-d64d-4752-8452-400c41240aa5).
- **Mantenimiento:** soporte DJI indicó en agosto de 2025 que V4 no recibiría
  más actualizaciones iterativas y que podría no adaptarse a Android 15.
  Android 15/16 no se declaran compatibles ni incompatibles sin ensayo concreto.
  [Respuesta oficial dji-dev](https://github.com/dji-sdk/Mobile-SDK-Android/issues/1343#issuecomment-3226686996).
- **Bibliotecas nativas:** la muestra V4.18 usa `armeabi-v7a` y `arm64-v8a`.
  El AVD x86_64 del PC servirá para la variante simulada. La muestra oficial
  documenta problemas x86; no es una prueba de DJI real.
  [Build de la muestra DJI](https://github.com/dji-sdk/Mobile-SDK-Android/blob/master/Sample%20Code/app/build.gradle).
- **Páginas de memoria:** en móviles que usen páginas de 16 KB hay que comprobar
  compatibilidad de las bibliotecas nativas. No todos los móviles Android 15+
  usan 16 KB. [Documentación Android](https://developer.android.com/guide/practices/page-sizes).
- **Simulación:** las notas DJI 4.16 señalan que Mini 2 no puede usar DA2 Simulator.
  El simulador MSDK usa el controlador de vuelo real; no sustituye al dron.
  Nuestra simulación será un doble propio sin hardware.
  [Notas DJI](https://developer.dji.com/document/122b5969-d64d-4752-8452-400c41240aa5),
  [pruebas y simulador MSDK](https://developer.dji.com/mobile-sdk/documentation/application-development-workflow/workflow-testing.html).

La página de notas DJI carga el contenido dinámicamente. Se verificó también la
[API pública que usa esa página](https://dev.dji.com/api/v1/documents/detail?content_uid=122b5969-d64d-4752-8452-400c41240aa5).
En la auditoría posterior del repositorio DJI se descargaron SDK 4.18, `provided`
y dependencias transitivas mediante Gradle/Maven Central. Quedan en la caché local,
fuera de Git. La resolución del catálogo pasó; el SDK aún no está integrado en
una app ni se ha ejecutado en Android. Ver [INTEGRACION_DJI.md](INTEGRACION_DJI.md).

## MediaPipe: preparación comprobada y pendientes

### Ruta elegida

**Google MediaPipe sustituye a OpenCV**, según confirmación del propietario.
Usar `com.google.mediapipe:tasks-vision` desde Kotlin y empaquetar un modelo
compatible en los assets de la app. El APK instala sus bibliotecas y modelo en
el móvil; la inferencia visual se ejecuta allí. No se requiere Python/pip,
OpenCV Manager, servidor propio ni instalar MediaPipe por separado.

Google documenta Android SDK/API **24 o superior**, IDE Android y bibliotecas
precompiladas. El móvil también debe cumplir ese mínimo y los de las demás
dependencias. Algunas tareas pueden no funcionar en un emulador. CPU será la
referencia; GPU y rendimiento sostenido se comprobarán en el teléfono.
[Guía oficial de configuración](https://developers.google.com/edge/mediapipe/solutions/setup_android).

### Paquetes inspeccionados

Se consultó Google Maven por HTTPS desde este PC. La metadata anunció `1.0.0`
y se descargaron POM y AAR de `tasks-vision:1.0.0` y su dependencia
`tasks-core:1.0.0` a `.local/mediapipe-audit/`. Es una **versión candidata auditada**,
no una dependencia ya fijada en el proyecto ni una combinación certificada con DJI.

| Comprobación | Resultado |
| --- | --- |
| Acceso a Google Maven | Correcto, metadata/POM/AAR descargados |
| Manifest de ambos AAR | `minSdkVersion=24`; no determina el targetSdk final de nuestra app |
| API Tasks Vision | AAR precompilado, dependencia de Tasks Core de la misma versión |
| Biblioteca nativa en Tasks Core | `libmediapipe_tasks_jni.so` para arm64-v8a, armeabi-v7a, x86 y x86_64 |
| Modelos `.tflite`/`.task` en esos AAR | Ninguno; hay que seleccionar e incorporar el modelo de la rutina |
| Compilación Gradle y dependencias transitivas | No probadas; no existe proyecto Android todavía |
| Carga nativa / inferencia / GPU | No probadas |
| Convivencia MediaPipe + DJI y páginas de 16 KB | Pendiente en APK y móvil reales |

Evidencia de distribución: [metadata Tasks Vision](https://dl.google.com/dl/android/maven2/com/google/mediapipe/tasks-vision/maven-metadata.xml),
[POM Tasks Vision 1.0.0](https://dl.google.com/dl/android/maven2/com/google/mediapipe/tasks-vision/1.0.0/tasks-vision-1.0.0.pom),
[AAR Tasks Core 1.0.0](https://dl.google.com/dl/android/maven2/com/google/mediapipe/tasks-core/1.0.0/tasks-core-1.0.0.aar).

No se compiló MediaPipe desde fuentes: las `.so` ya están en el paquete.
Por esta vía no hace falta preparar NDK/CMake/Bazel ahora. Si se decide escribir
C++/JNI, compilar grafos personalizados o modificar la biblioteca, habrá que
auditar e instalar las herramientas específicas de esa alternativa.

### Lo que falta

1. Elegir la primera rutina visual y una tarea/modelo compatible, con sus entradas,
   objetivo y métricas. Los modelos son específicos de cada tarea; no cualquier
   `.tflite` sirve. [Requisitos de modelos, ejemplo de detección de objetos](https://developers.google.com/edge/mediapipe/solutions/vision/object_detector).
2. Crear el proyecto Kotlin/Gradle y añadir repositorio `google()`, versión exacta
   de `tasks-vision`, dependencias transitivas y assets del modelo. Fijar la matriz
   de herramientas y compilar/instalar una variante sin DJI para probar inferencia.
3. Confirmar móvil/Android/ABI/tamaño de página y conectar ADB. El SDK del PC es
   suficiente para empezar; la potencia y compatibilidad del móvil son desconocidas.
4. Probar la decodificación del vídeo DJI y su conversión a `MPImage`; luego ejecutar
   visión sin movimientos y medir la latencia conjunta. Habilitar una rutina de
   órdenes solo después de validar [visión](CONTRATO_VISION.md) y [control](CONTRATO_DRON.md).

No hay una carencia de herramientas básicas que obligue a instalar software
adicional ahora para la vía precompilada. **El entorno completo de ejecución aún
no está validado**: faltan app, modelo, teléfono y prueba de integración.

## Lo que falta para la prueba real

1. Confirmar modelo y Android del móvil, modelo de mando, y firmware actual del
   dron/mando. No hacen falta números de serie.
2. Teléfono compatible, mando, Mini 2 y cable de datos adecuado. Activar opciones
   de desarrollador y depuración, y autorizar el PC desde el teléfono.
3. Comprobar ADB; si Windows necesita driver del fabricante, instalar el oficial.
   Cuando el mando ocupa el USB del móvil, Android 11+ permite emparejar ADB por
   Wi-Fi en una red apropiada. [Pruebas en dispositivo Android](https://developer.android.com/studio/run/device).
4. Elegir `applicationId`, registrar la app en DJI Developer y obtener su App Key,
   guardándola fuera de Git. El primer registro SDK necesita acceso a Internet.
   [Registro y ejecución DJI](https://developer.dji.com/mobile-sdk/documentation/application-development-workflow/workflow-run.html).
5. Compilar e instalar la variante real y ensayar registro, conexión, lectura y
   reconexión en banco según el [contrato del dron](CONTRATO_DRON.md).

### Matriz que se completará con hardware

| Dato | Estado |
| --- | --- |
| Dron | Mini 2 según el propietario; pendiente confirmar en SDK |
| Mando y firmware | Pendiente |
| Firmware de dron | Pendiente |
| Teléfono y Android | Pendiente |
| ABI / tamaño de página | Pendiente; consultar `ro.product.cpu.abilist` y `getconf PAGE_SIZE` por ADB |
| Registro DJI / USB / telemetría | No probados |
| APK / SDK / toolchain probados | No existen todavía |
