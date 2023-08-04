package dev.latvian.mods.kubejs.block.callbacks;

import dev.latvian.mods.rhino.util.HideFromJS;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;

public class BlockStateRotateCallbackJS extends BlockStateModifyCallbackJS {

	private final Rotation rotation;

	public BlockStateRotateCallbackJS(BlockState state, Rotation rotation) {
		super(state);
		this.rotation = rotation;
	}

	public Direction rotate(Direction dir) {
		return rotation.rotate(dir);
	}

	@HideFromJS // begone ambiguity!
	@Override
	public BlockStateModifyCallbackJS rotate(Rotation rotation) {
		throw new IllegalCallerException("Do not call this or you will get stuck in a loop!");
	}

	public Rotation getRotation() {
		return rotation;
	}
}
