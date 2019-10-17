package dev.latvian.kubejs.player;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.util.Overlay;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;

/**
 * @author LatvianModder
 */
public class ClientPlayerJS extends PlayerJS<EntityPlayer>
{
	public ClientPlayerJS(ClientPlayerDataJS d, EntityPlayer player)
	{
		super(d, d.getWorld(), player);
	}

	@Override
	public PlayerStatsJS getStats()
	{
		return new PlayerStatsJS(this, ((EntityPlayerSP) minecraftPlayer).getStatFileWriter());
	}

	@Override
	public void openOverlay(Overlay overlay)
	{
		KubeJS.PROXY.openOverlay(overlay);
	}

	@Override
	public void closeOverlay(String overlay)
	{
		KubeJS.PROXY.closeOverlay(overlay);
	}
}