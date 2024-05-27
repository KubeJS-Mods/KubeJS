package dev.latvian.mods.kubejs.color;

import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.KubeJSCodecs;
import dev.latvian.mods.kubejs.bindings.ColorWrapper;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.util.SpecialEquality;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public interface Color extends SpecialEquality {
	Codec<Color> CODEC = KubeJSCodecs.stringResolverCodec(Color::toString, ColorWrapper::of);
	StreamCodec<ByteBuf, Color> STREAM_CODEC = ByteBufCodecs.fromCodec(CODEC);

	int getArgbJS();

	default int getRgbJS() {
		return getArgbJS() & 0xFFFFFF;
	}

	default int getFireworkColorJS() {
		return getRgbJS();
	}

	default String getHexJS() {
		return String.format("#%08X", getArgbJS());
	}

	default String getSerializeJS() {
		return getHexJS();
	}

	default TextColor createTextColorJS() {
		return TextColor.fromRgb(getRgbJS());
	}

	@Override
	default boolean specialEquals(Context cx, Object o, boolean shallow) {
		Color c = ColorWrapper.of(o);
		return shallow ? (getArgbJS() == c.getArgbJS()) : (getRgbJS() == c.getRgbJS());
	}
}