package dev.latvian.mods.kubejs.block.predicate;

import dev.latvian.mods.kubejs.level.BlockContainerJS;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

public class BlockEntityPredicate implements BlockPredicate {
	private final ResourceLocation id;
	private BlockEntityPredicateDataCheck checkData;

	public BlockEntityPredicate(ResourceLocation i) {
		id = i;
	}

	public BlockEntityPredicate data(BlockEntityPredicateDataCheck cd) {
		checkData = cd;
		return this;
	}

	@Override
	public boolean check(BlockContainerJS block) {
		var tileEntity = block.getEntity();
		return tileEntity != null && id.equals(BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(tileEntity.getType())) && (checkData == null || checkData.checkData(block.getEntityData()));
	}

	@Override
	public String toString() {
		return "{entity=" + id + "}";
	}
}