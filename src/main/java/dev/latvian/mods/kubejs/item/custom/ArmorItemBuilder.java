package dev.latvian.mods.kubejs.item.custom;

import dev.latvian.mods.kubejs.item.ItemBuilder;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.AnimalArmorItem;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.Item;

@ReturnsSelf
public class ArmorItemBuilder extends ItemBuilder {
	public static class Helmet extends ArmorItemBuilder {
		public static final ResourceLocation[] HELMET_TAGS = {
			ItemTags.HEAD_ARMOR.location(),
		};

		public Helmet(ResourceLocation i) {
			super(i, ArmorItem.Type.HELMET);
			tag(HELMET_TAGS);
		}
	}

	public static class Chestplate extends ArmorItemBuilder {
		public static final ResourceLocation[] CHESTPLATE_TAGS = {
			ItemTags.CHEST_ARMOR.location(),
		};

		public Chestplate(ResourceLocation i) {
			super(i, ArmorItem.Type.CHESTPLATE);
			tag(CHESTPLATE_TAGS);
		}
	}

	public static class Leggings extends ArmorItemBuilder {
		public static final ResourceLocation[] LEGGING_TAGS = {
			ItemTags.LEG_ARMOR.location(),
		};

		public Leggings(ResourceLocation i) {
			super(i, ArmorItem.Type.LEGGINGS);
			tag(LEGGING_TAGS);
		}
	}

	public static class Boots extends ArmorItemBuilder {
		public static final ResourceLocation[] BOOT_TAGS = {
			ItemTags.FOOT_ARMOR.location(),
		};

		public Boots(ResourceLocation i) {
			super(i, ArmorItem.Type.BOOTS);
			tag(BOOT_TAGS);
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

	public ArmorItemBuilder material(Holder<ArmorMaterial> material) {
		this.material = material;
		return this;
	}
}
