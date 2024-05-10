package dev.latvian.mods.kubejs.item;

import dev.latvian.mods.kubejs.registry.BuilderBase;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.kubejs.util.UtilsJS;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class ArmorMaterialBuilder extends BuilderBase<ArmorMaterial> {
	public transient Map<ArmorItem.Type, Integer> defense;
	public transient int enchantmentValue;
	public transient Holder<SoundEvent> equipSound;
	public transient Supplier<Ingredient> repairIngredient;
	public transient List<ArmorMaterial.Layer> layers;
	public transient float toughness;
	public transient float knockbackResistance;

	public ArmorMaterialBuilder(ResourceLocation i) {
		super(i);
		defense = Map.of();
		enchantmentValue = 9;
		equipSound = null;
		repairIngredient = null;
		layers = null;
		toughness = 0F;
		knockbackResistance = 0F;
	}

	@Override
	public RegistryInfo getRegistryType() {
		return RegistryInfo.ARMOR_MATERIAL;
	}

	@Override
	public ArmorMaterial createObject() {
		return new ArmorMaterial(
			defense == null ? Map.of(
				ArmorItem.Type.BOOTS, 2,
				ArmorItem.Type.LEGGINGS, 5,
				ArmorItem.Type.CHESTPLATE, 6,
				ArmorItem.Type.HELMET, 2,
				ArmorItem.Type.BODY, 5
			) : defense,
			enchantmentValue,
			equipSound == null ? SoundEvents.ARMOR_EQUIP_IRON : equipSound,
			repairIngredient == null ? () -> Ingredient.of(Items.IRON_INGOT) : repairIngredient,
			layers == null ? List.of(new ArmorMaterial.Layer(id)) : layers,
			toughness,
			knockbackResistance
		);
	}

	public ArmorMaterialBuilder defense(Map<Object, Integer> v) {
		defense = UtilsJS.remap(v, ArmorItem.Type.class, Integer.class, false);
		return this;
	}

	public ArmorMaterialBuilder enchantmentValue(int v) {
		enchantmentValue = v;
		return this;
	}

	public ArmorMaterialBuilder equipSound(ResourceLocation id) {
		equipSound = RegistryInfo.SOUND_EVENT.getHolder(id);
		return this;
	}

	public ArmorMaterialBuilder repairIngredient(Supplier<Ingredient> v) {
		repairIngredient = v;
		return this;
	}

	public ArmorMaterialBuilder layers(ArmorMaterial.Layer[] v) {
		layers = List.of(v);
		return this;
	}

	public ArmorMaterialBuilder toughness(float v) {
		toughness = v;
		return this;
	}

	public ArmorMaterialBuilder knockbackResistance(float v) {
		knockbackResistance = v;
		return this;
	}
}
