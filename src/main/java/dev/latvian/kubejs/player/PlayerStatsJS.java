package dev.latvian.kubejs.player;

import dev.latvian.kubejs.util.UtilsJS;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatisticsManager;

/**
 * @author LatvianModder
 */
public class PlayerStatsJS
{
	public final PlayerJS player;
	private final StatisticsManager statFile;

	public PlayerStatsJS(PlayerJS p, StatisticsManager s)
	{
		player = p;
		statFile = s;
	}

	public int get(Object id)
	{
		StatBase stat = UtilsJS.getStat(id);
		return stat == null ? 0 : statFile.readStat(stat);
	}

	public void set(Object id, int value)
	{
		StatBase stat = UtilsJS.getStat(id);

		if (stat != null)
		{
			statFile.unlockAchievement(player.entityPlayer, stat, value);
		}
	}

	public void add(Object id, int value)
	{
		StatBase stat = UtilsJS.getStat(id);

		if (stat != null)
		{
			statFile.increaseStat(player.entityPlayer, stat, value);
		}
	}
}