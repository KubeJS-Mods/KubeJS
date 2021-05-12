package dev.latvian.kubejs.block;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.latvian.kubejs.util.BuilderBase;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import me.shedaniel.architectury.registry.BlockProperties;
import me.shedaniel.architectury.registry.ToolType;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author LatvianModder
 */
public class BlockBuilder extends BuilderBase {
	public static BlockBuilder current;

	public MaterialJS material;
	public float hardness;
	public float resistance;
	public float lightLevel;
	public ToolType harvestTool;
	public int harvestLevel;
	public boolean opaque;
	public boolean fullBlock;
	public boolean requiresTool;
	public String renderType;
	public Int2IntOpenHashMap color;
	public final JsonObject textures;
	public String model;
	public BlockItemBuilder itemBuilder;
	public List<VoxelShape> customShape;
	public boolean notSolid;
	public boolean waterlogged;
	public boolean noDrops;
	public float slipperiness = 0.6F;
	public float speedFactor = 1.0F;
	public float jumpFactor = 1.0F;
	public Consumer<RandomTickCallbackJS> randomTickCallback;
	private JsonObject lootTableJson;
	private JsonObject blockstateJson;
	private JsonObject modelJson;

	public BlockJS block;

	public BlockBuilder(String i) {
		super(i);
		material = MaterialListJS.INSTANCE.map.get("wood");
		hardness = 0.5F;
		resistance = -1F;
		lightLevel = 0F;
		harvestTool = null;
		harvestLevel = -1;
		opaque = true;
		fullBlock = false;
		requiresTool = false;
		renderType = "solid";
		color = new Int2IntOpenHashMap();
		color.defaultReturnValue(0xFFFFFFFF);
		textures = new JsonObject();
		texture(id.getNamespace() + ":block/" + id.getPath());
		model = id.getNamespace() + ":block/" + id.getPath();
		itemBuilder = new BlockItemBuilder(i);
		itemBuilder.blockBuilder = this;
		itemBuilder.parentModel = model;
		customShape = new ArrayList<>();
		notSolid = false;
		waterlogged = false;
		noDrops = false;
		randomTickCallback = null;
	}

	@Override
	public String getBuilderType() {
		return "block";
	}

	public BlockBuilder material(MaterialJS m) {
		material = m;
		return this;
	}

	public BlockBuilder hardness(float h) {
		hardness = h;
		return this;
	}

	public BlockBuilder resistance(float r) {
		resistance = r;
		return this;
	}

	public BlockBuilder unbreakable() {
		hardness = -1F;
		resistance = Float.MAX_VALUE;
		return this;
	}

	public BlockBuilder lightLevel(float light) {
		lightLevel = light;
		return this;
	}

	public BlockBuilder harvestTool(ToolType tool, int level) {
		harvestTool = tool;
		harvestLevel = level;
		return this;
	}

	public BlockBuilder harvestTool(String tool, int level) {
		return harvestTool(ToolType.byName(tool), level);
	}

	public BlockBuilder opaque(boolean o) {
		opaque = o;
		return this;
	}

	public BlockBuilder fullBlock(boolean f) {
		fullBlock = f;
		return this;
	}

	public BlockBuilder requiresTool(boolean f) {
		requiresTool = f;
		return this;
	}

	public BlockBuilder renderType(String l) {
		renderType = l;
		return this;
	}

	public BlockBuilder color(int index, int c) {
		color.put(index, 0xFF000000 | c);
		return this;
	}

	public BlockBuilder texture(String tex) {
		for (Direction direction : Direction.values()) {
			textures.addProperty(direction.getSerializedName(), tex);
		}

		textures.addProperty("particle", tex);
		return this;
	}

	public BlockBuilder texture(String id, String tex) {
		textures.addProperty(id, tex);
		return this;
	}

	public BlockBuilder texture(Direction direction, String tex) {
		return texture(direction.getSerializedName(), tex);
	}

	public BlockBuilder model(String m) {
		model = m;
		itemBuilder.parentModel = model;
		return this;
	}

	public BlockBuilder item(@Nullable Consumer<BlockItemBuilder> i) {
		if (i == null) {
			itemBuilder = null;
		} else {
			i.accept(itemBuilder);
		}

		return this;
	}

	public BlockBuilder noItem() {
		return item(null);
	}

	public BlockBuilder shapeCube(double x0, double y0, double z0, double x1, double y1, double z1) {
		customShape.add(Block.box(x0, y0, z0, x1, y1, z1));
		return this;
	}

	public BlockBuilder notSolid() {
		notSolid = true;
		return this;
	}

	public BlockBuilder waterlogged() {
		waterlogged = true;
		return this;
	}

	public BlockBuilder noDrops() {
		noDrops = true;
		return this;
	}

	public BlockBuilder slipperiness(float f) {
		slipperiness = f;
		return this;
	}

	public BlockBuilder speedFactor(float f) {
		speedFactor = f;
		return this;
	}

	public BlockBuilder jumpFactor(float f) {
		jumpFactor = f;
		return this;
	}

	/**
	 * Sets random tick callback for this black.
	 *
	 * @param randomTickCallback A callback using a block container and a random.
	 */
	public BlockBuilder randomTick(@Nullable Consumer<RandomTickCallbackJS> randomTickCallback) {
		this.randomTickCallback = randomTickCallback;
		return this;
	}

	public void setLootTableJson(JsonObject o) {
		lootTableJson = o;
	}

	public JsonObject getLootTableJson() {
		if (lootTableJson == null) {
			lootTableJson = new JsonObject();
			lootTableJson.addProperty("type", "minecraft:block");
			JsonArray pools = new JsonArray();
			JsonObject pool = new JsonObject();
			pool.addProperty("rolls", 1);
			JsonArray entries = new JsonArray();
			JsonObject entry = new JsonObject();
			entry.addProperty("type", "minecraft:item");
			entry.addProperty("name", id.toString());
			entries.add(entry);
			pool.add("entries", entries);
			JsonArray conditions = new JsonArray();
			JsonObject condition = new JsonObject();
			condition.addProperty("condition", "minecraft:survives_explosion");
			conditions.add(condition);
			pool.add("conditions", conditions);
			pools.add(pool);
			lootTableJson.add("pools", pools);
		}

		return lootTableJson;
	}

	public void setBlockstateJson(JsonObject o) {
		blockstateJson = o;
	}

	public JsonObject getBlockstateJson() {
		if (blockstateJson == null) {
			blockstateJson = new JsonObject();
			JsonObject variants = new JsonObject();
			JsonObject modelo = new JsonObject();
			modelo.addProperty("model", model);
			variants.add("", modelo);
			blockstateJson.add("variants", variants);
		}

		return blockstateJson;
	}

	public void setModelJson(JsonObject o) {
		modelJson = o;
	}

	public JsonObject getModelJson() {
		if (modelJson == null) {
			modelJson = new JsonObject();

			String particle = textures.get("particle").getAsString();

			if (areAllTexturesEqual(textures, particle)) {
				modelJson.addProperty("parent", "block/cube_all");
				JsonObject textures = new JsonObject();
				textures.addProperty("all", particle);
				modelJson.add("textures", textures);
			} else {
				modelJson.addProperty("parent", "block/cube");
				modelJson.add("textures", textures);
			}

			if (!color.isEmpty()) {
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
		}

		return modelJson;
	}

	private boolean areAllTexturesEqual(JsonObject tex, String t) {
		for (Direction direction : Direction.values()) {
			if (!tex.get(direction.getSerializedName()).getAsString().equals(t)) {
				return false;
			}
		}

		return true;
	}

	public Block.Properties createProperties() {
		BlockProperties properties = BlockProperties.of(material.getMinecraftMaterial());
		properties.sound(material.getSound());

		if (resistance >= 0F) {
			properties.strength(hardness, resistance);
		} else {
			properties.strength(hardness);
		}

		properties.lightLevel(state -> (int) (lightLevel * 15F));

		if (harvestTool != null && harvestLevel >= 0) {
			properties.tool(harvestTool, harvestLevel);
		}

		if (notSolid) {
			properties.noOcclusion();
		}

		if (requiresTool) {
			properties.requiresCorrectToolForDrops();
		}

		if (noDrops) {
			properties.noDrops();
		}

		properties.friction(slipperiness);
		properties.speedFactor(speedFactor);
		properties.jumpFactor(jumpFactor);
		return properties;
	}
}