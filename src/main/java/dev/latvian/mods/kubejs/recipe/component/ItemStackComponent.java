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
		return allowEmpty ? "optional_item_stack" : "item_stack";
	}
}
