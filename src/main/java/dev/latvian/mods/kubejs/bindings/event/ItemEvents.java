package dev.latvian.mods.kubejs.bindings.event;

import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.event.Extra;
import dev.latvian.mods.kubejs.event.SpecializedEventHandler;
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
import dev.latvian.mods.kubejs.item.ItemTooltipKubeEvent;
import dev.latvian.mods.kubejs.item.custom.ItemToolTierRegistryKubeEvent;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

public interface ItemEvents {
	EventGroup GROUP = EventGroup.of("ItemEvents");
	Extra<ResourceKey<Item>> SUPPORTS_ITEM = Extra.registryKey(Registries.ITEM, Item.class);

	EventHandler MODIFICATION = GROUP.startup("modification", () -> ItemModificationKubeEvent.class);
	EventHandler TOOL_TIER_REGISTRY = GROUP.startup("toolTierRegistry", () -> ItemToolTierRegistryKubeEvent.class);
	SpecializedEventHandler<ResourceKey<Item>> RIGHT_CLICKED = GROUP.common("rightClicked", SUPPORTS_ITEM, () -> ItemClickedKubeEvent.class).hasResult(ItemStackJS.TYPE_INFO);
	SpecializedEventHandler<ResourceKey<Item>> CAN_PICK_UP = GROUP.common("canPickUp", SUPPORTS_ITEM, () -> ItemPickedUpKubeEvent.class).hasResult();
	SpecializedEventHandler<ResourceKey<Item>> PICKED_UP = GROUP.common("pickedUp", SUPPORTS_ITEM, () -> ItemPickedUpKubeEvent.class);
	SpecializedEventHandler<ResourceKey<Item>> DROPPED = GROUP.common("dropped", SUPPORTS_ITEM, () -> ItemDroppedKubeEvent.class).hasResult();
	SpecializedEventHandler<ResourceKey<Item>> ENTITY_INTERACTED = GROUP.common("entityInteracted", SUPPORTS_ITEM, () -> ItemEntityInteractedKubeEvent.class).hasResult();
	SpecializedEventHandler<ResourceKey<Item>> CRAFTED = GROUP.common("crafted", SUPPORTS_ITEM, () -> ItemCraftedKubeEvent.class);
	SpecializedEventHandler<ResourceKey<Item>> SMELTED = GROUP.common("smelted", SUPPORTS_ITEM, () -> ItemSmeltedKubeEvent.class);
	SpecializedEventHandler<ResourceKey<Item>> FOOD_EATEN = GROUP.common("foodEaten", SUPPORTS_ITEM, () -> FoodEatenKubeEvent.class).hasResult();
	EventHandler TOOLTIP = GROUP.client("tooltip", () -> ItemTooltipKubeEvent.class);
	EventHandler MODEL_PROPERTIES = GROUP.startup("modelProperties", () -> ItemModelPropertiesKubeEvent.class);
	SpecializedEventHandler<ResourceKey<Item>> FIRST_RIGHT_CLICKED = GROUP.common("firstRightClicked", SUPPORTS_ITEM, () -> ItemClickedKubeEvent.class);
	SpecializedEventHandler<ResourceKey<Item>> FIRST_LEFT_CLICKED = GROUP.common("firstLeftClicked", SUPPORTS_ITEM, () -> ItemClickedKubeEvent.class);
	SpecializedEventHandler<ResourceKey<Item>> ITEM_DESTROYED = GROUP.common("destroyed", SUPPORTS_ITEM, () -> ItemDestroyedKubeEvent.class);
}
