package dev.latvian.mods.kubejs.item.custom;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import dev.latvian.mods.kubejs.KubeJSRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.PickaxeItem;

public class PickaxeItemBuilder extends HandheldItemBuilder {
	public PickaxeItemBuilder(ResourceLocation i) {
		super(i, 1F, -2.8F);
	}

	@Override
	public Item createObject() {
		return new PickaxeItem(toolTier, (int) attackDamageBaseline, speedBaseline, createItemProperties()) {
			private boolean modified = false;

			{
				defaultModifiers = ArrayListMultimap.<Attribute, AttributeModifier>create();
				defaultModifiers.putAll(defaultModifiers);
			}

			@Override
			public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot equipmentSlot) {
				if (!modified) {
					modified = true;
					attributes.forEach((r, m) -> defaultModifiers.put(KubeJSRegistries.attributes().get(r), m));
				}
				return super.getDefaultAttributeModifiers(equipmentSlot);
			}
		};
	}
}
