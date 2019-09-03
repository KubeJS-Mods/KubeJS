package dev.latvian.kubejs.player;

import dev.latvian.kubejs.documentation.DocField;
import dev.latvian.kubejs.entity.LivingEntityJS;
import dev.latvian.kubejs.text.Text;
import dev.latvian.kubejs.util.UtilsJS;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.stats.StatBase;

import java.util.Map;

/**
 * @author LatvianModder
 */
public class PlayerJS extends LivingEntityJS
{
	public final transient EntityPlayerMP player;

	@DocField("Temporary data, mods can attach objects to this")
	public final Map<String, Object> data;

	@DocField
	public final PlayerInventoryJS inventory;

	public PlayerJS(PlayerDataJS d, EntityPlayerMP p)
	{
		super(d.server, p);
		data = d.data;
		player = p;
		inventory = new PlayerInventoryJS(this);
	}

	@Override
	public boolean isPlayer()
	{
		return true;
	}

	@Override
	public void setPositionAndRotation(double x, double y, double z, float yaw, float pitch)
	{
		super.setPositionAndRotation(x, y, z, yaw, pitch);
		player.connection.setPlayerLocation(x, y, z, yaw, pitch);
	}

	@Override
	public void statusMessage(Object message)
	{
		player.sendStatusMessage(Text.of(message).component(), true);
	}

	public boolean isCreativeMode()
	{
		return player.capabilities.isCreativeMode;
	}

	public boolean isSpectator()
	{
		return player.isSpectator();
	}

	public int getStat(Object id)
	{
		StatBase stat = UtilsJS.stat(id);
		return stat == null ? 0 : player.getStatFile().readStat(stat);
	}

	public void setStat(Object id, int value)
	{
		StatBase stat = UtilsJS.stat(id);

		if (stat != null)
		{
			player.getStatFile().unlockAchievement(player, stat, value);
		}
	}

	public void addStat(Object id, int value)
	{
		StatBase stat = UtilsJS.stat(id);

		if (stat != null)
		{
			player.getStatFile().increaseStat(player, stat, value);
		}
	}
}