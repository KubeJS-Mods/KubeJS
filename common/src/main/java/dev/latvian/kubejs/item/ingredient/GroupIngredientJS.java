package dev.latvian.kubejs.item.ingredient;

import dev.latvian.kubejs.docs.MinecraftClass;
import dev.latvian.kubejs.item.ItemStackJS;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

/**
 * @author LatvianModder
 */
public class GroupIngredientJS implements IngredientJS
{
	private final CreativeModeTab group;

	public GroupIngredientJS(CreativeModeTab m)
	{
		group = m;
	}

	@MinecraftClass
	public CreativeModeTab getGroup()
	{
		return group;
	}

	public String getGroupId()
	{
		return group.getRecipeFolderName();
	}

	@Override
	public boolean test(ItemStackJS stack)
	{
		return !stack.isEmpty() && stack.getItem().getItemCategory() == group;
	}

	@Override
	public boolean testVanilla(ItemStack stack)
	{
		return !stack.isEmpty() && stack.getItem().getItemCategory() == group;
	}

	@Override
	public String toString()
	{
		return "'%" + group.getRecipeFolderName() + "'";
	}
}
