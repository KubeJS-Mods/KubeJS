package dev.latvian.kubejs.player;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.net.KubeJSNet;
import dev.latvian.kubejs.net.MessageSendDataFromClient;
import dev.latvian.kubejs.util.MapJS;
import dev.latvian.kubejs.util.Overlay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public class ClientPlayerJS extends PlayerJS<PlayerEntity>
{
	private final boolean isSelf;

	public ClientPlayerJS(ClientPlayerDataJS d, PlayerEntity p, boolean s)
	{
		super(d, d.getWorld(), p);
		isSelf = s;
	}

	public boolean isSelf()
	{
		return isSelf;
	}

	@Override
	public PlayerStatsJS getStats()
	{
		if (!isSelf())
		{
			throw new IllegalStateException("Can't access other player stats!");
		}

		return new PlayerStatsJS(this, ((ClientPlayerEntity) minecraftPlayer).getStats());
	}

	@Override
	public void openOverlay(Overlay overlay)
	{
		if (isSelf())
		{
			KubeJS.instance.proxy.openOverlay(overlay);
		}
	}

	@Override
	public void closeOverlay(String overlay)
	{
		if (isSelf())
		{
			KubeJS.instance.proxy.closeOverlay(overlay);
		}
	}

	@Override
	public boolean isMiningBlock()
	{
		return isSelf() && Minecraft.getInstance().playerController.getIsHittingBlock();
	}

	@Override
	public void sendData(String channel, @Nullable Object data)
	{
		if (!channel.isEmpty() && isSelf())
		{
			KubeJSNet.MAIN.sendToServer(new MessageSendDataFromClient(channel, MapJS.nbt(data)));
		}
	}
}