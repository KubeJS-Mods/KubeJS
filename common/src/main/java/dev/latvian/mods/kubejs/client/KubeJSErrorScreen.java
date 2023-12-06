package dev.latvian.mods.kubejs.client;

import dev.latvian.mods.kubejs.CommonProperties;
import dev.latvian.mods.kubejs.script.ConsoleLine;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.kubejs.util.LogType;
import dev.latvian.mods.kubejs.util.UtilsJS;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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
import org.jetbrains.annotations.Nullable;

import java.awt.Desktop;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class KubeJSErrorScreen extends Screen {
	public final Screen lastScreen;
	public final ScriptType scriptType;
	public final Path logFile;
	public final List<ConsoleLine> errors;
	public final List<ConsoleLine> warnings;
	//private MultiLineLabel multilineMessage;
	private ErrorList list;

	public KubeJSErrorScreen(Screen lastScreen, ScriptType scriptType, @Nullable Path logFile, List<ConsoleLine> errors, List<ConsoleLine> warnings) {
		super(Component.empty());
		this.lastScreen = lastScreen;
		this.scriptType = scriptType;
		this.logFile = logFile;
		this.errors = errors;
		this.warnings = warnings;
		//this.multilineMessage = MultiLineLabel.EMPTY;
	}

	public KubeJSErrorScreen(Screen lastScreen, ConsoleJS console) {
		this(lastScreen, console.scriptType, console.scriptType.getLogFile(), new ArrayList<>(console.errors), new ArrayList<>(console.warnings));
	}

	@Override
	public Component getNarrationMessage() {
		return Component.literal("There were KubeJS " + scriptType.name + " errors!");
	}

	@Override
	protected void init() {
		super.init();
		this.list = new ErrorList(this, this.minecraft, this.width, this.height, 32, this.height - 32);
		this.addWidget(list);

		/*
		var list = new ArrayList<Component>();
		list.add(Component.literal("There were KubeJS " + scriptType.name + " errors ").append(Component.literal("[" + errors.size() + "]").withStyle(ChatFormatting.DARK_RED)).append("!"));

		var errorStyle = Style.EMPTY.withColor(0xD19893);
		var warningStyle = Style.EMPTY.withColor(0xCEB692);

		for (int i = 0; i < errors.size(); i++) {
			list.add(Component.empty());
			list.add(Component.literal((i + 1) + ") ").withStyle(ChatFormatting.DARK_RED).append(Component.literal(errors.get(i).getText().replace("Error occurred while handling event ", "Error in ").replace("dev.latvian.mods.kubejs.", "...")).withStyle(errorStyle)));
		}

		for (int i = 0; i < warnings.size(); i++) {
			list.add(Component.empty());
			list.add(Component.literal((i + 1) + ") ").withStyle(ChatFormatting.GOLD).append(Component.literal(warnings.get(i).getText().replace("Error occurred while handling event ", "Error in ").replace("dev.latvian.mods.kubejs.", "...")).withStyle(warningStyle)));
		}
		 */

		// this.multilineMessage = MultiLineLabel.create(this.font, CommonComponents.joinLines(list), this.width - 12);
		// this.multilineMessage = MultiLineLabel.create(this.font, CommonComponents.joinLines(Component.literal("Hi")), this.width - 12);
		int i = this.height - 26;

		Button openLog;

		if (CommonProperties.get().startupErrorReportUrl.isBlank()) {
			openLog = this.addRenderableWidget(Button.builder(Component.literal("Open Log File"), this::openLog).bounds(this.width / 2 - 155, i, 150, 20).build());
			this.addRenderableWidget(Button.builder(Component.literal(scriptType.isStartup() ? "Quit" : "Close"), this::quit).bounds(this.width / 2 - 155 + 160, i, 150, 20).build());
		} else {
			openLog = this.addRenderableWidget(Button.builder(Component.literal("Open Log File"), this::openLog).bounds(this.width / 4 - 55, i, 100, 20).build());
			this.addRenderableWidget(Button.builder(Component.literal("Report"), this::report).bounds(this.width / 2 - 50, i, 100, 20).build());
			this.addRenderableWidget(Button.builder(Component.literal(scriptType.isStartup() ? "Quit" : "Close"), this::quit).bounds(this.width * 3 / 4 - 45, i, 100, 20).build());
		}

		openLog.active = logFile != null;
	}

	private void quit(Button button) {
		if (scriptType.isStartup()) {
			minecraft.stop();
		} else {
			onClose();
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

	@Override
	public void render(GuiGraphics guiGraphics, int mx, int my, float delta) {
		this.renderBackground(guiGraphics);
		this.list.render(guiGraphics, mx, my, delta);
		guiGraphics.drawCenteredString(this.font, "KubeJS " + scriptType.name + " script errors", this.width / 2, 12, 0xFFFFFF);
		super.render(guiGraphics, mx, my, delta);
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return !scriptType.isStartup();
	}

	@Override
	public void onClose() {
		minecraft.setScreen(lastScreen);
	}

	public static class ErrorList extends ObjectSelectionList<Entry> {
		public final KubeJSErrorScreen screen;

		public ErrorList(KubeJSErrorScreen screen, Minecraft minecraft, int x1, int height, int y0, int y1) {
			super(minecraft, x1, height, y0, y1, 48);
			this.screen = screen;

			setRenderBackground(false);

			var calendar = Calendar.getInstance();

			for (int i = 0; i < screen.errors.size(); i++) {
				addEntry(new KubeJSErrorScreen.Entry(this, minecraft, i, screen.errors.get(i), calendar));
			}

			for (int i = 0; i < screen.warnings.size(); i++) {
				addEntry(new KubeJSErrorScreen.Entry(this, minecraft, i, screen.warnings.get(i), calendar));
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

	@Environment(EnvType.CLIENT)
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

			if (line.source.isEmpty()) {
				if (line.externalFile != null) {
					this.scriptLineText = Component.literal(line.externalFile.getFileName().toString()).getVisualOrderText();
				} else if (line.line == 0) {
					this.scriptLineText = Component.literal("Internal Error").getVisualOrderText();
				} else {
					this.scriptLineText = Component.literal("<unknown source>#" + line.line).getVisualOrderText();
				}
			} else {
				this.scriptLineText = Component.literal(line.source + "#" + line.line).getVisualOrderText();
			}

			var sb = new StringBuilder();
			calendar.setTimeInMillis(line.timestamp);
			UtilsJS.appendTimestamp(sb, calendar);
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
			var path = line.externalFile == null ? (line.source.isEmpty() ? null : line.console.scriptType.path.resolve(line.source)) : line.externalFile;

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
