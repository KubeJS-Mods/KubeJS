package dev.latvian.mods.kubejs.level;

import net.minecraft.world.level.Level;

public class SimpleLevelKubeEvent implements KubeLevelEvent {
	private final Level level;

	public SimpleLevelKubeEvent(Level l) {
		level = l;
	}

	@Override
	public Level getLevel() {
		return level;
	}
}