package dev.latvian.kubejs.entity;

import net.minecraftforge.event.entity.living.LivingAttackEvent;

/**
 * @author LatvianModder
 */
public class LivingEntityAttackEventJS extends LivingEntityEventJS
{
	public final transient LivingAttackEvent event;

	public LivingEntityAttackEventJS(LivingAttackEvent e)
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

	public float getDamage()
	{
		return event.getAmount();
	}
}