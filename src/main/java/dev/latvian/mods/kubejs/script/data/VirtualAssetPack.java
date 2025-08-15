package dev.latvian.mods.kubejs.script.data;

import dev.latvian.mods.kubejs.client.LoadedTexture;
import dev.latvian.mods.kubejs.client.SoundsGenerator;
import dev.latvian.mods.kubejs.generator.KubeAssetGenerator;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class VirtualAssetPack extends VirtualResourcePack implements KubeAssetGenerator {
	private final Map<ResourceLocation, LoadedTexture> loadedTextures;
	private final Map<String, SoundsGenerator> sounds;

	public VirtualAssetPack(GeneratedDataStage stage, Supplier<RegistryAccessContainer> registries) {
		super(ScriptType.CLIENT, PackType.CLIENT_RESOURCES, stage, registries);
		loadedTextures = new HashMap<>();
		sounds = new HashMap<>();
	}

	@Override
	public LoadedTexture loadTexture(ResourceLocation id) {
		return loadedTextures.computeIfAbsent(id, KubeAssetGenerator.super::loadTexture);
	}

	@Override
	public void texture(ResourceLocation target, LoadedTexture texture) {
		KubeAssetGenerator.super.texture(target, texture);

		if (texture.width > 0 && texture.height > 0) {
			loadedTextures.put(target, texture);
		}
	}

	@Override
	public void close() {
		super.close();
		loadedTextures.clear();
	}

	@Override
	public void sounds(String namespace, Consumer<SoundsGenerator> consumer) {
		sounds.put(namespace, Util.make(new SoundsGenerator(), consumer));
	}

	@Override
	public void flush() {
		super.flush();
		sounds.forEach((mod, gen) -> json(ResourceLocation.fromNamespaceAndPath(mod, "sounds.json"), gen.toJson()));
		sounds.clear();
	}
}
