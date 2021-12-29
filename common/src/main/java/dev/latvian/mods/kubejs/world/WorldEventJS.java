package dev.latvian.mods.kubejs.world;

import dev.latvian.mods.kubejs.event.EventJS;
import dev.latvian.mods.kubejs.server.ServerJS;
import dev.latvian.mods.kubejs.util.UtilsJS;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * @author LatvianModder
 */
public abstract class WorldEventJS extends EventJS {
	public abstract WorldJS getLevel();

	@Deprecated(forRemoval = true)
	@ApiStatus.ScheduledForRemoval(inVersion = "4.1")
	public final WorldJS getWorld() {
		return getLevel();
	}

	@Nullable
	public ServerJS getServer() {
		return getLevel().getServer();
	}

	protected WorldJS levelOf(Level level) {
		return UtilsJS.getLevel(level);
	}

	protected WorldJS levelOf(Entity entity) {
		return levelOf(entity.level);
	}

	public final boolean post(String id) {
		return post(getLevel().getSide(), id);
	}

	public final boolean post(String id, String sub) {
		return post(getLevel().getSide(), id, sub);
	}
}