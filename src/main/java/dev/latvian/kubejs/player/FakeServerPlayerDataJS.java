package dev.latvian.kubejs.player;

import dev.latvian.kubejs.server.ServerJS;
import dev.latvian.kubejs.world.WorldJS;
import net.minecraft.entity.player.EntityPlayerMP;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public class FakeServerPlayerDataJS extends ServerPlayerDataJS
{
	public transient EntityPlayerMP player;

	public FakeServerPlayerDataJS(ServerJS s, EntityPlayerMP p)
	{
		super(s, p.getUniqueID(), p.getName(), true);
		player = p;
	}

	@Override
	public WorldJS getOverworld()
	{
		return server.overworld;
	}

	@Override
	@Nullable
	public EntityPlayerMP getPlayerEntity()
	{
		return player;
	}
}