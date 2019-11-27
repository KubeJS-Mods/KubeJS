package dev.latvian.kubejs.player;

import dev.latvian.kubejs.util.UtilsJS;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatisticsManager;
import net.minecraft.util.ResourceLocation;

/**
 * @author LatvianModder
 */
public class PlayerStatsJS
{
	private final PlayerJS player;
	private final StatisticsManager statFile;

	public PlayerStatsJS(PlayerJS p, StatisticsManager s)
	{
		player = p;
		statFile = s;
	}

	public PlayerJS getPlayer()
	{
		return player;
	}

	public int get(Object id)
	{
		Stat<ResourceLocation> stat = UtilsJS.getStat(id);
		return stat == null ? 0 : statFile.getValue(stat);
	}

	public void set(Object id, int value)
	{
		Stat<ResourceLocation> stat = UtilsJS.getStat(id);

		if (stat != null)
		{
			statFile.setValue(player.minecraftPlayer, stat, value);
		}
	}

	public void add(Object id, int value)
	{
		Stat<ResourceLocation> stat = UtilsJS.getStat(id);

		if (stat != null)
		{
			statFile.increment(player.minecraftPlayer, stat, value);
		}
	}
}