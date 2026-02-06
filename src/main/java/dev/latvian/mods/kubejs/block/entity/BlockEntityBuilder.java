package dev.latvian.mods.kubejs.block.entity;

import dev.latvian.mods.kubejs.registry.BuilderBase;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class BlockEntityBuilder extends BuilderBase<BlockEntityType<?>> {
	public BlockEntityInfo info;

	public BlockEntityBuilder(Identifier i, BlockEntityInfo info) {
		super(i);
		this.info = info;
	}

	@Override
	public BlockEntityType<?> createObject() {
		info.entityType = new BlockEntityType<>(info::createBlockEntity, info.blockBuilder.get());
		return info.entityType;
	}
}
