package dev.latvian.kubejs.entity;

import dev.latvian.kubejs.MinecraftClass;
import dev.latvian.kubejs.documentation.P;
import dev.latvian.kubejs.documentation.T;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.world.BlockContainerJS;
import dev.latvian.kubejs.world.WorldJS;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
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
	@MinecraftClass
	public final EntityLivingBase minecraftLivingEntity;

	public LivingEntityJS(WorldJS w, EntityLivingBase e)
	{
		super(w, e);
		minecraftLivingEntity = e;
	}

	@Override
	public boolean isLiving()
	{
		return true;
	}

	public boolean isChild()
	{
		return minecraftLivingEntity.isChild();
	}

	public float getHealth()
	{
		return minecraftLivingEntity.getHealth();
	}

	public void setHealth(@P("hp") float hp)
	{
		minecraftLivingEntity.setHealth(hp);
	}

	public void heal(@P("hp") float hp)
	{
		minecraftLivingEntity.heal(hp);
	}

	public float getMaxHealth()
	{
		return minecraftLivingEntity.getMaxHealth();
	}

	public void setMaxHealth(@P("hp") float hp)
	{
		minecraftLivingEntity.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(hp);
	}

	public boolean isUndead()
	{
		return minecraftLivingEntity.isEntityUndead();
	}

	public boolean isOnLadder()
	{
		return minecraftLivingEntity.isOnLadder();
	}

	public boolean isSleeping()
	{
		return minecraftLivingEntity.isPlayerSleeping();
	}

	public boolean isElytraFlying()
	{
		return minecraftLivingEntity.isElytraFlying();
	}

	@Nullable
	public LivingEntityJS getRevengeTarget()
	{
		return getWorld().getLivingEntity(minecraftLivingEntity.getRevengeTarget());
	}

	public int getRevengeTimer()
	{
		return minecraftLivingEntity.getRevengeTimer();
	}

	public void setRevengeTarget(@Nullable @P("target") LivingEntityJS target)
	{
		minecraftLivingEntity.setRevengeTarget(target == null ? null : target.minecraftLivingEntity);
	}

	@Nullable
	public LivingEntityJS getLastAttackedEntity()
	{
		return getWorld().getLivingEntity(minecraftLivingEntity.getLastAttackedEntity());
	}

	public int getLastAttackedEntityTime()
	{
		return minecraftLivingEntity.getLastAttackedEntityTime();
	}

	public int getIdleTime()
	{
		return minecraftLivingEntity.getIdleTime();
	}

	public EntityPotionEffectsJS getPotionEffects()
	{
		return new EntityPotionEffectsJS(minecraftLivingEntity);
	}

	@Nullable
	public DamageSourceJS getLastDamageSource()
	{
		return minecraftLivingEntity.getLastDamageSource() == null ? null : new DamageSourceJS(getWorld(), minecraftLivingEntity.getLastDamageSource());
	}

	@Nullable
	public LivingEntityJS getAttackingEntity()
	{
		return getWorld().getLivingEntity(minecraftLivingEntity.getAttackingEntity());
	}

	public void swingArm(@P("hand") EnumHand hand)
	{
		minecraftLivingEntity.swingArm(hand);
	}

	public ItemStackJS getEquipment(@P("slot") EntityEquipmentSlot slot)
	{
		return ItemStackJS.of(minecraftLivingEntity.getItemStackFromSlot(slot));
	}

	public void setEquipment(@P("slot") EntityEquipmentSlot slot, @P("item") @T(ItemStackJS.class) Object item)
	{
		minecraftLivingEntity.setItemStackToSlot(slot, ItemStackJS.of(item).getItemStack());
	}

	public ItemStackJS getHeldItem(@P("hand") EnumHand hand)
	{
		return ItemStackJS.of(minecraftLivingEntity.getHeldItem(hand));
	}

	public void setHeldItem(@P("hand") EnumHand hand, @P("item") @T(ItemStackJS.class) Object item)
	{
		minecraftLivingEntity.setHeldItem(hand, ItemStackJS.of(item).getItemStack());
	}

	public ItemStackJS getMainHandItem()
	{
		return getHeldItem(EnumHand.MAIN_HAND);
	}

	public void setMainHandItem(@P("item") @T(ItemStackJS.class) Object item)
	{
		setHeldItem(EnumHand.MAIN_HAND, item);
	}

	public ItemStackJS getOffHandItem()
	{
		return getHeldItem(EnumHand.OFF_HAND);
	}

	public void setOffHandItem(@P("item") @T(ItemStackJS.class) Object item)
	{
		setHeldItem(EnumHand.OFF_HAND, item);
	}

	public void damageHeldItem(@P("hand") EnumHand hand, @P("amount") int amount)
	{
		ItemStack stack = minecraftLivingEntity.getHeldItem(hand);

		if (!stack.isEmpty())
		{
			stack.damageItem(amount, minecraftLivingEntity);

			if (stack.isEmpty())
			{
				minecraftLivingEntity.setHeldItem(hand, ItemStack.EMPTY);
			}
		}
	}

	public boolean isHoldingInAnyHand(@P("ingredient") @T(IngredientJS.class) Object ingredient)
	{
		IngredientJS i = IngredientJS.of(ingredient);
		return i.testVanilla(minecraftLivingEntity.getHeldItem(EnumHand.MAIN_HAND)) || i.testVanilla(minecraftLivingEntity.getHeldItem(EnumHand.OFF_HAND));
	}

	public float getMovementSpeed()
	{
		return minecraftLivingEntity.getAIMoveSpeed();
	}

	public void setMovementSpeed(@P("speed") float speed)
	{
		minecraftLivingEntity.setAIMoveSpeed(speed);
	}

	public boolean canEntityBeSeen(@P("entity") EntityJS entity)
	{
		return minecraftLivingEntity.canEntityBeSeen(entity.minecraftEntity);
	}

	public float getAbsorptionAmount()
	{
		return minecraftLivingEntity.getAbsorptionAmount();
	}

	public void setAbsorptionAmount(@P("amount") float amount)
	{
		minecraftLivingEntity.setAbsorptionAmount(amount);
	}

	public double getReachDistance()
	{
		return minecraftLivingEntity.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue();
	}

	@Nullable
	public Map<String, Object> rayTrace(@P("distance") double distance)
	{
		Map<String, Object> map = new HashMap<>();
		RayTraceResult ray = ForgeHooks.rayTraceEyes(minecraftLivingEntity, distance);

		if (ray != null)
		{
			map.put("info", ray.hitInfo);
			map.put("hitX", ray.hitVec.x);
			map.put("hitY", ray.hitVec.y);
			map.put("hitZ", ray.hitVec.z);

			if (ray.typeOfHit == RayTraceResult.Type.BLOCK)
			{
				map.put("block", new BlockContainerJS(getWorld().minecraftWorld, ray.getBlockPos()));
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