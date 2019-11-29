package dev.latvian.kubejs.recipe;

import dev.latvian.kubejs.recipe.type.RecipeJS;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.Event;

import java.util.List;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class RegisterRecipeHandlersEvent extends Event
{
	private final List<RecipeJS> recipes;
	private final Map<ResourceLocation, RecipeFunction> map;

	public RegisterRecipeHandlersEvent(List<RecipeJS> r, Map<ResourceLocation, RecipeFunction> m)
	{
		recipes = r;
		map = m;
	}

	public void register(RecipeTypeJS type)
	{
		map.put(type.id, new RecipeFunction(type, recipes));
	}
}