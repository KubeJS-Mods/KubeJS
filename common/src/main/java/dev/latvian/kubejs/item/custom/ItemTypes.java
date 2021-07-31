package dev.latvian.kubejs.item.custom;

import java.util.HashMap;
import java.util.Map;

public class ItemTypes {
	private static final Map<String, ItemType> MAP = new HashMap<>();

	public static void register(ItemType type) {
		MAP.put(type.name, type);
	}

	public static ItemType get(Object name) {
		return name == null || name.toString().isEmpty() ? BasicItemType.INSTANCE : MAP.getOrDefault(name.toString(), BasicItemType.INSTANCE);
	}
}
