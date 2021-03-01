package dev.latvian.kubejs.player;

import dev.latvian.kubejs.entity.EntityJS;
import net.minecraft.advancements.Advancement;
import net.minecraft.server.level.ServerPlayer;

/**
 * @author LatvianModder
 */
public class PlayerAdvancementEventJS extends PlayerEventJS {
	private final ServerPlayer player;
	private final Advancement advancement;

	public PlayerAdvancementEventJS(ServerPlayer player, Advancement advancement) {
		this.player = player;
		this.advancement = advancement;
	}

	@Override
	public boolean canCancel() {
		return true;
	}

	@Override
	public EntityJS getEntity() {
		return entityOf(player);
	}

	public AdvancementJS getAdvancement() {
		return new AdvancementJS(advancement);
	}
}