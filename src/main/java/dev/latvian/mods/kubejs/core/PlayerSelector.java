package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.bindings.UUIDWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

@FunctionalInterface
public interface PlayerSelector {

	static PlayerSelector wrap(Object o) {
		if (o instanceof ServerPlayer sp) {
			return identity(sp);
		} else if (o instanceof UUID uuid) {
			return uuid(uuid);
		}

		var name = Objects.toString(o, "").trim().toLowerCase(Locale.ROOT);

		if (name.isEmpty()) {
			return identity(null);
		}

		var uuid = UUIDWrapper.fromString(name);
		if (uuid != null) {
			return uuid(uuid);
		}

		return name(name).or(fuzzyName(name));
	}

	@Nullable
	ServerPlayer getPlayer(MinecraftServer server);

	static PlayerSelector identity(ServerPlayer player) {
		return server -> player;
	}

	static PlayerSelector uuid(UUID uuid) {
		return server -> server.getPlayerList().getPlayer(uuid);
	}

	static PlayerSelector name(String name) {
		return server -> server.getPlayerList().getPlayerByName(name);
	}

	static PlayerSelector fuzzyName(String name) {
		return server -> {
			for (var p : server.getPlayerList().getPlayers()) {
				if (p.getScoreboardName().toLowerCase(Locale.ROOT).contains(name)) {
					return p;
				}
			}

			return null;
		};
	}

	default PlayerSelector or(PlayerSelector fallback) {
		return server -> {
			var p = getPlayer(server);
			return p == null ? fallback.getPlayer(server) : p;
		};
	}

}
