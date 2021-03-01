package dev.latvian.kubejs.block;

import dev.latvian.kubejs.core.BlockKJS;
import dev.latvian.kubejs.core.BlockStateKJS;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;

/**
 * @author LatvianModder
 */
public class BlockModificationProperties {
	public final BlockKJS block;

	public BlockModificationProperties(BlockKJS i) {
		block = i;
	}

	public void setMaterial(MaterialJS v) {
		Material m = v.getMinecraftMaterial();

		block.setMaterialKJS(m);

		for (BlockState state : block.getBlockStatesKJS()) {
			if (state instanceof BlockStateKJS) {
				((BlockStateKJS) state).setMaterialKJS(m);
			}
		}
	}

	public void setHasCollision(boolean v) {
		block.setHasCollisionKJS(v);
	}

	public void setDestroySpeed(float v) {
		for (BlockState state : block.getBlockStatesKJS()) {
			if (state instanceof BlockStateKJS) {
				((BlockStateKJS) state).setDestroySpeedKJS(v);
			}
		}
	}

	public void setExplosionResistance(float v) {
		block.setExplosionResistanceKJS(v);
	}

	public void setRandomlyTicking(boolean v) {
		block.setIsRandomlyTickingKJS(v);
	}

	public void setSoundType(SoundType v) {
		block.setSoundTypeKJS(v);
	}

	public void setFriction(float v) {
		block.setFrictionKJS(v);
	}

	public void setSpeedFactor(float v) {
		block.setSpeedFactorKJS(v);
	}

	public void setJumpFactor(float v) {
		block.setJumpFactorKJS(v);
	}

	public void setLightEmission(int v) {
		for (BlockState state : block.getBlockStatesKJS()) {
			if (state instanceof BlockStateKJS) {
				((BlockStateKJS) state).setLightEmissionKJS(v);
			}
		}
	}

	public void setRequiresTool(boolean v) {
		for (BlockState state : block.getBlockStatesKJS()) {
			if (state instanceof BlockStateKJS) {
				((BlockStateKJS) state).setRequiresToolKJS(v);
			}
		}
	}
}
