package dev.latvian.kubejs.player;

import dev.latvian.kubejs.script.AttachDataEvent;
import dev.latvian.kubejs.script.DataType;
import dev.latvian.kubejs.util.KubeJSPlugins;
import me.shedaniel.architectury.event.Event;
import me.shedaniel.architectury.event.EventFactory;

import java.util.function.Consumer;

/**
 * @author LatvianModder
 */
public class AttachPlayerDataEvent extends AttachDataEvent<PlayerDataJS> {
	public static final Event<Consumer<AttachPlayerDataEvent>> EVENT = EventFactory.createConsumerLoop(AttachPlayerDataEvent.class);

	public AttachPlayerDataEvent(PlayerDataJS p) {
		super(DataType.PLAYER, p);
	}

	public void invoke() {
		KubeJSPlugins.forEachPlugin(plugin -> plugin.attachPlayerData(this));
		EVENT.invoker().accept(this);
	}
}