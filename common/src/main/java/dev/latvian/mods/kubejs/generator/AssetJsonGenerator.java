package dev.latvian.mods.kubejs.generator;

import com.google.gson.JsonElement;
import dev.latvian.mods.kubejs.client.ModelGenerator;
import dev.latvian.mods.kubejs.client.MultipartBlockStateGenerator;
import dev.latvian.mods.kubejs.client.VariantBlockStateGenerator;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.function.Consumer;

public class AssetJsonGenerator extends JsonGenerator {
	public AssetJsonGenerator(Map<ResourceLocation, JsonElement> m) {
		super(ConsoleJS.CLIENT, m);
	}

	public void blockState(ResourceLocation id, Consumer<VariantBlockStateGenerator> consumer) {
		VariantBlockStateGenerator gen = new VariantBlockStateGenerator();
		consumer.accept(gen);
		json(new ResourceLocation(id.getNamespace(), "blockstates/" + id.getPath()), gen.toJson());
	}

	public void multipartState(ResourceLocation id, Consumer<MultipartBlockStateGenerator> consumer) {
		MultipartBlockStateGenerator gen = new MultipartBlockStateGenerator();
		consumer.accept(gen);
		json(new ResourceLocation(id.getNamespace(), "blockstates/" + id.getPath()), gen.toJson());
	}

	public void blockModel(ResourceLocation id, Consumer<ModelGenerator> consumer) {
		ModelGenerator gen = new ModelGenerator();
		consumer.accept(gen);
		json(new ResourceLocation(id.getNamespace(), "models/block/" + id.getPath()), gen.toJson());
	}

	public void itemModel(ResourceLocation id, Consumer<ModelGenerator> consumer) {
		ModelGenerator gen = new ModelGenerator();
		consumer.accept(gen);
		json(new ResourceLocation(id.getNamespace(), "models/item/" + id.getPath()), gen.toJson());
	}
}
