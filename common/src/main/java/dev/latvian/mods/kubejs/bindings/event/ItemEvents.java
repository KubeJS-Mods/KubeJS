package dev.latvian.mods.kubejs.bindings.event;

import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.item.FoodEatenEventJS;
import dev.latvian.mods.kubejs.item.ItemCraftedEventJS;
import dev.latvian.mods.kubejs.item.ItemDroppedEventJS;
import dev.latvian.mods.kubejs.item.ItemEntityInteractedEventJS;
import dev.latvian.mods.kubejs.item.ItemLeftClickedEventJS;
import dev.latvian.mods.kubejs.item.ItemModelPropertiesEventJS;
import dev.latvian.mods.kubejs.item.ItemModificationEventJS;
import dev.latvian.mods.kubejs.item.ItemPickedUpEventJS;
import dev.latvian.mods.kubejs.item.ItemRightClickedEmptyEventJS;
import dev.latvian.mods.kubejs.item.ItemRightClickedEventJS;
import dev.latvian.mods.kubejs.item.ItemSmeltedEventJS;
import dev.latvian.mods.kubejs.item.ItemTooltipEventJS;
import dev.latvian.mods.kubejs.item.custom.ItemArmorTierRegistryEventJS;
import dev.latvian.mods.kubejs.item.custom.ItemToolTierRegistryEventJS;

public interface ItemEvents {
	EventGroup GROUP = EventGroup.of("ItemEvents");
	EventHandler MODIFICATION = GROUP.startup("modification", () -> ItemModificationEventJS.class).legacy("item.modification");
	EventHandler TOOL_TIER_REGISTRY = GROUP.startup("toolTierRegistry", () -> ItemToolTierRegistryEventJS.class).legacy("item.registry.tool_tiers");
	EventHandler ARMOR_TIER_REGISTRY = GROUP.startup("armorTierRegistry", () -> ItemArmorTierRegistryEventJS.class).legacy("item.registry.armor_tiers");
	EventHandler RIGHT_CLICKED = GROUP.server("rightClicked", () -> ItemRightClickedEventJS.class).supportsNamespacedExtraId().cancelable().legacy("item.right_click");
	EventHandler PICKED_UP = GROUP.server("pickedUp", () -> ItemPickedUpEventJS.class).supportsNamespacedExtraId().legacy("item.pickup").cancelable();
	EventHandler DROPPED = GROUP.server("dropped", () -> ItemDroppedEventJS.class).supportsNamespacedExtraId().legacy("item.toss").cancelable();
	EventHandler ENTITY_INTERACTED = GROUP.server("entityInteracted", () -> ItemEntityInteractedEventJS.class).supportsNamespacedExtraId().cancelable().legacy("item.entity_interact");
	EventHandler CRAFTED = GROUP.server("crafted", () -> ItemCraftedEventJS.class).supportsNamespacedExtraId().legacy("item.crafted");
	EventHandler SMELTED = GROUP.server("smelted", () -> ItemSmeltedEventJS.class).supportsNamespacedExtraId().legacy("item.smelted");
	EventHandler FOOD_EATEN = GROUP.server("foodEaten", () -> FoodEatenEventJS.class).supportsNamespacedExtraId().cancelable().legacy("item.food_eaten");
	EventHandler RIGHT_CLICKED_EMPTY = GROUP.client("rightClickedEmpty", () -> ItemRightClickedEmptyEventJS.class).supportsNamespacedExtraId().legacy("item.right_click_empty");
	EventHandler LEFT_CLICKED = GROUP.client("leftClicked", () -> ItemLeftClickedEventJS.class).supportsNamespacedExtraId().legacy("item.left_click");
	EventHandler TOOLTIP = GROUP.client("tooltip", () -> ItemTooltipEventJS.class).legacy("item.tooltip");
	EventHandler MODEL_PROPERTIES = GROUP.startup("modelProperties", () -> ItemModelPropertiesEventJS.class).legacy("item.model_properties");
}
