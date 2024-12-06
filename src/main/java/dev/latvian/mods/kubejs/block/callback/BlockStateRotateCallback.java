package dev.latvian.mods.kubejs.block.callback;

import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.rhino.util.HideFromJS;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;

public class BlockStateRotateCallback extends BlockStateModifyCallback {

	private final Rotation rotation;

	public BlockStateRotateCallback(BlockState state, Rotation rotation) {
		super(state);
		this.rotation = rotation;
	}

	@Info("Rotates the specified direction")
	public Direction rotate(Direction dir) {
		return rotation.rotate(dir);
	}

	@HideFromJS // begone ambiguity!
	@Override
	public BlockStateModifyCallback rotate(Rotation rotation) {
		throw new IllegalCallerException("Do not call this or you will get stuck in a loop!");
	}

	@Info("Get the Rotation that this block is being rotated by")
	public Rotation getRotation() {
		return rotation;
	}
}
