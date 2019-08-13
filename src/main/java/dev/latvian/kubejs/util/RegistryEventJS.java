package dev.latvian.kubejs.util;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.events.EventJS;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

/**
 * @author LatvianModder
 */
public class RegistryEventJS<T extends IForgeRegistryEntry<T>> extends EventJS
{
	protected final IForgeRegistry<T> registry;

	public RegistryEventJS(IForgeRegistry<T> r)
	{
		registry = r;
	}

	public T setID(String name, T value)
	{
		value.setRegistryName(new ResourceLocation(KubeJS.ID_CONTEXT.appendModId(name)));
		return value;
	}
}