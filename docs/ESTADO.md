# Estado para el próximo agente

- Fecha: 2026-09-06.
- Tarea: T-001 completada por Codex. Relevo libre al finalizar esta sesión.
- Rama: `main`, sin remoto.
- Fase: documentación y herramientas; todavía no hay aplicación Android.

## Resultado

Contratos de producto, arquitectura, calidad y operación del dron; instrucciones
comunes Codex/Claude; Git local inicializado y exclusiones de secretos/compilaciones.
Entorno de Android instalado, pero las herramientas no estaban en el PATH de la
terminal. Scripts de activación por sesión y diagnóstico repetible añadidos.
El [informe de entorno](ENTORNO_ANDROID.md) incluye inventario, límites del SDK V4
y preparación de hardware. No se instaló software ni se modificaron variables globales.

## Comprobaciones realizadas

- Diagnóstico PowerShell 5.1: salida 0; Java/Javac 17.0.19, SDK Manager 20.0,
  ADB 36.0.2, herramientas Android y WHPX disponibles.
- Scripts: parseo correcto, PATH idempotente, respeto de rutas explícitas y
  rechazo de ruta inválida; diagnóstico sin SDK devuelve 1.
- Gradle 9.2.1 de caché: `--version` correcto; no elegido aún para la app.
- AVD `Medium_Phone_API_36.1`: arranque real sin ventana y en modo lectura;
  `sys.boot_completed=1`, Android 16, x86_64. Cerrado después de comprobarlo.
- Contratos revisados por un segundo agente; sin contradicciones sustanciales detectadas.
- Enlaces locales Markdown, exclusiones de archivos privados y
  `git diff --cached --check`: correctos.
- No se ha compilado APK ni conectado teléfono, mando o dron. Los logs temporales
  están en `.local/`, ignorados por Git.

## Estado de Git

Base entregada en el commit inicial de preparación de `main`; consultar
`git log -1 --oneline` para su identificador. No hay trabajo funcional de app
sin terminar ni remoto configurado. Comprobar `git status` al retomar: las notas
describen esta entrega, no sustituyen al estado real del checkout.

## Próximo paso después de la preparación

T-002: confirmar móvil, Android, mando y firmware. T-003: crear la base simulada
Kotlin con toolchain fijado y pruebas reales de compilación/instalación.

Se pidió al propietario modelo de móvil/Android y mando; respuesta pendiente al
cerrar esta preparación. No bloquea el desarrollo de la variante simulada.

La clave DJI y el `applicationId` se resolverán al preparar la integración real,
sin guardar credenciales en Git. GitHub queda para una petición posterior.
