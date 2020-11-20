package dev.latvian.kubejs.util.fabric;

import me.shedaniel.architectury.registry.Registry;
import me.shedaniel.architectury.registry.fabric.RegistriesImpl;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.function.Function;

public class UtilsJSImpl
{
	public static <T> Field findField(Class<? extends T> className, String fieldName)
	{
		try
		{
			// We actually need the field description to remap, but it is unmapped in production anyways so it is probably fine to not even remap it.
			return className.getDeclaredField(fieldName);
		}
		catch (NoSuchFieldException e)
		{
			throw new RuntimeException(e);
		}
	}

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
		else throw new UnsupportedOperationException("Not a registry: " + registry);
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
