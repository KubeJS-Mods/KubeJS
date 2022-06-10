package dev.latvian.mods.kubejs.block;

import dev.latvian.mods.kubejs.level.BlockContainerJS;
import dev.latvian.mods.kubejs.level.LevelJS;
import dev.latvian.mods.kubejs.server.ServerJS;
import net.minecraft.util.RandomSource;

import java.util.Random;

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

	public ServerJS getServer() {
		return this.getLevel().getServer();
	}
}
