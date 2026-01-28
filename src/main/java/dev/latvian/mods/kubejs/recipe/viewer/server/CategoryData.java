package dev.latvian.mods.kubejs.recipe.viewer.server;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public record CategoryData(
	Identifier category,
	List<Identifier> removedRecipes
) {
	public static final StreamCodec<RegistryFriendlyByteBuf, CategoryData> STREAM_CODEC = StreamCodec.composite(
		Identifier.STREAM_CODEC, CategoryData::category,
		Identifier.STREAM_CODEC.apply(ByteBufCodecs.list()), CategoryData::removedRecipes,
		CategoryData::new
	);

	public CategoryData(Identifier category) {
		this(category, new ArrayList<>());
	}

	public CategoryData lock() {
		return new CategoryData(category, List.copyOf(new HashSet<>(removedRecipes)));
	}
}
