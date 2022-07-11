package dev.latvian.mods.kubejs.event;

import dev.latvian.mods.kubejs.script.ScriptType;

public class StartupEventJS extends EventJS {
	public final boolean post(String id) {
		return post(ScriptType.STARTUP, id);
	}
}
