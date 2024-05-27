package dev.latvian.mods.kubejs.util.registrypredicate;

import com.google.gson.JsonPrimitive;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.kubejs.util.RegExpJS;
import dev.latvian.mods.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.BaseFunction;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.regexp.NativeRegExp;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;

import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

@FunctionalInterface
public interface RegistryPredicate<T> extends Predicate<Holder<T>> {
	static RegistryPredicate<?> of(Context cx, Object from, TypeInfo target) {
		if (from == null) {
			return EntireRegistryPredicate.FALSE;
		} else if (from instanceof RegistryPredicate<?> p) {
			return p;
		} else if (from instanceof Pattern || from instanceof NativeRegExp) {
			return new RegistryRegExpPredicate<>(RegExpJS.of(from));
		} else if (from instanceof CharSequence || from instanceof JsonPrimitive) {
			var s = from instanceof JsonPrimitive p ? p.getAsString() : from.toString();

			if (s.equals("*")) {
				return EntireRegistryPredicate.TRUE;
			} else if (s.equals("-")) {
				return EntireRegistryPredicate.FALSE;
			} else if (s.startsWith("#")) {
				var reg = RegistryInfo.ofClass(target.param(0).asClass());
				var tag = UtilsJS.getMCID(cx, s.substring(1));

				if (reg != null) {
					return new RegistryTagKeyPredicate<>(TagKey.create(reg.key, tag));
				} else {
					return new RegistryTagIDPredicate<>(tag);
				}
			} else if (s.startsWith("@")) {
				return new RegistryNamespacePredicate<>(s.substring(1));
			} else {
				var pattern = RegExpJS.of(s);

				if (pattern != null) {
					return new RegistryRegExpPredicate<>(pattern);
				} else {
					var reg = RegistryInfo.ofClass(target.param(0).asClass());
					var id = UtilsJS.getMCID(cx, s);

					if (reg != null) {
						return new RegistryHolderPredicate<>(reg.getHolder(id));
					} else {
						return new RegistryIDPredicate<>(id);
					}
				}
			}
		} else if (from instanceof BaseFunction) {
			var t = target.param(0);
			var predicate = (Predicate) cx.jsToJava(from, t.shouldConvert() ? TypeInfo.RAW_PREDICATE.withParams(t) : TypeInfo.RAW_PREDICATE);
			return predicate::test;
		} else {
			return new RegistryHolderPredicate<>(new Holder.Direct<>(from));
		}
	}

	default List<Holder.Reference<T>> getHolders(Registry<T> registry) {
		return registry.holders().filter(this).toList();
	}

	default List<T> getValues(Registry<T> registry) {
		return registry.holders().filter(this).map(Holder::value).toList();
	}
}
