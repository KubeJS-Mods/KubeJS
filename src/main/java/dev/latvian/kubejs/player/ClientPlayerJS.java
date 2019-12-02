package dev.latvian.kubejs.player;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.util.Overlay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;

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
}