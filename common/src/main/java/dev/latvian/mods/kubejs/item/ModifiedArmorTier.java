package dev.latvian.mods.kubejs.item;

import dev.latvian.mods.kubejs.item.ingredient.IngredientJS;
import dev.latvian.mods.rhino.util.RemapForJS;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Optional;

/**
 * @author LatvianModder
 */
public class ModifiedArmorTier implements ArmorMaterial {
	private static final int[] HEALTH_PER_SLOT = new int[]{13, 15, 16, 11};

	public final ArmorMaterial parent;
	private int durabilityMultiplier;
	private int[] slotProtections;
	private int enchantmentValue;
	private SoundEvent sound;
	private float toughness;
	private float knockbackResistance;
	private Optional<Ingredient> repairIngredient;
	private String name;

	public ModifiedArmorTier(String id, ArmorMaterial p) {
		parent = p;
		enchantmentValue = p.getEnchantmentValue();
		sound = p.getEquipSound();
		repairIngredient = Optional.empty();
		toughness = p.getToughness();
		knockbackResistance = p.getKnockbackResistance();
		name = id;
	}

	@Override
	public int getDurabilityForSlot(EquipmentSlot equipmentSlot) {
		return durabilityMultiplier == 0 ? parent.getDurabilityForSlot(equipmentSlot) : HEALTH_PER_SLOT[equipmentSlot.getIndex()] * durabilityMultiplier;
	}

	public void setDurabilityMultiplier(int m) {
		durabilityMultiplier = m;
	}

	@Override
	public int getDefenseForSlot(EquipmentSlot equipmentSlot) {
		return slotProtections == null ? parent.getDefenseForSlot(equipmentSlot) : slotProtections[equipmentSlot.getIndex()];
	}

	public void setSlotProtections(int[] p) {
		slotProtections = p;
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
	@RemapForJS("getEquipSound")
	public SoundEvent getEquipSound() {
		return sound;
	}

	public void setEquipSound(SoundEvent e) {
		sound = e;
	}

	@Override
	@RemapForJS("getVanillaRepairIngredient")
	public Ingredient getRepairIngredient() {
		return repairIngredient.orElse(parent.getRepairIngredient());
	}

	public void setRepairIngredient(IngredientJS in) {
		repairIngredient = Optional.of(in.createVanillaIngredient());
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	@RemapForJS("getToughness")
	public float getToughness() {
		return toughness;
	}

	public void setToughness(float f) {
		toughness = f;
	}

	@Override
	@RemapForJS("getKnockbackResistance")
	public float getKnockbackResistance() {
		return knockbackResistance;
	}

	public void setKnockbackResistance(float f) {
		knockbackResistance = f;
	}
}
