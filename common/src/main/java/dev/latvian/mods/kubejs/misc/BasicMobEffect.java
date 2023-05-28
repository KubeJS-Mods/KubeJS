package dev.latvian.mods.kubejs.misc;

import dev.latvian.mods.kubejs.registry.KubeJSRegistries;
import dev.latvian.mods.kubejs.script.ScriptType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class BasicMobEffect extends MobEffect {

	private final MobEffectBuilder.EffectTickCallback effectTickCallback;
	private final Map<ResourceLocation, AttributeModifier> modifierMap;
	private final Map<Attribute, AttributeModifier> attributeMap;
	private boolean modified = false;
	private final ResourceLocation id;

	public BasicMobEffect(Builder builder) {
		super(builder.category, builder.color);
		this.effectTickCallback = builder.effectTick;
		modifierMap = builder.attributeModifiers;
		attributeMap = new HashMap<>();
		this.id = builder.id;
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
			modifierMap.forEach((r, m) -> attributeMap.put(KubeJSRegistries.attributes().get(r), m));
			modified = true;
		}
	}

	@Override
	public Map<Attribute, AttributeModifier> getAttributeModifiers() {
		this.applyAttributeModifications();
		return attributeMap;
	}

	@Override
	public void removeAttributeModifiers(LivingEntity livingEntity, AttributeMap attributeMap, int i) {
		this.applyAttributeModifications();
		for (Map.Entry<Attribute, AttributeModifier> entry : this.attributeMap.entrySet()) {
			AttributeInstance attributeInstance = attributeMap.getInstance(entry.getKey());
			if (attributeInstance != null) {
				attributeInstance.removeModifier(entry.getValue());
			}
		}
	}

	@Override
	public void addAttributeModifiers(LivingEntity livingEntity, AttributeMap attributeMap, int i) {
		this.applyAttributeModifications();
		for (Map.Entry<Attribute, AttributeModifier> attributeAttributeModifierEntry : this.attributeMap.entrySet()) {
			AttributeInstance attributeInstance = attributeMap.getInstance(attributeAttributeModifierEntry.getKey());
			if (attributeInstance != null) {
				AttributeModifier attributeModifier = attributeAttributeModifierEntry.getValue();
				attributeInstance.removeModifier(attributeModifier);
				attributeInstance.addPermanentModifier(new AttributeModifier(attributeModifier.getId(), this.getDescriptionId() + " " + i, this.getAttributeModifierValue(i, attributeModifier), attributeModifier.getOperation()));
			}
		}

	}

	@Override
	public boolean isDurationEffectTick(int i, int j) {
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
