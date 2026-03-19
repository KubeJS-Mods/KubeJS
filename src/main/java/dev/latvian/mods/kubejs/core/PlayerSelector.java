package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.error.KubeRuntimeException;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.UUIDWrapper;
import dev.latvian.mods.kubejs.script.SourceLine;
import dev.latvian.mods.kubejs.util.Cast;
import dev.latvian.mods.rhino.BaseFunction;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.jspecify.annotations.Nullable;

import java.util.Locale;
import java.util.UUID;

@FunctionalInterface
public interface PlayerSelector {
	TypeInfo TYPE_INFO = TypeInfo.of(PlayerSelector.class);

	static PlayerSelector wrap(Context cx, Object o) {
		return switch (o) {
			case null -> throw new KubeRuntimeException("PlayerSelector cannot be null!").source(SourceLine.of(cx));
			case ServerPlayer sp -> identity(sp);
			case UUID uuid -> uuid(uuid);
			case BaseFunction fn -> Cast.to(cx.createInterfaceAdapter(TYPE_INFO, fn));
			default -> fromString(cx, String.valueOf(o).trim().toLowerCase(Locale.ROOT));
		};
	}

	private static PlayerSelector fromString(Context cx, String name) {
		if (name.isEmpty()) {
			throw new KubeRuntimeException("PlayerSelector cannot be blank!").source(SourceLine.of(cx));
		}

		var uuid = UUIDWrapper.fromString(cx, name);
		if (uuid != null) {
			return uuid(uuid);
		}

		return server -> {
			var player = name(name).or(fuzzyName(name)).getPlayer(server);
			if (player != null) {
				return player;
			}

			throw new KubeRuntimeException("No player matched selector '%s'".formatted(name)).source(SourceLine.of(cx));
		};
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
