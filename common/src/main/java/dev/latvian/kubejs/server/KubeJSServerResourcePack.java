package dev.latvian.kubejs.server;

import com.google.gson.JsonElement;
import dev.latvian.kubejs.KubeJSObjects;
import dev.latvian.kubejs.block.BlockBuilder;
import dev.latvian.kubejs.script.data.KubeJSResourcePack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;

import java.util.Map;

public class KubeJSServerResourcePack extends KubeJSResourcePack {
	public KubeJSServerResourcePack() {
		super(PackType.SERVER_DATA);
	}

	@Override
	public void generateJsonFiles(Map<ResourceLocation, JsonElement> map) {
		for (BlockBuilder builder : KubeJSObjects.BLOCKS.values()) {
			map.put(new ResourceLocation(builder.id.getNamespace(), "loot_tables/blocks/" + builder.id.getPath()), builder.getLootTableJson());
		}
	}
}
