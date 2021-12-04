package dev.latvian.mods.kubejs.world;

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
 * @deprecated This class and others like it will be changed significantly in 4.1,
 * including the removal of {@code EVENT} and the {@code @ForgeEvent}
 * annotation, honestly, just use the KubeJS plugin system instead...
 */
@Deprecated
public class AttachWorldDataEvent extends AttachDataEvent<WorldJS> {
	/**
	 * @deprecated Use {@link KubeJSPlugin#attachWorldData(AttachWorldDataEvent)} instead
	 */
	@Deprecated(forRemoval = true)
	@ApiStatus.ScheduledForRemoval(inVersion = "4.1")
	public static final Event<Consumer<AttachWorldDataEvent>> EVENT = EventFactory.createConsumerLoop(AttachWorldDataEvent.class);

	public AttachWorldDataEvent(WorldJS w) {
		super(DataType.WORLD, w);
	}

	public void invoke() {
		KubeJSPlugins.forEachPlugin(plugin -> plugin.attachWorldData(this));
		EVENT.invoker().accept(this);
	}
}