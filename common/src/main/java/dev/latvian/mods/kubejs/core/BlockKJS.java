package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.block.BlockBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import org.apache.commons.lang3.NotImplementedException;

import java.util.Collections;
import java.util.List;

/**
 * @author LatvianModder
 */
public interface BlockKJS extends BlockBuilderProvider {
	default void setBlockBuilderKJS(BlockBuilder b) {
		throw new NotImplementedException("A mixin should have implemented this method!");
	}

	default CompoundTag getTypeDataKJS() {
		throw new NotImplementedException("A mixin should have implemented this method!");
	}

	default void setMaterialKJS(Material v) {
		throw new NotImplementedException("A mixin should have implemented this method!");
	}

	default void setHasCollisionKJS(boolean v) {
		throw new NotImplementedException("A mixin should have implemented this method!");
	}

	default void setExplosionResistanceKJS(float v) {
		throw new NotImplementedException("A mixin should have implemented this method!");
	}

	default void setIsRandomlyTickingKJS(boolean v) {
		throw new NotImplementedException("A mixin should have implemented this method!");
	}

	default void setSoundTypeKJS(SoundType v) {
		throw new NotImplementedException("A mixin should have implemented this method!");
	}

	default void setFrictionKJS(float v) {
		throw new NotImplementedException("A mixin should have implemented this method!");
	}

	default void setSpeedFactorKJS(float v) {
		throw new NotImplementedException("A mixin should have implemented this method!");
	}

	default void setJumpFactorKJS(float v) {
		throw new NotImplementedException("A mixin should have implemented this method!");
	}

	default List<BlockState> getBlockStatesKJS() {
		return this instanceof Block block ? block.getStateDefinition().getPossibleStates() : Collections.emptyList();
	}
}
