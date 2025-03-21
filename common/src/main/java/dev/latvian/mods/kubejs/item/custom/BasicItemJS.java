package dev.latvian.mods.kubejs.item.custom;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import dev.architectury.registry.fuel.FuelRegistry;
import dev.latvian.mods.kubejs.KubeJSRegistries;
import dev.latvian.mods.kubejs.item.ItemBuilder;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Collection;
import java.util.function.Function;

/**
 * @author LatvianModder
 */
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
	private final Multimap<ResourceLocation, AttributeModifier> modifiers;
	private boolean modified = false;
	private final Function<ItemStackJS, Collection<ItemStackJS>> subtypes;
	private final CreativeModeTab group;

	public BasicItemJS(ItemBuilder p) {
		super(p.createItemProperties());
		if (p.burnTime > 0) {
			FuelRegistry.register(p.burnTime, this);
		}

		subtypes = p.subtypes;
		attributes = ArrayListMultimap.create();
		modifiers = p.attributes;
		group = p.group;
	}

	@Override
	public void fillItemCategory(CreativeModeTab category, NonNullList<ItemStack> stacks) {
		if (subtypes != null && category == group) {
			for (var stack : subtypes.apply(ItemStackJS.of(this))) {
				stacks.add(stack.getItemStack());
			}
		} else {
			super.fillItemCategory(category, stacks);
		}
	}

	@Override
	public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
		if (!modified) {
			modifiers.forEach((r, m) -> attributes.put(KubeJSRegistries.attributes().get(r), m));
			modified = true;
		}
		return slot == EquipmentSlot.MAINHAND ? attributes : super.getDefaultAttributeModifiers(slot);
	}
}