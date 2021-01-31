package dev.latvian.kubejs.util.fabric;

import me.shedaniel.architectury.registry.Registry;
import me.shedaniel.architectury.registry.fabric.RegistriesImpl;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Function;

public class UtilsJSImpl
{
	private static <T> Function<ResourceLocation, Optional<T>> getValue(Object registry, @Nullable T def)
	{
		Registry<T> reg;
		if (registry instanceof net.minecraft.core.Registry)
		{
			reg = RegistriesImpl.RegistryProviderImpl.INSTANCE.get((net.minecraft.core.Registry) registry);
		}
		else if (registry instanceof ResourceKey)
		{
			reg = RegistriesImpl.RegistryProviderImpl.INSTANCE.get((ResourceKey) registry);
		}
		else
		{
			throw new UnsupportedOperationException("Not a registry: " + registry);
		}
		return id -> {
			T value = reg.get(id);

			if (value != null && value != def)
			{
				return Optional.of(value);
			}

			return Optional.empty();
		};
	}
}
