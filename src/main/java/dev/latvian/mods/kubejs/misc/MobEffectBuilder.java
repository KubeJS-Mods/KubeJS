package dev.latvian.mods.kubejs.misc;

import dev.latvian.mods.kubejs.registry.BuilderBase;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.rhino.mod.util.color.Color;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author Prunoideae
 */
public abstract class MobEffectBuilder extends BuilderBase<MobEffect> {

	@FunctionalInterface
	public interface EffectTickCallback {
		void applyEffectTick(LivingEntity livingEntity, int level);
	}

	public transient MobEffectCategory category;
	public transient EffectTickCallback effectTick;
	public transient Map<ResourceLocation, MobEffect.AttributeTemplate> attributeModifiers;
	public transient int color;
	public transient boolean instant;

	public MobEffectBuilder(ResourceLocation i) {
		super(i);
		category = MobEffectCategory.NEUTRAL;
		color = 0xFFFFFF;
		effectTick = null;
		attributeModifiers = new HashMap<>();
	}

	@Override
	public final RegistryInfo getRegistryType() {
		return RegistryInfo.MOB_EFFECT;
	}

	public MobEffectBuilder modifyAttribute(ResourceLocation attribute, String id, double d, AttributeModifier.Operation operation) {
		var uuid = UUID.nameUUIDFromBytes(id.getBytes(StandardCharsets.UTF_8));
		attributeModifiers.put(attribute, new MobEffect.AttributeTemplate(uuid, d, operation));
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
		this.effectTick = effectTick;
		return this;
	}

	public MobEffectBuilder color(Color col) {
		color = col.getRgbJS();
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
