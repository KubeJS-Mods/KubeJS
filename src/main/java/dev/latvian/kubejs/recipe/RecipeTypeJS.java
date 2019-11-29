package dev.latvian.kubejs.recipe;

import com.google.gson.JsonObject;
import dev.latvian.kubejs.recipe.type.RecipeJS;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.Objects;

/**
 * @author LatvianModder
 */
public abstract class RecipeTypeJS
{
	public final ResourceLocation id;
	public final IRecipeSerializer serializer;

	public RecipeTypeJS(ResourceLocation i)
	{
		id = i;
		serializer = ForgeRegistries.RECIPE_SERIALIZERS.getValue(i);
	}

	public RecipeTypeJS(IRecipeSerializer s)
	{
		id = Objects.requireNonNull(s.getRegistryName());
		serializer = s;
	}

	@Nullable
	public abstract RecipeJS create(Object[] args);

	@Nullable
	public abstract RecipeJS create(JsonObject json);
}