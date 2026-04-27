package dev.latvian.mods.kubejs.item.custom;

import dev.latvian.mods.kubejs.generator.KubeAssetGenerator;
import dev.latvian.mods.kubejs.item.ItemBuilder;
import dev.latvian.mods.kubejs.item.MutableToolTier;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.component.ItemAttributeModifiers;

import java.util.function.Consumer;

@ReturnsSelf
public abstract class HandheldItemBuilder extends ItemBuilder {
	public transient MutableToolTier toolTier;
	public transient float attackDamageBaseline;
	public transient float disableBlockingForSeconds;
	public transient float speedBaseline;

	public HandheldItemBuilder(Identifier i, float d, float s) {
		super(i);
		toolTier = new MutableToolTier(ToolMaterial.IRON);
		attackDamageBaseline = d;
		disableBlockingForSeconds = 0;
		speedBaseline = s;
		parentModel(KubeAssetGenerator.HANDHELD_ITEM_MODEL);
		unstackable();
	}

	public HandheldItemBuilder tier(MutableToolTier t) {
		if (t == null) {
			try {
				return this;
			} catch (Exception e) {
				ScriptType.STARTUP.console.error("Cannot pass in a null tier to tier builder.");
			}
		}

		toolTier = t instanceof MutableToolTier mtt ? mtt : new MutableToolTier(t.build());
		return this;
	}

	@Info("""
		Sets the base attack damage of the tool. Different tools have different baselines.
		
		For example, a sword has a baseline of 3, while an axe has a baseline of 6.
		
		The actual damage is the sum of the baseline and the attackDamageBonus from tier.
		""")
	public HandheldItemBuilder attackDamageBaseline(float f) {
		attackDamageBaseline = f;
		return this;
	}

	@Info("""
		Sets how long blocking is disabled on the target after being hit, in seconds.
		
		Defaults to 0, meaning blocking is not disabled.
		
		Axes set this to 5.0 seconds, which is what causes them to disable shields.
		""")
	public HandheldItemBuilder disableBlockingForSeconds(float f) {
		disableBlockingForSeconds = f;
		return this;
	}

	@Info("""
		Sets the base attack speed of the tool. Different tools have different baselines.
		
		For example, a sword has a baseline of -2.4, while an axe has a baseline of -3.1.
		
		The actual speed is the sum of the baseline and the speed from tier + 4 (bare hand).
		""")
	public HandheldItemBuilder speedBaseline(float f) {
		speedBaseline = f;
		return this;
	}

	@Info("Modifies the tool tier.")
	public HandheldItemBuilder modifyTier(Consumer<MutableToolTier> callback) {
		callback.accept(toolTier);
		return this;
	}

	@Info("Sets the attack damage bonus of the tool.")
	public HandheldItemBuilder attackDamageBonus(float f) {
		toolTier.setAttackDamageBonus(f);
		return this;
	}

	@Info("Sets the attack speed of the tool.")
	public HandheldItemBuilder speed(float f) {
		toolTier.setSpeed(f);
		return this;
	}

	protected static ItemAttributeModifiers createToolAttributes(ToolMaterial material, float attackDamageBaseline, float attackSpeedBaseline) {
		return ItemAttributeModifiers.builder()
			.add(
				Attributes.ATTACK_DAMAGE,
				new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, attackDamageBaseline + material.attackDamageBonus(), AttributeModifier.Operation.ADD_VALUE),
				EquipmentSlotGroup.MAINHAND
			)
			.add(
				Attributes.ATTACK_SPEED,
				new AttributeModifier(Item.BASE_ATTACK_SPEED_ID, attackSpeedBaseline, AttributeModifier.Operation.ADD_VALUE),
				EquipmentSlotGroup.MAINHAND
			)
			.build();
	}
}
