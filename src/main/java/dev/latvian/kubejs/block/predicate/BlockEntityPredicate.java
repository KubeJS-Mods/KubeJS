package dev.latvian.kubejs.block.predicate;

import dev.latvian.kubejs.util.ID;
import dev.latvian.kubejs.util.nbt.NBTBaseJS;
import dev.latvian.kubejs.world.BlockContainerJS;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

/**
 * @author LatvianModder
 */
public class BlockEntityPredicate implements BlockPredicate
{
	private final ResourceLocation id;
	private BlockEntityPredicateDataCheck checkData;

	public BlockEntityPredicate(Object i)
	{
		id = ID.of(i).mc();
	}

	public BlockEntityPredicate data(BlockEntityPredicateDataCheck cd)
	{
		checkData = cd;
		return this;
	}

	@Override
	public boolean check(BlockContainerJS block)
	{
		TileEntity tileEntity = block.getEntity();
		return tileEntity != null && id.equals(TileEntity.getKey(tileEntity.getClass())) && (checkData == null || checkData.checkData(NBTBaseJS.of(tileEntity.serializeNBT()).asCompound()));
	}

	@Override
	public String toString()
	{
		return "{entity=" + id + "}";
	}
}