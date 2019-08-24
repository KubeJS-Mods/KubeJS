package dev.latvian.kubejs.util;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.event.EventJS;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

/**
 * @author LatvianModder
 */
public class RegistryEventJS<T extends IForgeRegistryEntry<T>> extends EventJS
{
	public final transient IForgeRegistry<T> registry;

	public RegistryEventJS(IForgeRegistry<T> r)
	{
		registry = r;
	}

	public T setID(String name, T value)
	{
		value.setRegistryName(new ResourceLocation(KubeJS.appendModId(name)));
		return value;
	}
}