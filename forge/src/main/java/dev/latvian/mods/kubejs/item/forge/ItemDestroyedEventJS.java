package dev.latvian.mods.kubejs.item.forge;

import dev.latvian.mods.kubejs.player.PlayerEventJS;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import org.jetbrains.annotations.Nullable;

public class ItemDestroyedEventJS extends PlayerEventJS {
	private final PlayerDestroyItemEvent event;

	public ItemDestroyedEventJS(PlayerDestroyItemEvent e) {
		event = e;
	}

	@Override
	public ServerPlayer getEntity() {
		return (ServerPlayer) event.getEntity();
	}

	@Nullable
	public InteractionHand getHand() {
		return event.getHand();
	}

	public ItemStack getItem() {
		return event.getOriginal();
	}
}