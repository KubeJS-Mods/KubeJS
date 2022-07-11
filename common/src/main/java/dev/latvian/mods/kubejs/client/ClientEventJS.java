package dev.latvian.mods.kubejs.client;

import dev.latvian.mods.kubejs.entity.EntityJS;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.event.EventJS;
import dev.latvian.mods.kubejs.level.ClientLevelJS;
import dev.latvian.mods.kubejs.player.ClientPlayerJS;

public class ClientEventJS extends EventJS {
	public static final EventHandler INIT_EVENT = EventHandler.client(ClientEventJS.class).name("clientInit").legacy("client.init");
	public static final EventHandler LOGGED_IN_EVENT = EventHandler.client(ClientEventJS.class).name("clientLoggedIn").legacy("client.logged_in");
	public static final EventHandler LOGGED_OUT_EVENT = EventHandler.client(ClientEventJS.class).name("clientLoggedOut").legacy("client.logged_out");
	public static final EventHandler TICK_EVENT = EventHandler.client(ClientEventJS.class).name("clientTick").legacy("client.tick");
	public static final EventHandler PAINTER_UPDATED_EVENT = EventHandler.client(ClientEventJS.class).name("painterUpdated").legacy("client.painter_updated");

	public ClientLevelJS getLevel() {
		return ClientLevelJS.getInstance();
	}

	public EntityJS getEntity() {
		return getPlayer();
	}

	public ClientPlayerJS getPlayer() {
		return ClientLevelJS.getInstance().clientPlayerData.getPlayer();
	}
}
