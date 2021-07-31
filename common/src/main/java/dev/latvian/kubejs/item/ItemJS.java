package dev.latvian.kubejs.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import me.shedaniel.architectury.registry.fuel.FuelRegistry;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * @author LatvianModder
 */
public class ItemJS extends Item {
	private final ImmutableMultimap<Attribute, AttributeModifier> attributes;
	private ItemStack containerItem;

	public ItemJS(ItemBuilder p) {
		super(p.createItemProperties());
		Float attackDamage = p.getAttackDamage();
		Float attackSpeed = p.getAttackSpeed();

		if (p.burnTime > 0) {
			FuelRegistry.register(p.burnTime, this);
		}

		ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();

		if (attackDamage != null) {
			builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", attackDamage.doubleValue(), AttributeModifier.Operation.ADDITION));
		}

		if (attackSpeed != null) {
			builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", attackSpeed.doubleValue(), AttributeModifier.Operation.ADDITION));
		}

		attributes = builder.build();
	}

	@Override
	public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
		return slot == EquipmentSlot.MAINHAND ? attributes : super.getDefaultAttributeModifiers(slot);
	}
}