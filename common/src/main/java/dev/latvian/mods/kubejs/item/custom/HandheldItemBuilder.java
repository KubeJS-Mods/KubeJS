package dev.latvian.mods.kubejs.item.custom;

import dev.latvian.mods.kubejs.item.ItemBuilder;
import dev.latvian.mods.kubejs.item.MutableToolTier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;

import java.util.function.Consumer;

public abstract class HandheldItemBuilder extends ItemBuilder {
	public transient MutableToolTier toolTier;
	public transient float attackDamageBaseline;
	public transient float speedBaseline;

	public HandheldItemBuilder(ResourceLocation i, float d, float s) {
		super(i);
		toolTier = new MutableToolTier(Tiers.IRON);
		attackDamageBaseline = d;
		speedBaseline = s;
		parentModel("minecraft:item/handheld");
		unstackable();
	}

	public HandheldItemBuilder tier(Tier t) {
		toolTier = new MutableToolTier(t);
		return this;
	}

	public HandheldItemBuilder attackDamageBaseline(float f) {
		attackDamageBaseline = f;
		return this;
	}

	public HandheldItemBuilder speedBaseline(float f) {
		speedBaseline = f;
		return this;
	}

	public HandheldItemBuilder modifyTier(Consumer<MutableToolTier> callback) {
		callback.accept(toolTier);
		return this;
	}

	public HandheldItemBuilder attackDamageBonus(float f) {
		toolTier.setAttackDamageBonus(f);
		return this;
	}

	public HandheldItemBuilder speed(float f) {
		toolTier.setSpeed(f);
		return this;
	}
}
