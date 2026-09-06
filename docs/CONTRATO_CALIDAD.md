# Contrato de calidad

## Una tarea está terminada cuando

1. Cumple criterios de aceptación concretos y el diff se limita a su alcance.
2. Se revisaron errores, estados vacíos, permisos y ciclo de vida afectados.
3. Pasan las comprobaciones aplicables y se indica cuáles no pudieron ejecutarse.
4. No contiene credenciales ni datos reales de vuelos en código, logs versionados
   o documentación. No hay código muerto ni TODO que oculte un fallo funcional.
5. Estado y decisiones relevantes están actualizados y el cambio es revisable en Git.

Una tarea de documentación no requiere compilar una app que no ha cambiado.
Una prueba de emulador no certifica una integración DJI ni un vuelo seguro.

## Verificación proporcional

| Tipo de cambio | Evidencia necesaria |
| --- | --- |
| Markdown/configuración de repo | Revisión del contenido, enlaces locales y `git diff --check` |
| Scripts de entorno | Parseo PowerShell y ejecución del caso real; errores de rutas tratados con claridad |
| Lógica Kotlin | Compilación y pruebas unitarias de comportamiento nuevo o regresión |
| UI Android | Compilación/lint y recorrido manual o instrumentado de estados afectados |
| Adaptador DJI | Pruebas con doble para fallos y ciclo de vida; banco real para afirmar conexión/funcionamiento |
| Comandos de vuelo | Casos de rechazo, timeout, desconexión, duplicado y prueba manual del protocolo del dron |

No se exige porcentaje de cobertura, suite de capturas ni test por cada getter.
Sí se prueban decisiones que pueden producir acciones erróneas: datos caducados,
comandos repetidos, callbacks antiguos, pérdida de enlace y estado incierto.

## Puerta de entrada del primer proyecto Android

Al crear la app, añadir y ejecutar con el wrapper las tareas reales de ensamblado
debug, tests unitarios y lint de la variante simulada. Anotar los comandos exactos
en README: sus nombres dependerán de las variantes creadas.

Después añadir la verificación de compilación de la variante DJI. La aceptación
del registro SDK y la conexión se verifican en el teléfono real y se documentan
por separado. No bajar target SDK ni desactivar lint de forma general para ocultar
incompatibilidades; cualquier excepción concreta necesita motivo y seguimiento.

## Evidencia breve

En `docs/ESTADO.md`: fecha, comando/recorrido, resultado y límite. Para hardware,
añadir modelo/Android/firmware, versión de app y resultado sin seriales ni ubicación.
Logs extensos o capturas privadas van en `.local/`, con datos sensibles depurados
antes de compartirlos. No convertir el estado en un diario interminable.

## Git y revisiones

- Commits pequeños con mensajes descriptivos: `chore:`, `docs:`, `feat:`, `fix:` o
  `test:` si ayudan; no se exige una convención ceremonial.
- Revisar `git diff` y `git diff --cached` antes de cerrar un commit; añadir rutas
  explícitas para no llevarse cambios de otra IA.
- Una segunda IA puede revisar cambios complejos. No es requisito para cada tarea.
- CI y pull requests se añadirán al subir a GitHub y cuando exista algo ejecutable;
  no crear comprobaciones ficticias que siempre pasen.
