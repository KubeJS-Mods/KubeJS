package dev.latvian.mods.kubejs.script;

import dev.latvian.mods.rhino.Context;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.Nullable;

public record SourceLine(String source, int line) {
	public static final SourceLine UNKNOWN = new SourceLine("", 0);

	public static SourceLine of(@Nullable String source, int line) {
		if ((source == null || source.isEmpty()) && line <= 0) {
			return UNKNOWN;
		} else {
			return new SourceLine(source == null || source.isEmpty() ? "" : source, Math.max(line, 0));
		}
	}

	public static SourceLine read(FriendlyByteBuf buf) {
		return of(buf.readUtf(), buf.readVarInt());
	}

	public static SourceLine of(@Nullable Context cx) {
		if (cx == null) {
			return UNKNOWN;
		}

		int[] lineP = {0};
		var source = Context.getSourcePositionFromStack(cx, lineP);
		return SourceLine.of(source, lineP[0]);
	}

	public boolean isUnknown() {
		return source.isEmpty() && line <= 0;
	}

	@Override
	public String toString() {
		if (source.isEmpty() && line <= 0) {
			return "";
		} else if (source.isEmpty()) {
			return "<unknown source>#" + line;
		} else if (line <= 0) {
			return source;
		} else {
			return source + "#" + line;
		}
	}

	public static void write(FriendlyByteBuf buf, SourceLine sourceLine) {
		buf.writeUtf(sourceLine.source);
		buf.writeVarInt(sourceLine.line);
	}
}
