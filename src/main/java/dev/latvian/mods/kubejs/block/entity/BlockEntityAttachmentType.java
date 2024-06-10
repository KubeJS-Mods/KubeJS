package dev.latvian.mods.kubejs.block.entity;

import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.util.KubeJSPlugins;
import dev.latvian.mods.kubejs.util.Lazy;
import dev.latvian.mods.rhino.type.TypeInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public record BlockEntityAttachmentType(String type, TypeInfo input, BlockEntityAttachment.FactoryProvider factory) {
	public static final Lazy<Map<String, BlockEntityAttachmentType>> ALL = Lazy.of(() -> {
		var map = new HashMap<String, BlockEntityAttachmentType>();
		var list = new ArrayList<BlockEntityAttachmentType>();
		KubeJSPlugins.forEachPlugin(list, KubeJSPlugin::registerBlockEntityAttachments);

		for (var type : list) {
			map.put(type.type, type);
		}

		return map;
	});

	@Override
	public String toString() {
		return type;
	}
}
