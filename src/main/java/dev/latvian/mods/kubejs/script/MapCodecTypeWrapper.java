package dev.latvian.mods.kubejs.script;

import com.mojang.serialization.JavaOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import dev.latvian.mods.kubejs.util.Cast;
import dev.latvian.mods.kubejs.util.MapJS;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Wrapper;
import dev.latvian.mods.rhino.type.TypeInfo;
import dev.latvian.mods.rhino.util.wrap.TypeWrapperFactory;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;

import java.util.Map;

public record MapCodecTypeWrapper<T>(Class<T> target, MapCodec<T> codec, T defaultValue) implements TypeWrapperFactory<T> {
	@Override
	public T wrap(Context cx, Object o, TypeInfo target) {
		o = Wrapper.unwrapped(o);

		if (o == null) {
			return defaultValue;
		} else if (target.asClass().isInstance(o)) {
			return Cast.to(o);
		} else if (o instanceof Tag tag) {
			return codec.decode(NbtOps.INSTANCE, NbtOps.INSTANCE.getMap(tag).getOrThrow()).result().get();
		} else if (o instanceof Map<?, ?> map) {
			return codec.decode(JavaOps.INSTANCE, JavaOps.INSTANCE.getMap(map).getOrThrow()).result().get();
		} else {
			return codec.decode(JsonOps.INSTANCE, JsonOps.INSTANCE.getMap(MapJS.json(cx, o)).getOrThrow()).result().get();
		}
	}
}