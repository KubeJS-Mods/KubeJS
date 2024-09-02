package dev.latvian.mods.kubejs.block.entity;

import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugins;
import dev.latvian.mods.kubejs.util.Lazy;
import dev.latvian.mods.rhino.type.TypeInfo;

import java.util.HashMap;
import java.util.Map;

public record BlockEntityAttachmentType(String name, TypeInfo typeInfo) {
	public static final Lazy<Map<String, BlockEntityAttachmentType>> ALL = Lazy.of(() -> {
		var map = new HashMap<String, BlockEntityAttachmentType>();
		KubeJSPlugins.forEachPlugin(type -> map.put(type.name, type), KubeJSPlugin::registerBlockEntityAttachments);
		return Map.copyOf(map);
	});

	public BlockEntityAttachmentType(String name, Class<?> type) {
		this(name, TypeInfo.of(type));
	}

	@Override
	public String toString() {
		return name;
	}
}
