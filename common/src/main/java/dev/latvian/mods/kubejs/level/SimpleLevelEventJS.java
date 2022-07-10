package dev.latvian.mods.kubejs.level;

import dev.latvian.mods.kubejs.event.EventHandler;

/**
 * @author LatvianModder
 */
public class SimpleLevelEventJS extends LevelEventJS {
	public static final EventHandler LOAD_EVENT = EventHandler.server(SimpleLevelEventJS.class).legacy("level.load");
	public static final EventHandler UNLOAD_EVENT = EventHandler.server(SimpleLevelEventJS.class).legacy("level.unload");
	public static final EventHandler TICK_EVENT = EventHandler.server(SimpleLevelEventJS.class).legacy("level.tick");

	private final LevelJS level;

	public SimpleLevelEventJS(LevelJS l) {
		level = l;
	}

	@Override
	public LevelJS getLevel() {
		return level;
	}
}