package dev.latvian.kubejs;

import dev.latvian.kubejs.client.SoundRegistryEventJS;
import dev.latvian.kubejs.script.ScriptType;
import me.shedaniel.architectury.registry.Registries;
import net.minecraft.core.Registry;
import net.minecraft.sounds.SoundEvent;

/**
 * @author LatvianModder
 */
public class KubeJSOtherEventHandler
{
	public static void init()
	{
		new SoundRegistryEventJS(id -> {
			Registries.get(KubeJS.MOD_ID).get(Registry.SOUND_EVENT_REGISTRY).register(id, () -> new SoundEvent(id));
		}).post(ScriptType.STARTUP, KubeJSEvents.SOUND_REGISTRY);
	}
}