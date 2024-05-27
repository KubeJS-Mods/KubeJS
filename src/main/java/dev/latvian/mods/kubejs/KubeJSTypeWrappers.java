package dev.latvian.mods.kubejs;

import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;

public interface KubeJSTypeWrappers {
	static Holder<?> holder(Context cx, Object from, TypeInfo target) {
		if (from == null) {
			return null;
		} else if (from instanceof Holder<?> h) {
			return h;
		}

		var reg = RegistryInfo.ofClass(target.param(0).asClass());

		if (reg != null) {
			return reg.getHolder(UtilsJS.getMCID(cx, from));
		}

		return new Holder.Direct<>(from);
	}

	static ResourceKey<?> resourceKey(Context cx, Object from, TypeInfo target) {
		if (from == null) {
			return null;
		} else if (from instanceof ResourceKey<?> k) {
			return k;
		}

		var cl = target.param(0).asClass();

		if (cl == ResourceKey.class) {
			return ResourceKey.createRegistryKey(UtilsJS.getMCID(cx, from));
		}

		var reg = RegistryInfo.ofClass(cl);

		if (reg != null) {
			return ResourceKey.create(reg.key, UtilsJS.getMCID(cx, from));
		}

		throw new IllegalArgumentException("Can't parse " + from + " as ResourceKey<?>!");
	}
}
