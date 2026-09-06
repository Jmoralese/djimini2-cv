# Auditoría del entorno Android

Fecha: **2026-09-06**. Inventario obtenido del PC y ejecutables reales, no deducido
solo del PATH. Las versiones son una fotografía del entorno, no la matriz de
compilación del futuro proyecto.

## Conclusión

El PC dispone de las herramientas básicas para empezar Android nativo. Java y
Android SDK estaban instalados pero no accesibles desde la terminal habitual;
`Use-AndroidEnv.ps1` resuelve esto para cada sesión sin alterar variables globales.

La preparación del PC no confirma aún que una app DJI funcione: no existe app
para compilar y no hay teléfono/mando/dron conectado. La primera base deberá
fijar y probar Gradle/AGP/Kotlin/SDK; la integración DJI requiere ensayo real.

## Inventario observado

| Componente | Resultado | Lectura práctica |
| --- | --- | --- |
| Sistema | Windows 11 Home x64, 10.0.26200 | Entorno Windows nativo |
| CPU | Intel Core i7-11800H | Procesador con capacidad para el desarrollo local |
| Memoria | 15,6 GiB utilizables | Un emulador a la vez; rendimiento con IDE/app aún sin medir |
| Disco C: | Aproximadamente 120,8 GiB libres al auditar | Espacio disponible para empezar |
| Gráficos | Intel UHD y NVIDIA RTX 3060 Laptop | Detectados, sin benchmark gráfico |
| Git | 2.47.1.windows.1 | Operativo; identidad existente conservada |
| Android Studio | Instalado, serie 2025.3; build 253.29346.138.2531.14876573 | En `%ProgramFiles%\Android\Android Studio` |
| JDK para terminal | Temurin 17.0.19, `java` y `javac` ejecutados | Detectado en `%USERPROFILE%\.gradle\jdks\eclipse_adoptium-17-amd64-windows.2` |
| Java del IDE | JBR 21.0.9 | No confundir Java del IDE con JDK de compilación elegido |
| Android SDK | `%LOCALAPPDATA%\Android\Sdk` | Instalado y accesible con el script |
| Command-line Tools | 20.0 en `cmdline-tools\latest` | `sdkmanager --version` y `--list_installed` funcionan |
| Plataforma Android | `platforms;android-36.1`, revisión 1 | Elegir paquete exacto según compileSdk del primer proyecto |
| Build Tools | 36.0.0, 36.1.0, 37.0.0-rc2 | Hay versiones estables; no adoptar la RC por defecto |
| Platform Tools / ADB | 36.0.2; protocolo 1.0.41 | Servidor ADB iniciado y consulta de dispositivos ejecutada |
| Emulador | 36.4.9.0 | `-accel-check`: WHPX instalado y utilizable, código 0 |
| AVD | `Medium_Phone_API_36.1`, imagen Google Play x86_64 | Configurado para Android 16/API 36.1 |
| Gradle disponible en caché | 9.2.1; `--version` ejecutado con JDK 17 | No es todavía el Gradle del proyecto |
| Licencias SDK | Registro local presente | No se aceptaron términos nuevos; otros paquetes pueden pedirlos |
| Dispositivo físico | Ninguno detectado por ADB | Depuración USB/Wi-Fi real pendiente |
| Agentes | Codex disponible; `claude` no encontrado en PATH | Instrucciones Claude preparadas; su cliente/sesión no verificado |

WMI devolvió indicadores de virtualización ambiguos bajo un hipervisor activo.
La prueba directa `emulator -accel-check` confirmó WHPX utilizable; no hay evidencia
que justifique cambiar BIOS ni instalar otro hipervisor.

La memoria está cerca del mínimo de 16 GB publicado para Studio con emulador en
Windows; no se requiere comprar hardware para esta preparación. Los requisitos
y el rendimiento real son cosas distintas. [Requisitos de Android Studio](https://developer.android.com/studio/install).

## Activar y repetir el diagnóstico

```powershell
. .\scripts\Use-AndroidEnv.ps1
java -version
adb version
powershell -NoProfile -File .\scripts\Test-AndroidEnvironment.ps1
```

Para usar otra instalación, los parámetros son `-SdkPath` y `-JdkPath`. El script
prefiere un JDK 17 detectado; después usa el JBR del IDE si no encuentra uno.
Al fijar el toolchain se elegirá una ruta/version compatible explícitamente.
La instalación de Java en caché de Gradle funciona ahora, pero puede desaparecer
si se limpia esa caché; en ese caso configurar otra instalación de JDK.

`Test-AndroidEnvironment.ps1` se ejecuta como proceso, **no mediante dot-sourcing**:
termina con código 0 si están las herramientas básicas y 1 si faltan o fallan.
También puede iniciar el servidor local ADB. No inicia AVD ni compila una app.
El 0 no certifica compatibilidad DJI ni que haya un teléfono real disponible.

Solo se encontró la plataforma 36.1. Esto no es un error: si el proyecto elige,
por ejemplo, compileSdk 34 o 36, habrá que instalar su paquete exacto desde SDK
Manager. No se han descargado paquetes ni cambiado licencias sin una necesidad
de compilación definida. `ANDROID_HOME` es la variable empleada; Android marca
`ANDROID_SDK_ROOT` como obsoleta. [Variables Android](https://developer.android.com/tools/variables).

No es necesario instalar Kotlin o Gradle globalmente. Al crear la app se
versionará Gradle Wrapper y la configuración de plugins. SDK paths y clave DJI
se mantendrán en configuración local ignorada por Git.

## Compatibilidad del DJI Mini 2

- **Modelo:** DJI confirma Mobile SDK para Mini 2 original; Mini 2 SE y Mini 4K
  figuran sin soporte. [Tabla de compatibilidad DJI](https://repair.dji.com/help/content?customId=01700000763&lang=en&paperDocType=ARTICLE&re=US&spaceId=17).
- **SDK:** la referencia oficial V4 es **4.18**. Mini 2 no aparece en la lista
  de V5. [Repositorio V4](https://github.com/dji-sdk/Mobile-SDK-Android),
  [repositorio V5](https://github.com/dji-sdk/Mobile-SDK-Android-V5).
- **Android:** V4.18, publicada el 24-10-2024, anuncia adaptación a Android 14.
  Lista firmware Mini 2 `01.07.0200`; es una referencia de la release, no una
  orden de actualizar el dron. [Notas oficiales DJI](https://developer.dji.com/document/122b5969-d64d-4752-8452-400c41240aa5).
- **Mantenimiento:** soporte DJI indicó en agosto de 2025 que V4 no recibiría
  más actualizaciones iterativas y que podría no adaptarse a Android 15.
  Android 15/16 no se declaran compatibles ni incompatibles sin ensayo concreto.
  [Respuesta oficial dji-dev](https://github.com/dji-sdk/Mobile-SDK-Android/issues/1343#issuecomment-3226686996).
- **Bibliotecas nativas:** la muestra V4.18 usa `armeabi-v7a` y `arm64-v8a`.
  El AVD x86_64 del PC servirá para la variante simulada. La muestra oficial
  documenta problemas x86; no es una prueba de DJI real.
  [Build de la muestra DJI](https://github.com/dji-sdk/Mobile-SDK-Android/blob/master/Sample%20Code/app/build.gradle).
- **Páginas de memoria:** en móviles que usen páginas de 16 KB hay que comprobar
  compatibilidad de las bibliotecas nativas. No todos los móviles Android 15+
  usan 16 KB. [Documentación Android](https://developer.android.com/guide/practices/page-sizes).
- **Simulación:** las notas DJI 4.16 señalan que Mini 2 no puede usar DA2 Simulator.
  El simulador MSDK usa el controlador de vuelo real; no sustituye al dron.
  Nuestra simulación será un doble propio sin hardware.
  [Notas DJI](https://developer.dji.com/document/122b5969-d64d-4752-8452-400c41240aa5),
  [pruebas y simulador MSDK](https://developer.dji.com/mobile-sdk/documentation/application-development-workflow/workflow-testing.html).

La página de notas DJI carga el contenido dinámicamente. Se verificó también la
[API pública que usa esa página](https://dev.dji.com/api/v1/documents/detail?content_uid=122b5969-d64d-4752-8452-400c41240aa5).
No se ha descargado ni integrado aún el artefacto SDK en este repositorio.

## Lo que falta para la prueba real

1. Confirmar modelo y Android del móvil, modelo de mando, y firmware actual del
   dron/mando. No hacen falta números de serie.
2. Teléfono compatible, mando, Mini 2 y cable de datos adecuado. Activar opciones
   de desarrollador y depuración, y autorizar el PC desde el teléfono.
3. Comprobar ADB; si Windows necesita driver del fabricante, instalar el oficial.
   Cuando el mando ocupa el USB del móvil, Android 11+ permite emparejar ADB por
   Wi-Fi en una red apropiada. [Pruebas en dispositivo Android](https://developer.android.com/studio/run/device).
4. Elegir `applicationId`, registrar la app en DJI Developer y obtener su App Key,
   guardándola fuera de Git. El primer registro SDK necesita acceso a Internet.
   [Registro y ejecución DJI](https://developer.dji.com/mobile-sdk/documentation/application-development-workflow/workflow-run.html).
5. Compilar e instalar la variante real y ensayar registro, conexión, lectura y
   reconexión en banco según el [contrato del dron](CONTRATO_DRON.md).

### Matriz que se completará con hardware

| Dato | Estado |
| --- | --- |
| Dron | Mini 2 según el propietario; pendiente confirmar en SDK |
| Mando y firmware | Pendiente |
| Firmware de dron | Pendiente |
| Teléfono y Android | Pendiente |
| ABI / tamaño de página | Pendiente; consultar `ro.product.cpu.abilist` y `getconf PAGE_SIZE` por ADB |
| Registro DJI / USB / telemetría | No probados |
| APK / SDK / toolchain probados | No existen todavía |

## Evidencia de esta sesión

- Inventario Windows, instalaciones y SDK; ejecución de Java/Javac 17 y JBR 21.
- `sdkmanager --version` y `--list_installed`: correctos.
- Gradle 9.2.1 de caché: `--version` correcto con Java 17; no se compiló app.
- `adb version`, consulta de dispositivos y `emulator -accel-check`: correctos.
- Scripts: parseo PS 5.1, ejecución real, PATH idempotente, rutas explícitas y
  rechazo de ruta inválida; caso sin SDK devuelve 1.
- Arranque temporal del AVD en modo `-read-only -no-window -no-snapshot-save`:
  correcto. ADB devolvió `sys.boot_completed=1`, Android 16, ABI x86_64 y páginas
  de 4096 bytes; el gestor de paquetes respondió. AVD cerrado al terminar.
  Esto valida el arranque del emulador, no una APK propia ni la integración DJI.

Logs técnicos temporales en `.local/`, fuera de Git. El diagnóstico y la creación
del repo no han instalado software ni cambiado variables del usuario/sistema.

El directorio está bajo OneDrive. Mantenerlo disponible localmente y evitar que
dos PCs sincronicen y editen el mismo `.git` a la vez. No se ha movido el proyecto.
