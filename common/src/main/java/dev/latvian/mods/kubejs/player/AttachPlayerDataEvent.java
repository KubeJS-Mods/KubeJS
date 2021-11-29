package dev.latvian.mods.kubejs.player;

import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.script.AttachDataEvent;
import dev.latvian.mods.kubejs.script.DataType;
import dev.latvian.mods.kubejs.util.KubeJSPlugins;
import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;

import java.util.function.Consumer;

/**
 * @author LatvianModder
 */
public class AttachPlayerDataEvent extends AttachDataEvent<PlayerDataJS> {
	/**
	 * @deprecated Use {@link KubeJSPlugin#attachPlayerData(AttachPlayerDataEvent)} instead
	 */
	@Deprecated
	public static final Event<Consumer<AttachPlayerDataEvent>> EVENT = EventFactory.createConsumerLoop(AttachPlayerDataEvent.class);

	public AttachPlayerDataEvent(PlayerDataJS p) {
		super(DataType.PLAYER, p);
	}

	public void invoke() {
		KubeJSPlugins.forEachPlugin(plugin -> plugin.attachPlayerData(this));
		EVENT.invoker().accept(this);
	}
}