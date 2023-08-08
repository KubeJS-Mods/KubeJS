package dev.latvian.mods.kubejs.item;

import dev.latvian.mods.rhino.util.RemapForJS;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

public class MutableArmorTier implements ArmorMaterial {
	private static final int[] HEALTH_PER_SLOT = new int[]{13, 15, 16, 11};

	public final ArmorMaterial parent;
	private int durabilityMultiplier;
	private int[] slotProtections;
	private int enchantmentValue;
	private SoundEvent sound;
	private float toughness;
	private float knockbackResistance;
	private Ingredient repairIngredient;
	private String name;

	public MutableArmorTier(String id, ArmorMaterial p) {
		parent = p;
		enchantmentValue = p.getEnchantmentValue();
		sound = p.getEquipSound();
		repairIngredient = parent.getRepairIngredient();
		toughness = p.getToughness();
		knockbackResistance = p.getKnockbackResistance();
		name = id;
	}

	@Override
	public int getDurabilityForType(ArmorItem.Type equipmentSlot) {
		return durabilityMultiplier == 0 ? parent.getDurabilityForType(equipmentSlot) : HEALTH_PER_SLOT[equipmentSlot.getSlot().getIndex()] * durabilityMultiplier;
	}

	public void setDurabilityMultiplier(int m) {
		durabilityMultiplier = m;
	}

	@Override
	public int getDefenseForType(ArmorItem.Type equipmentSlot) {
		return slotProtections == null ? parent.getDefenseForType(equipmentSlot) : slotProtections[equipmentSlot.getSlot().getIndex()];
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
		return repairIngredient;
	}

	public void setRepairIngredient(Ingredient in) {
		repairIngredient = in;
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
