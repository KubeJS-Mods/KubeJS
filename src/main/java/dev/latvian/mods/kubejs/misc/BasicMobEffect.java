package dev.latvian.mods.kubejs.misc;

import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.kubejs.script.ScriptType;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class BasicMobEffect extends MobEffect {

	private final MobEffectBuilder.EffectTickCallback effectTickCallback;
	private final Map<ResourceLocation, AttributeTemplate> modifierMap;
	private boolean modified = false;
	private final ResourceLocation id;
	private final boolean instant;

	public BasicMobEffect(Builder builder) {
		super(builder.category, builder.color);
		this.effectTickCallback = builder.effectTick;
		modifierMap = builder.attributeModifiers;
		this.id = builder.id;
		this.instant = builder.instant;
	}

	@Override
	public boolean applyEffectTick(@NotNull LivingEntity livingEntity, int i) {
		try {
			effectTickCallback.applyEffectTick(livingEntity, i);
			return true;
		} catch (Throwable e) {
			ScriptType.STARTUP.console.error("Error while ticking mob effect " + id + " for entity " + livingEntity.getName().getString(), e);
			return false;
		}
	}

	private void applyAttributeModifications() {
		if (!modified) {
			modifierMap.forEach((r, m) -> attributeModifiers.put(RegistryInfo.ATTRIBUTE.getHolder(r), m));
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
		return instant && this.effectTickCallback != null;
	}

	@Override
	public boolean shouldApplyEffectTickThisTick(int i, int j) {
		return this.effectTickCallback != null;
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
