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
		super(d, d.world, player);
	}

	@Override
	public PlayerStatsJS stats()
	{
		return new PlayerStatsJS(this, ((EntityPlayerSP) player).getStatFileWriter());
	}
}