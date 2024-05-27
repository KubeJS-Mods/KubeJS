package dev.latvian.mods.kubejs.util;

import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.KubeJSCodecs;
import dev.latvian.mods.rhino.regexp.NativeRegExp;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Pattern;

public interface RegExpJS {
	Pattern REGEX_PATTERN = Pattern.compile("/(.*)/([a-z]*)");
	Codec<Pattern> CODEC = KubeJSCodecs.stringResolverCodec(RegExpJS::toRegExpString, RegExpJS::of);
	StreamCodec<ByteBuf, Pattern> STREAM_CODEC = ByteBufCodecs.STRING_UTF8.map(RegExpJS::of, RegExpJS::toRegExpString);

	@Nullable
	static Pattern of(Object o) {
		if (o instanceof CharSequence || o instanceof NativeRegExp) {
			return ofString(o.toString());
		} else if (o instanceof Pattern pattern) {
			return pattern;
		}

		return null;
	}

	@Nullable
	static Pattern ofString(String string) {
		if (string.length() < 3) {
			return null;
		}

		var matcher = REGEX_PATTERN.matcher(string);

		if (matcher.matches()) {
			var flags = 0;
			var f = matcher.group(2);

			for (var i = 0; i < f.length(); i++) {
				switch (f.charAt(i)) {
					case 'd' -> flags |= Pattern.UNIX_LINES;
					case 'i' -> flags |= Pattern.CASE_INSENSITIVE;
					case 'x' -> flags |= Pattern.COMMENTS;
					case 'm' -> flags |= Pattern.MULTILINE;
					case 's' -> flags |= Pattern.DOTALL;
					case 'u' -> flags |= Pattern.UNICODE_CASE;
					case 'U' -> flags |= Pattern.UNICODE_CHARACTER_CLASS;
				}
			}

			return Pattern.compile(matcher.group(1), flags);
		}

		return null;
	}

	static String toRegExpString(Pattern pattern) {
		var sb = new StringBuilder("/");
		sb.append(pattern.pattern());
		sb.append('/');

		var flags = pattern.flags();

		if ((flags & Pattern.UNIX_LINES) != 0) {
			sb.append('d');
		}

		if ((flags & Pattern.CASE_INSENSITIVE) != 0) {
			sb.append('i');
		}

		if ((flags & Pattern.COMMENTS) != 0) {
			sb.append('x');
		}

		if ((flags & Pattern.MULTILINE) != 0) {
			sb.append('m');
		}

		if ((flags & Pattern.DOTALL) != 0) {
			sb.append('s');
		}

		if ((flags & Pattern.UNICODE_CASE) != 0) {
			sb.append('u');
		}

		if ((flags & Pattern.UNICODE_CHARACTER_CLASS) != 0) {
			sb.append('U');
		}

		return sb.toString();
	}
}
