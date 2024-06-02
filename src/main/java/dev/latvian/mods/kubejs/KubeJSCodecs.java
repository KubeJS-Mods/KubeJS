package dev.latvian.mods.kubejs;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import dev.latvian.mods.kubejs.util.UtilsJS;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public interface KubeJSCodecs {
	Codec<Character> CHARACTER = Codec.STRING.xmap(str -> str.charAt(0), Object::toString);

	StreamCodec<? super RegistryFriendlyByteBuf, IntProvider> INT_PROVIDER_STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistries(IntProvider.CODEC);

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
		return UtilsJS.getUniqueId(input, o -> toJsonOrThrow(o, codec));
	}

	static JsonElement numberProviderJson(NumberProvider gen) {
		return toJsonOrThrow(gen, NumberProviders.CODEC);
	}

	// TODO: Check if this is correct
	static <T> Codec<List<T>> listOfOrSelf(Codec<T> codec) {
		return Codec.withAlternative(codec.listOf(), codec.xmap(List::of, List::getFirst));
	}
}
