package dev.latvian.mods.kubejs;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import dev.latvian.mods.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.mod.util.color.Color;
import dev.latvian.mods.rhino.mod.wrapper.ColorWrapper;
import io.netty.buffer.ByteBuf;
import net.minecraft.Util;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.regex.Pattern;

public interface KubeJSCodecs {
	Codec<Pattern> REGEX = stringResolverCodec(UtilsJS::toRegexString, UtilsJS::parseRegex);
	Codec<Duration> DURATION = stringResolverCodec(Duration::toString, UtilsJS::getDuration);
	Codec<Color> RHINO_COLOR = stringResolverCodec(Color::toString, ColorWrapper::of);

	StreamCodec<RegistryFriendlyByteBuf, Pattern> REGEX_STREAM = ByteBufCodecs.fromCodecWithRegistries(REGEX);
	StreamCodec<RegistryFriendlyByteBuf, Duration> DURATION_STREAM = ByteBufCodecs.fromCodecWithRegistries(DURATION);
	StreamCodec<RegistryFriendlyByteBuf, Color> RHINO_COLOR_STREAM = ByteBufCodecs.fromCodecWithRegistries(RHINO_COLOR);

	StreamCodec<ByteBuf, UUID> UUID_STREAM_CODEC = new StreamCodec<ByteBuf, UUID>() {
		@Override
		public UUID decode(ByteBuf buf) {
			return new UUID(buf.readLong(), buf.readLong());
		}

		@Override
		public void encode(ByteBuf buf, UUID uuid) {
			buf.writeLong(uuid.getMostSignificantBits());
			buf.writeLong(uuid.getLeastSignificantBits());
		}
	};

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
		return Util.getOrThrow(codec.parse(JsonOps.INSTANCE, json), onError);
	}

	static <E, X extends Throwable> JsonElement toJsonOrThrow(E value, Codec<E> codec, Function<String, X> onError) throws X {
		return Util.getOrThrow(codec.encodeStart(JsonOps.INSTANCE, value), onError);
	}

	static <T> String getUniqueId(T input, Codec<T> codec) {
		return UtilsJS.getUniqueId(input, o -> toJsonOrThrow(o, codec));
	}

	static JsonElement numberProviderJson(NumberProvider gen) {
		return toJsonOrThrow(gen, NumberProviders.CODEC);
	}
}
