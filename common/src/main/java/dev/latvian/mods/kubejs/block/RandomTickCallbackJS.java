package dev.latvian.mods.kubejs.block;

import dev.latvian.mods.kubejs.level.BlockContainerJS;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;

public class RandomTickCallbackJS {
	public BlockContainerJS block;
	public RandomSource random;

	public RandomTickCallbackJS(BlockContainerJS containerJS, RandomSource random) {
		this.block = containerJS;
		this.random = random;
	}

	public Level getLevel() {
		return this.block.getLevel();
	}

	public MinecraftServer getServer() {
		return this.getLevel().getServer();
	}
}
