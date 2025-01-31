package dev.latvian.mods.kubejs.block;

import dev.latvian.mods.kubejs.block.callback.RandomTickCallback;
import dev.latvian.mods.kubejs.block.state.BlockStatePredicate;
import dev.latvian.mods.kubejs.event.KubeEvent;
import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;

import java.util.function.Consumer;

public class BlockModificationKubeEvent implements KubeEvent {

	@Info("""
		Modifies blocks that match the given predicate.
		
		**NOTE**: tag predicates are not supported at this time.
		""")
	public void modify(BlockStatePredicate predicate, Consumer<BlockModifications> c) {
		for (var block : predicate.getBlocks()) {
			c.accept(new BlockModifications(block));
		}
	}

	public record BlockModifications(Block block) {
		public void setHasCollision(boolean v) {
			block.kjs$setHasCollision(v);
		}

		public void setExplosionResistance(float v) {
			block.kjs$setExplosionResistance(v);
		}

		public void setIsRandomlyTicking(boolean v) {
			block.kjs$setIsRandomlyTicking(v);
		}

		public void setRandomTickCallback(Consumer<RandomTickCallback> callback) {
			block.kjs$setRandomTickCallback(callback);
		}

		public void setSoundType(SoundType v) {
			block.kjs$setSoundType(v);
		}

		public void setFriction(float v) {
			block.kjs$setFriction(v);
		}

		public void setSpeedFactor(float v) {
			block.kjs$setSpeedFactor(v);
		}

		public void setJumpFactor(float v) {
			block.kjs$setJumpFactor(v);
		}

		public void setNameKey(String key) {
			block.kjs$setNameKey(key);
		}

		public void setDestroySpeed(float v) {
			for (var state : block.kjs$getBlockStates()) {
				state.kjs$setDestroySpeed(v);
			}
		}

		public void setLightEmission(int v) {
			for (var state : block.kjs$getBlockStates()) {
				state.kjs$setLightEmission(v);
			}
		}

		public void setRequiresTool(boolean v) {
			for (var state : block.kjs$getBlockStates()) {
				state.kjs$setRequiresTool(v);
			}
		}
	}
}
