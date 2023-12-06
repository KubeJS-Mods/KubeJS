package dev.latvian.mods.kubejs.script;

import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.kubejs.util.LogType;
import net.minecraft.network.FriendlyByteBuf;

import java.nio.file.Path;
import java.util.List;

public class ConsoleLine {
	public static final ConsoleLine[] EMPTY_ARRAY = new ConsoleLine[0];

	public final ConsoleJS console;
	public final long timestamp;
	public String message;
	public LogType type = LogType.INFO;
	public String group = "";
	public String source = "";
	public int line = 0;
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
		this.source = buf.readUtf();
		this.line = buf.readVarInt();
		this.stackTrace = buf.readList(FriendlyByteBuf::readUtf);
	}

	public static void writeToNet(FriendlyByteBuf buf, ConsoleLine line) {
		buf.writeByte(line.console.scriptType.ordinal());
		buf.writeLong(line.timestamp);
		buf.writeUtf(line.message);
		buf.writeByte(line.type.ordinal());
		buf.writeUtf(line.source);
		buf.writeVarInt(line.line);
		buf.writeCollection(line.stackTrace, FriendlyByteBuf::writeUtf);
	}

	public String getText() {
		if (cachedText == null) {
			var builder = new StringBuilder();

			if (line > 0) {
				if (!source.isEmpty()) {
					builder.append(source);
					builder.append('#');
				} else {
					builder.append("<unknown source>#");
				}

				builder.append(line);
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

	public ConsoleLine withExternalFile(Path path) {
		externalFile = path;
		return this;
	}

	@Override
	public String toString() {
		return getText();
	}
}
