package dev.latvian.mods.kubejs.bindings.event;

import dev.latvian.mods.kubejs.bindings.ItemWrapper;
import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.event.Extra;
import dev.latvian.mods.kubejs.item.FoodEatenEventJS;
import dev.latvian.mods.kubejs.item.ItemClickedEventJS;
import dev.latvian.mods.kubejs.item.ItemCraftedEventJS;
import dev.latvian.mods.kubejs.item.ItemDestroyedEventJS;
import dev.latvian.mods.kubejs.item.ItemDroppedEventJS;
import dev.latvian.mods.kubejs.item.ItemEntityInteractedEventJS;
import dev.latvian.mods.kubejs.item.ItemModelPropertiesEventJS;
import dev.latvian.mods.kubejs.item.ItemModificationEventJS;
import dev.latvian.mods.kubejs.item.ItemPickedUpEventJS;
import dev.latvian.mods.kubejs.item.ItemSmeltedEventJS;
import dev.latvian.mods.kubejs.item.ItemTooltipEventJS;
import dev.latvian.mods.kubejs.item.custom.ItemArmorTierRegistryEventJS;
import dev.latvian.mods.kubejs.item.custom.ItemToolTierRegistryEventJS;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

public interface ItemEvents {
	EventGroup GROUP = EventGroup.of("ItemEvents");

	Extra SUPPORTS_ITEM = new Extra().transformer(ItemEvents::transformItem).toString(o -> ((Item) o).kjs$getId()).identity();

	private static Object transformItem(Object o) {
		if (o == null) {
			return null;
		} else if (o instanceof ItemLike item) {
			return item.asItem();
		}

		var id = ResourceLocation.tryParse(o.toString());
		var item = id == null ? null : ItemWrapper.getItem(id);
		return item == Items.AIR ? null : item;
	}

	EventHandler MODIFICATION = GROUP.startup("modification", () -> ItemModificationEventJS.class);
	EventHandler TOOL_TIER_REGISTRY = GROUP.startup("toolTierRegistry", () -> ItemToolTierRegistryEventJS.class);
	EventHandler ARMOR_TIER_REGISTRY = GROUP.startup("armorTierRegistry", () -> ItemArmorTierRegistryEventJS.class);
	EventHandler RIGHT_CLICKED = GROUP.common("rightClicked", () -> ItemClickedEventJS.class).extra(SUPPORTS_ITEM).hasResult();
	EventHandler CAN_PICK_UP = GROUP.common("canPickUp", () -> ItemPickedUpEventJS.class).extra(SUPPORTS_ITEM).hasResult();
	EventHandler PICKED_UP = GROUP.common("pickedUp", () -> ItemPickedUpEventJS.class).extra(SUPPORTS_ITEM);
	EventHandler DROPPED = GROUP.common("dropped", () -> ItemDroppedEventJS.class).extra(SUPPORTS_ITEM).hasResult();
	EventHandler ENTITY_INTERACTED = GROUP.common("entityInteracted", () -> ItemEntityInteractedEventJS.class).extra(SUPPORTS_ITEM).hasResult();
	EventHandler CRAFTED = GROUP.common("crafted", () -> ItemCraftedEventJS.class).extra(SUPPORTS_ITEM);
	EventHandler SMELTED = GROUP.common("smelted", () -> ItemSmeltedEventJS.class).extra(SUPPORTS_ITEM);
	EventHandler FOOD_EATEN = GROUP.common("foodEaten", () -> FoodEatenEventJS.class).extra(SUPPORTS_ITEM).hasResult();
	EventHandler TOOLTIP = GROUP.client("tooltip", () -> ItemTooltipEventJS.class);
	EventHandler MODEL_PROPERTIES = GROUP.startup("modelProperties", () -> ItemModelPropertiesEventJS.class);
	EventHandler FIRST_RIGHT_CLICKED = GROUP.common("firstRightClicked", () -> ItemClickedEventJS.class).extra(ItemEvents.SUPPORTS_ITEM);
	EventHandler FIRST_LEFT_CLICKED = GROUP.common("firstLeftClicked", () -> ItemClickedEventJS.class).extra(ItemEvents.SUPPORTS_ITEM);
	EventHandler ITEM_DESTROYED = GROUP.common("destroyed", () -> ItemDestroyedEventJS.class).extra(SUPPORTS_ITEM);
}
