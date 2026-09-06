# Tareas

Una tarea activa por checkout; dividir antes de empezar si deja de ser pequeña.
Las etapas posteriores son una propuesta de orden, no funcionalidades ya entregadas.
El plan completo de la app (fases, dependencias y fichas T-013…T-028 con
criterios y verificación) está en [PLAN_APP.md](PLAN_APP.md): un prompt
«haz la tarea T-0XX» basta para ejecutar cualquiera.

| ID | Estado | Resultado y aceptación |
| --- | --- | --- |
| T-001 | Hecha | PC auditado, contratos e instrucciones comunes preparados, scripts comprobados y base Git local |
| T-008 | Hecha | Objetivo MediaPipe en móvil, contratos de visión/control y requisitos auditados; AAR inspeccionados sin afirmar inferencia probada |
| T-012 | Hecha | Revisar repo DJI, preparar catálogo V4.18 y verificar descarga/transitivos mediante Gradle; sin importar el proyecto de ejemplo |
| T-003 | Hecha | Base Kotlin/Compose con variantes `sim`/`real`, wrapper 8.14.3 fijado, APK compilados, tests/lint en verde y recorrido en emulador (D-007) |
| T-002 | Pendiente | Confirmar teléfono/Android, mando y firmware del Mini 2; registrar matriz real sin seriales ni claves |
| T-004 | Pendiente | La variante real ya compila con MSDK V4.18 y adaptador/pantalla listos; falta App Key local, instalar en el teléfono, registro y lectura en banco con reconexión sin motores |
| T-009 | Pendiente | Elegir primera rutina visual, tarea MediaPipe/modelo, objetivo, entradas/salidas y criterios; registrar origen/licencia/hash del modelo |
| T-010 | Pendiente | Integrar AAR/modelo en variante sin DJI; carga e inferencia sobre assets conocidas en Android; tests de política con dron simulado |
| T-005 | Pendiente | La previsualización (VideoFeeder + DJICodecManager) está implementada sin probar; falta validarla en banco y obtener frames decodificados con formato/rotación/tiempos comprobados |
| T-011 | Pendiente | Conectar vídeo DJI con MediaPipe en solo análisis; validar detecciones, pérdida de objetivo/vídeo y medir latencia/carga en móvil |
| T-006 | Futuro | Implementar una rutina de control visual acotada con activación/parada, ejes/límites/caducidad probados y ensayo manual tras T-011 |
| T-007 | Cuando se solicite | Publicar en GitHub tras elegir visibilidad/licencia y revisar contenido; añadir CI para comandos existentes |
| T-013 | Pendiente | Navegación multi-pantalla (Inicio, Vuelo, Mapa, Galería, Ajustes); ficha en PLAN_APP.md |
| T-014 | Pendiente | Telemetría en el gateway y HUD de vuelo; ficha en PLAN_APP.md |
| T-015 | Pendiente | Framework de órdenes + despegue/aterrizaje con confirmación; ficha en PLAN_APP.md |
| T-016 | Pendiente | RTH, cancelación y altura de retorno; ficha en PLAN_APP.md |
| T-017 | Pendiente | Foto y grabación de vídeo con estados de orden; ficha en PLAN_APP.md |
| T-018 | Pendiente | Ajustes de cámara/exposición según capacidades reales; ficha en PLAN_APP.md |
| T-019 | Pendiente | Control de gimbal desde la pantalla de vuelo; ficha en PLAN_APP.md |
| T-020 | Pendiente | Galería de medios de la SD (MediaManager); ficha en PLAN_APP.md |
| T-021 | Pendiente | Pantalla de mapa y «encontrar mi dron»; ficha en PLAN_APP.md |
| T-022 | Pendiente | Zonas de vuelo DJI GEO en el mapa; ficha en PLAN_APP.md |
| T-023 | Pendiente | Capa oficial ENAIRE de zonas UAS; ficha en PLAN_APP.md |
| T-024 | Pendiente | Pantalla de configuración (seguridad, unidades, acerca de); ficha en PLAN_APP.md |
| T-025 | Pendiente | Avisos y diagnóstico del dron; ficha en PLAN_APP.md |
| T-026 | Pendiente | Selección de objetivo y tracking en UI (sin órdenes); ficha en PLAN_APP.md |
| T-027 | Pendiente | Sesión de control y emisor Virtual Stick; ficha en PLAN_APP.md |
| T-028 | Futuro | Rutinas automáticas propias tipo QuickShot; ficha en PLAN_APP.md |
| T-029 | Pendiente | Planificador de misiones por puntos GPS en el mapa (sin ejecución); ficha en PLAN_APP.md |
| T-030 | Pendiente | Ejecución de misión guiada desde el móvil vía Virtual Stick; ficha en PLAN_APP.md |
| T-031 | Pendiente | Asistencia visual frente a obstáculos, experimental (avisar/frenar, sin esquiva); ficha en PLAN_APP.md |
| T-032 | Pendiente | Reconocer un objeto concreto por re-identificación («busca mi perro/coche»); ficha en PLAN_APP.md |
| T-033 | Pendiente | Búsqueda con patrón de barrido sobre misión guiada; ficha en PLAN_APP.md |

## Próximo paso

T-002 y T-004: confirmar equipo real, obtener la App Key de DJI Developer para
`io.github.jmoralese.djimini2cv` (guardarla en `local.properties` como
`dji.appKey`) y ensayar registro/conexión/vídeo en banco. En paralelo puede
avanzar T-009 (elegir rutina/modelo MediaPipe) y después T-010 sobre la variante
`sim`. Secuencia real: conexión T-004 -> vídeo T-005 -> visión T-011 -> control
T-006. Que la variante real compile no significa que esté probada con el dron.
