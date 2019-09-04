package dev.latvian.kubejs.player;

import dev.latvian.kubejs.world.ClientWorldJS;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * @author LatvianModder
 */
public class ClientPlayerDataJS extends PlayerDataJS<EntityPlayerSP, ClientPlayerJS>
{
	public final ClientWorldJS world;
	public final ClientPlayerJS player;

	public ClientPlayerDataJS(ClientWorldJS w, UUID id, String n)
	{
		super(id, n);
		world = w;
		player = new ClientPlayerJS(this);
	}

	@Nullable
	@Override
	public EntityPlayerSP getPlayerEntity()
	{
		return Minecraft.getMinecraft().player;
	}

	@Override
	public ClientPlayerJS player()
	{
		return player;
	}
}