package dev.latvian.mods.kubejs.script;

import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.kubejs.util.LogType;
import net.minecraft.network.FriendlyByteBuf;

import java.nio.file.Path;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ConsoleLine {
	public record SourceLine(String source, int line) {
		public SourceLine(FriendlyByteBuf buf) {
			this(buf.readUtf(), buf.readVarInt());
		}

		@Override
		public String toString() {
			if (source.isEmpty() && line == 0) {
				return "Internal Error";
			} else if (source.isEmpty()) {
				return "<unknown source>#" + line;
			} else if (line == 0) {
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

	public static final ConsoleLine[] EMPTY_ARRAY = new ConsoleLine[0];

	public final ConsoleJS console;
	public final long timestamp;
	public String message;
	public LogType type = LogType.INFO;
	public String group = "";
	public Collection<SourceLine> sourceLines = Set.of();
	public Path externalFile = null;
	public List<String> stackTrace = List.of();
	private String cachedText;

	public ConsoleLine(ConsoleJS console, long timestamp, String message) {
		this.console = console;
		this.timestamp = timestamp;
		this.message = message;
	}

	public ConsoleLine(FriendlyByteBuf buf) {
		this.console = ScriptType.VALUES[buf.readByte()].console;
		this.timestamp = buf.readLong();
		this.message = buf.readUtf();
		this.type = LogType.VALUES[buf.readByte()];
		this.group = "";
		this.sourceLines = buf.readList(SourceLine::new);
		this.stackTrace = buf.readList(FriendlyByteBuf::readUtf);
	}

	public static void writeToNet(FriendlyByteBuf buf, ConsoleLine line) {
		buf.writeByte(line.console.scriptType.ordinal());
		buf.writeLong(line.timestamp);
		buf.writeUtf(line.message);
		buf.writeByte(line.type.ordinal());
		buf.writeCollection(line.sourceLines, SourceLine::write);
		buf.writeCollection(line.stackTrace, FriendlyByteBuf::writeUtf);
	}

	public String getText() {
		if (cachedText == null) {
			var builder = new StringBuilder();

			if (!sourceLines.isEmpty()) {
				builder.append(sourceLines.iterator().next());
				builder.append(": ");
			}

			if (!group.isEmpty()) {
				builder.append(group);
			}

			builder.append(message);
			cachedText = builder.toString();
		}

		return cachedText;
	}

	public ConsoleLine withSourceLine(String source, int line) {
		if (source == null) {
			source = "";
		}

		if (!source.isEmpty() && source.startsWith(console.scriptType.nameStrip)) {
			source = source.substring(console.scriptType.nameStrip.length());
		}

		if (line < 0) {
			line = 0;
		}

		if (sourceLines.isEmpty()) {
			sourceLines = Set.of(new SourceLine(source, line));
			return this;
		} else if (sourceLines.size() == 1) {
			var line0 = sourceLines.iterator().next();
			sourceLines = new LinkedHashSet<>();
			sourceLines.add(line0);
		}

		sourceLines.add(new SourceLine(source, line));
		return this;
	}

	public ConsoleLine withExternalFile(Path path) {
		externalFile = path;
		sourceLines = Set.of(new SourceLine(path.getFileName().toString(), 0));
		return this;
	}

	@Override
	public String toString() {
		return getText();
	}
}
