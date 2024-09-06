package dev.latvian.mods.kubejs.block.custom;

import dev.latvian.mods.kubejs.block.BlockRenderType;
import dev.latvian.mods.kubejs.block.drop.BlockDrops;
import dev.latvian.mods.kubejs.client.ModelGenerator;
import dev.latvian.mods.kubejs.client.VariantBlockStateGenerator;
import dev.latvian.mods.kubejs.generator.KubeAssetGenerator;
import dev.latvian.mods.kubejs.util.ID;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.SetComponentsFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.util.List;
import java.util.Map;

@ReturnsSelf
public class DoorBlockBuilder extends ShapedBlockBuilder {
	public static final ResourceLocation[] TRAPDOOR_TAGS = {
		BlockTags.TRAPDOORS.location(),
	};

	public transient BlockSetType behaviour;

	public DoorBlockBuilder(ResourceLocation i) {
		super(i);
		renderType(BlockRenderType.CUTOUT);
		noValidSpawns(true);
		notSolid();
		tagBoth(TRAPDOOR_TAGS);
		textures.put("top", newID("block/", "_top").toString());
		textures.put("bottom", newID("block/", "_bottom").toString());
		hardness(3F);
		behaviour = BlockSetType.OAK;
	}

	public DoorBlockBuilder behaviour(BlockSetType wt) {
		behaviour = wt;
		return this;
	}

	@Override
	public Block createObject() {
		return new DoorBlock(behaviour, createProperties());
	}

	@Override
	protected void generateBlockState(VariantBlockStateGenerator bs) {
		var modelMap = Map.of(
			DoubleBlockHalf.UPPER, Map.of(
				DoorHingeSide.RIGHT, Map.of(
					Boolean.FALSE, newID("block/", "_top_right"),
					Boolean.TRUE, newID("block/", "_top_right_open")
				),
				DoorHingeSide.LEFT, Map.of(
					Boolean.FALSE, newID("block/", "_top_left"),
					Boolean.TRUE, newID("block/", "_top_left_open")
				)
			),
			DoubleBlockHalf.LOWER, Map.of(
				DoorHingeSide.RIGHT, Map.of(
					Boolean.FALSE, newID("block/", "_bottom_right"),
					Boolean.TRUE, newID("block/", "_bottom_right_open")
				),
				DoorHingeSide.LEFT, Map.of(
					Boolean.FALSE, newID("block/", "_bottom_left"),
					Boolean.TRUE, newID("block/", "_bottom_left_open")
				)
			)
		);

		var rotationMap = Map.of(
			Direction.EAST, Map.of(
				DoorHingeSide.RIGHT, Map.of(
					Boolean.FALSE, 0,
					Boolean.TRUE, 270
				),
				DoorHingeSide.LEFT, Map.of(
					Boolean.FALSE, 0,
					Boolean.TRUE, 90
				)
			),
			Direction.NORTH, Map.of(
				DoorHingeSide.RIGHT, Map.of(
					Boolean.FALSE, 270,
					Boolean.TRUE, 180
				),
				DoorHingeSide.LEFT, Map.of(
					Boolean.FALSE, 270,
					Boolean.TRUE, 0
				)
			),
			Direction.SOUTH, Map.of(
				DoorHingeSide.RIGHT, Map.of(
					Boolean.FALSE, 90,
					Boolean.TRUE, 0
				),
				DoorHingeSide.LEFT, Map.of(
					Boolean.FALSE, 90,
					Boolean.TRUE, 180
				)
			),
			Direction.WEST, Map.of(
				DoorHingeSide.RIGHT, Map.of(
					Boolean.FALSE, 180,
					Boolean.TRUE, 90
				),
				DoorHingeSide.LEFT, Map.of(
					Boolean.FALSE, 180,
					Boolean.TRUE, 270
				)
			)
		);

		var halfValues = DoubleBlockHalf.values();
		var openValues = List.of(Boolean.TRUE, Boolean.FALSE);
		var facingValues = BlockStateProperties.HORIZONTAL_FACING.getPossibleValues();
		var hingeValues = DoorHingeSide.values();

		for (var half : halfValues) {
			for (var open : openValues) {
				for (var facing : facingValues) {
					for (var hinge : hingeValues) {
						bs.variant("facing=" + facing.getSerializedName() + ",half=" + half.getSerializedName() + ",hinge=" + hinge.getSerializedName() + ",open=" + open, v -> {
							v.model(modelMap.get(half).get(hinge).get(open)).y(rotationMap.get(facing).get(hinge).get(open));
						});
					}
				}
			}
		}
	}

	@Override
	protected void generateBlockModels(KubeAssetGenerator generator) {
		var topTexture = textures.get("top");
		var bottomTexture = textures.get("bottom");

		for (var type : List.of(
			"top_right",
			"top_right_open",
			"top_left",
			"top_left_open",
			"bottom_right",
			"bottom_right_open",
			"bottom_left",
			"bottom_left_open"
		)) {
			generator.blockModel(newID("", "_" + type), m -> {
				m.parent("minecraft:block/door_" + type);
				m.texture("top", topTexture);
				m.texture("bottom", bottomTexture);
			});
		}
	}

	@Override
	public LootTable generateLootTable() {
		var blockDrops = drops == null ? BlockDrops.createDefault(get().asItem().getDefaultInstance()) : drops.get();

		if (blockDrops.items().length == 0) {
			return null;
		}

		var pool = new LootPool.Builder();

		if (blockDrops.rolls() != null) {
			pool.setRolls(blockDrops.rolls());
		}

		pool.when(ExplosionCondition.survivesExplosion());

		for (var drop : blockDrops.items()) {
			var item = LootItem.lootTableItem(drop.getItem());

			item.when(new LootItemBlockStatePropertyCondition.Builder(get()).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(DoorBlock.HALF, DoubleBlockHalf.LOWER)));

			if (drop.getCount() > 1) {
				item.apply(SetItemCountFunction.setCount(ConstantValue.exactly(drop.getCount())));
			}

			if (!drop.isComponentsPatchEmpty()) {
				item.apply(LootItemConditionalFunction.simpleBuilder(c -> new SetComponentsFunction(c, drop.getComponentsPatch())));
			}

			pool.add(item);
		}

		return new LootTable.Builder().withPool(pool).build();
	}

	@Override
	protected void generateItemModel(ModelGenerator m) {
		m.parent(KubeAssetGenerator.GENERATED_ITEM_MODEL);
		m.texture("layer0", id.withPath(ID.ITEM).toString());
	}
}