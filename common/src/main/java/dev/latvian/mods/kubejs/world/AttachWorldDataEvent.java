package dev.latvian.mods.kubejs.world;

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
public class AttachWorldDataEvent extends AttachDataEvent<WorldJS> {
	/**
	 * @deprecated Use {@link KubeJSPlugin#attachWorldData(AttachWorldDataEvent)} instead
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