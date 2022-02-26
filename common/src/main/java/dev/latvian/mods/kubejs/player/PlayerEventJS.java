package dev.latvian.mods.kubejs.player;

import dev.latvian.mods.kubejs.entity.LivingEntityEventJS;
import dev.latvian.mods.kubejs.stages.Stages;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

/**
 * @author LatvianModder
 */
public abstract class PlayerEventJS extends LivingEntityEventJS {
	@Nullable
	public PlayerJS getPlayer() {
		var e = getEntity();

		if (e instanceof PlayerJS playerJS) {
			return playerJS;
		}

		return null;
	}

	@Nullable
	public Player getMinecraftPlayer() {
		PlayerJS<?> p = getPlayer();
		return p == null ? null : p.minecraftPlayer;
	}

	public boolean hasGameStage(String stage) {
		return Stages.get(getMinecraftPlayer()).has(stage);
	}

	public void addGameStage(String stage) {
		Stages.get(getMinecraftPlayer()).add(stage);
	}

	public void removeGameStage(String stage) {
		Stages.get(getMinecraftPlayer()).remove(stage);
	}
}