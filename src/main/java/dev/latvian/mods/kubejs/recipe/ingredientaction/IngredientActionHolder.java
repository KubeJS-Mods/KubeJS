package dev.latvian.mods.kubejs.recipe.ingredientaction;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.kubejs.util.SlotFilter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public record IngredientActionHolder(IngredientAction action, SlotFilter filter) {
	public static final Codec<IngredientActionHolder> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		IngredientAction.CODEC.fieldOf("action").forGetter(IngredientActionHolder::action),
		SlotFilter.CODEC.optionalFieldOf("filter", SlotFilter.EMPTY).forGetter(IngredientActionHolder::filter)
	).apply(instance, IngredientActionHolder::new));

	public static final Codec<List<IngredientActionHolder>> LIST_CODEC = CODEC.listOf();

	public static final StreamCodec<RegistryFriendlyByteBuf, IngredientActionHolder> STREAM_CODEC = StreamCodec.composite(
		IngredientAction.STREAM_CODEC, IngredientActionHolder::action,
		SlotFilter.STREAM_CODEC, IngredientActionHolder::filter,
		IngredientActionHolder::new
	);

	public static final StreamCodec<RegistryFriendlyByteBuf, List<IngredientActionHolder>> LIST_STREAM_CODEC = STREAM_CODEC.apply(ByteBufCodecs.list());
}
