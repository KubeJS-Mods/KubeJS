package dev.latvian.mods.kubejs.player;

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
public class AttachPlayerDataEvent extends AttachDataEvent<PlayerDataJS> {
	/**
	 * @deprecated Use {@link KubeJSPlugin#attachPlayerData(AttachPlayerDataEvent)} instead
	 */
	@Deprecated(forRemoval = true)
	@ApiStatus.ScheduledForRemoval(inVersion = "4.1")
	public static final Event<Consumer<AttachPlayerDataEvent>> EVENT = EventFactory.createConsumerLoop(AttachPlayerDataEvent.class);

	public AttachPlayerDataEvent(PlayerDataJS p) {
		super(DataType.PLAYER, p);
	}

	public void invoke() {
		KubeJSPlugins.forEachPlugin(plugin -> plugin.attachPlayerData(this));
		EVENT.invoker().accept(this);
	}
}