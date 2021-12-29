package dev.latvian.mods.kubejs.block;

import dev.latvian.mods.kubejs.server.ServerJS;
import dev.latvian.mods.kubejs.world.BlockContainerJS;
import dev.latvian.mods.kubejs.world.WorldJS;
import org.jetbrains.annotations.ApiStatus;

import java.util.Random;

public class RandomTickCallbackJS {
	public BlockContainerJS block;
	public Random random;

	public RandomTickCallbackJS(BlockContainerJS containerJS, Random random) {
		this.block = containerJS;
		this.random = random;
	}

	public WorldJS getLevel() {
		return this.block.getLevel();
	}

	@Deprecated(forRemoval = true)
	@ApiStatus.ScheduledForRemoval(inVersion = "4.1")
	public WorldJS getWorld() {
		return getLevel();
	}

	public ServerJS getServer() {
		return this.getLevel().getServer();
	}
}
