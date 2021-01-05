package dev.latvian.kubejs.integration.rei;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.script.ScriptType;
import me.shedaniel.rei.api.EntryRegistry;
import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.api.RecipeHelper;
import me.shedaniel.rei.api.plugins.REIPluginV0;
import me.shedaniel.rei.utils.CollectionUtils;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.function.Function;

/**
 * @author shedaniel
 */
public class REIPlugin implements REIPluginV0
{
	@Override
	public ResourceLocation getPluginIdentifier()
	{
		return new ResourceLocation(KubeJS.MOD_ID, "rei");
	}

	@Override
	public void registerEntries(EntryRegistry entryRegistry)
	{
		Function<Object, Collection<EntryStack>> itemSerializer = o -> EntryStack.ofItemStacks(CollectionUtils.map(IngredientJS.of(o).getStacks(), ItemStackJS::getItemStack));
		
		new HideREIEventJS<>(entryRegistry, EntryStack.Type.ITEM, itemSerializer).post(ScriptType.CLIENT, REIIntegration.REI_HIDE_ITEMS);
		new AddREIEventJS(entryRegistry, itemSerializer).post(ScriptType.CLIENT, REIIntegration.REI_ADD_ITEMS);
	}

	@Override
	public void registerRecipeDisplays(RecipeHelper recipeHelper)
	{
		new InformationREIEventJS().post(ScriptType.CLIENT, REIIntegration.REI_INFORMATION);
	}
}