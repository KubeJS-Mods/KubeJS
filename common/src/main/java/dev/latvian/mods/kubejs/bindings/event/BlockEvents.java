package dev.latvian.mods.kubejs.bindings.event;

import dev.latvian.mods.kubejs.bindings.BlockWrapper;
import dev.latvian.mods.kubejs.block.BlockBrokenEventJS;
import dev.latvian.mods.kubejs.block.BlockLeftClickedEventJS;
import dev.latvian.mods.kubejs.block.BlockModificationEventJS;
import dev.latvian.mods.kubejs.block.BlockPlacedEventJS;
import dev.latvian.mods.kubejs.block.BlockRightClickedEventJS;
import dev.latvian.mods.kubejs.block.DetectorBlockEventJS;
import dev.latvian.mods.kubejs.block.FarmlandTrampledEventJS;
import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.event.Extra;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public interface BlockEvents {
	EventGroup GROUP = EventGroup.of("BlockEvents");

	Extra SUPPORTS_BLOCK = new Extra().transformer(BlockEvents::transformBlock).toString(o -> ((Block) o).kjs$getId()).identity();

	private static Block transformBlock(Object o) {
		if (o == null) {
			return null;
		} else if (o instanceof Block block) {
			return block;
		} else if (o instanceof BlockItem item) {
			return item.getBlock();
		} else if (o instanceof BlockState state) {
			return state.getBlock();
		}

		var id = ResourceLocation.tryParse(o.toString());
		var block = id == null ? null : BlockWrapper.getBlock(id);
		return block == Blocks.AIR ? null : block;
	}

	EventHandler MODIFICATION = GROUP.startup("modification", () -> BlockModificationEventJS.class);
	EventHandler RIGHT_CLICKED = GROUP.common("rightClicked", () -> BlockRightClickedEventJS.class).extra(SUPPORTS_BLOCK).hasResult();
	EventHandler LEFT_CLICKED = GROUP.common("leftClicked", () -> BlockLeftClickedEventJS.class).extra(SUPPORTS_BLOCK).hasResult();
	EventHandler PLACED = GROUP.common("placed", () -> BlockPlacedEventJS.class).extra(SUPPORTS_BLOCK).hasResult();
	EventHandler BROKEN = GROUP.common("broken", () -> BlockBrokenEventJS.class).extra(SUPPORTS_BLOCK).hasResult();
	EventHandler DETECTOR_CHANGED = GROUP.common("detectorChanged", () -> DetectorBlockEventJS.class).extra(SUPPORTS_BLOCK);
	EventHandler DETECTOR_POWERED = GROUP.common("detectorPowered", () -> DetectorBlockEventJS.class).extra(SUPPORTS_BLOCK);
	EventHandler DETECTOR_UNPOWERED = GROUP.common("detectorUnpowered", () -> DetectorBlockEventJS.class).extra(SUPPORTS_BLOCK);
	EventHandler FARMLAND_TRAMPLED = GROUP.common("farmlandTrampled", () -> FarmlandTrampledEventJS.class).extra(SUPPORTS_BLOCK).hasResult();
}
