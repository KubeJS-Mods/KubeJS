package dev.latvian.kubejs.recipe;

import dev.latvian.kubejs.documentation.Ignore;
import dev.latvian.kubejs.documentation.P;
import dev.latvian.kubejs.documentation.T;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.script.data.VirtualKubeJSDataPack;
import net.minecraft.item.ItemStack;

/**
 * @author LatvianModder
 */
public interface IRecipeCollection
{
	void remove();

	boolean hasInput(@P("ingredient") @T(IngredientJS.class) Object ingredient);

	boolean hasOutput(@P("ingredient") @T(IngredientJS.class) Object ingredient);

	boolean replaceInput(@P("ingredient") @T(IngredientJS.class) Object ingredient, @P("with") @T(IngredientJS.class) Object with);

	boolean replaceOutput(@P("ingredient") @T(IngredientJS.class) Object ingredient, @P("with") @T(ItemStack.class) Object with);

	void setGroup(String group);

	@Ignore
	void addToDataPack(VirtualKubeJSDataPack pack);
}