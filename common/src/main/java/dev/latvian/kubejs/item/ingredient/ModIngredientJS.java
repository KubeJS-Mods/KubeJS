package dev.latvian.kubejs.item.ingredient;

import dev.latvian.kubejs.KubeJSRegistries;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.recipe.RecipeExceptionJS;
import dev.latvian.kubejs.recipe.RecipeJS;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.LinkedHashSet;
import java.util.Set;

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
	public boolean test(ItemStackJS stack) {
		return !stack.isEmpty() && mod.equals(stack.getMod());
	}

	@Override
	public boolean testVanilla(ItemStack stack) {
		return !stack.isEmpty() && mod.equals(KubeJSRegistries.items().getId(stack.getItem()).getNamespace());
	}

	@Override
	public boolean testVanillaItem(Item item) {
		return item != Items.AIR && mod.equals(KubeJSRegistries.items().getId(item).getNamespace());
	}

	@Override
	public Set<ItemStackJS> getStacks() {
		Set<ItemStackJS> set = new LinkedHashSet<>();

		for (ItemStackJS stack : ItemStackJS.getList()) {
			if (mod.equals(stack.getMod())) {
				set.add(stack);
			}
		}

		return set;
	}

	@Override
	public ItemStackJS getFirst() {
		for (ItemStackJS stack : ItemStackJS.getList()) {
			if (mod.equals(stack.getMod())) {
				return stack.copy();
			}
		}

		return ItemStackJS.EMPTY;
	}

	@Override
	public String toString() {
		return "'@" + mod + "'";
	}
}