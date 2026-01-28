package dev.latvian.mods.kubejs.block.entity;

import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugins;
import dev.latvian.mods.kubejs.util.Lazy;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.resources.Identifier;

import java.util.Map;

public record BlockEntityAttachmentType(Identifier id, TypeInfo typeInfo) {
	public static final Lazy<Map<Identifier, BlockEntityAttachmentType>> ALL = Lazy.map(map -> KubeJSPlugins.forEachPlugin(type -> map.put(type.id, type), KubeJSPlugin::registerBlockEntityAttachments));

	public BlockEntityAttachmentType(Identifier id, Class<?> type) {
		this(id, TypeInfo.of(type));
	}

	@Override
	public String toString() {
		return id.toString();
	}
}
