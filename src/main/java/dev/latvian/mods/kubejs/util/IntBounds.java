package dev.latvian.mods.kubejs.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;

public record IntBounds(int min, int max) {
	public static final IntBounds DEFAULT = new IntBounds(1, Integer.MAX_VALUE);
	public static final IntBounds OPTIONAL = new IntBounds(0, Integer.MAX_VALUE);

	public static IntBounds of(int min, int max) {
		if (max == Integer.MAX_VALUE) {
			if (min == 1) {
				return DEFAULT;
			} else if (min == 0) {
				return OPTIONAL;
			}
		}

		return new IntBounds(min, max);
	}

	public static final MapCodec<IntBounds> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("min", 1).forGetter(IntBounds::min),
		ExtraCodecs.POSITIVE_INT.optionalFieldOf("max", Integer.MAX_VALUE).forGetter(IntBounds::max)
	).apply(instance, IntBounds::of));

	public static final Codec<IntBounds> CODEC = MAP_CODEC.codec();

	public static final StreamCodec<ByteBuf, IntBounds> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.VAR_INT, IntBounds::min,
		ByteBufCodecs.VAR_INT, IntBounds::max,
		IntBounds::of
	);
}
