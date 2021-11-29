package dev.latvian.mods.kubejs.block.custom;

import dev.latvian.mods.kubejs.block.BlockBuilder;
import dev.latvian.mods.kubejs.generator.AssetJsonGenerator;
import dev.architectury.platform.Platform;
import net.minecraft.world.level.block.Block;

import java.util.function.Function;

public class ShapedBlockType extends BlockType {
	public static final ShapedBlockType SLAB = new ShapedBlockType("slab", SlabBlockJS::new, "_slab");
	public static final ShapedBlockType STAIRS = new ShapedBlockType("stairs", StairBlockJS::new, "_stairs");
	public static final ShapedBlockType FENCE = new ShapedBlockType("fence", FenceBlockJS::new, "_fence");
	public static final ShapedBlockType FENCE_GATE = new ShapedBlockType("fence_gate", FenceGateBlockJS::new, "_fence_gate");
	public static final ShapedBlockType WALL = new ShapedBlockType("wall", WallBlockJS::new, "_wall");
	public static final ShapedBlockType WOODEN_PRESSURE_PLATE = new ShapedBlockType("wooden_pressure_plate", WoodenPressurePlateBlockJS::new, "_wooden_pressure_plate", "_pressure_plate");
	public static final ShapedBlockType STONE_PRESSURE_PLATE = new ShapedBlockType("stone_pressure_plate", StonePressurePlateBlockJS::new, "_stone_pressure_plate", "_pressure_plate");
	public static final ShapedBlockType WOODEN_BUTTON = new ShapedBlockType("wooden_button", WoodenButtonBlockJS::new, "_wooden_button", "_button");
	public static final ShapedBlockType STONE_BUTTON = new ShapedBlockType("stone_button", StoneButtonBlockJS::new, "_stone_button", "_button");

	private final Function<Block.Properties, Block> factory;
	private final String[] suffixes;

	public ShapedBlockType(String s, Function<Block.Properties, Block> f, String... su) {
		super(s);
		factory = f;
		suffixes = su;
	}

	@Override
	public Block createBlock(BlockBuilder builder) {
		return factory.apply(builder.createProperties());
	}

	@Override
	public void applyDefaults(BlockBuilder builder) {
		builder.notSolid();
		builder.waterlogged();
		builder.texture("texture", "kubejs:block/detector");

		for (String s : suffixes) {
			if (builder.id.getPath().endsWith(s)) {
				builder.texture("texture", builder.id.getNamespace() + ":block/" + builder.id.getPath().substring(0, builder.id.getPath().length() - s.length()));
				break;
			}
		}

		if (this == SLAB) {
			builder.tagBlockAndItem("minecraft:slabs");
		} else if (this == STAIRS) {
			builder.tagBlockAndItem("minecraft:stairs");
		} else if (this == FENCE) {
			builder.tagBlockAndItem("minecraft:fences");

			if (Platform.isForge()) {
				builder.tagBlockAndItem("forge:fences");
			}
		} else if (this == FENCE_GATE) {
			builder.tagBlockAndItem("minecraft:fence_gates");

			if (Platform.isForge()) {
				builder.tagBlockAndItem("forge:fence_gates");
			}
		} else if (this == WALL) {
			builder.tagBlockAndItem("minecraft:walls");
		} else if (this == WOODEN_PRESSURE_PLATE) {
			builder.noCollission();
			builder.tagBlockAndItem("minecraft:pressure_plates");
			builder.tagBlockAndItem("minecraft:wooden_pressure_plates");
		} else if (this == STONE_PRESSURE_PLATE) {
			builder.noCollission();
			builder.tagBlockAndItem("minecraft:pressure_plates");
			builder.tagBlockAndItem("minecraft:stone_pressure_plates");
		} else if (this == WOODEN_BUTTON) {
			builder.noCollission();
			builder.tagBlockAndItem("minecraft:buttons");
			builder.tagBlockAndItem("minecraft:wooden_buttons");
		} else if (this == STONE_BUTTON) {
			builder.noCollission();
			builder.tagBlockAndItem("minecraft:buttons");
		}
	}

	@Override
	public void generateAssets(BlockBuilder builder, AssetJsonGenerator generator) {
		if (builder.block instanceof CustomBlockJS) {
			((CustomBlockJS) builder.block).generateAssets(builder, generator);
		} else {
			super.generateAssets(builder, generator);
		}
	}
}
