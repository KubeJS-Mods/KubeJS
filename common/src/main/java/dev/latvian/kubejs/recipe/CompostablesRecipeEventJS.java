package dev.latvian.kubejs.recipe;

import dev.latvian.kubejs.event.EventJS;
import dev.latvian.kubejs.item.ItemStackJS;
import net.minecraft.world.level.block.ComposterBlock;

/**
 * @author LatvianModder
 */
public class CompostablesRecipeEventJS extends EventJS
{
	public void remove(Object o)
	{
		ComposterBlock.COMPOSTABLES.removeFloat(ItemStackJS.of(o).getItem());
	}

	public void removeAll()
	{
		ComposterBlock.COMPOSTABLES.clear();
	}

	public void add(Object o, float f)
	{
		ComposterBlock.COMPOSTABLES.put(ItemStackJS.of(o).getItem(), Math.max(0F, Math.min(1F, f)));
	}
}