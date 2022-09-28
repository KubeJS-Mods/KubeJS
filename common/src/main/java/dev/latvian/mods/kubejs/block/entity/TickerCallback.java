package dev.latvian.mods.kubejs.block.entity;

import dev.latvian.mods.kubejs.level.BlockContainerJS;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class TickerCallback {
	public BlockContainerJS block;
	public BlockEntity blockEntity;

	public TickerCallback(BlockContainerJS containerJS, BlockEntity blockEntity) {
		this.block = containerJS;
		this.blockEntity = blockEntity;
	}

	public Level getLevel() {
		return this.block.getLevel();
	}

	public MinecraftServer getServer() {
		return this.getLevel().getServer();
	}

	public BlockEntity getBlockEntity() {
		return this.blockEntity;
	}
}
