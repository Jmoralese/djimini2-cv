# Tareas

Una tarea activa por checkout; dividir antes de empezar si deja de ser pequeña.
Las etapas posteriores son una propuesta de orden, no funcionalidades ya entregadas.

| ID | Estado | Resultado y aceptación |
| --- | --- | --- |
| T-001 | Hecha | PC auditado, contratos e instrucciones comunes preparados, scripts comprobados y base Git local |
| T-002 | Pendiente | Confirmar teléfono/Android, mando y firmware del Mini 2; registrar matriz real sin seriales ni claves |
| T-003 | Pendiente | Crear base Kotlin nativa y variante simulada, fijar toolchain/wrapper, compilar APK, pasar tests/lint e instalar en emulador |
| T-004 | Pendiente | Integrar MSDK V4 en variante real: registrar app/clave local, compilar e instalar, leer estado en banco y probar reconexión sin motores |
| T-005 | Pendiente | Definir con el propietario primeras funciones de vídeo, cámara y gimbal; implementar y verificar cada una |
| T-006 | Futuro | Acordar una función de vuelo concreta, sus precondiciones y protocolo; implementar solo después de validar la base real |
| T-007 | Cuando se solicite | Publicar en GitHub tras elegir visibilidad/licencia y revisar contenido; añadir CI para comandos existentes |

## Próximo paso

El siguiente desarrollo de código es T-003; puede avanzar sin teléfono con la
simulación. T-004 requiere los datos/equipo de T-002 y
una clave de aplicación DJI que corresponda al `applicationId` elegido.
