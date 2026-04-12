package dev.latvian.mods.kubejs.core.component;

import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.component.ItemAttributeModifiers;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.world.item.Item.BASE_ATTACK_DAMAGE_ID;
import static net.minecraft.world.item.Item.BASE_ATTACK_SPEED_ID;

@RemapPrefixForJS("kjs$")
public interface AttributeModifierAccessor extends AttributeModifierGetter, AttributeModifierSetter {
	default void kjs$setAttributeModifiersWithTooltip(List<ItemAttributeModifiers.Entry> modifiers) {
		var out = new ArrayList<ItemAttributeModifiers.Entry>(modifiers.size());
		for (var e : modifiers) {
			out.add(new ItemAttributeModifiers.Entry(e.attribute(), e.modifier(), e.slot(), ItemAttributeModifiers.Display.attributeModifiers()));
		}
		kjs$setAttributeModifiers(new ItemAttributeModifiers(out));
	}

	default void kjs$setAttributeModifiersWithoutTooltip(List<ItemAttributeModifiers.Entry> modifiers) {
		var out = new ArrayList<ItemAttributeModifiers.Entry>(modifiers.size());
		for (var e : modifiers) {
			out.add(new ItemAttributeModifiers.Entry(e.attribute(), e.modifier(), e.slot(), ItemAttributeModifiers.Display.hidden()));
		}
		kjs$setAttributeModifiers(new ItemAttributeModifiers(out));
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

		ItemAttributeModifiers.Display display = ItemAttributeModifiers.Display.attributeModifiers();
		for (var entry : oldMods.modifiers()) {
			if (entry.attribute().equals(Attributes.ATTACK_SPEED)) {
				display = entry.display();
				break;
			}
		}

		list.add(new ItemAttributeModifiers.Entry(
			Attributes.ATTACK_SPEED,
			new AttributeModifier(BASE_ATTACK_SPEED_ID, speed, AttributeModifier.Operation.ADD_VALUE),
			EquipmentSlotGroup.MAINHAND,
			display
		));

		kjs$setAttributeModifiers(new ItemAttributeModifiers(list));
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

		ItemAttributeModifiers.Display display = ItemAttributeModifiers.Display.attributeModifiers();
		for (var entry : oldMods.modifiers()) {
			if (entry.attribute().equals(Attributes.ATTACK_DAMAGE)) {
				display = entry.display();
				break;
			}
		}

		list.add(new ItemAttributeModifiers.Entry(
			Attributes.ATTACK_DAMAGE,
			new AttributeModifier(BASE_ATTACK_DAMAGE_ID, dmg, AttributeModifier.Operation.ADD_VALUE),
			EquipmentSlotGroup.MAINHAND,
			display
		));

		kjs$setAttributeModifiers(new ItemAttributeModifiers(list));
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
}
