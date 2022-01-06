package dev.latvian.mods.kubejs.client;

import dev.latvian.mods.kubejs.entity.EntityJS;
import dev.latvian.mods.kubejs.event.EventJS;
import dev.latvian.mods.kubejs.level.world.ClientLevelJS;
import dev.latvian.mods.kubejs.player.ClientPlayerJS;
import dev.latvian.mods.kubejs.script.ScriptType;
import org.jetbrains.annotations.ApiStatus;

public class ClientEventJS extends EventJS {

	@Deprecated(forRemoval = true)
	@ApiStatus.ScheduledForRemoval(inVersion = "4.1")
	public final ClientLevelJS getWorld() {
		return getLevel();
	}

	public ClientLevelJS getLevel() {
		return ClientLevelJS.getInstance();
	}

	public EntityJS getEntity() {
		return getPlayer();
	}

	public ClientPlayerJS getPlayer() {
		return ClientLevelJS.getInstance().clientPlayerData.getPlayer();
	}

	public final boolean post(String id) {
		return post(ScriptType.CLIENT, id);
	}

	public final boolean post(String id, String sub) {
		return post(ScriptType.CLIENT, id, sub);
	}
}
