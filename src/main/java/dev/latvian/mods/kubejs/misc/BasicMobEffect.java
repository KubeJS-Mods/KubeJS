package dev.latvian.mods.kubejs.misc;

import dev.latvian.mods.kubejs.script.ScriptType;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.jetbrains.annotations.NotNull;

public class BasicMobEffect extends MobEffect {
	public final transient MobEffectBuilder builder;
	private boolean modified = false;
	private final ResourceLocation id;
	private final boolean instant;

	public BasicMobEffect(MobEffectBuilder builder) {
		super(builder.category, builder.color);
		this.builder = builder;
		this.id = builder.id;
		this.instant = builder.instant;
	}

	@Override
	public boolean applyEffectTick(@NotNull LivingEntity entity, int i) {
		if (builder.effectTick == null) {
			return false;
		}

		try {
			builder.effectTick.applyEffectTick(entity, i);
			return true;
		} catch (Throwable e) {
			ScriptType.STARTUP.console.error("Error while ticking mob effect " + id + " for entity " + entity.getName().getString(), e);
			return false;
		}
	}

	@Override
	public void onEffectStarted(LivingEntity livingEntity, int amplifier) {
	}

	@Override
	public void onMobRemoved(LivingEntity livingEntity, int amplifier, Entity.RemovalReason reason) {
		super.onMobRemoved(livingEntity, amplifier, reason);
	}

	private void applyAttributeModifications() {
		if (!modified) {
			builder.attributeModifiers.forEach((r, m) -> BuiltInRegistries.ATTRIBUTE.getHolder(r).ifPresent(h -> attributeModifiers.put(h, m)));
			modified = true;
		}
	}

	@Override
	public void removeAttributeModifiers(AttributeMap attributeMap) {
		applyAttributeModifications();
		super.removeAttributeModifiers(attributeMap);
	}

	@Override
	public MobEffect addAttributeModifier(Holder<Attribute> attribute, ResourceLocation id, double d, AttributeModifier.Operation operation) {
		applyAttributeModifications();
		return super.addAttributeModifier(attribute, id, d, operation);
	}

	@Override
	public boolean isInstantenous() {
		return instant && builder.effectTick != null;
	}

	@Override
	public boolean shouldApplyEffectTickThisTick(int i, int j) {
		return builder.effectTick != null;
	}
}
