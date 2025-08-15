package dev.latvian.mods.kubejs.block.custom;

import dev.latvian.mods.kubejs.client.ModelGenerator;
import dev.latvian.mods.kubejs.client.VariantBlockStateGenerator;
import dev.latvian.mods.kubejs.generator.KubeAssetGenerator;
import dev.latvian.mods.kubejs.util.TickDuration;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.state.properties.BlockSetType;

@ReturnsSelf
public class ButtonBlockBuilder extends ShapedBlockBuilder implements ButtonOrPressurePlateBuilder {
	public static final ResourceLocation[] BUTTON_TAGS = {
		BlockTags.BUTTONS.location(),
	};

	private static final ResourceLocation MODEL = ResourceLocation.withDefaultNamespace("block/button");
	private static final ResourceLocation PRESSED_MODEL = ResourceLocation.withDefaultNamespace("block/button_pressed");
	private static final ResourceLocation INVENTORY_MODEL = ResourceLocation.withDefaultNamespace("block/button_inventory");

	public transient BlockSetType behaviour;
	public transient int ticksToStayPressed;

	public ButtonBlockBuilder(ResourceLocation i) {
		super(i, "_button");
		noCollision();
		tagBoth(BUTTON_TAGS);
		behaviour = BlockSetType.OAK;
		ticksToStayPressed = 30;
	}

	@Override
	public ButtonBlockBuilder behaviour(BlockSetType behaviour) {
		this.behaviour = behaviour;
		return this;
	}

	@Override
	public ButtonBlockBuilder ticksToStayPressed(TickDuration ticks) {
		this.ticksToStayPressed = ticks.intTicks();
		return this;
	}

	@Override
	public Block createObject() {
		return new ButtonBlock(behaviour, ticksToStayPressed, createProperties());
	}

	@Override
	protected void generateBlockState(VariantBlockStateGenerator bs) {
		var mod0 = newID("block/", "");
		var mod1 = newID("block/", "_pressed");

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
	protected void generateBlockModels(KubeAssetGenerator generator) {
		generator.blockModel(id, m -> {
			m.parent(MODEL);
			m.texture("texture", baseTexture);
		});

		generator.blockModel(newID("", "_pressed"), m -> {
			m.parent(PRESSED_MODEL);
			m.texture("texture", baseTexture);
		});
	}

	@Override
	protected void generateItemModel(ModelGenerator m) {
		m.parent(INVENTORY_MODEL);
		m.texture("texture", baseTexture);
	}
}