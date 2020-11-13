package dev.latvian.kubejs.util.forge;

import dev.latvian.kubejs.KubeJS;
import me.shedaniel.architectury.registry.Registries;
import me.shedaniel.architectury.registry.Registry;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.Optional;
import java.util.function.Function;

public class UtilsJSImpl
{
	public static <T> Field findField(Class<? extends T> className, String fieldName)
	{
		return ObfuscationReflectionHelper.findField((Class) className, fieldName);
	}

	private static <T> Function<ResourceLocation, Optional<T>> getValue(Object registry, @Nullable T def)
	{
		Registry<T> reg;
		if (registry instanceof IForgeRegistry)
		{
			reg = Registries.get(KubeJS.MOD_ID).get(RegistryKey.createRegistryKey(((IForgeRegistry<?>) registry).getRegistryName()));
		}
		else if (registry instanceof net.minecraft.util.registry.Registry)
		{
			reg = Registries.get(KubeJS.MOD_ID).get((net.minecraft.util.registry.Registry) registry);
		}
		else if (registry instanceof RegistryKey)
		{
			reg = Registries.get(KubeJS.MOD_ID).get((RegistryKey) registry);
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
