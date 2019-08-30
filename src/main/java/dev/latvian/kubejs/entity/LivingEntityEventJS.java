package dev.latvian.kubejs.entity;

import net.minecraft.entity.Entity;

/**
 * @author LatvianModder
 */
public class LivingEntityEventJS extends EntityEventJS
{
	public LivingEntityEventJS(LivingEntityJS e)
	{
		super(e);
	}

	public LivingEntityEventJS(Entity e)
	{
		super(e);
	}
}