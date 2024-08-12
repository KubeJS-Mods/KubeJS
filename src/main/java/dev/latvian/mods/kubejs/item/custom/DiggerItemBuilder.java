package dev.latvian.mods.kubejs.item.custom;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.Tier;
import net.neoforged.neoforge.common.Tags;

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
		public static final ResourceLocation[] PICKAXE_TAGS = {
			ItemTags.PICKAXES.location(),
			ItemTags.CLUSTER_MAX_HARVESTABLES.location(),
			Tags.Items.MINING_TOOL_TOOLS.location(),
		};

		public Pickaxe(ResourceLocation i) {
			super(i, 1F, -2.8F, PickaxeItem::new);
			parentModel = "minecraft:item/iron_pickaxe";
			tag(PICKAXE_TAGS);
		}
	}

	public static class Shovel extends DiggerItemBuilder {
		public static final ResourceLocation[] SHOVEL_TAGS = {
			ItemTags.SHOVELS.location(),
		};

		public Shovel(ResourceLocation i) {
			super(i, 1.5F, -3F, ShovelItem::new);
			parentModel = "minecraft:item/iron_shovel";
			tag(SHOVEL_TAGS);
		}
	}

	public static class Axe extends DiggerItemBuilder {
		public static final ResourceLocation[] AXE_TAGS = {
			ItemTags.AXES.location(),
		};

		public Axe(ResourceLocation i) {
			super(i, 6F, -3.1F, AxeItem::new);
			parentModel = "minecraft:item/iron_axe";
			tag(AXE_TAGS);
		}
	}

	public static class Hoe extends DiggerItemBuilder {
		public static final ResourceLocation[] HOE_TAGS = {
			ItemTags.HOES.location(),
		};

		public Hoe(ResourceLocation i) {
			super(i, 0F, -3F, HoeItem::new);
			parentModel = "minecraft:item/iron_hoe";
			tag(HOE_TAGS);
		}
	}
}
