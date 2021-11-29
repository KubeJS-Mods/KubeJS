package dev.latvian.mods.kubejs.item.custom;

import dev.latvian.mods.kubejs.generator.AssetJsonGenerator;
import dev.latvian.mods.kubejs.item.ItemBuilder;
import net.minecraft.world.item.Item;

import java.util.function.Function;

public class ToolItemType extends ItemType {
	public static final ToolItemType SWORD = new ToolItemType("sword", SwordItemJS::new, 3F, -2.4F);
	public static final ToolItemType PICKAXE = new ToolItemType("pickaxe", PickaxeItemJS::new, 1F, -2.8F);
	public static final ToolItemType AXE = new ToolItemType("axe", AxeItemJS::new, 6F, -3.1F);
	public static final ToolItemType SHOVEL = new ToolItemType("shovel", ShovelItemJS::new, 1.5F, -3F);
	public static final ToolItemType HOE = new ToolItemType("hoe", HoeItemJS::new, -2F, -1F);

	public final Function<ItemBuilder, Item> factory;
	public final float attackDamageBaseline;
	public final float attackSpeedBaseline;

	public ToolItemType(String n, Function<ItemBuilder, Item> f, float d, float s) {
		super(n);
		factory = f;
		attackDamageBaseline = d;
		attackSpeedBaseline = s;
	}

	@Override
	public Item createItem(ItemBuilder builder) {
		return factory.apply(builder);
	}

	@Override
	public void applyDefaults(ItemBuilder builder) {
		super.applyDefaults(builder);
		builder.parentModel = "minecraft:item/handheld";
		builder.attackDamageBaseline = attackDamageBaseline;
		builder.attackSpeedBaseline = attackSpeedBaseline;
		builder.unstackable();
		builder.maxDamage(300);
	}

	@Override
	public void generateAssets(ItemBuilder builder, AssetJsonGenerator generator) {
		generator.itemModel(builder.id, m -> {
			if (!builder.parentModel.isEmpty()) {
				m.parent(builder.parentModel);
			} else {
				m.parent("minecraft:item/handheld");
			}

			m.texture("layer0", builder.texture.isEmpty() ? builder.newID("item/", "").toString() : builder.texture);
		});
	}
}
