package dev.latvian.mods.kubejs.plugin.builtin.event;

import dev.latvian.mods.kubejs.block.BlockBrokenKubeEvent;
import dev.latvian.mods.kubejs.block.BlockDropsKubeEvent;
import dev.latvian.mods.kubejs.block.BlockLeftClickedKubeEvent;
import dev.latvian.mods.kubejs.block.BlockModificationKubeEvent;
import dev.latvian.mods.kubejs.block.BlockPickedKubeEvent;
import dev.latvian.mods.kubejs.block.BlockPlacedKubeEvent;
import dev.latvian.mods.kubejs.block.BlockRightClickedKubeEvent;
import dev.latvian.mods.kubejs.block.BlockStartedFallingKubeEvent;
import dev.latvian.mods.kubejs.block.BlockStoppedFallingKubeEvent;
import dev.latvian.mods.kubejs.block.DetectorBlockKubeEvent;
import dev.latvian.mods.kubejs.block.FarmlandTrampledKubeEvent;
import dev.latvian.mods.kubejs.block.RandomTickKubeEvent;
import dev.latvian.mods.kubejs.block.entity.BlockEntityTickKubeEvent;
import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.event.EventTargetType;
import dev.latvian.mods.kubejs.event.TargetedEventHandler;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.ItemWrapper;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;

public interface BlockEvents {
	EventGroup GROUP = EventGroup.of("BlockEvents");
	EventTargetType<ResourceKey<Block>> TARGET = EventTargetType.registryKey(Registries.BLOCK, Block.class);

	EventHandler MODIFICATION = GROUP.startup("modification", () -> BlockModificationKubeEvent.class);
	TargetedEventHandler<ResourceKey<Block>> RIGHT_CLICKED = GROUP.common("rightClicked", () -> BlockRightClickedKubeEvent.class).hasResult().supportsTarget(TARGET);
	TargetedEventHandler<ResourceKey<Block>> LEFT_CLICKED = GROUP.common("leftClicked", () -> BlockLeftClickedKubeEvent.class).hasResult().supportsTarget(TARGET);
	TargetedEventHandler<ResourceKey<Block>> PLACED = GROUP.common("placed", () -> BlockPlacedKubeEvent.class).hasResult().supportsTarget(TARGET);
	TargetedEventHandler<ResourceKey<Block>> BROKEN = GROUP.common("broken", () -> BlockBrokenKubeEvent.class).hasResult().supportsTarget(TARGET);
	TargetedEventHandler<ResourceKey<Block>> DROPS = GROUP.server("drops", () -> BlockDropsKubeEvent.class).hasResult().supportsTarget(TARGET);
	TargetedEventHandler<String> DETECTOR_CHANGED = GROUP.common("detectorChanged", () -> DetectorBlockKubeEvent.class).supportsTarget(EventTargetType.STRING);
	TargetedEventHandler<String> DETECTOR_POWERED = GROUP.common("detectorPowered", () -> DetectorBlockKubeEvent.class).supportsTarget(EventTargetType.STRING);
	TargetedEventHandler<String> DETECTOR_UNPOWERED = GROUP.common("detectorUnpowered", () -> DetectorBlockKubeEvent.class).supportsTarget(EventTargetType.STRING);
	TargetedEventHandler<ResourceKey<Block>> FARMLAND_TRAMPLED = GROUP.common("farmlandTrampled", () -> FarmlandTrampledKubeEvent.class).hasResult().supportsTarget(TARGET);
	TargetedEventHandler<ResourceKey<Block>> RANDOM_TICK = GROUP.server("randomTick", () -> RandomTickKubeEvent.class).hasResult().requiredTarget(TARGET);
	TargetedEventHandler<ResourceKey<Block>> BLOCK_ENTITY_TICK = GROUP.common("blockEntityTick", () -> BlockEntityTickKubeEvent.class).requiredTarget(TARGET);
	TargetedEventHandler<ResourceKey<Block>> STARTED_FALLING = GROUP.common("startedFalling", () -> BlockStartedFallingKubeEvent.class).hasResult().supportsTarget(TARGET);
	TargetedEventHandler<ResourceKey<Block>> STOPPED_FALLING = GROUP.common("stoppedFalling", () -> BlockStoppedFallingKubeEvent.class).supportsTarget(TARGET);
	TargetedEventHandler<ResourceKey<Block>> PICKED = GROUP.common("picked", () -> BlockPickedKubeEvent.class).hasResult(ItemWrapper.TYPE_INFO).supportsTarget(TARGET);
}
