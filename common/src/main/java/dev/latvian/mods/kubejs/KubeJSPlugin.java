package dev.latvian.mods.kubejs;

import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.generator.AssetJsonGenerator;
import dev.latvian.mods.kubejs.generator.DataJsonGenerator;
import dev.latvian.mods.kubejs.level.LevelJS;
import dev.latvian.mods.kubejs.player.PlayerDataJS;
import dev.latvian.mods.kubejs.recipe.RegisterRecipeTypesEvent;
import dev.latvian.mods.kubejs.script.AttachDataEvent;
import dev.latvian.mods.kubejs.script.BindingsEvent;
import dev.latvian.mods.kubejs.script.CustomJavaToJsWrappersEvent;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.server.ServerJS;
import dev.latvian.mods.kubejs.util.ClassFilter;
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

	/**
	 * Call {@link EventHandler#register()} of events your mod adds
	 */
	public void registerEvents() {
	}

	public void registerClasses(ScriptType type, ClassFilter filter) {
	}

	public void registerBindings(BindingsEvent event) {
	}

	public void registerTypeWrappers(ScriptType type, TypeWrappers typeWrappers) {
	}

	public void registerCustomJavaToJsWrappers(CustomJavaToJsWrappersEvent event) {
	}

	public void registerRecipeTypes(RegisterRecipeTypesEvent event) {
	}

	public void attachServerData(AttachDataEvent<ServerJS> event) {
	}

	public void attachLevelData(AttachDataEvent<LevelJS> event) {
	}

	public void attachPlayerData(AttachDataEvent<PlayerDataJS> event) {
	}

	public void generateDataJsons(DataJsonGenerator generator) {
	}

	public void generateAssetJsons(AssetJsonGenerator generator) {
	}

	public void generateLang(Map<String, String> lang) {
	}
}
