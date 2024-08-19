package dev.latvian.mods.kubejs.registry;

import com.mojang.datafixers.util.Pair;
import dev.latvian.mods.kubejs.DevProperties;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.bindings.event.StartupEvents;
import dev.latvian.mods.kubejs.entity.AttributeBuilder;
import dev.latvian.mods.kubejs.script.ConsoleJS;
import dev.latvian.mods.kubejs.script.ScriptType;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.List;
import java.util.function.Predicate;

@EventBusSubscriber(modid = KubeJS.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class RegistryEventHandler {
	@SuppressWarnings({"rawtypes", "unchecked"})
	@SubscribeEvent(priority = EventPriority.LOW)
	public static void registerAll(RegisterEvent event) {
		handleRegistryEvent((ResourceKey) event.getRegistryKey(), event);
	}

	@SubscribeEvent(priority = EventPriority.LOW)
	public static void registerEntityAttributes(EntityAttributeModificationEvent event) {
		var objStorage = RegistryObjectStorage.of(Registries.ATTRIBUTE);
		List<Pair<Predicate<EntityType<?>>, Holder<Attribute>>> predicatePair = objStorage.objects.values().stream().filter(AttributeBuilder.class::isInstance).map(AttributeBuilder.class::cast).flatMap(b -> b.getPredicateList().stream().map(p -> Pair.of(p, BuiltInRegistries.ATTRIBUTE.wrapAsHolder(b.get())))).toList();

		event.getTypes().forEach(type -> predicatePair.stream().filter(p -> p.getFirst().test(type)).forEach(p -> event.add(type, p.getSecond())));
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
