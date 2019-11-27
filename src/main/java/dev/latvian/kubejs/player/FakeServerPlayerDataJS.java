package dev.latvian.kubejs.player;

import dev.latvian.kubejs.server.ServerJS;
import dev.latvian.kubejs.world.WorldJS;
import net.minecraft.entity.player.ServerPlayerEntity;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public class FakeServerPlayerDataJS extends ServerPlayerDataJS
{
	public ServerPlayerEntity player;

	public FakeServerPlayerDataJS(ServerJS s, ServerPlayerEntity p)
	{
		super(s, p.getUniqueID(), p.getGameProfile().getName(), true);
		player = p;
	}

	@Override
	public WorldJS getOverworld()
	{
		return getServer().getOverworld();
	}

	@Override
	@Nullable
	public ServerPlayerEntity getMinecraftPlayer()
	{
		return player;
	}
}