package dev.latvian.kubejs.block;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.KubeJSObjects;
import dev.latvian.kubejs.fluid.FluidBuilder;
import me.shedaniel.architectury.annotations.ExpectPlatform;
import me.shedaniel.architectury.event.events.EntityEvent;
import me.shedaniel.architectury.event.events.InteractionEvent;
import me.shedaniel.architectury.event.events.PlayerEvent;
import me.shedaniel.architectury.registry.Registries;
import me.shedaniel.architectury.utils.IntValue;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import org.jetbrains.annotations.Nullable;

/**
 * @author LatvianModder
 */
public class KubeJSBlockEventHandler
{
	public static void init()
	{
		registry();
		InteractionEvent.RIGHT_CLICK_BLOCK.register(KubeJSBlockEventHandler::rightClick);
		InteractionEvent.LEFT_CLICK_BLOCK.register(KubeJSBlockEventHandler::leftClick);
		PlayerEvent.BREAK_BLOCK.register(KubeJSBlockEventHandler::blockBreak);
		EntityEvent.PLACE_BLOCK.register(KubeJSBlockEventHandler::blockPlace);
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
			BlockBuilder.current = builder;
			builder.block = new BlockJS(builder);
			Registries.get(KubeJS.MOD_ID).get(Registry.BLOCK_REGISTRY).register(builder.id, () -> builder.block);
		}

		BlockBuilder.current = null;

		for (FluidBuilder builder : KubeJSObjects.FLUIDS.values())
		{
			builder.block = buildFluidBlock(builder, Block.Properties.of(Material.WATER).noCollission().strength(100.0F).noDrops());
			Registries.get(KubeJS.MOD_ID).get(Registry.BLOCK_REGISTRY).register(builder.id, () -> builder.block);
		}
	}

	private static InteractionResult rightClick(Player player, InteractionHand hand, BlockPos pos, Direction direction)
	{
		if (player != null && player.level != null && new BlockRightClickEventJS(player, hand, pos, direction).post(KubeJSEvents.BLOCK_RIGHT_CLICK))
		{
			return InteractionResult.FAIL;
		}
		return InteractionResult.PASS;
	}

	private static InteractionResult leftClick(Player player, InteractionHand hand, BlockPos pos, Direction direction)
	{
		if (player != null && player.level != null && new BlockLeftClickEventJS(player, hand, pos, direction).post(KubeJSEvents.BLOCK_LEFT_CLICK))
		{
			return InteractionResult.FAIL;
		}
		return InteractionResult.PASS;
	}

	private static InteractionResult blockBreak(Level world, BlockPos pos, BlockState state, ServerPlayer player, @Nullable IntValue xp)
	{
		if (player != null && player.level != null && new BlockBreakEventJS(player, world, pos, state, xp).post(KubeJSEvents.BLOCK_BREAK))
		{
			return InteractionResult.FAIL;
		}
		return InteractionResult.PASS;
	}

	private static InteractionResult blockPlace(Level world, BlockPos pos, BlockState state, @Nullable Entity placer)
	{
		if (world != null && (placer == null || placer.level != null) && new BlockPlaceEventJS(placer, world, pos, state).post(KubeJSEvents.BLOCK_PLACE))
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