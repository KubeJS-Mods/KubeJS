package dev.latvian.mods.kubejs.recipe.component;

import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.kubejs.recipe.ItemMatch;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.ReplacementMatch;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

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
		return ItemStackJS.TYPE_INFO;
	}

	@Override
	public boolean hasPriority(Context cx, KubeRecipe recipe, Object from) {
		return ItemStackJS.isItemStackLike(from);
	}

	@Override
	public boolean isOutput(KubeRecipe recipe, ItemStack value, ReplacementMatch match) {
		return match instanceof ItemMatch m && !value.isEmpty() && m.contains(value);
	}

	@Override
	public String checkEmpty(RecipeKey<ItemStack> key, ItemStack value) {
		if (value.isEmpty()) {
			return "ItemStack '" + key.name + "' can't be empty!";
		}

		return "";
	}

	@Override
	@Nullable
	public String createUniqueId(ItemStack value) {
		return value == null || value.isEmpty() ? null : RecipeSchema.normalizeId(value.kjs$getId()).replace('/', '_');
	}

	@Override
	public String toString() {
		return "item_stack";
	}
}
