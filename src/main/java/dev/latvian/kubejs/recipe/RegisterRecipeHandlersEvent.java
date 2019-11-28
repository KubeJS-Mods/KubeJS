package dev.latvian.kubejs.recipe;

import dev.latvian.kubejs.recipe.type.RecipeJS;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraftforge.eventbus.api.Event;

import java.util.List;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class RegisterRecipeHandlersEvent extends Event
{
	private final List<RecipeJS> recipes;
	private final Map<String, RecipeFunction> map;
	private final Map<IRecipeSerializer, RecipeDeserializerJS> deserializerMap;

	public RegisterRecipeHandlersEvent(List<RecipeJS> r, Map<String, RecipeFunction> m, Map<IRecipeSerializer, RecipeDeserializerJS> dm)
	{
		recipes = r;
		map = m;
		deserializerMap = dm;
	}

	public void registerProvider(String id, RecipeProviderJS handler)
	{
		map.put(id, new RecipeFunction(recipes, handler));
	}

	public void registerDeserializer(IRecipeSerializer type, RecipeDeserializerJS deserializer)
	{
		deserializerMap.put(type, deserializer);
	}
}