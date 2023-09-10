package dev.latvian.mods.kubejs;

import dev.latvian.mods.kubejs.client.ClientProperties;
import dev.latvian.mods.kubejs.client.LangEventJS;
import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.generator.AssetJsonGenerator;
import dev.latvian.mods.kubejs.generator.DataJsonGenerator;
import dev.latvian.mods.kubejs.recipe.schema.RegisterRecipeSchemasEvent;
import dev.latvian.mods.kubejs.script.BindingsEvent;
import dev.latvian.mods.kubejs.script.CustomJavaToJsWrappersEvent;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.server.DataExport;
import dev.latvian.mods.kubejs.util.AttachedData;
import dev.latvian.mods.kubejs.util.ClassFilter;
import dev.latvian.mods.rhino.util.wrap.TypeWrappers;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

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

	public void onServerReload() {
	}

	/**
	 * Call {@link EventGroup#register()} of events your mod adds
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

	public void registerRecipeSchemas(RegisterRecipeSchemasEvent event) {
	}

	public void attachServerData(AttachedData<MinecraftServer> event) {
	}

	public void attachLevelData(AttachedData<Level> event) {
	}

	public void attachPlayerData(AttachedData<Player> event) {
	}

	public void generateDataJsons(DataJsonGenerator generator) {
	}

	public void generateAssetJsons(AssetJsonGenerator generator) {
	}

	public void generateLang(LangEventJS event) {
	}

	public void loadCommonProperties(CommonProperties properties) {
	}

	@Environment(EnvType.CLIENT)
	public void loadClientProperties(ClientProperties properties) {
	}

	public void loadDevProperties(DevProperties properties) {
	}

	public void clearCaches() {
	}

	public void exportServerData(DataExport export) {
	}

	@Override
	public String toString() {
		return getClass().getName();
	}
}
