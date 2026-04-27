package dev.latvian.mods.kubejs.item.custom;

import dev.latvian.mods.kubejs.item.ItemBuilder;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorMaterials;
import net.minecraft.world.item.equipment.ArmorType;

@ReturnsSelf
public class ArmorItemBuilder extends ItemBuilder {
	public static class Helmet extends ArmorItemBuilder {
		public static final Identifier[] HELMET_TAGS = {
			ItemTags.HEAD_ARMOR.location(),
		};

		public Helmet(Identifier id) {
			super(id, ArmorType.HELMET);
			tag(HELMET_TAGS);
		}
	}

	public static class Chestplate extends ArmorItemBuilder {
		public static final Identifier[] CHESTPLATE_TAGS = {
			ItemTags.CHEST_ARMOR.location(),
		};

		public Chestplate(Identifier id) {
			super(id, ArmorType.CHESTPLATE);
			tag(CHESTPLATE_TAGS);
		}
	}

	public static class Leggings extends ArmorItemBuilder {
		public static final Identifier[] LEGGING_TAGS = {
			ItemTags.LEG_ARMOR.location(),
		};

		public Leggings(Identifier id) {
			super(id, ArmorType.LEGGINGS);
			tag(LEGGING_TAGS);
		}
	}

	public static class Boots extends ArmorItemBuilder {
		public static final Identifier[] BOOT_TAGS = {
			ItemTags.FOOT_ARMOR.location(),
		};

		public Boots(Identifier id) {
			super(id, ArmorType.BOOTS);
			tag(BOOT_TAGS);
		}
	}

	public enum AnimalArmorKind {
		WOLF,
		HORSE,
		NAUTILUS
	}

	@ReturnsSelf
	public static class AnimalArmor extends ItemBuilder {
		public ArmorMaterial material;
		public AnimalArmorKind kind;

		public AnimalArmor(Identifier id) {
			super(id);
			material = ArmorMaterials.IRON;
			kind = AnimalArmorKind.WOLF;
			unstackable();
		}

		@Override
		public Item createObject() {
			Item.Properties p = createItemProperties();
			var resolvedMaterial = material == null ? ArmorMaterials.IRON : material;

			return switch (kind) {
				case WOLF -> new Item(p.wolfArmor(resolvedMaterial));
				case HORSE -> new Item(p.horseArmor(resolvedMaterial));
				case NAUTILUS -> new Item(p.nautilusArmor(resolvedMaterial));
			};
		}

		@Info("""
			Sets the armor material for this item.
			
			If unset, this defaults to vanilla iron armor material.
			""")
		public AnimalArmor material(ArmorMaterialType type) {
			this.material = type.material;
			return this;
		}

		public AnimalArmor kind(AnimalArmorKind kind) {
			this.kind = kind;
			return this;
		}
	}

	public final ArmorType armorType;
	public ArmorMaterial material;

	protected ArmorItemBuilder(Identifier id, ArmorType t) {
		super(id);
		armorType = t;
		material = ArmorMaterials.IRON;
		unstackable();
	}

	@Override
	public Item createObject() {
		return new Item(createItemProperties().humanoidArmor(material == null ? ArmorMaterials.IRON : material, armorType));
	}

	@Info("""
		Sets the armor material for this item.
		
		If unset, this defaults to vanilla iron armor material.
		""")
	public ArmorItemBuilder material(ArmorMaterialType type) {
		material = type.material;
		return this;
	}

	public enum ArmorMaterialType {
		LEATHER(ArmorMaterials.LEATHER),
		COPPER(ArmorMaterials.COPPER),
		CHAINMAIL(ArmorMaterials.CHAINMAIL),
		IRON(ArmorMaterials.IRON),
		GOLD(ArmorMaterials.GOLD),
		DIAMOND(ArmorMaterials.DIAMOND),
		TURTLE_SCUTE(ArmorMaterials.TURTLE_SCUTE),
		NETHERITE(ArmorMaterials.NETHERITE),
		ARMADILLO_SCUTE(ArmorMaterials.ARMADILLO_SCUTE);

		public final ArmorMaterial material;

		ArmorMaterialType(ArmorMaterial material) {
			this.material = material;
		}
	}

}
