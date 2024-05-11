package dev.latvian.mods.kubejs.level;

import dev.latvian.mods.kubejs.event.EventJS;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public abstract class LevelEventJS extends EventJS {
	public abstract Level getLevel();

	@Nullable
	public MinecraftServer getServer() {
		return getLevel().getServer();
	}

	public RegistryAccess getRegistries() {
		return getLevel().registryAccess();
	}
}