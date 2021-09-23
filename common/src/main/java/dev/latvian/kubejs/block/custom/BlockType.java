package dev.latvian.kubejs.block.custom;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.latvian.kubejs.block.BlockBuilder;
import dev.latvian.kubejs.generator.AssetJsonGenerator;
import dev.latvian.kubejs.generator.DataJsonGenerator;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BlockType {
	public final String name;

	public BlockType(String n) {
		name = n;
	}

	public abstract Block createBlock(BlockBuilder builder);

	public void applyDefaults(BlockBuilder builder) {
	}

	public void generateAssets(BlockBuilder b, AssetJsonGenerator generator) {
		if (b.blockstateJson != null) {
			generator.json(b.newID("blockstates/", ""), b.blockstateJson);
		} else {
			generator.blockState(b.id, bs -> bs.variant("", b.model.isEmpty() ? (b.id.getNamespace() + ":block/" + b.id.getPath()) : b.model));
		}

		if (b.modelJson != null) {
			generator.json(b.newID("models/block/", ""), b.modelJson);
		} else {
			generator.blockModel(b.id, m -> {
				String particle = b.textures.get("particle").getAsString();

				if (areAllTexturesEqual(b.textures, particle)) {
					m.parent("minecraft:block/cube_all");
					m.texture("all", particle);
				} else {
					m.parent("block/cube");
					m.textures(b.textures);
				}

				if (!b.color.isEmpty() || !b.customShape.isEmpty()) {
					List<AABB> boxes = new ArrayList<>(b.customShape);

					if (boxes.isEmpty()) {
						boxes.add(new AABB(0D, 0D, 0D, 1D, 1D, 1D));
					}

					for (AABB box : boxes) {
						m.element(e -> {
							e.box(box);

							for (Direction direction : Direction.values()) {
								e.face(direction, face -> {
									face.tex("#" + direction.getSerializedName());
									face.cull();

									if (!b.color.isEmpty()) {
										face.tintindex(0);
									}
								});
							}
						});
					}
				}
			});
		}

		if (b.itemBuilder != null) {
			generator.itemModel(b.itemBuilder.id, m -> {
				if (!b.model.isEmpty()) {
					m.parent(b.model);
				} else {
					m.parent(b.newID("block/", "").toString());
				}
			});
		}
	}

	public Map<ResourceLocation, JsonObject> generateBlockModels(BlockBuilder builder) {
		Map<ResourceLocation, JsonObject> map = new HashMap<>();

		if (builder.modelJson != null) {
			map.put(builder.newID("models/block/", ""), builder.modelJson);
		} else {
			JsonObject modelJson = new JsonObject();

			String particle = builder.textures.get("particle").getAsString();

			if (areAllTexturesEqual(builder.textures, particle)) {
				modelJson.addProperty("parent", "block/cube_all");
				JsonObject textures = new JsonObject();
				textures.addProperty("all", particle);
				modelJson.add("textures", textures);
			} else {
				modelJson.addProperty("parent", "block/cube");
				modelJson.add("textures", builder.textures);
			}

			if (!builder.color.isEmpty()) {
				JsonObject cube = new JsonObject();
				JsonArray from = new JsonArray();
				from.add(0);
				from.add(0);
				from.add(0);
				cube.add("from", from);
				JsonArray to = new JsonArray();
				to.add(16);
				to.add(16);
				to.add(16);
				cube.add("to", to);
				JsonObject faces = new JsonObject();

				for (Direction direction : Direction.values()) {
					JsonObject f = new JsonObject();
					f.addProperty("texture", "#" + direction.getSerializedName());
					f.addProperty("cullface", direction.getSerializedName());
					f.addProperty("tintindex", 0);
					faces.add(direction.getSerializedName(), f);
				}

				cube.add("faces", faces);

				JsonArray elements = new JsonArray();
				elements.add(cube);
				modelJson.add("elements", elements);
			}

			map.put(builder.newID("models/block/", ""), modelJson);
		}

		return map;
	}

	private boolean areAllTexturesEqual(JsonObject tex, String t) {
		for (Direction direction : Direction.values()) {
			if (!tex.get(direction.getSerializedName()).getAsString().equals(t)) {
				return false;
			}
		}

		return true;
	}

	public void generateData(BlockBuilder builder, DataJsonGenerator generator) {

	}
}
