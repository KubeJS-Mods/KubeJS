package dev.latvian.mods.kubejs.item;

import dev.latvian.mods.kubejs.registry.BuilderBase;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;

import java.util.Map;


@ReturnsSelf
public class ArmorMaterialBuilder extends BuilderBase<ArmorMaterial> {
	public transient int durability;
	public transient Map<ArmorType, Integer> defense;
	public transient int enchantmentValue;
	public transient Holder<SoundEvent> equipSound;
	public transient TagKey<Item> repairIngredient;
	public transient ResourceKey<EquipmentAsset> assetId;
	public transient float toughness;
	public transient float knockbackResistance;

	public ArmorMaterialBuilder(Identifier i) {
		super(i);
		durability = 0;
		defense = Map.of();
		enchantmentValue = 9;
		equipSound = null;
		repairIngredient = null;
		assetId = null;
		toughness = 0F;
		knockbackResistance = 0F;
	}

	@Override
	public ArmorMaterial createObject() {
		if (durability <= 0) {
			throw new IllegalStateException("ArmorMaterialBuilder requires durability > 0: " + id);
		}
		if (repairIngredient == null) {
			throw new IllegalStateException("ArmorMaterialBuilder requires repairIngredient TagKey<Item>: " + id);
		}

		ResourceKey<EquipmentAsset> resolvedAssetId = assetId != null ? assetId : ResourceKey.create(EquipmentAssets.ROOT_ID, id);

		return new ArmorMaterial(
			durability,
			defense == null ? Map.of() : defense,
			enchantmentValue,
			equipSound == null ? SoundEvents.ARMOR_EQUIP_IRON : equipSound,
			toughness,
			knockbackResistance,
			repairIngredient,
			resolvedAssetId
		);
	}


	public ArmorMaterialBuilder durability(int v) {
		durability = v;
		return this;
	}

	public ArmorMaterialBuilder defense(Map<ArmorType, Integer> v) {
		defense = v;
		return this;
	}

	public ArmorMaterialBuilder enchantmentValue(int v) {
		enchantmentValue = v;
		return this;
	}

	public ArmorMaterialBuilder equipSound(Holder<SoundEvent> sound) {
		equipSound = sound;
		return this;
	}

	public ArmorMaterialBuilder repairIngredient(TagKey<Item> v) {
		repairIngredient = v;
		return this;
	}

	public ArmorMaterialBuilder assetId(ResourceKey<EquipmentAsset> v) {
		assetId = v;
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

	@Deprecated
	public ArmorMaterialBuilder layers(Object[] v) {
		return this;
	}
}
