package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.player.ServerPlayerDataJS;
import dev.latvian.mods.kubejs.player.ServerPlayerJS;
import dev.latvian.mods.rhino.mod.wrapper.UUIDWrapper;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@FunctionalInterface
public interface PlayerSelector {

	static PlayerSelector of(Object o) {
		if (o instanceof ServerPlayerJS sp) {
			return identity(sp);
		} else if (o instanceof Player p) {
			return mc(p);
		} else if (o instanceof UUID uuid) {
			return uuid(uuid);
		}

		var name = Objects.toString(o, "").trim().toLowerCase();

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
	ServerPlayerJS getPlayer(Map<UUID, ? extends ServerPlayerDataJS> knownPlayers);

	static PlayerSelector identity(ServerPlayerJS player) {
		return knownPlayers -> player;
	}

	static PlayerSelector mc(Player player) {
		return uuid(player.getUUID());
	}

	static PlayerSelector uuid(UUID uuid) {
		return knownPlayers -> {
			var data = knownPlayers.get(uuid);
			return data == null ? null : data.getPlayer();
		};
	}

	static PlayerSelector name(String name) {
		return knownPlayers -> {
			for (var p : knownPlayers.values()) {
				if (p.getName().toLowerCase(Locale.ROOT).equals(name)) {
					return p.getPlayer();
				}
			}
			return null;
		};
	}

	static PlayerSelector fuzzyName(String name) {
		return knownPlayers -> {
			for (var p : knownPlayers.values()) {
				if (p.getName().toLowerCase(Locale.ROOT).contains(name)) {
					return p.getPlayer();
				}
			}
			return null;
		};
	}

	default PlayerSelector or(PlayerSelector fallback) {
		return knownPlayers -> {
			var p = getPlayer(knownPlayers);
			return p == null ? fallback.getPlayer(knownPlayers) : p;
		};
	}

}
