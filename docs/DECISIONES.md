# Decisiones técnicas

Registro corto; añadir entradas solo cuando condicionen trabajo posterior.
Formato: fecha, decisión, motivo/evidencia y cuándo revisarla.

## D-001 · Android nativo y proyecto personal · 2026-09-06

**Decisión:** Kotlin, Android Studio, sin iOS ni backend inicial. Un módulo de app
al empezar y UI nueva preferentemente Compose.

**Motivo:** el propietario pide Android nativo y calidad proporcionada a un uso
personal. La separación en paquetes y un adaptador DJI basta inicialmente.

**Revisión:** si una necesidad real exige otro módulo o integración; no por simetría.

## D-002 · Línea DJI Mobile SDK V4 · 2026-09-06

**Decisión:** usar V4 para el Mini 2 original. Versión 4.18 fijada en el catálogo
del proyecto; integración Android y prueba en el teléfono pendientes.

**Evidencia:** [repositorio oficial V4](https://github.com/dji-sdk/Mobile-SDK-Android).
Verificación específica del modelo: las [notas oficiales Android SDK 4.18](https://developer.dji.com/document/122b5969-d64d-4752-8452-400c41240aa5)
incluyen explícitamente **DJI Mini 2** en `Supported Products`. La
[tabla oficial de compatibilidad](https://repair.dji.com/help/content?customId=01700000763&lang=en&paperDocType=ARTICLE&re=US&spaceId=17)
también confirma Mobile SDK para ese modelo. Soporte documental comprobado el
2026-09-06; la prueba con el teléfono y firmware reales sigue pendiente.
La compatibilidad y sus límites se detallan en [ENTORNO_ANDROID.md](ENTORNO_ANDROID.md).
V5 no se adopta automáticamente como actualización del SDK para este dron.

**Consecuencia:** verificar APIs por modelo, bibliotecas nativas, permisos y Android
reciente antes de prometer funcionamiento. No arrastrar el build antiguo de la
muestra oficial completo sin comprobarlo.

V4.18 anuncia adaptación a Android 14 y el soporte DJI ha comunicado el fin de
actualizaciones iterativas de V4. El ensayo del teléfono es una condición de
viabilidad, especialmente con Android 15+ y páginas de 16 KB; véanse las fuentes
en el informe de entorno.

**Revisión:** si DJI cambia el soporte del modelo o si el ensayo real revela un bloqueo.

**Referencia confirmada por el propietario (2026-09-06):** el
[portal oficial DJI Mobile SDK V4](https://developer.dji.com/mobile-sdk-v4/)
es la referencia principal del desarrollo, junto con la guía y las APIs Android
V4 del contrato de arquitectura. Verificar siempre el soporte específico del Mini 2.

## D-003 · Simulación independiente del SDK · 2026-09-06

**Decisión:** UI y lógica dependen de una interfaz pequeña con implementación
simulada y real. La variante simulada no inicializa ni empaqueta innecesariamente DJI.

**Motivo:** desarrollo y regresiones sin dron conectado; el emulador x86_64 no
valida las bibliotecas ARM, USB ni comportamiento de vuelo del equipo real.

**Revisión:** ajustar la separación de variantes al crear la primera app, conservando
la posibilidad de probar sin hardware.

## D-004 · Relevo en Git y herramientas de terminal locales · 2026-09-06

**Decisión:** `AGENTS.md` común, `CLAUDE.md` importador, estado/tareas breves, Git
local en `main` sin remoto. Scripts activan el SDK/JDK únicamente en la terminal.

**Motivo:** alternancia entre IAs sin duplicar reglas ni cambiar herramientas de otros
proyectos. No se publica en GitHub hasta una petición posterior del propietario.

**Revisión:** al crear el wrapper se fijará la matriz de compilación; al publicar se
añadirán remoto, licencia elegida y CI útil.

## D-005 · MediaPipe en el móvil y control visual · 2026-09-06

**Decisión:** el objetivo central es analizar vídeo del Mini 2 con Google MediaPipe
en el teléfono y traducir observaciones a instrucciones DJI. El propietario ha
confirmado que MediaPipe sustituye a OpenCV; no añadir este último como dependencia
directa. No se requiere ejecutar nuestras rutinas en el dron ni en la nube.

Usar MediaPipe Tasks Vision precompilado desde Kotlin y modelos compatibles
incluidos en el APK. La instalación de nuestra app instala sus bibliotecas/modelos;
no hay una app MediaPipe adicional. Elegir tarea/modelo y fijar versión después
de comprobar la integración. [Guía oficial Android](https://developers.google.com/edge/mediapipe/solutions/setup_android).

**Consecuencias:** un adaptador de frames alimenta visión; la política valida
observaciones antes de enviar órdenes. Solo análisis por defecto. Una sesión de
control activada por el usuario permite órdenes sucesivas acotadas, y se revoca
ante pérdida de datos o parada. Esto sustituye el requisito de una pulsación por
cada orden; no autoriza vuelos mediante herramientas de desarrollo.

**Revisión:** tras la primera prueba de modelo + vídeo DJI en el móvil se fijarán
presupuestos de latencia, límites y CPU/GPU. C++ propio o grafos personalizados
se incorporan únicamente si una necesidad justifica herramientas adicionales.

## D-006 · SDK DJI mediante Maven Central · 2026-09-06

**Decisión:** consumir los artefactos oficiales V4.18 mediante Gradle, con aliases
en `gradle/libs.versions.toml`. No copiar el repo DJI ni añadir un submódulo; usar
sus ejemplos como referencia. Runtime solo en la variante real y `provided` como
`compileOnly`, siguiendo [INTEGRACION_DJI.md](INTEGRACION_DJI.md).

**Evidencia:** la propia muestra oficial declara dependencias Maven. El catálogo
se probó con Gradle: se descargaron los artefactos y transitivos correctamente.
Eso valida distribución/resolución, no compilación Android ni funcionamiento DJI.

**Revisión:** si cambia la disponibilidad de Maven o se justifica adaptar un
fragmento de la muestra; conservar entonces su origen/licencia. La próxima base
Android debe consumir este catálogo y probar el empaquetado de la variante real.

## D-007 · Matriz de la primera app Android · 2026-09-06

**Decisión:** módulo único `app` con dimensión de flavor `drone` (`sim`/`real`),
`applicationId` **io.github.jmoralese.djimini2cv** (la variante simulada añade
`.sim`). Matriz que compila en este PC: Gradle **8.14.3** (wrapper versionado con
checksum), AGP **8.12.0**, Kotlin **2.2.21** con plugin Compose, JDK **17.0.19**,
compileSdk **36**, minSdk **24**, targetSdk **34**, Compose BOM 2025.06.01.

**Motivo y evidencia:** combinación estable y compatible entre sí con el JDK ya
disponible; minSdk 24 lo exige MediaPipe Tasks. targetSdk 34 corresponde a la
adaptación declarada de MSDK V4.18 (Android 14); subirlo sin ensayo real podría
activar comportamientos de Android 15+ no probados con el SDK. `assembleSimDebug`,
`testSimDebugUnitTest`, `lintSimDebug` y `assembleRealDebug` pasan; la variante
simulada se instaló y recorrió en el AVD API 36.

**Detalles DJI comprobados al integrar:** el AAR 4.18 exige `appcompat` y
`constraintlayout` para enlazar sus recursos (la muestra oficial los declara);
el cargador SecNeo en 4.18 es `com.cySdkyc.clx.Helper` (no `com.secneo.sdk`);
las bibliotecas nativas requieren `useLegacyPackaging`. La App Key se inyecta
desde `local.properties` (`dji.appKey`) como placeholder de manifiesto; sin ella
el modo real muestra `MissingAppKey` y no intenta registrarse.

**Revisión:** al validar el teléfono real (T-002/T-004) se revisará targetSdk y
la convivencia con Android 15/16; al integrar MediaPipe, la resolución conjunta
de dependencias. No subir versiones en bloque mezcladas con funcionalidad.
