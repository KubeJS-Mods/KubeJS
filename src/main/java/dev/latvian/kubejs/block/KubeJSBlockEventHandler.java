package dev.latvian.kubejs.block;

import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.KubeJSObjects;
import dev.latvian.kubejs.fluid.FluidBuilder;
import dev.latvian.kubejs.item.ItemStackJS;
import net.minecraft.block.Block;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.material.Material;
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
		FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Block.class, this::registry);
		MinecraftForge.EVENT_BUS.addListener(this::rightClick);
		MinecraftForge.EVENT_BUS.addListener(this::leftClick);
		MinecraftForge.EVENT_BUS.addListener(this::blockBreak);
		MinecraftForge.EVENT_BUS.addListener(this::blockPlace);
		MinecraftForge.EVENT_BUS.addListener(this::blockDrops);
	}

	private void registry(RegistryEvent.Register<Block> event)
	{
		for (BlockBuilder builder : KubeJSObjects.BLOCKS.values())
		{
			builder.block = new BlockJS(builder);
			builder.block.setRegistryName(builder.id);
			event.getRegistry().register(builder.block);
		}

		for (FluidBuilder builder : KubeJSObjects.FLUIDS.values())
		{
			builder.block = new FlowingFluidBlock(() -> builder.stillFluid, Block.Properties.create(Material.WATER).doesNotBlockMovement().hardnessAndResistance(100.0F).noDrops());
			builder.block.setRegistryName(builder.id);
			event.getRegistry().register(builder.block);
		}
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