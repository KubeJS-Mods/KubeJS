package dev.latvian.mods.kubejs.misc;

import dev.latvian.mods.kubejs.color.KubeColor;
import dev.latvian.mods.kubejs.registry.BuilderBase;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.HashMap;
import java.util.Map;

@ReturnsSelf
public class MobEffectBuilder extends BuilderBase<MobEffect> {
	@FunctionalInterface
	public interface EffectEntityCallback {
		void applyEffectTick(LivingEntity entity, int level);
	}

	public transient MobEffectCategory category;
	public transient EffectEntityCallback effectTick;
	public transient Map<ResourceLocation, MobEffect.AttributeTemplate> attributeModifiers;
	public transient int color;
	public transient boolean instant;

	public MobEffectBuilder(ResourceLocation i) {
		super(i);
		category = MobEffectCategory.NEUTRAL;
		color = 0xFFFFFF;
		effectTick = null;
		attributeModifiers = new HashMap<>(0);
	}

	@Override
	public MobEffect createObject() {
		var effect = new BasicMobEffect(this);
		effect.applyAttributeModifications();
		return effect;
	}

	@Override
	public String getTranslationKeyGroup() {
		return "effect";
	}

	public MobEffectBuilder modifyAttribute(ResourceLocation attribute, ResourceLocation id, double amount, AttributeModifier.Operation operation) {
		attributeModifiers.put(attribute, new MobEffect.AttributeTemplate(id, amount, operation));
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

	public MobEffectBuilder effectTick(EffectEntityCallback effectTick) {
		this.effectTick = effectTick;
		return this;
	}

	public MobEffectBuilder color(KubeColor col) {
		color = col.kjs$getRGB();
		return this;
	}

	public MobEffectBuilder instant() {
		return instant(true);
	}

	public MobEffectBuilder instant(boolean instant) {
		this.instant = instant;
		return this;
	}
}
