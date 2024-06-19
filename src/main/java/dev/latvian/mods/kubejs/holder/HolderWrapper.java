package dev.latvian.mods.kubejs.holder;

import dev.latvian.mods.kubejs.script.KubeJSContext;
import dev.latvian.mods.kubejs.util.ID;
import dev.latvian.mods.kubejs.util.RegExpKJS;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

import java.util.List;

public interface HolderWrapper {
	TypeInfo HOLDER = TypeInfo.of(Holder.class);
	TypeInfo HOLDER_SET = TypeInfo.of(HolderSet.class);

	static Holder<?> wrap(KubeJSContext cx, Object from, TypeInfo param) {
		if (from instanceof Holder<?> h) {
			return h;
		}

		var registry = cx.lookupRegistry(param, from);
		var id = ID.mc(from);

		var holder = registry.getHolder(id);

		if (holder.isEmpty()) {
			throw Context.reportRuntimeError("Can't interpret '" + from + "' as Holder: entry not found", cx);
		}

		return holder.get();
	}

	static HolderSet<?> wrapSet(KubeJSContext cx, Object from, TypeInfo param) {
		if (from instanceof HolderSet<?> h) {
			return h;
		}

		var registry = cx.lookupRegistry(param, from);

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
				var tagKey = TagKey.create((ResourceKey) registry.key(), ResourceLocation.parse(s.substring(1)));
				return (HolderSet) registry.getTag(tagKey).orElse(HolderSet.empty());
			}
		}

		if (from instanceof Iterable<?>) {
			var holder = (List) cx.jsToJava(from, TypeInfo.RAW_LIST.withParams(HOLDER.withParams(param)));
			return HolderSet.direct(holder);
		} else {
			var holder = wrap(cx, from, param);
			return HolderSet.direct(holder);
		}
	}
}
