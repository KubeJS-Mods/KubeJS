package dev.latvian.kubejs.item.ingredient;

import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.recipe.RecipeExceptionJS;
import dev.latvian.kubejs.recipe.RecipeJS;
import dev.latvian.kubejs.util.UtilsJS;
import dev.architectury.registry.registries.Registries;
import net.minecraft.core.Registry;
import net.minecraft.world.item.ItemStack;

import java.util.regex.Pattern;

/**
 * @author LatvianModder
 */
public class RegexIngredientJS implements IngredientJS {
	private final Pattern pattern;

	public RegexIngredientJS(Pattern p) {
		pattern = p;

		if (RecipeJS.itemErrors && getFirst().isEmpty()) {
			throw new RecipeExceptionJS("Regex '" + pattern + "' doesn't match any items!").error();
		}
	}

	public Pattern getPattern() {
		return pattern;
	}

	@Override
	public boolean test(ItemStackJS stack) {
		return !stack.isEmpty() && pattern.matcher(stack.getId()).find();
	}

	@Override
	public boolean testVanilla(ItemStack stack) {
		return !stack.isEmpty() && pattern.matcher(Registries.getId(stack.getItem(), Registry.ITEM_REGISTRY).toString()).find();
	}

	@Override
	public String toString() {
		return UtilsJS.toRegexString(pattern);
	}
}