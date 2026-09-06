# Cómo incorporar DJI Mobile SDK V4

## Decisión

Consumir el SDK como dependencia de **Maven Central**. El repositorio
[Mobile-SDK-Android](https://github.com/dji-sdk/Mobile-SDK-Android) sirve como
referencia de código; no hay que copiarlo entero, añadirlo como submódulo ni
convertir nuestra app en un fork de su muestra.

El propio ejemplo oficial usa esta distribución. Se revisó la versión del repo
`37afa622a1e95f179e2d5431f0afe4203ee5ca88` el 2026-09-06:
[dependencias del ejemplo](https://github.com/dji-sdk/Mobile-SDK-Android/blob/37afa622a1e95f179e2d5431f0afe4203ee5ca88/Sample%20Code/app/build.gradle).
Su build usa AGP 4.2.2/Kotlin 1.5.10; no trasladar esa combinación completa a
nuestro proyecto. La matriz Android propia sigue pendiente de validación.

## Lo que queda en Git

El [catálogo de versiones](../gradle/libs.versions.toml) fija:

| Alias Gradle | Artefacto | Uso al crear la variante real |
| --- | --- | --- |
| `libs.dji.runtime` | `com.dji:dji-sdk:4.18` | `implementation`: biblioteca que se incorpora al APK |
| `libs.dji.provided` | `com.dji:dji-sdk-provided:4.18` | `compileOnly`: APIs de compilación; no empaquetar este JAR en el APK |

El catálogo no descarga nada por sí solo. Al crear el proyecto Android, Gradle
cargará `gradle/libs.versions.toml` y el módulo tendrá que consumir esos alias.
Se versionarán el wrapper, build, configuración y código propios. Los AAR/JAR
descargados por Gradle pertenecen a su caché, fuera de nuestro repositorio.

## Integración en la futura app

1. Crear el módulo Kotlin y las variantes de simulación y hardware, con una
   combinación de Gradle/AGP/JDK comprobada. Mantener Android mínimo 24 o el mayor
   que exijan las dependencias elegidas para MediaPipe y el teléfono.
2. Configurar `mavenCentral()` para DJI y `google()` para dependencias Android y
   MediaPipe. Añadir ambos alias **solo a la variante real**, con los scopes de
   la tabla. La variante simulada no carga ni empaqueta DJI.
3. Excluir `com.dji:library-anti-distortion` de la dependencia runtime, igual que
   la muestra oficial: corresponde a la corrección de Mavic 2 Pro/Zoom y no se
   necesita para Mini 2. Conservar `fly-safe-database` inicialmente; no eliminar
   otras dependencias por tamaño sin justificarlo y comprobar su efecto.
4. Adaptar manifiesto/permisos, manejo USB, inicialización V4.18 y reglas de
   empaquetado/R8 al proyecto propio. La muestra filtra `arm64-v8a` y
   `armeabi-v7a`; el SDK real no se prueba en nuestro AVD x86_64.
5. Configurar `applicationId` y App Key DJI local sin versionarla; el registro y
   la conexión se implementan en el adaptador DJI. No usar la clave ni el
   identificador de la aplicación de ejemplo.
6. Compilar la variante real y validar registro/telemetría/reconexión en banco.
   Después integrar vídeo y MediaPipe según los contratos; descargar dependencias
   no demuestra funcionamiento del SDK en el móvil.

Fuentes: [build oficial](https://github.com/dji-sdk/Mobile-SDK-Android/blob/37afa622a1e95f179e2d5431f0afe4203ee5ca88/Sample%20Code/app/build.gradle),
[guía de integración DJI](https://developer.dji.com/mobile-sdk/documentation/application-development-workflow/workflow-integrate.html).
La guía y los ejemplos contienen instrucciones de distintas antigüedades;
contrastar la inicialización con las [notas de V4.18](https://developer.dji.com/document/122b5969-d64d-4752-8452-400c41240aa5).

## Verificación realizada en este PC

- Gradle **9.2.1** ya disponible y JDK **17.0.19**, sin instalar otro toolchain.
- Un build temporal en `.local/dji-dependency-check/` importó el catálogo real
  y ejecutó `verifyDjiDependencies` contra Maven Central: **BUILD SUCCESSFUL**,
  salida 0, 14 artefactos resueltos incluyendo transitivos y `provided`.
- SDK runtime: AAR de 59.037.696 bytes; `provided`: JAR de 16.570.534 bytes.
  Descargas en la caché habitual `%USERPROFILE%\.gradle\caches`.
- AAR inspeccionado: contiene bibliotecas ARM y x86, sin x86_64; el manifiesto
  declara mínimo 21. Estos datos no sustituyen los mínimos de nuestra app ni
  la recomendación de ABI del ejemplo para hardware real.
- La auditoría excluyó `library-anti-distortion` y conservó la base fly-safe.
  Build temporal y log están ignorados por Git. No se ha generado APK, inicializado
  DJI ni conectado hardware; tampoco se validó resolución conjunta con MediaPipe.

## Código de ejemplo y licencias

No se ha incorporado código de la muestra. Si se adapta un fragmento, registrar
el archivo/commit de origen y conservar los avisos exigidos por su licencia MIT.
El `LICENSE.txt` de DJI distingue la licencia de ejemplos de la EULA del SDK y
los avisos de sus componentes; no atribuir MIT a todo el SDK basándose solo en
la metadata Maven. [Licencia publicada por DJI](https://github.com/dji-sdk/Mobile-SDK-Android/blob/37afa622a1e95f179e2d5431f0afe4203ee5ca88/LICENSE.txt).
