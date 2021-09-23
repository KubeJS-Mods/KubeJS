package dev.latvian.kubejs.block.custom;

import dev.latvian.kubejs.block.BlockBuilder;
import dev.latvian.kubejs.generator.AssetJsonGenerator;
import net.minecraft.world.level.block.StoneButtonBlock;

public class StoneButtonBlockJS extends StoneButtonBlock implements CustomBlockJS {
	public StoneButtonBlockJS(Properties properties) {
		super(properties);
	}

	@Override
	public void generateAssets(BlockBuilder builder, AssetJsonGenerator generator) {
		generator.blockState(builder.id, bs -> {
			String mod0 = builder.newID("block/", "").toString();
			String mod1 = builder.newID("block/", "_pressed").toString();

			bs.variant("face=ceiling,facing=east,powered=false", v -> v.model(mod0).x(180).y(270));
			bs.variant("face=ceiling,facing=east,powered=true", v -> v.model(mod1).x(180).y(270));
			bs.variant("face=ceiling,facing=north,powered=false", v -> v.model(mod0).x(180).y(180));
			bs.variant("face=ceiling,facing=north,powered=true", v -> v.model(mod1).x(180).y(180));
			bs.variant("face=ceiling,facing=south,powered=false", v -> v.model(mod0).x(180));
			bs.variant("face=ceiling,facing=south,powered=true", v -> v.model(mod1).x(180));
			bs.variant("face=ceiling,facing=west,powered=false", v -> v.model(mod0).x(180).y(90));
			bs.variant("face=ceiling,facing=west,powered=true", v -> v.model(mod1).x(180).y(90));
			bs.variant("face=floor,facing=east,powered=false", v -> v.model(mod0).y(90));
			bs.variant("face=floor,facing=east,powered=true", v -> v.model(mod1).y(90));
			bs.variant("face=floor,facing=north,powered=false", v -> v.model(mod0));
			bs.variant("face=floor,facing=north,powered=true", v -> v.model(mod1));
			bs.variant("face=floor,facing=south,powered=false", v -> v.model(mod0).y(180));
			bs.variant("face=floor,facing=south,powered=true", v -> v.model(mod1).y(180));
			bs.variant("face=floor,facing=west,powered=false", v -> v.model(mod0).y(270));
			bs.variant("face=floor,facing=west,powered=true", v -> v.model(mod1).y(270));
			bs.variant("face=wall,facing=east,powered=false", v -> v.model(mod0).x(90).y(90).uvlock());
			bs.variant("face=wall,facing=east,powered=true", v -> v.model(mod1).x(90).y(90).uvlock());
			bs.variant("face=wall,facing=north,powered=false", v -> v.model(mod0).x(90).uvlock());
			bs.variant("face=wall,facing=north,powered=true", v -> v.model(mod1).x(90).uvlock());
			bs.variant("face=wall,facing=south,powered=false", v -> v.model(mod0).x(90).y(180).uvlock());
			bs.variant("face=wall,facing=south,powered=true", v -> v.model(mod1).x(90).y(180).uvlock());
			bs.variant("face=wall,facing=west,powered=false", v -> v.model(mod0).x(90).y(270).uvlock());
			bs.variant("face=wall,facing=west,powered=true", v -> v.model(mod1).x(90).y(270).uvlock());
		});

		final String texture = builder.textures.get("texture").getAsString();

		generator.blockModel(builder.id, m -> {
			m.parent("minecraft:block/button");
			m.texture("texture", texture);
		});

		generator.blockModel(builder.newID("", "_pressed"), m -> {
			m.parent("minecraft:block/button_pressed");
			m.texture("texture", texture);
		});

		generator.itemModel(builder.itemBuilder.id, m -> {
			m.parent("minecraft:block/button_inventory");
			m.texture("texture", texture);
		});
	}
}
