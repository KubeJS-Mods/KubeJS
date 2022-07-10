package dev.latvian.mods.kubejs.event;

import dev.latvian.mods.kubejs.script.ScriptType;

public class StartupEventJS extends EventJS {
	public static final EventHandler INIT_EVENT = EventHandler.startup(StartupEventJS.class).legacy("init");
	public static final EventHandler POST_INIT_EVENT = EventHandler.startup(StartupEventJS.class).legacy("postinit");
	public static final EventHandler LOADED_EVENT = EventHandler.startup(StartupEventJS.class).legacy("loaded");

	public final boolean post(String id) {
		return post(ScriptType.STARTUP, id);
	}
}
