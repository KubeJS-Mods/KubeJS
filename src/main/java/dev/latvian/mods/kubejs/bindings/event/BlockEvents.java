package dev.latvian.mods.kubejs.bindings.event;

import dev.latvian.mods.kubejs.bindings.BlockWrapper;
import dev.latvian.mods.kubejs.block.BlockBrokenKubeEvent;
import dev.latvian.mods.kubejs.block.BlockLeftClickedKubeEvent;
import dev.latvian.mods.kubejs.block.BlockModificationKubeEvent;
import dev.latvian.mods.kubejs.block.BlockPlacedKubeEvent;
import dev.latvian.mods.kubejs.block.BlockRightClickedKubeEvent;
import dev.latvian.mods.kubejs.block.DetectorBlockKubeEvent;
import dev.latvian.mods.kubejs.block.FarmlandTrampledKubeEvent;
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

	Extra SUPPORTS_BLOCK = new Extra().transformer(BlockEvents::transformBlock).toString(o -> ((Block) o).kjs$getId()).identity().describeType(context -> context.javaType(Block.class));

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

	EventHandler MODIFICATION = GROUP.startup("modification", () -> BlockModificationKubeEvent.class);
	EventHandler RIGHT_CLICKED = GROUP.common("rightClicked", () -> BlockRightClickedKubeEvent.class).extra(SUPPORTS_BLOCK).hasResult();
	EventHandler LEFT_CLICKED = GROUP.common("leftClicked", () -> BlockLeftClickedKubeEvent.class).extra(SUPPORTS_BLOCK).hasResult();
	EventHandler PLACED = GROUP.common("placed", () -> BlockPlacedKubeEvent.class).extra(SUPPORTS_BLOCK).hasResult();
	EventHandler BROKEN = GROUP.common("broken", () -> BlockBrokenKubeEvent.class).extra(SUPPORTS_BLOCK).hasResult();
	EventHandler DETECTOR_CHANGED = GROUP.common("detectorChanged", () -> DetectorBlockKubeEvent.class).extra(Extra.STRING);
	EventHandler DETECTOR_POWERED = GROUP.common("detectorPowered", () -> DetectorBlockKubeEvent.class).extra(Extra.STRING);
	EventHandler DETECTOR_UNPOWERED = GROUP.common("detectorUnpowered", () -> DetectorBlockKubeEvent.class).extra(Extra.STRING);
	EventHandler FARMLAND_TRAMPLED = GROUP.common("farmlandTrampled", () -> FarmlandTrampledKubeEvent.class).extra(SUPPORTS_BLOCK).hasResult();
}
