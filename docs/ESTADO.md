# Estado para el próximo agente

- Fecha: 2026-09-06.
- Último cambio: carpeta `referencias/` con capturas de DJI Fly para inspiración
  de diseño, creada por Claude a petición del propietario. Relevo libre.
- Cambio anterior: T-012, distribución y catálogo DJI V4.18 preparados por Codex.
- Rama: `main`, remoto `origin`: <https://github.com/Jmoralese/djimini2-cv>.
- Fase: documentación y herramientas; todavía no hay aplicación Android.

## Resultado

Nueva carpeta `referencias/` para inputs de diseño e ideas, fuera de la futura
app: 15 capturas de la app oficial DJI Fly (ficha de APKPure y dos guías) en
`referencias/dji-fly/`, con índice y fuentes en `referencias/README.md`. Cada
archivo se verificó como imagen real (JPEG/WEBP) y se revisó su contenido. El
README avisa de que algunas capturas son de versiones recientes de DJI Fly con
funciones que el Mini 2 no tiene (ActiveTrack, waypoints): valen como lenguaje
visual, no como catálogo de funciones. Material de terceros, solo referencia
interna; no se empaqueta en el APK ni se redistribuye.

SDK DJI mediante Maven Central: se añadió `gradle/libs.versions.toml` con runtime
y `provided` V4.18, y [INTEGRACION_DJI.md](INTEGRACION_DJI.md) con scopes, variantes
y pasos pendientes. No se copió ni añadió como submódulo el repositorio de ejemplos.
La descarga con Gradle se verificó; todavía no hay módulo Android que consuma el
catálogo ni APK. D-006 registra esta decisión para ambas IAs.

La referencia principal indicada por el propietario es el
[portal DJI Mobile SDK V4](https://developer.dji.com/mobile-sdk-v4/). README, AGENTS,
contratos de arquitectura/dron y D-002 están actualizados. Se conservan los
enlaces a guías/API Android V4 y la comprobación de capacidades por modelo.
Confirmación específica: las notas oficiales de Android SDK 4.18 nombran al
DJI Mini 2 en `Supported Products`; evidencia enlazada en D-002 y el informe de
entorno. Se mantiene pendiente la integración con el teléfono y firmware reales.

El objetivo es ejecutar visión por computador con **Google MediaPipe en el móvil**
y convertir resultados en órdenes DJI para el Mini 2 mediante el mando. El
propietario confirmó que MediaPipe sustituye a OpenCV. Biblioteca precompilada
y modelo se incluirán en el APK; no hay app MediaPipe adicional que instalar.

Contratos e instrucciones comunes actualizados; nuevo
[contrato de visión](CONTRATO_VISION.md), D-005 y tareas de modelo/inferencia/vídeo.
Control visual desactivado inicialmente; una sesión activada por el usuario
permite órdenes sucesivas acotadas, sin confirmar cada frame. Se revoca al perder
datos/parar y no se reactiva al reconectar. Los tests de desarrollo no vuelan el dron.

El [informe de entorno](ENTORNO_ANDROID.md) conserva la auditoría Android inicial
y añade la de MediaPipe. No se instaló software ni se modificaron variables globales.

## Comprobaciones realizadas

- T-012: README/build/licencia del repo DJI revisados, referencia upstream
  `37afa622a1e95f179e2d5431f0afe4203ee5ca88`. Gradle 9.2.1 + JDK 17.0.19 importaron
  el catálogo y resolvieron 14 artefactos desde Maven Central: BUILD SUCCESSFUL,
  salida 0. Se conservó fly-safe y excluyó anti-distortion como en la muestra.
  SDK y transitivos quedan en caché Gradle; build/log temporales en
  `.local/dji-dependency-check/`. Esto no prueba compilación ni ejecución Android.

- Última verificación: tabla oficial DJI y notas Android SDK 4.18 (contenido de
  la API pública de documentación) leídas; Mini 2 figura como producto soportado.
  Cambio documental revisado; no se ejecutaron pruebas con hardware.

- Diagnóstico PowerShell 5.1: salida 0; Java/Javac 17.0.19, SDK Manager 20.0,
  ADB 36.0.2, herramientas Android y WHPX disponibles.
- Scripts: parseo correcto, PATH idempotente, respeto de rutas explícitas y
  rechazo de ruta inválida; diagnóstico sin SDK devuelve 1.
- Gradle 9.2.1 de caché: `--version` correcto; no elegido aún para la app.
- AVD `Medium_Phone_API_36.1`: arranque real sin ventana y en modo lectura;
  `sys.boot_completed=1`, Android 16, x86_64. Cerrado después de comprobarlo.
- Los contratos iniciales de T-001 fueron revisados por un segundo agente.
- Nueva auditoría: Google Maven accesible; POM/AAR de Tasks Vision y Core 1.0.0
  descargados e inspeccionados en `.local/mediapipe-audit/`. Ambos declaran mínimo
  API 24; Core incluye `.so` ARM y x86/x86_64. No contienen modelos de tarea.
- NDK/CMake ausentes, sin bloqueo para la vía de bibliotecas precompiladas.
  El diagnóstico actualizado informa de estos componentes y de los límites de
  comprobación de MediaPipe; ejecutado con salida 0.
- Enlaces locales Markdown, exclusiones de archivos privados y
  `git diff --cached --check`: correctos.
- No se ha compilado APK ni ejecutado inferencia MediaPipe. La descarga de AAR
  no prueba carga nativa, modelos ni compatibilidad conjunta con DJI.
  No se ha conectado teléfono, mando o dron. Los logs temporales
  están en `.local/`, ignorados por Git.

## Estado de Git

Actualización entregada en un commit local de `main`; consultar
`git log -1 --oneline` para su identificador. El propietario pidió publicar en
GitHub: remoto `origin` configurado y `main` subida a
<https://github.com/Jmoralese/djimini2-cv>. No hay trabajo funcional de app sin
terminar. Comprobar `git status` al retomar: las notas describen esta entrega,
no sustituyen al estado real del checkout.

## Próximo paso después de la preparación

T-009: concretar primera rutina/tarea/modelo MediaPipe y criterios de aceptación.
T-003 y T-010: base Kotlin e inferencia real sobre assets sin DJI. T-002: confirmar
móvil/Android, mando y firmware. Después T-004/T-005/T-011: conexión, frames y
visión sobre vídeo DJI sin movimientos. T-006 queda para control visual validado.

Siguen pendientes modelo de móvil/Android y mando; no bloquean la preparación
de la variante sin DJI. La rutina y su modelo tampoco están elegidos todavía.

La clave DJI y el `applicationId` se resolverán al preparar la integración real,
sin guardar credenciales en Git. La licencia del código sigue sin decidirse
aunque el repositorio ya esté publicado.
