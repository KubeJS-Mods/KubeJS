package dev.latvian.mods.kubejs.item.custom;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import dev.architectury.registry.fuel.FuelRegistry;
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

	private final ImmutableMultimap<Attribute, AttributeModifier> attributes;
	private final Function<ItemStackJS, Collection<ItemStackJS>> subtypes;

	public BasicItemJS(ItemBuilder p) {
		super(p.createItemProperties());
		// var attackDamage = p.getAttackDamage();
		// var attackSpeed = p.getAttackSpeed();

		if (p.burnTime > 0) {
			FuelRegistry.register(p.burnTime, this);
		}

		subtypes = p.subtypes;

		ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();

		/*
		if (attackDamage != null) {
			builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", attackDamage.doubleValue(), AttributeModifier.Operation.ADDITION));
		}

		if (attackSpeed != null) {
			builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", attackSpeed.doubleValue(), AttributeModifier.Operation.ADDITION));
		}
		*/

		attributes = builder.build();
	}

	@Override
	public void fillItemCategory(CreativeModeTab category, NonNullList<ItemStack> stacks) {
		if (subtypes != null) {
			for (var stack : subtypes.apply(ItemStackJS.of(this))) {
				stacks.add(stack.getItemStack());
			}
		} else {
			super.fillItemCategory(category, stacks);
		}
	}

	@Override
	public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
		return slot == EquipmentSlot.MAINHAND ? attributes : super.getDefaultAttributeModifiers(slot);
	}
}