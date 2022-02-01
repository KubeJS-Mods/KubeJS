package dev.latvian.mods.kubejs.enchantment;


import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.bindings.EnchantmentCategoryWrapper;
import dev.latvian.mods.kubejs.bindings.EnchantmentRarityWrapper;
import dev.latvian.mods.kubejs.util.BuilderBase;
import dev.latvian.mods.kubejs.util.ListJS;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author ILIKEPIEFOO2
 */
public class EnchantmentBuilder extends BuilderBase {
	private ArrayList<EquipmentSlot> equipmentSlots = new ArrayList<>();

	public int levelMin = 1;
	public int levelMax = 1;
	public boolean treasureOnly = false;
	public boolean curse = false;
	public boolean tradeable = true;
	public boolean discoverable = true;
	public EnchantmentRarityWrapper rarityWrapper = EnchantmentRarityWrapper.COMMON;
	public EnchantmentCategoryWrapper categoryWrapper = EnchantmentCategoryWrapper.BREAKABLE;


	// Functions with setters.
	public Consumer<MinimumCostCallbackJS> costMin = null;
	public Consumer<MaximumCostCallbackJS> costMax = null;
	public Consumer<DamageProtectionCallbackJS> damageProtection = null;
	public Consumer<DamageBonusCallbackJS> damageBonus = null;
	public Consumer<CustomEnchantCallbackJS> customEnchantCheck = null;

	// Functions that don't return anything.
	public Consumer<PostAttackCallbackJS> postAttack = null;
	public Consumer<PostHurtCallbackJS>  postHurt = null;


	public transient Enchantment enchantment;

	public EnchantmentBuilder(String s) {
		super(s);
	}


	public EquipmentSlot[] getEquipmentSlots(){
		EquipmentSlot[] slots = new EquipmentSlot[equipmentSlots.size()];
		equipmentSlots.toArray(slots);
		return slots;
	}

	/**
	 * Tells the enchantment to look on a slot for a specific enchantment.
	 * Used to tell if a player has a specific enchantment.
	 *
	 * @param slot The Equipment slot for the enchantment to look on a player for.
	 */
	public EnchantmentBuilder equipSlot(Object slot) {
		if (slot instanceof EquipmentSlot) {
			equipmentSlots.add((EquipmentSlot) slot);
		}else if(slot instanceof String) {
			equipmentSlots.add(EquipmentSlot.valueOf((String) slot));
		}else if(slot instanceof ListJS) {
			((ListJS) slot).forEach(this::equipSlot);
		} else if(slot instanceof List<?>) {
			((List<?>) slot).forEach(this::equipSlot);
		}
		else {
			KubeJS.LOGGER.warn("Invalid equipment slot was provided: \"" + slot + "\", Type: "+ slot.getClass().getName());
		}
		return this;
	}

	/**
	 * Sets the enchantment's rarity. See minecraft's Enchantment.Rarity enum for more info.
	 *
	 * @param rarityWrapper The rarity of the enchantment.
	 */
	public EnchantmentBuilder setRarity(Object rarityWrapper){
		if(rarityWrapper instanceof EnchantmentRarityWrapper) {
			this.rarityWrapper = (EnchantmentRarityWrapper) rarityWrapper;
		}else if(rarityWrapper instanceof String) {
			this.rarityWrapper = EnchantmentRarityWrapper.fromString((String) rarityWrapper);
		} else {
			KubeJS.LOGGER.warn("Invalid enchantment rarity was provided: " + rarityWrapper);
		}
		return this;
	}

	/**
	 * Sets the category of the enchantment.
	 * By default, used to check if an enchantment can be applied to an item.
	 * Further logic can be added in the customEnchantCheck function.
	 * (You can test if this works with the /enchant command since it uses
	 * this and the customEnchantCheck and will tell you if it can be enchanted normally.)
	 *
	 * @param categoryWrapper The category of the enchantment.
	 */
	public EnchantmentBuilder setCategory(Object categoryWrapper){
		if(categoryWrapper instanceof EnchantmentCategoryWrapper) {
			this.categoryWrapper = (EnchantmentCategoryWrapper) categoryWrapper;
		}else if(categoryWrapper instanceof String) {
			this.categoryWrapper = EnchantmentCategoryWrapper.fromString((String) categoryWrapper);
		} else {
			KubeJS.LOGGER.error("Invalid category type for enchantment. Object \"" + categoryWrapper.getClass().getName() + "\" is not a valid category type.");
		}
		return this;
	}

	/**
	 * Sets the minimum level of the enchantment. (As well as the max if it happens to be lower.
	 * (Not sure why you would change this to anything besides 1, but you can.)
	 *
	 * @param level	The minimum level of the enchantment.
	 *
	 */
	public EnchantmentBuilder setMinLevel(int level){
		levelMin = level;
		levelMax = (levelMax < levelMin) ? level : levelMax;
		return this;
	}

	/**
	 * Sets the Maximum level of the enchantment.
	 * Minimum level will be modified if it happens to be higher.
	 *
	 * @param level	The maximum level of the enchantment.
	 */
	public EnchantmentBuilder setMaxLevel(int level){
		levelMax = level;
		levelMin = (levelMax < levelMin) ? level : levelMin;
		return this;
	}

	/**
	 * Determines whether the enchantment is considered a 'treasure enchantment'.
	 * Or in other words, this enchantment should only be found in treasure chests.
	 *
	 * If you don't want this enchantment to be obtainable from an enchantment table,
	 * use this method.
	 */
	public EnchantmentBuilder treasureOnly(){
		treasureOnly = true;
		return this;
	}

	/**
	 * Determines whether the enchantment is considered a curse.
	 */
	public EnchantmentBuilder cursed() {
		curse = true;
		return this;
	}

	/**
	 * Determines whether the enchantment can be traded for.
	 */
	public EnchantmentBuilder notTradeable(){
		tradeable = false;
		return this;
	}

	/**
	 * Determines whether the enchantment can be discovered?
	 * I have no clue what this means. For some reason
	 * Soul speed is the only vanilla enchantment that is not discoverable.
	 * Do what you will with this.
	 */
	public EnchantmentBuilder notDiscoverable(){
		discoverable = false;
		return this;
	}

	/**
	 * Calculates the minimum cost to enchant an item provided it's level.
	 *
	 * By default, the equation is calculated as (1 + level * 10).
	 *
	 * @implNote If your function throws an error it will log a KubeJS error to
	 * console and use the default function instead.
	 */
	public EnchantmentBuilder setCostMin(Consumer<MinimumCostCallbackJS> costMin) {
		this.costMin = costMin;
		return this;
	}

	/**
	 * Calculates the maximum cost to enchant an item provided it's level.
	 * By default, the equation is calculated by using the setCostMin function and adding 5.
	 *
	 *
	 * @implNote If your function throws an error it will log a KubeJS error to
	 * console and use the default function instead.
	 */
	public EnchantmentBuilder setCostMax(Consumer<MaximumCostCallbackJS> costMax) {
		this.costMax = costMax;
		return this;
	}

	/**
	 * Calculates how much of a damage protection bonus is shown and provided when you
	 * hover over the equipment in your inventory as well as whenever you take damage from an entity.
	 *
	 * @implNote If your function throws an error it will log a KubeJS error to
	 * console and use the default function instead.
	 */
	public EnchantmentBuilder setDamageProtection(Consumer<DamageProtectionCallbackJS> damageProtection) {
		this.damageProtection = damageProtection;
		return this;
	}

	/**
	 * Calculates how much of a damage bonus is shown when you
	 * hover over the item in your inventory as well as whenever you
	 * damage an entity.
	 *
	 * @implNote If your function throws an error it will log a KubeJS error to
	 * console and use the default function instead.
	 */
	public EnchantmentBuilder setDamageBonus(Consumer<DamageBonusCallbackJS> damageBonus) {
		this.damageBonus = damageBonus;
		return this;
	}

	/**
     * Once damage has been applied to a target, you can perform some logic.
     *
	 * @implNote If your function throws an error it will log a KubeJS error to
	 * console and use the default function instead.
     */
	public EnchantmentBuilder setPostAttack(Consumer<PostAttackCallbackJS> postAttack) {
		this.postAttack = postAttack;
		return this;
	}

	/**
	 * Once damage has been applied to someone with this enchantment, you can perform some logic.
	 *
	 * @param postHurt  A function that will be called after the attack has been applied.
	 *
	 * @implNote If your function throws an error it will log a KubeJS error to
	 * console and use the default function instead.
	 */
	public EnchantmentBuilder setPostHurt(Consumer<PostHurtCallbackJS> postHurt) {
		this.postHurt = postHurt;
		return this;
	}

	/**
	 * This method will be called anytime the game tries to check if an enchantment
	 * can be applied to an item. It will ALSO call the default method of this function as well.
	 * So if the item is part of the enchantment category you specify, it will ignore this.
	 *
	 *
	 * @implNote If your function throws an error it will log a KubeJS error to
	 * console and use the default function instead.
	 */
	public EnchantmentBuilder setCustomEnchantCheck(Consumer<CustomEnchantCallbackJS> customEnchantCheck) {
		this.customEnchantCheck = customEnchantCheck;
		return this;
	}

	@Override
	public String getBuilderType() {
		return "enchantment";
	}
}