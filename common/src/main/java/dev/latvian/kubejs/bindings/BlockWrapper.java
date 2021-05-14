package dev.latvian.kubejs.bindings;

import dev.latvian.kubejs.KubeJSRegistries;
import dev.latvian.kubejs.block.MaterialJS;
import dev.latvian.kubejs.block.MaterialListJS;
import dev.latvian.kubejs.block.predicate.BlockEntityPredicate;
import dev.latvian.kubejs.block.predicate.BlockIDPredicate;
import dev.latvian.kubejs.block.predicate.BlockPredicate;
import dev.latvian.kubejs.docs.MinecraftClass;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class BlockWrapper {
	public static Map<String, MaterialJS> getMaterial() {
		return MaterialListJS.INSTANCE.map;
	}

	public static BlockIDPredicate id(ResourceLocation id) {
		return new BlockIDPredicate(id);
	}

	public static BlockIDPredicate id(ResourceLocation id, Map<String, Object> properties) {
		BlockIDPredicate b = id(id);

		for (Map.Entry<String, Object> entry : properties.entrySet()) {
			b = b.with(entry.getKey(), entry.getValue().toString());
		}

		return b;
	}

	public static BlockEntityPredicate entity(ResourceLocation id) {
		return new BlockEntityPredicate(id);
	}

	public static BlockPredicate custom(BlockPredicate predicate) {
		return predicate;
	}

	private static Map<String, Direction> facingMap;

	public static Map<String, Direction> getFacing() {
		if (facingMap == null) {
			facingMap = new HashMap<>(6);

			for (Direction facing : Direction.values()) {
				facingMap.put(facing.getSerializedName(), facing);
			}
		}

		return facingMap;
	}

	@MinecraftClass
	public static Block getBlock(ResourceLocation id) {
		return KubeJSRegistries.blocks().get(id);
	}

	public static List<String> getTypeList() {
		List<String> list = new ArrayList<>();

		for (ResourceLocation block : KubeJSRegistries.blocks().getIds()) {
			list.add(block.toString());
		}

		return list;
	}
}