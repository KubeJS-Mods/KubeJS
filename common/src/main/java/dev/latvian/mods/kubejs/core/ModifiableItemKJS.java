package dev.latvian.mods.kubejs.core;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.apache.commons.lang3.NotImplementedException;

public interface ModifiableItemKJS {
	default Multimap<Attribute, AttributeModifier> getAttributeMapKJS() {
		throw new NotImplementedException("A mixin should have implemented this method!");
	}

	default void setAttributeMapKJS(Multimap<Attribute, AttributeModifier> attributes) {
		throw new NotImplementedException("A mixin should have implemented this method!");
	}

	default Multimap<Attribute, AttributeModifier> getMutableAttributeMap() {
		Multimap<Attribute, AttributeModifier> attributes = getAttributeMapKJS();
		if (attributes instanceof ImmutableMultimap) {
			attributes = ArrayListMultimap.create(attributes);
			setAttributeMapKJS(attributes);
		}

		return attributes;
	}
}
