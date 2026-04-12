package dev.latvian.mods.kubejs.core.component;

import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.component.ItemAttributeModifiers;

@RemapPrefixForJS("kjs$")
public interface AttributeModifierSetter {
	void kjs$setAttributeModifiers(ItemAttributeModifiers modifiers);

	default void kjs$addAttributeModifier(Holder<Attribute> attribute, AttributeModifier mod, EquipmentSlotGroup slot) {
		if (this instanceof AttributeModifierGetter amf) {
			kjs$setAttributeModifiers(amf.kjs$getAttributeModifiers().withModifierAdded(attribute, mod, slot));
		}
	}
}
