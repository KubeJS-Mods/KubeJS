package dev.latvian.mods.kubejs.world;

/**
 * @author LatvianModder
 */
public class SimpleWorldEventJS extends WorldEventJS {
	private final WorldJS level;

	public SimpleWorldEventJS(WorldJS l) {
		level = l;
	}

	@Override
	public WorldJS getWorld() {
		return level;
	}
}