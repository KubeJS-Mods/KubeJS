package dev.latvian.kubejs.entity;

import dev.latvian.kubejs.world.WorldJS;
import net.minecraft.util.DamageSource;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public class DamageSourceJS
{
	public final String type;

	@Nullable
	public final EntityJS immediate;

	@Nullable
	public final EntityJS actual;

	public DamageSourceJS(WorldJS world, DamageSource damageSource)
	{
		type = damageSource.damageType;
		immediate = world.entity(damageSource.getImmediateSource());
		actual = world.entity(damageSource.getTrueSource());
	}
}