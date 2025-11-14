package dev.latvian.mods.kubejs.component;

import dev.latvian.mods.kubejs.color.KubeColor;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
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

@ReturnsSelf
@RemapPrefixForJS("kjs$")
public interface ItemComponentFunctions extends ComponentFunctions {
	@Override
	<T> ItemComponentFunctions kjs$override(DataComponentType<T> type, @Nullable T value);

	default ItemComponentFunctions kjs$setMaxStackSize(int size) {
		return kjs$override(DataComponents.MAX_STACK_SIZE, size);
	}

	default ItemComponentFunctions kjs$setMaxDamage(int maxDamage) {
		return kjs$override(DataComponents.MAX_DAMAGE, maxDamage);
	}

	default ItemComponentFunctions kjs$setDamage(int damage) {
		return kjs$override(DataComponents.DAMAGE, damage);
	}

	default ItemComponentFunctions kjs$setUnbreakable() {
		return kjs$override(DataComponents.UNBREAKABLE, new Unbreakable(false));
	}

	default ItemComponentFunctions kjs$setUnbreakableWithTooltip() {
		return kjs$override(DataComponents.UNBREAKABLE, new Unbreakable(true));
	}

	default ItemComponentFunctions kjs$setItemName(Component component) {
		return kjs$override(DataComponents.ITEM_NAME, component);
	}

	default ItemComponentFunctions kjs$setRepairCost(int repairCost) {
		return kjs$override(DataComponents.REPAIR_COST, repairCost);
	}

	default ItemComponentFunctions kjs$setFood(FoodProperties foodProperties) {
		return kjs$override(DataComponents.FOOD, foodProperties);
	}

	default ItemComponentFunctions kjs$setFood(int nutrition, float saturation) {
		return kjs$setFood(new FoodProperties.Builder().nutrition(nutrition).saturationModifier(saturation).build());
	}

	default ItemComponentFunctions kjs$setFireResistant() {
		kjs$setUnit(DataComponents.FIRE_RESISTANT);
		return this;
	}

	default ItemComponentFunctions kjs$setTool(Tool tool) {
		return kjs$override(DataComponents.TOOL, tool);
	}

	default ItemComponentFunctions kjs$setMapItemColor(KubeColor color) {
		return kjs$override(DataComponents.MAP_COLOR, new MapItemColor(color.kjs$getRGB()));
	}

	default ItemComponentFunctions kjs$setChargedProjectiles(List<ItemStack> items) {
		return kjs$override(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.of(items));
	}

	default ItemComponentFunctions kjs$setBundleContents(List<ItemStack> items) {
		return kjs$override(DataComponents.BUNDLE_CONTENTS, new BundleContents(items));
	}

	default ItemComponentFunctions kjs$setBucketEntityData(CompoundTag tag) {
		return kjs$override(DataComponents.BUCKET_ENTITY_DATA, CustomData.of(tag));
	}

	default ItemComponentFunctions kjs$setBlockEntityData(CompoundTag tag) {
		return kjs$override(DataComponents.BLOCK_ENTITY_DATA, CustomData.of(tag));
	}

	default ItemComponentFunctions kjs$setInstrument(Holder<Instrument> instrument) {
		return kjs$override(DataComponents.INSTRUMENT, instrument);
	}

	default ItemComponentFunctions kjs$setFireworkExplosion(FireworkExplosion explosion) {
		return kjs$override(DataComponents.FIREWORK_EXPLOSION, explosion);
	}

	default ItemComponentFunctions kjs$setFireworks(Fireworks fireworks) {
		return kjs$override(DataComponents.FIREWORKS, fireworks);
	}

	default ItemComponentFunctions kjs$setNoteBlockSound(ResourceLocation id) {
		return kjs$override(DataComponents.NOTE_BLOCK_SOUND, id);
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

	default ItemComponentFunctions kjs$setAttributeModifiers(List<ItemAttributeModifiers.Entry> modifiers) {
		return kjs$override(DataComponents.ATTRIBUTE_MODIFIERS, new ItemAttributeModifiers(modifiers, false));
	}

	default ItemComponentFunctions kjs$setAttributeModifiersWithTooltip(List<ItemAttributeModifiers.Entry> modifiers) {
		return kjs$override(DataComponents.ATTRIBUTE_MODIFIERS, new ItemAttributeModifiers(modifiers, true));
	}

	@Info("""
		Sets the attack speed of this item to the given value, **removing** all other modifiers to attack speed.
		Note that players have a default attack speed of 4.0, so this modifier is added on top of that.
		(Example: Swords have an attack speed of -2.4, leading to a total value of 1.6 without any other changes.)
		""")
	default ItemComponentFunctions kjs$setAttackSpeed(double speed) {
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

		return kjs$override(DataComponents.ATTRIBUTE_MODIFIERS, new ItemAttributeModifiers(list, oldMods.showInTooltip()));
	}

	@Info("""
		Sets the attack damage of this item to the given value, **removing** all other modifiers to attack damage.
		Note that since players have a default attack damage of 1.0, total damage will be (dmg + 1.0) before other modifiers.
		(In practice, this simply means that most weapons have this value set to 1 less than what you might think.)
		""")
	default ItemComponentFunctions kjs$setAttackDamage(double dmg) {
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

		return kjs$override(DataComponents.ATTRIBUTE_MODIFIERS, new ItemAttributeModifiers(list, oldMods.showInTooltip()));
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
	default ItemComponentFunctions kjs$setBaseAttackSpeed(double speed) {
		return kjs$override(DataComponents.ATTRIBUTE_MODIFIERS, kjs$getAttributeModifiers()
			.withModifierAdded(Attributes.ATTACK_SPEED,
				new AttributeModifier(BASE_ATTACK_SPEED_ID, speed, Operation.ADD_VALUE),
				EquipmentSlotGroup.MAINHAND));
	}

	@Info("""
		Overrides the *base* attack damage of this item to be the given value, keeping other modifiers intact.
		Note that since players have a default attack damage of 1.0, total damage will be (dmg + 1.0) before other modifiers.
		""")
	default ItemComponentFunctions kjs$setBaseAttackDamage(double dmg) {
		return kjs$override(DataComponents.ATTRIBUTE_MODIFIERS, kjs$getAttributeModifiers()
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
