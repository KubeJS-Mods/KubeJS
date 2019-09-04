package dev.latvian.kubejs.entity;

import dev.latvian.kubejs.world.WorldJS;
import net.minecraft.util.DamageSource;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public class DamageSourceJS
{
	public final WorldJS world;
	public final transient DamageSource source;

	public DamageSourceJS(WorldJS w, DamageSource s)
	{
		world = w;
		source = s;
	}

	public String getType()
	{
		return source.damageType;
	}

	@Nullable
	public EntityJS getImmediate()
	{
		return world.getEntity(source.getImmediateSource());
	}

	@Nullable
	public EntityJS getActual()
	{
		return world.getEntity(source.getImmediateSource());
	}
}