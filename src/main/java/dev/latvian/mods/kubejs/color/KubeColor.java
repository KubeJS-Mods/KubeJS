package dev.latvian.mods.kubejs.color;

import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.KubeJSCodecs;
import dev.latvian.mods.kubejs.bindings.ColorWrapper;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.util.RemapForJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import dev.latvian.mods.rhino.util.SpecialEquality;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

@RemapPrefixForJS("kjs$")
public interface KubeColor extends SpecialEquality {
	Codec<KubeColor> CODEC = KubeJSCodecs.stringResolverCodec(KubeColor::kjs$serialize, ColorWrapper::of);
	StreamCodec<ByteBuf, KubeColor> STREAM_CODEC = ByteBufCodecs.fromCodec(CODEC);

	@RemapForJS("getArgb")
	int kjs$getARGB();

	@RemapForJS("getRgb")
	default int kjs$getRGB() {
		return kjs$getARGB() & 0xFFFFFF;
	}

	default int kjs$getFireworkRGB() {
		return kjs$getRGB();
	}

	default String kjs$toHexString() {
		return String.format("#%08X", kjs$getARGB());
	}

	default String kjs$serialize() {
		return kjs$toHexString();
	}

	default TextColor kjs$createTextColor() {
		return TextColor.fromRgb(kjs$getRGB());
	}

	@Override
	default boolean specialEquals(Context cx, Object o, boolean shallow) {
		KubeColor c = ColorWrapper.of(o);
		return shallow ? (kjs$getARGB() == c.kjs$getARGB()) : (kjs$getRGB() == c.kjs$getRGB());
	}
}