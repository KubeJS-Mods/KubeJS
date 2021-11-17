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

public class EnchantmentBuilder extends BuilderBase {
	private ArrayList<EquipmentSlot> equipmentSlots = new ArrayList<>();

	public EnchantmentRarityWrapper rarityWrapper = EnchantmentRarityWrapper.COMMON;
	public EnchantmentCategoryWrapper categoryWrapper = EnchantmentCategoryWrapper.BREAKABLE;
	public int level_min = 1;
	public int level_max = 1;
	public boolean treasureOnly = false;
	public boolean curse = false;
	public boolean tradeable = true;
	public boolean discoverable = true;

	public Function<Integer, Integer> costMin = null;
	public Function<Integer, Integer> costMax = null;

	public Function3<Integer, DamageSourceJS, EnchantmentJS, Integer> damageProtection = null;
	public Function3<Integer, MobTypeWrapper, EnchantmentJS, Double> damageBonus = null;

	public TriConsumer<LivingEntityJS, EntityJS, Integer>  postAttack = null;
	public TriConsumer<LivingEntityJS, EntityJS, Integer>  postHurt = null;

	public Function<ItemStack, Boolean> customEnchantCheck = null;

	public transient Enchantment enchantment;

	public EnchantmentBuilder(String s) {
		super(s);
	}

	@Override
	public String getBuilderType() {
		return "enchantment";
	}

	public EquipmentSlot[] getEquipmentSlots(){
		EquipmentSlot[] slots = new EquipmentSlot[equipmentSlots.size()];
		equipmentSlots.toArray(slots);
		return slots;
	}

	public EnchantmentBuilder equipSlot(EquipmentSlot slot) {
		equipmentSlots.add(slot);
		return this;
	}

	public EnchantmentBuilder setRarity(EnchantmentRarityWrapper rarityWrapper){
		this.rarityWrapper = rarityWrapper;
		return this;
	}

	public EnchantmentBuilder setCategory(EnchantmentCategoryWrapper categoryWrapper){
		this.categoryWrapper = categoryWrapper;
		return this;
	}

	public EnchantmentBuilder setMinLevel(int level){
		level_min = level;
		level_max = (level_max < level_min) ? level : level_max;
		return this;
	}

	public EnchantmentBuilder setMaxLevel(int level){
		level_max = level;
		level_min = (level_max < level_min) ? level : level_min;
		return this;
	}

	public EnchantmentBuilder treasureOnly(){
		treasureOnly = true;
		return this;
	}

	public EnchantmentBuilder cursed() {
		curse = true;
		return this;
	}

	public EnchantmentBuilder notTradeable(){
		tradeable = false;
		return this;
	}

	public EnchantmentBuilder notDiscoverable(){
		discoverable = false;
		return this;
	}

	public EnchantmentBuilder setCostMin(Function<Integer, Integer> costMin) {
		this.costMin = costMin;
		return this;
	}

	public EnchantmentBuilder setCostMax(Function<Integer, Integer> costMax) {
		this.costMax = costMax;
		return this;
	}

	public EnchantmentBuilder setDamageProtection(Function3<Integer, DamageSourceJS, EnchantmentJS, Integer> damageProtection) {
		this.damageProtection = damageProtection;
		return this;
	}

	public EnchantmentBuilder setDamageBonus(Function3<Integer, MobTypeWrapper, EnchantmentJS, Double> damageBonus) {
		this.damageBonus = damageBonus;
		return this;
	}

	public EnchantmentBuilder setPostAttack(TriConsumer<LivingEntityJS, EntityJS, Integer> postAttack) {
		this.postAttack = postAttack;
		return this;
	}

	public EnchantmentBuilder setPostHurt(TriConsumer<LivingEntityJS, EntityJS, Integer> postHurt) {
		this.postHurt = postHurt;
		return this;
	}

	public EnchantmentBuilder setCustomEnchantCheck(Function<ItemStack, Boolean> customEnchantCheck) {
		this.customEnchantCheck = customEnchantCheck;
		return this;
	}
}