package dev.latvian.mods.kubejs.item;

import dev.latvian.mods.kubejs.registry.AdditionalObjectRegistry;
import dev.latvian.mods.kubejs.registry.BuilderBase;
import dev.latvian.mods.kubejs.registry.KubeJSRegistries;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;
import org.jspecify.annotations.Nullable;

import java.util.Map;

@ReturnsSelf
public class EquipmentAssetBuilder extends BuilderBase<EquipmentAsset> {
	public transient int durability;
	public transient @Nullable Map<ArmorType, Integer> defense;
	public transient int enchantmentValue;
	public transient @Nullable Holder<SoundEvent> equipSound;
	public transient @Nullable TagKey<Item> repairIngredient;
	public transient float toughness;
	public transient float knockbackResistance;

	public EquipmentAssetBuilder(Identifier i) {
		super(i);
	}

	public EquipmentAssetBuilder durability(int v) {
		durability = v;
		return this;
	}

	public EquipmentAssetBuilder defense(Map<ArmorType, Integer> v) {
		defense = v;
		return this;
	}

	public EquipmentAssetBuilder enchantmentValue(int v) {
		enchantmentValue = v;
		return this;
	}

	public EquipmentAssetBuilder equipSound(Holder<SoundEvent> v) {
		equipSound = v;
		return this;
	}

	public EquipmentAssetBuilder repairIngredient(TagKey<Item> v) {
		repairIngredient = v;
		return this;
	}

	public EquipmentAssetBuilder toughness(float v) {
		toughness = v;
		return this;
	}

	public EquipmentAssetBuilder knockbackResistance(float v) {
		knockbackResistance = v;
		return this;
	}

	@Override
	public EquipmentAsset createObject() {
		return new EquipmentAsset();
	}

	@Override
	public void createAdditionalObjects(AdditionalObjectRegistry registry) {
		if (durability > 0 || repairIngredient != null || (defense != null && !defense.isEmpty()) || toughness != 0F || knockbackResistance != 0F) {
			var b = new ArmorMaterialBuilder(id);
			b.durability = durability;
			b.defense = defense == null ? Map.of() : defense;
			b.enchantmentValue = enchantmentValue;
			b.equipSound = equipSound;
			b.repairIngredient = repairIngredient;
			b.assetId = ResourceKey.create(EquipmentAssets.ROOT_ID, id);
			b.toughness = toughness;
			b.knockbackResistance = knockbackResistance;
			registry.add(KubeJSRegistries.ARMOR_MATERIAL, b);
		}
	}

}
