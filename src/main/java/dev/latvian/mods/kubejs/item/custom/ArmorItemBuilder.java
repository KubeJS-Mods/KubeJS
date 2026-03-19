package dev.latvian.mods.kubejs.item.custom;

import dev.latvian.mods.kubejs.item.ItemBuilder;
import dev.latvian.mods.kubejs.registry.BuilderBase;
import dev.latvian.mods.kubejs.registry.KubeJSRegistries;
import dev.latvian.mods.kubejs.registry.RegistryObjectStorage;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import org.jspecify.annotations.Nullable;

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
		public @Nullable ArmorMaterial material;
		public AnimalArmorKind kind;

		public AnimalArmor(Identifier id) {
			super(id);
			this.material = null;
			this.kind = AnimalArmorKind.WOLF;
			unstackable();
		}

		@Override
		public Item createObject() {
			Item.Properties p = createItemProperties();

			if (material == null) {
				throw new IllegalStateException("Armor material not set for " + id);
			}

			return switch (kind) {
				case WOLF -> new Item(p.wolfArmor(material));
				case HORSE -> new Item(p.horseArmor(material));
				case NAUTILUS -> new Item(p.nautilusArmor(material));
			};
		}

		public AnimalArmor material(Identifier materialId) {
			var storage = RegistryObjectStorage.of(KubeJSRegistries.ARMOR_MATERIAL);
			var b = (BuilderBase<ArmorMaterial>) storage.objects.get(materialId);

			if (b == null) {
				throw new IllegalStateException("Unknown ArmorMaterial '" + materialId + "' for " + id);
			}

			material = b.getOrCreate();
			return this;
		}

		public AnimalArmor kind(AnimalArmorKind kind) {
			this.kind = kind;
			return this;
		}
	}

	public final ArmorType armorType;
	public @Nullable ArmorMaterial material;

	protected ArmorItemBuilder(Identifier id, ArmorType t) {
		super(id);
		armorType = t;
		material = null;
		unstackable();
	}

	@Override
	public Item createObject() {
		if (material == null) {
			throw new IllegalStateException("Armor material not set for " + id);
		}

		return new Item(createItemProperties().humanoidArmor(material, armorType));
	}

	public ArmorItemBuilder material(Identifier materialId) {
		var storage = RegistryObjectStorage.of(KubeJSRegistries.ARMOR_MATERIAL);
		var b = (BuilderBase<ArmorMaterial>) storage.objects.get(materialId);

		if (b == null) {
			throw new IllegalStateException("Unknown ArmorMaterial '" + materialId + "' for " + id);
		}

		material = b.getOrCreate();
		return this;
	}
}
