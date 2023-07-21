package dev.latvian.mods.kubejs.block;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.client.ModelGenerator;
import dev.latvian.mods.kubejs.client.VariantBlockStateGenerator;
import dev.latvian.mods.kubejs.generator.AssetJsonGenerator;
import dev.latvian.mods.kubejs.generator.DataJsonGenerator;
import dev.latvian.mods.kubejs.loot.LootBuilder;
import dev.latvian.mods.kubejs.registry.BuilderBase;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.mod.util.color.Color;
import dev.latvian.mods.rhino.util.HideFromJS;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class BlockBuilder extends BuilderBase<Block> {
	private static final Consumer<LootBuilder> EMPTY = loot -> {
	};

	private static final BlockBehaviour.StatePredicate ALWAYS_FALSE_STATE_PREDICATE = (blockState, blockGetter, blockPos) -> false;
	private static final BlockBehaviour.StateArgumentPredicate<?> ALWAYS_FALSE_STATE_ARG_PREDICATE = (blockState, blockGetter, blockPos, type) -> false;

	public transient MaterialJS material;
	public transient float hardness;
	public transient float resistance;
	public transient float lightLevel;
	public transient boolean opaque;
	public transient boolean fullBlock;
	public transient boolean requiresTool;
	public transient String renderType;
	public transient Int2IntOpenHashMap color;
	public transient final JsonObject textures;
	public transient String model;
	public transient BlockItemBuilder itemBuilder;
	public transient List<AABB> customShape;
	public transient boolean noCollision;
	public transient boolean notSolid;
	public transient float slipperiness = Float.NaN;
	public transient float speedFactor = Float.NaN;
	public transient float jumpFactor = Float.NaN;
	public Consumer<RandomTickCallbackJS> randomTickCallback;
	public Consumer<LootBuilder> lootTable;
	public JsonObject blockstateJson;
	public JsonObject modelJson;
	public transient boolean noValidSpawns;
	public transient boolean suffocating;
	public transient boolean viewBlocking;
	public transient boolean redstoneConductor;
	public transient boolean transparent;
	public transient Set<Property<?>> blockStateProperties;
	public transient Consumer<BlockStateModifyCallbackJS> defaultStateModification;
	public transient Consumer<BlockStateModifyPlacementCallbackJS> placementStateModification;
	public transient Function<CanBeReplacedCallbackJS, Boolean> canBeReplacedFunction;

	public BlockBuilder(ResourceLocation i) {
		super(i);
		material = MaterialListJS.INSTANCE.map.get("wood");
		hardness = 1.5F;
		resistance = 3F;
		lightLevel = 0F;
		opaque = true;
		fullBlock = false;
		requiresTool = false;
		renderType = "solid";
		color = new Int2IntOpenHashMap();
		color.defaultReturnValue(0xFFFFFFFF);
		textures = new JsonObject();
		textureAll(id.getNamespace() + ":block/" + id.getPath());
		model = "";
		itemBuilder = getOrCreateItemBuilder();
		itemBuilder.blockBuilder = this;
		customShape = new ArrayList<>();
		noCollision = false;
		notSolid = false;
		randomTickCallback = null;
		lootTable = null;
		blockstateJson = null;
		modelJson = null;
		noValidSpawns = false;
		suffocating = true;
		viewBlocking = true;
		redstoneConductor = true;
		transparent = false;
		blockStateProperties = new HashSet<>();
		defaultStateModification = null;
		placementStateModification = null;
		canBeReplacedFunction = null;
	}

	@Override
	public final RegistryInfo getRegistryType() {
		return RegistryInfo.BLOCK;
	}

	@Override
	public Block transformObject(Block obj) {
		obj.kjs$setBlockBuilder(this);
		return obj;
	}

	@Override
	public void createAdditionalObjects() {
		if (itemBuilder != null) {
			RegistryInfo.ITEM.addBuilder(itemBuilder);
		}
	}

	@Override
	public BuilderBase<Block> displayName(String name) {
		if (itemBuilder != null) {
			itemBuilder.displayName(name);
		}

		return super.displayName(name);
	}

	@Override
	public void generateDataJsons(DataJsonGenerator generator) {
		if (this.lootTable == EMPTY) {
			return;
		}

		var lootBuilder = new LootBuilder(null);
		lootBuilder.type = "minecraft:block";

		if (lootTable != null) {
			lootTable.accept(lootBuilder);
		} else if (get().asItem() != Items.AIR) {
			lootBuilder.addPool(pool -> {
				pool.survivesExplosion();
				pool.addItem(new ItemStack(get()));
			});
		}

		var json = lootBuilder.toJson();
		generator.json(newID("loot_tables/blocks/", ""), json);
	}

	@Override
	public void generateAssetJsons(AssetJsonGenerator generator) {
		if (blockstateJson != null) {
			generator.json(newID("blockstates/", ""), blockstateJson);
		} else {
			generator.blockState(id, this::generateBlockStateJson);
		}

		if (modelJson != null) {
			generator.json(newID("models/", ""), modelJson);
		} else {
			// This is different because there can be multiple models, so we should let the block handle those
			generateBlockModelJsons(generator);
		}

		if (itemBuilder != null) {
			if (itemBuilder.modelJson != null) {
				generator.json(newID("models/item/", ""), itemBuilder.modelJson);
			} else {
				generator.itemModel(itemBuilder.id, this::generateItemModelJson);
			}
		}

	}

	protected void generateItemModelJson(ModelGenerator m) {
		if (!model.isEmpty()) {
			m.parent(model);
		} else {
			m.parent(newID("block/", "").toString());
		}
	}

	protected void generateBlockModelJsons(AssetJsonGenerator generator) {
		generator.blockModel(id, mg -> {
			var particle = textures.get("particle").getAsString();

			if (areAllTexturesEqual(textures, particle)) {
				mg.parent("minecraft:block/cube_all");
				mg.texture("all", particle);
			} else {
				mg.parent("block/cube");
				mg.textures(textures);
			}

			if (!color.isEmpty() || !customShape.isEmpty()) {
				List<AABB> boxes = new ArrayList<>(customShape);

				if (boxes.isEmpty()) {
					boxes.add(new AABB(0D, 0D, 0D, 1D, 1D, 1D));
				}

				for (var box : boxes) {
					mg.element(e -> {
						e.box(box);

						for (var direction : Direction.values()) {
							e.face(direction, face -> {
								face.tex("#" + direction.getSerializedName());
								face.cull();

								if (!color.isEmpty()) {
									face.tintindex(0);
								}
							});
						}
					});
				}
			}
		});
	}

	protected void generateBlockStateJson(VariantBlockStateGenerator bs) {
		bs.simpleVariant("", model.isEmpty() ? (id.getNamespace() + ":block/" + id.getPath()) : model);
	}

	public Map<ResourceLocation, JsonObject> generateBlockModels(BlockBuilder builder) {
		Map<ResourceLocation, JsonObject> map = new HashMap<>();

		if (builder.modelJson != null) {
			map.put(builder.newID("models/block/", ""), builder.modelJson);
		} else {
			var modelJson = new JsonObject();

			var particle = builder.textures.get("particle").getAsString();

			if (areAllTexturesEqual(builder.textures, particle)) {
				modelJson.addProperty("parent", "block/cube_all");
				var textures = new JsonObject();
				textures.addProperty("all", particle);
				modelJson.add("textures", textures);
			} else {
				modelJson.addProperty("parent", "block/cube");
				modelJson.add("textures", builder.textures);
			}

			if (!builder.color.isEmpty()) {
				var cube = new JsonObject();
				var from = new JsonArray();
				from.add(0);
				from.add(0);
				from.add(0);
				cube.add("from", from);
				var to = new JsonArray();
				to.add(16);
				to.add(16);
				to.add(16);
				cube.add("to", to);
				var faces = new JsonObject();

				for (var direction : Direction.values()) {
					var f = new JsonObject();
					f.addProperty("texture", "#" + direction.getSerializedName());
					f.addProperty("cullface", direction.getSerializedName());
					f.addProperty("tintindex", 0);
					faces.add(direction.getSerializedName(), f);
				}

				cube.add("faces", faces);

				var elements = new JsonArray();
				elements.add(cube);
				modelJson.add("elements", elements);
			}

			map.put(builder.newID("models/block/", ""), modelJson);
		}

		return map;
	}

	protected boolean areAllTexturesEqual(JsonObject tex, String t) {
		for (var direction : Direction.values()) {
			if (!tex.get(direction.getSerializedName()).getAsString().equals(t)) {
				return false;
			}
		}

		return true;
	}

	@Override
	public void addResourcePackLocations(String path, List<ResourceLocation> list, PackType packType) {
		if (lootTable != EMPTY && path.equals("loot_tables")) {
			list.add(new ResourceLocation(id.getNamespace(), "loot_tables/blocks/" + id.getPath() + ".json"));
		}
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

	public BlockBuilder color(int index, Color c) {
		color.put(index, c.getArgbJS());
		return this;
	}

	public BlockBuilder textureAll(String tex) {
		for (var direction : Direction.values()) {
			textureSide(direction, tex);
		}

		textures.addProperty("particle", tex);
		return this;
	}

	public BlockBuilder textureSide(Direction direction, String tex) {
		return texture(direction.getSerializedName(), tex);
	}

	public BlockBuilder texture(String id, String tex) {
		textures.addProperty(id, tex);
		return this;
	}

	public BlockBuilder model(String m) {
		model = m;
		if (itemBuilder != null) {
			itemBuilder.parentModel(m);
		}
		return this;
	}

	public BlockBuilder item(@Nullable Consumer<BlockItemBuilder> i) {
		if (i == null) {
			itemBuilder = null;
			lootTable = EMPTY;
		} else {
			i.accept(getOrCreateItemBuilder());
		}

		return this;
	}

	@HideFromJS
	protected BlockItemBuilder getOrCreateItemBuilder() {
		return itemBuilder == null ? (itemBuilder = new BlockItemBuilder(id)) : itemBuilder;
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

	public static VoxelShape createShape(List<AABB> boxes) {
		if (boxes.isEmpty()) {
			return Shapes.block();
		}

		var shape = Shapes.create(boxes.get(0));

		for (var i = 1; i < boxes.size(); i++) {
			shape = Shapes.or(shape, Shapes.create(boxes.get(i)));
		}

		return shape;
	}

	public BlockBuilder noCollision() {
		noCollision = true;
		return this;
	}

	public BlockBuilder notSolid() {
		notSolid = true;
		return this;
	}

	@Deprecated(forRemoval = true)
	public BlockBuilder setWaterlogged(boolean waterlogged) {
		ScriptType.STARTUP.console.warn("\"BlockBuilder.waterlogged\" is a deprecated property! Please use \"BlockBuilder.property(BlockProperties.WATERLOGGED)\" instead.");
		if (waterlogged) {
			property(BlockStateProperties.WATERLOGGED);
		}
		return this;
	}

	@Deprecated(forRemoval = true)
	public boolean getWaterlogged() {
		ScriptType.STARTUP.console.warn("\"BlockBuilder.waterlogged\" is a deprecated property! Please use \"BlockBuilder.property(BlockProperties.WATERLOGGED)\" instead.");
		return canBeWaterlogged();
	}

	public BlockBuilder waterlogged() {
		return property(BlockStateProperties.WATERLOGGED);
	}

	public boolean canBeWaterlogged() {
		return blockStateProperties.contains(BlockStateProperties.WATERLOGGED);
	}

	public BlockBuilder noDrops() {
		lootTable = EMPTY;
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

	@Override
	public BlockBuilder tag(ResourceLocation tag) {
		return tagBoth(tag);
	}

	public BlockBuilder tagBoth(ResourceLocation tag) {
		tagBlock(tag);
		tagItem(tag);
		return this;
	}

	public BlockBuilder tagBlock(ResourceLocation tag) {
		defaultTags.add(tag);
		return this;
	}

	public BlockBuilder tagItem(ResourceLocation tag) {
		itemBuilder.defaultTags.add(tag);
		return this;
	}

	private MaterialColor getStateColor(BlockState state) {
		return material.getMinecraftMaterial().getColor();
	}

	public BlockBuilder defaultState(Consumer<BlockStateModifyCallbackJS> callbackJS) {
		defaultStateModification = callbackJS;
		return this;
	}

	public BlockBuilder placementState(Consumer<BlockStateModifyPlacementCallbackJS> callbackJS) {
		placementStateModification = callbackJS;
		return this;
	}

	public BlockBuilder canBeReplaced(Function<CanBeReplacedCallbackJS, Boolean> callbackJS) {
		canBeReplacedFunction = callbackJS;
		return this;
	}

	public BlockBuilder property(Property<?> property) {
		if (property.getPossibleValues().size() <= 1) {
			throw new IllegalArgumentException(String.format("Block \"%s\" has an illegal Blockstate Property \"%s\" which has <= 1 possible values. (%d possible values)", id, property.getName(), property.getPossibleValues().size()));
		}
		blockStateProperties.add(property);
		return this;
	}

	public Block.Properties createProperties() {
		var properties = new KubeJSBlockProperties(this, material.getMinecraftMaterial(), this::getStateColor);
		properties.sound(material.getSound());

		if (resistance >= 0F) {
			properties.strength(hardness, resistance);
		} else {
			properties.strength(hardness);
		}

		properties.lightLevel(state -> (int) (lightLevel * 15F));

		if (noCollision) {
			properties.noCollission();
		}

		if (notSolid) {
			properties.noOcclusion();
		}

		if (requiresTool) {
			properties.requiresCorrectToolForDrops();
		}

		if (lootTable == EMPTY) {
			properties.noLootTable();
		}

		if (!Float.isNaN(slipperiness)) {
			properties.friction(slipperiness);
		}

		if (!Float.isNaN(speedFactor)) {
			properties.speedFactor(speedFactor);
		}

		if (!Float.isNaN(jumpFactor)) {
			properties.jumpFactor(jumpFactor);
		}

		if (noValidSpawns) {
			properties.isValidSpawn(UtilsJS.cast(ALWAYS_FALSE_STATE_ARG_PREDICATE));
		}

		if (!suffocating) {
			properties.isSuffocating(ALWAYS_FALSE_STATE_PREDICATE);
		}

		if (!viewBlocking) {
			properties.isViewBlocking(ALWAYS_FALSE_STATE_PREDICATE);
		}

		if (!redstoneConductor) {
			properties.isRedstoneConductor(ALWAYS_FALSE_STATE_PREDICATE);
		}

		if (randomTickCallback != null) {
			properties.randomTicks();
		}

		return properties;
	}
}