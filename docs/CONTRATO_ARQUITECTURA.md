# Contrato de arquitectura

## Base y decisiones todavía abiertas

- Android nativo, Kotlin y Android Studio. Jetpack Compose es la opción inicial
  para UI nueva; una vista Android tradicional puede integrar vídeo del SDK.
- DJI Mobile SDK V4, referencia inicial 4.18. No sustituirlo por V5 por ser más nuevo.
- Un solo módulo `app` al empezar, organizado por responsabilidades; crear módulos
  adicionales solo si simplifican una necesidad demostrada.
- Fijar JDK, Gradle Wrapper, Android Gradle Plugin, Kotlin, compile/target/min SDK
  y dependencias cuando se cree y compruebe el primer proyecto. La instalación
  más reciente del PC no demuestra compatibilidad del SDK DJI.

## Límites del código

```text
UI -> ViewModel / estado de pantalla -> lógica de aplicación -> DroneGateway
                                                            |-> simulación
                                                            |-> adaptador DJI V4
```

Los nombres son orientativos; los límites son el contrato:

- La UI representa estado y recoge intenciones. No llama directamente a DJI.
- La lógica de aplicación decide si una acción está permitida. No depende de
  clases DJI ni del ciclo de vida de una Activity.
- `DroneGateway` expone operaciones necesarias, capacidades, conexión y telemetría
  con tipos propios. No convertirlo en copia de toda la API DJI.
- El adaptador real concentra registro SDK, USB, callbacks, errores y traducciones.
  El simulado implementa el mismo comportamiento observable, incluidos fallos.
- El modo simulado no carga bibliotecas nativas DJI. La separación se hará con
  variantes/source sets u otra solución pequeña comprobada al crear la app.

## Estado y concurrencia

- Estados explícitos: desconectado, conectando, conectado y error, distinguiendo
  inicialización/registro de conexión física cuando corresponda.
- Datos inmutables hacia UI, unidades explícitas y tiempo de actualización para
  telemetría. Usar tiempo monotónico para caducidad y timeouts.
- Coroutines/Flow para trabajo asíncrono cuando resulten útiles; sin `GlobalScope`,
  bloqueo del hilo principal ni listeners que sobrevivan a su propietario.
- Registrar y retirar callbacks de forma simétrica; descartar callbacks de una
  sesión antigua tras reconexión. Las órdenes no son estado persistente reejecutable.
- Modelar error y resultado incierto. No tragar excepciones ni simular éxito.

## Configuración y dependencias

- Versiones exactas en un único lugar, preferiblemente catálogo Gradle; sin `+`
  ni actualizaciones masivas mezcladas con funcionalidad.
- Versionar el wrapper completo y su checksum de distribución. Registrar la
  combinación de herramientas que realmente compila.
- Evitar frameworks de inyección al empezar: composición explícita suficiente.
- Clave DJI fuera de Git; documentar la inyección local al crear el proyecto.
  Si falta, el modo real falla de forma clara y la simulación sigue disponible.
- La clave incrustada en un APK no es un secreto imposible de extraer. Nunca
  incrustar credenciales de cuenta DJI ni claves privadas de firma.
- Persistir solo preferencias necesarias. No guardar órdenes pendientes de vuelo.

## Cómo evoluciona

Una decisión que cambie estos límites o la compatibilidad se registra en
[DECISIONES.md](DECISIONES.md), con evidencia y consecuencias. No crear capas,
interfaces o documentos adicionales solo para parecer un proyecto empresarial.
