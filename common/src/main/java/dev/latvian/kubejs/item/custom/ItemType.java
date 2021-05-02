package dev.latvian.kubejs.item.custom;

import dev.latvian.kubejs.item.ItemBuilder;
import dev.latvian.kubejs.item.ItemJS;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum ItemType {
	BASIC("basic", ItemJS::new),
	SWORD("sword", SwordItemJS::new),
	PICKAXE("pickaxe", PickaxeItemJS::new),
	AXE("axe", AxeItemJS::new),
	SHOVEL("shovel", ShovelItemJS::new),
	HOE("hoe", HoeItemJS::new),
	HELMET("helmet", b -> new ArmorItemJS(b, EquipmentSlot.HEAD)),
	CHESTPLATE("chestplate", b -> new ArmorItemJS(b, EquipmentSlot.CHEST)),
	LEGGINGS("leggings", b -> new ArmorItemJS(b, EquipmentSlot.LEGS)),
	BOOTS("boots", b -> new ArmorItemJS(b, EquipmentSlot.FEET)),

	;

	public static final ItemType[] VALUES = values();
	public static final Map<String, ItemType> MAP = Arrays.stream(VALUES).collect(Collectors.toMap(v -> v.name, v -> v));

	public final String name;
	public final Function<ItemBuilder, Item> itemFactory;

	ItemType(String n, Function<ItemBuilder, Item> f) {
		name = n;
		itemFactory = f;
	}

	public void applyDefaults(ItemBuilder builder) {
		if (this == BASIC) {
			return;
		}

		builder.parentModel = "minecraft:item/handheld";
		builder.unstackable();
		builder.maxDamage(300);

		switch (this) {
			case SWORD:
				builder.attackDamageBaseline = 3F;
				builder.attackSpeedBaseline = -2.4F;
				break;
			case PICKAXE:
				builder.attackDamageBaseline = 1F;
				builder.attackSpeedBaseline = -2.8F;
				break;
			case AXE:
				builder.attackDamageBaseline = 6F;
				builder.attackSpeedBaseline = -3.1F;
				break;
			case SHOVEL:
				builder.attackDamageBaseline = 1.5F;
				builder.attackSpeedBaseline = -3F;
				break;
			case HOE:
				builder.attackDamageBaseline = -2F;
				builder.attackSpeedBaseline = -1F;
				break;
		}
	}
}
