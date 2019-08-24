package dev.latvian.kubejs.entity;

import net.minecraftforge.event.entity.living.LivingDeathEvent;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public class LivingEntityDeathEventJS extends LivingEntityEventJS
{
	public final String type;

	@Nullable
	public final EntityJS immediateSource;

	@Nullable
	public final EntityJS trueSource;

	public LivingEntityDeathEventJS(LivingDeathEvent event)
	{
		super(event.getEntity());
		type = event.getSource().damageType;
		immediateSource = world.entity(event.getSource().getImmediateSource());
		trueSource = world.entity(event.getSource().getTrueSource());
	}

	@Override
	public boolean canCancel()
	{
		return true;
	}
}