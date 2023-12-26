package dev.latvian.mods.kubejs.core;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public interface ModifiableItemKJS {
	default Multimap<Attribute, AttributeModifier> kjs$getAttributeMap() {
		throw new NoMixinException();
	}

	default void kjs$setAttributeMap(Multimap<Attribute, AttributeModifier> attributes) {
		throw new NoMixinException();
	}

	default Multimap<Attribute, AttributeModifier> kjs$getMutableAttributeMap() {
		Multimap<Attribute, AttributeModifier> attributes = kjs$getAttributeMap();
		if (attributes instanceof ImmutableMultimap) {
			attributes = ArrayListMultimap.create(attributes);
			kjs$setAttributeMap(attributes);
		}

		return attributes;
	}
}
