package dev.latvian.mods.kubejs.bindings.event;

import dev.latvian.mods.kubejs.block.BlockBrokenEventJS;
import dev.latvian.mods.kubejs.block.BlockLeftClickedEventJS;
import dev.latvian.mods.kubejs.block.BlockModificationEventJS;
import dev.latvian.mods.kubejs.block.BlockPlacedEventJS;
import dev.latvian.mods.kubejs.block.BlockRightClickedEventJS;
import dev.latvian.mods.kubejs.block.DetectorBlockEventJS;
import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;

public interface BlockEvents {
	EventGroup GROUP = EventGroup.of("BlockEvents");
	EventHandler MODIFICATION = GROUP.startup("modification", () -> BlockModificationEventJS.class).supportsNamespacedExtraId();
	EventHandler RIGHT_CLICKED = GROUP.server("rightClicked", () -> BlockRightClickedEventJS.class).supportsNamespacedExtraId().cancelable();
	EventHandler LEFT_CLICKED = GROUP.server("leftClicked", () -> BlockLeftClickedEventJS.class).supportsNamespacedExtraId().cancelable();
	EventHandler PLACED = GROUP.server("placed", () -> BlockPlacedEventJS.class).supportsNamespacedExtraId().cancelable();
	EventHandler BROKEN = GROUP.server("broken", () -> BlockBrokenEventJS.class).supportsNamespacedExtraId().cancelable();
	EventHandler DETECTOR_CHANGED = GROUP.server("detectorChanged", () -> DetectorBlockEventJS.class).supportsNamespacedExtraId();
	EventHandler DETECTOR_POWERED = GROUP.server("detectorPowered", () -> DetectorBlockEventJS.class).supportsNamespacedExtraId();
	EventHandler DETECTOR_UNPOWERED = GROUP.server("detectorUnpowered", () -> DetectorBlockEventJS.class).supportsNamespacedExtraId();
}
