package dev.latvian.kubejs.block.predicate;

import dev.latvian.kubejs.docs.ID;
import dev.latvian.kubejs.util.UtilsJS;
import dev.latvian.kubejs.world.BlockContainerJS;
import me.shedaniel.architectury.registry.Registries;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * @author LatvianModder
 */
public class BlockEntityPredicate implements BlockPredicate
{
	private final ResourceLocation id;
	private BlockEntityPredicateDataCheck checkData;

	public BlockEntityPredicate(@ID String i)
	{
		id = UtilsJS.getMCID(i);
	}

	public BlockEntityPredicate data(BlockEntityPredicateDataCheck cd)
	{
		checkData = cd;
		return this;
	}

	@Override
	public boolean check(BlockContainerJS block)
	{
		BlockEntity tileEntity = block.getEntity();
		return tileEntity != null && id.equals(Registries.getId(tileEntity.getType(), Registry.BLOCK_ENTITY_TYPE_REGISTRY)) && (checkData == null || checkData.checkData(block.getEntityData()));
	}

	@Override
	public String toString()
	{
		return "{entity=" + id + "}";
	}
}