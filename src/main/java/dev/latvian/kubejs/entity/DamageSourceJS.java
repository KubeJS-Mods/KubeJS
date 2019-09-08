package dev.latvian.kubejs.entity;

import dev.latvian.kubejs.world.WorldJS;
import net.minecraft.util.DamageSource;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public class DamageSourceJS
{
	private final WorldJS world;
	public final transient DamageSource source;

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
		return source.damageType;
	}

	@Nullable
	public EntityJS getImmediate()
	{
		return getWorld().getEntity(source.getImmediateSource());
	}

	public boolean hasImmediate()
	{
		return source.getImmediateSource() != null;
	}

	@Nullable
	public EntityJS getActual()
	{
		return getWorld().getEntity(source.getImmediateSource());
	}

	public boolean hasActual()
	{
		return source.getImmediateSource() != null;
	}
}