package dev.latvian.kubejs.block;

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
	}

	@Override
	public String getType() {
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