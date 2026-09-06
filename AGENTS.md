# Instrucciones compartidas: Codex y Claude

Proyecto personal Android nativo de **visión por computador en el móvil con
Google MediaPipe**, para controlar un **DJI Mini 2 original** a través de su mando.
Comunícate y documenta en español; usa inglés para identificadores de código.
Las instrucciones explícitas del usuario prevalecen sobre estas normas del proyecto.

## Al empezar

1. Lee [README.md](README.md), [estado](docs/ESTADO.md) y [tareas](docs/TAREAS.md).
2. Ejecuta `git status --short --branch` y `git log -5 --oneline` (si hay commits).
3. Lee los contratos afectados antes de editar: [producto](docs/CONTRATO_PRODUCTO.md),
   [arquitectura](docs/CONTRATO_ARQUITECTURA.md), [calidad](docs/CONTRATO_CALIDAD.md),
   [visión en el móvil](docs/CONTRATO_VISION.md) y, para cualquier integración DJI,
   [operación del dron](docs/CONTRATO_DRON.md).
4. Comprueba la [decisión técnica vigente](docs/DECISIONES.md) y el
   [protocolo de colaboración](docs/COLABORACION.md). No reconstruyas decisiones de memoria.
5. Elige una tarea concreta y sus criterios de aceptación. Para cambios pequeños basta
   explicarlos en el turno; no crees documentos de planificación por rutina.

## Durante el trabajo

- Un solo escritor por checkout. Para trabajo simultáneo, usa ramas y worktrees
  distintos con alcance separado; una rama por sí sola no aísla el directorio.
- Conserva los cambios ajenos. No uses `reset --hard`, `clean -fd`, force push,
  restauraciones masivas ni stashes ajenos para limpiar el entorno.
- Haz lo autorizado sin pedir aprobación para cada archivo o decisión reversible.
  Pregunta solo por información imprescindible o acciones fuera del alcance concedido.
- Android nativo con Kotlin. No introduzcas iOS, Flutter, React Native, backend,
  cuentas de usuario ni infraestructura empresarial sin necesidad explícita.
- DJI MSDK V4 es la línea para Mini 2. Verifica las APIs en fuentes oficiales:
  no copies APIs V5 ni asumas capacidades de otros drones.
- Para desarrollar integración DJI, usa como referencia principal el [portal
  oficial Mobile SDK V4](https://developer.dji.com/mobile-sdk-v4/) indicado por el
  propietario y las guías/API V4 del contrato de arquitectura. Confirma el soporte
  específico del Mini 2 antes de usar una capacidad anunciada para la familia SDK.
- Mantén DJI detrás de un adaptador y permite probar con un dron simulado.
- Obtén DJI de Maven Central usando `gradle/libs.versions.toml`; no copies todo
  el repo de ejemplos ni lo añadas como submódulo. Sigue
  [INTEGRACION_DJI.md](docs/INTEGRACION_DJI.md): runtime y `compileOnly` separados,
  solo en la variante real. El catálogo preparado no equivale a una app integrada.
- MediaPipe sustituye a OpenCV por decisión explícita del propietario. Usa
  MediaPipe Tasks Vision precompilado desde Kotlin; no añadas OpenCV como
  dependencia directa ni instales un runtime separado en el móvil.
- Empaqueta la biblioteca y el modelo compatible con la app. El análisis se
  ejecuta en el teléfono; las observaciones pasan por una política de control
  antes de convertirse en órdenes DJI. MediaPipe no llama directamente al dron.
- La rutina visual comienza desactivada. El usuario puede activar una sesión
  acotada que genere órdenes automáticamente; no necesita confirmar cada frame.
  Una reconexión no reactiva esa sesión. Sigue el contrato de visión y del dron.
- Nunca ordenes despegue, armado, movimiento, aterrizaje o RTH a hardware como
  parte de una comprobación automática de desarrollo.
- No guardes claves, credenciales de firma, coordenadas reales ni vuelos privados
  en Git o en documentos de traspaso. Usa `.local/` para evidencias locales.
- No añadas dependencias, capas ni tests triviales por cumplir una cuota.
- No declares una prueba pasada si no se ejecutó. Distingue compilación, emulador,
  banco con hardware y vuelo real.

## Al terminar o cambiar de IA

1. Revisa el diff y ejecuta las comprobaciones que correspondan al cambio.
2. Actualiza `docs/ESTADO.md` con cambios, comprobaciones reales, pendientes y
   siguiente paso; actualiza `docs/TAREAS.md` si cambió una tarea.
3. Registra en `docs/DECISIONES.md` solo decisiones que condicionen trabajo futuro.
4. Deja un commit local pequeño y revisado cuando la tarea lo permita; no incluyas
   cambios ajenos. Si queda trabajo sin commit, enumera archivos y motivo en el estado.
5. Informa del resultado y sus límites. No publiques en GitHub hasta que el usuario
   lo solicite. No necesitas una segunda IA para aprobar un cambio rutinario.

## Comandos actuales

```powershell
. .\scripts\Use-AndroidEnv.ps1
powershell -NoProfile -File .\scripts\Test-AndroidEnvironment.ps1
git diff --check
```

Todavía no hay app ni Gradle Wrapper: no inventes comandos de compilación disponibles.
Cuando se añadan, actualiza README y el contrato de calidad en el mismo cambio.
