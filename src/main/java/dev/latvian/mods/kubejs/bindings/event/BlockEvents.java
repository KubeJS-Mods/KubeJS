package dev.latvian.mods.kubejs.bindings.event;

import dev.latvian.mods.kubejs.block.BlockBrokenKubeEvent;
import dev.latvian.mods.kubejs.block.BlockDropsKubeEvent;
import dev.latvian.mods.kubejs.block.BlockLeftClickedKubeEvent;
import dev.latvian.mods.kubejs.block.BlockModificationKubeEvent;
import dev.latvian.mods.kubejs.block.BlockPlacedKubeEvent;
import dev.latvian.mods.kubejs.block.BlockRightClickedKubeEvent;
import dev.latvian.mods.kubejs.block.DetectorBlockKubeEvent;
import dev.latvian.mods.kubejs.block.FarmlandTrampledKubeEvent;
import dev.latvian.mods.kubejs.block.RandomTickKubeEvent;
import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.event.Extra;
import dev.latvian.mods.kubejs.event.SpecializedEventHandler;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;

public interface BlockEvents {
	EventGroup GROUP = EventGroup.of("BlockEvents");
	Extra<ResourceKey<Block>> SUPPORTS_BLOCK = Extra.registryKey(Registries.BLOCK, Block.class);

	EventHandler MODIFICATION = GROUP.startup("modification", () -> BlockModificationKubeEvent.class);
	SpecializedEventHandler<ResourceKey<Block>> RIGHT_CLICKED = GROUP.common("rightClicked", SUPPORTS_BLOCK, () -> BlockRightClickedKubeEvent.class).hasResult();
	SpecializedEventHandler<ResourceKey<Block>> LEFT_CLICKED = GROUP.common("leftClicked", SUPPORTS_BLOCK, () -> BlockLeftClickedKubeEvent.class).hasResult();
	SpecializedEventHandler<ResourceKey<Block>> PLACED = GROUP.common("placed", SUPPORTS_BLOCK, () -> BlockPlacedKubeEvent.class).hasResult();
	SpecializedEventHandler<ResourceKey<Block>> BROKEN = GROUP.common("broken", SUPPORTS_BLOCK, () -> BlockBrokenKubeEvent.class).hasResult();
	SpecializedEventHandler<ResourceKey<Block>> DROPS = GROUP.server("drops", SUPPORTS_BLOCK, () -> BlockDropsKubeEvent.class).hasResult();
	SpecializedEventHandler<String> DETECTOR_CHANGED = GROUP.common("detectorChanged", Extra.STRING, () -> DetectorBlockKubeEvent.class);
	SpecializedEventHandler<String> DETECTOR_POWERED = GROUP.common("detectorPowered", Extra.STRING, () -> DetectorBlockKubeEvent.class);
	SpecializedEventHandler<String> DETECTOR_UNPOWERED = GROUP.common("detectorUnpowered", Extra.STRING, () -> DetectorBlockKubeEvent.class);
	SpecializedEventHandler<ResourceKey<Block>> FARMLAND_TRAMPLED = GROUP.common("farmlandTrampled", SUPPORTS_BLOCK, () -> FarmlandTrampledKubeEvent.class).hasResult();
	SpecializedEventHandler<ResourceKey<Block>> RANDOM_TICK = GROUP.server("randomTick", SUPPORTS_BLOCK, () -> RandomTickKubeEvent.class).hasResult().required();
}
