package dev.latvian.mods.kubejs.script.data;

import dev.latvian.mods.kubejs.client.LoadedTexture;
import dev.latvian.mods.kubejs.generator.KubeAssetGenerator;
import dev.latvian.mods.kubejs.script.ScriptType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;

import java.util.HashMap;
import java.util.Map;

public class VirtualAssetPack extends VirtualResourcePack implements KubeAssetGenerator {
	private final Map<ResourceLocation, LoadedTexture> loadedTextures;

	public VirtualAssetPack(GeneratedDataStage stage) {
		super(ScriptType.CLIENT, PackType.CLIENT_RESOURCES, stage);
		loadedTextures = new HashMap<>();
	}

	@Override
	public LoadedTexture loadTexture(ResourceLocation id) {
		return loadedTextures.computeIfAbsent(id, LoadedTexture::load);
	}

	@Override
	public void close() {
		super.close();
		loadedTextures.clear();
	}
}
