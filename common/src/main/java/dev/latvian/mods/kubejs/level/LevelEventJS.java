package dev.latvian.mods.kubejs.level;

import dev.latvian.mods.kubejs.event.EventJS;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

/**
 * @author LatvianModder
 */
public abstract class LevelEventJS extends EventJS {
	public abstract Level getLevel();

	@Nullable
	public MinecraftServer getServer() {
		return getLevel().getServer();
	}
}