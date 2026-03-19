package dev.latvian.mods.kubejs.block.predicate;

import dev.latvian.mods.kubejs.level.LevelBlock;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

public class BlockEntityPredicate implements BlockPredicate {
	private final Identifier id;
	private @Nullable BlockEntityPredicateDataCheck checkData;

	public BlockEntityPredicate(Identifier i) {
		id = i;
	}

	public BlockEntityPredicate data(BlockEntityPredicateDataCheck cd) {
		checkData = cd;
		return this;
	}

	@Override
	public boolean check(LevelBlock block) {
		var tileEntity = block.getEntity();
		return tileEntity != null && id.equals(BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(tileEntity.getType())) && (checkData == null || checkData.checkData(block.getEntityData()));
	}

	@Override
	public String toString() {
		return "{entity=" + id + "}";
	}
}