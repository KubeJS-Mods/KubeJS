package dev.latvian.mods.kubejs;

import dev.latvian.mods.kubejs.level.LevelJS;
import dev.latvian.mods.kubejs.script.BindingsEvent;
import dev.latvian.mods.kubejs.server.ServerJS;
import dev.latvian.mods.rhino.util.wrap.TypeWrappers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * @author LatvianModder
 */
public class KubeJSCommon {
	public void init() {
	}

	public void reloadClientInternal() {
	}

	public void clientBindings(BindingsEvent event) {
	}

	public void clientTypeWrappers(TypeWrappers typeWrappers) {
	}

	public void handleDataToClientPacket(String channel, @Nullable CompoundTag data) {
	}

	@Nullable
	public Player getClientPlayer() {
		return null;
	}

	public void paint(CompoundTag tag) {
	}

	@Deprecated(forRemoval = true)
	@ApiStatus.ScheduledForRemoval(inVersion = "4.2")
	public final LevelJS getClientWorld() {
		return getClientLevel();
	}

	public LevelJS getClientLevel() {
		throw new IllegalStateException("Can't access client level from server side!");
	}

	@Deprecated(forRemoval = true)
	@ApiStatus.ScheduledForRemoval(inVersion = "4.1")
	public final LevelJS getWorld(Level level) {
		return getLevel(level);
	}

	public LevelJS getLevel(Level level) {
		if (level.isClientSide()) {
			return getClientLevel();
		}

		return ServerJS.instance.getLevel(level);
	}

	public void reloadTextures() {
	}

	public void reloadLang() {
	}

	public boolean isClientButNotSelf(Player player) {
		return false;
	}
}