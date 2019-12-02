package dev.latvian.kubejs.player;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.documentation.P;
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
	public ClientPlayerJS(ClientPlayerDataJS d)
	{
		super(d, d.getWorld(), d.getWorld().minecraftPlayer);
	}

	@Override
	public PlayerStatsJS getStats()
	{
		return new PlayerStatsJS(this, ((ClientPlayerEntity) minecraftPlayer).getStats());
	}

	@Override
	public void openOverlay(Overlay overlay)
	{
		KubeJS.instance.proxy.openOverlay(overlay);
	}

	@Override
	public void closeOverlay(String overlay)
	{
		KubeJS.instance.proxy.closeOverlay(overlay);
	}

	@Override
	public boolean isMiningBlock()
	{
		return Minecraft.getInstance().playerController.getIsHittingBlock();
	}

	@Override
	public void sendData(@P("channel") String channel, @Nullable @P("data") Object data)
	{
		if (!channel.isEmpty())
		{
			KubeJSNet.MAIN.sendToServer(new MessageSendDataFromClient(channel, MapJS.nbt(data)));
		}
	}
}