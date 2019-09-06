package dev.latvian.kubejs.player;

import dev.latvian.kubejs.documentation.DocClass;
import dev.latvian.kubejs.documentation.DocField;
import dev.latvian.kubejs.documentation.DocMethod;
import dev.latvian.kubejs.world.WorldJS;
import net.minecraft.entity.player.EntityPlayer;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author LatvianModder
 */
@DocClass
public abstract class PlayerDataJS<E extends EntityPlayer, P extends PlayerJS<E>>
{
	@DocField
	public final UUID id;

	@DocField
	public final String name;

	@DocField
	public final Map<String, Object> data;

	public PlayerDataJS(UUID i, String n)
	{
		id = i;
		name = n;
		data = new HashMap<>();
	}

	public abstract WorldJS getOverworld();

	@Nullable
	public abstract E getPlayerEntity();

	@DocMethod
	public abstract P getPlayer();
}