package dev.latvian.mods.kubejs.block;

import dev.latvian.mods.kubejs.world.BlockContainerJS;

import java.util.Random;

public class RandomTickCallbackJS {
	public BlockContainerJS block;
	public Random random;

	public RandomTickCallbackJS(BlockContainerJS containerJS, Random random) {
		this.block = containerJS;
		this.random = random;
	}
}
