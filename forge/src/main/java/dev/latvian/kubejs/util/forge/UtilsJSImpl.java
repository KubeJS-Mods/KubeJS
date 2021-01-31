package dev.latvian.kubejs.util.forge;

import dev.latvian.kubejs.KubeJS;
import me.shedaniel.architectury.registry.Registries;
import me.shedaniel.architectury.registry.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Function;

public class UtilsJSImpl
{
	private static <T> Function<ResourceLocation, Optional<T>> getValue(Object registry, @Nullable T def)
	{
		Registry<T> reg;
		if (registry instanceof IForgeRegistry)
		{
			reg = Registries.get(KubeJS.MOD_ID).get(ResourceKey.createRegistryKey(((IForgeRegistry<?>) registry).getRegistryName()));
		}
		else if (registry instanceof net.minecraft.core.Registry)
		{
			reg = Registries.get(KubeJS.MOD_ID).get((net.minecraft.core.Registry) registry);
		}
		else if (registry instanceof ResourceKey)
		{
			reg = Registries.get(KubeJS.MOD_ID).get((ResourceKey) registry);
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
