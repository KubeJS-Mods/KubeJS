package dev.latvian.mods.kubejs.registry;

import dev.latvian.mods.kubejs.client.ModelGenerator;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@ReturnsSelf
public abstract class ModelledBuilderBase<T> extends BuilderBase<T> {
	public transient @Nullable Identifier parentModel;
	public transient Map<String, String> textures;
	public transient String baseTexture;
	public transient @Nullable Consumer<ModelGenerator> modelGenerator;

	public ModelledBuilderBase(Identifier id) {
		super(id);
		this.parentModel = null;
		this.textures = new HashMap<>(0);
		this.baseTexture = "";
		this.modelGenerator = null;
	}

	@Info("Sets the texture.")
	public ModelledBuilderBase<T> texture(String tex) {
		baseTexture = tex;
		return this;
	}

	@Info("Sets the texture by given key.")
	public ModelledBuilderBase<T> texture(String[] key, String tex) {
		for (var k : key) {
			textures.put(k, tex);
		}

		return this;
	}

	@Info("Directly set the texture map.")
	public ModelledBuilderBase<T> textures(Map<String, String> map) {
		textures.putAll(map);
		return this;
	}

	@Info("Sets the parent model.")
	public ModelledBuilderBase<T> parentModel(Identifier id) {
		parentModel = id;
		return this;
	}

	@Info("Replaces default model with custom generator.")
	public ModelledBuilderBase<T> modelGenerator(Consumer<ModelGenerator> generator) {
		modelGenerator = generator;
		return this;
	}
}
