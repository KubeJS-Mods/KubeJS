package dev.latvian.kubejs.recipe;

import dev.latvian.kubejs.recipe.type.CookingRecipeJS;
import dev.latvian.kubejs.recipe.type.ShapedRecipeJS;
import dev.latvian.kubejs.recipe.type.ShapelessRecipeJS;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.crafting.ConditionalRecipe;

/**
 * @author LatvianModder
 */
public class KubeJSRecipeEventHandler
{
	public void init()
	{
		MinecraftForge.EVENT_BUS.addListener(this::registerRecipeHandlers);
	}

	private void registerRecipeHandlers(RegisterRecipeHandlersEvent event)
	{
		event.register(new RecipeTypeJS(ConditionalRecipe.SERIALZIER, ConditionalRecipeJS::new));
		event.register(new RecipeTypeJS(IRecipeSerializer.CRAFTING_SHAPED, ShapedRecipeJS::new));
		event.register(new RecipeTypeJS(IRecipeSerializer.CRAFTING_SHAPELESS, ShapelessRecipeJS::new));
		//event.register(new RecipeTypeJS(IRecipeSerializer.STONECUTTING, StonecuttingRecipeJS::new));

		for (CookingRecipeJS.Type type : CookingRecipeJS.Type.values())
		{
			event.register(new RecipeTypeJS(type.serializer, () -> new CookingRecipeJS(type)));
		}
	}
}