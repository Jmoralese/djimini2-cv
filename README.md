# dji2

App personal para **Android nativo** orientada al **DJI Mini 2 original**.
El desarrollo se alterna entre Codex y Claude, con decisiones y traspasos en Git.

**Estado:** preparación del repositorio y auditoría del PC. Todavía no hay app,
APK, Gradle Wrapper ni conexión al dron verificada. El alcance inicial y las
condiciones para avanzar están en el [contrato de producto](docs/CONTRATO_PRODUCTO.md).

## Empezar

Desde PowerShell, en la raíz del repositorio:

```powershell
# Activa Java y Android SDK solo en esta terminal.
. .\scripts\Use-AndroidEnv.ps1

# Comprueba las herramientas instaladas; no mueve el dron.
powershell -NoProfile -File .\scripts\Test-AndroidEnvironment.ps1
```

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
| [Colaboración](docs/COLABORACION.md) | Alternancia entre IAs y trabajo simultáneo |
| [Decisiones](docs/DECISIONES.md) | Decisiones importantes, motivo y condiciones de revisión |

## Git

Repositorio local con rama `main`. Sin remoto ni publicación en GitHub.
Los cambios se agrupan en commits pequeños; las ramas se usan para tareas de código.
Las claves, compilaciones y datos personales quedan excluidos mediante `.gitignore`.
La licencia de distribución del código propio se decidirá antes de publicarlo.

La base prevista es Kotlin y DJI Mobile SDK V4. La muestra oficial de DJI usa
V4.18; su compatibilidad con el teléfono concreto debe probarse antes de añadir
control de vuelo. [Fuente oficial DJI](https://github.com/dji-sdk/Mobile-SDK-Android).
