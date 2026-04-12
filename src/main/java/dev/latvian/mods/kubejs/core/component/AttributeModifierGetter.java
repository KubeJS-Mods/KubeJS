package dev.latvian.mods.kubejs.core.component;

import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import org.jspecify.annotations.Nullable;

@RemapPrefixForJS("kjs$")
@ReturnsSelf
public interface AttributeModifierGetter {
	ItemAttributeModifiers kjs$getAttributeModifiers();

	default boolean kjs$hasAttributeModifier(Holder<Attribute> attribute, Identifier id) {
		for (var entry : kjs$getAttributeModifiers().modifiers()) {
			if (entry.matches(attribute, id)) {
				return true;
			}
		}
		return false;
	}

	@Nullable
	default AttributeModifier kjs$getAttributeModifier(Holder<Attribute> attribute, Identifier id) {
		for (var entry : kjs$getAttributeModifiers().modifiers()) {
			if (entry.matches(attribute, id)) {
				return entry.modifier();
			}
		}
		return null;
	}

	default double kjs$getAttackDamage() {
		var base = kjs$getBaseAttackDamage();
		var sum = base;

		for (var entry : kjs$getAttributeModifiers().modifiers()) {
			if (entry.matches(Attributes.ATTACK_DAMAGE, net.minecraft.world.item.Item.BASE_ATTACK_DAMAGE_ID)) {
				continue;
			}

			var mod = entry.modifier();
			double d1 = mod.amount();

			sum += switch (mod.operation()) {
				case ADD_VALUE -> d1;
				case ADD_MULTIPLIED_BASE -> d1 * base;
				case ADD_MULTIPLIED_TOTAL -> d1 * sum;
			};
		}
		return sum;
	}

	default double kjs$getAttackSpeed() {
		var base = kjs$getBaseAttackSpeed();
		var sum = base;

		for (var entry : kjs$getAttributeModifiers().modifiers()) {
			if (entry.matches(Attributes.ATTACK_SPEED, net.minecraft.world.item.Item.BASE_ATTACK_SPEED_ID)) {
				continue;
			}

			var mod = entry.modifier();
			double d1 = mod.amount();

			sum += switch (mod.operation()) {
				case ADD_VALUE -> d1;
				case ADD_MULTIPLIED_BASE -> d1 * base;
				case ADD_MULTIPLIED_TOTAL -> d1 * sum;
			};
		}
		return sum;
	}

	default double kjs$getBaseAttackDamage() {
		for (var modifier : kjs$getAttributeModifiers().modifiers()) {
			if (modifier.matches(Attributes.ATTACK_DAMAGE, net.minecraft.world.item.Item.BASE_ATTACK_DAMAGE_ID)) {
				return modifier.modifier().amount();
			}
		}
		return 0.0;
	}

	default double kjs$getBaseAttackSpeed() {
		for (var modifier : kjs$getAttributeModifiers().modifiers()) {
			if (modifier.matches(Attributes.ATTACK_SPEED, net.minecraft.world.item.Item.BASE_ATTACK_SPEED_ID)) {
				return modifier.modifier().amount();
			}
		}
		return 0.0;
	}
}
