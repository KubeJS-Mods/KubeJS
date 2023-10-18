package dev.latvian.mods.kubejs.generator;

import dev.latvian.mods.kubejs.client.ModelGenerator;
import dev.latvian.mods.kubejs.client.MultipartBlockStateGenerator;
import dev.latvian.mods.kubejs.client.VariantBlockStateGenerator;
import dev.latvian.mods.kubejs.script.data.GeneratedData;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.function.Consumer;

public class AssetJsonGenerator extends JsonGenerator {
	public AssetJsonGenerator(Map<ResourceLocation, GeneratedData> m) {
		super(ConsoleJS.CLIENT, m);
	}

	public void blockState(ResourceLocation id, Consumer<VariantBlockStateGenerator> consumer) {
		var gen = Util.make(new VariantBlockStateGenerator(), consumer);
		json(new ResourceLocation(id.getNamespace(), "blockstates/" + id.getPath()), gen.toJson());
	}

	public void multipartState(ResourceLocation id, Consumer<MultipartBlockStateGenerator> consumer) {
		var gen = Util.make(new MultipartBlockStateGenerator(), consumer);
		json(new ResourceLocation(id.getNamespace(), "blockstates/" + id.getPath()), gen.toJson());
	}

	public void blockModel(ResourceLocation id, Consumer<ModelGenerator> consumer) {
		var gen = Util.make(new ModelGenerator(), consumer);
		json(new ResourceLocation(id.getNamespace(), "models/block/" + id.getPath()), gen.toJson());
	}

	public void itemModel(ResourceLocation id, Consumer<ModelGenerator> consumer) {
		var gen = Util.make(new ModelGenerator(), consumer);
		json(asItemModelLocation(id), gen.toJson());
	}

	public static ResourceLocation asItemModelLocation(ResourceLocation id) {
		return new ResourceLocation(id.getNamespace(), "models/item/" + id.getPath());
	}
}
