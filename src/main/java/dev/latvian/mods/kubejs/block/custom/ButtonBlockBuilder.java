package dev.latvian.mods.kubejs.block.custom;

import dev.latvian.mods.kubejs.client.ModelGenerator;
import dev.latvian.mods.kubejs.client.VariantBlockStateGenerator;
import dev.latvian.mods.kubejs.generator.AssetJsonGenerator;
import dev.latvian.mods.kubejs.typings.ReturnsSelf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.state.properties.BlockSetType;

@ReturnsSelf
public class ButtonBlockBuilder extends ShapedBlockBuilder {
	public transient BlockSetType behaviour;
	public transient int ticksToStayPressed;
	public transient boolean arrowsCanPress;

	public ButtonBlockBuilder(ResourceLocation i) {
		super(i, "_button");
		noCollision();
		tagBoth(BlockTags.BUTTONS.location());
		// tagBoth(BlockTags.WOODEN_BUTTONS.location());
		behaviour = BlockSetType.OAK;
		ticksToStayPressed = 30;
		arrowsCanPress = true;
	}

	public ButtonBlockBuilder behaviour(BlockSetType wt) {
		behaviour = wt;
		return this;
	}

	public ButtonBlockBuilder behaviour(String wt) {
		for (var type : BlockSetType.values().toList()) {
			if (type.name().equals(wt)) {
				behaviour = type;
				return this;
			}
		}

		return this;
	}

	public ButtonBlockBuilder ticksToStayPressed(int t) {
		ticksToStayPressed = t;
		return this;
	}

	// TODO: this is now determined by the BlockSetType
	public ButtonBlockBuilder arrowsCanPress(boolean b) {
		arrowsCanPress = b;
		return this;
	}

	@Override
	public Block createObject() {
		// TODO: (maybe) Custom BlockSetTypes?
		//  instead of all of these methods above, we could just have a single method
		//  that can take either a string and return an already registered BlockSetType
		//  or create a BlockSetType using a function like (typeBuilder) => {}
		return new ButtonBlock(BlockSetType.OAK, ticksToStayPressed, createProperties());
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