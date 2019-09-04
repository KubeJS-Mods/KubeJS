package dev.latvian.kubejs.player;

import net.minecraft.entity.player.EntityPlayer;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author LatvianModder
 */
public abstract class PlayerDataJS<E extends EntityPlayer, P extends PlayerJS<E>>
{
	public final UUID uuid;
	public final String name;
	public final Map<String, Object> data;

	public PlayerDataJS(UUID id, String n)
	{
		uuid = id;
		name = n;
		data = new HashMap<>();
	}

	@Nullable
	public abstract E getPlayerEntity();

	public abstract P player();
}