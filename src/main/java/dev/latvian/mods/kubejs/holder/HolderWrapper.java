package dev.latvian.mods.kubejs.holder;

import dev.latvian.mods.kubejs.script.KubeJSContext;
import dev.latvian.mods.kubejs.util.Cast;
import dev.latvian.mods.kubejs.util.ID;
import dev.latvian.mods.kubejs.util.RegExpKJS;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.Nullable;

import java.util.List;

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

	static HolderSet<?> wrapSet(KubeJSContext cx, Object from, TypeInfo param) {
		if (from instanceof HolderSet<?> h) {
			return h;
		}

		var registry = cx.lookupRegistry(param, from);

		var simpleHolders = wrapSimpleSet(registry, from);
		if (simpleHolders != null) {
			return simpleHolders;
		}

		if (from instanceof Iterable<?>) {
			var holder = (List) cx.jsToJava(from, TypeInfo.RAW_LIST.withParams(HOLDER.withParams(param)));
			return HolderSet.direct(holder);
		} else {
			var holder = wrap(cx, from, param);
			return HolderSet.direct(holder);
		}
	}

	@Nullable
	static <T> HolderSet<T> wrapSimpleSet(Registry<T> registry, Object from) {
		var regex = RegExpKJS.wrap(from);

		if (regex != null) {
			return new RegExHolderSet<>(registry.asLookup(), regex);
		}

		if (from instanceof CharSequence) {
			var s = from.toString();

			if (s.isEmpty()) {
				return HolderSet.empty();
			} else if (s.charAt(0) == '@') {
				return new NamespaceHolderSet<>(registry.asLookup(), s.substring(1));
			} else if (s.charAt(0) == '#') {
				var tagKey = TagKey.create(registry.key(), ResourceLocation.parse(s.substring(1)));
				return registry.getOrCreateTag(tagKey);
			}
		}

		return null;
	}
}
