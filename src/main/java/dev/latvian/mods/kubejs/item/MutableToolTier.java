package dev.latvian.mods.kubejs.item;

import dev.latvian.mods.rhino.util.RemapForJS;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

public class MutableToolTier implements Tier {
	public final Tier parent;
	private int uses;
	private float speed;
	private float attackDamageBonus;
	private TagKey<Block> incorrectBlocksForDrops;
	private int enchantmentValue;
	private Ingredient repairIngredient;

	public MutableToolTier(Tier p) {
		parent = p;
		uses = parent.getUses();
		speed = parent.getSpeed();
		attackDamageBonus = parent.getAttackDamageBonus();
		incorrectBlocksForDrops = parent.getIncorrectBlocksForDrops();
		enchantmentValue = parent.getEnchantmentValue();
		repairIngredient = parent.getRepairIngredient();
	}

	@Override
	@RemapForJS("getUses")
	public int getUses() {
		return uses;
	}

	public void setUses(int i) {
		uses = i;
	}

	@Override
	@RemapForJS("getSpeed")
	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float f) {
		speed = f;
	}

	@Override
	@RemapForJS("getAttackDamageBonus")
	public float getAttackDamageBonus() {
		return attackDamageBonus;
	}

	public void setIncorrectBlocksForDropsTag(ResourceLocation tag) {
		incorrectBlocksForDrops = BlockTags.create(tag);
	}

	public ResourceLocation getIncorrectBlocksForDropsTag() {
		return incorrectBlocksForDrops.location();
	}

	@Override
	public TagKey<Block> getIncorrectBlocksForDrops() {
		return incorrectBlocksForDrops;
	}

	public void setAttackDamageBonus(float f) {
		attackDamageBonus = f;
	}

	@Override
	@RemapForJS("getEnchantmentValue")
	public int getEnchantmentValue() {
		return enchantmentValue;
	}

	public void setEnchantmentValue(int i) {
		enchantmentValue = i;
	}

	@Override
	@RemapForJS("getVanillaRepairIngredient")
	public Ingredient getRepairIngredient() {
		return repairIngredient;
	}

	public void setRepairIngredient(Ingredient in) {
		repairIngredient = in;
	}
}
