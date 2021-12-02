package dev.latvian.mods.kubejs.block;

import dev.latvian.mods.kubejs.server.ServerJS;
import dev.latvian.mods.kubejs.world.BlockContainerJS;
import dev.latvian.mods.kubejs.world.WorldJS;

import java.util.Random;

public class RandomTickCallbackJS {
	public BlockContainerJS block;
	public Random random;

	public RandomTickCallbackJS(BlockContainerJS containerJS, Random random) {
		this.block = containerJS;
		this.random = random;
	}

	public WorldJS getWorld() {
		return this.block.getWorld();
	}

	public ServerJS getServer() {
		return this.getWorld().getServer();
	}
}
