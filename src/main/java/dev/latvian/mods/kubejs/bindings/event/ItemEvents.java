package dev.latvian.mods.kubejs.bindings.event;

import dev.latvian.mods.kubejs.bindings.ItemWrapper;
import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.event.Extra;
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
import dev.latvian.mods.kubejs.item.ItemTooltipKubeEvent;
import dev.latvian.mods.kubejs.item.custom.ItemToolTierRegistryKubeEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

public interface ItemEvents {
	EventGroup GROUP = EventGroup.of("ItemEvents");

	Extra SUPPORTS_ITEM = new Extra().transformer(ItemEvents::transformItem).toString(o -> ((Item) o).kjs$getId()).identity().describeType(context -> context.javaType(Item.class));

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

	EventHandler MODIFICATION = GROUP.startup("modification", () -> ItemModificationKubeEvent.class);
	EventHandler TOOL_TIER_REGISTRY = GROUP.startup("toolTierRegistry", () -> ItemToolTierRegistryKubeEvent.class);
	EventHandler RIGHT_CLICKED = GROUP.common("rightClicked", () -> ItemClickedKubeEvent.class).extra(SUPPORTS_ITEM).hasResult();
	EventHandler CAN_PICK_UP = GROUP.common("canPickUp", () -> ItemPickedUpKubeEvent.class).extra(SUPPORTS_ITEM).hasResult();
	EventHandler PICKED_UP = GROUP.common("pickedUp", () -> ItemPickedUpKubeEvent.class).extra(SUPPORTS_ITEM);
	EventHandler DROPPED = GROUP.common("dropped", () -> ItemDroppedKubeEvent.class).extra(SUPPORTS_ITEM).hasResult();
	EventHandler ENTITY_INTERACTED = GROUP.common("entityInteracted", () -> ItemEntityInteractedKubeEvent.class).extra(SUPPORTS_ITEM).hasResult();
	EventHandler CRAFTED = GROUP.common("crafted", () -> ItemCraftedKubeEvent.class).extra(SUPPORTS_ITEM);
	EventHandler SMELTED = GROUP.common("smelted", () -> ItemSmeltedKubeEvent.class).extra(SUPPORTS_ITEM);
	EventHandler FOOD_EATEN = GROUP.common("foodEaten", () -> FoodEatenKubeEvent.class).extra(SUPPORTS_ITEM).hasResult();
	EventHandler TOOLTIP = GROUP.client("tooltip", () -> ItemTooltipKubeEvent.class);
	EventHandler MODEL_PROPERTIES = GROUP.startup("modelProperties", () -> ItemModelPropertiesKubeEvent.class);
	EventHandler FIRST_RIGHT_CLICKED = GROUP.common("firstRightClicked", () -> ItemClickedKubeEvent.class).extra(ItemEvents.SUPPORTS_ITEM);
	EventHandler FIRST_LEFT_CLICKED = GROUP.common("firstLeftClicked", () -> ItemClickedKubeEvent.class).extra(ItemEvents.SUPPORTS_ITEM);
	EventHandler ITEM_DESTROYED = GROUP.common("destroyed", () -> ItemDestroyedKubeEvent.class).extra(SUPPORTS_ITEM);
}
