package dev.latvian.mods.kubejs.holder;

import dev.latvian.mods.kubejs.core.RegistryObjectKJS;
import dev.latvian.mods.kubejs.script.KubeJSContext;
import dev.latvian.mods.kubejs.util.Cast;
import dev.latvian.mods.kubejs.util.ID;
import dev.latvian.mods.kubejs.util.RegExpKJS;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.regexp.NativeRegExp;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.holdersets.OrHolderSet;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public interface HolderWrapper {
	TypeInfo HOLDER = TypeInfo.of(Holder.class);
	TypeInfo HOLDER_SET = TypeInfo.of(HolderSet.class);

	static Holder<?> wrap(KubeJSContext cx, Object from, TypeInfo param) {
		if (from instanceof Holder<?> h) {
			return h;
		} else if (from == null) {
			throw Context.reportRuntimeError("Can't interpret 'null' as a Holder", cx);
		}

		var registry = cx.lookupRegistry(param, from);

		if (!ID.isKey(from)) {
			var h = registry.wrapAsHolder(Cast.to(from));

			if (h instanceof Holder.Direct) {
				var baseClass = cx.lookupRegistryType(param, from).baseClass();

				if (!baseClass.isInstance(from)) {
					throw Context.reportRuntimeError("Can't interpret '" + from + "' as Holder: can't cast object to '" + baseClass.getName() + "' of " + registry.key().location(), cx);
				}
			}

			return h;
		}

		var id = ID.mc(from);
		var holder = registry.getHolder(id);
		return holder.isEmpty() ? DeferredHolder.create(registry.key(), id) : holder.get();
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	static HolderSet<?> wrapSet(KubeJSContext cx, Object from, TypeInfo param) {
		var registry = cx.lookupRegistry(param, from);

		var simpleHolders = wrapSimpleSet(registry, from);

		if (simpleHolders != null) {
			return simpleHolders;
		}

		if (from instanceof Iterable<?> itr) {
			var allDirects = Stream.<HolderSet<?>>builder();
			var complex = new ArrayList<HolderSet<?>>();

			for (var elem : itr) {
				var wrapped = wrapSet(cx, elem, param);

				if (wrapped instanceof HolderSet.Direct direct) {
					allDirects.accept(direct);
				} else {
					complex.add(wrapped);
				}
			}

			var compressedDirects = allDirects.build().flatMap(HolderSet::stream).distinct().toList();

			if (compressedDirects.isEmpty()) {
				return switch (complex.size()) {
					case 0 -> HolderSet.empty();
					case 1 -> complex.getFirst();
					default -> new OrHolderSet<>(complex);
				};
			} else {
				if (complex.isEmpty()) {
					return HolderSet.direct(compressedDirects);
				} else {
					complex.add(HolderSet.direct(compressedDirects));
					return new OrHolderSet<>(complex);
				}
			}
		} else {
			var holder = (Holder) cx.jsToJava(from, HOLDER.withParams(param));
			return HolderSet.direct(holder);
		}
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	@Nullable
	static <T> HolderSet<T> wrapSimpleSet(Registry<T> registry, Object from) {
		return switch (from) {
			case HolderSet set -> set;
			case Holder holder when holder.canSerializeIn(registry.holderOwner()) -> HolderSet.direct(holder);
			case NativeRegExp regex -> new RegExHolderSet<>(registry.asLookup(), RegExpKJS.wrap(regex));
			case Pattern regex -> new RegExHolderSet<>(registry.asLookup(), regex);
			case RegistryObjectKJS registered -> wrapSimpleSet(registry, registered.kjs$asHolder());
			case TagKey tag when tag.isFor(registry.key()) -> orEmpty(registry.getTag(tag));
			case ResourceKey<?> key when key.isFor(registry.key()) -> orEmpty(key.cast(registry.key())
				.flatMap(registry::getHolder)
				.map(HolderSet::direct));
			case ResourceLocation id -> orEmpty(registry.getHolder(id).map(HolderSet::direct));
			case CharSequence cs when cs.isEmpty() -> HolderSet.empty();
			case CharSequence cs -> {
				var s = cs.toString();
				yield switch (s.charAt(0)) {
					case '@' -> new NamespaceHolderSet<>(registry.asLookup(), s.substring(1));
					case '#' -> {
						var tagKey = TagKey.create(registry.key(), ResourceLocation.parse(s.substring(1)));
						yield registry.getOrCreateTag(tagKey);
					}
					case '/' -> wrapSimpleSet(registry, RegExpKJS.wrap(from));
					default -> null;
				};
			}
			case null, default -> null;
		};
	}

	@SuppressWarnings("all")
	private static <T> HolderSet<T> orEmpty(Optional<? extends HolderSet<T>> holder) {
		return ((Optional<HolderSet<T>>) holder).orElse(HolderSet.empty());
	}
}
