package dev.latvian.mods.kubejs.block.custom;

import dev.latvian.mods.kubejs.client.VariantBlockStateGenerator;
import dev.latvian.mods.kubejs.generator.KubeAssetGenerator;
import dev.latvian.mods.kubejs.util.ID;
import dev.latvian.mods.kubejs.util.TickDuration;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.state.properties.BlockSetType;

@ReturnsSelf
public class PressurePlateBlockBuilder extends ShapedBlockBuilder implements ButtonOrPressurePlateBuilder {
	public static final Identifier[] PRESSURE_PLATE_TAGS = {
		BlockTags.PRESSURE_PLATES.location(),
	};

	private static final Identifier MODEL = Identifier.withDefaultNamespace("block/pressure_plate_up");
	private static final Identifier PRESSED_MODEL = Identifier.withDefaultNamespace("block/pressure_plate_down");

	private static class KubePressurePlateBlock extends PressurePlateBlock {
		private final int pressedTime;

		public KubePressurePlateBlock(BlockSetType type, int pressedTime, Properties properties) {
			super(type, properties);
			this.pressedTime = pressedTime;
		}

		@Override
		protected int getPressedTime() {
			return pressedTime;
		}
	}

	public transient BlockSetType behaviour;
	public transient int ticksToStayPressed;

	public PressurePlateBlockBuilder(Identifier i) {
		super(i, "_pressure_plate");
		noCollision();
		tagBoth(PRESSURE_PLATE_TAGS);
		// tagBoth(BlockTags.WOODEN_PRESSURE_PLATES.identifier());
		behaviour = BlockSetType.OAK;
		ticksToStayPressed = 20;
	}

	@Override
	public PressurePlateBlockBuilder behaviour(BlockSetType behaviour) {
		this.behaviour = behaviour;
		return this;
	}

	@Override
	public PressurePlateBlockBuilder ticksToStayPressed(TickDuration ticks) {
		this.ticksToStayPressed = ticks.intTicks();
		return this;
	}

	@Override
	public Block createObject() {
		return new KubePressurePlateBlock(behaviour, ticksToStayPressed, createProperties());
	}

	@Override
	protected void generateBlockState(VariantBlockStateGenerator bs) {
		bs.variant("powered=false", v -> v.model(id.withPath(ID.BLOCK)));
		bs.variant("powered=true", v -> v.model(newID("block/", "_down")));
	}

	@Override
	protected void generateBlockModels(KubeAssetGenerator generator) {
		generator.blockModel(id, m -> {
			m.parent(MODEL);
			m.texture("texture", baseTexture);
		});

		generator.blockModel(newID("", "_down"), m -> {
			m.parent(PRESSED_MODEL);
			m.texture("texture", baseTexture);
		});
	}
}