package dev.latvian.mods.kubejs;

import dev.latvian.mods.kubejs.block.entity.BlockEntityAttachmentType;
import dev.latvian.mods.kubejs.client.ClientProperties;
import dev.latvian.mods.kubejs.client.LangKubeEvent;
import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventGroupRegistry;
import dev.latvian.mods.kubejs.generator.AssetJsonGenerator;
import dev.latvian.mods.kubejs.generator.DataJsonGenerator;
import dev.latvian.mods.kubejs.recipe.RecipesKubeEvent;
import dev.latvian.mods.kubejs.recipe.schema.RecipeComponentFactoryRegistryEvent;
import dev.latvian.mods.kubejs.recipe.schema.RegisterRecipeSchemasEvent;
import dev.latvian.mods.kubejs.registry.BuilderTypeRegistry;
import dev.latvian.mods.kubejs.script.BindingsEvent;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.script.WrapperRegistry;
import dev.latvian.mods.kubejs.server.DataExport;
import dev.latvian.mods.kubejs.util.AttachedData;
import dev.latvian.mods.kubejs.util.ClassFilter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Map;

public interface KubeJSPlugin {
	default void init() {
	}

	default void initStartup() {
	}

	default void clientInit() {
	}

	default void afterInit() {
	}

	default void onServerReload() {
	}

	default void registerBuilderTypes(BuilderTypeRegistry registry) {
	}

	/**
	 * Call {@link EventGroupRegistry#register(EventGroup)} for event groups your mod adds
	 */
	default void registerEvents(EventGroupRegistry registry) {
	}

	default void registerClasses(ScriptType type, ClassFilter filter) {
	}

	default void registerBindings(BindingsEvent event) {
	}

	default void registerTypeWrappers(WrapperRegistry registry) {
	}

	default void registerRecipeSchemas(RegisterRecipeSchemasEvent event) {
	}

	default void registerRecipeComponents(RecipeComponentFactoryRegistryEvent event) {
	}

	default void registerBlockEntityAttachments(List<BlockEntityAttachmentType> types) {
	}

	default void attachServerData(AttachedData<MinecraftServer> event) {
	}

	default void attachLevelData(AttachedData<Level> event) {
	}

	default void attachPlayerData(AttachedData<Player> event) {
	}

	default void generateDataJsons(DataJsonGenerator generator) {
	}

	default void generateAssetJsons(AssetJsonGenerator generator) {
	}

	default void generateLang(LangKubeEvent event) {
	}

	default void loadCommonProperties(CommonProperties properties) {
	}

	default void loadClientProperties(ClientProperties properties) {
	}

	default void loadDevProperties(DevProperties properties) {
	}

	default void clearCaches() {
	}

	default void exportServerData(DataExport export) {
	}

	/**
	 * Only use this method if your mod adds runtime recipes and is conflicting with KubeJS recipe manager. Disable your other hook if "kubejs" mod is loaded!
	 */
	default void injectRuntimeRecipes(RecipesKubeEvent event, RecipeManager manager, Map<ResourceLocation, RecipeHolder<?>> recipesByName) {
	}
}
