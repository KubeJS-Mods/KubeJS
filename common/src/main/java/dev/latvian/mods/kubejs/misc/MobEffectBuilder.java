package dev.latvian.mods.kubejs.misc;

import dev.latvian.mods.kubejs.BuilderBase;
import dev.latvian.mods.kubejs.KubeJSRegistries;
import dev.latvian.mods.kubejs.RegistryObjectBuilderTypes;
import dev.latvian.mods.kubejs.entity.LivingEntityJS;
import dev.latvian.mods.rhino.mod.util.color.Color;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class MobEffectBuilder extends BuilderBase<MobEffect> {

	@FunctionalInterface
	public interface EffectTickCallback {
		void applyEffectTick(LivingEntityJS livingEntity, int level);
	}

	public transient MobEffectCategory category;
	public transient EffectTickCallback effectTickCallback;
	public transient Map<Attribute, AttributeModifier> attributeModifiers;
	public transient int color;

	public MobEffectBuilder(ResourceLocation i) {
		super(i);
		category = MobEffectCategory.NEUTRAL;
		color = 0xFFFFFF;
		effectTickCallback = null;
		attributeModifiers = new HashMap<>();
	}

	@Override
	public final RegistryObjectBuilderTypes<MobEffect> getRegistryType() {
		return RegistryObjectBuilderTypes.MOB_EFFECT;
	}

	public MobEffectBuilder modifyAttribute(ResourceLocation attribute, String identifier, double d, AttributeModifier.Operation operation) {
		AttributeModifier attributeModifier = new AttributeModifier(new UUID(identifier.hashCode(), identifier.hashCode()), identifier, d, operation);
		Attribute attr = KubeJSRegistries.attributes().get(attribute);
		attributeModifiers.put(attr, attributeModifier);
		return this;
	}

	public MobEffectBuilder category(MobEffectCategory c) {
		category = c;
		return this;
	}

	public MobEffectBuilder harmful() {
		return category(MobEffectCategory.HARMFUL);
	}

	public MobEffectBuilder beneficial() {
		return category(MobEffectCategory.BENEFICIAL);
	}

	public MobEffectBuilder effectTick(EffectTickCallback effectTick) {
		this.effectTickCallback = effectTick;
		return this;
	}

	public MobEffectBuilder color(Color col) {
		color = col.getRgbKJS();
		return this;
	}
}
