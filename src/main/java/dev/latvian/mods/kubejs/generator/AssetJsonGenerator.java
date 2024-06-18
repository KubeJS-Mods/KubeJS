package dev.latvian.mods.kubejs.generator;

import dev.latvian.mods.kubejs.client.LoadedTexture;
import dev.latvian.mods.kubejs.client.ModelGenerator;
import dev.latvian.mods.kubejs.client.MultipartBlockStateGenerator;
import dev.latvian.mods.kubejs.client.VariantBlockStateGenerator;
import dev.latvian.mods.kubejs.color.Color;
import dev.latvian.mods.kubejs.script.data.GeneratedData;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class AssetJsonGenerator extends ResourceGenerator {
	private final Map<ResourceLocation, LoadedTexture> loadedTextures;

	public AssetJsonGenerator(Map<ResourceLocation, GeneratedData> m) {
		super(ConsoleJS.CLIENT, m);
		this.loadedTextures = new HashMap<>();
	}

	public LoadedTexture loadTexture(ResourceLocation id) {
		return loadedTextures.computeIfAbsent(id, LoadedTexture::load);
	}

	public void blockState(ResourceLocation id, Consumer<VariantBlockStateGenerator> consumer) {
		var gen = Util.make(new VariantBlockStateGenerator(), consumer);
		json(ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "blockstates/" + id.getPath()), gen.toJson());
	}

	public void multipartState(ResourceLocation id, Consumer<MultipartBlockStateGenerator> consumer) {
		var gen = Util.make(new MultipartBlockStateGenerator(), consumer);
		json(ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "blockstates/" + id.getPath()), gen.toJson());
	}

	public void blockModel(ResourceLocation id, Consumer<ModelGenerator> consumer) {
		var gen = Util.make(new ModelGenerator(), consumer);
		json(ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "models/block/" + id.getPath()), gen.toJson());
	}

	public void itemModel(ResourceLocation id, Consumer<ModelGenerator> consumer) {
		var gen = Util.make(new ModelGenerator(), consumer);
		json(asItemModelLocation(id), gen.toJson());
	}

	public static ResourceLocation asItemModelLocation(ResourceLocation id) {
		return ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "models/item/" + id.getPath());
	}

	public void texture(ResourceLocation target, LoadedTexture texture) {
		if (texture.width <= 0 || texture.height <= 0) {
			ConsoleJS.CLIENT.error("Failed to load texture " + target);
			return;
		}

		add(ResourceLocation.fromNamespaceAndPath(target.getNamespace(), "textures/" + target.getPath() + ".png"), texture::toBytes);

		if (texture.mcmeta != null) {
			add(ResourceLocation.fromNamespaceAndPath(target.getNamespace(), "textures/" + target.getPath() + ".png.mcmeta"), () -> texture.mcmeta);
		}
	}

	public void stencil(ResourceLocation target, ResourceLocation stencil, Map<Color, Color> colors) {
		var stencilTexture = loadTexture(stencil);
		texture(target, stencilTexture.remap(colors));
	}

	public boolean mask(ResourceLocation target, ResourceLocation mask, ResourceLocation input) {
		var maskTexture = loadTexture(mask);

		if (maskTexture.height != maskTexture.width) {
			return false;
		}

		var in = loadTexture(input);

		int w = Math.max(maskTexture.width, in.width);

		if (maskTexture.width != in.width) {
			int mframes = maskTexture.height / maskTexture.width;
			int iframes = in.height / in.width;
			maskTexture = maskTexture.resize(w, w * mframes);
			in = in.resize(w, w * iframes).copy();
		} else {
			in = in.copy();
		}

		for (int y = 0; y < in.height; y++) {
			for (int x = 0; x < w; x++) {
				int ii = x + (y * w);

				int m = maskTexture.pixels[x + ((y % maskTexture.height) * w)];
				int ma = (m >> 24) & 0xFF;

				if (ma == 0) {
					in.pixels[ii] = 0;
				} else {
					float mr = ((m >> 16) & 0xFF) / 255F;
					float mg = ((m >> 8) & 0xFF) / 255F;
					float mb = (m & 0xFF) / 255F;

					float ir = ((in.pixels[ii] >> 16) & 0xFF) / 255F;
					float ig = ((in.pixels[ii] >> 8) & 0xFF) / 255F;
					float ib = (in.pixels[ii] & 0xFF) / 255F;

					in.pixels[ii] = (((int) (mr * ir * 255F)) << 16) | (((int) (mg * ig * 255F)) << 8) | ((int) (mb * ib * 255F)) | (ma << 24);
				}
			}
		}

		texture(target, in);
		return true;
	}
}
