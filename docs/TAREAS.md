# Tareas

Una tarea activa por checkout; dividir antes de empezar si deja de ser pequeña.
Las etapas posteriores son una propuesta de orden, no funcionalidades ya entregadas.

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

## Próximo paso

T-002 y T-004: confirmar equipo real, obtener la App Key de DJI Developer para
`io.github.jmoralese.djimini2cv` (guardarla en `local.properties` como
`dji.appKey`) y ensayar registro/conexión/vídeo en banco. En paralelo puede
avanzar T-009 (elegir rutina/modelo MediaPipe) y después T-010 sobre la variante
`sim`. Secuencia real: conexión T-004 -> vídeo T-005 -> visión T-011 -> control
T-006. Que la variante real compile no significa que esté probada con el dron.
