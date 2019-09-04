package dev.latvian.kubejs.entity;

import dev.latvian.kubejs.world.WorldJS;
import net.minecraft.entity.EntityLivingBase;

import javax.annotation.Nullable;

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

	public boolean isChild()
	{
		return livingEntity.isChild();
	}

	public float getHealth()
	{
		return livingEntity.getHealth();
	}

	public void setHealth(float hp)
	{
		livingEntity.setHealth(hp);
	}

	public void heal(float hp)
	{
		livingEntity.heal(hp);
	}

	public boolean isUndead()
	{
		return livingEntity.isEntityUndead();
	}

	@Nullable
	public LivingEntityJS getRevengeTarget()
	{
		return world.getLivingEntity(livingEntity.getRevengeTarget());
	}

	public int getRevengeTimer()
	{
		return livingEntity.getRevengeTimer();
	}

	public void setRevengeTarget(@Nullable LivingEntityJS target)
	{
		livingEntity.setRevengeTarget(target == null ? null : target.livingEntity);
	}

	@Nullable
	public LivingEntityJS getLastAttackedEntity()
	{
		return world.getLivingEntity(livingEntity.getLastAttackedEntity());
	}

	public int getLastAttackedEntityTime()
	{
		return livingEntity.getLastAttackedEntityTime();
	}

	public int getIdleTime()
	{
		return livingEntity.getIdleTime();
	}

	public EntityPotionEffectsJS getPotionEffects()
	{
		return new EntityPotionEffectsJS(livingEntity);
	}

	@Nullable
	public DamageSourceJS getLastDamageSource()
	{
		return livingEntity.getLastDamageSource() == null ? null : new DamageSourceJS(world, livingEntity.getLastDamageSource());
	}
}