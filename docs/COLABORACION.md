# Colaboración entre Codex y Claude

## Fuente común

`AGENTS.md` contiene las instrucciones de ambas IAs. Codex lo carga como archivo
de instrucciones; `CLAUDE.md` lo importa para Claude Code. Las decisiones y el
estado viven en Git, no únicamente en conversaciones o memorias privadas.
Si se utiliza una interfaz distinta, adjuntar o hacer leer esos archivos.

No hay una IA principal permanente: cualquiera puede implementar, revisar y
cerrar tareas dentro del alcance indicado por el propietario.

## Alternancia habitual

1. La IA entrante lee estado y tareas, revisa rama, último commit y cambios pendientes.
2. La IA saliente debe estar detenida antes de que la otra edite el mismo checkout.
3. Continuar desde el código real. Si contradice el estado, verificar y corregir
   la nota; no sobrescribir código para hacer coincidir un resumen desactualizado.
4. Al cerrar, actualizar el estado y dejar un commit coherente. Si se agotan tokens,
   priorizar un relevo breve con archivos pendientes, problema y siguiente comando.

Prompt útil para retomar:

> Lee AGENTS.md, docs/ESTADO.md y docs/TAREAS.md, comprueba Git y continúa con la
> siguiente tarea pendiente dentro de su alcance. Conserva cambios ajenos y deja
> el estado actualizado con las pruebas realmente ejecutadas.

## Trabajo simultáneo excepcional

Dos agentes no cambian de rama ni escriben a la vez en el mismo directorio.
Para paralelizar, el coordinador asigna una tarea y archivos a cada agente y crea
una rama **y un worktree** independiente, por ejemplo:

```powershell
git worktree add ..\dji2-codex -b codex/tarea-corta
git worktree add ..\dji2-claude -b claude/otra-tarea
```

Crear los worktrees solo cuando hagan falta y desde una base conocida. El
coordinador integra commits revisados en una rama de integración o `main`,
resuelve conflictos y vuelve a ejecutar las comprobaciones afectadas.
Los archivos locales ignorados, SDK paths y claves no se copian automáticamente.
No usar OneDrive como mecanismo de merge entre clones o PCs.

## Relevo mínimo

El estado contiene: tarea/IA activa o libre, rama, resultado, evidencias,
archivos sin commit (si los hay), bloqueos y próximo paso concreto. Si dos
worktrees trabajan a la vez, cada uno entrega su resumen y el coordinador
consolida `ESTADO.md`/`TAREAS.md`; no compiten por editar esos archivos.

No hacen falta reuniones, revisiones cruzadas obligatorias ni un historial de
sesiones duplicando Git. Las preguntas al propietario se reservan para datos
necesarios y cambios de alcance; las decisiones pequeñas se resuelven trabajando.

## Fuentes de carga de instrucciones

Verificadas el 2026-09-06:

- [Codex: AGENTS.md](https://learn.chatgpt.com/docs/agent-configuration/agents-md).
- [Claude Code: memoria e importación de AGENTS.md](https://code.claude.com/docs/en/memory).

Estos archivos orientan a los agentes; la calidad se comprueba también revisando
diffs y ejecutando verificaciones. No constituyen un bloqueo técnico de acciones.
