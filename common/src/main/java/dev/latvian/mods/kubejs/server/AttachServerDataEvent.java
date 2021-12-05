package dev.latvian.mods.kubejs.server;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.script.AttachDataEvent;
import dev.latvian.mods.kubejs.script.DataType;
import dev.latvian.mods.kubejs.util.KubeJSPlugins;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Consumer;

/**
 * @author LatvianModder
 * @apiNote This class and others like it will be changed significantly in 4.1,
 * including the removal of {@code EVENT} and the {@code @ForgeEvent}
 * annotation, honestly, just use the KubeJS plugin system instead...
 */
public class AttachServerDataEvent extends AttachDataEvent<ServerJS> {
	/**
	 * @deprecated Use {@link KubeJSPlugin#attachServerData(AttachServerDataEvent)} instead
	 */
	@Deprecated(forRemoval = true)
	@ApiStatus.ScheduledForRemoval(inVersion = "4.1")
	public static final Event<Consumer<AttachServerDataEvent>> EVENT = EventFactory.createConsumerLoop(AttachServerDataEvent.class);

	public AttachServerDataEvent(ServerJS s) {
		super(DataType.SERVER, s);
	}

	public void invoke() {
		KubeJSPlugins.forEachPlugin(plugin -> plugin.attachServerData(this));
		EVENT.invoker().accept(this);
	}
}