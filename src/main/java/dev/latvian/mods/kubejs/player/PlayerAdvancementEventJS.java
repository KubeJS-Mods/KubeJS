package dev.latvian.mods.kubejs.player;

import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

@Info("""
	Invoked when a player gets an advancement.
	""")
public class PlayerAdvancementEventJS extends PlayerEventJS {
	private final ServerPlayer player;
	private final ResourceLocation id;

	public PlayerAdvancementEventJS(ServerPlayer player, ResourceLocation id) {
		this.player = player;
		this.id = id;
	}

	@Override
	@Info("Returns the player that got the advancement.")
	public ServerPlayer getEntity() {
		return player;
	}

	@Info("Returns the advancement that was obtained.")
	public AdvancementJS getAdvancement() {
		return player.server.kjs$getAdvancement(id);
	}
}