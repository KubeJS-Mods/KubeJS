package dev.latvian.mods.kubejs.script;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.latvian.mods.kubejs.KubeJSCodecs;
import dev.latvian.mods.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.Wrapper;
import dev.latvian.mods.rhino.util.wrap.TypeWrapperFactory;
import dev.latvian.mods.rhino.util.wrap.TypeWrappers;
import net.minecraft.util.StringRepresentable;

import java.util.function.Predicate;

public class WrapperRegistry {
	public final ScriptType type;
	public final TypeWrappers typeWrappers;

	public WrapperRegistry(ScriptType type, TypeWrappers typeWrappers) {
		this.type = type;
		this.typeWrappers = typeWrappers;
	}

	public <T> void register(Class<T> target, Predicate<Object> validator, TypeWrapperFactory<T> factory) {
		typeWrappers.register(target, validator, factory);
	}

	public <T> void register(Class<T> target, TypeWrapperFactory<T> factory) {
		typeWrappers.register(target, factory);
	}

	public <T> void registerSimple(Class<T> target, Predicate<Object> validator, TypeWrapperFactory.Simple<T> factory) {
		typeWrappers.registerSimple(target, validator, factory);
	}

	public <T> void registerSimple(Class<T> target, TypeWrapperFactory.Simple<T> factory) {
		typeWrappers.registerSimple(target, factory);
	}

	public <T extends Enum<T> & StringRepresentable> void registerEnumFromStringCodec(Class<T> target, Codec<T> codec, T defaultValue, boolean forceLowerCase) {
		registerSimple(target, o -> {
			o = Wrapper.unwrapped(o);

			if (o == null) {
				return defaultValue;
			} else if (target.isInstance(o)) {
				return UtilsJS.cast(o);
			} else {
				var s = o.toString();
				return s.isEmpty() ? defaultValue : KubeJSCodecs.byName(codec, forceLowerCase ? s.toLowerCase() : s);
			}
		});
	}

	public <T extends Enum<T> & StringRepresentable> void registerEnumFromStringCodec(Class<T> target, Codec<T> codec) {
		registerEnumFromStringCodec(target, codec, null, true);
	}

	public <T> void registerCodec(Class<T> target, Codec<T> codec, T defaultValue) {
		typeWrappers.register(target, new CodecTypeWrapper<>(target, codec, defaultValue));
	}

	public <T> void registerCodec(Class<T> target, Codec<T> codec) {
		registerCodec(target, codec, null);
	}

	public <T> void registerMapCodec(Class<T> target, MapCodec<T> codec, T defaultValue) {
		typeWrappers.register(target, new MapCodecTypeWrapper<>(target, codec, defaultValue));
	}

	public <T> void registerMapCodec(Class<T> target, MapCodec<T> codec) {
		registerMapCodec(target, codec, null);
	}
}
