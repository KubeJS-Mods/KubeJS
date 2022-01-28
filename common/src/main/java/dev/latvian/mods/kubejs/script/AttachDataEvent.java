package dev.latvian.mods.kubejs.script;

import dev.latvian.mods.kubejs.level.LevelJS;
import dev.latvian.mods.kubejs.player.PlayerDataJS;
import dev.latvian.mods.kubejs.server.ServerJS;
import dev.latvian.mods.kubejs.util.WithAttachedData;

public record AttachDataEvent<T extends WithAttachedData>(
		DataType<T> type,
		T parent
) {

	public void add(String id, Object object) {
		parent.getData().put(id, object);
	}

	public void invoke() {
		type.pluginCallback().accept(this);
	}

	public static AttachDataEvent<LevelJS> forLevel(LevelJS level) {
		return new AttachDataEvent<>(DataType.LEVEL, level);
	}

	public static AttachDataEvent<ServerJS> forServer(ServerJS server) {
		return new AttachDataEvent<>(DataType.SERVER, server);
	}

	public static AttachDataEvent<PlayerDataJS> forPlayer(PlayerDataJS playerData) {
		return new AttachDataEvent<>(DataType.PLAYER, playerData);
	}

}