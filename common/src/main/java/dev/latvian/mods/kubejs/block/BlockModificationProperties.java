package dev.latvian.mods.kubejs.block;

import dev.latvian.mods.kubejs.core.BlockKJS;
import dev.latvian.mods.kubejs.core.BlockStateKJS;
import net.minecraft.world.level.block.SoundType;

/**
 * @author LatvianModder
 */
public class BlockModificationProperties {
	public final BlockKJS block;

	public BlockModificationProperties(BlockKJS i) {
		block = i;
	}

	public void setMaterial(MaterialJS v) {
		var m = v.getMinecraftMaterial();

		block.setMaterialKJS(m);
		setSoundType(v.getSound());

		for (var state : block.getBlockStatesKJS()) {
			if (state instanceof BlockStateKJS stateKJS) {
				stateKJS.setMaterialKJS(m);
			}
		}
	}

	public void setHasCollision(boolean v) {
		block.setHasCollisionKJS(v);
	}

	public void setDestroySpeed(float v) {
		for (var state : block.getBlockStatesKJS()) {
			if (state instanceof BlockStateKJS stateKJS) {
				stateKJS.setDestroySpeedKJS(v);
			}
		}
	}

	public void setExplosionResistance(float v) {
		block.setExplosionResistanceKJS(v);
	}

	public void setRandomlyTicking(boolean v) {
		block.setIsRandomlyTickingKJS(v);
	}

	// TODO: separate materials and sound types to make this method actually useful
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
		for (var state : block.getBlockStatesKJS()) {
			if (state instanceof BlockStateKJS stateKJS) {
				stateKJS.setLightEmissionKJS(v);
			}
		}
	}

	public void setRequiresTool(boolean v) {
		for (var state : block.getBlockStatesKJS()) {
			if (state instanceof BlockStateKJS stateKJS) {
				stateKJS.setRequiresToolKJS(v);
			}
		}
	}
}
