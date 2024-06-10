package dev.latvian.mods.kubejs.block.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.kubejs.block.BlockBuilder;
import dev.latvian.mods.kubejs.color.Color;
import dev.latvian.mods.kubejs.typings.ReturnsSelf;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ColorRGBA;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;

@ReturnsSelf
public class FallingBlockBuilder extends BlockBuilder {
	private int dustColor = 0xff807c7b;

	public FallingBlockBuilder(ResourceLocation i) {
		super(i);
	}

	@Override
	public Block createObject() {
		var dustRgb = dustColor & 0xffffff;
		var dustAlpha = (dustColor >> 24) & 0xff;
		var dustColor = new ColorRGBA(dustRgb | (dustAlpha << 24));
		return new KubeJSFallingBlock(dustColor, createProperties());
	}

	public FallingBlockBuilder dustColor(Color color) {
		dustColor = color.getArgbJS(); // TODO: Add Color -> ColorRGBA conversion
		return this;
	}

	static class KubeJSFallingBlock extends FallingBlock {
		private final ColorRGBA dustColor;

		private static final MapCodec<KubeJSFallingBlock> CODEC = RecordCodecBuilder.mapCodec(
			(instance) -> instance.group(ColorRGBA.CODEC.fieldOf("falling_dust_color").forGetter(block -> block.dustColor),
				propertiesCodec()).apply(instance, KubeJSFallingBlock::new));

		public KubeJSFallingBlock(ColorRGBA dustColor, Properties p) {
			super(p);
			this.dustColor = dustColor;
		}

		@Override
		public int getDustColor(BlockState state, BlockGetter level, BlockPos pos) {
			return dustColor.rgba();
		}

		@Override
		protected MapCodec<KubeJSFallingBlock> codec() {
			return CODEC;
		}
	}
}
