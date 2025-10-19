package dev.latvian.mods.kubejs.util.registrypredicate;

import com.google.gson.JsonPrimitive;
import dev.latvian.mods.kubejs.registry.RegistryType;
import dev.latvian.mods.kubejs.util.ID;
import dev.latvian.mods.kubejs.util.RegExpKJS;
import dev.latvian.mods.rhino.BaseFunction;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.regexp.NativeRegExp;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

@FunctionalInterface
@SuppressWarnings({"rawtypes", "unchecked"})
public interface RegistryPredicate<T> extends Predicate<Holder<T>> {
	static RegistryPredicate<?> of(Context cx, Object from, TypeInfo target) {
		return switch (from) {
			case null -> EntireRegistryPredicate.FALSE;
			case RegistryPredicate<?> p -> p;
			case Pattern pattern -> new RegistryRegExpPredicate<>(pattern);
			case NativeRegExp pattern -> new RegistryRegExpPredicate<>(RegExpKJS.wrap(pattern));
			case CharSequence cs -> ofString(target, cs.toString());
			case JsonPrimitive p -> ofString(target, p.getAsString());
			case BaseFunction fn -> {
				var t = target.param(0);
				var predicate = (Predicate) cx.jsToJava(fn, t.shouldConvert() ? TypeInfo.RAW_PREDICATE.withParams(t) : TypeInfo.RAW_PREDICATE);
				yield predicate::test;
			}
			default -> new RegistryHolderPredicate<>(new Holder.Direct<>(from));
		};
	}

	private static @NotNull RegistryPredicate<?> ofString(TypeInfo target, String s) {
		return switch (s) {
			case "*" -> EntireRegistryPredicate.TRUE;
			case "-" -> EntireRegistryPredicate.FALSE;
			case String tag when tag.charAt(0) == '#' -> {
				var reg = RegistryType.ofType(target.param(0));
				var registryTag = ID.mc(tag.substring(1));

				yield reg != null
					? new RegistryTagKeyPredicate<>(TagKey.create(reg.key(), registryTag))
					: new RegistryTagIDPredicate<>(registryTag);
			}
			case String namespace when namespace.charAt(0) == '@' -> new RegistryNamespacePredicate<>(namespace.substring(1));
			default -> {
				var pattern = RegExpKJS.wrap(s);

				if (pattern != null) {
					yield new RegistryRegExpPredicate<>(pattern);
				}

				var reg = RegistryType.ofType(target.param(0));
				var id = ID.mc(s);

				if (reg != null) {
					var registry = BuiltInRegistries.REGISTRY.get((ResourceKey) reg.key());

					if (registry != null) {
						var opt = registry.getHolder(id);

						if (opt.isPresent()) {
							yield new RegistryHolderPredicate<>((Holder) opt.get());
						}
					}
				}

				yield new RegistryIDPredicate<>(id);
			}
		};
	}

	default List<Holder.Reference<T>> getHolders(Registry<T> registry) {
		return registry.holders().filter(this).toList();
	}

	default List<T> getValues(Registry<T> registry) {
		return registry.holders().filter(this).map(Holder::value).toList();
	}
}
