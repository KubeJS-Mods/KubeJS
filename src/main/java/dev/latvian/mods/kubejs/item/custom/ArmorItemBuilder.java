package dev.latvian.mods.kubejs.item.custom;

import dev.latvian.mods.kubejs.item.ItemBuilder;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.kubejs.typings.ReturnsSelf;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.AnimalArmorItem;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.Item;

@ReturnsSelf
public class ArmorItemBuilder extends ItemBuilder {
	public static class Helmet extends ArmorItemBuilder {
		public Helmet(ResourceLocation i) {
			super(i, ArmorItem.Type.HELMET);
		}
	}

	public static class Chestplate extends ArmorItemBuilder {
		public Chestplate(ResourceLocation i) {
			super(i, ArmorItem.Type.CHESTPLATE);
		}
	}

	public static class Leggings extends ArmorItemBuilder {

		public Leggings(ResourceLocation i) {
			super(i, ArmorItem.Type.LEGGINGS);
		}
	}

	public static class Boots extends ArmorItemBuilder {
		public Boots(ResourceLocation i) {
			super(i, ArmorItem.Type.BOOTS);
		}
	}

	@ReturnsSelf
	public static class AnimalArmor extends ArmorItemBuilder {
		public AnimalArmorItem.BodyType bodyType;
		public boolean overlay;

		public AnimalArmor(ResourceLocation i) {
			super(i, ArmorItem.Type.BODY);
			bodyType = AnimalArmorItem.BodyType.CANINE;
			overlay = true;
		}

		@Override
		public Item createObject() {
			return new AnimalArmorItem(material, bodyType, overlay, createItemProperties());
		}

		public AnimalArmor bodyType(AnimalArmorItem.BodyType type) {
			bodyType = type;
			return this;
		}

		public AnimalArmor overlay(boolean o) {
			overlay = o;
			return this;
		}
	}

	public final ArmorItem.Type armorType;
	public Holder<ArmorMaterial> material;

	protected ArmorItemBuilder(ResourceLocation i, ArmorItem.Type t) {
		super(i);
		armorType = t;
		material = ArmorMaterials.IRON;
		unstackable();
	}

	@Override
	public Item createObject() {
		return new ArmorItem(material, armorType, createItemProperties());
	}

	public ArmorItemBuilder material(ResourceLocation id) {
		material = RegistryInfo.ARMOR_MATERIAL.getHolder(id);
		return this;
	}
}
