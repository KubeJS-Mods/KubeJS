package dev.latvian.mods.kubejs.item.custom;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import dev.latvian.mods.kubejs.KubeJSRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item;

public class AxeItemBuilder extends HandheldItemBuilder {
	public AxeItemBuilder(ResourceLocation i) {
		super(i, 6F, -3.1F);
	}

	@Override
	public Item createObject() {
		return new AxeItem(toolTier, attackDamageBaseline, speedBaseline, createItemProperties()) {
			private boolean modified = false;

			{
				defaultModifiers = ArrayListMultimap.create(defaultModifiers);
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
