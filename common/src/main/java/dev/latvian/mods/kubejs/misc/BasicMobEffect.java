package dev.latvian.mods.kubejs.misc;

import dev.latvian.mods.kubejs.core.EntityKJS;
import dev.latvian.mods.kubejs.entity.LivingEntityJS;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class BasicMobEffect extends MobEffect {

	private final MobEffectBuilder.EffectTickCallback effectTickCallback;

	public BasicMobEffect(Builder builder) {
		super(builder.category, builder.color);
		this.effectTickCallback = builder.effectTick;
		this.getAttributeModifiers().putAll(builder.attributeModifiers);
	}

	@Override
	public void applyEffectTick(@NotNull LivingEntity livingEntity, int i) {
		if (this.effectTickCallback != null) {
			effectTickCallback.applyEffectTick((LivingEntityJS) ((EntityKJS) livingEntity).asKJS(), i);
		} else {
			super.applyEffectTick(livingEntity, i);
		}
	}

	public static class Builder extends MobEffectBuilder {
		public Builder(ResourceLocation i) {
			super(i);
		}

		@Override
		public MobEffect createObject() {
			return new BasicMobEffect(this);
		}
	}
}
