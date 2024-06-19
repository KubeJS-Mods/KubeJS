package dev.latvian.mods.kubejs.recipe.viewer.server;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public record RecipeViewerData(
	ItemData itemData,
	FluidData fluidData,
	List<ResourceLocation> removedCategories,
	List<ResourceLocation> removedGlobalRecipes,
	List<CategoryData> categoryData
) {
	public static final StreamCodec<RegistryFriendlyByteBuf, RecipeViewerData> STREAM_CODEC = StreamCodec.composite(
		ItemData.STREAM_CODEC, RecipeViewerData::itemData,
		FluidData.STREAM_CODEC, RecipeViewerData::fluidData,
		ResourceLocation.STREAM_CODEC.apply(ByteBufCodecs.list()), RecipeViewerData::removedCategories,
		ResourceLocation.STREAM_CODEC.apply(ByteBufCodecs.list()), RecipeViewerData::removedGlobalRecipes,
		CategoryData.STREAM_CODEC.apply(ByteBufCodecs.list()), RecipeViewerData::categoryData,
		RecipeViewerData::new
	);

	public static RecipeViewerData collect() {
		var removedCategories = new ArrayList<ResourceLocation>();
		var removedGlobalRecipes = new ArrayList<ResourceLocation>();
		var categoryData = new HashMap<ResourceLocation, CategoryData>();

		return new RecipeViewerData(
			ItemData.collect(),
			FluidData.collect(),
			List.copyOf(removedCategories),
			List.copyOf(removedGlobalRecipes),
			List.copyOf(categoryData.values().stream().map(CategoryData::lock).toList())
		);
	}
}
