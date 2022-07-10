package dev.latvian.mods.kubejs.player;

import dev.latvian.mods.kubejs.entity.EntityJS;
import dev.latvian.mods.kubejs.event.EventHandler;
import net.minecraft.advancements.Advancement;
import net.minecraft.server.level.ServerPlayer;

/**
 * @author LatvianModder
 */
public class PlayerAdvancementEventJS extends PlayerEventJS {
	public static final EventHandler EVENT = EventHandler.server(PlayerAdvancementEventJS.class).cancelable().legacy("player.advancement");

	private final ServerPlayer player;
	private final Advancement advancement;

	public PlayerAdvancementEventJS(ServerPlayer player, Advancement advancement) {
		this.player = player;
		this.advancement = advancement;
	}

	@Override
	public EntityJS getEntity() {
		return entityOf(player);
	}

	public AdvancementJS getAdvancement() {
		return new AdvancementJS(advancement);
	}
}