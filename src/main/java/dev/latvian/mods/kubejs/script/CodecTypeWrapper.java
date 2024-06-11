package dev.latvian.mods.kubejs.script;

import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.util.Cast;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Wrapper;
import dev.latvian.mods.rhino.type.TypeInfo;
import dev.latvian.mods.rhino.util.wrap.TypeWrapperFactory;

public record CodecTypeWrapper<T>(Class<T> target, Codec<T> codec, T defaultValue) implements TypeWrapperFactory<T> {
	@Override
	public T wrap(Context cx, Object o, TypeInfo target) {
		o = Wrapper.unwrapped(o);

		if (o == null) {
			return defaultValue;
		} else if (target.asClass().isInstance(o)) {
			return Cast.to(o);
		} else {
			return ((KubeJSContext) cx).getRegistries().decode(cx, codec, o);
		}
	}
}