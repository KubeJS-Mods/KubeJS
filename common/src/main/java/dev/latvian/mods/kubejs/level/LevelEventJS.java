package dev.latvian.mods.kubejs.level;

import dev.latvian.mods.kubejs.event.EventJS;
import dev.latvian.mods.kubejs.server.ServerJS;
import dev.latvian.mods.kubejs.util.UtilsJS;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

/**
 * @author LatvianModder
 */
public abstract class LevelEventJS extends EventJS {
	public abstract LevelJS getLevel();

	@Nullable
	public ServerJS getServer() {
		return getLevel().getServer();
	}

	protected LevelJS levelOf(Level level) {
		return UtilsJS.getLevel(level);
	}

	protected LevelJS levelOf(Entity entity) {
		return levelOf(entity.level);
	}

	public final boolean post(String id) {
		return post(getLevel().getSide(), id);
	}

	public final boolean post(String id, String sub) {
		return post(getLevel().getSide(), id, sub);
	}
}