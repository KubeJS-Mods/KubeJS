package dev.latvian.kubejs.entity;

import dev.latvian.kubejs.server.ServerJS;
import net.minecraft.entity.EntityLivingBase;

/**
 * @author LatvianModder
 */
public class LivingEntityJS extends EntityJS
{
	public final transient EntityLivingBase livingEntity;

	public LivingEntityJS(ServerJS s, EntityLivingBase e)
	{
		super(s, e);
		livingEntity = e;
	}

	@Override
	public boolean isLiving()
	{
		return true;
	}
}