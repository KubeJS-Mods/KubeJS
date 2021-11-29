package dev.latvian.mods.kubejs.item;

import dev.latvian.mods.kubejs.item.ingredient.IngredientJS;
import dev.latvian.mods.rhino.util.RemapForJS;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Optional;

/**
 * @author LatvianModder
 */
public class ModifiedToolTier implements Tier {
	public final Tier parent;
	private int uses;
	private float speed;
	private float attackDamageBonus;
	private int level;
	private int enchantmentValue;
	private Optional<Ingredient> repairIngredient;

	public ModifiedToolTier(Tier p) {
		parent = p;
		uses = parent.getUses();
		speed = parent.getSpeed();
		attackDamageBonus = parent.getAttackDamageBonus();
		level = parent.getLevel();
		enchantmentValue = parent.getEnchantmentValue();
		repairIngredient = Optional.empty();
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

	public void setAttackDamageBonus(float f) {
		attackDamageBonus = f;
	}

	@Override
	@RemapForJS("getLevel")
	public int getLevel() {
		return level;
	}

	public void setLevel(int i) {
		level = i;
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
		return repairIngredient.orElse(parent.getRepairIngredient());
	}

	public void setRepairIngredient(IngredientJS in) {
		repairIngredient = Optional.of(in.createVanillaIngredient());
	}
}
