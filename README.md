# dji2

App personal para **Android nativo** que ejecuta **visión por computador con
Google MediaPipe en el móvil** y envía órdenes concretas al **DJI Mini 2 original**.
El desarrollo se alterna entre Codex y Claude, con decisiones y traspasos en Git.

```text
Vídeo del DJI -> móvil: decodificación -> MediaPipe -> decisión de control
                                                      -> SDK DJI -> mando -> dron
```

MediaPipe y los modelos elegidos se incluyen en el APK: se instalan con nuestra
app, sin aplicación MediaPipe separada. El procesamiento visual propio no requiere
ejecutarse en el dron ni en un servidor. La primera integración será de análisis
sin movimientos; después se validará una rutina de control activada por el usuario.

**Estado:** existe la primera app (módulo `app`, Kotlin + Compose) con dos
variantes: `sim` (dron simulado, sin bibliotecas DJI) y `real` (MSDK V4.18).
La pantalla inicial muestra conexión y vídeo; la variante simulada se verificó
en emulador. La integración DJI compila pero **no** está probada con mando ni
dron, y requiere una App Key local. El alcance y las condiciones para avanzar
están en el [contrato de producto](docs/CONTRATO_PRODUCTO.md).

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

Para la variante real hace falta una App Key de DJI Developer asociada al
`applicationId` `io.github.jmoralese.djimini2cv`, guardada fuera de Git en
`local.properties` como `dji.appKey=...`. Sin ella la app compila y muestra
un error claro al conectar. `local.properties` también fija `sdk.dir`.

El [informe de entorno](docs/ENTORNO_ANDROID.md) recoge lo encontrado, las pruebas
realizadas y lo que falta para probar con el teléfono. No hace falta instalar
Gradle ni Kotlin globalmente: el futuro proyecto fijará sus versiones.

## Documentación que se mantiene

| Documento | Para qué sirve |
| --- | --- |
| [AGENTS.md](AGENTS.md) / [CLAUDE.md](CLAUDE.md) | Instrucciones comunes y entrada de cada IA |
| [Estado](docs/ESTADO.md) | Punto de relevo: qué funciona, qué falta y próximo paso |
| [Tareas](docs/TAREAS.md) | Secuencia corta de trabajo con criterios de aceptación |
| [Producto](docs/CONTRATO_PRODUCTO.md) | Alcance personal, prioridades y resultados esperados |
| [Arquitectura](docs/CONTRATO_ARQUITECTURA.md) | Límites del código y decisiones de implementación |
| [Calidad](docs/CONTRATO_CALIDAD.md) | Qué significa terminado y cómo comprobarlo |
| [Dron](docs/CONTRATO_DRON.md) | Reglas de conexión, comandos y pruebas con hardware |
| [Visión](docs/CONTRATO_VISION.md) | Frames DJI, MediaPipe, modelos y paso de observaciones a órdenes |
| [Integración DJI](docs/INTEGRACION_DJI.md) | Dependencias Maven, catálogo y qué incorporar al proyecto |
| [Colaboración](docs/COLABORACION.md) | Alternancia entre IAs y trabajo simultáneo |
| [Decisiones](docs/DECISIONES.md) | Decisiones importantes, motivo y condiciones de revisión |

## Guía de referencia DJI

La referencia principal es el [portal oficial DJI Mobile SDK V4](https://developer.dji.com/mobile-sdk-v4/),
indicado por el propietario para su Mini 2. El
[contrato de arquitectura](docs/CONTRATO_ARQUITECTURA.md) enlaza también la guía
de desarrollo y los ejemplos Android V4 que consultarán Codex y Claude.

El SDK se obtiene mediante Gradle desde Maven Central. No hay que copiar el
repositorio completo de DJI. [libs.versions.toml](gradle/libs.versions.toml) fija
las dependencias V4.18, que consume el módulo `app` solo en su variante `real`.
[Detalle de integración](docs/INTEGRACION_DJI.md).

## Git

Repositorio con rama `main` y remoto `origin` en GitHub:
<https://github.com/Jmoralese/djimini2-cv>, publicado a petición del propietario.
Los cambios se agrupan en commits pequeños; las ramas se usan para tareas de código.
Las claves, compilaciones y datos personales quedan excluidos mediante `.gitignore`.
La licencia de distribución del código propio sigue pendiente de decidir.

La base prevista es Kotlin, Google MediaPipe Tasks Vision y DJI Mobile SDK V4.
MediaPipe sustituye a OpenCV; no se incorpora como segunda biblioteca de visión.
La muestra oficial de DJI usa
V4.18; su compatibilidad con el teléfono concreto debe probarse antes de añadir
control de vuelo. [Fuente oficial DJI](https://github.com/dji-sdk/Mobile-SDK-Android).

El PC tiene las herramientas Android básicas. La auditoría específica de MediaPipe
y lo que falta para ejecutarlo figuran en [ENTORNO_ANDROID.md](docs/ENTORNO_ANDROID.md).
La matriz que realmente compila está registrada en la decisión D-007 de
[DECISIONES.md](docs/DECISIONES.md). MediaPipe (dependencia y modelo) sigue
pendiente de integrar en la app.
