package dev.latvian.mods.kubejs.recipe.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.kubejs.error.InvalidRecipeComponentValueException;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.ItemWrapper;
import dev.latvian.mods.kubejs.recipe.filter.RecipeMatchContext;
import dev.latvian.mods.kubejs.recipe.match.ItemMatch;
import dev.latvian.mods.kubejs.recipe.match.ReplacementMatchInfo;
import dev.latvian.mods.kubejs.util.OpsContainer;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record ItemStackComponent(ResourceKey<RecipeComponentType<?>> type, Codec<ItemStack> codec, boolean allowEmpty, Optional<Ingredient> filter) implements RecipeComponent<ItemStack> {
	public static final ItemStackComponent ITEM_STACK = new ItemStackComponent(
		RecipeComponentType.builtin("item_stack"),
		false, Optional.empty()
	);
	public static final ItemStackComponent OPTIONAL_ITEM_STACK = new ItemStackComponent(
		RecipeComponentType.builtin("optional_item_stack"),
		true, Optional.empty()
	);

	public static final ResourceKey<RecipeComponentType<?>> FILTERED_TYPE = RecipeComponentType.builtin("filtered_item_stack");
	public static final MapCodec<ItemStackComponent> FILTERED_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codec.BOOL.optionalFieldOf("allow_empty", false).forGetter(ItemStackComponent::allowEmpty),
		Ingredient.CODEC.optionalFieldOf("filter").forGetter(ItemStackComponent::filter)
	).apply(instance, (allowEmpty, filter) -> new ItemStackComponent(FILTERED_TYPE, allowEmpty, filter)));

	public ItemStackComponent(ResourceKey<RecipeComponentType<?>> type, boolean allowEmpty, Optional<Ingredient> filter) {
		this(type, allowEmpty ? ItemStack.OPTIONAL_CODEC : ItemStack.CODEC.flatXmap(
			ItemStack::validateStrict,
			ItemStack::validateStrict
		), allowEmpty, filter);
	}

	@Override
	public TypeInfo typeInfo() {
		return ItemWrapper.TYPE_INFO;
	}

	@Override
	public boolean hasPriority(RecipeMatchContext cx, @Nullable Object from) {
		return ItemWrapper.isItemStackLike(from);
	}

	@Override
	public boolean matches(RecipeMatchContext cx, ItemStack value, ReplacementMatchInfo match) {
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
	public void validate(RecipeValidationContext ctx, ItemStack value) {
		RecipeComponent.super.validate(ctx, value);

		filter.ifPresent(ingredient -> {
			if (!ingredient.test(value)) {
				throw new InvalidRecipeComponentValueException("Item " + value.kjs$toItemString0(ctx.ops().nbt()) + " doesn't match filter " + ingredient.kjs$toIngredientString(ctx.ops().nbt()), this, value);
			}
		});
	}

	@Override
	public List<ItemStack> spread(ItemStack value) {
		int count = value.getCount();

		if (count <= 0) {
			return List.of();
		} else if (count == 1) {
			return List.of(value.copyWithCount(1));
		} else {
			var list = new ArrayList<ItemStack>(count);

			for (int i = 0; i < count; i++) {
				list.add(value.copyWithCount(1));
			}

			return list;
		}
	}
}
