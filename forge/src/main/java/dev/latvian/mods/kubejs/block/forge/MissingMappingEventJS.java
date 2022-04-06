package dev.latvian.mods.kubejs.block.forge;

import dev.latvian.mods.kubejs.event.EventJS;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.kubejs.util.UtilsJS;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author LatvianModder
 */
public class MissingMappingEventJS<T extends IForgeRegistryEntry<T>> extends EventJS {
	private final RegistryEvent.MissingMappings<T> event;
	private final Function<ResourceLocation, T> valueProvider;

	public MissingMappingEventJS(RegistryEvent.MissingMappings<T> e, Function<ResourceLocation, T> v) {
		event = e;
		valueProvider = v;
	}

	private void findMapping(ResourceLocation key, Consumer<RegistryEvent.MissingMappings.Mapping<T>> callback) {
		for (var mapping : event.getAllMappings()) {
			if (mapping.key.equals(key)) {
				callback.accept(mapping);
				return;
			}
		}
	}

	public void remap(ResourceLocation key, ResourceLocation value) {
		findMapping(key, mapping ->
		{
			var to = valueProvider.apply(value);

			if (to != null) {
				ConsoleJS.STARTUP.info("Remapping " + mapping.key + " to " + value + " (" + to.getClass() + ")");
				mapping.remap(UtilsJS.cast(to));
			}
		});
	}

	public void ignore(ResourceLocation key) {
		findMapping(key, RegistryEvent.MissingMappings.Mapping::ignore);
	}

	public void warn(ResourceLocation key) {
		findMapping(key, RegistryEvent.MissingMappings.Mapping::warn);
	}

	public void fail(ResourceLocation key) {
		findMapping(key, RegistryEvent.MissingMappings.Mapping::fail);
	}
}