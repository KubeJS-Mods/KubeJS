package dev.latvian.kubejs.entity.forge;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.ForgeMod;

public class LivingEntityJSImpl
{
	public static double getReachDistance(LivingEntity livingEntity)
	{
		return livingEntity.getAttribute(ForgeMod.REACH_DISTANCE.get()).getValue();
	}
}
