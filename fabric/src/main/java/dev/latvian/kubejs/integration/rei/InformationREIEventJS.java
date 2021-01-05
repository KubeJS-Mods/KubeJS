package dev.latvian.kubejs.integration.rei;

import dev.latvian.kubejs.event.EventJS;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.text.Text;
import dev.latvian.kubejs.util.ListJS;
import me.shedaniel.rei.api.BuiltinPlugin;
import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.utils.CollectionUtils;

/**
 * @author shedaniel
 */
public class InformationREIEventJS extends EventJS
{
	/**
	 * Registers an information display.
	 *
	 * @param stacks      The target stacks
	 * @param title       The title of the information display
	 * @param description The information to be provided
	 */
	public void add(Object stacks, Object title, Object description)
	{
		BuiltinPlugin.getInstance().registerInformation(
				EntryStack.ofItemStacks(CollectionUtils.map(IngredientJS.of(stacks).getStacks(), ItemStackJS::getItemStack)),
				Text.of(title).component(),
				components -> {
					for (Object o : ListJS.orSelf(description))
					{
						components.add(Text.of(o).component());
					}
					return components;
				}
		);
	}
}