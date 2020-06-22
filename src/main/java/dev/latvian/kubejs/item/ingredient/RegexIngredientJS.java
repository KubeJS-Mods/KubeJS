package dev.latvian.kubejs.item.ingredient;

import dev.latvian.kubejs.item.ItemStackJS;
import net.minecraft.item.ItemStack;

import java.util.regex.Pattern;

/**
 * @author LatvianModder
 */
public class RegexIngredientJS implements IngredientJS
{
	private final Pattern pattern;

	public RegexIngredientJS(Pattern p)
	{
		pattern = p;
	}

	public Pattern getPattern()
	{
		return pattern;
	}

	@Override
	public boolean test(ItemStackJS stack)
	{
		return !stack.isEmpty() && pattern.matcher(stack.getId()).find();
	}

	@Override
	public boolean testVanilla(ItemStack stack)
	{
		return !stack.isEmpty() && pattern.matcher(stack.getItem().getRegistryName().toString()).find();
	}

	@Override
	public String toString()
	{
		return "regex:" + pattern;
	}
}