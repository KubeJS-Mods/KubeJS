package dev.latvian.kubejs;

import dev.latvian.kubejs.client.SoundRegistryEventJS;
import dev.latvian.kubejs.script.ScriptType;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * @author LatvianModder
 */
public class KubeJSOtherEventHandler
{
	public void init()
	{
		FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(SoundEvent.class, this::registry);
	}

	private void registry(RegistryEvent.Register<SoundEvent> event)
	{
		new SoundRegistryEventJS(event.getRegistry()).post(ScriptType.STARTUP, KubeJSEvents.SOUND_REGISTRY);
	}
}