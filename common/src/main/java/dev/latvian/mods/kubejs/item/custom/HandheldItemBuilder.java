package dev.latvian.mods.kubejs.item.custom;

import dev.latvian.mods.kubejs.item.ItemBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;

public abstract class HandheldItemBuilder extends ItemBuilder {
	public transient Tier toolTier;
	public transient float attackDamageBaseline;
	public transient float attackSpeedBaseline;

	public HandheldItemBuilder(ResourceLocation i, float d, float s) {
		super(i);
		toolTier = Tiers.IRON;
		attackDamageBaseline = d;
		attackSpeedBaseline = s;
		parentModel("minecraft:item/handheld");
		unstackable();
	}

	public HandheldItemBuilder tier(String t) {
		toolTier = TOOL_TIERS.getOrDefault(t, Tiers.IRON);
		return this;
	}
}
