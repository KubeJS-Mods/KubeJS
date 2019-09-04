package dev.latvian.kubejs.entity;

import dev.latvian.kubejs.world.WorldJS;
import net.minecraft.entity.EntityLivingBase;

/**
 * @author LatvianModder
 */
public class LivingEntityJS extends EntityJS
{
	public final transient EntityLivingBase livingEntity;

	public LivingEntityJS(WorldJS w, EntityLivingBase e)
	{
		super(w, e);
		livingEntity = e;
	}

	@Override
	public boolean isLiving()
	{
		return true;
	}
}