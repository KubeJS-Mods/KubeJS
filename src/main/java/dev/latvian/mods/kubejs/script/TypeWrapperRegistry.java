package dev.latvian.mods.kubejs.script;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.latvian.mods.kubejs.KubeJSCodecs;
import dev.latvian.mods.kubejs.util.Cast;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Wrapper;
import dev.latvian.mods.rhino.util.wrap.DirectTypeWrapperFactory;
import dev.latvian.mods.rhino.util.wrap.TypeWrapperFactory;
import dev.latvian.mods.rhino.util.wrap.TypeWrapperValidator;
import dev.latvian.mods.rhino.util.wrap.TypeWrappers;
import net.minecraft.util.StringRepresentable;

import java.util.function.BiFunction;

public class TypeWrapperRegistry {
	public interface ContextFromFunction<T> extends BiFunction<Context, Object, T> {
	}

	public interface RegistriesFromFunction<T> extends BiFunction<RegistryAccessContainer, Object, T> {
	}

	private final ScriptType type;
	private final TypeWrappers typeWrappers;

	public TypeWrapperRegistry(ScriptType type, TypeWrappers typeWrappers) {
		this.type = type;
		this.typeWrappers = typeWrappers;
	}

	public ScriptType scriptType() {
		return type;
	}

	public <T> void register(Class<T> target, TypeWrapperValidator validator, TypeWrapperFactory<T> factory) {
		typeWrappers.register(target, validator, factory);
	}

	public <T> void register(Class<T> target, TypeWrapperFactory<T> factory) {
		typeWrappers.register(target, factory);
	}

	public <T> void register(Class<T> target, TypeWrapperValidator validator, ContextFromFunction<T> factory) {
		typeWrappers.register(target, validator, (cx, from, t) -> factory.apply(cx, from));
	}

	public <T> void register(Class<T> target, ContextFromFunction<T> factory) {
		typeWrappers.register(target, (cx, from, t) -> factory.apply(cx, from));
	}

	public <T> void register(Class<T> target, RegistriesFromFunction<T> factory) {
		typeWrappers.register(target, (cx, from, t) -> factory.apply(((KubeJSContext) cx).getRegistries(), from));
	}

	public <T> void register(Class<T> target, TypeWrapperValidator validator, DirectTypeWrapperFactory<T> factory) {
		typeWrappers.registerDirect(target, validator, factory);
	}

	public <T> void register(Class<T> target, DirectTypeWrapperFactory<T> factory) {
		typeWrappers.registerDirect(target, factory);
	}

	public <T extends Enum<T> & StringRepresentable> void registerEnumFromStringCodec(Class<T> target, Codec<T> codec, T defaultValue, boolean forceLowerCase) {
		register(target, o -> {
			o = Wrapper.unwrapped(o);

			if (o == null) {
				return defaultValue;
			} else if (target.isInstance(o)) {
				return Cast.to(o);
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
