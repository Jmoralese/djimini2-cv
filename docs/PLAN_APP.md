# Plan de la app completa

Hoja de ruta acordada con el propietario (2026-09-06) para llevar `app` desde la
base actual (conexión + vídeo) hasta una app de pilotaje completa tipo DJI Fly
con funcionalidades propias de visión por computador. **El propietario aportará
las rutinas de visión**; el resto de tareas preparan la app y las costuras para
que esas rutinas se enchufen sin tocar UI ni SDK.

## Cómo pedir una tarea

Un prompt del tipo **«haz la tarea T-0XX»** es suficiente. El agente debe:

1. Seguir el ritual de inicio de [AGENTS.md](../AGENTS.md) (estado, contratos,
   decisiones) y leer aquí la ficha de la tarea y sus dependencias.
2. Verificar en el [portal V4](https://developer.dji.com/mobile-sdk-v4/) las APIs
   que use y su soporte en Mini 2 antes de implementarlas.
3. Implementar también el comportamiento simulado (la variante `sim` debe
   permitir recorrer la pantalla/función sin dron).
4. Ejecutar la puerta de calidad (`assembleSimDebug`, `testSimDebugUnitTest`,
   `lintSimDebug`, `assembleRealDebug`) y las pruebas propias de la ficha.
5. Actualizar ESTADO/TAREAS (y DECISIONES si aplica) y dejar commits pequeños.

Si una ficha requiere hardware («banco» o «vuelo»), el agente implementa y
verifica todo lo posible sin dron y entrega un guion de ensayo para que el
propietario lo ejecute; **nunca** ordena motores/vuelo desde herramientas de
desarrollo ([contrato del dron](CONTRATO_DRON.md)).

## Correspondencia con DJI Fly (Mini 2)

| Función DJI Fly | Soporte técnico | En este plan |
| --- | --- | --- |
| Vista FPV con HUD (batería, GPS, señal, altura, distancia, velocidad) | SDK V4: FlightController/Battery/AirLink | T-014 |
| Despegue / aterrizaje asistidos | SDK V4: `startTakeoff`/`startLanding` | T-015 |
| RTH (volver a casa) + altura RTH + avisos Smart RTH | SDK V4: `startGoHome`, `goHomeHeightInMeters` | T-016 |
| Foto: única, intervalo, AEB | SDK V4: `ShootPhotoMode` | T-017 |
| Vídeo 4K/2.7K/1080 + grabación | SDK V4: `startShootPhoto`/`startRecordVideo` | T-017 (grabar) y T-018 (resolución) |
| Modo M: ISO, obturador, EV, balance de blancos; JPEG/RAW | SDK V4: `ExposureSettings` | T-018 |
| Zoom digital 4x | Verificar API/soporte en banco | T-018 (opcional) |
| Control de gimbal (pitch) | SDK V4: `Gimbal.rotate` | T-019 |
| Álbum: previsualizar y descargar del dron | SDK V4 MediaManager; confirmar Mini 2 en banco | T-020 |
| Mapa con posición, home, trayecto, «encontrar mi dron» | Mapa propio + telemetría | T-021 |
| Zonas GEO / fly zones | SDK V4 FlyZoneManager + fuente oficial ENAIRE (España) | T-022, T-023 |
| Ajustes: seguridad (máx. altura/distancia), unidades, acerca de | SDK V4 FlightController + DataStore | T-024 |
| Avisos/diagnóstico (compass, IMU, viento, batería baja) | SDK V4 DJIDiagnostics | T-025 |
| **QuickShots** (Dronie, Helix, Rocket, Circle, Boomerang) | **No expuesto por MSDK**: DJI Fly los implementa app-side | T-028 (propios, con Virtual Stick) |
| **Panorámicas** (Sphere/180°/Wide) | **No soportado por MSDK V4** (confirmado por DJI) | Fuera del plan; revisar si DJI lo cambia |
| **ActiveTrack / seguimiento** | **No existe en Mini 2**: DJI lo reserva a otros modelos | T-009→T-011, T-026, T-027, T-006: nuestro seguimiento por visión |
| Editor de vídeo / redes sociales | App DJI, fuera de alcance | No se hace |
| Actualización de firmware | Fuera de alcance (se usa DJI Fly para ello) | No se hace |

Evidencia: notas de prensa del soporte MSDK para Mini 2 y respuesta DJI sobre
panorama ([dronedj](https://dronedj.com/2022/01/03/dji-sdk-mini-2-se-air-2s/),
[foro SDK](https://sdk-forum.dji.net/hc/en-us/articles/25516653720601)); Virtual
Stick en Mini 2 confirmado en el [contrato del dron](CONTRATO_DRON.md). Las
capacidades se re-verifican por modelo al implementar cada tarea.

## Más allá de DJI Fly: el motivo de esta app

Objetivos del propietario que DJI Fly no ofrece en Mini 2 y que esta app añade
con visión en el móvil + Virtual Stick:

| Función objetivo | Viabilidad en Mini 2 | Tareas |
| --- | --- | --- |
| Seguimiento de un objetivo (persona, bici…) | Viable: visión propia + Virtual Stick | T-026, T-027, T-006 |
| Planificar un vuelo por puntos GPS y ejecutarlo | Viable como **misión guiada desde el móvil**: el SDK no ofrece waypoints a bordo para Mini 2, pero sí telemetría GPS + Virtual Stick. Precedente: Litchi lo hace así en Mini 2. Límites heredados: exige enlace continuo mando-dron (la misión no continúa fuera de cobertura) y el yaw puede ser poco suave (límite conocido del SDK) | T-029, T-030 |
| Aviso/reacción ante obstáculos (el Mini 2 no trae sensores) | **Parcial y experimental**: con una sola cámara se puede detectar y frenar/avisar, no garantizar esquiva. Se presenta siempre como asistencia, nunca como navegación segura (contrato de producto) | T-031 |
| «Busca mi perro / busca mi coche» (objeto concreto) | Viable: detector por categoría + re-identificación con embeddings (MediaPipe Image Embedder) sobre fotos de referencia del usuario; búsqueda con patrón de barrido sobre la misión guiada | T-032, T-033 |

Fuentes del precedente de waypoints: [soporte de waypoints por modelo en
drones DJI Fly](https://www.litchiutilities.com/docs/waypoint.php) (Mini 2 va
por Virtual Stick), [hilo DJI Forum sobre Litchi+Mini 2](https://forum.dji.com/thread-258531-1-1.html).

## Mapa de pantallas

```text
Inicio (estado, conectar, accesos)
 ├── Vuelo (FPV): vídeo + HUD + despegue/aterrizar/RTH + foto/vídeo + gimbal
 │     └── Overlay visión: detecciones, objetivo, sesión de seguimiento
 ├── Mapa: dron/home/trayecto + zonas DJI GEO + capa ENAIRE + encontrar mi dron
 ├── Galería: medios de la SD (previsualizar, descargar)
 └── Ajustes: seguridad, cámara, unidades, visión, acerca de
```

Arquitectura: se mantienen los límites del
[contrato de arquitectura](CONTRATO_ARQUITECTURA.md). `DroneGateway` crece por
capacidades pequeñas (telemetría, órdenes, cámara, medios, zonas); MediaPipe
queda tras `VisionProcessor`/`FrameSource`; la política de control valida antes
de cada envío; un único emisor Virtual Stick. Cada ampliación del gateway se
implementa a la vez en el adaptador DJI y en el simulador.

## Fases y dependencias

```text
FASE 0 hardware:   T-002 -> T-004 -> T-005
FASE 1 esqueleto:  T-013 -> T-014            (sin dron; sim completa)
FASE 2 vuelo:      T-015 -> T-016            (requiere FASE 0 para validar)
FASE 3 cámara:     T-017 -> T-018, T-019, T-020
FASE 4 mapa:       T-021 -> T-022, T-023     (T-021 no requiere dron)
FASE 5 ajustes:    T-024, T-025
FASE 6 visión:     T-009 -> T-010 -> (T-005) -> T-011 -> T-026 -> T-027 -> T-006 -> T-028
FASE 7 misiones:   T-029 -> T-030;  T-031 (tras T-011+T-027);  T-032 -> T-033 (tras T-030+T-032)
```

Las fases 1, 4 (parcial), 5 (parcial) y 6 (hasta T-010) avanzan sin hardware.
El orden dentro de cada fase es la dependencia real; entre fases se puede
intercalar. Las fichas de T-002/004/005/006/009/010/011 están en
[TAREAS.md](TAREAS.md) y los contratos; aquí se detallan las nuevas.

---

## Fichas de tareas

### T-013 · Navegación y esqueleto multi-pantalla

- **Depende de:** nada (base actual). **Hardware:** no.
- **Alcance:** Navigation Compose con las pantallas Inicio, Vuelo (la actual de
  conexión+vídeo evoluciona aquí), Mapa (placeholder), Galería (placeholder) y
  Ajustes (placeholder). Barra/accesos de navegación, `DroneViewModel` por
  pantalla compartiendo el gateway, y orientación: Vuelo en landscape estable.
- **Aceptación:** todas las pantallas accesibles y con estado propio en la
  variante sim; rotación y background/foreground sin perder conexión simulada;
  ninguna pantalla llama a clases DJI.
- **Verificación:** puerta de calidad + recorrido en emulador con capturas en
  `.local/`. Tests JVM de navegación no exigidos; sí de cualquier lógica nueva.

### T-014 · Telemetría y HUD de vuelo

- **Depende de:** T-013. **Hardware:** banco para validar valores reales (la
  implementación y la sim no lo requieren).
- **Alcance:** ampliar `DroneGateway` con `telemetry: StateFlow<DroneTelemetry>`
  (tipos propios: batería %, satélites/señal GPS, señal mando, altura, distancia
  al home, velocidades H/V, rumbo, modo de vuelo, grabando sí/no, tiempo de
  grabación; cada dato con unidad y timestamp monotónico). Adaptador DJI con
  listeners de `FlightControllerState`, `Battery`, AirLink; simulador que genera
  telemetría coherente (vuelo ficticio). HUD sobre el vídeo en la pantalla Vuelo
  al estilo DJI Fly: valores desconocidos se muestran como «--», nunca como 0;
  datos caducados se marcan (contrato de producto).
- **Aceptación:** HUD completo en sim; caducidad visible al «congelar» el
  simulador; callbacks retirados simétricamente al desconectar; sin trabajo en
  el hilo UI.
- **Verificación:** puerta de calidad; tests JVM de mapeo/caducidad de
  telemetría; captura del HUD en emulador. Guion de banco para contrastar
  valores reales cuando haya equipo.

### T-015 · Órdenes de vuelo: despegue y aterrizaje

- **Depende de:** T-014 (y F0 para validar en real). **Hardware:** banco sin
  hélices + ensayo de vuelo del propietario.
- **Alcance:** framework de órdenes discretas en el gateway conforme al
  [contrato del dron](CONTRATO_DRON.md): validación previa (conexión, estado,
  frescura de datos), estados pendiente/completada/rechazada/desconocida,
  bloqueo de duplicados, sin reintentos automáticos. Órdenes `startTakeoff`,
  `startLanding` y `cancelLanding` con **confirmación deliberada** en UI
  (deslizador o pulsación mantenida, como DJI Fly). Simulador reproduce éxito,
  rechazo y timeout.
- **Aceptación:** en sim, la orden recorre todos los estados y el duplicado se
  bloquea; con datos caducados la orden se deshabilita mostrando causa; tests
  JVM de la máquina de estados de órdenes; ninguna orden se dispara al abrir
  pantallas, rotar o reconectar.
- **Verificación:** puerta de calidad + tests; guion de ensayo real por fases
  (banco: solo lectura de precondiciones; vuelo: propietario operando).

### T-016 · RTH y altura de retorno

- **Depende de:** T-015. **Hardware:** como T-015.
- **Alcance:** `startGoHome`/`cancelGoHome`, lectura/ajuste de
  `goHomeHeightInMeters` con rango validado, estado «volviendo a casa» en HUD y
  home point registrado en telemetría. Avisos Smart RTH/batería del SDK
  mostrados sin inventar maniobras propias (la app nunca improvisa RTH:
  contrato del dron).
- **Aceptación:** como T-015 aplicado a RTH; cancelación disponible durante el
  retorno; el ajuste de altura rechaza valores fuera de rango con mensaje.
- **Verificación:** puerta de calidad + tests JVM; guion de ensayo real.

### T-017 · Foto y grabación de vídeo

- **Depende de:** T-014 (cámara visible con T-005 validada para vídeo real).
  **Hardware:** banco.
- **Alcance:** capacidad de cámara en el gateway: modo foto/vídeo, disparo
  (única; intervalo y AEB si el ensayo los confirma), start/stop grabación,
  tiempo de grabación e indicador de SD (presencia/espacio). Botones estilo
  DJI Fly en la pantalla Vuelo. Resultado de cada acción visible (pendiente/
  hecha/rechazada/desconocida). El simulador «captura» y lo refleja.
- **Aceptación:** en sim, disparo y grabación cambian estado e indicadores;
  órdenes deshabilitadas sin conexión o sin SD con causa visible; tests JVM del
  flujo de órdenes de cámara.
- **Verificación:** puerta de calidad; banco: foto y clip reales grabados en SD
  y verificados; anotar en ESTADO los modos que Mini 2 acepte realmente.

### T-018 · Ajustes de cámara y exposición

- **Depende de:** T-017. **Hardware:** banco.
- **Alcance:** panel de cámara: Auto/M (ISO, obturador, EV), balance de
  blancos, formato foto (JPEG/RAW), resolución/fps de vídeo según capacidades
  reportadas por el SDK (no lista fija), rejilla de composición local. Zoom
  digital solo si `Camera` lo expone para Mini 2 (verificar; si no, documentar
  y omitir). Valores actuales leídos del dron, no supuestos.
- **Aceptación:** en sim, panel navegable con capacidades simuladas; cambios
  aplicados se releen del gateway (no se muestra lo deseado como confirmado);
  parámetros no soportados ocultos o deshabilitados con causa.
- **Verificación:** puerta de calidad + tests de mapeo de capacidades; banco:
  matriz de valores aceptados por Mini 2 anotada en ESTADO.

### T-019 · Control de gimbal

- **Depende de:** T-014. **Hardware:** banco.
- **Alcance:** pitch del gimbal desde la pantalla Vuelo (deslizador lateral o
  arrastre vertical, como DJI Fly), recentrado, límites leídos del SDK,
  indicador de ángulo en HUD. Emisión con throttling; sin acumular órdenes.
  Preparado para que la rutina de seguimiento (T-006) también mande pitch por
  la misma vía única.
- **Aceptación:** en sim, el ángulo simulado sigue el gesto respetando límites;
  tests JVM del throttling/límites.
- **Verificación:** puerta de calidad; banco: movimiento real del gimbal con
  dron en mesa (no requiere motores).

### T-020 · Galería de medios del dron

- **Depende de:** T-004 (y T-017 útil para tener contenido). **Hardware:** banco.
- **Alcance:** MediaManager V4: listar SD con miniaturas, previsualizar,
  descargar a almacenamiento de la app compartible. Estados de carga/errores;
  descarga cancelable; sin borrar medios en la primera versión. Confirmar
  soporte Mini 2 en banco: si MediaManager no funciona en este modelo,
  documentarlo y cerrar la tarea con la evidencia.
- **Aceptación:** en sim, galería con medios ficticios y flujo de descarga
  simulado; errores visibles y reintentos manuales.
- **Verificación:** puerta de calidad; banco: listar/descargar un medio real.

### T-021 · Pantalla de mapa y «encontrar mi dron»

- **Depende de:** T-014. **Hardware:** no (posición simulada).
- **Alcance:** mapa con posición/rumbo del dron, posición del móvil, home
  point y trayecto de la sesión; centrar/seguir; «encontrar mi dron» (última
  posición conocida con timestamp, sonido no incluido). Stack por defecto:
  **Google Maps SDK for Android** con key en `local.properties`
  (`google.mapsApiKey`), igual de protegida que la de DJI; si el propietario
  prefiere evitar keys, alternativa MapLibre+OSM. Registrar la elección como
  D-008. Coordenadas reales nunca se versionan (usar `.local/` para evidencias).
- **Aceptación:** en sim, dron ficticio moviéndose sobre el mapa con trayecto y
  home; sin key el mapa falla con mensaje claro y el resto de la app funciona.
- **Verificación:** puerta de calidad; recorrido en emulador con capturas.

### T-022 · Zonas de vuelo DJI (GEO)

- **Depende de:** T-021, T-004. **Hardware:** banco recomendable.
- **Alcance:** `FlyZoneManager` V4: zonas del entorno pintadas por categoría
  (restringida, autorización, advertencia…) con leyenda y detalle al tocar.
  Solo consulta: el desbloqueo de zonas queda fuera del alcance. La base
  fly-safe empaquetada ya está en la variante real.
- **Aceptación:** en sim, capa con zonas ficticias de cada categoría; en real,
  datos del SDK sin bloquear la UI y con estado de carga/error visible.
- **Verificación:** puerta de calidad; banco: zonas reales alrededor de una
  ubicación de prueba (sin guardar coordenadas reales en Git).

### T-023 · Zonas oficiales ENAIRE (España)

- **Depende de:** T-021 (independiente de T-022). **Hardware:** no.
- **Alcance:** capa con las zonas geográficas UAS oficiales de España desde los
  servicios de ENAIRE (API AIS/ED-318 documentada en
  [aip.enaire.es](https://aip.enaire.es/AIP/UAS-es.html) y
  [drones.enaire.es](https://drones.enaire.es/)). La tarea empieza registrando
  endpoint, formato, licencia/condiciones de uso y frecuencia de actualización
  en DECISIONES; después: descarga con caché y fecha visible, pintado por tipo,
  filtro por altura de vuelo prevista, y aviso permanente de que la fuente
  vinculante es ENAIRE/AESA (la app no autoriza vuelos). Sin credenciales en
  Git si el servicio las exigiera.
- **Aceptación:** zonas visibles sobre el mapa con datos reales cacheados y su
  antigüedad; sin red se muestra la caché marcada como tal o un error claro;
  tests JVM del parseo con respuestas guardadas como fixtures.
- **Verificación:** puerta de calidad + tests; contraste manual de 2-3 zonas
  conocidas con el visor oficial ENAIRE Drones.

### T-024 · Pantalla de configuración

- **Depende de:** T-013 (secciones de seguridad requieren T-014/T-016).
  **Hardware:** banco para los parámetros del dron.
- **Alcance:** Ajustes con secciones: **Seguridad** (altura RTH, máx. altura y
  distancia vía FlightController, con rangos del SDK y lectura tras escritura),
  **Unidades** (métrico/imperial aplicado en HUD/mapa), **Cámara** (valores por
  defecto locales), **Visión** (hueco para parámetros de rutinas del
  propietario), **Acerca de** (versión app/SDK, estado de registro y de la App
  Key, modelo conectado, enlaces a documentación). Preferencias locales con
  DataStore; solo se persisten preferencias, nunca órdenes (contratos).
- **Aceptación:** en sim, todas las secciones navegables; parámetros del dron
  deshabilitados sin conexión con causa; unidades cambian toda la app; tests
  JVM de conversión de unidades y validación de rangos.
- **Verificación:** puerta de calidad + tests; banco: leer/escribir altura RTH
  real y confirmarla releyendo.

### T-025 · Avisos y diagnóstico

- **Depende de:** T-014. **Hardware:** banco.
- **Alcance:** panel de avisos alimentado por `DJIDiagnostics` y estados del
  SDK (compass/IMU, GPS débil, viento, batería baja/crítica, SD llena),
  priorizado y con histórico de la sesión; toast/banner para los críticos como
  DJI Fly. El simulador puede inyectar avisos para probar la UI.
- **Aceptación:** en sim, avisos de cada severidad visibles y ordenados; los
  críticos no se pierden al rotar; nada de traducciones inventadas de causas
  desconocidas (se muestra el texto/código del SDK).
- **Verificación:** puerta de calidad + tests del priorizado; banco: provocar
  un aviso real inofensivo (p. ej. GPS débil en interior).

### T-026 · Selección de objetivo y tracking en UI

- **Depende de:** T-011 (análisis validado). **Hardware:** móvil (visión) o
  emulador con clips.
- **Alcance:** sobre el overlay de detecciones de T-011: tocar una detección
  (persona, bici…) para fijarla como objetivo; mantenimiento de identidad entre
  frames (tracker del task MediaPipe o asociación propia sencilla), caja
  resaltada, estados «objetivo fijado / perdido / recuperado» con tiempos según
  el [contrato de visión](CONTRATO_VISION.md). Sin ninguna orden al dron:
  esta tarea es solo UI+visión. Deja la interfaz `TargetProvider` que consumirá
  la política de control.
- **Aceptación:** con clips de prueba, fijar/perder/recuperar objetivo funciona
  y se ve; tests JVM de la lógica de identidad/caducidad con observaciones
  falsas.
- **Verificación:** puerta de calidad + tests; demo grabada en `.local/`.

### T-027 · Sesión de control y emisor Virtual Stick

- **Depende de:** T-015 (framework de órdenes), T-004. **Hardware:** banco para
  verificación de modos; el ensayo con movimiento pertenece a T-006.
- **Alcance:** infraestructura de control visual del contrato del dron: gestión
  de **sesión** (activación deliberada con objetivo y límites visibles, parada
  siempre accesible, revocación por pérdida de vídeo/objetivo/enlace/background,
  no reactivación al reconectar) y **emisor único** Virtual Stick a cadencia
  fija dentro de 5-25 Hz que re-valida cada intención antes de enviar y ejecuta
  la salida validada al terminar. Modos/ejes/unidades V4 documentados en el
  código. Todo probado con dron simulado; la política concreta de seguimiento
  llega en T-006.
- **Aceptación:** tests JVM exhaustivos con dobles: caducidad, otra sesión,
  parada durante inferencia bloqueada, reconexión sin reactivación, cadencia
  independiente del FPS de visión (contrato de calidad los exige).
- **Verificación:** puerta de calidad + tests; banco: activar/desactivar modo
  Virtual Stick sin motores y registrar el comportamiento del mando.

### T-029 · Planificador de misiones por puntos GPS (solo planificación)

- **Depende de:** T-021 (mapa). **Hardware:** no.
- **Alcance:** editor de misiones sobre el mapa: añadir/mover/borrar puntos con
  altura y velocidad por punto, acciones simples (foto, pausa, mirar a un POI),
  ruta dibujada con distancia/duración estimadas. Validación al editar: límites
  de altura/distancia configurados (T-024), aviso si la ruta cruza zonas de
  T-022/T-023 (aviso, no bloqueo: la responsabilidad es del piloto). Guardado
  local de misiones con nombre (Room o DataStore); exportación GeoJSON opcional.
  **Sin ejecución**: esta tarea no envía nada al dron.
- **Aceptación:** crear, editar, guardar y recargar misiones en la variante sim;
  validaciones y avisos visibles; tests JVM de la validación de rutas y de la
  estimación de distancia/duración; fixtures con coordenadas ficticias (nunca
  ubicaciones reales del propietario en Git).
- **Verificación:** puerta de calidad + tests; recorrido en emulador con capturas.

### T-030 · Ejecución de misión guiada desde el móvil

- **Depende de:** T-027 (sesión/emisor), T-029, T-016 (RTH). **Hardware:**
  vuelo con propietario (por fases; primero todo en sim).
- **Alcance:** guiado por Virtual Stick hacia cada punto usando telemetría GPS
  del dron: control de rumbo/velocidad con corrección continua, llegada por
  radio de tolerancia, ejecución de acciones por punto, y controles pausar
  (hover) / reanudar / abortar (hover o RTH validado, a elección explícita).
  Reutiliza íntegra la sesión de T-027: parada visible, revocación por pérdida
  de vídeo/enlace/datos, sin reanudación automática tras reconectar. Límites
  documentados en UI antes de lanzar: requiere enlace continuo (si se pierde,
  manda el failsafe del mando/dron, no la app), precisión GPS de metros, yaw
  poco suave. El simulador incorpora una física simple (posición/velocidad) para
  ejecutar misiones completas sin dron.
- **Aceptación:** en sim, una misión de varios puntos se recorre con pausa,
  reanudación y aborto correctos; tests JVM del guiado (convergencia al punto,
  tolerancia, timeout por no-avance, aborto limpio, datos caducados detienen la
  emisión); ninguna misión arranca sin activación deliberada con resumen visible.
- **Verificación:** puerta de calidad + tests; ensayo real escalonado del
  [contrato del dron](CONTRATO_DRON.md): banco (modos sin motores) -> vuelo
  corto de 2-3 puntos a la vista, con guion entregado al propietario.

### T-031 · Asistencia visual frente a obstáculos (experimental)

- **Depende de:** T-011 (análisis) y T-027 (para reaccionar). **Hardware:**
  móvil con clips; validación final en vuelo.
- **Alcance:** el Mini 2 no tiene sensores de obstáculos; con una sola cámara
  **no se puede garantizar esquiva** y esta tarea no lo promete (contrato de
  producto). Se implementa una asistencia: detección de obstáculo probable en
  la trayectoria (detección de objetos + crecimiento de caja/expansión, y/o
  flujo óptico) que (a) muestra aviso siempre, y (b) durante rutinas propias
  (T-006/T-030/T-033) dispara la reacción validada: frenar a hover y pausar la
  rutina. Nunca maniobra de esquiva autónoma. Umbrales configurables en Ajustes
  (sección Visión); métrica de falsos positivos/negativos registrada con clips.
- **Aceptación:** con clips de acercamiento conocidos, el aviso salta dentro de
  la ventana definida y la tasa de falsos positivos queda medida y anotada; en
  sim, una misión se pausa ante un obstáculo inyectado; tests JVM de la política
  de reacción (histéresis, prioridad sobre otras órdenes, datos caducados).
- **Verificación:** puerta de calidad + tests + informe de métricas en ESTADO;
  ensayo real solo tras validar T-030 y con obstáculos controlados.

### T-032 · Reconocimiento de un objeto concreto (re-identificación)

- **Depende de:** T-010/T-011 (MediaPipe operativo). **Hardware:** móvil.
- **Alcance:** «busca mi perro / mi coche»: el usuario registra el objetivo con
  2-5 fotos de referencia; pipeline detector por categoría (perro, coche,
  persona… etiquetas COCO del Object Detector) + **MediaPipe Image Embedder**
  para comparar cada candidato con la firma de referencia por similitud coseno.
  Estados candidato/confirmado con umbrales e histéresis; galería local de
  objetivos guardados (fotos solo en el dispositivo). Al confirmar: alerta y
  opción de fijarlo como objetivo de T-026 (y de ahí seguimiento T-006).
  Registrar modelos usados (detector + embedder) con origen/hash/licencia según
  el [contrato de visión](CONTRATO_VISION.md).
- **Aceptación:** en Android (variante sim) con assets: distingue el objeto de
  referencia frente a distractores de la misma categoría con métricas anotadas;
  tests JVM de la lógica de decisión con similitudes falsas; latencia por frame
  medida en el móvil.
- **Verificación:** puerta de calidad + tests + métricas en ESTADO; prueba
  manual con el objeto real del propietario grabado en vídeo.

### T-033 · Búsqueda con patrón de barrido

- **Depende de:** T-030 y T-032 (T-031 recomendable activa). **Hardware:** vuelo.
- **Alcance:** definir un área en el mapa y generar automáticamente una misión
  de barrido (idas y venidas con solape según altura/FOV) que se ejecuta con
  T-030 mientras corre el reconocimiento de T-032. Al confirmar un hallazgo:
  pausa en hover, marca en el mapa la posición estimada (desde telemetría +
  gimbal, con su incertidumbre visible), aviso sonoro/visual y opciones:
  continuar barrido, fijar y seguir (T-006) o RTH. Todo dentro de una sesión
  con los mismos límites/paradas de T-027.
- **Aceptación:** en sim completa: barrido generado correcto para áreas de
  prueba (tests JVM del generador de patrón), detección inyectada pausa y marca
  en el mapa; la incertidumbre de posición se muestra, no se presenta como
  exacta.
- **Verificación:** puerta de calidad + tests; ensayo real escalonado con el
  propietario en área abierta y objetivo colocado a propósito.

### T-028 · Rutinas automáticas propias (tipo QuickShot) — futuro

- **Depende de:** T-006 validada en vuelo. **Hardware:** vuelo con propietario.
- **Alcance:** una primera rutina coreografiada (p. ej. órbita alrededor del
  objetivo fijado o dronie) construida sobre la misma sesión/emisor de T-027,
  parametrizada (radio, velocidad, duración) y con los mismos mecanismos de
  parada. Cada rutina adicional se acuerda como tarea propia.
- **Aceptación/verificación:** se detallará al activarla; hereda los requisitos
  de T-006 y del contrato del dron.

---

## Notas de alcance

- **Lo que DJI Fly hace y aquí no:** editor/social, actualización de firmware,
  tutoriales y desbloqueo GEO. Panorámica queda fuera mientras MSDK no la
  exponga. ActiveTrack no existe en Mini 2: el seguimiento es nuestro (visión).
- **Seguridad transversal:** ninguna tarea habilita órdenes automáticas fuera
  de una sesión activada por el usuario; los ensayos con hardware siguen las
  fases del [contrato del dron](CONTRATO_DRON.md) y los resultados se anotan en
  ESTADO sin datos privados.
- **Revisión del plan:** al cerrar cada fase (o si un ensayo revela un límite
  del Mini 2/SDK), se ajustan aquí las fichas afectadas en el mismo cambio.
