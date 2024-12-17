package dev.latvian.mods.kubejs.recipe.component;

import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.bindings.ItemWrapper;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.match.ItemMatch;
import dev.latvian.mods.kubejs.recipe.match.ReplacementMatchInfo;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.world.item.ItemStack;

public class ItemStackComponent implements RecipeComponent<ItemStack> {
	public static final ItemStackComponent ITEM_STACK = new ItemStackComponent("item_stack", ItemStack.OPTIONAL_CODEC);
	public static final ItemStackComponent STRICT_ITEM_STACK = new ItemStackComponent("strict_item_stack", ItemStack.STRICT_CODEC);

	public final String name;
	public final Codec<ItemStack> codec;

	public ItemStackComponent(String name, Codec<ItemStack> codec) {
		this.name = name;
		this.codec = codec;
	}

	@Override
	public Codec<ItemStack> codec() {
		return ItemStack.OPTIONAL_CODEC;
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
		return name;
	}
}
