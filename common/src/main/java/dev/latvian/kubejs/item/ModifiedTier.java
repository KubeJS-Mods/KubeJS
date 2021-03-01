package dev.latvian.kubejs.item;

import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.mods.rhino.util.RemapForJS;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;

/**
 * @author LatvianModder
 */
public class ModifiedTier implements Tier {
	public final Tier parent;
	private OptionalInt uses = OptionalInt.empty();
	private OptionalDouble speed = OptionalDouble.empty();
	private OptionalDouble attackDamageBonus = OptionalDouble.empty();
	private OptionalInt level = OptionalInt.empty();
	private OptionalInt enchantmentValue = OptionalInt.empty();
	private Optional<Ingredient> repairIngredient = Optional.empty();

	public ModifiedTier(Tier p) {
		parent = p;
	}

	@Override
	@RemapForJS("getUses")
	public int getUses() {
		return uses.orElse(parent.getUses());
	}

	public void setUses(int i) {
		uses = OptionalInt.of(i);
	}

	@Override
	@RemapForJS("getSpeed")
	public float getSpeed() {
		return (float) speed.orElse(parent.getSpeed());
	}

	public void setSpeed(float f) {
		speed = OptionalDouble.of(f);
	}

	@Override
	@RemapForJS("getAttackDamageBonus")
	public float getAttackDamageBonus() {
		return (float) attackDamageBonus.orElse(parent.getAttackDamageBonus());
	}

	public void setAttackDamageBonus(float f) {
		attackDamageBonus = OptionalDouble.of(f);
	}

	@Override
	@RemapForJS("getLevel")
	public int getLevel() {
		return level.orElse(parent.getLevel());
	}

	public void setLevel(int i) {
		level = OptionalInt.of(i);
	}

	@Override
	@RemapForJS("getEnchantmentValue")
	public int getEnchantmentValue() {
		return enchantmentValue.orElse(parent.getEnchantmentValue());
	}

	public void setEnchantmentValue(int i) {
		enchantmentValue = OptionalInt.of(i);
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
