package dev.latvian.mods.kubejs.misc;

import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.kubejs.script.ScriptType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.AttributeModifierTemplate;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class BasicMobEffect extends MobEffect {

	private final MobEffectBuilder.EffectTickCallback effectTickCallback;
	private final Map<ResourceLocation, AttributeModifierTemplate> modifierMap;
	private final Map<Attribute, AttributeModifierTemplate> attributeMap;
	private boolean modified = false;
	private final ResourceLocation id;
	private final boolean instant;

	public BasicMobEffect(Builder builder) {
		super(builder.category, builder.color);
		this.effectTickCallback = builder.effectTick;
		modifierMap = builder.attributeModifiers;
		attributeMap = getAttributeModifiers();
		this.id = builder.id;
		this.instant = builder.instant;
	}

	@Override
	public void applyEffectTick(@NotNull LivingEntity livingEntity, int i) {
		try {
			effectTickCallback.applyEffectTick(livingEntity, i);
		} catch (Throwable e) {
			ScriptType.STARTUP.console.error("Error while ticking mob effect " + id + " for entity " + livingEntity.getName().getString(), e);
		}
	}

	private void applyAttributeModifications() {
		if (!modified) {
			modifierMap.forEach((r, m) -> attributeMap.put(RegistryInfo.ATTRIBUTE.getValue(r), m));
			modified = true;
		}
	}

	@Override
	public Map<Attribute, AttributeModifierTemplate> getAttributeModifiers() {
		applyAttributeModifications();
		return super.getAttributeModifiers();
	}

	@Override
	public void removeAttributeModifiers(AttributeMap attributeMap) {
		applyAttributeModifications();
		super.removeAttributeModifiers(attributeMap);
	}

	@Override
	public MobEffect addAttributeModifier(Attribute attribute, String string, double d, AttributeModifier.Operation operation) {
		applyAttributeModifications();
		return super.addAttributeModifier(attribute, string, d, operation);
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
