package dev.latvian.kubejs.player;

import dev.latvian.kubejs.docs.ID;
import dev.latvian.kubejs.util.UtilsJS;
import net.minecraft.stats.StatisticsManager;

/**
 * @author LatvianModder
 */
public class PlayerStatsJS
{
	private final PlayerJS<?> player;
	private final StatisticsManager statFile;

	public PlayerStatsJS(PlayerJS<?> p, StatisticsManager s)
	{
		player = p;
		statFile = s;
	}

	public PlayerJS<?> getPlayer()
	{
		return player;
	}

	public int get(@ID String id)
	{
		return statFile.getValue(UtilsJS.getStat(id));
	}

	public void set(@ID String id, int value)
	{
		statFile.setValue(player.minecraftPlayer, UtilsJS.getStat(id), value);
	}

	public void add(@ID String id, int value)
	{
		statFile.increment(player.minecraftPlayer, UtilsJS.getStat(id), value);
	}
}