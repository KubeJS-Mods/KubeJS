package dev.latvian.mods.kubejs.script;

import dev.latvian.mods.kubejs.level.world.LevelJS;
import dev.latvian.mods.kubejs.player.PlayerDataJS;
import dev.latvian.mods.kubejs.player.PlayerJS;
import dev.latvian.mods.kubejs.server.ServerJS;

/**
 * @author LatvianModder
 */
public final class DataType<T> {
	public static DataType<ServerJS> SERVER = new DataType<>("server", ServerJS.class);
	public static DataType<LevelJS> WORLD = new DataType<>("world", LevelJS.class);
	public static DataType<PlayerDataJS> PLAYER = new DataType<>("player", PlayerDataJS.class, PlayerJS.class);

	public final String name;
	public final Class<T> parent;
	public final Class actualParent;

	public DataType(String s, Class<T> c, Class a) {
		name = s;
		parent = c;
		actualParent = a;
	}

	public DataType(String s, Class<T> c) {
		this(s, c, c);
	}
}