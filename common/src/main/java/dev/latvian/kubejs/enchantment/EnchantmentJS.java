package dev.latvian.kubejs.enchantment;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.bindings.MobTypeWrapper;
import dev.latvian.kubejs.entity.DamageSourceJS;
import dev.latvian.kubejs.entity.EntityJS;
import dev.latvian.kubejs.entity.LivingEntityJS;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.server.ServerJS;
import dev.latvian.kubejs.util.UtilsJS;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

/**
 * @author ILIKEPIEFOO2
 */
public class EnchantmentJS extends Enchantment {
	public final EnchantmentBuilder properties;

	public EnchantmentJS(EnchantmentBuilder builder){
		super(
				builder.rarityWrapper.rarity,
				builder.categoryWrapper.category,
				builder.getEquipmentSlots()
		);
		this.properties = builder;
	}

	@Override
	public int getMinLevel() {
		return properties.level_min;
	}

	@Override
	public int getMaxLevel() {
		return properties.level_max;
	}

	@Override
	public int getMinCost(int i) {
		if(properties.costMin == null)
			return super.getMinCost(i);
		try{
			return properties.costMin.apply(new MinimumCostCallbackJS(
					i,
					this
			));
		}catch(Exception e){
			KubeJS.LOGGER.error("Unable to apply min cost for enchantment " + this.properties.id + "!", e);
		}
		return super.getMinCost(i);
	}

	@Override
	public int getMaxCost(int i) {
		if(properties.costMax == null)
			return super.getMaxCost(i);
		try{
			return properties.costMax.apply(new MaximumCostCallbackJS(
					i,
					this
			));
		}catch(Exception e){
			KubeJS.LOGGER.error("Unable to apply max cost for enchantment " + this.properties.id + "!", e);
		}
		return super.getMaxCost(i);
	}

	@Override
	public int getDamageProtection(int i, DamageSource damageSource) {
		if(properties.damageProtection == null)
			return super.getDamageProtection(i, damageSource);
		try{
			if(damageSource.getEntity() == null && damageSource.getDirectEntity() == null){
				return super.getDamageProtection(i, damageSource);
			}else {
				return properties.damageProtection.apply(new DamageProtectionCallbackJS(
						i,
						damageSource,
						this
				));
			}
		}catch(Exception e){
			KubeJS.LOGGER.error("Unable to apply damage protection for enchantment " + this.properties.id + "!", e);
		}
		return super.getDamageProtection(i, damageSource);
	}

	@Override
	public float getDamageBonus(int i, MobType mobType) {
		if(properties.damageBonus == null)
			return super.getDamageBonus(i, mobType);
		try {
			return (properties.damageBonus.apply(new DamageBonusCallbackJS(
					i,
					mobType,
					this
					))
			).floatValue();
		}catch (Exception e){
			KubeJS.LOGGER.error("Unable to apply damage bonus for enchantment " + this.properties.id + "!", e);
		}
		return super.getDamageBonus(i, mobType);
	}

	@Override
	public boolean canEnchant(ItemStack itemStack) {
		if(properties.customEnchantCheck == null)
			return super.canEnchant(itemStack);
		try {
			return properties.customEnchantCheck.apply(new CustomEnchantCallbackJS(
					itemStack,
					this
			));
		}catch(Exception e){
			KubeJS.LOGGER.error("Unable to check if item can be enchanted with enchantment " + this.properties.id + "!", e);
		}
		return super.canEnchant(itemStack);
	}

	@Override
	public void doPostAttack(LivingEntity livingEntity, Entity entity, int i) {
		if (properties.postAttack == null) {
			super.doPostAttack(livingEntity, entity, i);
		}else{
			try {
				properties.postAttack.accept(new PostAttackCallbackJS(
						i,
						livingEntity,
						entity,
						this
				));
			} catch (Exception e) {
				KubeJS.LOGGER.error("Unable to apply post attack for enchantment " + this.properties.id + "!", e);
				super.doPostAttack(livingEntity, entity, i);
			}
		}
	}

	@Override
	public void doPostHurt(LivingEntity livingEntity, Entity entity, int i) {
		if(properties.postHurt == null) {
			super.doPostHurt(livingEntity, entity, i);
		}else {
			try {
				properties.postHurt.accept(new PostHurtCallbackJS(
						i,
						livingEntity,
						entity,
						this
				));
			} catch (Exception e) {
				KubeJS.LOGGER.error("Unable to apply post hurt for enchantment " + this.properties.id + "!", e);
				super.doPostHurt(livingEntity,entity,i);
			}
		}
	}

	@Override
	public boolean isTreasureOnly() {
		return properties.treasureOnly;
	}

	@Override
	public boolean isCurse() {
		return properties.curse;
	}

	@Override
	public boolean isTradeable() {
		return properties.tradeable;
	}

	@Override
	public boolean isDiscoverable() {
		return properties.discoverable;
	}

	@Override
	public Rarity getRarity() {
		return super.getRarity();
	}

	@Override
	protected boolean checkCompatibility(Enchantment enchantment) {
		return super.checkCompatibility(enchantment);
	}

	@Override
	protected String getOrCreateDescriptionId() {
		return super.getOrCreateDescriptionId();
	}

	@Override
	public String getDescriptionId() {
		return super.getDescriptionId();
	}

	@Override
	public Component getFullname(int i) {
		return super.getFullname(i);
	}
}
