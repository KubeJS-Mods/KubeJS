package dev.latvian.mods.kubejs.generator;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.client.LoadedTexture;
import dev.latvian.mods.kubejs.client.ModelGenerator;
import dev.latvian.mods.kubejs.client.MultipartBlockStateGenerator;
import dev.latvian.mods.kubejs.client.ParticleGenerator;
import dev.latvian.mods.kubejs.client.SoundsGenerator;
import dev.latvian.mods.kubejs.client.VariantBlockStateGenerator;
import dev.latvian.mods.kubejs.color.KubeColor;
import dev.latvian.mods.kubejs.script.ConsoleJS;
import dev.latvian.mods.kubejs.script.data.GeneratedData;
import dev.latvian.mods.kubejs.util.ID;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.function.Consumer;

public interface KubeAssetGenerator extends KubeResourceGenerator {
	Identifier GENERATED_ITEM_MODEL = Identifier.withDefaultNamespace("item/generated");
	Identifier HANDHELD_ITEM_MODEL = Identifier.withDefaultNamespace("item/handheld");
	Identifier CUBE_BLOCK_MODEL = Identifier.withDefaultNamespace("block/cube");
	Identifier CUBE_ALL_BLOCK_MODEL = Identifier.withDefaultNamespace("block/cube_all");

	default LoadedTexture loadTexture(Identifier id) {
		return LoadedTexture.load(id);
	}

	default void blockState(Identifier id, Consumer<VariantBlockStateGenerator> consumer) {
		var gen = Util.make(new VariantBlockStateGenerator(), consumer);
		json(id.withPath(ID.BLOCKSTATE), gen.toJson());
	}

	default void multipartState(Identifier id, Consumer<MultipartBlockStateGenerator> consumer) {
		var gen = Util.make(new MultipartBlockStateGenerator(), consumer);
		json(id.withPath(ID.BLOCKSTATE), gen.toJson());
	}

	default void blockModel(Identifier id, Consumer<ModelGenerator> consumer) {
		var gen = Util.make(new ModelGenerator(), consumer);
		json(id.withPath(ID.BLOCK_MODEL), gen.toJson());
	}

	default void itemModel(Identifier id, Consumer<ModelGenerator> consumer) {
		itemModel(id, consumer, null);
	}

	default void itemModel(Identifier id, Consumer<ModelGenerator> consumer, @Nullable JsonArray tints) {
		var gen = Util.make(new ModelGenerator(), consumer);
		json(id.withPath(ID.ITEM_MODEL), gen.toJson());
		itemDefinition(id, id.withPath(ID.ITEM), tints);
	}

	default void itemDefinition(Identifier id, Identifier model, @Nullable JsonArray tints) {
		var modelRef = new JsonObject();
		modelRef.addProperty("type", "minecraft:model");
		modelRef.addProperty("model", model.toString());

		if (tints != null) {
			modelRef.add("tints", tints);
		}

		var def = new JsonObject();
		def.add("model", modelRef);
		json(id.withPath(ID.ITEM_DEFINITION), def);
	}

	static @Nullable JsonArray createItemTintSources(int maxTintIndex) {
		if (maxTintIndex < 0) {
			return null;
		}

		var tints = new JsonArray();

		for (int i = 0; i <= maxTintIndex; i++) {
			var tintSource = new JsonObject();
			tintSource.addProperty("type", "kubejs:tint");
			tintSource.addProperty("index", i);
			tints.add(tintSource);
		}

		return tints;
	}

	default void defaultItemModel(Identifier id) {
		itemModel(id, model -> {
			model.parent(GENERATED_ITEM_MODEL);
			model.texture("layer0", id.withPath(ID.ITEM).toString());
		});
	}

	default void defaultHandheldItemModel(Identifier id) {
		itemModel(id, model -> {
			model.parent(HANDHELD_ITEM_MODEL);
			model.texture("layer0", id.withPath(ID.ITEM).toString());
		});
	}

	default void texture(Identifier target, LoadedTexture texture) {
		if (texture.width <= 0 || texture.height <= 0) {
			ConsoleJS.CLIENT.error("Failed to save texture " + target);
			return;
		}

		add(new GeneratedData(target.withPath(ID.PNG_TEXTURE), texture::toBytes));

		if (texture.mcmeta != null) {
			add(new GeneratedData(target.withPath(ID.PNG_TEXTURE_MCMETA), () -> texture.mcmeta));
		}
	}

	default void stencil(Identifier target, Identifier stencil, Map<KubeColor, KubeColor> colors) {
		var stencilTexture = loadTexture(stencil);

		if (stencilTexture.width == 0 || stencilTexture.height == 0) {
			ConsoleJS.CLIENT.error("Failed to load texture " + stencil);
			return;
		}

		texture(target, stencilTexture.remap(colors));
	}

	default boolean mask(Identifier target, Identifier mask, Identifier input) {
		var maskTexture = loadTexture(mask);

		if (maskTexture.height != maskTexture.width || maskTexture.width == 0) {
			return false;
		}

		var in = loadTexture(input);

		if (in.width == 0 || in.height == 0) {
			return false;
		}

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

	default void particle(Identifier id, Consumer<ParticleGenerator> consumer) {
		json(id.withPath(ID.PARTICLE), Util.make(new ParticleGenerator(), consumer).toJson());
	}

	default void sounds(String namespace, Consumer<SoundsGenerator> consumer) {
	}
}
