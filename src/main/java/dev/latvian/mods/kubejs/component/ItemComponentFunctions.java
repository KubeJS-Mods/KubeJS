package dev.latvian.mods.kubejs.component;

import dev.latvian.mods.kubejs.color.KubeColor;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Instrument;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.item.component.ChargedProjectiles;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.item.component.Fireworks;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.MapItemColor;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.component.Unbreakable;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import static net.minecraft.world.item.Item.BASE_ATTACK_DAMAGE_ID;
import static net.minecraft.world.item.Item.BASE_ATTACK_SPEED_ID;

@RemapPrefixForJS("kjs$")
public interface ItemComponentFunctions extends ComponentFunctions {
	default void kjs$setMaxStackSize(int size) {
		kjs$override(DataComponents.MAX_STACK_SIZE, size);
	}

	default void kjs$setMaxDamage(int maxDamage) {
		kjs$override(DataComponents.MAX_DAMAGE, maxDamage);
	}

	default void kjs$setDamage(int damage) {
		kjs$override(DataComponents.DAMAGE, damage);
	}

	default void kjs$setUnbreakable() {
		kjs$override(DataComponents.UNBREAKABLE, new Unbreakable(false));
	}

	default void kjs$setUnbreakableWithTooltip() {
		kjs$override(DataComponents.UNBREAKABLE, new Unbreakable(true));
	}

	default void kjs$setItemName(Component component) {
		kjs$override(DataComponents.ITEM_NAME, component);
	}

	default void kjs$setRepairCost(int repairCost) {
		kjs$override(DataComponents.REPAIR_COST, repairCost);
	}

	default void kjs$setFood(FoodProperties foodProperties) {
		kjs$override(DataComponents.FOOD, foodProperties);
	}

	default void kjs$setFood(int nutrition, float saturation) {
		kjs$setFood(new FoodProperties.Builder().nutrition(nutrition).saturationModifier(saturation).build());
	}

	default void kjs$setFireResistant() {
		kjs$setUnit(DataComponents.FIRE_RESISTANT);
	}

	default void kjs$setTool(Tool tool) {
		kjs$override(DataComponents.TOOL, tool);
	}

	default void kjs$setMapItemColor(KubeColor color) {
		kjs$override(DataComponents.MAP_COLOR, new MapItemColor(color.kjs$getRGB()));
	}

	default void kjs$setChargedProjectiles(List<ItemStack> items) {
		kjs$override(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.of(items));
	}

	default void kjs$setBundleContents(List<ItemStack> items) {
		kjs$override(DataComponents.BUNDLE_CONTENTS, new BundleContents(items));
	}

	default void kjs$setBucketEntityData(CompoundTag tag) {
		kjs$override(DataComponents.BUCKET_ENTITY_DATA, CustomData.of(tag));
	}

	default void kjs$setBlockEntityData(CompoundTag tag) {
		kjs$override(DataComponents.BLOCK_ENTITY_DATA, CustomData.of(tag));
	}

	default void kjs$setInstrument(Holder<Instrument> instrument) {
		kjs$override(DataComponents.INSTRUMENT, instrument);
	}

	default void kjs$setFireworkExplosion(FireworkExplosion explosion) {
		kjs$override(DataComponents.FIREWORK_EXPLOSION, explosion);
	}

	default void kjs$setFireworks(Fireworks fireworks) {
		kjs$override(DataComponents.FIREWORKS, fireworks);
	}

	default void kjs$setNoteBlockSound(ResourceLocation id) {
		kjs$override(DataComponents.NOTE_BLOCK_SOUND, id);
	}

	default ItemAttributeModifiers kjs$getAttributeModifiers() {
		var mods = kjs$get(DataComponents.ATTRIBUTE_MODIFIERS);
		return mods == null ? new ItemAttributeModifiers(List.of(), true) : mods;
	}

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

	default void kjs$setAttributeModifiers(List<ItemAttributeModifiers.Entry> modifiers) {
		kjs$override(DataComponents.ATTRIBUTE_MODIFIERS, new ItemAttributeModifiers(modifiers, false));
	}

	default void kjs$setAttributeModifiersWithTooltip(List<ItemAttributeModifiers.Entry> modifiers) {
		kjs$override(DataComponents.ATTRIBUTE_MODIFIERS, new ItemAttributeModifiers(modifiers, true));
	}

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
			new AttributeModifier(BASE_ATTACK_SPEED_ID, speed, Operation.ADD_VALUE),
			EquipmentSlotGroup.MAINHAND));

		kjs$override(DataComponents.ATTRIBUTE_MODIFIERS, new ItemAttributeModifiers(list, oldMods.showInTooltip()));
	}

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
			new AttributeModifier(BASE_ATTACK_DAMAGE_ID, dmg, Operation.ADD_VALUE),
			EquipmentSlotGroup.MAINHAND));

		kjs$override(DataComponents.ATTRIBUTE_MODIFIERS, new ItemAttributeModifiers(list, oldMods.showInTooltip()));
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

	default void kjs$setBaseAttackSpeed(double speed) {
		kjs$override(DataComponents.ATTRIBUTE_MODIFIERS, kjs$getAttributeModifiers()
			.withModifierAdded(Attributes.ATTACK_SPEED,
				new AttributeModifier(BASE_ATTACK_SPEED_ID, speed, Operation.ADD_VALUE),
				EquipmentSlotGroup.MAINHAND));
	}

	default void kjs$setBaseAttackDamage(double dmg) {
		kjs$override(DataComponents.ATTRIBUTE_MODIFIERS, kjs$getAttributeModifiers()
			.withModifierAdded(Attributes.ATTACK_DAMAGE,
				new AttributeModifier(BASE_ATTACK_DAMAGE_ID, dmg, Operation.ADD_VALUE),
				EquipmentSlotGroup.MAINHAND));
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
