package dev.latvian.kubejs.script;

import dev.latvian.kubejs.player.PlayerDataJS;
import dev.latvian.kubejs.player.PlayerJS;
import dev.latvian.kubejs.server.ServerJS;
import dev.latvian.kubejs.world.WorldJS;

/**
 * @author LatvianModder
 */
public final class DataType<T>
{
	public static DataType<ServerJS> SERVER = new DataType<>("server", ServerJS.class);
	public static DataType<WorldJS> WORLD = new DataType<>("world", WorldJS.class);
	public static DataType<PlayerDataJS> PLAYER = new DataType<>("player", PlayerDataJS.class, PlayerJS.class);

	public final String name;
	public final Class<T> parent;
	public final Class actualParent;

	public DataType(String s, Class<T> c, Class a)
	{
		name = s;
		parent = c;
		actualParent = a;
	}

	public DataType(String s, Class<T> c)
	{
		this(s, c, c);
	}
}