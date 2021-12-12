package dev.latvian.mods.kubejs.item;

import dev.architectury.event.CompoundEventResult;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.InteractionEvent;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.latvian.mods.kubejs.*;
import dev.latvian.mods.kubejs.core.ItemKJS;
import dev.latvian.mods.kubejs.fluid.FluidBuilder;
import dev.latvian.mods.kubejs.player.InventoryChangedEventJS;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;

import java.util.function.Supplier;

/**
 * @author LatvianModder
 */
public class KubeJSItemEventHandler {
	public static Supplier<Item> DUMMY_FLUID_ITEM = () -> Items.STRUCTURE_VOID;

	public static void init() {
		if (!CommonProperties.get().serverOnly) {
			registry();
		}

		InteractionEvent.RIGHT_CLICK_ITEM.register(KubeJSItemEventHandler::rightClick);
		InteractionEvent.CLIENT_RIGHT_CLICK_AIR.register(KubeJSItemEventHandler::rightClickEmpty);
		InteractionEvent.CLIENT_LEFT_CLICK_AIR.register(KubeJSItemEventHandler::leftClickEmpty);
		PlayerEvent.PICKUP_ITEM_PRE.register(KubeJSItemEventHandler::pickup);
		PlayerEvent.DROP_ITEM.register(KubeJSItemEventHandler::drop);
		InteractionEvent.INTERACT_ENTITY.register(KubeJSItemEventHandler::entityInteract);
		PlayerEvent.CRAFT_ITEM.register(KubeJSItemEventHandler::crafted);
		PlayerEvent.SMELT_ITEM.register(KubeJSItemEventHandler::smelted);
	}

	@ExpectPlatform
	private static BucketItem buildBucket(FluidBuilder builder) {
		throw new AssertionError();
	}

	private static void registry() {
		for (var builder : KubeJSObjects.ITEMS.values()) {
			builder.item = builder.type.createItem(builder);

			if (builder.item instanceof ItemKJS kjs) {
				kjs.setItemBuilderKJS(builder);
			}

			KubeJSRegistries.items().register(builder.id, () -> builder.item);
		}

		for (var builder : KubeJSObjects.BLOCKS.values()) {
			if (builder.itemBuilder != null) {
				builder.itemBuilder.blockItem = new BlockItemJS(builder.itemBuilder);

				if (builder.itemBuilder.blockItem instanceof ItemKJS kjs) {
					kjs.setItemBuilderKJS(builder.itemBuilder);
				}

				KubeJSRegistries.items().register(builder.id, () -> builder.itemBuilder.blockItem);
			}
		}

		for (var builder : KubeJSObjects.FLUIDS.values()) {
			builder.bucketItem = buildBucket(builder);
			KubeJSRegistries.items().register(builder.newID("", "_bucket"), () -> builder.bucketItem);
		}

		for (var detector : KubeJSObjects.DETECTORS.values()) {
			detector.item = KubeJSRegistries.items().register(KubeJS.id("detector_" + detector.id), () -> new BlockItem(detector.block.get(), new Item.Properties().tab(KubeJS.tab)));
		}

		if (!CommonProperties.get().serverOnly) {
			DUMMY_FLUID_ITEM = KubeJSRegistries.items().register(KubeJS.id("dummy_fluid_item"), () -> new Item(new Item.Properties().stacksTo(1).tab(KubeJS.tab)));
		}
	}

	private static CompoundEventResult<ItemStack> rightClick(Player player, InteractionHand hand) {
		if (!player.getCooldowns().isOnCooldown(player.getItemInHand(hand).getItem()) && new ItemRightClickEventJS(player, hand).post(KubeJSEvents.ITEM_RIGHT_CLICK)) {
			return CompoundEventResult.interruptTrue(player.getItemInHand(hand));
		}

		return CompoundEventResult.pass();
	}

	private static void rightClickEmpty(Player player, InteractionHand hand) {
		if (player != null && player.level != null) {
			new ItemRightClickEmptyEventJS(player, hand).post(KubeJSEvents.ITEM_RIGHT_CLICK_EMPTY);
		}
	}

	private static void leftClickEmpty(Player player, InteractionHand hand) {
		if (player != null && player.level != null) {
			new ItemLeftClickEventJS(player, hand).post(KubeJSEvents.ITEM_LEFT_CLICK);
		}
	}

	private static EventResult pickup(Player player, ItemEntity entity, ItemStack stack) {
		if (player != null && entity != null && player.level != null && new ItemPickupEventJS(player, entity, stack).post(KubeJSEvents.ITEM_PICKUP)) {
			return EventResult.interruptFalse();
		}

		return EventResult.pass();
	}

	private static EventResult drop(Player player, ItemEntity entity) {
		if (player != null && entity != null && player.level != null && new ItemTossEventJS(player, entity).post(KubeJSEvents.ITEM_TOSS)) {
			return EventResult.interruptFalse();
		}

		return EventResult.pass();
	}

	private static EventResult entityInteract(Player player, Entity entity, InteractionHand hand) {
		if (player != null && entity != null && player.level != null && new ItemEntityInteractEventJS(player, entity, hand).post(KubeJSEvents.ITEM_ENTITY_INTERACT)) {
			return EventResult.interruptFalse();
		}

		return EventResult.pass();
	}

	private static void crafted(Player player, ItemStack crafted, Container grid) {
		if (player instanceof ServerPlayer && !crafted.isEmpty()) {
			new ItemCraftedEventJS(player, crafted, grid).post(KubeJSEvents.ITEM_CRAFTED);
			new InventoryChangedEventJS((ServerPlayer) player, crafted, -1).post(KubeJSEvents.PLAYER_INVENTORY_CHANGED);
		}
	}

	private static void smelted(Player player, ItemStack smelted) {
		if (player instanceof ServerPlayer && !smelted.isEmpty()) {
			new ItemSmeltedEventJS(player, smelted).post(KubeJSEvents.ITEM_SMELTED);
			new InventoryChangedEventJS((ServerPlayer) player, smelted, -1).post(KubeJSEvents.PLAYER_INVENTORY_CHANGED);
		}
	}
}