package dev.latvian.mods.kubejs.registry;

import dev.latvian.mods.kubejs.CommonProperties;
import dev.latvian.mods.kubejs.DevProperties;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.util.ConsoleJS;
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
		handleRegistryEvent(RegistryInfo.of((ResourceKey) event.getRegistryKey()), event);
	}

	private static <T> void handleRegistryEvent(RegistryInfo<T> registryInfo, RegisterEvent event) {
		if (!registryInfo.bypassServerOnly && CommonProperties.get().serverOnly) {
			if (DevProperties.get().debugInfo) {
				KubeJS.LOGGER.info("Skipping " + registryInfo + " registry - server only");
			}

			return;
		}

		if (registryInfo.objects.isEmpty()) {
			if (DevProperties.get().debugInfo) {
				KubeJS.LOGGER.info("Skipping " + registryInfo + " registry - no objects to build");
			}

			return;
		}

		if (DevProperties.get().debugInfo) {
			KubeJS.LOGGER.info("Building " + registryInfo.objects.size() + " objects of " + registryInfo + " registry");
		}

		int added = 0;

		for (var builder : registryInfo) {
			if (!builder.dummyBuilder) {
				event.register(registryInfo.key, builder.id, builder::createTransformedObject);

				if (DevProperties.get().debugInfo) {
					ConsoleJS.STARTUP.info("+ " + registryInfo + " | " + builder.id);
				}

				added++;
			}
		}

		if (!registryInfo.objects.isEmpty() && DevProperties.get().debugInfo) {
			KubeJS.LOGGER.info("Registered " + added + "/" + registryInfo.objects.size() + " objects of " + registryInfo);
		}
	}
}
