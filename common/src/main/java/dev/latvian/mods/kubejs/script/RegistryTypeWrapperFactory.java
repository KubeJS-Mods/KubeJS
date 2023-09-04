package dev.latvian.mods.kubejs.script;

import dev.architectury.registry.registries.Registrar;
import dev.latvian.mods.kubejs.registry.KubeJSRegistries;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.kubejs.util.Lazy;
import dev.latvian.mods.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.util.wrap.TypeWrapperFactory;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

import java.util.ArrayList;
import java.util.List;

public record RegistryTypeWrapperFactory<T>(RegistryInfo info, ResourceKey<Registry<T>> key, Registrar<T>[] registrar) implements TypeWrapperFactory<T> {
	public static final Lazy<List<RegistryTypeWrapperFactory<?>>> ALL = Lazy.of(() -> {
		var all = new ArrayList<RegistryTypeWrapperFactory<?>>();

		for (var ri : RegistryInfo.MAP.values()) {
			if (ri.autoWrap) {
				all.add(new RegistryTypeWrapperFactory<>(ri, UtilsJS.cast(ri.key), new Registrar[1]));
			}
		}

		return all;
	});

	@Override
	@SuppressWarnings("unchecked")
	public T wrap(Context cx, Object o) {
		if (o == null) {
			return null;
		} else if (info.objectBaseClass.isInstance(o)) {
			return (T) o;
		}

		if (registrar[0] == null) {
			registrar[0] = KubeJSRegistries.genericRegistry(key);
		}

		var id = UtilsJS.getMCID(cx, o);
		var value = registrar[0].get(id);

		if (value == null) {
			var npe = new NullPointerException("No such element with id %s in registry %s!".formatted(id, info));
			ConsoleJS.getCurrent(cx).error("Error while wrapping registry element type!", npe);
			throw npe;
		}

		return value;
	}

	@Override
	public String toString() {
		return "RegistryTypeWrapperFactory{type=" + info.objectBaseClass.getName() + ", registry=" + info + '}';
	}
}
