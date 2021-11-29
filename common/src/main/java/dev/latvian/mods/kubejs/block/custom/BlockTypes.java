package dev.latvian.mods.kubejs.block.custom;

import java.util.HashMap;
import java.util.Map;

public class BlockTypes {
	private static final Map<String, BlockType> MAP = new HashMap<>();

	public static void register(BlockType type) {
		MAP.put(type.name, type);
	}

	public static BlockType get(Object name) {
		return name == null || name.toString().isEmpty() ? BasicBlockType.INSTANCE : MAP.getOrDefault(name.toString(), BasicBlockType.INSTANCE);
	}
}
