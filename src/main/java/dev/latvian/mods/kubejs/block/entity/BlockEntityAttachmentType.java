package dev.latvian.mods.kubejs.block.entity;

import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugins;
import dev.latvian.mods.kubejs.util.Lazy;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public record BlockEntityAttachmentType(ResourceLocation id, TypeInfo typeInfo) {
	public static final Lazy<Map<ResourceLocation, BlockEntityAttachmentType>> ALL = Lazy.of(() -> {
		var map = new HashMap<ResourceLocation, BlockEntityAttachmentType>();
		KubeJSPlugins.forEachPlugin(type -> map.put(type.id, type), KubeJSPlugin::registerBlockEntityAttachments);
		return Map.copyOf(map);
	});

	public BlockEntityAttachmentType(ResourceLocation id, Class<?> type) {
		this(id, TypeInfo.of(type));
	}

	@Override
	public String toString() {
		return id.toString();
	}
}
