package dev.latvian.mods.kubejs.level;

import net.minecraft.world.level.Level;

public class SimpleLevelEventJS extends LevelEventJS {
	private final Level level;

	public SimpleLevelEventJS(Level l) {
		level = l;
	}

	@Override
	public Level getLevel() {
		return level;
	}
}