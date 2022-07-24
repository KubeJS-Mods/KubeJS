package dev.latvian.mods.kubejs.level;

import dev.latvian.mods.kubejs.event.EventJS;
import dev.latvian.mods.kubejs.util.UtilsJS;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

/**
 * @author LatvianModder
 */
public abstract class LevelEventJS extends EventJS {
	public abstract LevelJS getLevel();

	@Nullable
	public MinecraftServer getServer() {
		return getLevel().getServer();
	}

	protected LevelJS levelOf(Level level) {
		return UtilsJS.getLevel(level);
	}

	protected LevelJS levelOf(Entity entity) {
		return levelOf(entity.level);
	}
}