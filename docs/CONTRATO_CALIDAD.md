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
| MediaPipe/modelo | Carga e inferencia reales en Android con imágenes/clips conocidos; formato/coordenadas correctos y comportamiento ante modelo ausente/inválido |
| Política de control visual | Tests JVM con observaciones falsas: caducidad, confianza, objetivo perdido, otra sesión, ejes/límites y parada independiente de inferencia |
| Comandos de vuelo | Casos de rechazo, timeout, desconexión, duplicado y prueba manual del protocolo del dron |

No se exige porcentaje de cobertura, suite de capturas ni test por cada getter.
Sí se prueban decisiones que pueden producir acciones erróneas: datos caducados,
comandos repetidos, callbacks antiguos, pérdida de enlace y estado incierto.
También vídeo congelado, resultados fuera de orden y rutina detenida que no debe
reactivarse al reconectar. Las consignas continuas vigentes se distinguen de los
duplicados de acciones discretas según el contrato del dron.

## Puerta de entrada del primer proyecto Android

La puerta está en vigor desde T-003. Con el wrapper (`.\gradlew.bat`), las tareas
reales son `:app:assembleSimDebug`, `:app:testSimDebugUnitTest` y
`:app:lintSimDebug` para la variante simulada, y `:app:assembleRealDebug` como
verificación de compilación/empaquetado de la variante DJI; están anotadas en
README. Ejecutarlas cuando el cambio toque código o build de la app.

La aceptación del registro SDK y la conexión se verifican en el teléfono real y
se documentan por separado. No bajar target SDK ni desactivar lint de forma
general para ocultar incompatibilidades; cualquier excepción concreta necesita
motivo y seguimiento. El targetSdk inicial (34) está justificado en D-007.

## Puerta de entrada de visión y control

La variante sin DJI debe poder ejecutar MediaPipe real sobre assets de prueba.
Fijar y registrar versión del AAR y modelo; comprobar ABI y carga de bibliotecas
nativas en Android. Tests JVM con dobles no demuestran inferencia nativa.

Antes de control real, comprobar en el móvil vídeo DJI + inferencia simultáneos,
con resolución/FPS, latencia y variación, frames descartados, memoria/temperatura
y frecuencia de control dentro de los límites definidos para la rutina. Validar
parada y revocación de sesión aunque no lleguen callbacks de visión. Emulador,
prueba en banco y vuelo mantienen evidencias separadas.

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
