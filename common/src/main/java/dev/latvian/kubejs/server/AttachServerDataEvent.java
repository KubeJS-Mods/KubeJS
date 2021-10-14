package dev.latvian.kubejs.server;

import dev.latvian.kubejs.script.AttachDataEvent;
import dev.latvian.kubejs.script.DataType;
import dev.latvian.kubejs.util.KubeJSPlugins;
import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;

import java.util.function.Consumer;

/**
 * @author LatvianModder
 */
public class AttachServerDataEvent extends AttachDataEvent<ServerJS> {
	/**
	 * @deprecated Use {@link dev.latvian.kubejs.KubeJSPlugin#attachServerData(AttachServerDataEvent)} instead
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