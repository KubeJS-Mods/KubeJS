package dev.latvian.mods.kubejs;

import dev.latvian.mods.kubejs.script.BindingsEvent;
import dev.latvian.mods.kubejs.server.ServerJS;
import dev.latvian.mods.kubejs.world.WorldJS;
import dev.latvian.mods.rhino.util.wrap.TypeWrappers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
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

	public WorldJS getClientWorld() {
		throw new IllegalStateException("Can't access client world from server side!");
	}

	public WorldJS getWorld(Level level) {
		if (level.isClientSide()) {
			return getClientWorld();
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