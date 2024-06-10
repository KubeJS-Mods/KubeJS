package dev.latvian.mods.kubejs.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.kubejs.item.ingredient.IngredientJS;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.type.RecordTypeInfo;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public record SlotFilter(Ingredient item, int index) {
	public static final SlotFilter EMPTY = new SlotFilter(Ingredient.EMPTY, -1);

	public static SlotFilter of(Ingredient ingredient, int index) {
		return ingredient.isEmpty() && index == -1 ? EMPTY : new SlotFilter(ingredient, index);
	}

	public static final Codec<SlotFilter> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Ingredient.CODEC.optionalFieldOf("item", Ingredient.EMPTY).forGetter(SlotFilter::item),
		Codec.INT.optionalFieldOf("index", -1).forGetter(SlotFilter::index)
	).apply(instance, SlotFilter::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, SlotFilter> STREAM_CODEC = StreamCodec.composite(
		Ingredient.CONTENTS_STREAM_CODEC, SlotFilter::item,
		ByteBufCodecs.VAR_INT, SlotFilter::index,
		SlotFilter::new
	);

	public static SlotFilter wrap(Context cx, Object o, TypeInfo target) {
		if (o instanceof Number num) {
			return of(Ingredient.EMPTY, num.intValue());
		} else if (o instanceof String || o instanceof Ingredient) {
			return of(IngredientJS.wrap(cx, o), -1);
		} else {
			return (SlotFilter) ((RecordTypeInfo) target).wrap(cx, o, target);
		}
	}

	public boolean checkFilter(int index, ItemStack stack) {
		return (this.index == -1 || this.index == index) && (this.item.isEmpty() || this.item.test(stack));
	}
}
