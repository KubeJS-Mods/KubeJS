package dev.latvian.mods.kubejs.core;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import dev.latvian.mods.kubejs.KubeJSRegistries;
import dev.latvian.mods.kubejs.item.FoodBuilder;
import dev.latvian.mods.kubejs.item.ItemBuilder;
import dev.latvian.mods.kubejs.item.MutableToolTier;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TieredItem;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * @author LatvianModder
 */
@RemapPrefixForJS("kjs$")
public interface ItemKJS {
	UUID KJS_BASE_ATTACK_DAMAGE_UUID = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
	UUID KJS_BASE_ATTACK_SPEED_UUID = UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3");
	UUID[] KJS_ARMOR_MODIFIER_UUID_PER_SLOT = new UUID[]{UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"), UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"), UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"), UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150")};

	@Nullable
	default ItemBuilder kjs$getItemBuilder() {
		throw new NoMixinException();
	}

	default Item kjs$self() {
		throw new NoMixinException();
	}

	default String kjs$getId() {
		return KubeJSRegistries.items().getId((Item) this).toString();
	}

	default void kjs$setItemBuilder(ItemBuilder b) {
		throw new NoMixinException();
	}

	default CompoundTag kjs$getTypeData() {
		throw new NoMixinException();
	}

	default void kjs$setMaxStackSize(int i) {
		throw new NoMixinException();
	}

	default void kjs$setMaxDamage(int i) {
		throw new NoMixinException();
	}

	default void kjs$setCraftingRemainder(Item i) {
		throw new NoMixinException();
	}

	default void kjs$setFireResistant(boolean b) {
		throw new NoMixinException();
	}

	default void kjs$setRarity(Rarity r) {
		throw new NoMixinException();
	}

	default void kjs$setBurnTime(int i) {
		throw new NoMixinException();
	}

	default void kjs$setFoodProperties(FoodProperties properties) {
		throw new NoMixinException();
	}

	default void kjs$setDigSpeed(float speed) {
		if (this instanceof DiggerItem diggerItem) {
			diggerItem.speed = speed;
		} else {
			throw new IllegalArgumentException("Item is not a digger item (axe, shovel, etc.)!");
		}
	}

	default float kjs$getDigSpeed() {
		if (this instanceof DiggerItem diggerItem) {
			return diggerItem.speed;
		} else {
			throw new IllegalArgumentException("Item is not a digger item (axe, shovel, etc.)!");
		}
	}

	default void kjs$setTier(Consumer<MutableToolTier> c) {
		if (this instanceof TieredItem tiered) {
			tiered.tier = Util.make(new MutableToolTier(tiered.tier), c);
		} else {
			throw new IllegalArgumentException("Item is not a tool/tiered item!");
		}
	}

	default void kjs$setFoodProperties(Consumer<FoodBuilder> consumer) {
		var fp = kjs$self().getFoodProperties();
		var builder = fp == null ? new FoodBuilder() : new FoodBuilder(fp);
		consumer.accept(builder);
		kjs$setFoodProperties(builder.build());
	}

	default void kjs$setAttackDamage(double attackDamage) {
		if (this instanceof ArmorItem) {
			throw new UnsupportedOperationException("Modifying attack damage of unsupported item: " + this);
		}

		kjs$removeAttribute(Attributes.ATTACK_DAMAGE, KJS_BASE_ATTACK_DAMAGE_UUID);
		kjs$addAttribute(Attributes.ATTACK_DAMAGE, KJS_BASE_ATTACK_DAMAGE_UUID, "Tool modifier", attackDamage, AttributeModifier.Operation.ADDITION);
	}

	default void kjs$setAttackSpeed(double attackSpeed) {
		if (this instanceof ArmorItem) {
			throw new UnsupportedOperationException("Modifying attack speed of unsupported item: " + this);
		}

		kjs$removeAttribute(Attributes.ATTACK_SPEED, KJS_BASE_ATTACK_SPEED_UUID);
		kjs$addAttribute(Attributes.ATTACK_SPEED, KJS_BASE_ATTACK_SPEED_UUID, "Tool modifier", attackSpeed, AttributeModifier.Operation.ADDITION);
	}

	default void kjs$setArmorProtection(double armorProtection) {
		if (!(this instanceof ArmorItem armor)) {
			throw new UnsupportedOperationException("Modifying armor value of unsupported item: " + this);
		}

		UUID uuid = KJS_ARMOR_MODIFIER_UUID_PER_SLOT[armor.getSlot().getIndex()];
		kjs$removeAttribute(Attributes.ARMOR, uuid);
		kjs$addAttribute(Attributes.ARMOR, uuid, "Armor modifier", armorProtection, AttributeModifier.Operation.ADDITION);
	}

	default void kjs$setArmorToughness(double armorToughness) {
		if (!(this instanceof ArmorItem armor)) {
			throw new UnsupportedOperationException("Modifying protection of unsupported item: " + this);
		}

		UUID uuid = KJS_ARMOR_MODIFIER_UUID_PER_SLOT[armor.getSlot().getIndex()];
		kjs$removeAttribute(Attributes.ARMOR_TOUGHNESS, uuid);
		kjs$addAttribute(Attributes.ARMOR_TOUGHNESS, uuid, "Armor modifier", armorToughness, AttributeModifier.Operation.ADDITION);
	}

	default void kjs$setArmorKnockbackResistance(double knockbackResistance) {
		if (!(this instanceof ArmorItem armor)) {
			throw new UnsupportedOperationException("Modifying protection of unsupported item: " + this);
		}

		UUID uuid = KJS_ARMOR_MODIFIER_UUID_PER_SLOT[armor.getSlot().getIndex()];
		kjs$removeAttribute(Attributes.KNOCKBACK_RESISTANCE, uuid);
		kjs$addAttribute(Attributes.KNOCKBACK_RESISTANCE, uuid, "Armor modifier", knockbackResistance, AttributeModifier.Operation.ADDITION);
	}

	default void kjs$addAttribute(Attribute attribute, UUID uuid, String name, double d, AttributeModifier.Operation operation) {
		if (!(this instanceof ModifiableItemKJS modifiableItemKJS)) {
			throw new UnsupportedOperationException("Adding attribute in unsupported item: " + this);
		}

		Multimap<Attribute, AttributeModifier> attributes = modifiableItemKJS.getMutableAttributeMap();
		attributes.put(attribute, new AttributeModifier(uuid, name, d, operation));
	}

	default void kjs$removeAttribute(Attribute attribute, UUID uuid) {
		if (!(this instanceof ModifiableItemKJS modifiableItem)) {
			throw new UnsupportedOperationException("Removing attribute in unsupported item: " + this);
		}

		Multimap<Attribute, AttributeModifier> attributes = modifiableItem.getMutableAttributeMap();
		Collection<AttributeModifier> modifiers = attributes.get(attribute);
		Optional<AttributeModifier> value = modifiers.stream().filter(modifier -> uuid.equals(modifier.getId())).findFirst();
		value.ifPresent(modifier -> attributes.remove(attribute, modifier));
	}

	default List<AttributeModifier> kjs$getAttributes(Attribute attribute) {
		if (!(this instanceof ModifiableItemKJS modifiableItem)) {
			throw new UnsupportedOperationException("Getting attribute in unsupported item: " + this);
		}

		Multimap<Attribute, AttributeModifier> attributes = modifiableItem.getAttributeMapKJS();
		return ImmutableList.copyOf(attributes.get(attribute));
	}
}
