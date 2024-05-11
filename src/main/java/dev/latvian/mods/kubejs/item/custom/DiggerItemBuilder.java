package dev.latvian.mods.kubejs.item.custom;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.Tier;

import java.util.function.BiFunction;

public class DiggerItemBuilder extends HandheldItemBuilder {
	public final BiFunction<Tier, Item.Properties, DiggerItem> function;

	public DiggerItemBuilder(ResourceLocation i, float d, float s, BiFunction<Tier, Item.Properties, DiggerItem> f) {
		super(i, d, s);
		function = f;
		itemAttributeModifiers = DiggerItem.createAttributes(toolTier, attackDamageBaseline, speedBaseline);
	}

	@Override
	public Item createObject() {
		return function.apply(toolTier, createItemProperties());
	}

	public static class Pickaxe extends DiggerItemBuilder {
		public Pickaxe(ResourceLocation i) {
			super(i, 1F, -2.8F, PickaxeItem::new);
			parentModel = "minecraft:item/iron_pickaxe";
		}
	}

	public static class Shovel extends DiggerItemBuilder {
		public Shovel(ResourceLocation i) {
			super(i, 1.5F, -3F, ShovelItem::new);
			parentModel = "minecraft:item/iron_shovel";
		}
	}

	public static class Axe extends DiggerItemBuilder {
		public Axe(ResourceLocation i) {
			super(i, 6F, -3.1F, AxeItem::new);
			parentModel = "minecraft:item/iron_axe";
		}
	}

	public static class Hoe extends DiggerItemBuilder {
		public Hoe(ResourceLocation i) {
			super(i, 0F, -3F, HoeItem::new);
			parentModel = "minecraft:item/iron_hoe";
		}
	}
}
