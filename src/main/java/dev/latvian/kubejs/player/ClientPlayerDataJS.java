package dev.latvian.kubejs.player;

import com.mojang.authlib.GameProfile;
import dev.latvian.kubejs.world.ClientWorldJS;
import dev.latvian.kubejs.world.WorldJS;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * @author LatvianModder
 */
public class ClientPlayerDataJS extends PlayerDataJS<EntityPlayer, ClientPlayerJS>
{
	private final ClientWorldJS world;
	private final ClientPlayerJS player;

	public ClientPlayerDataJS(ClientWorldJS w)
	{
		world = w;
		player = new ClientPlayerJS(this, world.getMinecraft().player);
	}

	public ClientWorldJS getWorld()
	{
		return world;
	}

	@Override
	public UUID getId()
	{
		return world.getMinecraft().player.getUniqueID();
	}

	@Override
	public String getName()
	{
		return world.getMinecraft().player.getName();
	}

	@Override
	public GameProfile getProfile()
	{
		return world.getMinecraft().player.getGameProfile();
	}

	@Override
	public WorldJS getOverworld()
	{
		return world;
	}

	@Nullable
	@Override
	public EntityPlayerSP getPlayerEntity()
	{
		return world.getMinecraft().player;
	}

	@Override
	public ClientPlayerJS getPlayer()
	{
		return player;
	}
}