package dev.latvian.kubejs.core;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;

import java.util.Collections;
import java.util.List;

/**
 * @author LatvianModder
 */
public interface BlockKJS
{
	void setMaterialKJS(Material v);

	void setHasCollisionKJS(boolean v);

	void setExplosionResistanceKJS(float v);

	void setIsRandomlyTickingKJS(boolean v);

	void setSoundTypeKJS(SoundType v);

	void setFrictionKJS(float v);

	void setSpeedFactorKJS(float v);

	void setJumpFactorKJS(float v);

	default List<BlockState> getBlockStatesKJS()
	{
		if (!(this instanceof Block))
		{
			return Collections.emptyList();
		}

		return ((Block) this).getStateDefinition().getPossibleStates();
	}
}
