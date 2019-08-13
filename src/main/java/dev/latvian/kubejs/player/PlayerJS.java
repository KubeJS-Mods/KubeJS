package dev.latvian.kubejs.player;

import dev.latvian.kubejs.text.TextUtils;
import dev.latvian.kubejs.util.ServerJS;
import dev.latvian.kubejs.util.UtilsJS;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.stats.StatBase;

/**
 * @author LatvianModder
 */
public class PlayerJS extends LivingEntityJS
{
	public final EntityPlayerMP player;
	private PlayerInventoryJS inventory;

	public PlayerJS(ServerJS s, EntityPlayerMP p)
	{
		super(s, p);
		player = p;
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

	public void sendStatusMessage(Object... message)
	{
		player.sendStatusMessage(TextUtils.INSTANCE.of(message).component(), true);
	}

	public PlayerInventoryJS inventory()
	{
		if (inventory == null)
		{
			inventory = new PlayerInventoryJS(this);
		}

		return inventory;
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
		StatBase stat = UtilsJS.INSTANCE.stat(id);
		return stat == null ? 0 : player.getStatFile().readStat(stat);
	}

	public void setStat(Object id, int value)
	{
		StatBase stat = UtilsJS.INSTANCE.stat(id);

		if (stat != null)
		{
			player.getStatFile().unlockAchievement(player, stat, value);
		}
	}

	public void addStat(Object id, int value)
	{
		StatBase stat = UtilsJS.INSTANCE.stat(id);

		if (stat != null)
		{
			player.getStatFile().increaseStat(player, stat, value);
		}
	}
}