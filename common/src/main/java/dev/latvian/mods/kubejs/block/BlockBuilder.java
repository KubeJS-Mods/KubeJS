package dev.latvian.mods.kubejs.block;

import com.google.gson.JsonObject;
import dev.architectury.registry.block.BlockProperties;
import dev.architectury.registry.block.ToolType;
import dev.latvian.mods.kubejs.block.custom.BasicBlockType;
import dev.latvian.mods.kubejs.block.custom.BlockType;
import dev.latvian.mods.kubejs.loot.LootBuilder;
import dev.latvian.mods.kubejs.util.BuilderBase;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 * @author LatvianModder
 */
public class BlockBuilder extends BuilderBase {
	public static BlockBuilder current;

	public transient BlockType type;
	public transient MaterialJS material;
	public transient float hardness;
	public transient float resistance;
	public transient float lightLevel;
	public transient ToolType harvestTool;
	public transient int harvestLevel;
	public transient boolean opaque;
	public transient boolean fullBlock;
	public transient boolean requiresTool;
	public transient String renderType;
	public transient Int2IntOpenHashMap color;
	public transient final JsonObject textures;
	public transient String model;
	public transient BlockItemBuilder itemBuilder;
	public transient List<AABB> customShape;
	public transient boolean noCollission;
	public transient boolean notSolid;
	public transient boolean waterlogged;
	public transient float slipperiness = 0.6F;
	public transient float speedFactor = 1.0F;
	public transient float jumpFactor = 1.0F;
	public Consumer<RandomTickCallbackJS> randomTickCallback;
	public Consumer<LootBuilder> lootTable;
	public JsonObject blockstateJson;
	public JsonObject modelJson;
	public transient boolean noValidSpawns;
	public transient boolean suffocating;
	public transient boolean viewBlocking;
	public transient boolean redstoneConductor;
	public transient boolean transparent;
	public transient Set<String> defaultTags;

	public transient Block block;

	public BlockBuilder(String i) {
		super(i);
		type = BasicBlockType.INSTANCE;
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
		model = "";
		itemBuilder = new BlockItemBuilder(i);
		itemBuilder.blockBuilder = this;
		customShape = new ArrayList<>();
		noCollission = false;
		notSolid = false;
		waterlogged = false;
		randomTickCallback = null;

		lootTable = loot -> loot.addPool(pool -> {
			pool.survivesExplosion();
			pool.addItem(new ItemStack(block));
		});

		blockstateJson = null;
		modelJson = null;
		noValidSpawns = false;
		suffocating = true;
		viewBlocking = true;
		redstoneConductor = true;
		transparent = false;
		defaultTags = new HashSet<>();
	}

	@Override
	public String getBuilderType() {
		return "block";
	}

	public BlockBuilder type(BlockType t) {
		type = t;
		type.applyDefaults(this);
		return this;
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
		for (var direction : Direction.values()) {
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
			lootTable = null;
		} else {
			i.accept(itemBuilder);
		}

		return this;
	}

	public BlockBuilder noItem() {
		return item(null);
	}

	public BlockBuilder box(double x0, double y0, double z0, double x1, double y1, double z1, boolean scale16) {
		if (scale16) {
			customShape.add(new AABB(x0 / 16D, y0 / 16D, z0 / 16D, x1 / 16D, y1 / 16D, z1 / 16D));
		} else {
			customShape.add(new AABB(x0, y0, z0, x1, y1, z1));
		}

		return this;
	}

	public BlockBuilder box(double x0, double y0, double z0, double x1, double y1, double z1) {
		return box(x0, y0, z0, x1, y1, z1, true);
	}

	public VoxelShape createShape() {
		if (customShape.isEmpty()) {
			return Shapes.block();
		}

		VoxelShape shape = Shapes.create(customShape.get(0));

		for (int i = 1; i < customShape.size(); i++) {
			shape = Shapes.or(shape, Shapes.create(customShape.get(i)));
		}

		return shape;
	}

	public BlockBuilder noCollission() {
		noCollission = true;
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
		lootTable = null;
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

	public BlockBuilder noValidSpawns(boolean b) {
		noValidSpawns = b;
		return this;
	}

	public BlockBuilder suffocating(boolean b) {
		suffocating = b;
		return this;
	}

	public BlockBuilder viewBlocking(boolean b) {
		viewBlocking = b;
		return this;
	}

	public BlockBuilder redstoneConductor(boolean b) {
		redstoneConductor = b;
		return this;
	}

	public BlockBuilder transparent(boolean b) {
		transparent = b;
		return this;
	}

	public BlockBuilder defaultCutout() {
		return renderType("cutout").notSolid().noValidSpawns(true).suffocating(false).viewBlocking(false).redstoneConductor(false).transparent(true);
	}

	public BlockBuilder defaultTranslucent() {
		return defaultCutout().renderType("translucent");
	}

	public BlockBuilder tag(String tag) {
		defaultTags.add(tag);
		return this;
	}

	public BlockBuilder tagBlockAndItem(String tag) {
		defaultTags.add(tag);
		itemBuilder.defaultTags.add(tag);
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

		// TODO: migrate to tag tools and harvest levels
		//  (probably using defaultTags directly)
		/*if (harvestTool != null && harvestLevel >= 0) {
			properties.tool(harvestTool, harvestLevel);
		}*/

		if (noCollission) {
			properties.noCollission();
		}

		if (notSolid) {
			properties.noOcclusion();
		}

		if (requiresTool) {
			properties.requiresCorrectToolForDrops();
		}

		if (lootTable == null) {
			properties.noDrops();
		}

		properties.friction(slipperiness);
		properties.speedFactor(speedFactor);
		properties.jumpFactor(jumpFactor);

		if (noValidSpawns) {
			properties.isValidSpawn((blockState, blockGetter, blockPos, object) -> false);
		}

		if (!suffocating) {
			properties.isSuffocating((blockState, blockGetter, blockPos) -> false);
		}

		if (!viewBlocking) {
			properties.isViewBlocking((blockState, blockGetter, blockPos) -> false);
		}

		if (!redstoneConductor) {
			properties.isRedstoneConductor((blockState, blockGetter, blockPos) -> false);
		}

		if (randomTickCallback != null) {
			properties.randomTicks();
		}

		return properties;
	}
}