package dev.latvian.mods.kubejs.script;

import dev.latvian.mods.rhino.ContextFactory;

public class KubeJSContextFactory extends ContextFactory {
	public final ScriptManager manager;

	public KubeJSContextFactory(ScriptManager manager) {
		this.manager = manager;
	}

	@Override
	protected KubeJSContext createContext() {
		return new KubeJSContext(this);
	}
}
