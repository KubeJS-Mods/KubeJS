package dev.latvian.mods.kubejs.block;

import dev.architectury.utils.value.IntValue;
import dev.latvian.mods.kubejs.entity.EntityJS;
import dev.latvian.mods.kubejs.level.world.BlockContainerJS;
import dev.latvian.mods.kubejs.player.PlayerEventJS;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * @author LatvianModder
 */
public class BlockBreakEventJS extends PlayerEventJS {
	private final ServerPlayer entity;
	private final Level level;
	private final BlockPos pos;
	private final BlockState state;
	@Nullable
	private final IntValue xp;

	public BlockBreakEventJS(ServerPlayer entity, Level level, BlockPos pos, BlockState state, @Nullable IntValue xp) {
		this.entity = entity;
		this.level = level;
		this.pos = pos;
		this.state = state;
		this.xp = xp;
	}

	@Override
	public boolean canCancel() {
		return true;
	}

	@Override
	public EntityJS getEntity() {
		return entityOf(entity);
	}

	public BlockContainerJS getBlock() {
		return new BlockContainerJS(level, pos) {
			@Override
			public BlockState getBlockState() {
				return state;
			}
		};
	}

	public int getXp() {
		if (xp == null) {
			return 0;
		}
		return xp.getAsInt();
	}

	public void setXp(int xp) {
		if (this.xp != null) {
			this.xp.accept(xp);
		}
	}
}