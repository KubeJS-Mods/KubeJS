package dev.latvian.mods.kubejs.item.custom;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import dev.latvian.mods.kubejs.registry.KubeJSRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;

public class HoeItemBuilder extends HandheldItemBuilder {
	public HoeItemBuilder(ResourceLocation i) {
		super(i, -2F, -1F);
	}

	@Override
	public Item createObject() {
		return new HoeItem(toolTier, (int) attackDamageBaseline, speedBaseline, createItemProperties()) {
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
