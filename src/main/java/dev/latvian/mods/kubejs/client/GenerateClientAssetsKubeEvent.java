package dev.latvian.mods.kubejs.client;

import com.google.gson.JsonElement;
import dev.latvian.mods.kubejs.color.Color;
import dev.latvian.mods.kubejs.event.KubeEvent;
import dev.latvian.mods.kubejs.generator.AssetJsonGenerator;
import dev.latvian.mods.kubejs.script.ConsoleJS;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.function.Consumer;

public class GenerateClientAssetsKubeEvent implements KubeEvent {
	public final AssetJsonGenerator generator;

	public GenerateClientAssetsKubeEvent(AssetJsonGenerator gen) {
		this.generator = gen;
	}

	public void addLang(String key, String value) {
		ConsoleJS.CLIENT.error("Use ClientEvents.lang('en_us', event => { event.add(key, value) }) instead!");
	}

	public void add(ResourceLocation location, JsonElement json) {
		generator.json(location, json);
	}

	public void addModel(String type, ResourceLocation id, Consumer<ModelGenerator> consumer) {
		var gen = Util.make(new ModelGenerator(), consumer);
		add(ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "models/%s/%s".formatted(type, id.getPath())), gen.toJson());
	}

	public void addBlockState(ResourceLocation id, Consumer<VariantBlockStateGenerator> consumer) {
		var gen = Util.make(new VariantBlockStateGenerator(), consumer);
		add(ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "blockstates/" + id.getPath()), gen.toJson());
	}

	public void addMultipartBlockState(ResourceLocation id, Consumer<MultipartBlockStateGenerator> consumer) {
		var gen = Util.make(new MultipartBlockStateGenerator(), consumer);
		add(ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "blockstates/" + id.getPath()), gen.toJson());
	}

	public void stencil(ResourceLocation target, ResourceLocation stencil, Map<Color, Color> remap) {
		generator.stencil(target, stencil, remap);
	}

	public void defaultItemModel(ResourceLocation id) {
		addModel("item", id, model -> {
			model.parent("minecraft:item/generated");
			model.texture("layer0", id.getNamespace() + ":item/" + id.getPath());
		});
	}

	public void defaultHandheldItemModel(ResourceLocation id) {
		addModel("item", id, model -> {
			model.parent("minecraft:item/handheld");
			model.texture("layer0", id.getNamespace() + ":item/" + id.getPath());
		});
	}
}