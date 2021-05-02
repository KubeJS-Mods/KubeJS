package dev.latvian.kubejs.client;

import dev.latvian.kubejs.entity.EntityJS;
import dev.latvian.kubejs.event.EventJS;
import dev.latvian.kubejs.player.ClientPlayerJS;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.world.ClientWorldJS;

public class ClientEventJS extends EventJS {
	public ClientWorldJS getWorld() {
		return ClientWorldJS.getInstance();
	}

	public EntityJS getEntity() {
		return getPlayer();
	}

	public ClientPlayerJS getPlayer() {
		return ClientWorldJS.getInstance().clientPlayerData.getPlayer();
	}

	public final boolean post(String id) {
		return post(ScriptType.CLIENT, id);
	}

	public final boolean post(String id, String sub) {
		return post(ScriptType.CLIENT, id, sub);
	}
}
