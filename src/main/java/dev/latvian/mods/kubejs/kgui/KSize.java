package dev.latvian.mods.kubejs.kgui;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public record KSize(int w, int h) {
	public static final Codec<KSize> CODEC = Codec.either(Codec.INT, Codec.INT.listOf(2, 2)).xmap(
		e -> e.map(i -> of(i, i), l -> of(l.get(0), l.get(1))),
		s -> s.w == s.h ? Either.left(s.w) : Either.right(List.of(s.w, s.h))
	);

	public static final StreamCodec<ByteBuf, KSize> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.VAR_INT, KSize::w,
		ByteBufCodecs.VAR_INT, KSize::h,
		KSize::of
	);

	public static final KSize ZERO = new KSize(0, 0);

	public static KSize of(int w, int h) {
		return w <= 0 && h <= 0 ? ZERO : new KSize(Math.max(0, w), Math.max(0, h));
	}

	public boolean isZero() {
		return w <= 0 && h <= 0;
	}
}
