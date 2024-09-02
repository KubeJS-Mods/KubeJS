package dev.latvian.mods.kubejs.block.custom;

import com.mojang.datafixers.util.Pair;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.block.BlockBuilder;
import dev.latvian.mods.kubejs.block.BlockRenderType;
import dev.latvian.mods.kubejs.block.RandomTickCallbackJS;
import dev.latvian.mods.kubejs.block.SeedItemBuilder;
import dev.latvian.mods.kubejs.client.VariantBlockStateGenerator;
import dev.latvian.mods.kubejs.generator.KubeAssetGenerator;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;

@ReturnsSelf
public class CropBlockBuilder extends BlockBuilder {
	public static final ResourceLocation[] CROP_BLOCK_TAGS = {
		BlockTags.CROPS.location(),
	};

	public static final ResourceLocation[] CROP_ITEM_TAGS = {
		Tags.Items.SEEDS.location(),
	};

	@FunctionalInterface
	public interface SurviveCallback {
		boolean survive(BlockState state, LevelReader reader, BlockPos pos);
	}

	public static class ShapeBuilder {
		private final List<VoxelShape> shapes;

		public ShapeBuilder(int age) {
			this.shapes = new ArrayList<>(Collections.nCopies(age + 1, Shapes.block()));
		}

		@Info("""
			Describe the shape of the crop at a specific age.
			
			min/max coordinates are double values between 0 and 16.
			""")
		public ShapeBuilder shape(int age, double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
			shapes.set(age, Block.box(minX, minY, minZ, maxX, maxY, maxZ));
			return this;
		}

		@Info("Makes the block to have a box like wheat for each stage.")
		public ShapeBuilder wheat() {
			shapes.clear();

			for (int i = 0; i < 8; i++) {
				shapes.add(Block.box(0, 0, 0, 16, 2 + i * 2, 16));
			}

			return this;
		}

		@Info("Makes the block to have a box like carrot for each stage.")
		public ShapeBuilder carrot() {
			shapes.clear();

			for (int i = 0; i < 8; i++) {
				shapes.add(Block.box(0, 0, 0, 16, 2 + i, 16));
			}

			return this;
		}

		@Info("Makes the block to have a box like beetroot for each stage.")
		public ShapeBuilder beetroot() {
			shapes.clear();

			for (int i = 0; i < 4; i++) {
				shapes.add(Block.box(0, 0, 0, 16, 2 + i * 2, 16));
			}

			return this;
		}

		@Info("Makes the block to have a box like potato for each stage.")
		public ShapeBuilder potato() {
			return carrot();
		}

		public List<VoxelShape> getShapes() {
			return List.copyOf(shapes);
		}
	}

	public transient int age;
	protected transient List<VoxelShape> shapeByAge;
	public transient ToDoubleFunction<RandomTickCallbackJS> growSpeedCallback;
	public transient ToIntFunction<RandomTickCallbackJS> fertilizerCallback;
	public transient SurviveCallback surviveCallback;

	public transient List<Pair<Item, NumberProvider>> outputs;

	public CropBlockBuilder(ResourceLocation id) {
		super(id);
		age = 7;
		shapeByAge = Collections.nCopies(8, Shapes.block());
		growSpeedCallback = null;
		fertilizerCallback = null;
		surviveCallback = null;
		renderType = BlockRenderType.CUTOUT;
		noCollision = true;
		itemBuilder = new SeedItemBuilder(newID("", "_seeds"));
		((SeedItemBuilder) itemBuilder).blockBuilder = this;
		hardness = 0.0f;
		resistance = 0.0f;
		outputs = new ArrayList<>();
		notSolid = true;

		soundType(SoundType.CROP);
		mapColor(MapColor.PLANT);

		for (int a = 0; a <= age; a++) {
			texture(String.valueOf(a), newID("block/", "/" + a).toString());
		}

		tagBlock(CROP_BLOCK_TAGS);
		tagItem(CROP_ITEM_TAGS);
	}

	@Override
	public BlockBuilder noItem() {
		itemBuilder = null;
		return this;
	}

	@Info("Add a crop output with exactly one output.")
	public CropBlockBuilder crop(Item output) {
		crop(output, ConstantValue.exactly(1.0f));
		return this;
	}

	@Info("Add a crop output with a specific amount.")
	public CropBlockBuilder crop(Item output, NumberProvider chance) {
		outputs.add(new Pair<>(output, chance));
		return this;
	}

	@Info("Set the age of the crop. Note that the box will be the same for all ages (A full block size).")
	public CropBlockBuilder age(int age) {
		age(age, (builder) -> {
		});
		return this;
	}

	@Info("Set the age of the crop and the shape of the crop at that age.")
	public CropBlockBuilder age(int age, Consumer<ShapeBuilder> builder) {
		this.age = age;
		ShapeBuilder shapes = new ShapeBuilder(age);
		builder.accept(shapes);
		this.shapeByAge = shapes.getShapes();
		for (int i = 0; i <= age; i++) {
			texture(String.valueOf(i), newID("block/", "/" + i).toString());
		}
		return this;
	}

	@Override
	public BlockBuilder texture(String id, String tex) {
		try {
			int intId = (int) Double.parseDouble(id);
			return super.texture(String.valueOf(intId), tex);
		} catch (NumberFormatException e) {
			return super.texture(id, tex);
		}
	}

	public CropBlockBuilder farmersCanPlant() {
		this.tagItem(new ResourceLocation[]{ResourceLocation.withDefaultNamespace("villager_plantable_seeds")});
		return this;
	}

	public CropBlockBuilder bonemeal(ToIntFunction<RandomTickCallbackJS> bonemealCallback) {
		this.fertilizerCallback = bonemealCallback;
		return this;
	}

	public CropBlockBuilder survive(SurviveCallback surviveCallback) {
		this.surviveCallback = surviveCallback;
		return this;
	}

	public CropBlockBuilder growTick(ToDoubleFunction<RandomTickCallbackJS> growSpeedCallback) {
		this.growSpeedCallback = growSpeedCallback;
		return this;
	}

	@Override
	public BlockBuilder randomTick(@Nullable Consumer<RandomTickCallbackJS> randomTickCallback) {
		KubeJS.LOGGER.warn("randomTick is overridden by growTick to return grow speed, use it instead.");
		return this;
	}

	// FIXME
	// TODO: Get lookup for enchantments here to apply fortune bonus
	@Override
	@Nullable
	public LootTable generateLootTable() {
		var mature = LootItemBlockStatePropertyCondition.hasBlockStateProperties(this.get())
			.setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(CropBlock.AGE, age));

		var builder = LootTable.lootTable();
		for (var output : outputs) {
			var cropItem = LootItem.lootTableItem(output.getFirst())
				.apply(SetItemCountFunction.setCount(output.getSecond()))
				.when(mature);
			builder.withPool(LootPool.lootPool().add(cropItem));
		}

		if (itemBuilder != null) {
			var pool = LootPool.lootPool().add(LootItem.lootTableItem(itemBuilder.get())
				.when(mature)
				.otherwise(LootItem.lootTableItem(itemBuilder.get()))
			);

			builder.withPool(pool);
		}

		return builder.build();
	}


	@Override
	protected void generateBlockStateJson(VariantBlockStateGenerator bs) {
		for (int i = 0; i <= age; i++) {
			bs.simpleVariant("age=" + i, model == null ? id.withPath("block/" + id.getPath() + "/" + i) : model);
		}
	}

	@Override
	protected void generateBlockModelJsons(KubeAssetGenerator generator) {
		for (int i = 0; i <= age; i++) {
			final int fi = i;
			generator.blockModel(newID("", "/" + i), m -> {
				m.parent("minecraft:block/crop");
				m.texture("crop", textures.get(String.valueOf(fi)));
			});
		}

	}

	@Override
	public Block createObject() {
		return new BasicCropBlockJS(this);
	}
}
