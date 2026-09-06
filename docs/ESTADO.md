# Estado para el próximo agente

- Fecha: 2026-09-06.
- Último cambio: plan completo de la app en [PLAN_APP.md](PLAN_APP.md) (fichas
  T-013…T-028 ejecutables con «haz la tarea T-0XX»), creado por Claude a
  petición del propietario tras completar T-003. Relevo libre.
- Reparto acordado: el propietario aportará las rutinas de visión por
  computador; las tareas del plan preparan la app completa tipo DJI Fly y las
  costuras de visión/control. QuickShots/ActiveTrack/panorámica no los expone
  el MSDK para Mini 2: el seguimiento será propio (T-026/T-027/T-006).
- Rama: `main`, remoto `origin`: <https://github.com/Jmoralese/djimini2-cv>.
  Publicar cambios solo si el propietario lo pide.
- Fase: primera app Android funcional en simulación; integración DJI compilada
  pero sin probar con hardware.

## Resultado

Existe el módulo `app` (Kotlin, Jetpack Compose, tema oscuro tipo DJI Fly) con
una pantalla única de conexión y vídeo, y dos variantes por flavor `drone`:

- **`sim`**: `SimulatedDroneGateway` + `SimulatedVideoFeed` (patrón animado
  sobre la superficie). Sin bibliotecas DJI, con distintivo SIMULACIÓN visible,
  funciona en el AVD x86_64. `applicationId` con sufijo `.sim`.
- **`real`**: adaptador `DjiDroneGateway` (registro MSDK V4.18, conexión al
  producto, estados propios) y `DjiVideoFeed` (VideoFeeder + DJICodecManager
  sobre TextureView). Permisos/USB del mando en el manifiesto de la variante.
  App Key vía `local.properties` (`dji.appKey`); si falta, estado `MissingAppKey`
  sin intento de registro.

Límites del contrato de arquitectura respetados: UI -> `DroneViewModel` ->
`DroneGateway` (interfaz con tipos propios en `drone/`); nada de clases DJI en
UI/lógica; `VideoFeed` es la costura donde T-005/T-011 añadirán frames para
MediaPipe. La matriz de herramientas y los hallazgos DJI (appcompat/
constraintlayout, `com.cySdkyc.clx.Helper`, `useLegacyPackaging`, ABIs ARM)
están en D-007 y en INTEGRACION_DJI.md. README, AGENTS, contrato de calidad y
TAREAS actualizados con los comandos reales.

## Comprobaciones realizadas

- `.\gradlew.bat :app:assembleSimDebug :app:testSimDebugUnitTest :app:lintSimDebug`
  y `:app:assembleRealDebug`: BUILD SUCCESSFUL (Gradle 8.14.3 wrapper, JDK 17.0.19).
  4 tests JVM de `SimulatedDroneGateway` (secuencia de estados, conexión
  duplicada, cancelación sin estados antiguos, reconexión) en verde; lint sin
  errores. APK sim ≈ 9,8 MB; APK real ≈ 79,3 MB.
- Recorrido manual en AVD `Medium_Phone_API_36.1` (Android 16): instalación con
  `:app:installSimDebug`, arranque, Conectar -> Registrando -> Esperando ->
  «Conectado: Mini 2 (simulado)» con patrón de vídeo animado y Desconectar.
  Captura en `.local/emulador-sim/sim-conectado.png` (fuera de Git).
- AGP instaló `platforms;android-36` y Build-Tools 35 con licencias ya aceptadas.
- Límites: nada de esto prueba DJI real. No hay teléfono, mando ni dron
  conectados; el registro SDK, el vídeo real y la reconexión física siguen sin
  ensayar. El emulador no ejecuta la variante real (ABIs ARM).

## Estado de Git

Trabajo entregado en commits locales de `main` (base de app + documentación);
ver `git log`. Sin publicar en GitHub en esta sesión: el propietario decide el
push. `local.properties` y `.local/` quedan fuera de Git.

## Próximo paso

1. T-002: confirmar teléfono/Android, mando y firmware; T-004: crear la App Key
   DJI para `io.github.jmoralese.djimini2cv`, ponerla en `local.properties` e
   instalar la variante real en el teléfono para registro/conexión/vídeo en
   banco (sin motores). El vídeo (T-005) se valida en ese mismo banco.
2. En paralelo: T-009 (elegir rutina/tarea/modelo MediaPipe) y T-010 (inferencia
   sobre assets en la variante `sim`).
3. Pendientes menores: decidir si se sube targetSdk tras el ensayo real y elegir
   licencia del repositorio.
