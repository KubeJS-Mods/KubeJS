package dev.latvian.mods.kubejs.item.ingredient;

import dev.latvian.mods.kubejs.KubeJSRegistries;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.kubejs.item.ItemStackSet;
import dev.latvian.mods.kubejs.recipe.RecipeExceptionJS;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * @author LatvianModder
 */
public class ModIngredientJS implements IngredientJS {
	private final String mod;

	public ModIngredientJS(String m) {
		mod = m;

		if (RecipeJS.itemErrors && getFirst().isEmpty()) {
			throw new RecipeExceptionJS("Mod '" + mod + "' doesn't have any items!").error();
		}
	}

	public String getMod() {
		return mod;
	}

	@Override
	public boolean test(ItemStack stack) {
		return !stack.isEmpty() && mod.equals(KubeJSRegistries.items().getId(stack.getItem()).getNamespace());
	}

	@Override
	public boolean testItem(Item item) {
		return item != Items.AIR && mod.equals(KubeJSRegistries.items().getId(item).getNamespace());
	}

	@Override
	public void gatherStacks(ItemStackSet set) {
		for (var stack : ItemStackJS.getList()) {
			if (mod.equals(stack.kjs$getMod())) {
				set.add(stack);
			}
		}
	}

	@Override
	public ItemStack getFirst() {
		for (var stack : ItemStackJS.getList()) {
			if (mod.equals(stack.kjs$getMod())) {
				return stack.copy();
			}
		}

		return ItemStack.EMPTY;
	}

	@Override
	public String toString() {
		return "'@" + mod + "'";
	}
}