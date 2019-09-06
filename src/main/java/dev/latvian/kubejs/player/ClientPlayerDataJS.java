package dev.latvian.kubejs.player;

import dev.latvian.kubejs.world.ClientWorldJS;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * @author LatvianModder
 */
public class ClientPlayerDataJS extends PlayerDataJS<EntityPlayer, ClientPlayerJS>
{
	public final ClientWorldJS world;
	public final ClientPlayerJS player;

	public ClientPlayerDataJS(ClientWorldJS w, UUID id, String n)
	{
		super(id, n);
		world = w;
		player = new ClientPlayerJS(this, world.minecraft.player);
	}

	@Nullable
	@Override
	public EntityPlayerSP getPlayerEntity()
	{
		return world.minecraft.player;
	}

	@Override
	public ClientPlayerJS getPlayer()
	{
		return player;
	}
}