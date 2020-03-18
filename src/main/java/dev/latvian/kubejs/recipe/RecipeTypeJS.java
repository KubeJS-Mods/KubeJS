package dev.latvian.kubejs.recipe;

import dev.latvian.kubejs.util.UtilsJS;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * @author LatvianModder
 */
public class RecipeTypeJS
{
	public final IRecipeSerializer serializer;
	public final Supplier<RecipeJS> factory;
	private final String string;

	public RecipeTypeJS(IRecipeSerializer s, Supplier<RecipeJS> f)
	{
		serializer = s;
		factory = f;
		string = s.getRegistryName().toString();
	}

	public RecipeTypeJS(Object id, Supplier<RecipeJS> f)
	{
		this(Objects.requireNonNull(ForgeRegistries.RECIPE_SERIALIZERS.getValue(UtilsJS.getID(id))), f);
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
}