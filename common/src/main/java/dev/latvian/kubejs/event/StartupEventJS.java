package dev.latvian.kubejs.event;

import dev.latvian.kubejs.script.ScriptType;

public class StartupEventJS extends EventJS {
	public final boolean post(String id) {
		return post(ScriptType.STARTUP, id);
	}

	public final boolean post(String id, String sub) {
		return post(ScriptType.STARTUP, id, sub);
	}
}
