package dev.latvian.kubejs.world;

import dev.latvian.kubejs.script.AttachDataEvent;
import dev.latvian.kubejs.script.DataType;
import dev.latvian.kubejs.util.KubeJSPlugins;
import dev.architectury.architectury.event.Event;
import dev.architectury.architectury.event.EventFactory;

import java.util.function.Consumer;

/**
 * @author LatvianModder
 */
public class AttachWorldDataEvent extends AttachDataEvent<WorldJS> {
	/**
	 * @deprecated Use {@link dev.latvian.kubejs.KubeJSPlugin#attachWorldData(AttachWorldDataEvent)} instead
	 */
	@Deprecated
	public static final Event<Consumer<AttachWorldDataEvent>> EVENT = EventFactory.createConsumerLoop(AttachWorldDataEvent.class);

	public AttachWorldDataEvent(WorldJS w) {
		super(DataType.WORLD, w);
	}

	public void invoke() {
		KubeJSPlugins.forEachPlugin(plugin -> plugin.attachWorldData(this));
		EVENT.invoker().accept(this);
	}
}