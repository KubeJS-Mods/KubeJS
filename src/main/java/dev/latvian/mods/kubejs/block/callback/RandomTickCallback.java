package dev.latvian.mods.kubejs.block.callback;

import dev.latvian.mods.kubejs.level.LevelBlock;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;

public class RandomTickCallback {
	public final LevelBlock block;
	public final RandomSource random;

	public RandomTickCallback(LevelBlock block, RandomSource random) {
		this.block = block;
		this.random = random;
	}

	public Level getLevel() {
		return this.block.getLevel();
	}

	public MinecraftServer getServer() {
		return this.getLevel().getServer();
	}
}
