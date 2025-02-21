package dev.latvian.mods.kubejs;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.StringUtilsWrapper;
import dev.latvian.mods.kubejs.util.TimeJS;
import dev.latvian.mods.rhino.type.EnumTypeInfo;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public interface KubeJSCodecs {
	Codec<Character> CHARACTER = Codec.STRING.xmap(str -> str.charAt(0), Object::toString);
	Codec<ResourceLocation> KUBEJS_ID = Codec.STRING.xmap(str -> str.indexOf(':') == -1 ? KubeJS.id(str) : ResourceLocation.parse(str), s -> s.getNamespace().equals(KubeJS.MOD_ID) ? s.getPath() : s.toString());

	Codec<Class<?>> ENUM_CLASS = Codec.STRING.flatXmap(str -> {
		try {
			var c = Class.forName(str);

			if (!c.isEnum()) {
				return DataResult.error(() -> "Class '" + str + "' is not an enum");
			}

			return DataResult.success(c);
		} catch (ClassNotFoundException e) {
			return DataResult.error(() -> "Could not find enum class: " + str);
		}
	}, c -> DataResult.success(c.getName()));

	Codec<EnumTypeInfo> ENUM_TYPE_INFO = ENUM_CLASS.flatXmap(c -> {
		if (TypeInfo.of(c) instanceof EnumTypeInfo info) {
			return DataResult.success(info);
		} else {
			return DataResult.error(() -> "Class " + c.getTypeName() + " is not an enum!");
		}
	}, info -> DataResult.success(info.asClass()));

	Codec<ResourceKey<? extends Registry<?>>> REGISTRY_KEY = ResourceLocation.CODEC.xmap(ResourceKey::createRegistryKey, ResourceKey::location);

	MapCodec<EntityType<?>> ENTITY_TYPE_FIELD = BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("id");

	Codec<Duration> DURATION = KubeJSCodecs.stringResolverCodec(Duration::toString, TimeJS::wrapDuration);

	static <E> Codec<E> stringResolverCodec(Function<E, String> toStringFunction, Function<String, E> fromStringFunction) {
		return Codec.STRING.flatXmap(str -> Optional.ofNullable(fromStringFunction.apply(str))
				.map(DataResult::success)
				.orElseGet(() -> DataResult.error(() -> "Unknown element id: " + str)),
			(object) -> DataResult.success(toStringFunction.apply(object))
		);
	}

	static <E extends Enum<E> & StringRepresentable> E byName(Codec<E> codec, String s) {
		return fromJsonOrThrow(new JsonPrimitive(s), codec);
	}

	static <E> E fromJsonOrThrow(JsonElement json, Codec<E> codec) {
		return fromJsonOrThrow(json, codec, str -> {
			throw new JsonSyntaxException("Could not decode element from JSON: " + str);
		});
	}

	static <E> JsonElement toJsonOrThrow(E value, Codec<E> codec) {
		return toJsonOrThrow(value, codec, str -> {
			throw new IllegalArgumentException("Could not encode element to JSON: " + str);
		});
	}

	static <E, X extends Throwable> E fromJsonOrThrow(JsonElement json, Codec<E> codec, Function<String, X> onError) throws X {
		return codec.parse(JsonOps.INSTANCE, json).getOrThrow(onError);
	}

	static <E, X extends Throwable> JsonElement toJsonOrThrow(E value, Codec<E> codec, Function<String, X> onError) throws X {
		return codec.encodeStart(JsonOps.INSTANCE, value).getOrThrow(onError);
	}

	static <T> String getUniqueId(T input, Codec<T> codec) {
		return StringUtilsWrapper.getUniqueId(input, o -> toJsonOrThrow(o, codec));
	}

	static JsonElement numberProviderJson(NumberProvider gen) {
		return toJsonOrThrow(gen, NumberProviders.CODEC);
	}

	static <T> Codec<List<T>> listOfOrSelf(Codec<T> codec) {
		return listOfOrSelf(codec.listOf(), codec);
	}

	// TODO: Check if this is correct
	static <T> Codec<List<T>> listOfOrSelf(Codec<List<T>> listCodec, Codec<T> codec) {
		return Codec.either(listCodec, codec).xmap(either -> either.map(Function.identity(), List::of), Either::left);
		// return Codec.withAlternative(listCodec, codec.xmap(List::of, List::getFirst));
	}
}
