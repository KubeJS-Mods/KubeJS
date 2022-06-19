package dev.latvian.mods.kubejs.item;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import dev.latvian.mods.kubejs.core.ItemKJS;
import dev.latvian.mods.kubejs.core.ModifiableItemKJS;
import net.minecraft.Util;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.TridentItem;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * @author LatvianModder
 */
public class ItemModificationProperties {

	private static final UUID BASE_ATTACK_DAMAGE_UUID = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
	private static final UUID BASE_ATTACK_SPEED_UUID = UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3");
	private static final UUID[] ARMOR_MODIFIER_UUID_PER_SLOT = new UUID[]{UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"), UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"), UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"), UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150")};

	public final ItemKJS item;

	public ItemModificationProperties(ItemKJS i) {
		item = i;
	}

	public void setMaxStackSize(int i) {
		item.setMaxStackSizeKJS(i);
	}

	public void setMaxDamage(int i) {
		item.setMaxDamageKJS(i);
	}

	public void setBurnTime(int i) {
		item.setBurnTimeKJS(i);
	}

	public void setCraftingRemainder(Item i) {
		item.setCraftingRemainderKJS(i);
	}

	public void setFireResistant(boolean b) {
		item.setFireResistantKJS(b);
	}

	public void setRarity(Rarity r) {
		item.setRarityKJS(r);
	}

	public void setDigSpeed(float speed) {
		if (item instanceof DiggerItem diggerItem) {
			diggerItem.speed = speed;
		} else {
			throw new IllegalArgumentException("Item is not a digger item (axe, shovel, etc.)!");
		}
	}

	public void setTier(Consumer<MutableToolTier> c) {
		if (item instanceof TieredItem tiered) {
			tiered.tier = Util.make(new MutableToolTier(tiered.tier), c);
		} else {
			throw new IllegalArgumentException("Item is not a tool/tiered item!");
		}
	}

	public void setFoodProperties(Consumer<FoodBuilder> consumer) {
		var originalItem = (Item) item;
		var fp = originalItem.getFoodProperties();
		var builder = fp == null ? new FoodBuilder() : new FoodBuilder(fp);
		consumer.accept(builder);
		item.setFoodPropertiesKJS(builder.build());
	}

	public void setAttackDamage(double attackDamage) {
		if (item instanceof ArmorItem) {
			throw new UnsupportedOperationException("Modifying attack damage of unsupported item: " + item);
		}
		removeAttribute(Attributes.ATTACK_DAMAGE, BASE_ATTACK_DAMAGE_UUID);
		addAttribute(Attributes.ATTACK_DAMAGE, BASE_ATTACK_DAMAGE_UUID, "Tool modifier", attackDamage, AttributeModifier.Operation.ADDITION);
	}

	public void setAttackSpeed(double attackSpeed) {
		if (item instanceof ArmorItem) {
			throw new UnsupportedOperationException("Modifying attack speed of unsupported item: " + item);
		}
		removeAttribute(Attributes.ATTACK_SPEED, BASE_ATTACK_SPEED_UUID);
		addAttribute(Attributes.ATTACK_SPEED, BASE_ATTACK_SPEED_UUID, "Tool modifier", attackSpeed, AttributeModifier.Operation.ADDITION);
	}

	public void setArmorProtection(double armorProtection) {
		if (!(item instanceof ArmorItem armor)) {
			throw new UnsupportedOperationException("Modifying armor value of unsupported item: " + item.toString());
		}
		UUID uuid = ARMOR_MODIFIER_UUID_PER_SLOT[armor.getSlot().getIndex()];
		removeAttribute(Attributes.ARMOR, uuid);
		addAttribute(Attributes.ARMOR, uuid, "Armor modifier", armorProtection, AttributeModifier.Operation.ADDITION);
	}

	public void setArmorToughness(double armorToughness) {
		if (!(item instanceof ArmorItem armor)) {
			throw new UnsupportedOperationException("Modifying protection of unsupported item: " + item.toString());
		}
		UUID uuid = ARMOR_MODIFIER_UUID_PER_SLOT[armor.getSlot().getIndex()];
		removeAttribute(Attributes.ARMOR_TOUGHNESS, uuid);
		addAttribute(Attributes.ARMOR_TOUGHNESS, uuid, "Armor modifier", armorToughness, AttributeModifier.Operation.ADDITION);
	}

	public void setArmorKnockbackResistance(double knockbackResistance) {
		if (!(item instanceof ArmorItem armor)) {
			throw new UnsupportedOperationException("Modifying protection of unsupported item: " + item.toString());
		}
		UUID uuid = ARMOR_MODIFIER_UUID_PER_SLOT[armor.getSlot().getIndex()];
		removeAttribute(Attributes.KNOCKBACK_RESISTANCE, uuid);
		addAttribute(Attributes.KNOCKBACK_RESISTANCE, uuid, "Armor modifier", knockbackResistance, AttributeModifier.Operation.ADDITION);
	}

	public void addAttribute(Attribute attribute, UUID uuid, String name, double d, AttributeModifier.Operation operation) {
		if (!(item instanceof ModifiableItemKJS modifiableItemKJS)) {
			throw new UnsupportedOperationException("Adding attribute in unsupported item: " + item.toString());
		}
		Multimap<Attribute, AttributeModifier> attributes = modifiableItemKJS.getMutableAttributeMap();
		attributes.put(attribute, new AttributeModifier(uuid, name, d, operation));
	}

	public void removeAttribute(Attribute attribute, UUID uuid) {
		if (!(item instanceof ModifiableItemKJS modifiableItem)) {
			throw new UnsupportedOperationException("Removing attribute in unsupported item: " + item.toString());
		}
		Multimap<Attribute, AttributeModifier> attributes = modifiableItem.getMutableAttributeMap();
		Collection<AttributeModifier> modifiers = attributes.get(attribute);
		Optional<AttributeModifier> value = modifiers.stream().filter(modifier -> uuid.equals(modifier.getId())).findFirst();
		value.ifPresent(modifier -> attributes.remove(attribute, modifier));
	}
}
