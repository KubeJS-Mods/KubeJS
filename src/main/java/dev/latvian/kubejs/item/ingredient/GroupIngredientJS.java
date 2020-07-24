package dev.latvian.kubejs.item.ingredient;

import dev.latvian.kubejs.docs.MinecraftClass;
import dev.latvian.kubejs.item.ItemStackJS;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

/**
 * @author LatvianModder
 */
public class GroupIngredientJS implements IngredientJS
{
	private final ItemGroup group;

	public GroupIngredientJS(ItemGroup m)
	{
		group = m;
	}

	@MinecraftClass
	public ItemGroup getGroup()
	{
		return group;
	}

	public String getGroupId()
	{
		return group.getPath();
	}

	@Override
	public boolean test(ItemStackJS stack)
	{
		return !stack.isEmpty() && stack.getItem().getGroup() == group;
	}

	@Override
	public boolean testVanilla(ItemStack stack)
	{
		return !stack.isEmpty() && stack.getItem().getGroup() == group;
	}

	@Override
	public String toString()
	{
		return "%" + group.getPath();
	}
}
