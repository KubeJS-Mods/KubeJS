package dev.latvian.kubejs.player;

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
		return new PlayerStatsJS(this, ((EntityPlayerSP) getPlayerEntity()).getStatFileWriter());
	}
}