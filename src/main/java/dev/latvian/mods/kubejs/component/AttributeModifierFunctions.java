package dev.latvian.mods.kubejs.component;

import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.rhino.util.HideFromJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.world.item.Item.BASE_ATTACK_DAMAGE_ID;
import static net.minecraft.world.item.Item.BASE_ATTACK_SPEED_ID;

@RemapPrefixForJS("kjs$")
@ReturnsSelf
public interface AttributeModifierFunctions {
	ItemAttributeModifiers kjs$getAttributeModifiers();

	default boolean kjs$hasAttributeModifier(Holder<Attribute> attribute, ResourceLocation id) {
		for (var entry : kjs$getAttributeModifiers().modifiers()) {
			if (entry.matches(attribute, id)) {
				return true;
			}
		}
		return false;
	}

	@Nullable
	default AttributeModifier kjs$getAttributeModifier(Holder<Attribute> attribute, ResourceLocation id) {
		for (var entry : kjs$getAttributeModifiers().modifiers()) {
			if (entry.matches(attribute, id)) {
				return entry.modifier();
			}
		}
		return null;
	}

	@HideFromJS
	void kjs$setAttributeModifiers(ItemAttributeModifiers modifiers);

	default void kjs$addAttributeModifier(Holder<Attribute> attribute, AttributeModifier mod, EquipmentSlotGroup slot) {
		kjs$setAttributeModifiers(kjs$getAttributeModifiers().withModifierAdded(attribute, mod, slot));
	}

	@ApiStatus.NonExtendable
	default void kjs$setAttributeModifiers(List<ItemAttributeModifiers.Entry> modifiers) {
		kjs$setAttributeModifiers(new ItemAttributeModifiers(modifiers, false));
	}

	@ApiStatus.NonExtendable
	default void kjs$setAttributeModifiersWithTooltip(List<ItemAttributeModifiers.Entry> modifiers) {
		kjs$setAttributeModifiers(new ItemAttributeModifiers(modifiers, true));
	}

	@Info("""
		Sets the attack speed of this item to the given value, **removing** all other modifiers to attack speed.
		Note that players have a default attack speed of 4.0, so this modifier is added on top of that.
		(Example: Swords have an attack speed of -2.4, leading to a total value of 1.6 without any other changes.)
		""")
	default void kjs$setAttackSpeed(double speed) {
		var oldMods = kjs$getAttributeModifiers();

		var list = new ArrayList<ItemAttributeModifiers.Entry>(oldMods.modifiers().size());
		for (var entry : oldMods.modifiers()) {
			if (entry.attribute().equals(Attributes.ATTACK_SPEED)) {
				continue;
			}

			list.add(entry);
		}
		list.add(new ItemAttributeModifiers.Entry(Attributes.ATTACK_SPEED,
			new AttributeModifier(BASE_ATTACK_SPEED_ID, speed, AttributeModifier.Operation.ADD_VALUE),
			EquipmentSlotGroup.MAINHAND));

		kjs$setAttributeModifiers(new ItemAttributeModifiers(list, oldMods.showInTooltip()));
	}

	@Info("""
		Sets the attack damage of this item to the given value, **removing** all other modifiers to attack damage.
		Note that since players have a default attack damage of 1.0, total damage will be (dmg + 1.0) before other modifiers.
		(In practice, this simply means that most weapons have this value set to 1 less than what you might think.)
		""")
	default void kjs$setAttackDamage(double dmg) {
		var oldMods = kjs$getAttributeModifiers();

		var list = new ArrayList<ItemAttributeModifiers.Entry>(oldMods.modifiers().size());
		for (var entry : oldMods.modifiers()) {
			if (entry.attribute().equals(Attributes.ATTACK_DAMAGE)) {
				continue;
			}

			list.add(entry);
		}
		list.add(new ItemAttributeModifiers.Entry(Attributes.ATTACK_DAMAGE,
			new AttributeModifier(BASE_ATTACK_DAMAGE_ID, dmg, AttributeModifier.Operation.ADD_VALUE),
			EquipmentSlotGroup.MAINHAND));

		kjs$setAttributeModifiers(new ItemAttributeModifiers(list, oldMods.showInTooltip()));
	}

	default double kjs$getAttackDamage() {
		var base = kjs$getBaseAttackDamage();
		var sum = base;

		for (var entry : kjs$getAttributeModifiers().modifiers()) {
			if (entry.matches(Attributes.ATTACK_DAMAGE, BASE_ATTACK_DAMAGE_ID)) {
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
			if (entry.matches(Attributes.ATTACK_SPEED, BASE_ATTACK_SPEED_ID)) {
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

	@Info("""
		Overrides the *base* attack speed of this item to be the given value, keeping other modifiers intact.
		Note that players have a default attack speed of 4.0, so this modifier is added on top of that.
		""")
	default void kjs$setBaseAttackSpeed(double speed) {
		kjs$addAttributeModifier(Attributes.ATTACK_SPEED,
			new AttributeModifier(BASE_ATTACK_SPEED_ID, speed, AttributeModifier.Operation.ADD_VALUE),
			EquipmentSlotGroup.MAINHAND);
	}

	@Info("""
		Overrides the *base* attack damage of this item to be the given value, keeping other modifiers intact.
		Note that since players have a default attack damage of 1.0, total damage will be (dmg + 1.0) before other modifiers.
		""")
	default void kjs$setBaseAttackDamage(double dmg) {
		kjs$addAttributeModifier(Attributes.ATTACK_DAMAGE,
			new AttributeModifier(BASE_ATTACK_DAMAGE_ID, dmg, AttributeModifier.Operation.ADD_VALUE),
			EquipmentSlotGroup.MAINHAND);
	}

	default double kjs$getBaseAttackDamage() {
		for (var modifier : kjs$getAttributeModifiers().modifiers()) {
			if (modifier.matches(Attributes.ATTACK_DAMAGE, BASE_ATTACK_DAMAGE_ID)) {
				return modifier.modifier().amount();
			}
		}
		return 0.0;
	}

	default double kjs$getBaseAttackSpeed() {
		for (var modifier : kjs$getAttributeModifiers().modifiers()) {
			if (modifier.matches(Attributes.ATTACK_SPEED, BASE_ATTACK_SPEED_ID)) {
				return modifier.modifier().amount();
			}
		}
		return 0.0;
	}
}
