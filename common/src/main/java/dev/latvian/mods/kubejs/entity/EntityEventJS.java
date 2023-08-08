package dev.latvian.mods.kubejs.entity;

import dev.latvian.mods.kubejs.level.LevelEventJS;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public abstract class EntityEventJS extends LevelEventJS {

	public abstract Entity getEntity();

	@Nullable
	public Player getPlayer() {
		return getEntity() instanceof Player p ? p : null;
	}

	@Override
	public Level getLevel() {
		return getEntity().level();
	}
}