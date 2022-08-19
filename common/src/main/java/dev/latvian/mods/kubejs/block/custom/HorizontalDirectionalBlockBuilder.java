package dev.latvian.mods.kubejs.block.custom;

import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.block.BlockBuilder;
import dev.latvian.mods.kubejs.client.ModelGenerator;
import dev.latvian.mods.kubejs.client.VariantBlockStateGenerator;
import dev.latvian.mods.kubejs.generator.AssetJsonGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;

public class HorizontalDirectionalBlockBuilder extends BlockBuilder {

	public HorizontalDirectionalBlockBuilder(ResourceLocation i) {
		super(i);
	}

	@Override
	public void generateAssetJsons(AssetJsonGenerator generator) {
		var baseID = newID("block/", "").toString();

		if (blockstateJson != null) {
			generator.json(newID("blockstates/", ""), blockstateJson);
		} else {
			generator.blockState(id, this::generateBlockStateJson);
		}

		if (modelJson != null) {
			generator.json(newID("models/", ""), modelJson);
		} else {
			generator.blockModel(id, this::generateBlockModelJsons);
		}
		generator.blockModel(id, mg -> {

		});

		if (itemBuilder != null) {
			generator.itemModel(itemBuilder.id, this::generateItemModelJson);
		}

	}

	protected void generateBlockStateJson(VariantBlockStateGenerator bs) {
		var modelLocation = model.isEmpty() ? newID("block/", "").toString() : model;
		bs.variant("facing=north", v -> v.model(modelLocation));
		bs.variant("facing=east", v -> v.model(modelLocation).y(90));
		bs.variant("facing=south", v -> v.model(modelLocation).y(180));
		bs.variant("facing=west", v -> v.model(modelLocation).y(270));
	}

	protected void generateBlockModelJsons(ModelGenerator mg) {
		var side = getTextureOrDefault("side", newID("block/", "").toString());
		mg.texture("side", side);
		mg.texture("front", getTextureOrDefault("front", newID("block/", "_front").toString()));
		mg.texture("particle", textures.get("particle").getAsString());
		mg.texture("top", getTextureOrDefault("top", side));

		if (textures.has("bottom")) {
			mg.parent("block/orientable_with_bottom");
			mg.texture("bottom", textures.get("bottom").getAsString());
		} else {
			mg.parent("minecraft:block/orientable");
		}
	}

	protected void generateItemModelJson(ModelGenerator mg) {
		mg.parent(model.isEmpty() ? newID("block/", "").toString() : model);
	}

	public HorizontalDirectionalBlockBuilder textureFront(String tex) {
		texture("front", tex);
		return this;
	}

	public HorizontalDirectionalBlockBuilder textureSides(String tex) {
		texture("side", tex);
		return this;
	}

	@Override
	public HorizontalDirectionalBlockBuilder textureAll(String tex) {
		super.textureAll(tex);
		texture("side", tex);
		return this;
	}

	private String getTextureOrDefault(String name, String defaultTexture) {
		return textures.has(name)?textures.get(name).getAsString():defaultTexture;
	}

	@Override
	public Block createObject() {
		return new HorizontalDirectionalBlock(createProperties()) {};
	}
}
