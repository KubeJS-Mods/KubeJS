package dev.latvian.mods.kubejs.script;

import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.ContextFactory;

/// Extension of Rhino's [ContextFactory] that produces [KubeJSContext] instances
/// instead of regular [Context]. For [ScriptType#SERVER], a separate subclass is
/// used that currently has no special features.
///
/// @see KubeJSContext
public class KubeJSContextFactory extends ContextFactory {
	public final ScriptManager manager;

	public KubeJSContextFactory(ScriptManager manager) {
		this.manager = manager;
	}

	@Override
	protected KubeJSContext createContext() {
		return manager.scriptType.isServer() ? new KubeJSServerContext(this) : new KubeJSContext(this);
	}
}
