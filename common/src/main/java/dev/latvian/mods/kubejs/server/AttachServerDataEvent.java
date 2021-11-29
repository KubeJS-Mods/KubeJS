package dev.latvian.mods.kubejs.server;

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
public class AttachServerDataEvent extends AttachDataEvent<ServerJS> {
	/**
	 * @deprecated Use {@link KubeJSPlugin#attachServerData(AttachServerDataEvent)} instead
	 */
	@Deprecated
	public static final Event<Consumer<AttachServerDataEvent>> EVENT = EventFactory.createConsumerLoop(AttachServerDataEvent.class);

	public AttachServerDataEvent(ServerJS s) {
		super(DataType.SERVER, s);
	}

	public void invoke() {
		KubeJSPlugins.forEachPlugin(plugin -> plugin.attachServerData(this));
		EVENT.invoker().accept(this);
	}
}