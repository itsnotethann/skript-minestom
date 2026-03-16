package ch.njol.skript.events;

import ch.njol.skript.Skript;
import ch.njol.skript.events.custom.ScriptUnloadEvent;
import ch.njol.skript.lang.util.SimpleEvent;

public class EvtScriptUnload extends SimpleEvent {
	static {
		Skript.registerEvent("Script Unload", EvtScriptUnload.class, ScriptUnloadEvent.class,
			"[script] (un[ ]load|disable)");
	}

	@Override
	public void unload() {
		trigger.execute(new ScriptUnloadEvent());
		super.unload();
	}
}
