package dev.latvian.mods.kubejs.entity;

import dev.latvian.mods.kubejs.level.KubeLevelEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public interface KubeEntityEvent extends KubeLevelEvent {
	Entity getEntity();

	@Nullable
	default Player getPlayer() {
		return getEntity() instanceof Player p ? p : null;
	}

	@Override
	default Level getLevel() {
		return getEntity().level();
	}
}