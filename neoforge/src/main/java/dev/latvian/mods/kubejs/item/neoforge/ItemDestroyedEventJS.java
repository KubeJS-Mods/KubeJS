package dev.latvian.mods.kubejs.item.neoforge;

import dev.latvian.mods.kubejs.player.PlayerEventJS;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.player.PlayerDestroyItemEvent;
import org.jetbrains.annotations.Nullable;

public class ItemDestroyedEventJS extends PlayerEventJS {
	private final PlayerDestroyItemEvent event;

	public ItemDestroyedEventJS(PlayerDestroyItemEvent e) {
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