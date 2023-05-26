package dev.latvian.mods.kubejs.player;

import dev.latvian.mods.kubejs.entity.LivingEntityEventJS;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public abstract class PlayerEventJS extends LivingEntityEventJS {
	@Override
	public abstract Player getEntity();

	@Override
	@Nullable
	public Player getPlayer() {
		return getEntity();
	}

	public boolean hasGameStage(String stage) {
		return getEntity().kjs$getStages().has(stage);
	}

	public void addGameStage(String stage) {
		getEntity().kjs$getStages().add(stage);
	}

	public void removeGameStage(String stage) {
		getEntity().kjs$getStages().remove(stage);
	}
}