package dev.latvian.kubejs.net;

import dev.latvian.kubejs.entity.EntityJS;
import dev.latvian.kubejs.player.PlayerEventJS;
import dev.latvian.kubejs.util.MapJS;
import net.minecraft.entity.player.PlayerEntity;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public class NetworkEventJS extends PlayerEventJS
{
	private final PlayerEntity player;
	private final String channel;
	private final MapJS data;

	public NetworkEventJS(PlayerEntity p, String c, @Nullable MapJS d)
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

	@Nullable
	public MapJS getData()
	{
		return data;
	}
}