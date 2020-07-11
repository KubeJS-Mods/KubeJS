package dev.latvian.kubejs.entity;

import dev.latvian.kubejs.docs.MinecraftClass;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.world.WorldJS;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author LatvianModder
 */
public class LivingEntityJS extends EntityJS
{
	@MinecraftClass
	public final LivingEntity minecraftLivingEntity;

	public LivingEntityJS(WorldJS w, LivingEntity e)
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

	public void setHealth(float hp)
	{
		minecraftLivingEntity.setHealth(hp);
	}

	public void heal(float hp)
	{
		minecraftLivingEntity.heal(hp);
	}

	public float getMaxHealth()
	{
		return minecraftLivingEntity.getMaxHealth();
	}

	public void setMaxHealth(float hp)
	{
		minecraftLivingEntity.getAttribute(Attributes.MAX_HEALTH).setBaseValue(hp);
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
		return minecraftLivingEntity.isSleeping();
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

	public void setRevengeTarget(@Nullable LivingEntityJS target)
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

	public void swingArm(Hand hand)
	{
		minecraftLivingEntity.swingArm(hand);
	}

	public ItemStackJS getEquipment(EquipmentSlotType slot)
	{
		return ItemStackJS.of(minecraftLivingEntity.getItemStackFromSlot(slot));
	}

	public void setEquipment(EquipmentSlotType slot, Object item)
	{
		minecraftLivingEntity.setItemStackToSlot(slot, ItemStackJS.of(item).getItemStack());
	}

	public ItemStackJS getHeldItem(Hand hand)
	{
		return ItemStackJS.of(minecraftLivingEntity.getHeldItem(hand));
	}

	public void setHeldItem(Hand hand, Object item)
	{
		minecraftLivingEntity.setHeldItem(hand, ItemStackJS.of(item).getItemStack());
	}

	public ItemStackJS getMainHandItem()
	{
		return getHeldItem(Hand.MAIN_HAND);
	}

	public void setMainHandItem(Object item)
	{
		setHeldItem(Hand.MAIN_HAND, item);
	}

	public ItemStackJS getOffHandItem()
	{
		return getHeldItem(Hand.OFF_HAND);
	}

	public void setOffHandItem(Object item)
	{
		setHeldItem(Hand.OFF_HAND, item);
	}

	public void damageHeldItem(Hand hand, int amount, Consumer<ItemStackJS> onBroken)
	{
		ItemStack stack = minecraftLivingEntity.getHeldItem(hand);

		if (!stack.isEmpty())
		{
			stack.damageItem(amount, minecraftLivingEntity, livingEntity -> onBroken.accept(ItemStackJS.of(stack)));

			if (stack.isEmpty())
			{
				minecraftLivingEntity.setHeldItem(hand, ItemStack.EMPTY);
			}
		}
	}

	public void damageHeldItem(Hand hand, int amount)
	{
		damageHeldItem(hand, amount, stack -> {});
	}

	public void damageHeldItem()
	{
		damageHeldItem(Hand.MAIN_HAND, 1);
	}

	public boolean isHoldingInAnyHand(Object ingredient)
	{
		IngredientJS i = IngredientJS.of(ingredient);
		return i.testVanilla(minecraftLivingEntity.getHeldItem(Hand.MAIN_HAND)) || i.testVanilla(minecraftLivingEntity.getHeldItem(Hand.OFF_HAND));
	}

	public float getMovementSpeed()
	{
		return minecraftLivingEntity.getAIMoveSpeed();
	}

	public void setMovementSpeed(float speed)
	{
		minecraftLivingEntity.setAIMoveSpeed(speed);
	}

	public boolean canEntityBeSeen(EntityJS entity)
	{
		return minecraftLivingEntity.canEntityBeSeen(entity.minecraftEntity);
	}

	public float getAbsorptionAmount()
	{
		return minecraftLivingEntity.getAbsorptionAmount();
	}

	public void setAbsorptionAmount(float amount)
	{
		minecraftLivingEntity.setAbsorptionAmount(amount);
	}

	public double getReachDistance()
	{
		return minecraftLivingEntity.getAttribute(net.minecraftforge.common.ForgeMod.REACH_DISTANCE.get()).getValue();
	}

	@Nullable
	public Map<String, Object> rayTrace()
	{
		return rayTrace(getReachDistance());
	}
}