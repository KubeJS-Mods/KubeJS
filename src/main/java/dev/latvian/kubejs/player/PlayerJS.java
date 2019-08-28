package dev.latvian.kubejs.player;

import dev.latvian.kubejs.documentation.DocMethod;
import dev.latvian.kubejs.documentation.Param;
import dev.latvian.kubejs.entity.LivingEntityJS;
import dev.latvian.kubejs.text.Text;
import dev.latvian.kubejs.text.TextUtilsJS;
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
	public final Map<String, Object> data;
	private PlayerInventoryJS inventory;

	public PlayerJS(PlayerDataJS d, EntityPlayerMP p)
	{
		super(d.server, p);
		data = d.data;
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

	@DocMethod(params = @Param(type = Text.class))
	public void statusMessage(Object message)
	{
		player.sendStatusMessage(TextUtilsJS.INSTANCE.of(message).component(), true);
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