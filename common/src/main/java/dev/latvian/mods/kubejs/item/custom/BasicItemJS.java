package dev.latvian.mods.kubejs.item.custom;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import dev.architectury.registry.fuel.FuelRegistry;
import dev.latvian.mods.kubejs.KubeJSRegistries;
import dev.latvian.mods.kubejs.item.ItemBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class BasicItemJS extends Item {
	public static class Builder extends ItemBuilder {
		public Builder(ResourceLocation i) {
			super(i);
		}

		@Override
		public Item createObject() {
			return new BasicItemJS(this);
		}
	}

	private final Multimap<Attribute, AttributeModifier> attributes;
	private boolean modified = false;

	public BasicItemJS(ItemBuilder p) {
		super(p.createItemProperties());
		if (p.burnTime > 0) {
			FuelRegistry.register(p.burnTime, this);
		}

		attributes = ArrayListMultimap.create();
	}

	@Override
	public void fillItemCategory(CreativeModeTab category, NonNullList<ItemStack> stacks) {
		if (kjs$getItemBuilder().subtypes != null) {
			stacks.addAll(kjs$getItemBuilder().subtypes.apply(new ItemStack(this)));
		} else {
			super.fillItemCategory(category, stacks);
		}
	}

	@Override
	public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
		if (!modified) {
			kjs$getItemBuilder().attributes.forEach((r, m) -> attributes.put(KubeJSRegistries.attributes().get(r), m));
			modified = true;
		}

		return slot == EquipmentSlot.MAINHAND ? attributes : super.getDefaultAttributeModifiers(slot);
	}
}