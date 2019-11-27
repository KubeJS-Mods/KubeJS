package dev.latvian.kubejs.block;

import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.script.ScriptType;
import net.minecraft.block.Block;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * @author LatvianModder
 */
public class KubeJSBlockEventHandler
{
	public void init()
	{
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registry);
		MinecraftForge.EVENT_BUS.addListener(this::rightClick);
		MinecraftForge.EVENT_BUS.addListener(this::leftClick);
		MinecraftForge.EVENT_BUS.addListener(this::blockBreak);
		MinecraftForge.EVENT_BUS.addListener(this::blockPlace);
		MinecraftForge.EVENT_BUS.addListener(this::blockDrops);
	}

	private void registry(RegistryEvent.Register<Block> event)
	{
		new BlockRegistryEventJS(event.getRegistry()).post(ScriptType.STARTUP, KubeJSEvents.BLOCK_REGISTRY);
	}

	private void rightClick(PlayerInteractEvent.RightClickBlock event)
	{
		if (new BlockRightClickEventJS(event).post(KubeJSEvents.BLOCK_RIGHT_CLICK))
		{
			event.setCanceled(true);
		}
	}

	private void leftClick(PlayerInteractEvent.LeftClickBlock event)
	{
		if (new BlockLeftClickEventJS(event).post(KubeJSEvents.BLOCK_LEFT_CLICK))
		{
			event.setCanceled(true);
		}
	}

	private void blockBreak(BlockEvent.BreakEvent event)
	{
		if (new BlockBreakEventJS(event).post(KubeJSEvents.BLOCK_BREAK))
		{
			event.setCanceled(true);
		}
	}

	private void blockPlace(BlockEvent.EntityPlaceEvent event)
	{
		if (new BlockPlaceEventJS(event).post(KubeJSEvents.BLOCK_PLACE))
		{
			event.setCanceled(true);
		}
	}

	private void blockDrops(BlockEvent.HarvestDropsEvent event)
	{
		if (event.getWorld().isRemote())
		{
			return;
		}

		BlockDropsEventJS e = new BlockDropsEventJS(event);
		e.post(KubeJSEvents.BLOCK_DROPS);

		if (e.dropList != null)
		{
			event.getDrops().clear();

			for (ItemStackJS stack : e.dropList)
			{
				event.getDrops().add(stack.getItemStack());
			}
		}
	}
}