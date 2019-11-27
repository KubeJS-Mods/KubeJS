package dev.latvian.kubejs.item;

import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.player.InventoryChangedEventJS;
import dev.latvian.kubejs.script.ScriptType;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
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
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registry);
		MinecraftForge.EVENT_BUS.addListener(this::rightClick);
		MinecraftForge.EVENT_BUS.addListener(this::rightClickEmpty);
		MinecraftForge.EVENT_BUS.addListener(this::leftClickEmpty);
		MinecraftForge.EVENT_BUS.addListener(this::pickup);
		MinecraftForge.EVENT_BUS.addListener(this::toss);
		MinecraftForge.EVENT_BUS.addListener(this::entityInteract);
		MinecraftForge.EVENT_BUS.addListener(this::crafted);
		MinecraftForge.EVENT_BUS.addListener(this::smelted);
	}

	private void registry(RegistryEvent.Register<Item> event)
	{
		new ItemRegistryEventJS(event.getRegistry()).post(ScriptType.STARTUP, KubeJSEvents.ITEM_REGISTRY);
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
		if (new ItemPickupEventJS(event).post(KubeJSEvents.ITEM_PICKUP))
		{
			event.setCanceled(true);
		}
	}

	private void toss(ItemTossEvent event)
	{
		if (new ItemTossEventJS(event).post(KubeJSEvents.ITEM_TOSS))
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
		if (event.getPlayer() instanceof ServerPlayerEntity && !event.getCrafting().isEmpty())
		{
			new ItemCraftedEventJS(event).post(KubeJSEvents.ITEM_CRAFTED);
			new InventoryChangedEventJS((ServerPlayerEntity) event.getPlayer(), event.getCrafting(), -1).post(KubeJSEvents.PLAYER_INVENTORY_CHANGED);
		}
	}

	private void smelted(PlayerEvent.ItemSmeltedEvent event)
	{
		if (event.getPlayer() instanceof ServerPlayerEntity && !event.getSmelting().isEmpty())
		{
			new ItemSmeltedEventJS(event).post(KubeJSEvents.ITEM_SMELTED);
			new InventoryChangedEventJS((ServerPlayerEntity) event.getPlayer(), event.getSmelting(), -1).post(KubeJSEvents.PLAYER_INVENTORY_CHANGED);
		}
	}
}