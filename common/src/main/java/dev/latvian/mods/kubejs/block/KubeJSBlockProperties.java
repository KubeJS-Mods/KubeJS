package dev.latvian.mods.kubejs.block;

import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

import java.util.function.Function;

public class KubeJSBlockProperties extends BlockBehaviour.Properties {
	public final BlockBuilder blockBuilder;

	public KubeJSBlockProperties(BlockBuilder blockBuilder, Material material, Function<BlockState, MaterialColor> function) {
		super(material, function);
		this.blockBuilder = blockBuilder;
	}
}
