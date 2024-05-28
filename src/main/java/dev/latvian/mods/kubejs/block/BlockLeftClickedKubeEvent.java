package dev.latvian.mods.kubejs.block;

import dev.latvian.mods.kubejs.level.BlockContainerJS;
import dev.latvian.mods.kubejs.player.KubePlayerEvent;
import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.jetbrains.annotations.Nullable;

@Info(value = """
	Invoked when a player left clicks on a block.
	""")
public class BlockLeftClickedKubeEvent implements KubePlayerEvent {
	private final PlayerInteractEvent.LeftClickBlock event;

	public BlockLeftClickedKubeEvent(PlayerInteractEvent.LeftClickBlock event) {
		this.event = event;
	}

	@Override
	@Info("The player that left clicked the block.")
	public Player getEntity() {
		return event.getEntity();
	}

	@Info("The block that was left clicked.")
	public BlockContainerJS getBlock() {
		return new BlockContainerJS(event.getLevel(), event.getPos());
	}

	@Info("The item that was used to left click the block.")
	public ItemStack getItem() {
		return event.getEntity().getItemInHand(event.getHand());
	}

	@Info("The face of the block that was left clicked.")
	@Nullable
	public Direction getFacing() {
		return event.getFace();
	}
}