package dev.latvian.mods.kubejs.item;

import dev.latvian.mods.kubejs.player.KubePlayerEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.player.PlayerDestroyItemEvent;
import org.jetbrains.annotations.Nullable;

public class ItemDestroyedKubeEvent implements KubePlayerEvent {
	private final PlayerDestroyItemEvent event;

	public ItemDestroyedKubeEvent(PlayerDestroyItemEvent e) {
		event = e;
	}

	@Override
	public Player getEntity() {
		return event.getEntity();
	}

	@Nullable
	public InteractionHand getHand() {
		return event.getHand();
	}

	public ItemStack getItem() {
		return event.getOriginal();
	}
}