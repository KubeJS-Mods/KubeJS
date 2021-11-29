package dev.latvian.mods.kubejs;

import dev.latvian.mods.kubejs.generator.AssetJsonGenerator;
import dev.latvian.mods.kubejs.generator.DataJsonGenerator;
import dev.latvian.mods.kubejs.player.AttachPlayerDataEvent;
import dev.latvian.mods.kubejs.recipe.RegisterRecipeHandlersEvent;
import dev.latvian.mods.kubejs.script.BindingsEvent;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.server.AttachServerDataEvent;
import dev.latvian.mods.kubejs.util.ClassFilter;
import dev.latvian.mods.kubejs.world.AttachWorldDataEvent;
import dev.latvian.mods.rhino.util.wrap.TypeWrappers;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.Map;

public class KubeJSPlugin {
	public void init() {
	}

	public void initStartup() {
	}

	@Environment(EnvType.CLIENT)
	public void clientInit() {
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

	public void generateDataJsons(DataJsonGenerator generator) {
	}

	public void generateAssetJsons(AssetJsonGenerator generator) {
	}

	public void generateLang(Map<String, String> lang) {
	}
}
