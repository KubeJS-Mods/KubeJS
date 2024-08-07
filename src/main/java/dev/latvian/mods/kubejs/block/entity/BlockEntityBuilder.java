package dev.latvian.mods.kubejs.block.entity;

import dev.latvian.mods.kubejs.registry.BuilderBase;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class BlockEntityBuilder extends BuilderBase<BlockEntityType<?>> {
	public BlockEntityInfo info;

	public BlockEntityBuilder(ResourceLocation i, BlockEntityInfo info) {
		super(i);
		this.info = info;
	}

	@Override
	public BlockEntityType<?> createObject() {
		info.entityType = BlockEntityType.Builder.of(info::createBlockEntity, info.blockBuilder.get()).build(null);
		return info.entityType;
	}
}
