package dev.latvian.mods.kubejs.client;

import com.google.gson.JsonElement;
import dev.latvian.mods.kubejs.event.EventJS;
import dev.latvian.mods.kubejs.generator.AssetJsonGenerator;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.function.Consumer;

/**
 * @author LatvianModder
 */
public class ClientGenerateAssetsEventJS extends EventJS {
	public final AssetJsonGenerator generator;
	private final Map<String, String> langMap;

	public ClientGenerateAssetsEventJS(AssetJsonGenerator gen, Map<String, String> langMap) {
		generator = gen;
		this.langMap = langMap;
	}

	public Map<ResourceLocation, JsonElement> getAllAssets() {
		return generator.getAllJsons();
	}

	public void addLang(String key, String value) {
		langMap.put(key, value);
	}

	public void add(ResourceLocation location, JsonElement json) {
		generator.json(location, json);
	}

	public void addModel(String type, ResourceLocation id, Consumer<ModelGenerator> consumer) {
		var gen = Util.make(new ModelGenerator(), consumer);
		add(new ResourceLocation(id.getNamespace(), "models/%s/%s".formatted(type, id.getPath())), gen.toJson());
	}

	public void addBlockState(ResourceLocation id, Consumer<VariantBlockStateGenerator> consumer) {
		var gen = Util.make(new VariantBlockStateGenerator(), consumer);
		add(new ResourceLocation(id.getNamespace(), "blockstates/" + id.getPath()), gen.toJson());
	}

	public void addMultipartBlockState(ResourceLocation id, Consumer<MultipartBlockStateGenerator> consumer) {
		var gen = Util.make(new MultipartBlockStateGenerator(), consumer);
		add(new ResourceLocation(id.getNamespace(), "blockstates/" + id.getPath()), gen.toJson());
	}
}