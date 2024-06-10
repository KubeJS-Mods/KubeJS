package dev.latvian.mods.kubejs.script;

import com.mojang.serialization.Codec;
import com.mojang.serialization.JavaOps;
import com.mojang.serialization.JsonOps;
import dev.latvian.mods.kubejs.util.Cast;
import dev.latvian.mods.kubejs.util.MapJS;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Wrapper;
import dev.latvian.mods.rhino.type.TypeInfo;
import dev.latvian.mods.rhino.util.wrap.TypeWrapperFactory;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;

import java.util.Map;

public record CodecTypeWrapper<T>(Class<T> target, Codec<T> codec, T defaultValue) implements TypeWrapperFactory<T> {
	@Override
	public T wrap(Context cx, Object o, TypeInfo target) {
		o = Wrapper.unwrapped(o);

		if (o == null) {
			return defaultValue;
		} else if (target.asClass().isInstance(o)) {
			return Cast.to(o);
		} else if (o instanceof Tag tag) {
			return codec.decode(NbtOps.INSTANCE, tag).result().get().getFirst();
		} else if (o instanceof Map<?, ?> map) {
			return codec.decode(JavaOps.INSTANCE, map).result().get().getFirst();
		} else {
			return codec.decode(JsonOps.INSTANCE, MapJS.json(cx, o)).result().get().getFirst();
		}
	}
}