package dev.latvian.mods.kubejs.item.forge;

import dev.architectury.registry.block.ToolType;
import net.minecraft.world.item.Item;

public class ItemBuilderImpl {
	public static void appendToolType(Item.Properties properties, ToolType type, Integer level) {
		// FIXME: properties.addToolType(net.minecraftforge.common.ToolType.get(type.forgeName), level);
	}
}
