package dev.latvian.mods.kubejs.block.custom;

import dev.latvian.mods.kubejs.client.ModelGenerator;
import dev.latvian.mods.kubejs.client.VariantBlockStateGenerator;
import dev.latvian.mods.kubejs.generator.KubeAssetGenerator;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.neoforged.neoforge.common.Tags;

@ReturnsSelf
public class FenceGateBlockBuilder extends ShapedBlockBuilder {
	public static final ResourceLocation[] FENCE_GATE_TAGS = {
		BlockTags.FENCE_GATES.location(),
		Tags.Blocks.FENCE_GATES.location()
	};

	private static final ResourceLocation MODEL = ResourceLocation.withDefaultNamespace("block/template_fence_gate");
	private static final ResourceLocation OPEN_MODEL = ResourceLocation.withDefaultNamespace("block/template_fence_gate_open");
	private static final ResourceLocation WALL_MODEL = ResourceLocation.withDefaultNamespace("block/template_fence_gate_wall");
	private static final ResourceLocation OPEN_WALL_MODEL = ResourceLocation.withDefaultNamespace("block/template_fence_gate_wall_open");

	public transient WoodType behaviour;

	public FenceGateBlockBuilder(ResourceLocation i) {
		super(i, "_fence_gate");
		tagBoth(FENCE_GATE_TAGS);
		behaviour = WoodType.OAK;
	}

	// TODO: (maybe) Custom WoodTypes?
	//  same idea as with BlockSetTypes in ButtonBlockBuilder
	public FenceGateBlockBuilder behaviour(WoodType wt) {
		behaviour = wt;
		return this;
	}

	public FenceGateBlockBuilder behaviour(String wt) {
		for (var type : WoodType.values().toList()) {
			if (type.name().equals(wt)) {
				behaviour = type;
				return this;
			}
		}

		return this;
	}

	@Override
	public Block createObject() {
		return new FenceGateBlock(behaviour, createProperties());
	}

	@Override
	protected void generateBlockState(VariantBlockStateGenerator bs) {
		var mod = newID("block/", "");
		var modOpen = newID("block/", "_open");
		var modWall = newID("block/", "_wall");
		var modWallOpen = newID("block/", "_wall_open");

		bs.variant("facing=east,in_wall=false,open=false", v -> v.model(mod).y(270).uvlock());
		bs.variant("facing=east,in_wall=false,open=true", v -> v.model(modOpen).y(270).uvlock());
		bs.variant("facing=east,in_wall=true,open=false", v -> v.model(modWall).y(270).uvlock());
		bs.variant("facing=east,in_wall=true,open=true", v -> v.model(modWallOpen).y(270).uvlock());
		bs.variant("facing=north,in_wall=false,open=false", v -> v.model(mod).y(180).uvlock());
		bs.variant("facing=north,in_wall=false,open=true", v -> v.model(modOpen).y(180).uvlock());
		bs.variant("facing=north,in_wall=true,open=false", v -> v.model(modWall).y(180).uvlock());
		bs.variant("facing=north,in_wall=true,open=true", v -> v.model(modWallOpen).y(180).uvlock());
		bs.variant("facing=south,in_wall=false,open=false", v -> v.model(mod).y(0).uvlock());
		bs.variant("facing=south,in_wall=false,open=true", v -> v.model(modOpen).y(0).uvlock());
		bs.variant("facing=south,in_wall=true,open=false", v -> v.model(modWall).y(0).uvlock());
		bs.variant("facing=south,in_wall=true,open=true", v -> v.model(modWallOpen).y(0).uvlock());
		bs.variant("facing=west,in_wall=false,open=false", v -> v.model(mod).y(90).uvlock());
		bs.variant("facing=west,in_wall=false,open=true", v -> v.model(modOpen).y(90).uvlock());
		bs.variant("facing=west,in_wall=true,open=false", v -> v.model(modWall).y(90).uvlock());
		bs.variant("facing=west,in_wall=true,open=true", v -> v.model(modWallOpen).y(90).uvlock());
	}

	@Override
	protected void generateBlockModels(KubeAssetGenerator generator) {
		generator.blockModel(id, m -> {
			m.parent(MODEL);
			m.texture("texture", baseTexture);
		});

		generator.blockModel(newID("", "_open"), m -> {
			m.parent(OPEN_MODEL);
			m.texture("texture", baseTexture);
		});

		generator.blockModel(newID("", "_wall"), m -> {
			m.parent(WALL_MODEL);
			m.texture("texture", baseTexture);
		});

		generator.blockModel(newID("", "_wall_open"), m -> {
			m.parent(OPEN_WALL_MODEL);
			m.texture("texture", baseTexture);
		});
	}

	@Override
	protected void generateItemModel(ModelGenerator m) {
		m.parent(MODEL);
		m.texture("texture", baseTexture);
	}
}