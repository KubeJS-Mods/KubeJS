package dev.latvian.mods.kubejs.item.custom;

import net.minecraft.resources.Identifier;
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

	public DiggerItemBuilder(Identifier i, float d, float s, BiFunction<Tier, Item.Properties, DiggerItem> f) {
		super(i, d, s);
		function = f;
	}

	@Override
	public Item createObject() {
		itemAttributeModifiers = DiggerItem.createAttributes(toolTier, attackDamageBaseline, speedBaseline);
		return function.apply(toolTier, createItemProperties());
	}

	public static class Pickaxe extends DiggerItemBuilder {
		public static final Identifier[] PICKAXE_TAGS = {
			ItemTags.PICKAXES.identifier(),
			ItemTags.CLUSTER_MAX_HARVESTABLES.identifier(),
			Tags.Items.MINING_TOOL_TOOLS.identifier(),
		};

		public static final Identifier PICKAXE_MODEL = Identifier.withDefaultNamespace("item/iron_pickaxe");

		public Pickaxe(Identifier i) {
			super(i, 1F, -2.8F, PickaxeItem::new);
			parentModel = PICKAXE_MODEL;
			tag(PICKAXE_TAGS);
		}
	}

	public static class Shovel extends DiggerItemBuilder {
		public static final Identifier[] SHOVEL_TAGS = {
			ItemTags.SHOVELS.identifier(),
		};

		public static final Identifier SHOVEL_MODEL = Identifier.withDefaultNamespace("item/iron_shovel");

		public Shovel(Identifier i) {
			super(i, 1.5F, -3F, ShovelItem::new);
			parentModel = SHOVEL_MODEL;
			tag(SHOVEL_TAGS);
		}
	}

	public static class Axe extends DiggerItemBuilder {
		public static final Identifier[] AXE_TAGS = {
			ItemTags.AXES.identifier(),
		};

		public static final Identifier AXE_MODEL = Identifier.withDefaultNamespace("item/iron_axe");

		public Axe(Identifier i) {
			super(i, 6F, -3.1F, AxeItem::new);
			parentModel = AXE_MODEL;
			tag(AXE_TAGS);
		}
	}

	public static class Hoe extends DiggerItemBuilder {
		public static final Identifier[] HOE_TAGS = {
			ItemTags.HOES.identifier(),
		};

		public static final Identifier HOE_MODEL = Identifier.withDefaultNamespace("item/iron_hoe");

		public Hoe(Identifier i) {
			super(i, 0F, -3F, HoeItem::new);
			parentModel = HOE_MODEL;
			tag(HOE_TAGS);
		}
	}
}
