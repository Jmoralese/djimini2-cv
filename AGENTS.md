# Instrucciones del proyecto: Codex y Claude

Proyecto personal Android nativo de **visión por computador en el móvil con
Google MediaPipe**, para controlar un **DJI Mini 2 original** a través de su mando.

Las reglas comunes a todas las apps móviles (verificación, idioma, git, relevo
entre IAs, secretos, modelos, límites duros) están en `../../AGENTS.md`. Codex:
léelo primero; Claude Code lo carga solo. Aquí queda únicamente lo que es de
este proyecto.

Si `../../AGENTS.md` no existe (sesión en la nube con solo este repo), las reglas comunes son el `AGENTS.md` del repo github.com/Jmoralese/appmoviles: añade ese repo a la sesión y léelo antes de trabajar.

Este proyecto no usa el plugin de la fábrica (`mobile-factory`): Codex y Claude Code trabajan directamente con estas reglas.

## Al empezar

1. Lee [README.md](README.md), [estado](docs/ESTADO.md) y [tareas](docs/TAREAS.md).
2. Lee los contratos afectados antes de editar: [producto](docs/CONTRATO_PRODUCTO.md),
   [arquitectura](docs/CONTRATO_ARQUITECTURA.md), [calidad](docs/CONTRATO_CALIDAD.md),
   [visión en el móvil](docs/CONTRATO_VISION.md) y, para cualquier integración DJI,
   [operación del dron](docs/CONTRATO_DRON.md).
3. Comprueba la [decisión técnica vigente](docs/DECISIONES.md). No reconstruyas
   decisiones de memoria.
4. Elige una tarea concreta del [plan](docs/PLAN_APP.md) por su identificador
   («haz la tarea T-0XX») y sus criterios de aceptación. Para cambios pequeños
   basta explicarlos en el turno; no crees documentos de planificación por rutina.

## Reglas de este proyecto

- Android nativo con Kotlin y Compose. No introduzcas iOS, Flutter, React Native,
  backend, cuentas de usuario ni infraestructura empresarial sin necesidad explícita.
- **DJI MSDK V4 (4.18) es la línea para Mini 2.** Verifica las APIs en el
  [portal oficial Mobile SDK V4](https://developer.dji.com/mobile-sdk-v4/) y las
  guías V4 del contrato de arquitectura. No copies APIs V5 (no soporta el Mini 2)
  ni asumas capacidades de otros drones; confirma el soporte específico del Mini 2
  antes de usar una capacidad anunciada para la familia.
- Mantén DJI detrás de un adaptador (`DroneGateway`) y permite probar con un dron
  simulado. Toda capacidad nueva del gateway se implementa a la vez en el adaptador
  DJI y en el simulador.
- Obtén DJI de Maven Central con `gradle/libs.versions.toml`; no copies el repo de
  ejemplos ni lo añadas como submódulo. Sigue [INTEGRACION_DJI.md](docs/INTEGRACION_DJI.md):
  runtime y `compileOnly` separados, solo en la variante `real`.
- **MediaPipe sustituye a OpenCV** por decisión del propietario. MediaPipe Tasks
  Vision precompilado desde Kotlin; no añadas OpenCV ni un runtime separado. La
  biblioteca y el modelo van en el APK; el análisis corre en el teléfono.
- MediaPipe no llama al dron: sus observaciones pasan por una política de control
  antes de convertirse en órdenes DJI. La rutina visual empieza desactivada; una
  sesión acotada puede generar órdenes sin confirmar cada frame, y una reconexión
  no la reactiva. Contratos de visión y del dron.
- **Nunca ordenes despegue, armado, movimiento, aterrizaje o RTH a hardware como
  parte de una comprobación automática.**
- Ni coordenadas reales ni vuelos privados en Git ni en traspasos. Evidencias
  locales en `.local/`.
- Distingue siempre compilación, emulador, banco con hardware y vuelo real. La
  variante `real` solo trae bibliotecas ARM y no arranca en el emulador x86_64.

## Al terminar o cambiar de IA

1. Ejecuta las comprobaciones que correspondan según el
   [contrato de calidad](docs/CONTRATO_CALIDAD.md).
2. Actualiza `docs/ESTADO.md` (cambios, comprobaciones reales, pendientes,
   siguiente paso) y `docs/TAREAS.md` si cambió una tarea.
3. Registra en `docs/DECISIONES.md` solo decisiones que condicionen trabajo futuro.
4. Commit local pequeño y revisado. La subida a GitHub sigue la regla común
   (`../../AGENTS.md`, apartado Git): se sube al cerrar sesión con la copia de
   seguridad. El repositorio `djimini2-cv` es **público a propósito** (decisión
   del propietario, 2026-11-14): nunca subas claves de DJI, coordenadas reales
   de vuelo ni nada que no deba verse.

## Comandos

```powershell
. .\scripts\Use-AndroidEnv.ps1
powershell -NoProfile -File .\scripts\Test-AndroidEnvironment.ps1
.\gradlew.bat :app:assembleSimDebug :app:testSimDebugUnitTest :app:lintSimDebug
.\gradlew.bat :app:assembleRealDebug
.\gradlew.bat :app:installSimDebug
git diff --check
```

Este proyecto compila con **JDK 17**. `local.properties` (fuera de Git) aporta
`sdk.dir` y, para la variante real, `dji.appKey` (o la variable `DJI_APP_KEY`).
