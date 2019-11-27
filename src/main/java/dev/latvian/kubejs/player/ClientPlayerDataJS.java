package dev.latvian.kubejs.player;

import com.mojang.authlib.GameProfile;
import dev.latvian.kubejs.world.ClientWorldJS;
import dev.latvian.kubejs.world.WorldJS;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * @author LatvianModder
 */
public class ClientPlayerDataJS extends PlayerDataJS<PlayerEntity, ClientPlayerJS>
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
		return world.getMinecraft().player.getGameProfile().getName();
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
	public ClientPlayerEntity getMinecraftPlayer()
	{
		return world.getMinecraft().player;
	}

	@Override
	public ClientPlayerJS getPlayer()
	{
		return player;
	}
}