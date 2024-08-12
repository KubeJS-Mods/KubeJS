package dev.latvian.mods.kubejs.item.custom;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;

public class SwordItemBuilder extends HandheldItemBuilder {
	public static final ResourceLocation[] SWORD_TAGS = {
		ItemTags.SWORDS.location(),
	};

	public SwordItemBuilder(ResourceLocation i) {
		super(i, 3F, -2.4F);
		itemAttributeModifiers = SwordItem.createAttributes(toolTier, attackDamageBaseline, speedBaseline);
		parentModel = "minecraft:item/iron_sword";
		tag(SWORD_TAGS);
	}

	@Override
	public Item createObject() {
		return new SwordItem(toolTier, createItemProperties());
	}
}
