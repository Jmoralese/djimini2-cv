# Referencias e inspiración

Carpeta para **inputs de diseño e ideas**: capturas de apps existentes, bocetos,
paletas, enlaces y cualquier material que ayude a decidir cómo será nuestra app.

**Nada de esta carpeta forma parte de la aplicación.** No es código, no se
compila, no se empaqueta en el APK y no impone decisiones: solo sirve para
consultar y comparar. El material de terceros (capturas de DJI Fly, imágenes de
artículos) es de sus autores y se guarda aquí únicamente como referencia
interna; no debe redistribuirse ni incluirse en la app.

## dji-fly/

Capturas de la app oficial **DJI Fly** (la que usa el DJI Mini 2), descargadas
el 2026-09-06. Útiles para ver cómo resuelve DJI la vista de cámara, la
telemetría y los ajustes antes de diseñar nuestra propia interfaz.

### Ficha de la app (promocionales oficiales, APKPure, era Mavic Mini/Mini 2)

| Archivo | Qué muestra |
| --- | --- |
| `apkpure-ficha-01.jpg` | «Camera View»: vista de cámara en vuelo con telemetría (H/D, velocidades), minimapa y modo P |
| `apkpure-ficha-02.jpg` | «Flight Tutorial»: tutorial de vuelo guiado con objetivos sobre la imagen |
| `apkpure-ficha-03.jpg` | «New Editor»: editor de vídeo integrado con línea de tiempo |
| `apkpure-ficha-04.jpg` | «Fly Spots»: mapa de sitios recomendados para volar |

### Guía pix-pro.com (versiones recientes de DJI Fly; estructura equivalente)

| Archivo | Qué muestra |
| --- | --- |
| `pixpro-vista-camara-principal.jpg` | Vista de cámara anotada: modo N, aviso «no GPS», batería/señal, zoom, AF, minimapa, telemetría y ajustes de exposición |
| `pixpro-pantalla-inicio.jpg` | Pantalla de inicio anotada: Fly Spots, QuickTransfer, álbum, perfil y botón «GO FLY» (aparece un Mavic 3 Pro, no un Mini 2) |
| `pixpro-perfil-y-ajustes.jpg` | Menú de perfil y ajustes generales |
| `pixpro-modos-de-captura.jpg` | Selector de modos de captura (foto/vídeo/QuickShots…) |
| `pixpro-ajustes-foto.jpg` | Ajustes de cámara en modo foto |
| `pixpro-ajustes-video.jpg` | Ajustes de cámara en modo vídeo |
| `pixpro-seleccion-tracking.jpg` | Selección de sujeto para seguimiento (los modos Focus/ActiveTrack no existen en el Mini 2; interesa como patrón de interacción para nuestra visión con MediaPipe) |
| `pixpro-waypoints-mapa.jpg` | Planificación de waypoints sobre mapa (tampoco disponible en Mini 2) |

### Guía dronesgator.com (vuelo real)

| Archivo | Qué muestra |
| --- | --- |
| `dronesgator-interfaz-camara.webp` | Vista de cámara en vuelo real: cuadrícula, histograma, telemetría en unidades imperiales |
| `dronesgator-quickshots.webp` | Menú de QuickShots |
| `dronesgator-ajustes-seguridad.webp` | Panel de ajustes de seguridad (altura máxima, distancia, RTH) |

### Fuentes

- Ficha APKPure de DJI Fly (`dji.go.v5`): <https://apkpure.com/dji-fly/dji.go.v5>
- Guía DJI Fly de Pix-pro: <https://www.pix-pro.com/blog/dji-fly-guide-part1>
- Guía DJI Fly de Dronesgator: <https://dronesgator.com/dji-fly-app-guide>

Ojo al mirar las capturas recientes: el Mini 2 usa DJI MSDK V4 y una versión de
DJI Fly con menos funciones (sin ActiveTrack, sin waypoints). Las capturas
sirven como lenguaje visual y disposición de la información, no como catálogo
de funciones a replicar.
