package dev.latvian.kubejs.block.custom;

import dev.latvian.kubejs.block.BlockBuilder;
import dev.latvian.kubejs.generator.AssetJsonGenerator;
import net.minecraft.world.level.block.FenceGateBlock;

public class FenceGateBlockJS extends FenceGateBlock implements CustomBlockJS {
	public FenceGateBlockJS(Properties properties) {
		super(properties);
	}

	@Override
	public void generateAssets(BlockBuilder builder, AssetJsonGenerator generator) {
		generator.blockState(builder.id, bs -> {
			String mod = builder.newID("block/", "").toString();
			String modOpen = builder.newID("block/", "_open").toString();
			String modWall = builder.newID("block/", "_wall").toString();
			String modWallOpen = builder.newID("block/", "_wall_open").toString();

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
		});

		final String texture = builder.textures.get("texture").getAsString();

		generator.blockModel(builder.id, m -> {
			m.parent("minecraft:block/template_fence_gate");
			m.texture("texture", texture);
		});

		generator.blockModel(builder.newID("", "_open"), m -> {
			m.parent("minecraft:block/template_fence_gate_open");
			m.texture("texture", texture);
		});

		generator.blockModel(builder.newID("", "_wall"), m -> {
			m.parent("minecraft:block/template_fence_gate_wall");
			m.texture("texture", texture);
		});

		generator.blockModel(builder.newID("", "_wall_open"), m -> {
			m.parent("minecraft:block/template_fence_gate_wall_open");
			m.texture("texture", texture);
		});

		generator.itemModel(builder.itemBuilder.id, m -> {
			m.parent("minecraft:block/template_fence_gate");
			m.texture("texture", texture);
		});
	}
}
