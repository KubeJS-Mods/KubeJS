package dev.latvian.mods.kubejs.block.drop;

import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.item.Item;

public record BlockDrop(Item item, int count, DataComponentPatch components) {
}
