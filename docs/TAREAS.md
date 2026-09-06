# Tareas

Una tarea activa por checkout; dividir antes de empezar si deja de ser pequeña.
Las etapas posteriores son una propuesta de orden, no funcionalidades ya entregadas.

| ID | Estado | Resultado y aceptación |
| --- | --- | --- |
| T-001 | Hecha | PC auditado, contratos e instrucciones comunes preparados, scripts comprobados y base Git local |
| T-008 | Hecha | Objetivo MediaPipe en móvil, contratos de visión/control y requisitos auditados; AAR inspeccionados sin afirmar inferencia probada |
| T-012 | Hecha | Revisar repo DJI, preparar catálogo V4.18 y verificar descarga/transitivos mediante Gradle; sin importar el proyecto de ejemplo |
| T-002 | Pendiente | Confirmar teléfono/Android, mando y firmware del Mini 2; registrar matriz real sin seriales ni claves |
| T-003 | Pendiente | Crear base Kotlin nativa y variante simulada, fijar toolchain/wrapper, compilar APK, pasar tests/lint e instalar en emulador |
| T-004 | Pendiente | Integrar MSDK V4 en variante real: registrar app/clave local, compilar e instalar, leer estado en banco y probar reconexión sin motores |
| T-009 | Pendiente | Elegir primera rutina visual, tarea MediaPipe/modelo, objetivo, entradas/salidas y criterios; registrar origen/licencia/hash del modelo |
| T-010 | Pendiente | Integrar AAR/modelo en variante sin DJI; carga e inferencia sobre assets conocidas en Android; tests de política con dron simulado |
| T-005 | Pendiente | Obtener vídeo y frames decodificados DJI con formato/rotación/tiempos comprobados, y previsualización estable |
| T-011 | Pendiente | Conectar vídeo DJI con MediaPipe en solo análisis; validar detecciones, pérdida de objetivo/vídeo y medir latencia/carga en móvil |
| T-006 | Futuro | Implementar una rutina de control visual acotada con activación/parada, ejes/límites/caducidad probados y ensayo manual tras T-011 |
| T-007 | Cuando se solicite | Publicar en GitHub tras elegir visibilidad/licencia y revisar contenido; añadir CI para comandos existentes |

## Próximo paso

Concretar T-009 y crear la base T-003. T-010 puede avanzar
con datos de prueba y sin dron; un emulador puede no soportar todas las tareas.
T-003/T-004 deben consumir el catálogo ya preparado siguiendo
[INTEGRACION_DJI.md](INTEGRACION_DJI.md). T-004 requiere equipo/datos de T-002 y una App Key DJI para el `applicationId`
elegido. Secuencia real: conexión T-004 -> vídeo T-005 -> visión T-011 -> control
T-006. No confundir el SDK descargado con una rutina integrada y probada.
