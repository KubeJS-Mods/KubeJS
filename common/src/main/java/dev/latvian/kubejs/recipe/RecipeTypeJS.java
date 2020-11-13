package dev.latvian.kubejs.recipe;

import me.shedaniel.architectury.registry.Registries;
import net.minecraft.core.Registry;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.function.Supplier;

/**
 * @author LatvianModder
 */
public class RecipeTypeJS
{
	public final RecipeSerializer<?> serializer;
	public final Supplier<RecipeJS> factory;
	private final String string;

	public RecipeTypeJS(RecipeSerializer<?> s, Supplier<RecipeJS> f)
	{
		serializer = s;
		factory = f;
		string = Registries.getId(s, Registry.RECIPE_SERIALIZER_REGISTRY).toString();
	}

	public boolean isCustom()
	{
		return false;
	}

	@Override
	public String toString()
	{
		return string;
	}

	@Override
	public int hashCode()
	{
		return string.hashCode();
	}

	@Override
	public boolean equals(Object obj)
	{
		return string.equals(obj.toString());
	}
}