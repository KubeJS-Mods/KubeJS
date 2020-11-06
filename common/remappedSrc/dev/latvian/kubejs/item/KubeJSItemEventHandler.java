package dev.latvian.kubejs.item;

import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.KubeJSObjects;
import dev.latvian.kubejs.block.BlockBuilder;
import dev.latvian.kubejs.fluid.BucketItemJS;
import dev.latvian.kubejs.fluid.FluidBuilder;
import dev.latvian.kubejs.player.InventoryChangedEventJS;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * @author LatvianModder
 */
public class KubeJSItemEventHandler
{
	public void init()
	{
		FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Item.class, this::registry);
		MinecraftForge.EVENT_BUS.addListener(this::rightClick);
		MinecraftForge.EVENT_BUS.addListener(this::rightClickEmpty);
		MinecraftForge.EVENT_BUS.addListener(this::leftClickEmpty);
		MinecraftForge.EVENT_BUS.addListener(this::pickup);
		MinecraftForge.EVENT_BUS.addListener(this::toss);
		MinecraftForge.EVENT_BUS.addListener(this::entityInteract);
		MinecraftForge.EVENT_BUS.addListener(this::crafted);
		MinecraftForge.EVENT_BUS.addListener(this::smelted);
		MinecraftForge.EVENT_BUS.addListener(this::destroyed);
	}

	private void registry(RegistryEvent.Register<Item> event)
	{
		for (ItemBuilder builder : KubeJSObjects.ITEMS.values())
		{
			builder.item = new ItemJS(builder);
			builder.item.setRegistryName(builder.id);
			event.getRegistry().register(builder.item);
		}

		for (BlockBuilder builder : KubeJSObjects.BLOCKS.values())
		{
			if (builder.itemBuilder != null)
			{
				builder.itemBuilder.blockItem = new BlockItemJS(builder.itemBuilder);
				builder.itemBuilder.blockItem.setRegistryName(builder.id);
				event.getRegistry().register(builder.itemBuilder.blockItem);
			}
		}

		for (FluidBuilder builder : KubeJSObjects.FLUIDS.values())
		{
			builder.bucketItem = new BucketItemJS(builder);
			builder.bucketItem.setRegistryName(builder.id.getNamespace() + ":" + builder.id.getPath() + "_bucket");
			event.getRegistry().register(builder.bucketItem);
		}
	}

	private void rightClick(PlayerInteractEvent.RightClickItem event)
	{
		if (new ItemRightClickEventJS(event).post(KubeJSEvents.ITEM_RIGHT_CLICK))
		{
			event.setCanceled(true);
		}
	}

	private void rightClickEmpty(PlayerInteractEvent.RightClickEmpty event)
	{
		new ItemRightClickEmptyEventJS(event).post(KubeJSEvents.ITEM_RIGHT_CLICK_EMPTY);
	}

	private void leftClickEmpty(PlayerInteractEvent.LeftClickEmpty event)
	{
		new ItemLeftClickEventJS(event).post(KubeJSEvents.ITEM_LEFT_CLICK);
	}

	private void pickup(EntityItemPickupEvent event)
	{
		if (event.getPlayer() != null && event.getPlayer().world != null && new ItemPickupEventJS(event).post(KubeJSEvents.ITEM_PICKUP))
		{
			event.setCanceled(true);
		}
	}

	private void toss(ItemTossEvent event)
	{
		if (event.getPlayer() != null && event.getPlayer().world != null && new ItemTossEventJS(event).post(KubeJSEvents.ITEM_TOSS))
		{
			event.setCanceled(true);
		}
	}

	private void entityInteract(PlayerInteractEvent.EntityInteract event)
	{
		if (new ItemEntityInteractEventJS(event).post(KubeJSEvents.ITEM_ENTITY_INTERACT))
		{
			event.setCanceled(true);
		}
	}

	private void crafted(PlayerEvent.ItemCraftedEvent event)
	{
		if (event.getPlayer() instanceof ServerPlayer && !event.getCrafting().isEmpty())
		{
			new ItemCraftedEventJS(event).post(KubeJSEvents.ITEM_CRAFTED);
			new InventoryChangedEventJS((ServerPlayer) event.getPlayer(), event.getCrafting(), -1).post(KubeJSEvents.PLAYER_INVENTORY_CHANGED);
		}
	}

	private void smelted(PlayerEvent.ItemSmeltedEvent event)
	{
		if (event.getPlayer() instanceof ServerPlayer && !event.getSmelting().isEmpty())
		{
			new ItemSmeltedEventJS(event).post(KubeJSEvents.ITEM_SMELTED);
			new InventoryChangedEventJS((ServerPlayer) event.getPlayer(), event.getSmelting(), -1).post(KubeJSEvents.PLAYER_INVENTORY_CHANGED);
		}
	}

	private void destroyed(PlayerDestroyItemEvent event)
	{
		if (event.getPlayer() instanceof ServerPlayer)
		{
			new ItemDestroyedEventJS(event).post(KubeJSEvents.ITEM_DESTROYED);
		}
	}
}