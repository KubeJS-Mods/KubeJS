package dev.latvian.mods.kubejs.registry;

import dev.latvian.mods.kubejs.DevProperties;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.bindings.event.StartupEvents;
import dev.latvian.mods.kubejs.script.ConsoleJS;
import dev.latvian.mods.kubejs.script.ScriptType;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.RegisterEvent;

@EventBusSubscriber(modid = KubeJS.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class RegistryEventHandler {
	@SuppressWarnings({"rawtypes", "unchecked"})
	@SubscribeEvent(priority = EventPriority.LOW)
	public static void registerAll(RegisterEvent event) {
		handleRegistryEvent((ResourceKey) event.getRegistryKey(), event);
	}

	private static <T> void handleRegistryEvent(ResourceKey<Registry<T>> registryKey, RegisterEvent event) {
		StartupEvents.REGISTRY.post(ScriptType.STARTUP, (ResourceKey) registryKey, new RegistryKubeEvent<>(registryKey));

		var objStorage = RegistryObjectStorage.of(registryKey);

		if (objStorage.objects.isEmpty()) {
			if (DevProperties.get().logRegistryEventObjects) {
				KubeJS.LOGGER.info("Skipping " + registryKey.location() + " registry - no objects to build");
			}

			return;
		}

		if (DevProperties.get().logRegistryEventObjects) {
			KubeJS.LOGGER.info("Building " + objStorage.objects.size() + " objects of " + registryKey.location() + " registry");
		}

		int added = 0;

		for (var builder : objStorage) {
			if (!builder.dummyBuilder) {
				event.register(registryKey, builder.id, builder::createTransformedObject);

				if (DevProperties.get().logRegistryEventObjects) {
					ConsoleJS.STARTUP.info("+ " + registryKey.location() + " | " + builder.id);
				}

				added++;
			}
		}

		if (!objStorage.objects.isEmpty() && DevProperties.get().logRegistryEventObjects) {
			KubeJS.LOGGER.info("Registered " + added + "/" + objStorage.objects.size() + " objects of " + registryKey.location());
		}
	}
}
