package dev.latvian.kubejs;

import dev.latvian.kubejs.client.SoundRegistryEventJS;
import dev.latvian.kubejs.script.ScriptType;
import net.minecraft.sounds.SoundEvent;

/**
 * @author LatvianModder
 */
public class KubeJSOtherEventHandler {
	public static void init() {
		new SoundRegistryEventJS(id -> KubeJSRegistries.soundEvents().register(id, () -> new SoundEvent(id))).post(ScriptType.STARTUP, KubeJSEvents.SOUND_REGISTRY);
	}
}