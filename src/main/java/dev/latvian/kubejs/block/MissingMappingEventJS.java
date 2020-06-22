package dev.latvian.kubejs.block;

import dev.latvian.kubejs.docs.ID;
import dev.latvian.kubejs.event.EventJS;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.util.UtilsJS;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author LatvianModder
 */
public class MissingMappingEventJS<T extends IForgeRegistryEntry<T>> extends EventJS
{
	private final RegistryEvent.MissingMappings<T> event;
	private final Function<ResourceLocation, T> valueProvider;

	public MissingMappingEventJS(RegistryEvent.MissingMappings<T> e, Function<ResourceLocation, T> v)
	{
		event = e;
		valueProvider = v;
	}

	private void findMapping(@ID String key, Consumer<RegistryEvent.MissingMappings.Mapping<T>> callback)
	{
		ResourceLocation k = UtilsJS.getMCID(key);

		for (RegistryEvent.MissingMappings.Mapping<T> mapping : event.getAllMappings())
		{
			if (mapping.key.equals(k))
			{
				callback.accept(mapping);
				return;
			}
		}
	}

	public void remap(@ID String key, @ID String value)
	{
		findMapping(key, mapping -> {
			ResourceLocation idTo = UtilsJS.getMCID(value);
			T to = valueProvider.apply(idTo);

			if (to != null)
			{
				ScriptType.STARTUP.console.info("Remapping " + mapping.key + " to " + idTo + " (" + to.getClass() + ")");
				mapping.remap(UtilsJS.cast(to));
			}
		});
	}

	public void ignore(@ID String key)
	{
		findMapping(key, RegistryEvent.MissingMappings.Mapping::ignore);
	}

	public void warn(@ID String key)
	{
		findMapping(key, RegistryEvent.MissingMappings.Mapping::warn);
	}

	public void fail(@ID String key)
	{
		findMapping(key, RegistryEvent.MissingMappings.Mapping::fail);
	}
}