package dev.latvian.mods.kubejs.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.kubejs.item.ingredient.IngredientJS;
import dev.latvian.mods.kubejs.script.KubeJSContext;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.regexp.NativeRegExp;
import dev.latvian.mods.rhino.type.JSObjectTypeInfo;
import dev.latvian.mods.rhino.type.JSOptionalParam;
import dev.latvian.mods.rhino.type.RecordTypeInfo;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;
import java.util.regex.Pattern;

public record SlotFilter(Ingredient item, int index) {
	public static final SlotFilter EMPTY = new SlotFilter(Ingredient.EMPTY, -1);
	public static final TypeInfo TYPE_INFO = TypeInfo.INT.or(IngredientJS.TYPE_INFO).or(new JSObjectTypeInfo(List.of(new JSOptionalParam("item", IngredientJS.TYPE_INFO, true), new JSOptionalParam("index", TypeInfo.INT, true))));

	public static SlotFilter of(Ingredient ingredient, int index) {
		return ingredient.isEmpty() && index == -1 ? EMPTY : new SlotFilter(ingredient, index);
	}

	public static final Codec<SlotFilter> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Ingredient.CODEC.optionalFieldOf("item", Ingredient.EMPTY).forGetter(SlotFilter::item),
		Codec.INT.optionalFieldOf("index", -1).forGetter(SlotFilter::index)
	).apply(instance, SlotFilter::of));

	public static final StreamCodec<RegistryFriendlyByteBuf, SlotFilter> STREAM_CODEC = StreamCodec.composite(
		Ingredient.CONTENTS_STREAM_CODEC, SlotFilter::item,
		ByteBufCodecs.VAR_INT, SlotFilter::index,
		SlotFilter::of
	);

	public static SlotFilter wrap(Context cx, Object o, TypeInfo target) {
		if (o instanceof Number num) {
			return of(Ingredient.EMPTY, num.intValue());
		} else if (o instanceof String || o instanceof Ingredient || o instanceof NativeRegExp || o instanceof Pattern) {
			return of(IngredientJS.wrap(((KubeJSContext) cx).getRegistries(), o), -1);
		} else {
			return (SlotFilter) ((RecordTypeInfo) target).wrap(cx, o, target);
		}
	}

	public boolean checkFilter(int index, ItemStack stack) {
		return (this.index == -1 || this.index == index) && (this.item == Ingredient.EMPTY || this.item.test(stack));
	}
}
