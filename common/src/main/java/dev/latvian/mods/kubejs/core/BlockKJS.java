package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.block.BlockBuilder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;

import java.util.Collections;
import java.util.List;

/**
 * @author LatvianModder
 */
public interface BlockKJS extends BlockBuilderProvider {
	void setBlockBuilderKJS(BlockBuilder b);

	void setMaterialKJS(Material v);

	void setHasCollisionKJS(boolean v);

	void setExplosionResistanceKJS(float v);

	void setIsRandomlyTickingKJS(boolean v);

	void setSoundTypeKJS(SoundType v);

	void setFrictionKJS(float v);

	void setSpeedFactorKJS(float v);

	void setJumpFactorKJS(float v);

	default List<BlockState> getBlockStatesKJS() {
		return this instanceof Block block ? block.getStateDefinition().getPossibleStates() : Collections.emptyList();
	}
}
