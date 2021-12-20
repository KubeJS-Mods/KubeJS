package dev.latvian.mods.kubejs.block.custom;

import dev.latvian.mods.kubejs.block.BlockBuilder;
import dev.latvian.mods.kubejs.generator.AssetJsonGenerator;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StairBlock;

public class StairBlockJS extends StairBlock implements CustomBlockJS {
	public StairBlockJS(Properties properties) {
		super(Blocks.OAK_PLANKS.defaultBlockState(), properties);
	}

	@Override
	public void generateAssets(BlockBuilder builder, AssetJsonGenerator generator) {
		generator.blockState(builder.id, bs -> {
			var mod = builder.newID("block/", "").toString();
			var modInner = builder.newID("block/", "_inner").toString();
			var modOuter = builder.newID("block/", "_outer").toString();

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
		});

		final var texture = builder.textures.get("texture").getAsString();

		generator.blockModel(builder.id, m -> {
			m.parent("minecraft:block/stairs");
			m.texture("bottom", texture);
			m.texture("top", texture);
			m.texture("side", texture);
		});

		generator.blockModel(builder.newID("", "_inner"), m -> {
			m.parent("minecraft:block/inner_stairs");
			m.texture("bottom", texture);
			m.texture("top", texture);
			m.texture("side", texture);
		});

		generator.blockModel(builder.newID("", "_outer"), m -> {
			m.parent("minecraft:block/outer_stairs");
			m.texture("bottom", texture);
			m.texture("top", texture);
			m.texture("side", texture);
		});

		generator.itemModel(builder.itemBuilder.id, m -> m.parent(builder.newID("block/", "").toString()));
	}
}
