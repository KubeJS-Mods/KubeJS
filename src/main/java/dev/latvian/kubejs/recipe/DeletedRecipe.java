package dev.latvian.kubejs.recipe;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

/**
 * @author LatvianModder
 */
public class DeletedRecipe implements IRecipe<IInventory>
{
	public static final IRecipeType<DeletedRecipe> TYPE = IRecipeType.register("kubejs_deleted");

	private final ResourceLocation id;

	public DeletedRecipe(ResourceLocation _id)
	{
		id = _id;
	}

	@Override
	public boolean matches(IInventory inv, World world)
	{
		return false;
	}

	@Override
	public ItemStack getCraftingResult(IInventory inv)
	{
		return ItemStack.EMPTY;
	}

	@Override
	public boolean canFit(int width, int height)
	{
		return false;
	}

	@Override
	public ItemStack getRecipeOutput()
	{
		return ItemStack.EMPTY;
	}

	@Override
	public ResourceLocation getId()
	{
		return id;
	}

	@Override
	public IRecipeSerializer<?> getSerializer()
	{
		return DeletedRecipeSerializer.instance;
	}

	@Override
	public IRecipeType<?> getType()
	{
		return TYPE;
	}
}
