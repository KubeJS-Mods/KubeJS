package dev.latvian.mods.kubejs.recipe.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.error.InvalidRecipeComponentValueException;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.ItemWrapper;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.match.ItemMatch;
import dev.latvian.mods.kubejs.recipe.match.ReplacementMatchInfo;
import dev.latvian.mods.kubejs.util.OpsContainer;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.List;

public record ItemStackComponent(RecipeComponentType<?> type, Codec<ItemStack> codec, boolean allowEmpty, Ingredient filter) implements RecipeComponent<ItemStack> {
	public static final RecipeComponentType<ItemStack> ITEM_STACK = RecipeComponentType.unit(KubeJS.id("item_stack"), type -> new ItemStackComponent(type, false, Ingredient.EMPTY));
	public static final RecipeComponentType<ItemStack> OPTIONAL_ITEM_STACK = RecipeComponentType.unit(KubeJS.id("optional_item_stack"), type -> new ItemStackComponent(type, true, Ingredient.EMPTY));

	public static final RecipeComponentType<ItemStack> FILTERED_ITEM_STACK = RecipeComponentType.dynamic(KubeJS.id("filtered_item_stack"), (RecipeComponentCodecFactory<ItemStackComponent>) (type, ctx) -> RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codec.BOOL.optionalFieldOf("allow_empty", false).forGetter(ItemStackComponent::allowEmpty),
		Ingredient.CODEC.optionalFieldOf("filter", Ingredient.EMPTY).forGetter(ItemStackComponent::filter)
	).apply(instance, (allowEmpty, filter) -> new ItemStackComponent(type, allowEmpty, filter))));

	public ItemStackComponent(RecipeComponentType<?> type, boolean allowEmpty, Ingredient filter) {
		this(type, allowEmpty ? ItemStack.OPTIONAL_CODEC : ItemStack.STRICT_CODEC, allowEmpty, filter);
	}

	@Override
	public TypeInfo typeInfo() {
		return ItemWrapper.TYPE_INFO;
	}

	@Override
	public boolean hasPriority(Context cx, KubeRecipe recipe, Object from) {
		return ItemWrapper.isItemStackLike(from);
	}

	@Override
	public boolean matches(Context cx, KubeRecipe recipe, ItemStack value, ReplacementMatchInfo match) {
		return match.match() instanceof ItemMatch m && !value.isEmpty() && m.matches(cx, value, match.exact());
	}

	@Override
	public boolean isEmpty(ItemStack value) {
		return value.isEmpty();
	}

	@Override
	public void buildUniqueId(UniqueIdBuilder builder, ItemStack value) {
		if (!value.isEmpty()) {
			builder.append(value.kjs$getIdLocation());
		}
	}

	@Override
	public String toString() {
		return type.toString();
	}

	@Override
	public String toString(OpsContainer ops, ItemStack value) {
		return value.kjs$toItemString0(ops.nbt());
	}

	@Override
	public void validate(ValidationContext ctx, ItemStack value) {
		RecipeComponent.super.validate(ctx, value);

		if (!filter.isEmpty() && !filter.test(value)) {
			throw new InvalidRecipeComponentValueException("Item " + value.kjs$toItemString0(ctx.ops().nbt()) + " doesn't match filter " + filter.kjs$toIngredientString(ctx.ops().nbt()), this, value);
		}
	}

	@Override
	public List<ItemStack> spread(ItemStack value) {
		int count = value.getCount();

		if (count <= 0) {
			return List.of();
		} else if (count == 1) {
			return List.of(value);
		} else {

			var list = new ArrayList<ItemStack>(count);

			for (int i = 0; i < count; i++) {
				// technically we could get away with not copying it every time but better safe than sorry
				list.add(value.copyWithCount(1));
			}

			return list;
		}
	}
}
