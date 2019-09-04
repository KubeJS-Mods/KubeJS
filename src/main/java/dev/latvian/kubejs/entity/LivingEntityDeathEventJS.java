package dev.latvian.kubejs.entity;

import net.minecraftforge.event.entity.living.LivingDeathEvent;

/**
 * @author LatvianModder
 */
public class LivingEntityDeathEventJS extends LivingEntityEventJS
{
	public final transient LivingDeathEvent event;

	public LivingEntityDeathEventJS(LivingDeathEvent e)
	{
		event = e;
	}

	@Override
	public boolean canCancel()
	{
		return true;
	}

	@Override
	public EntityJS getEntity()
	{
		return entityOf(event);
	}

	public DamageSourceJS getSource()
	{
		return new DamageSourceJS(getWorld(), event.getSource());
	}
}