package dev.latvian.kubejs.item;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.KubeJSObjects;
import dev.latvian.kubejs.KubeJSRegistries;
import dev.latvian.kubejs.block.BlockBuilder;
import dev.latvian.kubejs.block.DetectorInstance;
import dev.latvian.kubejs.fluid.FluidBuilder;
import dev.latvian.kubejs.player.InventoryChangedEventJS;
import me.shedaniel.architectury.annotations.ExpectPlatform;
import me.shedaniel.architectury.event.events.InteractionEvent;
import me.shedaniel.architectury.event.events.PlayerEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * @author LatvianModder
 */
public class KubeJSItemEventHandler {
	public static void init() {
		registry();
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
		for (ItemBuilder builder : KubeJSObjects.ITEMS.values()) {
			builder.item = builder.type.itemFactory.apply(builder);
			KubeJSRegistries.items().register(builder.id, () -> builder.item);
		}

		for (BlockBuilder builder : KubeJSObjects.BLOCKS.values()) {
			if (builder.itemBuilder != null) {
				builder.itemBuilder.blockItem = new BlockItemJS(builder.itemBuilder);
				KubeJSRegistries.items().register(builder.id, () -> builder.itemBuilder.blockItem);
			}
		}

		for (FluidBuilder builder : KubeJSObjects.FLUIDS.values()) {
			builder.bucketItem = buildBucket(builder);
			KubeJSRegistries.items().register(new ResourceLocation(builder.id.getNamespace(), builder.id.getPath() + "_bucket"), () -> builder.bucketItem);
		}

		for (DetectorInstance detector : KubeJSObjects.DETECTORS.values()) {
			detector.item = KubeJSRegistries.items().register(new ResourceLocation(KubeJS.MOD_ID, "detector_" + detector.id), () -> new BlockItem(detector.block.get(), new Item.Properties().tab(KubeJS.tab)));
		}
	}

	private static InteractionResultHolder<ItemStack> rightClick(Player player, InteractionHand hand) {
		if (!player.getCooldowns().isOnCooldown(player.getItemInHand(hand).getItem()) && new ItemRightClickEventJS(player, hand).post(KubeJSEvents.ITEM_RIGHT_CLICK)) {
			return InteractionResultHolder.success(player.getItemInHand(hand));
		}

		return InteractionResultHolder.pass(ItemStack.EMPTY);
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

	private static InteractionResult pickup(Player player, ItemEntity entity, ItemStack stack) {
		if (player != null && entity != null && player.level != null && new ItemPickupEventJS(player, entity, stack).post(KubeJSEvents.ITEM_PICKUP)) {
			return InteractionResult.FAIL;
		}

		return InteractionResult.PASS;
	}

	private static InteractionResult drop(Player player, ItemEntity entity) {
		if (player != null && entity != null && player.level != null && new ItemTossEventJS(player, entity).post(KubeJSEvents.ITEM_TOSS)) {
			return InteractionResult.FAIL;
		}

		return InteractionResult.PASS;
	}

	private static InteractionResult entityInteract(Player player, Entity entity, InteractionHand hand) {
		if (player != null && entity != null && player.level != null && new ItemEntityInteractEventJS(player, entity, hand).post(KubeJSEvents.ITEM_ENTITY_INTERACT)) {
			return InteractionResult.FAIL;
		}

		return InteractionResult.PASS;
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