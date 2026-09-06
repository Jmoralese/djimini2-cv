# Estado para el próximo agente

- Fecha: 2026-09-06.
- Último cambio: guía de referencia DJI incorporada por Codex. Relevo libre.
- Rama: `main`, sin remoto.
- Fase: documentación y herramientas; todavía no hay aplicación Android.

## Resultado

La guía oficial indicada por el propietario está en README, AGENTS y los contratos
de arquitectura/dron. El portal `https://developer.dji.com/doc/mobile-sdk-tutorial/en/`
documenta V5; las instrucciones compartidas obligan a contrastar APIs y capacidades
con V4 para el Mini 2. D-002 ampliada; no cambia el SDK elegido ni las tareas pendientes.

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

- Último cambio documental: portal DJI y contenido de su módulo público leídos;
  confirmadas notas MSDK 5.18.0. Revisión de enlaces locales y diff; no requiere
  repetir diagnóstico de PC ni ejecutar pruebas Android.

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
`git log -1 --oneline` para su identificador. No hay trabajo funcional de app
sin terminar ni remoto configurado. Comprobar `git status` al retomar: las notas
describen esta entrega, no sustituyen al estado real del checkout.

## Próximo paso después de la preparación

T-009: concretar primera rutina/tarea/modelo MediaPipe y criterios de aceptación.
T-003 y T-010: base Kotlin e inferencia real sobre assets sin DJI. T-002: confirmar
móvil/Android, mando y firmware. Después T-004/T-005/T-011: conexión, frames y
visión sobre vídeo DJI sin movimientos. T-006 queda para control visual validado.

Siguen pendientes modelo de móvil/Android y mando; no bloquean la preparación
de la variante sin DJI. La rutina y su modelo tampoco están elegidos todavía.

La clave DJI y el `applicationId` se resolverán al preparar la integración real,
sin guardar credenciales en Git. GitHub queda para una petición posterior.
