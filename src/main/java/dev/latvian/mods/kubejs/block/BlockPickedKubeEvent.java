package dev.latvian.mods.kubejs.block;

import dev.latvian.mods.kubejs.entity.KubeRayTraceResult;
import dev.latvian.mods.kubejs.level.LevelBlock;
import dev.latvian.mods.kubejs.player.KubePlayerEvent;
import dev.latvian.mods.kubejs.plugin.builtin.event.BlockEvents;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.rhino.util.HideFromJS;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;

@Info(value = """
	Invoked when player middle-clicks on a block.
	""")
public class BlockPickedKubeEvent implements KubePlayerEvent {
	public final Level level;
	public final LevelBlock block;
	public final Player player;
	private final HitResult hitResult;
	private KubeRayTraceResult target;

	public BlockPickedKubeEvent(Level level, BlockPos pos, BlockState state, Player player, HitResult hitResult) {
		this.level = level;
		this.block = level.kjs$getBlock(pos).cache(state);
		this.player = player;
		this.hitResult = hitResult;
	}

	@Override
	public Level getLevel() {
		return block.getLevel();
	}

	@Override
	public Player getEntity() {
		return player;
	}

	public KubeRayTraceResult getTarget() {
		if (target == null) {
			target = new KubeRayTraceResult(player, hitResult);
		}

		return target;
	}

	@Nullable
	@HideFromJS
	public static ItemStack handle(BlockState state, HitResult target, LevelReader levelReader, BlockPos pos, Player player) {
		if (levelReader instanceof Level level) {
			var key = state.kjs$getKey();

			if (BlockEvents.PICKED.hasListeners(key) && BlockEvents.PICKED.post(level, key, new BlockPickedKubeEvent(level, pos, state, player, target)).value() instanceof ItemStack stack) {
				return stack;
			}
		}

		return null;
	}
}