package dev.latvian.mods.kubejs.world;

import dev.latvian.mods.kubejs.event.EventJS;
import dev.latvian.mods.kubejs.server.ServerJS;
import dev.latvian.mods.kubejs.util.UtilsJS;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

/**
 * @author LatvianModder
 */
public abstract class WorldEventJS extends EventJS {
	public abstract WorldJS getWorld();

	public WorldJS getLevel() {
		return getWorld();
	}

	@Nullable
	public ServerJS getServer() {
		return getWorld().getServer();
	}

	protected WorldJS worldOf(Level world) {
		return UtilsJS.getWorld(world);
	}

	protected WorldJS worldOf(Entity entity) {
		return worldOf(entity.level);
	}

	public final boolean post(String id) {
		return post(getWorld().getSide(), id);
	}

	public final boolean post(String id, String sub) {
		return post(getWorld().getSide(), id, sub);
	}
}