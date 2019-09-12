package dev.latvian.kubejs.entity;

import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.world.BlockContainerJS;
import dev.latvian.kubejs.world.WorldJS;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.common.ForgeHooks;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class LivingEntityJS extends EntityJS
{
	public final EntityLivingBase livingEntity;

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
		return getWorld().getLivingEntity(livingEntity.getRevengeTarget());
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
		return getWorld().getLivingEntity(livingEntity.getLastAttackedEntity());
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
		return livingEntity.getLastDamageSource() == null ? null : new DamageSourceJS(getWorld(), livingEntity.getLastDamageSource());
	}

	@Nullable
	public LivingEntityJS getAttackingEntity()
	{
		return getWorld().getLivingEntity(livingEntity.getAttackingEntity());
	}

	public void swingArm(EnumHand hand)
	{
		livingEntity.swingArm(hand);
	}

	public ItemStackJS getEquipment(EntityEquipmentSlot slot)
	{
		return ItemStackJS.of(livingEntity.getItemStackFromSlot(slot));
	}

	public void setEquipment(EntityEquipmentSlot slot, Object item)
	{
		livingEntity.setItemStackToSlot(slot, ItemStackJS.of(item).getItemStack());
	}

	public ItemStackJS getHandItem(EnumHand hand)
	{
		return ItemStackJS.of(livingEntity.getHeldItem(hand));
	}

	public void setHandItem(EnumHand hand, ItemStackJS stack)
	{
		livingEntity.setHeldItem(hand, stack.getItemStack());
	}

	public void damageHeldItem(EnumHand hand, int amount)
	{
		ItemStack stack = livingEntity.getHeldItem(hand);

		if (!stack.isEmpty())
		{
			stack.damageItem(amount, livingEntity);

			if (stack.isEmpty())
			{
				livingEntity.setHeldItem(hand, ItemStack.EMPTY);
			}
		}
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

	public double getReachDistance()
	{
		return livingEntity.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue();
	}

	@Nullable
	public Map<String, Object> rayTrace(double distance)
	{
		Map<String, Object> map = new HashMap<>();
		RayTraceResult ray = ForgeHooks.rayTraceEyes(livingEntity, distance);

		if (ray != null)
		{
			map.put("info", ray.hitInfo);
			map.put("hitX", ray.hitVec.x);
			map.put("hitY", ray.hitVec.y);
			map.put("hitZ", ray.hitVec.z);

			if (ray.typeOfHit == RayTraceResult.Type.BLOCK)
			{
				map.put("block", new BlockContainerJS(getWorld().world, ray.getBlockPos()));
				map.put("facing", ray.sideHit);
				map.put("subHit", ray.subHit);
			}
			else if (ray.typeOfHit == RayTraceResult.Type.ENTITY)
			{
				map.put("entity", getWorld().getEntity(ray.entityHit));
			}
		}

		return map;
	}

	@Nullable
	public Map<String, Object> rayTrace()
	{
		return rayTrace(getReachDistance());
	}
}