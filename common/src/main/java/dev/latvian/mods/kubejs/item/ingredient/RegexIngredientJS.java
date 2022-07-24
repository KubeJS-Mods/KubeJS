package dev.latvian.mods.kubejs.item.ingredient;

import dev.architectury.registry.registries.Registries;
import dev.latvian.mods.kubejs.recipe.RecipeExceptionJS;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.util.UtilsJS;
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
	public boolean test(ItemStack stack) {
		return !stack.isEmpty() && pattern.matcher(Registries.getId(stack.getItem(), Registry.ITEM_REGISTRY).toString()).find();
	}

	@Override
	public String toString() {
		return UtilsJS.toRegexString(pattern);
	}
}