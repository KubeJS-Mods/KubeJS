package dev.latvian.mods.kubejs.web.local.client;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;

public class FakeClientWorld implements BlockAndTintGetter {
	public final LevelReader parent;
	public final BlockState blockState;
	public final Biome biome;

	public FakeClientWorld(LevelReader parent, BlockState blockState, ResourceKey<Biome> biome) {
		this.parent = parent;
		this.blockState = blockState;
		this.biome = parent.registryAccess().registryOrThrow(Registries.BIOME).get(biome);
	}

	@Override
	public float getShade(Direction direction, boolean shade) {
		return parent.getShade(direction, shade);
	}

	@Override
	public LevelLightEngine getLightEngine() {
		return parent.getLightEngine();
	}

	@Override
	public int getBlockTint(BlockPos pos, ColorResolver colorResolver) {
		return colorResolver.getColor(biome, 0D, 0D);
	}

	@Override
	@Nullable
	public BlockEntity getBlockEntity(BlockPos pos) {
		return null;
	}

	@Override
	public BlockState getBlockState(BlockPos pos) {
		return pos.equals(BlockPos.ZERO) ? blockState : Blocks.AIR.defaultBlockState();
	}

	@Override
	public FluidState getFluidState(BlockPos pos) {
		return pos.equals(BlockPos.ZERO) ? blockState.getFluidState() : Fluids.EMPTY.defaultFluidState();
	}

	@Override
	public int getHeight() {
		return 1;
	}

	@Override
	public int getMinBuildHeight() {
		return 0;
	}
}
