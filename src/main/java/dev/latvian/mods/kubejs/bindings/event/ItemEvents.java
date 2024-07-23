package dev.latvian.mods.kubejs.bindings.event;

import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.event.EventTargetType;
import dev.latvian.mods.kubejs.event.TargetedEventHandler;
import dev.latvian.mods.kubejs.item.DynamicItemTooltipsKubeEvent;
import dev.latvian.mods.kubejs.item.FoodEatenKubeEvent;
import dev.latvian.mods.kubejs.item.ItemClickedKubeEvent;
import dev.latvian.mods.kubejs.item.ItemCraftedKubeEvent;
import dev.latvian.mods.kubejs.item.ItemDestroyedKubeEvent;
import dev.latvian.mods.kubejs.item.ItemDroppedKubeEvent;
import dev.latvian.mods.kubejs.item.ItemEntityInteractedKubeEvent;
import dev.latvian.mods.kubejs.item.ItemModelPropertiesKubeEvent;
import dev.latvian.mods.kubejs.item.ItemModificationKubeEvent;
import dev.latvian.mods.kubejs.item.ItemPickedUpKubeEvent;
import dev.latvian.mods.kubejs.item.ItemSmeltedKubeEvent;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.kubejs.item.ModifyItemTooltipsKubeEvent;
import dev.latvian.mods.kubejs.item.custom.ItemToolTierRegistryKubeEvent;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

public interface ItemEvents {
	EventGroup GROUP = EventGroup.of("ItemEvents");
	EventTargetType<ResourceKey<Item>> TARGET = EventTargetType.registryKey(Registries.ITEM, Item.class);

	EventHandler MODIFICATION = GROUP.startup("modification", () -> ItemModificationKubeEvent.class);
	EventHandler TOOL_TIER_REGISTRY = GROUP.startup("toolTierRegistry", () -> ItemToolTierRegistryKubeEvent.class);
	TargetedEventHandler<ResourceKey<Item>> RIGHT_CLICKED = GROUP.common("rightClicked", () -> ItemClickedKubeEvent.class).hasResult(ItemStackJS.TYPE_INFO).supportsTarget(TARGET);
	TargetedEventHandler<ResourceKey<Item>> CAN_PICK_UP = GROUP.common("canPickUp", () -> ItemPickedUpKubeEvent.class).hasResult().supportsTarget(TARGET);
	TargetedEventHandler<ResourceKey<Item>> PICKED_UP = GROUP.common("pickedUp", () -> ItemPickedUpKubeEvent.class).supportsTarget(TARGET);
	TargetedEventHandler<ResourceKey<Item>> DROPPED = GROUP.common("dropped", () -> ItemDroppedKubeEvent.class).hasResult().supportsTarget(TARGET);
	TargetedEventHandler<ResourceKey<Item>> ENTITY_INTERACTED = GROUP.common("entityInteracted", () -> ItemEntityInteractedKubeEvent.class).hasResult().supportsTarget(TARGET);
	TargetedEventHandler<ResourceKey<Item>> CRAFTED = GROUP.common("crafted", () -> ItemCraftedKubeEvent.class).supportsTarget(TARGET);
	TargetedEventHandler<ResourceKey<Item>> SMELTED = GROUP.common("smelted", () -> ItemSmeltedKubeEvent.class).supportsTarget(TARGET);
	TargetedEventHandler<ResourceKey<Item>> FOOD_EATEN = GROUP.common("foodEaten", () -> FoodEatenKubeEvent.class).hasResult().supportsTarget(TARGET);
	EventHandler MODIFY_TOOLTIPS = GROUP.common("modifyTooltips", () -> ModifyItemTooltipsKubeEvent.class);
	TargetedEventHandler<String> DYNAMIC_TOOLTIPS = GROUP.client("dynamicTooltips", () -> DynamicItemTooltipsKubeEvent.class).requiredTarget(EventTargetType.STRING);
	EventHandler MODEL_PROPERTIES = GROUP.startup("modelProperties", () -> ItemModelPropertiesKubeEvent.class);
	TargetedEventHandler<ResourceKey<Item>> FIRST_RIGHT_CLICKED = GROUP.common("firstRightClicked", () -> ItemClickedKubeEvent.class).supportsTarget(TARGET);
	TargetedEventHandler<ResourceKey<Item>> FIRST_LEFT_CLICKED = GROUP.common("firstLeftClicked", () -> ItemClickedKubeEvent.class).supportsTarget(TARGET);
	TargetedEventHandler<ResourceKey<Item>> ITEM_DESTROYED = GROUP.common("destroyed", () -> ItemDestroyedKubeEvent.class).supportsTarget(TARGET);
}
