package dev.latvian.mods.kubejs.server;

import dev.latvian.mods.kubejs.generator.DataJsonGenerator;
import dev.latvian.mods.kubejs.script.data.GeneratedResourcePack;
import dev.latvian.mods.kubejs.util.KubeJSPlugins;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;

import java.util.Map;

public class GeneratedServerResourcePack extends GeneratedResourcePack {
	public GeneratedServerResourcePack() {
		super(PackType.SERVER_DATA);
	}

	@Override
	public void generate(Map<ResourceLocation, byte[]> map) {
		var generator = new DataJsonGenerator(map);
		KubeJSPlugins.forEachPlugin(p -> p.generateDataJsons(generator));
	}
}
