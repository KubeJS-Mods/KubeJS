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

public interface BlockEvents {
	EventGroup GROUP = EventGroup.of("BlockEvents");

	Extra SUPPORTS_BLOCK = new Extra().transformer(BlockEvents::transformBlock).identity();

	private static Object transformBlock(Object o) {
		if (o == null || o instanceof Block) {
			return o;
		} else if (o instanceof BlockItem item) {
			return item.getBlock();
		}

		var id = ResourceLocation.tryParse(o.toString());
		var block = id == null ? null : BlockWrapper.getBlock(id);
		return block == Blocks.AIR ? null : block;
	}

	EventHandler MODIFICATION = GROUP.startup("modification", () -> BlockModificationEventJS.class);
	EventHandler RIGHT_CLICKED = GROUP.server("rightClicked", () -> BlockRightClickedEventJS.class).extra(SUPPORTS_BLOCK).cancelable();
	EventHandler LEFT_CLICKED = GROUP.server("leftClicked", () -> BlockLeftClickedEventJS.class).extra(SUPPORTS_BLOCK).cancelable();
	EventHandler PLACED = GROUP.server("placed", () -> BlockPlacedEventJS.class).extra(SUPPORTS_BLOCK).cancelable();
	EventHandler BROKEN = GROUP.server("broken", () -> BlockBrokenEventJS.class).extra(SUPPORTS_BLOCK).cancelable();
	EventHandler DETECTOR_CHANGED = GROUP.server("detectorChanged", () -> DetectorBlockEventJS.class).extra(SUPPORTS_BLOCK);
	EventHandler DETECTOR_POWERED = GROUP.server("detectorPowered", () -> DetectorBlockEventJS.class).extra(SUPPORTS_BLOCK);
	EventHandler DETECTOR_UNPOWERED = GROUP.server("detectorUnpowered", () -> DetectorBlockEventJS.class).extra(SUPPORTS_BLOCK);
	EventHandler FARMLAND_TRAMPLED = GROUP.server("farmlandTrampled", () -> FarmlandTrampledEventJS.class).extra(SUPPORTS_BLOCK).cancelable();
}
