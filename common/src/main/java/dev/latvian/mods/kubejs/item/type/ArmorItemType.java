package dev.latvian.mods.kubejs.item.type;

import dev.latvian.mods.kubejs.item.ItemBuilder;
import dev.latvian.mods.kubejs.item.custom.ArmorItemJS;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;

public class ArmorItemType extends ItemType {
	public static final ArmorItemType HELMET = new ArmorItemType("helmet", EquipmentSlot.HEAD);
	public static final ArmorItemType CHESTPLATE = new ArmorItemType("chestplate", EquipmentSlot.CHEST);
	public static final ArmorItemType LEGGINGS = new ArmorItemType("leggings", EquipmentSlot.LEGS);
	public static final ArmorItemType BOOTS = new ArmorItemType("boots", EquipmentSlot.FEET);

	public final EquipmentSlot slot;

	public ArmorItemType(String n, EquipmentSlot s) {
		super(n);
		slot = s;
	}

	@Override
	public Item createItem(ItemBuilder builder) {
		return new ArmorItemJS(builder, slot);
	}

	@Override
	public void applyDefaults(ItemBuilder builder) {
		super.applyDefaults(builder);
		builder.unstackable();
		builder.maxDamage(300);
	}
}
