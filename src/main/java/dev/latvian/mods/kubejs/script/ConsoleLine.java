package dev.latvian.mods.kubejs.script;

import dev.latvian.mods.kubejs.util.LogType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.nio.file.Path;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ConsoleLine {
	public static final ConsoleLine[] EMPTY_ARRAY = new ConsoleLine[0];

	public static final StreamCodec<FriendlyByteBuf, ConsoleLine> STREAM_CODEC = new StreamCodec<>() {
		@Override
		public ConsoleLine decode(FriendlyByteBuf buf) {
			var console = ScriptType.VALUES[buf.readByte()].console;
			var timestamp = buf.readVarLong();
			var message = buf.readUtf();
			var line = new ConsoleLine(console, timestamp, message);
			line.type = LogType.VALUES[buf.readByte()];
			line.group = "";
			line.sourceLines = buf.readList(SourceLine::read);
			line.stackTrace = buf.readList(FriendlyByteBuf::readUtf);
			return line;
		}

		@Override
		public void encode(FriendlyByteBuf buf, ConsoleLine line) {
			buf.writeByte(line.console.scriptType.ordinal());
			buf.writeVarLong(line.timestamp);
			buf.writeUtf(line.message);
			buf.writeByte(line.type.ordinal());
			buf.writeCollection(line.sourceLines, SourceLine::write);
			buf.writeCollection(line.stackTrace, FriendlyByteBuf::writeUtf);
		}
	};

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

	public String getText() {
		if (cachedText == null) {
			var builder = new StringBuilder();

			if (!sourceLines.isEmpty()) {
				for (var line : sourceLines) {
					if (!line.isUnknown()) {
						builder.append(line.source()).append('#').append(line.line()).append(':').append(' ');
						break;
					}
				}
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

		return source.isEmpty() && line == 0 ? this : withSourceLine(new SourceLine(source, line));
	}

	public ConsoleLine withSourceLine(SourceLine sourceLine) {
		if (sourceLine.isUnknown()) {
			return this;
		}

		if (sourceLines.isEmpty()) {
			sourceLines = Set.of(sourceLine);
			return this;
		} else if (sourceLines.size() == 1) {
			var line0 = sourceLines.iterator().next();
			sourceLines = new LinkedHashSet<>();
			sourceLines.add(line0);
		}

		sourceLines.add(sourceLine);
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
