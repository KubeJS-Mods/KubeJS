package dev.latvian.mods.kubejs.plugin;

import com.google.gson.JsonElement;
import dev.latvian.mods.kubejs.block.entity.BlockEntityAttachmentType;
import dev.latvian.mods.kubejs.client.LangKubeEvent;
import dev.latvian.mods.kubejs.core.RecipeManagerKJS;
import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventGroupRegistry;
import dev.latvian.mods.kubejs.generator.KubeAssetGenerator;
import dev.latvian.mods.kubejs.generator.KubeDataGenerator;
import dev.latvian.mods.kubejs.recipe.RecipesKubeEvent;
import dev.latvian.mods.kubejs.recipe.ingredientaction.IngredientActionTypeRegistry;
import dev.latvian.mods.kubejs.recipe.schema.RecipeComponentFactoryRegistry;
import dev.latvian.mods.kubejs.recipe.schema.RecipeFactoryRegistry;
import dev.latvian.mods.kubejs.recipe.schema.RecipeMappingRegistry;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchemaRegistry;
import dev.latvian.mods.kubejs.recipe.viewer.RecipeViewerEntryType;
import dev.latvian.mods.kubejs.registry.BuilderTypeRegistry;
import dev.latvian.mods.kubejs.registry.ServerRegistryRegistry;
import dev.latvian.mods.kubejs.script.BindingRegistry;
import dev.latvian.mods.kubejs.script.DataComponentTypeInfoRegistry;
import dev.latvian.mods.kubejs.script.ScriptManager;
import dev.latvian.mods.kubejs.script.TypeDescriptionRegistry;
import dev.latvian.mods.kubejs.script.TypeWrapperRegistry;
import dev.latvian.mods.kubejs.server.DataExport;
import dev.latvian.mods.kubejs.util.AttachedData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public interface KubeJSPlugin {
	default void init() {
	}

	default void initStartup() {
	}

	default void afterInit() {
	}

	default void registerBuilderTypes(BuilderTypeRegistry registry) {
	}

	default void registerServerRegistries(ServerRegistryRegistry registry) {
	}

	/**
	 * Call {@link EventGroupRegistry#register(EventGroup)} for event groups your mod adds
	 */
	default void registerEvents(EventGroupRegistry registry) {
	}

	default void registerClasses(ClassFilter filter) {
	}

	default void registerBindings(BindingRegistry bindings) {
	}

	default void registerTypeWrappers(TypeWrapperRegistry registry) {
	}

	default void registerTypeDescriptions(TypeDescriptionRegistry registry) {
	}

	default void registerRecipeFactories(RecipeFactoryRegistry registry) {
	}

	default void registerRecipeMappings(RecipeMappingRegistry registry) {
	}

	default void registerRecipeComponents(RecipeComponentFactoryRegistry registry) {
	}

	default void registerRecipeSchemas(RecipeSchemaRegistry registry) {
	}

	default void registerBlockEntityAttachments(List<BlockEntityAttachmentType> types) {
	}

	default void registerIngredientActionTypes(IngredientActionTypeRegistry registry) {
	}

	default void registerRecipeViewerEntryTypes(Consumer<RecipeViewerEntryType> registry) {
	}

	default void registerDataComponentTypeDescriptions(DataComponentTypeInfoRegistry registry) {
	}

	default void attachServerData(AttachedData<MinecraftServer> event) {
	}

	default void attachLevelData(AttachedData<Level> event) {
	}

	default void attachPlayerData(AttachedData<Player> event) {
	}

	default void generateData(KubeDataGenerator generator) {
	}

	default void generateAssets(KubeAssetGenerator generator) {
	}

	default void generateLang(LangKubeEvent event) {
	}

	@Deprecated
	default void clearCaches() {
	}

	default void exportServerData(DataExport export) {
	}

	default void beforeRecipeLoading(RecipesKubeEvent event, RecipeManagerKJS manager, Map<ResourceLocation, JsonElement> recipeJsons) {
	}

	/**
	 * Only use this method if your mod adds runtime recipes and is conflicting with KubeJS recipe manager. Disable your other hook if "kubejs" mod is loaded!
	 */
	default void injectRuntimeRecipes(RecipesKubeEvent event, RecipeManagerKJS manager, Map<ResourceLocation, RecipeHolder<?>> recipesByName) {
	}

	default void beforeScriptsLoaded(ScriptManager manager) {
	}

	default void afterScriptsLoaded(ScriptManager manager) {
	}
}
