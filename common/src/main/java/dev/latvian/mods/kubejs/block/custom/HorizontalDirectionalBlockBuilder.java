package dev.latvian.mods.kubejs.block.custom;

import dev.latvian.mods.kubejs.block.BlockBuilder;
import dev.latvian.mods.kubejs.client.ModelGenerator;
import dev.latvian.mods.kubejs.client.VariantBlockStateGenerator;
import dev.latvian.mods.kubejs.generator.AssetJsonGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.NotNull;

public class HorizontalDirectionalBlockBuilder extends BlockBuilder {

	// Cardinal blocks that can face any horizontal direction (NSEW).

	public HorizontalDirectionalBlockBuilder(ResourceLocation i) {
		super(i);
	}

	@Override
	protected void generateBlockStateJson(VariantBlockStateGenerator bs) {
		var modelLocation = model.isEmpty() ? newID("block/", "").toString() : model;
		bs.variant("facing=north", v -> v.model(modelLocation));
		bs.variant("facing=east", v -> v.model(modelLocation).y(90));
		bs.variant("facing=south", v -> v.model(modelLocation).y(180));
		bs.variant("facing=west", v -> v.model(modelLocation).y(270));
	}

	@Override
	protected void generateBlockModelJsons(AssetJsonGenerator gen) {
		gen.blockModel(id, mg -> {
			var side = getTextureOrDefault("side", newID("block/", "").toString());

			mg.texture("side", side);
			mg.texture("front", getTextureOrDefault("front", newID("block/", "_front").toString()));
			mg.texture("particle", getTextureOrDefault("particle", side));
			mg.texture("top", getTextureOrDefault("top", side));

			if (textures.has("bottom")) {
				mg.parent("block/orientable_with_bottom");
				mg.texture("bottom", textures.get("bottom").getAsString());
			} else {
				mg.parent("minecraft:block/orientable");
			}
		});
	}

	@Override
	protected void generateItemModelJson(ModelGenerator m) {
		m.parent(model.isEmpty() ? newID("block/", "").toString() : model);
	}

	@Override
	public HorizontalDirectionalBlockBuilder textureAll(String tex) {
		super.textureAll(tex);
		texture("side", tex);
		return this;
	}

	private String getTextureOrDefault(String name, String defaultTexture) {
		return textures.has(name) ? textures.get(name).getAsString() : defaultTexture;
	}

	@Override
	public Block createObject() {
		return new HorizontalDirectionalBlockJS(this);
	}

	public static class HorizontalDirectionalBlockJS extends BasicBlockJS {

		public static DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

		public HorizontalDirectionalBlockJS(BlockBuilder p) {
			super(p);
		}

		@Override
		protected void createBlockStateDefinition(@NotNull StateDefinition.Builder<Block, BlockState> builder) {
			builder.add(FACING);
			super.createBlockStateDefinition(builder);
		}

		@Override
		public BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
			var state = defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());

			if (blockBuilder.canBeWaterlogged()) {
				state = state.setValue(BlockStateProperties.WATERLOGGED, context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER);
			}

			return state;

		}

	}
}
