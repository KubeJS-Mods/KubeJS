package dev.latvian.mods.kubejs.block;

import dev.latvian.mods.kubejs.level.LevelBlock;
import dev.latvian.mods.kubejs.player.KubePlayerEvent;
import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.level.BlockEvent;

@Info(value = """
	Invoked when a block is destroyed by a player.
	""")
public class BlockBrokenKubeEvent implements KubePlayerEvent {
	private final BlockEvent.BreakEvent event;

	public BlockBrokenKubeEvent(BlockEvent.BreakEvent event) {
		this.event = event;
	}

	@Override
	@Info("The player that broke the block.")
	public Player getEntity() {
		return event.getPlayer();
	}

	@Info("The block that was broken.")
	public LevelBlock getBlock() {
		return ((Level) event.getLevel()).kjs$getBlock(event.getPos()).cache(event.getState());
	}
}