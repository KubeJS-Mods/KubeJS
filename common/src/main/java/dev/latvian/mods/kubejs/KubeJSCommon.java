package dev.latvian.mods.kubejs;

import dev.latvian.mods.kubejs.level.LevelJS;
import dev.latvian.mods.kubejs.script.BindingsEvent;
import dev.latvian.mods.kubejs.util.KubeJSBackgroundThread;
import dev.latvian.mods.rhino.util.wrap.TypeWrappers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

/**
 * @author LatvianModder
 */
public class KubeJSCommon {
	public void startThread() {
		new KubeJSBackgroundThread().start();
	}

	public void init() {
	}

	public void reloadClientInternal() {
	}

	public void clientBindings(BindingsEvent event) {
	}

	public void clientTypeWrappers(TypeWrappers typeWrappers) {
	}

	public void handleDataFromServerPacket(String channel, @Nullable CompoundTag data) {
	}

	@Nullable
	public Player getClientPlayer() {
		return null;
	}

	public void paint(CompoundTag tag) {
	}

	public LevelJS getClientLevel() {
		throw new IllegalStateException("Can't access client level from server side!");
	}

	public LevelJS getLevel(Level level) {
		if (level.isClientSide()) {
			return getClientLevel();
		}

		return level.getServer().kjs$wrapMinecraftLevel(level);
	}

	public void reloadTextures() {
	}

	public void reloadLang() {
	}

	public boolean isClientButNotSelf(Player player) {
		return false;
	}
}