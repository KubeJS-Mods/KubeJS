package dev.latvian.kubejs.entity;

import net.minecraftforge.event.entity.living.LivingAttackEvent;

/**
 * @author LatvianModder
 */
public class LivingEntityAttackEventJS extends LivingEntityEventJS
{
	public final DamageSourceJS source;
	public final float amount;

	public LivingEntityAttackEventJS(LivingAttackEvent event)
	{
		super(event.getEntity());
		source = new DamageSourceJS(world, event.getSource());
		amount = event.getAmount();
	}

	@Override
	public boolean canCancel()
	{
		return true;
	}
}