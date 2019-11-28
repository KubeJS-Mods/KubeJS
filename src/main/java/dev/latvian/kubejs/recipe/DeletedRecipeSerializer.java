package dev.latvian.kubejs.recipe;

import com.google.gson.JsonObject;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public class DeletedRecipeSerializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<DeletedRecipe>
{
	public static DeletedRecipeSerializer instance;

	@Override
	public DeletedRecipe read(ResourceLocation id, JsonObject json)
	{
		return new DeletedRecipe(id);
	}

	@Nullable
	@Override
	public DeletedRecipe read(ResourceLocation id, PacketBuffer buffer)
	{
		return new DeletedRecipe(id);
	}

	@Override
	public void write(PacketBuffer buffer, DeletedRecipe recipe)
	{
	}
}