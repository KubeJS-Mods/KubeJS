package dev.latvian.kubejs.block;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.KubeJSObjects;
import dev.latvian.kubejs.fluid.FluidBuilder;
import dev.latvian.kubejs.script.ScriptsLoadedEvent;
import me.shedaniel.architectury.ExpectPlatform;
import me.shedaniel.architectury.event.events.InteractionEvent;
import me.shedaniel.architectury.registry.Registries;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;

/**
 * @author LatvianModder
 */
public class KubeJSBlockEventHandler
{
	public static void init()
	{
		ScriptsLoadedEvent.EVENT.register(KubeJSBlockEventHandler::registry);
		InteractionEvent.RIGHT_CLICK_BLOCK.register(KubeJSBlockEventHandler::rightClick);
		InteractionEvent.LEFT_CLICK_BLOCK.register(KubeJSBlockEventHandler::leftClick);
		//MinecraftForge.EVENT_BUS.addListener(this::blockDrops);
	}

	@ExpectPlatform
	private static LiquidBlock buildFluidBlock(FluidBuilder builder, BlockBehaviour.Properties properties)
	{
		throw new AssertionError();
	}

	private static void registry()
	{
		for (BlockBuilder builder : KubeJSObjects.BLOCKS.values())
		{
			Registries.get(KubeJS.MOD_ID).get(Registry.BLOCK_REGISTRY).register(builder.id, () -> {
				BlockBuilder.current = builder;
				return builder.block = new BlockJS(builder);
			});
		}

		BlockBuilder.current = null;

		for (FluidBuilder builder : KubeJSObjects.FLUIDS.values())
		{
			Registries.get(KubeJS.MOD_ID).get(Registry.BLOCK_REGISTRY).register(builder.id, () -> builder.block =
					buildFluidBlock(builder, Block.Properties.of(Material.WATER).noCollission().strength(100.0F).noDrops()));
		}
	}

	private static InteractionResult rightClick(Player player, InteractionHand hand, BlockPos pos, Direction direction)
	{
		if (new BlockRightClickEventJS(player, hand, pos, direction).post(KubeJSEvents.BLOCK_RIGHT_CLICK))
		{
			return InteractionResult.FAIL;
		}
		return InteractionResult.PASS;
	}

	private static InteractionResult leftClick(Player player, InteractionHand hand, BlockPos pos, Direction direction)
	{
		if (new BlockLeftClickEventJS(player, hand, pos, direction).post(KubeJSEvents.BLOCK_LEFT_CLICK))
		{
			return InteractionResult.FAIL;
		}
		return InteractionResult.PASS;
	}

	/*
	private static void blockDrops(BlockEvent.HarvestDropsEvent event)
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
	 */
}