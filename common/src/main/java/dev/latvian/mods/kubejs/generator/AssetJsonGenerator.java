package dev.latvian.mods.kubejs.generator;

import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.KubeJSPaths;
import dev.latvian.mods.kubejs.client.ModelGenerator;
import dev.latvian.mods.kubejs.client.MultipartBlockStateGenerator;
import dev.latvian.mods.kubejs.client.ParticleGenerator;
import dev.latvian.mods.kubejs.client.SoundGenerator;
import dev.latvian.mods.kubejs.client.StencilTexture;
import dev.latvian.mods.kubejs.client.VariantBlockStateGenerator;
import dev.latvian.mods.kubejs.script.data.GeneratedData;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;

import javax.imageio.ImageIO;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class AssetJsonGenerator extends ResourceGenerator {
	private final Map<String, StencilTexture> stencils;
	private final Map<String, SoundGenerator> sounds;

	public AssetJsonGenerator(Map<ResourceLocation, GeneratedData> m) {
		super(ConsoleJS.CLIENT, m);
		this.stencils = new HashMap<>();
		this.sounds = new HashMap<>();
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

	public void particle(ResourceLocation id, Consumer<ParticleGenerator> consumer) {
		var gen = Util.make(new ParticleGenerator(), consumer);
		json(new ResourceLocation(id.getNamespace(), "particles/" + id.getPath()), gen.toJson());
	}

	public void sounds(String mod, Consumer<SoundGenerator> consumer) {
		if (sounds.containsKey(mod)) {
			consumer.accept(sounds.get(mod));
		} else {
			sounds.put(mod, Util.make(new SoundGenerator(), consumer));
		}
	}

	public static ResourceLocation asItemModelLocation(ResourceLocation id) {
		return new ResourceLocation(id.getNamespace(), "models/item/" + id.getPath());
	}

	public void stencil(ResourceLocation target, String stencil, JsonObject colors) throws IOException {
		var st = stencils.get(stencil);

		if (st == null) {
			var path = KubeJSPaths.ASSETS.resolve("kubejs/textures/stencil/" + stencil + ".png");

			if (Files.notExists(path)) {
				throw new IllegalArgumentException("Stencil file 'kubejs/assets/kubejs/textures/stencil/'" + stencil + ".png' not found!");
			}

			try (var in = new BufferedInputStream(Files.newInputStream(path))) {
				var metaPath = KubeJSPaths.ASSETS.resolve("kubejs/textures/stencil/" + stencil + ".png.mcmeta");
				byte[] meta = null;

				if (Files.exists(metaPath)) {
					meta = Files.readAllBytes(metaPath);
				}

				st = new StencilTexture(ImageIO.read(in), meta);
				stencils.put(stencil, st);
			}
		}

		var st1 = st;
		add(new ResourceLocation(target.getNamespace(), "textures/" + target.getPath() + ".png"), () -> st1.create(colors), true);

		if (st.mcmeta != null) {
			add(new ResourceLocation(target.getNamespace(), "textures/" + target.getPath() + ".png.mcmeta"), () -> st1.mcmeta, false);
		}
	}

	public void buildSounds() {
		sounds.forEach((mod, gen) -> json(new ResourceLocation(mod, "sounds"), gen.toJson()));
	}
}
