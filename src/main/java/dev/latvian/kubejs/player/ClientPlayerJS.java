package dev.latvian.kubejs.player;

import net.minecraft.client.entity.EntityPlayerSP;

/**
 * @author LatvianModder
 */
public class ClientPlayerJS extends PlayerJS<EntityPlayerSP>
{
	public ClientPlayerJS(ClientPlayerDataJS d)
	{
		super(d, d.world, d.world.minecraft.player);
	}

	@Override
	public PlayerStatsJS stats()
	{
		return new PlayerStatsJS(this, player.getStatFileWriter());
	}
}