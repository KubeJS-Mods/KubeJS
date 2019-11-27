package dev.latvian.kubejs.net;

import dev.latvian.kubejs.entity.EntityJS;
import dev.latvian.kubejs.player.PlayerEventJS;
import dev.latvian.kubejs.util.nbt.NBTCompoundJS;
import net.minecraft.entity.player.PlayerEntity;

/**
 * @author LatvianModder
 */
public class NetworkEventJS extends PlayerEventJS
{
	private final PlayerEntity player;
	private final String channel;
	private final NBTCompoundJS data;

	public NetworkEventJS(PlayerEntity p, String c, NBTCompoundJS d)
	{
		player = p;
		channel = c;
		data = d;
	}

	@Override
	public boolean canCancel()
	{
		return true;
	}

	@Override
	public EntityJS getEntity()
	{
		return entityOf(player);
	}

	public String getChannel()
	{
		return channel;
	}

	public NBTCompoundJS getData()
	{
		return data;
	}
}