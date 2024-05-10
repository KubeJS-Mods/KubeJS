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
}