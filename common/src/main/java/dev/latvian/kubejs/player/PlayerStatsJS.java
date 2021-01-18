package dev.latvian.kubejs.player;

import dev.latvian.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.util.wrap.Wrap;
import net.minecraft.stats.StatsCounter;

/**
 * @author LatvianModder
 */
public class PlayerStatsJS
{
	private final PlayerJS<?> player;
	private final StatsCounter statFile;

	public PlayerStatsJS(PlayerJS<?> p, StatsCounter s)
	{
		player = p;
		statFile = s;
	}

	public PlayerJS<?> getPlayer()
	{
		return player;
	}

	public int get(@Wrap("id") String id)
	{
		return statFile.getValue(UtilsJS.getStat(id));
	}

	public void set(@Wrap("id") String id, int value)
	{
		statFile.setValue(player.minecraftPlayer, UtilsJS.getStat(id), value);
	}

	public void add(@Wrap("id") String id, int value)
	{
		statFile.increment(player.minecraftPlayer, UtilsJS.getStat(id), value);
	}
}