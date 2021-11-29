package dev.latvian.mods.kubejs.block;

import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.latvian.mods.kubejs.CommonProperties;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.BlockEvent;
import dev.architectury.event.events.common.InteractionEvent;
import dev.architectury.utils.value.IntValue;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.KubeJSEvents;
import dev.latvian.mods.kubejs.KubeJSObjects;
import dev.latvian.mods.kubejs.KubeJSRegistries;
import dev.latvian.mods.kubejs.core.BlockKJS;
import dev.latvian.mods.kubejs.fluid.FluidBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
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
public class KubeJSBlockEventHandler {

	public static void init() {
		if (!CommonProperties.get().serverOnly) {
			registry();
		}

		InteractionEvent.RIGHT_CLICK_BLOCK.register(KubeJSBlockEventHandler::rightClick);
		InteractionEvent.LEFT_CLICK_BLOCK.register(KubeJSBlockEventHandler::leftClick);
		BlockEvent.BREAK.register(KubeJSBlockEventHandler::blockBreak);
		BlockEvent.PLACE.register(KubeJSBlockEventHandler::blockPlace);
	}

	@ExpectPlatform
	private static LiquidBlock buildFluidBlock(FluidBuilder builder, BlockBehaviour.Properties properties) {
		throw new AssertionError();
	}

	private static void registry() {
		for (BlockBuilder builder : KubeJSObjects.BLOCKS.values()) {
			BlockBuilder.current = builder;

			builder.block = builder.type.createBlock(builder);

			if (builder.block instanceof BlockKJS) {
				((BlockKJS) builder.block).setBlockBuilderKJS(builder);
			}

			KubeJSRegistries.blocks().register(builder.id, () -> builder.block);
		}

		BlockBuilder.current = null;

		for (FluidBuilder builder : KubeJSObjects.FLUIDS.values()) {
			builder.block = buildFluidBlock(builder, Block.Properties.of(Material.WATER).noCollission().strength(100.0F).noDrops());
			KubeJSRegistries.blocks().register(builder.id, () -> builder.block);
		}

		for (DetectorInstance detector : KubeJSObjects.DETECTORS.values()) {
			detector.block = KubeJSRegistries.blocks().register(new ResourceLocation(KubeJS.MOD_ID, "detector_" + detector.id), () -> new DetectorBlock(detector.id));
		}
	}

	private static EventResult rightClick(Player player, InteractionHand hand, BlockPos pos, Direction direction) {
		if (player != null && player.level != null && !player.getCooldowns().isOnCooldown(player.getItemInHand(hand).getItem()) && new BlockRightClickEventJS(player, hand, pos, direction).post(KubeJSEvents.BLOCK_RIGHT_CLICK)) {
			return EventResult.interruptFalse();
		}

		return EventResult.pass();
	}

	private static EventResult leftClick(Player player, InteractionHand hand, BlockPos pos, Direction direction) {
		if (player != null && player.level != null && new BlockLeftClickEventJS(player, hand, pos, direction).post(KubeJSEvents.BLOCK_LEFT_CLICK)) {
			return EventResult.interruptFalse();
		}

		return EventResult.pass();
	}

	private static EventResult blockBreak(Level world, BlockPos pos, BlockState state, ServerPlayer player, @Nullable IntValue xp) {
		if (player != null && player.level != null && new BlockBreakEventJS(player, world, pos, state, xp).post(KubeJSEvents.BLOCK_BREAK)) {
			return EventResult.interruptFalse();
		}

		return EventResult.pass();
	}

	private static EventResult blockPlace(Level world, BlockPos pos, BlockState state, @Nullable Entity placer) {
		if (world != null && (placer == null || placer.level != null) && new BlockPlaceEventJS(placer, world, pos, state).post(KubeJSEvents.BLOCK_PLACE)) {
			return EventResult.interruptFalse();
		}

		return EventResult.pass();
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