package dev.latvian.mods.kubejs.item.custom;

import dev.latvian.mods.kubejs.item.ItemBuilder;
import dev.latvian.mods.kubejs.item.MutableToolTier;
import dev.latvian.mods.kubejs.typings.JsInfo;
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

	@JsInfo("""
			Sets the base attack damage of the tool. Different tools have different baselines.
						
			For example, a sword has a baseline of 3, while an axe has a baseline of 6.
						
			The actual damage is the sum of the baseline and the attackDamageBonus from tier.
			""")
	public HandheldItemBuilder attackDamageBaseline(float f) {
		attackDamageBaseline = f;
		return this;
	}

	@JsInfo("""
			Sets the base attack speed of the tool. Different tools have different baselines.
						
			For example, a sword has a baseline of -2.4, while an axe has a baseline of -3.1.
						
			The actual speed is the sum of the baseline and the speed from tier + 4 (bare hand).
			""")
	public HandheldItemBuilder speedBaseline(float f) {
		speedBaseline = f;
		return this;
	}

	@JsInfo("Modifies the tool tier.")
	public HandheldItemBuilder modifyTier(Consumer<MutableToolTier> callback) {
		callback.accept(toolTier);
		return this;
	}

	@JsInfo("Sets the attack damage bonus of the tool.")
	public HandheldItemBuilder attackDamageBonus(float f) {
		toolTier.setAttackDamageBonus(f);
		return this;
	}

	@JsInfo("Sets the attack speed of the tool.")
	public HandheldItemBuilder speed(float f) {
		toolTier.setSpeed(f);
		return this;
	}
}
