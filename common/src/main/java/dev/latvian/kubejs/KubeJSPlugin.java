package dev.latvian.kubejs;

import dev.latvian.kubejs.player.AttachPlayerDataEvent;
import dev.latvian.kubejs.recipe.RegisterRecipeHandlersEvent;
import dev.latvian.kubejs.script.BindingsEvent;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.server.AttachServerDataEvent;
import dev.latvian.kubejs.util.ClassFilter;
import dev.latvian.kubejs.world.AttachWorldDataEvent;
import dev.latvian.mods.rhino.util.wrap.TypeWrappers;

public class KubeJSPlugin {
	public void init() {
	}

	public void afterInit() {
	}

	public void addClasses(ScriptType type, ClassFilter filter) {
	}

	public void addBindings(BindingsEvent event) {
	}

	public void addTypeWrappers(ScriptType type, TypeWrappers typeWrappers) {
	}

	public void addRecipes(RegisterRecipeHandlersEvent event) {
	}

	public void attachServerData(AttachServerDataEvent event) {
	}

	public void attachWorldData(AttachWorldDataEvent event) {
	}

	public void attachPlayerData(AttachPlayerDataEvent event) {
	}
}
