package dev.latvian.mods.kubejs.bindings;

import dev.latvian.mods.kubejs.script.BindingsEvent;

public class JavaWrapper {
	private final BindingsEvent event;

	public JavaWrapper(BindingsEvent event) {
		this.event = event;
	}

	public Object loadClass(String className) {
		return event.manager.loadJavaClass(event, className, true);
	}
}
