package dev.latvian.kubejs.entity;

import dev.latvian.kubejs.world.WorldJS;
import net.minecraft.world.damagesource.DamageSource;
import org.jetbrains.annotations.Nullable;

/**
 * @author LatvianModder
 */
public class DamageSourceJS
{
	private final WorldJS world;
	public final DamageSource source;

	public DamageSourceJS(WorldJS w, DamageSource s)
	{
		world = w;
		source = s;
	}

	public WorldJS getWorld()
	{
		return world;
	}

	public String getType()
	{
		return source.msgId;
	}

	@Nullable
	public EntityJS getImmediate()
	{
		return getWorld().getEntity(source.getDirectEntity());
	}

	@Nullable
	public EntityJS getActual()
	{
		return getWorld().getEntity(source.getDirectEntity());
	}
}