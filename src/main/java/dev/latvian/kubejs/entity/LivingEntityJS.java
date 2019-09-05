package dev.latvian.kubejs.entity;

import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.world.WorldJS;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.EnumHand;

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

	public float getMaxHealth()
	{
		return livingEntity.getMaxHealth();
	}

	public boolean isUndead()
	{
		return livingEntity.isEntityUndead();
	}

	public boolean isOnLadder()
	{
		return livingEntity.isOnLadder();
	}

	public boolean isSleeping()
	{
		return livingEntity.isPlayerSleeping();
	}

	public boolean isElytraFlying()
	{
		return livingEntity.isElytraFlying();
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

	@Nullable
	public LivingEntityJS getAttackingEntity()
	{
		return world.getLivingEntity(livingEntity.getAttackingEntity());
	}

	public void swingArm(boolean mainHand)
	{
		livingEntity.swingArm(mainHand ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND);
	}

	public ItemStackJS getEquipment(EntityEquipmentSlot slot)
	{
		return ItemStackJS.of(livingEntity.getItemStackFromSlot(slot));
	}

	public void setEquipment(EntityEquipmentSlot slot, Object item)
	{
		livingEntity.setItemStackToSlot(slot, ItemStackJS.of(item).itemStack());
	}

	public ItemStackJS getHandItem(boolean mainHand)
	{
		return ItemStackJS.of(livingEntity.getHeldItem(mainHand ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND));
	}

	public void setHandItem(boolean mainHand, ItemStackJS stack)
	{
		livingEntity.setHeldItem(mainHand ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND, stack.itemStack());
	}

	public float getMovementSpeed()
	{
		return livingEntity.getAIMoveSpeed();
	}

	public void setMovementSpeed(float speed)
	{
		livingEntity.setAIMoveSpeed(speed);
	}

	public boolean canEntityBeSeen(EntityJS entity)
	{
		return livingEntity.canEntityBeSeen(entity.entity);
	}

	public float getAbsorptionAmount()
	{
		return livingEntity.getAbsorptionAmount();
	}

	public void setAbsorptionAmount(float amount)
	{
		livingEntity.setAbsorptionAmount(amount);
	}
}