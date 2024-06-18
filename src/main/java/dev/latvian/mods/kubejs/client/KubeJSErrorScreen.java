package dev.latvian.mods.kubejs.client;

import dev.latvian.mods.kubejs.CommonProperties;
import dev.latvian.mods.kubejs.script.ConsoleLine;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.kubejs.util.LogType;
import dev.latvian.mods.kubejs.util.TimeJS;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.navigation.CommonInputs;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.awt.Desktop;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class KubeJSErrorScreen extends Screen {
	public final Screen lastScreen;
	public final ScriptType scriptType;
	public final Path logFile;
	public final List<ConsoleLine> errors;
	public final List<ConsoleLine> warnings;
	public final boolean canClose;
	public List<ConsoleLine> viewing;
	private ErrorList list;

	public KubeJSErrorScreen(Screen lastScreen, ScriptType scriptType, @Nullable Path logFile, List<ConsoleLine> errors, List<ConsoleLine> warnings, boolean canClose) {
		super(Component.empty());
		this.lastScreen = lastScreen;
		this.scriptType = scriptType;
		this.logFile = logFile;
		this.errors = errors;
		this.warnings = warnings;
		this.canClose = canClose;

		this.viewing = errors.isEmpty() && !warnings.isEmpty() ? warnings : errors;
	}

	public KubeJSErrorScreen(Screen lastScreen, ConsoleJS console, boolean canClose) {
		this(lastScreen, console.scriptType, console.scriptType.getLogFile(), new ArrayList<>(console.errors), new ArrayList<>(console.warnings), canClose);
	}

	@Override
	public Component getNarrationMessage() {
		return Component.literal("There were KubeJS " + scriptType.name + " errors!");
	}

	@Override
	protected void init() {
		super.init();
		this.list = new ErrorList(this, this.minecraft, this.width, this.height, 32, this.height - 32, viewing);
		this.addWidget(list);

		int i = this.height - 26;

		Button openLog;

		if (CommonProperties.get().startupErrorReportUrl.isBlank()) {
			openLog = this.addRenderableWidget(Button.builder(Component.literal("Open Log File"), this::openLog).bounds(this.width / 2 - 155, i, 150, 20).build());
			this.addRenderableWidget(Button.builder(Component.literal(canClose ? "Close" : "Quit"), this::quit).bounds(this.width / 2 - 155 + 160, i, 150, 20).build());
		} else {
			openLog = this.addRenderableWidget(Button.builder(Component.literal("Open Log File"), this::openLog).bounds(this.width / 4 - 55, i, 100, 20).build());
			this.addRenderableWidget(Button.builder(Component.literal("Report"), this::report).bounds(this.width / 2 - 50, i, 100, 20).build());
			this.addRenderableWidget(Button.builder(Component.literal(canClose ? "Close" : "Quit"), this::quit).bounds(this.width * 3 / 4 - 45, i, 100, 20).build());
		}

		openLog.active = logFile != null;

		var viewOther = this.addRenderableWidget(Button.builder(Component.literal((viewing == errors) ? ("View Warnings [" + warnings.size() + "]") : ("View Errors [" + errors.size() + "]")), this::viewOther).bounds(this.width - 107, 7, 100, 20).build());

		if (errors.isEmpty() || warnings.isEmpty()) {
			viewOther.active = false;
		}
	}

	private void quit(Button button) {
		if (canClose) {
			onClose();
		} else {
			minecraft.stop();
		}
	}

	private void report(Button button) {
		handleComponentClicked(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, CommonProperties.get().startupErrorReportUrl)));
	}

	private void openLog(Button button) {
		if (logFile != null) {
			handleComponentClicked(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, logFile.toAbsolutePath().toString())));
		}
	}

	private void viewOther(Button button) {
		viewing = viewing == errors ? warnings : errors;
		repositionElements();
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mx, int my, float delta) {
		// this.renderBackground(guiGraphics, mx, my, delta);
		super.render(guiGraphics, mx, my, delta);
		this.list.render(guiGraphics, mx, my, delta);
		guiGraphics.drawCenteredString(this.font, "KubeJS " + scriptType.name + " script " + (viewing == errors ? "errors" : "warnings"), this.width / 2, 12, 0xFFFFFF);
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return canClose;
	}

	@Override
	public void onClose() {
		minecraft.setScreen(lastScreen);
	}

	public static class ErrorList extends ObjectSelectionList<Entry> {
		public final KubeJSErrorScreen screen;
		public final List<ConsoleLine> lines;

		public ErrorList(KubeJSErrorScreen screen, Minecraft minecraft, int width, int height, int top, int bottom, List<ConsoleLine> lines) {
			super(minecraft, width, bottom - top, top, 48);
			this.screen = screen;
			this.lines = lines;

			// FIXME setRenderBackground(false);

			var calendar = Calendar.getInstance();

			for (int i = 0; i < lines.size(); i++) {
				addEntry(new KubeJSErrorScreen.Entry(this, minecraft, i, lines.get(i), calendar));
			}
		}

		@Override
		public boolean keyPressed(int i, int j, int k) {
			if (CommonInputs.selected(i)) {
				var sel = this.getSelected();
				if (sel != null) {
					sel.open();
					return true;
				}
			}

			return super.keyPressed(i, j, k);
		}

		@Override
		public int getRowWidth() {
			return (int) (this.width * 0.93D);
		}
	}

	@OnlyIn(Dist.CLIENT)
	public static class Entry extends ObjectSelectionList.Entry<Entry> {
		private final ErrorList errorList;
		private final Minecraft minecraft;
		private final ConsoleLine line;
		private long lastClickTime;
		private final FormattedCharSequence indexText;
		private final FormattedCharSequence scriptLineText;
		private final FormattedCharSequence timestampText;
		private final List<FormattedCharSequence> errorText;
		private final List<FormattedCharSequence> stackTraceText;

		public Entry(ErrorList errorList, Minecraft minecraft, int index, ConsoleLine line, Calendar calendar) {
			this.errorList = errorList;
			this.minecraft = minecraft;
			this.line = line;

			this.indexText = Component.literal("#" + (index + 1)).getVisualOrderText();

			var sourceLines = new ArrayList<>(line.sourceLines);
			Collections.reverse(sourceLines);
			this.scriptLineText = Component.literal(sourceLines.stream().map(Object::toString).map(s -> s.isEmpty() ? (this.line.type == LogType.WARN ? "Internal Warning" : "Internal Error") : s).collect(Collectors.joining(" -> "))).getVisualOrderText();

			var sb = new StringBuilder();
			calendar.setTimeInMillis(line.timestamp);
			TimeJS.appendTimestamp(sb, calendar);
			this.timestampText = Component.literal(sb.toString()).getVisualOrderText();

			this.errorText = new ArrayList<>(minecraft.font.split(Component.literal(line.message), errorList.getRowWidth()).stream().limit(3L).toList());
			this.stackTraceText = line.stackTrace.isEmpty() ? List.of() : minecraft.font.split(Component.literal(String.join("\n", line.stackTrace)).setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)), Integer.MAX_VALUE);
		}

		@Override
		public Component getNarration() {
			return Component.empty();
		}

		@Override
		public void render(GuiGraphics g, int idx, int y, int x, int w, int h, int mx, int my, boolean hovered, float delta) {
			var col = line.type == LogType.ERROR ? 0xFF5B63 : 0xFFBB5B;

			g.drawString(minecraft.font, indexText, x + 1, y + 1, col);
			g.drawCenteredString(minecraft.font, scriptLineText, x + w / 2, y + 1, 0xFFFFFF);
			g.drawString(minecraft.font, timestampText, x + w - minecraft.font.width(timestampText) - 4, y + 1, 0x666666);

			for (int i = 0; i < errorText.size(); i++) {
				g.drawString(minecraft.font, errorText.get(i), x + 1, y + 13 + i * 10, col);
			}

			if (hovered && !stackTraceText.isEmpty()) {
				errorList.screen.setTooltipForNextRenderPass(Screen.hasShiftDown() ? stackTraceText : stackTraceText.stream().limit(4L).toList());
			}
		}

		@Override
		public boolean mouseClicked(double d, double e, int i) {
			errorList.setSelected(this);

			if (Util.getMillis() - this.lastClickTime < 250L) {
				if (i == 1) {
					minecraft.keyboardHandler.setClipboard(String.join("\n", line.stackTrace));
				} else {
					open();
				}
				return true;
			} else {
				this.lastClickTime = Util.getMillis();
				return true;
			}
		}

		public void open() {
			var path = line.externalFile == null ? (line.sourceLines.isEmpty() || line.sourceLines.iterator().next().source().isEmpty() ? null : line.console.scriptType.path.resolve(line.sourceLines.iterator().next().source())) : line.externalFile;

			if (path != null && Files.exists(path)) {
				try {
					if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE_FILE_DIR)) {
						Desktop.getDesktop().browseFileDirectory(path.toFile());
					} else {
						throw new IllegalStateException("Error");
					}
				} catch (Exception ignored) {
					if (Files.isRegularFile(path) && !path.getFileName().toString().endsWith(".js")) {
						path = path.getParent();
					}

					errorList.screen.handleComponentClicked(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, path.toAbsolutePath().toString())));
				}
			}
		}
	}
}
