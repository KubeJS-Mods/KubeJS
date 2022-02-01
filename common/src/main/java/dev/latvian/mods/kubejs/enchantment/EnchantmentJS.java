package dev.latvian.mods.kubejs.enchantment;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.bindings.EnchantmentCategoryWrapper;
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
		return properties.levelMin;
	}

	@Override
	public int getMaxLevel() {
		return properties.levelMax;
	}

	@Override
	public int getMinCost(int i) {
		if(properties.costMin == null)
			return super.getMinCost(i);
		try{
			MinimumCostCallbackJS callback = new MinimumCostCallbackJS(i, this);
			properties.costMin.accept(callback);
			return callback.getCost();
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
			MaximumCostCallbackJS callback = new MaximumCostCallbackJS(i, this);
			properties.costMax.accept(callback);
			return callback.getCost();
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
			DamageProtectionCallbackJS callback = new DamageProtectionCallbackJS(i, damageSource, this);
			properties.damageProtection.accept(callback);
			return callback.getBonus();
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
			DamageBonusCallbackJS callback = new DamageBonusCallbackJS(i, mobType, this);
			properties.damageBonus.accept(callback);
			return callback.getBonus();
		}catch (Exception e){
			KubeJS.LOGGER.error("Unable to apply damage bonus for enchantment " + this.properties.id + "!", e);
		}
		return super.getDamageBonus(i, mobType);
	}

	@Override
	public boolean canEnchant(ItemStack itemStack) {
		if(properties.customEnchantCheck != null) {
			try {
				CustomEnchantCallbackJS callback = new CustomEnchantCallbackJS(itemStack, this);
				properties.customEnchantCheck.accept(callback);
				// Check if the custom logic returned true or if the enchantment is allowed by default.
				// Prevent null pointer exception in the case of Custom Category.
				if(properties.categoryWrapper != EnchantmentCategoryWrapper.CUSTOM)
					return callback.canEnchant() || super.canEnchant(itemStack);
				return callback.canEnchant();
			}catch(Exception e){
				KubeJS.LOGGER.error("Unable to check if item can be enchanted with enchantment " + this.properties.id + "!", e);
			}
		}
		// Prevent null pointer exception in the case of Custom Category.
		if (properties.categoryWrapper != EnchantmentCategoryWrapper.CUSTOM)
			return super.canEnchant(itemStack);
		return false;
	}

	@Override
	public void doPostAttack(LivingEntity livingEntity, Entity entity, int i) {
		if (properties.postAttack == null) {
			super.doPostAttack(livingEntity, entity, i);
		}else{
			try {
				PostAttackCallbackJS callback = new PostAttackCallbackJS(livingEntity, entity, i, this);
				properties.postAttack.accept(callback);
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
				PostHurtCallbackJS callback = new PostHurtCallbackJS(livingEntity, entity, i, this);
				properties.postHurt.accept(callback);
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
