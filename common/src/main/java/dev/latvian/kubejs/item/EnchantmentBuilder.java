package dev.latvian.kubejs.item;

import com.mojang.datafixers.util.Function3;
import dev.latvian.kubejs.bindings.EnchantmentCategoryWrapper;
import dev.latvian.kubejs.bindings.EnchantmentRarityWrapper;
import dev.latvian.kubejs.bindings.MobTypeWrapper;
import dev.latvian.kubejs.entity.DamageSourceJS;
import dev.latvian.kubejs.entity.EntityJS;
import dev.latvian.kubejs.entity.LivingEntityJS;
import dev.latvian.kubejs.util.BuilderBase;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.apache.logging.log4j.util.TriConsumer;

import java.util.ArrayList;
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
	public Function<Integer, Integer> costMin = null;
	public Function<Integer, Integer> costMax = null;
	public Function3<Integer, DamageSourceJS, EnchantmentJS, Integer> damageProtection = null;
	public Function3<Integer, MobTypeWrapper, EnchantmentJS, Double> damageBonus = null;
	public Function<ItemStack, Boolean> customEnchantCheck = null;

	// Functions that don't return anything.
	public TriConsumer<LivingEntityJS, EntityJS, Integer>  postAttack = null;
	public TriConsumer<LivingEntityJS, EntityJS, Integer>  postHurt = null;


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
	public EnchantmentBuilder equipSlot(EquipmentSlot slot) {
		equipmentSlots.add(slot);
		return this;
	}

	/**
	 * Sets the enchantment's rarity. See minecraft's Enchantment.Rarity enum for more info.
	 *
	 * @param rarityWrapper The rarity of the enchantment.
	 */
	public EnchantmentBuilder setRarity(EnchantmentRarityWrapper rarityWrapper){
		this.rarityWrapper = rarityWrapper;
		return this;
	}

	/**
	 * Sets the category of the enchantment.
	 * By default, used to check if an enchantment can be applied to an item.
	 * Further logic can be added in the customEnchantCheck function.
	 *
	 * @param categoryWrapper The category of the enchantment.
	 */
	public EnchantmentBuilder setCategory(EnchantmentCategoryWrapper categoryWrapper){
		this.categoryWrapper = categoryWrapper;
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
	 * Determines whether the enchantment can be discovered
	 */
	public EnchantmentBuilder notDiscoverable(){
		discoverable = false;
		return this;
	}

	/**
	 * Calculates the cost to enchant an item with the minimum enchantment level.
	 *
	 * @param costMin A function that takes in the level of the enchantment and
	 *                   NEEDS TO return the minimum experience level cost.
	 * @example In JavaScript this might be something like: (level) => { return level * 10; }
	 *          This example would make the enchantment cost 10 times the experience levels of the minimum enchantment level.
	 *
	 *          By default, the equation is calculated as (1 + level * 10).
	 *
	 * @implNote If your function does not return an integer, or throws an error
	 * it will log a KubeJS error to console and use the default function instead.
	 */
	public EnchantmentBuilder setCostMin(Function<Integer, Integer> costMin) {
		this.costMin = costMin;
		return this;
	}

	/**
	 * Calculates the cost to enchant an item with the maximum enchantment level.
	 *
	 * @param costMax A function that takes in the level of the enchantment and
	 *                NEEDS TO return the maximum experience level cost.
	 * @example In JavaScript this might be something like:
	 * 			(level) => {
	 * 				return level * 10;
	 * 			}
	 *          This example would make the enchantment cost 10 times the experience levels
	 *          of the minimum enchantment level.
	 *
	 *			By default, the equation is calculated by using the setCostMin function and adding 5.
	 *		    If the setCostMin function is not provided, or null the default will be used.
	 *
	 * @implNote If your function does not return an integer, or throws an error during execution
	 * it will log a KubeJS error to console and use the default function instead.
	 */
	public EnchantmentBuilder setCostMax(Function<Integer, Integer> costMax) {
		this.costMax = costMax;
		return this;
	}

	/**
	 * Calculates how much of a damage protection bonus is shown when you
	 * hover over the equipment in your inventory as well as whenever you
	 * take damage from an entity.
	 *
	 * @param damageProtection A function that takes in three parameters:
	 *                         		1. Level of the enchantment.
	 *                         		2. The DamageSourceJS.
	 *                         		3. The EnchantmentJS instance.
	 *                         It needs to return:
	 *                         		- An Integer number of how much damage protection is given.
	 * @example In JavaScript this might be something like:
	 * 			(level, damageSource, enchantment) => {
	 * 				return level+1;
	 * 			}
	 *
	 *
	 * @implNote If your function does not return an integer, or throws an error during execution
	 * it will log a KubeJS error to console and use the default function instead.
	 */
	public EnchantmentBuilder setDamageProtection(Function3<Integer, DamageSourceJS, EnchantmentJS, Integer> damageProtection) {
		this.damageProtection = damageProtection;
		return this;
	}

	/**
	 * Calculates how much of a damage bonus is shown when you
	 * hover over the item in your inventory as well as whenever you
	 * damage an entity.
	 *
	 * @param damageBonus A function that takes in three parameters:
	 *                         1. Level of the enchantment.
	 *                         2. The MobTypeWrapper. Could be used to determine the type of mob and apply a bonus.
	 *                         3. The EnchantmentJS instance.
	 *                    It needs to return:
	 *                         - An Double number of how much damage bonus is given.
	 * @example In JavaScript this might be something like:
	 * 			(level, mobtype, enchantment) => {
	 * 				return level+1;
	 * 			}
	 * @implNote All doubles will be truncated into a float,
	 * due to implementation issues. Slight calculation errors may occur as a result.
	 *
	 * @implNote If your function does not return a Double, or throws an error during execution
	 * it will log a KubeJS error to console and use the default function instead.
	 */
	public EnchantmentBuilder setDamageBonus(Function3<Integer, MobTypeWrapper, EnchantmentJS, Double> damageBonus) {
		this.damageBonus = damageBonus;
		return this;
	}

	/**
     * Once damage has been applied to a target, you can perform some logic
	 * on either the target or the attacker.
     *
     * @param postAttack A function that takes in three parameters:
     *                         1. The living entity attacking, aka the attacker with the enchantment.
     *                         2. The Entity being attacked, aka the target.
     *                         3. The level of the enchantment.
	 *                   It does not need to return anything.
     * @example In JavaScript this might be something like:
     * 			(attacker, target, level) => {
     * 				attacker.heal(level);
	 * 			    target.kill();
     * 			}
     *
     * @implNote If your function throws an error during execution,
     * it will log a KubeJS error to console and use the default function instead.
     */
	public EnchantmentBuilder setPostAttack(TriConsumer<LivingEntityJS, EntityJS, Integer> postAttack) {
		this.postAttack = postAttack;
		return this;
	}

	/**
	 * Once damage has been applied to someone with this enchantment, you can perform some logic
	 * on either the enchantment holder or the attacker.
	 *
	 * @param postHurt  A function that takes in three parameters:
	 *                         1. The living entity who was attacked, the enchantment holder.
	 *                         2. The Entity who attacked the enchantment holder.
	 *                         3. The level of the enchantment.
	 *                   It does not need to return anything.
	 * @example In JavaScript this might be something like:
	 * 			(attacked, attacker, level) => {
	 * 				attacker.kill();
	 * 			    attacked.heal(level);
	 * 			}
	 *
	 * @implNote If your function throws an error during execution,
	 * it will log a KubeJS error to console and use the default function instead.
	 */
	public EnchantmentBuilder setPostHurt(TriConsumer<LivingEntityJS, EntityJS, Integer> postHurt) {
		this.postHurt = postHurt;
		return this;
	}

	/**
	 * This method will be called anytime the game tries to check if an enchantment
	 * can be applied to an item. It will ALSO call the default method of this function as well.
	 * So if the item is part of the enchantment category you specify, it will ignore this.
	 *
	 *
	 * @param customEnchantCheck A function that takes in three parameters:
	 *                         		1. The living entity who was attacked, the enchantment holder.
	 *                         		2. The Entity who attacked the enchantment holder.
	 *                         		3. The level of the enchantment.
	 *                   		It needs to return:
	 *                   	     	- A boolean, true if the enchantment can be applied to
	 *                   	     	the target, false otherwise.
	 * @example In JavaScript this might be something like:
	 * 			(item) => {
	 * 				return item.id == 'minecraft:apple';
	 * 			}
	 *
	 * @implNote If your function does not return a boolean, or throws an error during execution,
	 * it will log a KubeJS error to console and use the default function instead.
	 */
	public EnchantmentBuilder setCustomEnchantCheck(Function<ItemStack, Boolean> customEnchantCheck) {
		this.customEnchantCheck = customEnchantCheck;
		return this;
	}

	@Override
	public String getBuilderType() {
		return "enchantment";
	}
}