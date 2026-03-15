package dev.latvian.mods.kubejs.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

import java.util.LinkedHashMap;
import java.util.Map;

public final class OrderedCompoundTag {
	private OrderedCompoundTag() {
	}

	public static CompoundTag create() {
		return new CompoundTag(new LinkedHashMap<>());
	}

	public static CompoundTag create(Map<String, Tag> map) {
		return new CompoundTag(map instanceof LinkedHashMap<String, Tag> ? map : new LinkedHashMap<>(map));
	}
}

