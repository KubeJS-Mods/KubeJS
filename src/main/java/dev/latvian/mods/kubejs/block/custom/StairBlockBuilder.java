package dev.latvian.mods.kubejs.block.custom;

import dev.latvian.mods.kubejs.client.VariantBlockStateGenerator;
import dev.latvian.mods.kubejs.generator.KubeAssetGenerator;
import dev.latvian.mods.kubejs.util.ID;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StairBlock;

public class StairBlockBuilder extends ShapedBlockBuilder {
	public static final ResourceLocation[] STAIR_TAGS = {
		BlockTags.STAIRS.location(),
	};

	private static final ResourceLocation MODEL = ResourceLocation.withDefaultNamespace("block/stairs");
	private static final ResourceLocation INNER_MODEL = ResourceLocation.withDefaultNamespace("block/inner_stairs");
	private static final ResourceLocation OUTER_MODEL = ResourceLocation.withDefaultNamespace("block/outer_stairs");

	public StairBlockBuilder(ResourceLocation i) {
		super(i, "_stairs");
		tagBoth(STAIR_TAGS);
	}

	@Override
	public Block createObject() {
		return new StairBlock(Blocks.OAK_PLANKS.defaultBlockState(), createProperties());
	}

	@Override
	protected void generateBlockState(VariantBlockStateGenerator bs) {
		var mod = id.withPath(ID.BLOCK);
		var modInner = newID("block/", "_inner");
		var modOuter = newID("block/", "_outer");

		bs.variant("facing=east,half=bottom,shape=inner_left", v -> v.model(modInner).y(270).uvlock());
		bs.variant("facing=east,half=bottom,shape=inner_right", v -> v.model(modInner));
		bs.variant("facing=east,half=bottom,shape=outer_left", v -> v.model(modOuter).y(270).uvlock());
		bs.variant("facing=east,half=bottom,shape=outer_right", v -> v.model(modOuter));
		bs.variant("facing=east,half=bottom,shape=straight", v -> v.model(mod));
		bs.variant("facing=east,half=top,shape=inner_left", v -> v.model(modInner).x(180).uvlock());
		bs.variant("facing=east,half=top,shape=inner_right", v -> v.model(modInner).x(180).y(90).uvlock());
		bs.variant("facing=east,half=top,shape=outer_left", v -> v.model(modOuter).x(180).uvlock());
		bs.variant("facing=east,half=top,shape=outer_right", v -> v.model(modOuter).x(180).y(90).uvlock());
		bs.variant("facing=east,half=top,shape=straight", v -> v.model(mod).x(180).uvlock());
		bs.variant("facing=north,half=bottom,shape=inner_left", v -> v.model(modInner).y(180).uvlock());
		bs.variant("facing=north,half=bottom,shape=inner_right", v -> v.model(modInner).y(270).uvlock());
		bs.variant("facing=north,half=bottom,shape=outer_left", v -> v.model(modOuter).y(180).uvlock());
		bs.variant("facing=north,half=bottom,shape=outer_right", v -> v.model(modOuter).y(270).uvlock());
		bs.variant("facing=north,half=bottom,shape=straight", v -> v.model(mod).y(270).uvlock());
		bs.variant("facing=north,half=top,shape=inner_left", v -> v.model(modInner).x(180).y(270).uvlock());
		bs.variant("facing=north,half=top,shape=inner_right", v -> v.model(modInner).x(180).uvlock());
		bs.variant("facing=north,half=top,shape=outer_left", v -> v.model(modOuter).x(180).y(270).uvlock());
		bs.variant("facing=north,half=top,shape=outer_right", v -> v.model(modOuter).x(180).uvlock());
		bs.variant("facing=north,half=top,shape=straight", v -> v.model(mod).x(180).y(270).uvlock());
		bs.variant("facing=south,half=bottom,shape=inner_left", v -> v.model(modInner));
		bs.variant("facing=south,half=bottom,shape=inner_right", v -> v.model(modInner).y(90).uvlock());
		bs.variant("facing=south,half=bottom,shape=outer_left", v -> v.model(modOuter));
		bs.variant("facing=south,half=bottom,shape=outer_right", v -> v.model(modOuter).y(90).uvlock());
		bs.variant("facing=south,half=bottom,shape=straight", v -> v.model(mod).y(90).uvlock());
		bs.variant("facing=south,half=top,shape=inner_left", v -> v.model(modInner).x(180).y(90).uvlock());
		bs.variant("facing=south,half=top,shape=inner_right", v -> v.model(modInner).x(180).y(180).uvlock());
		bs.variant("facing=south,half=top,shape=outer_left", v -> v.model(modOuter).x(180).y(90).uvlock());
		bs.variant("facing=south,half=top,shape=outer_right", v -> v.model(modOuter).x(180).y(180).uvlock());
		bs.variant("facing=south,half=top,shape=straight", v -> v.model(mod).x(180).y(90).uvlock());
		bs.variant("facing=west,half=bottom,shape=inner_left", v -> v.model(modInner).y(90).uvlock());
		bs.variant("facing=west,half=bottom,shape=inner_right", v -> v.model(modInner).y(180).uvlock());
		bs.variant("facing=west,half=bottom,shape=outer_left", v -> v.model(modOuter).y(90).uvlock());
		bs.variant("facing=west,half=bottom,shape=outer_right", v -> v.model(modOuter).y(180).uvlock());
		bs.variant("facing=west,half=bottom,shape=straight", v -> v.model(mod).y(180).uvlock());
		bs.variant("facing=west,half=top,shape=inner_left", v -> v.model(modInner).x(180).y(180).uvlock());
		bs.variant("facing=west,half=top,shape=inner_right", v -> v.model(modInner).x(180).y(270).uvlock());
		bs.variant("facing=west,half=top,shape=outer_left", v -> v.model(modOuter).x(180).y(180).uvlock());
		bs.variant("facing=west,half=top,shape=outer_right", v -> v.model(modOuter).x(180).y(270).uvlock());
		bs.variant("facing=west,half=top,shape=straight", v -> v.model(mod).x(180).y(180).uvlock());
	}

	@Override
	protected void generateBlockModels(KubeAssetGenerator generator) {
		generator.blockModel(id, m -> {
			m.parent(MODEL);
			m.texture("bottom", baseTexture);
			m.texture("top", baseTexture);
			m.texture("side", baseTexture);
		});

		generator.blockModel(newID("", "_inner"), m -> {
			m.parent(INNER_MODEL);
			m.texture("bottom", baseTexture);
			m.texture("top", baseTexture);
			m.texture("side", baseTexture);
		});

		generator.blockModel(newID("", "_outer"), m -> {
			m.parent(OUTER_MODEL);
			m.texture("bottom", baseTexture);
			m.texture("top", baseTexture);
			m.texture("side", baseTexture);
		});
	}
}
