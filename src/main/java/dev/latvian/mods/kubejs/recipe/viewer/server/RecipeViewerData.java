package dev.latvian.mods.kubejs.recipe.viewer.server;

import dev.latvian.mods.kubejs.plugin.builtin.event.RecipeViewerEvents;
import dev.latvian.mods.kubejs.script.ScriptType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public record RecipeViewerData(
	List<ResourceLocation> removedCategories,
	List<ResourceLocation> removedGlobalRecipes,
	List<CategoryData> categoryData,
	ItemData itemData,
	FluidData fluidData
) {
	public static final StreamCodec<RegistryFriendlyByteBuf, RecipeViewerData> STREAM_CODEC = StreamCodec.composite(
		ResourceLocation.STREAM_CODEC.apply(ByteBufCodecs.list()), RecipeViewerData::removedCategories,
		ResourceLocation.STREAM_CODEC.apply(ByteBufCodecs.list()), RecipeViewerData::removedGlobalRecipes,
		CategoryData.STREAM_CODEC.apply(ByteBufCodecs.list()), RecipeViewerData::categoryData,
		ItemData.STREAM_CODEC, RecipeViewerData::itemData,
		FluidData.STREAM_CODEC, RecipeViewerData::fluidData,
		RecipeViewerData::new
	);

	@Nullable
	public static RecipeViewerData collect() {
		var removedCategories = new HashSet<ResourceLocation>();
		var removedGlobalRecipes = new HashSet<ResourceLocation>();
		var categoryData = new HashMap<ResourceLocation, CategoryData>();

		if (RecipeViewerEvents.REMOVE_CATEGORIES.hasListeners()) {
			RecipeViewerEvents.REMOVE_CATEGORIES.post(ScriptType.SERVER, new ServerRemoveCategoriesKubeEvent(removedCategories));
		}

		if (RecipeViewerEvents.REMOVE_RECIPES.hasListeners()) {
			RecipeViewerEvents.REMOVE_RECIPES.post(ScriptType.SERVER, new ServerRemoveRecipesKubeEvent(removedGlobalRecipes, categoryData));
		}

		var itemData = ItemData.collect();
		var fluidData = FluidData.collect();

		var data = new RecipeViewerData(
			List.copyOf(removedCategories),
			List.copyOf(removedGlobalRecipes),
			List.copyOf(categoryData.values().stream().map(CategoryData::lock).toList()),
			itemData,
			fluidData
		);

		return data.isEmpty() ? null : data;
	}

	public boolean isEmpty() {
		return itemData.isEmpty() && fluidData.isEmpty() && removedCategories.isEmpty() && removedGlobalRecipes.isEmpty() && categoryData.isEmpty();
	}
}
