package dev.latvian.kubejs.block;

import dev.latvian.kubejs.event.EventJS;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.util.UtilsJS;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;

import java.util.function.Consumer;

/**
 * @author LatvianModder
 */
public class MissingMappingEventJS extends EventJS
{
	public final RegistryEvent.MissingMappings<?> event;

	public MissingMappingEventJS(RegistryEvent.MissingMappings e)
	{
		event = e;
	}

	public ResourceLocation getRegistry()
	{
		return event.getName();
	}

	public void forEachMapping(Object key, Consumer<RegistryEvent.MissingMappings.Mapping> callback)
	{
		ResourceLocation k = UtilsJS.getID(key);

		for (RegistryEvent.MissingMappings.Mapping<?> mapping : event.getAllMappings())
		{
			if ((k.getNamespace().equals("*") || k.getNamespace().equals(mapping.key.getNamespace())) && (k.getPath().equals("*") || k.getPath().equals(mapping.key.getPath())))
			{
				callback.accept(mapping);
			}
		}
	}

	public void remap(Object key, Object value)
	{
		ResourceLocation idTo = UtilsJS.getID(value);
		Object to = event.getRegistry().getValue(idTo);

		if (to != null)
		{
			ResourceLocation id = UtilsJS.getID(key);
			ScriptType.STARTUP.console.info("Remapping " + id + " to " + idTo + " (" + to + ")");
			forEachMapping(id, mapping -> mapping.remap(UtilsJS.cast(to)));
		}
	}

	public void ignore(Object key)
	{
		forEachMapping(key, RegistryEvent.MissingMappings.Mapping::ignore);
	}

	public void warn(Object key)
	{
		forEachMapping(key, RegistryEvent.MissingMappings.Mapping::warn);
	}

	public void fail(Object key)
	{
		forEachMapping(key, RegistryEvent.MissingMappings.Mapping::fail);
	}
}