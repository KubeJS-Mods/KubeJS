package dev.latvian.kubejs.entity;

import dev.latvian.kubejs.documentation.P;
import dev.latvian.kubejs.documentation.T;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
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

	public void setHealth(@P("hp") float hp)
	{
		livingEntity.setHealth(hp);
	}

	public void heal(@P("hp") float hp)
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

	public void setRevengeTarget(@Nullable @P("target") LivingEntityJS target)
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

	public void swingArm(@P("hand") EnumHand hand)
	{
		livingEntity.swingArm(hand);
	}

	public ItemStackJS getEquipment(@P("slot") EntityEquipmentSlot slot)
	{
		return ItemStackJS.of(livingEntity.getItemStackFromSlot(slot));
	}

	public void setEquipment(@P("slot") EntityEquipmentSlot slot, @P("item") @T(ItemStackJS.class) Object item)
	{
		livingEntity.setItemStackToSlot(slot, ItemStackJS.of(item).getItemStack());
	}

	public ItemStackJS getHeldItem(@P("hand") EnumHand hand)
	{
		return ItemStackJS.of(livingEntity.getHeldItem(hand));
	}

	public void setHeldItem(@P("hand") EnumHand hand, @P("item") @T(ItemStackJS.class) Object item)
	{
		livingEntity.setHeldItem(hand, ItemStackJS.of(item).getItemStack());
	}

	public void damageHeldItem(@P("hand") EnumHand hand, @P("amount") int amount)
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

	public boolean isHoldingInAnyHand(@P("ingredient") @T(IngredientJS.class) Object ingredient)
	{
		IngredientJS i = IngredientJS.of(ingredient);
		return i.test(livingEntity.getHeldItem(EnumHand.MAIN_HAND)) || i.test(livingEntity.getHeldItem(EnumHand.OFF_HAND));
	}

	public float getMovementSpeed()
	{
		return livingEntity.getAIMoveSpeed();
	}

	public void setMovementSpeed(@P("speed") float speed)
	{
		livingEntity.setAIMoveSpeed(speed);
	}

	public boolean canEntityBeSeen(@P("entity") EntityJS entity)
	{
		return livingEntity.canEntityBeSeen(entity.entity);
	}

	public float getAbsorptionAmount()
	{
		return livingEntity.getAbsorptionAmount();
	}

	public void setAbsorptionAmount(@P("amount") float amount)
	{
		livingEntity.setAbsorptionAmount(amount);
	}

	public double getReachDistance()
	{
		return livingEntity.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue();
	}

	@Nullable
	public Map<String, Object> rayTrace(@P("distance") double distance)
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