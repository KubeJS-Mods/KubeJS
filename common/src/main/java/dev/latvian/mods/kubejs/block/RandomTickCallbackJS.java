package dev.latvian.mods.kubejs.block;

import dev.latvian.mods.kubejs.level.BlockContainerJS;
import dev.latvian.mods.kubejs.level.LevelJS;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RandomSource;

public class RandomTickCallbackJS {
	public BlockContainerJS block;
	public RandomSource random;

	public RandomTickCallbackJS(BlockContainerJS containerJS, RandomSource random) {
		this.block = containerJS;
		this.random = random;
	}

	public LevelJS getLevel() {
		return this.block.getLevel();
	}

	public MinecraftServer getServer() {
		return this.getLevel().getServer();
	}
}
