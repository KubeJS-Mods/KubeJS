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
	EventHandler MODIFICATION = GROUP.startup("modification", () -> ItemModificationEventJS.class);
	EventHandler TOOL_TIER_REGISTRY = GROUP.startup("toolTierRegistry", () -> ItemToolTierRegistryEventJS.class);
	EventHandler ARMOR_TIER_REGISTRY = GROUP.startup("armorTierRegistry", () -> ItemArmorTierRegistryEventJS.class);
	EventHandler RIGHT_CLICKED = GROUP.server("rightClicked", () -> ItemRightClickedEventJS.class).supportsNamespacedExtraId().cancelable();
	EventHandler PICKED_UP = GROUP.server("pickedUp", () -> ItemPickedUpEventJS.class).supportsNamespacedExtraId().cancelable();
	EventHandler DROPPED = GROUP.server("dropped", () -> ItemDroppedEventJS.class).supportsNamespacedExtraId().cancelable();
	EventHandler ENTITY_INTERACTED = GROUP.server("entityInteracted", () -> ItemEntityInteractedEventJS.class).supportsNamespacedExtraId().cancelable();
	EventHandler CRAFTED = GROUP.server("crafted", () -> ItemCraftedEventJS.class).supportsNamespacedExtraId();
	EventHandler SMELTED = GROUP.server("smelted", () -> ItemSmeltedEventJS.class).supportsNamespacedExtraId();
	EventHandler FOOD_EATEN = GROUP.server("foodEaten", () -> FoodEatenEventJS.class).supportsNamespacedExtraId().cancelable();
	EventHandler RIGHT_CLICKED_EMPTY = GROUP.client("rightClickedEmpty", () -> ItemRightClickedEmptyEventJS.class).supportsNamespacedExtraId();
	EventHandler LEFT_CLICKED = GROUP.client("leftClicked", () -> ItemLeftClickedEventJS.class).supportsNamespacedExtraId();
	EventHandler TOOLTIP = GROUP.client("tooltip", () -> ItemTooltipEventJS.class);
	EventHandler MODEL_PROPERTIES = GROUP.startup("modelProperties", () -> ItemModelPropertiesEventJS.class);
}
