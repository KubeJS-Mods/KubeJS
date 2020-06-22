package dev.latvian.kubejs.client;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.docs.ID;
import dev.latvian.kubejs.event.EventJS;
import dev.latvian.kubejs.util.UtilsJS;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.registries.IForgeRegistry;

/**
 * @author LatvianModder
 */
public class SoundRegistryEventJS extends EventJS
{
	public final IForgeRegistry<SoundEvent> registry;

	public SoundRegistryEventJS(IForgeRegistry<SoundEvent> r)
	{
		registry = r;
	}

	public void register(@ID String id)
	{
		ResourceLocation r = UtilsJS.getMCID(KubeJS.appendModId(id));
		registry.register(new SoundEvent(r).setRegistryName(r));
	}
}