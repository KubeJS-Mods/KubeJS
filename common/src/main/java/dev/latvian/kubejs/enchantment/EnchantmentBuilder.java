package dev.latvian.kubejs.enchantment;

import com.mojang.datafixers.util.Function3;
import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.bindings.EnchantmentCategoryWrapper;
import dev.latvian.kubejs.bindings.EnchantmentRarityWrapper;
import dev.latvian.kubejs.bindings.MobTypeWrapper;
import dev.latvian.kubejs.entity.DamageSourceJS;
import dev.latvian.kubejs.entity.EntityJS;
import dev.latvian.kubejs.entity.LivingEntityJS;
import dev.latvian.kubejs.util.BuilderBase;
import dev.latvian.kubejs.util.ListJS;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.apache.logging.log4j.util.TriConsumer;

import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author ILIKEPIEFOO2
 */
public class EnchantmentBuilder extends BuilderBase {
	private ArrayList<EquipmentSlot> equipmentSlots = new ArrayList<>();

	public int level_min = 1;
	public int level_max = 1;
	public boolean treasureOnly = false;
	public boolean curse = false;
	public boolean tradeable = true;
	public boolean discoverable = true;
	public EnchantmentRarityWrapper rarityWrapper = EnchantmentRarityWrapper.COMMON;
	public EnchantmentCategoryWrapper categoryWrapper = EnchantmentCategoryWrapper.BREAKABLE;


	// Functions that return something.
	public Function<MinimumCostCallbackJS, Integer> costMin = null;
	public Function<MaximumCostCallbackJS, Integer> costMax = null;
	public Function<DamageProtectionCallbackJS, Integer> damageProtection = null;
	public Function<DamageBonusCallbackJS, Double> damageBonus = null;
	public Function<CustomEnchantCallbackJS, Boolean> customEnchantCheck = null;

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
		} else {
			KubeJS.LOGGER.warn("Invalid equipment slot was provided: " + slot);
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
		level_min = level;
		level_max = (level_max < level_min) ? level : level_max;
		return this;
	}

	/**
	 * Sets the Maximum level of the enchantment.
	 * Minimum level will be modified if it happens to be higher.
	 *
	 * @param level	The maximum level of the enchantment.
	 */
	public EnchantmentBuilder setMaxLevel(int level){
		level_max = level;
		level_min = (level_max < level_min) ? level : level_min;
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
	 * @param costMin A function that needs to return the cost (in enchanting levels) to enchant an
	 *                   item with the minimum enchantment level as an Integer.
	 *
	 * @implNote If your function does not return an integer, or throws an error
	 * it will log a KubeJS error to console and use the default function instead.
	 */
	public EnchantmentBuilder setCostMin(Function<MinimumCostCallbackJS, Integer> costMin) {
		this.costMin = costMin;
		return this;
	}

	/**
	 * Calculates the maximum cost to enchant an item provided it's level.
	 * By default, the equation is calculated by using the setCostMin function and adding 5.
	 *
	 * @param costMax A function that needs to return the cost (in enchanting levels) to enchant an
	 * 	 				item with the maximum enchantment level as an Integer.
	 *
	 * @implNote If your function does not return an integer, or throws an error during execution
	 * it will log a KubeJS error to console and use the default function instead.
	 */
	public EnchantmentBuilder setCostMax(Function<MaximumCostCallbackJS, Integer> costMax) {
		this.costMax = costMax;
		return this;
	}

	/**
	 * Calculates how much of a damage protection bonus is shown and provided when you
	 * hover over the equipment in your inventory as well as whenever you take damage from an entity.
	 *
	 * @param damageProtection A function that needs to return the damage protection bonus as an Integer.
	 *
	 * @implNote If your function does not return an integer, or throws an error during execution
	 * it will log a KubeJS error to console and use the default function instead.
	 */
	public EnchantmentBuilder setDamageProtection(Function<DamageProtectionCallbackJS, Integer> damageProtection) {
		this.damageProtection = damageProtection;
		return this;
	}

	/**
	 * Calculates how much of a damage bonus is shown when you
	 * hover over the item in your inventory as well as whenever you
	 * damage an entity.
	 *
	 * @param damageBonus A function that needs to return a damage bonus as a double.
	 *
	 * @implNote All doubles will be truncated into a float,
	 * due to implementation issues. Slight calculation errors may occur as a result.
	 *
	 * @implNote If your function does not return a Double, or throws an error during execution
	 * it will log a KubeJS error to console and use the default function instead.
	 */
	public EnchantmentBuilder setDamageBonus(Function<DamageBonusCallbackJS, Double> damageBonus) {
		this.damageBonus = damageBonus;
		return this;
	}

	/**
     * Once damage has been applied to a target, you can perform some logic.
     *
     * @param postAttack A function that will be called after the attack has been applied.
     *
     * @implNote If your function throws an error during execution,
     * it will log a KubeJS error to console and use the default function instead.
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
	 * @implNote If your function throws an error during execution,
	 * it will log a KubeJS error to console and use the default function instead.
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
	 * @param customEnchantCheck A function that checks if an item can be enchanted, needs to return boolean.
	 *
	 * @implNote If your function does not return a boolean, or throws an error during execution,
	 * it will log a KubeJS error to console and use the default function instead.
	 */
	public EnchantmentBuilder setCustomEnchantCheck(Function<CustomEnchantCallbackJS, Boolean> customEnchantCheck) {
		this.customEnchantCheck = customEnchantCheck;
		return this;
	}

	@Override
	public String getBuilderType() {
		return "enchantment";
	}
}