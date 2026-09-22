# dji2

App personal para **Android nativo** que ejecuta **visión por computador con
Google MediaPipe en el móvil** y envía órdenes concretas al **DJI Mini 2 original**
a través de su mando. El desarrollo se alterna entre Codex y Claude, con
decisiones y traspasos registrados en Git.

```text
Vídeo del DJI -> móvil: decodificación -> MediaPipe -> decisión de control
                                                      -> SDK DJI -> mando -> dron
```

MediaPipe y los modelos elegidos se incluyen en el APK: se instalan con nuestra
app, sin aplicación MediaPipe separada. El procesamiento visual propio no requiere
ejecutarse en el dron ni en un servidor. MediaPipe nunca llama al dron: sus
observaciones pasan antes por una política de control que decide qué se convierte
en orden DJI.

## Para qué sirve esta app

Lo que se busca va más allá de pilotar: son funciones que DJI Fly no ofrece para
el Mini 2. Todas están planificadas, ninguna entregada todavía.

| Objetivo | Viabilidad conocida |
| --- | --- |
| Seguimiento visual de un sujeto | Propio, con MediaPipe + Virtual Stick: el MSDK no expone ActiveTrack para Mini 2 |
| Misiones por puntos GPS | Guiadas desde el móvil (el Mini 2 no tiene waypoints a bordo); exige enlace continuo mando-dron |
| Asistencia frente a obstáculos | Experimental: con una sola cámara se puede avisar o frenar, **nunca** garantizar esquiva |
| Búsqueda de un objeto concreto | Detección por categoría + re-identificación sobre fotos de referencia del usuario |

El detalle, las fases y las fichas de tarea están en el
[plan de la app](docs/PLAN_APP.md); el alcance y sus límites, en el
[contrato de producto](docs/CONTRATO_PRODUCTO.md).

## Estado actual

Existe la primera app (módulo `app`, Kotlin + Compose) con una pantalla de
conexión y vídeo, en dos variantes del flavor `drone`:

- **`sim`**: dron simulado (`SimulatedDroneGateway` + `SimulatedVideoFeed`), sin
  bibliotecas DJI y con distintivo SIMULACIÓN visible. **Verificada en emulador**:
  compila, pasa sus tests JVM y lint, e instala y se recorre a mano.
- **`real`**: adaptador DJI con MSDK V4.18 (registro, conexión al producto,
  VideoFeeder + DJICodecManager). **Solo compila**: no se ha probado con mando ni
  dron, y necesita una App Key local.

Ni el registro del SDK, ni el vídeo real, ni la reconexión física se han
ensayado. MediaPipe todavía no está integrado en la app (dependencia y modelo
pendientes). El punto de relevo vivo, con comprobaciones reales y próximo paso,
es [docs/ESTADO.md](docs/ESTADO.md).

## Requisitos

- **JDK 17** y **Android SDK** (plataforma 36; el wrapper de Gradle y las
  versiones de Kotlin/AGP los fija el propio proyecto, no hace falta instalarlos).
- `local.properties`, fuera de Git, con `sdk.dir` y —solo para la variante
  real— `dji.appKey`.
- Windows con PowerShell para los scripts de `scripts/`; en otros sistemas,
  `./gradlew` funciona igual.

El [inventario del PC](../../operacion/ENTORNO_ANDROID.md) dice qué hay instalado;
[docs/ENTORNO_ANDROID.md](docs/ENTORNO_ANDROID.md) recoge la compatibilidad DJI y
MediaPipe y qué falta para ensayar con el teléfono.

## Empezar

Desde PowerShell, en la raíz del repositorio:

```powershell
# Activa Java y Android SDK solo en esta terminal.
. .\scripts\Use-AndroidEnv.ps1

# Comprueba las herramientas instaladas; no mueve el dron.
powershell -NoProfile -File .\scripts\Test-AndroidEnvironment.ps1

# Variante simulada: compilar, tests unitarios y lint.
.\gradlew.bat :app:assembleSimDebug :app:testSimDebugUnitTest :app:lintSimDebug

# Instalarla en un emulador o teléfono conectado por ADB.
.\gradlew.bat :app:installSimDebug

# Variante real (DJI): verificación de compilación y empaquetado.
.\gradlew.bat :app:assembleRealDebug
```

La variante `sim` es la vía normal de desarrollo: no necesita hardware ni App Key
y corre en un emulador x86_64. La variante `real` solo trae bibliotecas nativas
ARM, así que **no arranca en ese emulador**: requiere teléfono.

### App Key de DJI

Para la variante real hace falta una App Key de DJI Developer asociada al
`applicationId` `io.github.jmoralese.djimini2cv`, guardada fuera de Git en
`local.properties` como `dji.appKey=...` (también se acepta la variable de
entorno `DJI_APP_KEY`). Sin ella la app compila y muestra un error claro al
conectar, sin intentar el registro.

## Estructura del repositorio

```text
app/src/main/  código común: UI Compose, DroneViewModel y el contrato drone/
app/src/sim/   implementación simulada (DroneRuntime del flavor sim)
app/src/real/  adaptador DJI: DjiDroneGateway, DjiVideoFeed, permisos USB
app/src/test/  tests JVM (por ahora, los del gateway simulado)
docs/          contratos, estado, tareas y plan; es la memoria del proyecto
scripts/       preparación y auditoría del entorno Android en PowerShell
referencias/   material de diseño e inspiración; no se compila ni se empaqueta
gradle/        wrapper y libs.versions.toml (catálogo único de versiones)
```

El límite que sostiene el diseño: UI -> `DroneViewModel` -> `DroneGateway`, una
interfaz con tipos propios en `drone/`. Ninguna clase DJI aparece en la UI ni en
la lógica, y toda capacidad nueva del gateway se implementa a la vez en el
adaptador DJI y en el simulador. `VideoFeed` es la costura por donde entrarán los
frames a MediaPipe. Lo fija el [contrato de arquitectura](docs/CONTRATO_ARQUITECTURA.md).

## Documentación que se mantiene

| Documento | Para qué sirve |
| --- | --- |
| [AGENTS.md](AGENTS.md) / [CLAUDE.md](CLAUDE.md) | Instrucciones de este proyecto y entrada de cada IA. Las comunes a todas las apps móviles están en `../../AGENTS.md` |
| [Estado](docs/ESTADO.md) | Punto de relevo: qué funciona, qué falta y próximo paso |
| [Tareas](docs/TAREAS.md) | Estado de cada tarea con su resultado y aceptación |
| [Plan de la app](docs/PLAN_APP.md) | Hoja de ruta por fases con fichas T-013…T-033 ejecutables por ID |
| [Producto](docs/CONTRATO_PRODUCTO.md) | Alcance personal, prioridades y resultados esperados |
| [Arquitectura](docs/CONTRATO_ARQUITECTURA.md) | Límites del código y decisiones de implementación |
| [Calidad](docs/CONTRATO_CALIDAD.md) | Qué significa terminado y cómo comprobarlo |
| [Dron](docs/CONTRATO_DRON.md) | Reglas de conexión, comandos y pruebas con hardware |
| [Visión](docs/CONTRATO_VISION.md) | Frames DJI, MediaPipe, modelos y paso de observaciones a órdenes |
| [Integración DJI](docs/INTEGRACION_DJI.md) | Dependencias Maven, catálogo y qué incorporar al proyecto |
| [Entorno y compatibilidad](docs/ENTORNO_ANDROID.md) | Compatibilidad del Mini 2 con el MSDK y preparación de MediaPipe; el inventario del PC está en `../../operacion/ENTORNO_ANDROID.md` |
| [Colaboración](../../operacion/COLABORACION.md) | Alternancia entre IAs y trabajo simultáneo, común a todos los proyectos |
| [Decisiones](docs/DECISIONES.md) | Decisiones importantes, motivo y condiciones de revisión |
| [Referencias](referencias/README.md) | Capturas e inspiración de diseño; material ajeno, solo consulta |

Cualquier tarea del plan se pide por su identificador: «haz la tarea T-0XX».

## Guía de referencia DJI

La referencia principal es el [portal oficial DJI Mobile SDK V4](https://developer.dji.com/mobile-sdk-v4/),
indicado por el propietario para su Mini 2. El
[contrato de arquitectura](docs/CONTRATO_ARQUITECTURA.md) enlaza también la guía
de desarrollo y los ejemplos Android V4 que consultarán Codex y Claude. **No se
usan APIs de MSDK V5**: no soporta el Mini 2 original.

El SDK se obtiene mediante Gradle desde Maven Central; no hay que copiar el
repositorio completo de DJI. [libs.versions.toml](gradle/libs.versions.toml) fija
las dependencias V4.18, que consume el módulo `app` solo en su variante `real`
([detalle de integración](docs/INTEGRACION_DJI.md)). La compatibilidad de esa
versión con el teléfono concreto debe probarse antes de añadir control de vuelo.
[Fuente oficial DJI](https://github.com/dji-sdk/Mobile-SDK-Android).

MediaPipe sustituye a OpenCV por decisión explícita del propietario: se usará
MediaPipe Tasks Vision precompilado desde Kotlin, sin una segunda biblioteca de
visión. La matriz de versiones que realmente compila está registrada como
decisión D-007 en [DECISIONES.md](docs/DECISIONES.md).

## Git

Repositorio con rama `main` y remoto `origin` en GitHub:
<https://github.com/Jmoralese/djimini2-cv>, publicado a petición del propietario.
Los cambios se agrupan en commits pequeños; las ramas se usan para tareas de
código. Las claves, compilaciones y datos personales quedan excluidos mediante
`.gitignore`; las evidencias locales (capturas, registros) van a `.local/`, fuera
de Git. La licencia de distribución del código propio sigue pendiente de decidir.
