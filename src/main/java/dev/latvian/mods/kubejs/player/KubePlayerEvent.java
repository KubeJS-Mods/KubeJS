package dev.latvian.mods.kubejs.player;

import dev.latvian.mods.kubejs.entity.KubeLivingEntityEvent;
import net.minecraft.world.entity.player.Player;
import org.jspecify.annotations.Nullable;

public interface KubePlayerEvent extends KubeLivingEntityEvent {
	@Override
	Player getEntity();

	@Override
	@Nullable
	default Player getPlayer() {
		return getEntity();
	}
}