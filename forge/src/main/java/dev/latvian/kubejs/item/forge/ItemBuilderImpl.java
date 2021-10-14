package dev.latvian.kubejs.item.forge;

import dev.architectury.architectury.registry.ToolType;
import net.minecraft.world.item.Item;

public class ItemBuilderImpl {
	public static void appendToolType(Item.Properties properties, ToolType type, Integer level) {
		properties.addToolType(net.minecraftforge.common.ToolType.get(type.forgeName), level);
	}
}
