package dev.latvian.mods.kubejs.recipe.component;

import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.ItemWrapper;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.match.ItemMatch;
import dev.latvian.mods.kubejs.recipe.match.ReplacementMatchInfo;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public record ItemStackComponent(RecipeComponentType<?> type, Codec<ItemStack> codec, boolean allowEmpty) implements RecipeComponent<ItemStack> {
	public static final RecipeComponentType<ItemStack> ITEM_STACK = RecipeComponentType.unit(KubeJS.id("item_stack"), type -> new ItemStackComponent(type, ItemStack.STRICT_CODEC, false));
	public static final RecipeComponentType<ItemStack> OPTIONAL_ITEM_STACK = RecipeComponentType.unit(KubeJS.id("optional_item_stack"), type -> new ItemStackComponent(type, ItemStack.OPTIONAL_CODEC, true));

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
	public String toString(ItemStack value) {
		return value.kjs$toItemString0(null);
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
