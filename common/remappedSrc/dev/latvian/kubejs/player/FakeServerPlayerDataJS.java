package dev.latvian.kubejs.player;

import dev.latvian.kubejs.server.ServerJS;
import dev.latvian.kubejs.world.WorldJS;
import org.jetbrains.annotations.Nullable;
import net.minecraft.server.level.ServerPlayer;

/**
 * @author LatvianModder
 */
public class FakeServerPlayerDataJS extends ServerPlayerDataJS
{
	public ServerPlayer player;

	public FakeServerPlayerDataJS(ServerJS s, ServerPlayer p)
	{
		super(s, p.getUUID(), p.getGameProfile().getName(), true);
		player = p;
	}

	@Override
	public WorldJS getOverworld()
	{
		return getServer().getOverworld();
	}

	@Override
	@Nullable
	public ServerPlayer getMinecraftPlayer()
	{
		return player;
	}
}