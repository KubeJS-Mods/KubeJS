package dev.latvian.mods.kubejs.server;

import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.generator.DataJsonGenerator;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.kubejs.script.data.GeneratedData;
import dev.latvian.mods.kubejs.script.data.GeneratedResourcePack;
import dev.latvian.mods.kubejs.util.KubeJSPlugins;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.PackSource;

import java.util.Map;
import java.util.Optional;

public class GeneratedServerResourcePack extends GeneratedResourcePack {
	private final PackLocationInfo packLocationInfo;

	public GeneratedServerResourcePack() {
		super(PackType.SERVER_DATA);
		this.packLocationInfo = new PackLocationInfo("kubejs", Component.empty(), PackSource.DEFAULT, Optional.empty());
		getGenerated();
	}

	@Override
	public void generate(Map<ResourceLocation, GeneratedData> map) {
		var generator = new DataJsonGenerator(map);

		for (var builder : RegistryInfo.ALL_BUILDERS) {
			builder.generateDataJsons(generator);
		}

		KubeJSPlugins.forEachPlugin(generator, KubeJSPlugin::generateDataJsons);
	}

	@Override
	protected boolean forgetFile(String path) {
		// return path.endsWith(".png") || path.endsWith(".ogg");
		return super.forgetFile(path);
	}

	@Override
	public PackLocationInfo location() {
		return packLocationInfo;
	}
}
