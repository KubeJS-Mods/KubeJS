package dev.latvian.mods.kubejs.block.custom;

import dev.latvian.mods.kubejs.client.ModelGenerator;
import dev.latvian.mods.kubejs.client.VariantBlockStateGenerator;
import dev.latvian.mods.kubejs.generator.AssetJsonGenerator;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.state.properties.BlockSetType;

@ReturnsSelf
public class ButtonBlockBuilder extends ShapedBlockBuilder {
	public static final ResourceLocation[] BUTTON_TAGS = {
		BlockTags.BUTTONS.location(),
	};

	public transient BlockSetType behaviour;
	public transient int ticksToStayPressed;

	public ButtonBlockBuilder(ResourceLocation i) {
		super(i, "_button");
		noCollision();
		tagBoth(BUTTON_TAGS);
		// tagBoth(BlockTags.WOODEN_BUTTONS.location());
		behaviour = BlockSetType.OAK;
		ticksToStayPressed = 30;
	}

	public ButtonBlockBuilder behaviour(BlockSetType wt) {
		behaviour = wt;
		return this;
	}

	public ButtonBlockBuilder ticksToStayPressed(int t) {
		ticksToStayPressed = t;
		return this;
	}

	@Override
	public Block createObject() {
		return new ButtonBlock(behaviour, ticksToStayPressed, createProperties());
	}

	@Override
	protected void generateBlockStateJson(VariantBlockStateGenerator bs) {
		var mod0 = newID("block/", "").toString();
		var mod1 = newID("block/", "_pressed").toString();

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
	}

	@Override
	protected void generateBlockModelJsons(AssetJsonGenerator generator) {
		var texture = textures.get("texture").getAsString();

		generator.blockModel(id, m -> {
			m.parent("minecraft:block/button");
			m.texture("texture", texture);
		});

		generator.blockModel(newID("", "_pressed"), m -> {
			m.parent("minecraft:block/button_pressed");
			m.texture("texture", texture);
		});
	}

	@Override
	protected void generateItemModelJson(ModelGenerator m) {
		m.parent("minecraft:block/button_inventory");
		m.texture("texture", textures.get("texture").getAsString());
	}
}