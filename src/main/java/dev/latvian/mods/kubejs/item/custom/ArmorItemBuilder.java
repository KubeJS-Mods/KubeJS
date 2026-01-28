package dev.latvian.mods.kubejs.item.custom;

import dev.latvian.mods.kubejs.item.ItemBuilder;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.AnimalArmorItem;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.Item;

@ReturnsSelf
public class ArmorItemBuilder extends ItemBuilder {
	public static class Helmet extends ArmorItemBuilder {
		public static final Identifier[] HELMET_TAGS = {
			ItemTags.HEAD_ARMOR.identifier(),
		};

		public Helmet(Identifier id) {
			super(id, ArmorItem.Type.HELMET);
			tag(HELMET_TAGS);
		}
	}

	public static class Chestplate extends ArmorItemBuilder {
		public static final Identifier[] CHESTPLATE_TAGS = {
			ItemTags.CHEST_ARMOR.identifier(),
		};

		public Chestplate(Identifier id) {
			super(id, ArmorItem.Type.CHESTPLATE);
			tag(CHESTPLATE_TAGS);
		}
	}

	public static class Leggings extends ArmorItemBuilder {
		public static final Identifier[] LEGGING_TAGS = {
			ItemTags.LEG_ARMOR.identifier(),
		};

		public Leggings(Identifier id) {
			super(id, ArmorItem.Type.LEGGINGS);
			tag(LEGGING_TAGS);
		}
	}

	public static class Boots extends ArmorItemBuilder {
		public static final Identifier[] BOOT_TAGS = {
			ItemTags.FOOT_ARMOR.identifier(),
		};

		public Boots(Identifier id) {
			super(id, ArmorItem.Type.BOOTS);
			tag(BOOT_TAGS);
		}
	}

	@ReturnsSelf
	public static class AnimalArmor extends ArmorItemBuilder {
		public AnimalArmorItem.BodyType bodyType;
		public boolean overlay;

		public AnimalArmor(Identifier id) {
			super(id, ArmorItem.Type.BODY);
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

	protected ArmorItemBuilder(Identifier id, ArmorItem.Type t) {
		super(id);
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
