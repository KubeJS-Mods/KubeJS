package dev.latvian.mods.kubejs.recipe.viewer.server;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public record CategoryData(
	ResourceLocation category,
	List<ResourceLocation> removedRecipes
) {
	public static final StreamCodec<RegistryFriendlyByteBuf, CategoryData> STREAM_CODEC = StreamCodec.composite(
		ResourceLocation.STREAM_CODEC, CategoryData::category,
		ResourceLocation.STREAM_CODEC.apply(ByteBufCodecs.list()), CategoryData::removedRecipes,
		CategoryData::new
	);

	public CategoryData(ResourceLocation category) {
		this(category, new ArrayList<>());
	}

	public CategoryData lock() {
		return new CategoryData(category, List.copyOf(new HashSet<>(removedRecipes)));
	}
}
