package dev.latvian.kubejs.player;

import com.mojang.authlib.GameProfile;
import dev.latvian.kubejs.world.ClientWorldJS;
import dev.latvian.kubejs.world.WorldJS;
import org.jetbrains.annotations.Nullable;
import net.minecraft.world.entity.player.Player;
import java.util.UUID;

/**
 * @author LatvianModder
 */
public class ClientPlayerDataJS extends PlayerDataJS<Player, ClientPlayerJS>
{
	private final ClientWorldJS world;
	private final ClientPlayerJS player;
	private final Player minecraftPlayer;

	public ClientPlayerDataJS(ClientWorldJS w, Player p, boolean s)
	{
		world = w;
		minecraftPlayer = p;
		player = new ClientPlayerJS(this, minecraftPlayer, s);
	}

	public ClientWorldJS getWorld()
	{
		return world;
	}

	@Override
	public UUID getId()
	{
		return player.getId();
	}

	@Override
	public String getName()
	{
		return player.getProfile().getName();
	}

	@Override
	public GameProfile getProfile()
	{
		return player.getProfile();
	}

	@Override
	public WorldJS getOverworld()
	{
		return world;
	}

	@Nullable
	@Override
	public Player getMinecraftPlayer()
	{
		return minecraftPlayer;
	}

	@Override
	public ClientPlayerJS getPlayer()
	{
		return player;
	}
}