package dev.latvian.mods.kubejs.client;

import dev.latvian.mods.betteradvancedtooltips.BATIcons;
import dev.latvian.mods.kubejs.CommonProperties;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.TextIcons;
import dev.latvian.mods.kubejs.script.ConsoleJS;
import dev.latvian.mods.kubejs.script.ConsoleLine;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.script.SourceLine;
import dev.latvian.mods.kubejs.util.LogType;
import dev.latvian.mods.kubejs.util.TimeJS;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import org.jspecify.annotations.Nullable;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@SuppressWarnings("NotNullFieldNotInitialized") // lateinit fields
public class KubeJSErrorScreen extends Screen {
	public final @Nullable Screen lastScreen;
	public final ScriptType scriptType;
	public final @Nullable Path logFile;
	public final List<ConsoleLine> errors;
	public final List<ConsoleLine> warnings;
	public final boolean canClose;
	public List<ConsoleLine> viewing;
	private ErrorList list;
	private List<FormattedCharSequence> tooltip;
	private boolean tooltipShowReportingDisclaimer;
	private boolean tooltipCenterAligned;
	private float tooltipScale;
	private @Nullable List<FormattedCharSequence> renderedTooltip;
	private boolean renderedTooltipShowReportingDisclaimer;
	private boolean renderedTooltipCenterAligned;
	private float renderedTooltipScale = 1F;
	private float tooltipFadeProgress;
	private long copyToastStartedAt = -1L;
	private Component copyToastText = COPY_TOAST_TEXT;
	private int copyToastX;
	private int copyToastY;
	private int copyStackTraceAreaX1;
	private int copyStackTraceAreaY1;
	private int copyStackTraceAreaX2;
	private int copyStackTraceAreaY2;
	private int copyStackTraceBoxX1;
	private int copyStackTraceBoxY1;
	private int copyStackTraceBoxX2;
	private int copyStackTraceBoxY2;
	private int copyStackTraceLabelCenterX;
	private int copyStackTraceLabelY1;
	private int copyStackTraceLabelY2;
	private long footerHintTooltipUntil = -1L;
	private float footerHintHoverProgress;
	private static final float HEADER_LABEL_SCALE = 0.75F;
	private static final float HEADER_ICON_SCALE = 1.34F;
	private static final int HEADER_ICON_GAP = 4;
	private static final float ICON_HOVER_LOOP_SECONDS = 4.8F;
	private static final float MINI_TOOLTIP_SCALE = 1F / 1.15F;
	private static final int MINI_TOOLTIP_CHARS_PER_LINE = 20;
	private static final int ICON_TEXT_GAP = 4;
	private static final int TIMESTAMP_BUTTON_GAP = 5;
	private static final float PANEL_TEXT_HOVER_SCALE = 1.06F;
	private static final int TOOLTIP_PADDING = 6;
	private static final int DISCLAIMER_ICON_WIDTH = 18;
	private static final long COPY_TOAST_DURATION_MS = 1100L;
	private static final long FOOTER_HINT_TOOLTIP_DURATION_MS = 2200L;
	private static final float SCREEN_CLICK_PITCH = 0.85F;
	private static final float FOOTER_HINT_SCALE = 0.7F;
	private static final float DISCLAIMER_TEXT_SCALE = 0.9F;
	private static final int FOOTER_HINT_LINE_GAP = 8;
	private static final int FLAT_BUTTON_FILL = 0xFF3A3A3A;
	private static final int FLAT_BUTTON_FILL_HOVERED = 0xFF4A4A4A;
	private static final int FLAT_BUTTON_TOP = 0xFF5A5A5A;
	private static final int FLAT_BUTTON_TOP_HOVERED = 0xFF7A7A7A;
	private static final int FLAT_BUTTON_BOTTOM = 0xFF1F1F1F;
	private static final int FLAT_BUTTON_FILL_DISABLED = 0xFF303030;
	private static final int FLAT_BUTTON_TOP_DISABLED = 0xFF454545;
	private static final int FLAT_BUTTON_TEXT_DISABLED = 0xFFA0A0A0;
	private static final Component COPY_TOAST_TEXT = Component.literal("Copied to Clipboard!").withColor(0xFF9FFFC6);
	private static final Component FOOTER_HINT_LINE_1 = Component.literal("Hold ").append(Component.literal("Shift").setStyle(Style.EMPTY.withBold(true).withUnderlined(true)));
	private static final Component FOOTER_HINT_LINE_2 = Component.literal("for details");

	public KubeJSErrorScreen(@Nullable Screen lastScreen, ScriptType scriptType, @Nullable Path logFile, List<ConsoleLine> errors, List<ConsoleLine> warnings, boolean canClose) {
		super(Component.empty());
		this.lastScreen = lastScreen;
		this.scriptType = scriptType;
		this.logFile = logFile;
		this.errors = errors;
		this.warnings = warnings;
		this.canClose = canClose;

		this.viewing = errors.isEmpty() && !warnings.isEmpty() ? warnings : errors;
	}

	public KubeJSErrorScreen(@Nullable Screen lastScreen, ConsoleJS console, boolean canClose) {
		this(lastScreen, console.scriptType, console.scriptType.getLogFile(), new ArrayList<>(console.errors), new ArrayList<>(console.warnings), canClose);
	}

	public void setTooltip(List<FormattedCharSequence> tooltip, boolean showReportingDisclaimer, float scale, boolean centerAligned) {
		this.tooltip = tooltip;
		this.tooltipShowReportingDisclaimer = showReportingDisclaimer;
		this.tooltipScale = scale;
		this.tooltipCenterAligned = centerAligned;
	}

	private void setCompactTooltip(Component... components) {
		setTooltip(buildCompactTooltip(components), false, MINI_TOOLTIP_SCALE, true);
	}

	@Override
	public Component getNarrationMessage() {
		return Component.literal("There were " + getHeaderTitleText().toLowerCase() + "!");
	}

	@Override
	protected void init() {
		super.init();
		int topControlWidth = Math.max(
			96,
			Math.max(
				this.font.width("View Warnings [" + warnings.size() + "]"),
				this.font.width("View Errors [" + errors.size() + "]")
			) + this.font.width(TextIcons.warn()) + ICON_TEXT_GAP + 12
		);
		int buttonsY = this.height - 26;
		this.list = new ErrorList(this, this.minecraft, this.width, this.height, 32, this.height - 32, viewing);
		this.addWidget(list);
		int checkboxSize = 12;
		this.copyStackTraceAreaX1 = this.width - topControlWidth - 7;
		this.copyStackTraceAreaY1 = 7;
		this.copyStackTraceAreaX2 = this.width - 7;
		this.copyStackTraceAreaY2 = 27;
		this.copyStackTraceBoxX2 = copyStackTraceAreaX1 + 16;
		this.copyStackTraceBoxX1 = copyStackTraceBoxX2 - checkboxSize;
		this.copyStackTraceBoxY1 = copyStackTraceAreaY1 + 4;
		this.copyStackTraceBoxY2 = copyStackTraceBoxY1 + checkboxSize;
		this.copyStackTraceLabelCenterX = (copyStackTraceBoxX2 + copyStackTraceAreaX2) / 2 - 5;
		this.copyStackTraceLabelY1 = copyStackTraceAreaY1 + 4;
		this.copyStackTraceLabelY2 = copyStackTraceAreaY1 + 11;

		Button openLog;
		var openLogText = Component.literal("Open Log File");
		var closeText = Component.literal(canClose ? "Close" : "Quit");
		Component openLogTooltipTitle = Component.literal("Open the KubeJS log file").withStyle(Style.EMPTY.withColor(ChatFormatting.AQUA));
		Component openLogTooltipPath = logFile == null ? null : Component.literal(logFile.toAbsolutePath().toString()).withStyle(Style.EMPTY.withColor(ChatFormatting.GRAY));

		if (CommonProperties.get().startupErrorReportUrl.isBlank()) {
			openLog = this.addRenderableWidget(Button.builder(openLogText, this::openLog).bounds(this.width / 2 - 145, buttonsY, 140, 20).build(builder -> new IconTextButton(this, builder, BATIcons.INFO.copy(), 0xFF66C9FF, openLogTooltipTitle, openLogTooltipPath)));
			this.addRenderableWidget(Button.builder(closeText, this::quit).bounds(this.width / 2 + 5, buttonsY, 140, 20).build(KubeJSErrorButton::new));
		} else {
			openLog = this.addRenderableWidget(Button.builder(openLogText, this::openLog).bounds(this.width / 4 - 50, buttonsY, 95, 20).build(builder -> new IconTextButton(this, builder, BATIcons.INFO.copy(), 0xFF66C9FF, openLogTooltipTitle, openLogTooltipPath)));
			this.addRenderableWidget(Button.builder(Component.literal("Report"), this::report).bounds(this.width / 2 - 50, buttonsY, 100, 20).build(KubeJSErrorButton::new));
			this.addRenderableWidget(Button.builder(closeText, this::quit).bounds(this.width * 3 / 4 - 40, buttonsY, 95, 20).build(KubeJSErrorButton::new));
		}

		openLog.active = logFile != null;

		Component viewOtherIcon = viewing == errors ? TextIcons.warn() : TextIcons.error();
		int viewOtherGlowColor = viewing == errors ? 0xFFFFC86A : 0xFFFF6E76;
		var viewOther = this.addRenderableWidget(Button.builder(Component.literal((viewing == errors) ? ("View Warnings [" + warnings.size() + "]") : ("View Errors [" + errors.size() + "]")), this::viewOther).bounds(7, 7, topControlWidth, 20).build(builder -> new IconTextButton(this, builder, viewOtherIcon, viewOtherGlowColor)));

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
		try {
			Util.getPlatform().openUri(URI.create(CommonProperties.get().startupErrorReportUrl));
		} catch (Exception ignored) {
		}
	}

	private void openLog(Button button) {
		if (logFile != null) {
			try {
				Util.getPlatform().openFile(logFile.toAbsolutePath().toFile());
			} catch (Exception ignored) {
			}
		}
	}

	private void toggleCopyStackTrace(boolean value) {
		var properties = ClientProperties.get();
		properties.setIncludeStackTraceWhenCopyingErrors(value);
	}

	private void playClickSound() {
		playScreenClickSound(minecraft.getSoundManager());
	}

	private static void playScreenClickSound(SoundManager soundManager) {
		soundManager.play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK.value(), 1F, SCREEN_CLICK_PITCH));
	}

	private void viewOther(Button button) {
		viewing = viewing == errors ? warnings : errors;
		repositionElements();
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor graphics, int mx, int my, float delta) {
		tooltip = null;
		tooltipShowReportingDisclaimer = false;
		tooltipCenterAligned = false;
		tooltipScale = 1F;

		super.extractRenderState(graphics, mx, my, delta);
		renderHeader(graphics);
		list.extractRenderState(graphics, mx, my, delta);
		boolean checkboxHovered = isMouseOverCopyStackTraceToggle(mx, my);
		int areaFill = checkboxHovered ? 0xFF4A4A4A : 0xFF3A3A3A;
		int areaTop = checkboxHovered ? 0xFF7A7A7A : 0xFF5A5A5A;
		int areaBottom = 0xFF1F1F1F;
		int checkboxBorder = checkboxHovered ? 0xFFB8C2CC : 0xFF8A8F99;
		int checkboxFill = 0xCC111317;
		graphics.fill(copyStackTraceAreaX1, copyStackTraceAreaY1, copyStackTraceAreaX2, copyStackTraceAreaY2, areaFill);
		graphics.fill(copyStackTraceAreaX1, copyStackTraceAreaY1, copyStackTraceAreaX2, copyStackTraceAreaY1 + 1, areaTop);
		graphics.fill(copyStackTraceAreaX1, copyStackTraceAreaY2 - 1, copyStackTraceAreaX2, copyStackTraceAreaY2, areaBottom);
		graphics.fill(copyStackTraceAreaX1, copyStackTraceAreaY1, copyStackTraceAreaX1 + 1, copyStackTraceAreaY2, areaTop);
		graphics.fill(copyStackTraceAreaX2 - 1, copyStackTraceAreaY1, copyStackTraceAreaX2, copyStackTraceAreaY2, areaBottom);
		renderScaledCenteredText(graphics, font, Component.literal("Include"), copyStackTraceLabelCenterX, copyStackTraceLabelY1, checkboxHovered ? 0xFFFFFFFF : 0xFFE0E0E0, HEADER_LABEL_SCALE);
		renderScaledCenteredText(graphics, font, Component.literal("Stacktrace"), copyStackTraceLabelCenterX, copyStackTraceLabelY2, checkboxHovered ? 0xFFFFFFFF : 0xFFE0E0E0, HEADER_LABEL_SCALE);
		graphics.fill(copyStackTraceBoxX1, copyStackTraceBoxY1, copyStackTraceBoxX2, copyStackTraceBoxY2, checkboxFill);
		graphics.fill(copyStackTraceBoxX1, copyStackTraceBoxY1, copyStackTraceBoxX2, copyStackTraceBoxY1 + 1, checkboxBorder);
		graphics.fill(copyStackTraceBoxX1, copyStackTraceBoxY2 - 1, copyStackTraceBoxX2, copyStackTraceBoxY2, checkboxBorder);
		graphics.fill(copyStackTraceBoxX1, copyStackTraceBoxY1, copyStackTraceBoxX1 + 1, copyStackTraceBoxY2, checkboxBorder);
		graphics.fill(copyStackTraceBoxX2 - 1, copyStackTraceBoxY1, copyStackTraceBoxX2, copyStackTraceBoxY2, checkboxBorder);

		if (ClientProperties.get().includeStackTraceWhenCopyingErrors) {
			graphics.pose().pushMatrix();
			graphics.pose().translate(copyStackTraceBoxX1 + 2.5F, copyStackTraceBoxY1 + 2F);
			graphics.text(font, BATIcons.YES.copy().withColor(0xFF7BE495), 0, 0, 0xFF7BE495);
			graphics.pose().popMatrix();
		}


		if (errors.isEmpty() && warnings.isEmpty()) {
			graphics.centeredText(font, "No errors or warnings found!", width / 2, height / 2 - 6, 0xFF66FF66);
		} else {
			boolean footerHintHovered = isMouseOverFooterHint(mx, my);
			footerHintHoverProgress = approachHoverProgress(footerHintHoverProgress, footerHintHovered);
			float footerHintScale = FOOTER_HINT_SCALE + 0.05F * easeOutCubicStatic(footerHintHoverProgress);
			int hintColor = withAlphaColor(0xC9CFD6, Mth.floor(136F + 64F * footerHintHoverProgress));
			int hintX = getFooterHintX();
			int hintY = getFooterHintY();
			renderScaledText(graphics, font, FOOTER_HINT_LINE_1, hintX, hintY, hintColor, footerHintScale);
			renderScaledText(graphics, font, FOOTER_HINT_LINE_2, hintX, hintY + FOOTER_HINT_LINE_GAP, hintColor, footerHintScale);
		}

		if (tooltip == null && checkboxHovered && Minecraft.getInstance().hasShiftDown()) {
			setCompactTooltip(Component.literal("Include the full stack trace when copying an error"));
		}

		if (tooltip == null && (footerHintTooltipUntil > Util.getMillis() || (Minecraft.getInstance().hasShiftDown() && isMouseOverFooterHint(mx, my)))) {
			setCompactTooltip(
				Component.literal("Hold Shift while hovering over"),
				Component.literal(getFooterHintContextLabel() + " or buttons for extra info")
			);
		}

		syncRenderedTooltip();

		if (renderedTooltip != null && tooltipFadeProgress > 0.01F) {
			renderTooltip(graphics, mx, my, renderedTooltip, renderedTooltipShowReportingDisclaimer, renderedTooltipScale, renderedTooltipCenterAligned, easeOutCubicStatic(tooltipFadeProgress));
		}

		renderCopyToast(graphics);

	}


	@Override
	public boolean shouldCloseOnEsc() {
		return canClose;
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		if (isMouseOverFooterHint(event.x(), event.y())) {
			playClickSound();
			footerHintTooltipUntil = Util.getMillis() + FOOTER_HINT_TOOLTIP_DURATION_MS;
			return true;
		}

		if (isMouseOverCopyStackTraceToggle(event.x(), event.y())) {
			playClickSound();
			toggleCopyStackTrace(!ClientProperties.get().includeStackTraceWhenCopyingErrors);
			return true;
		}

		if (list != null && !list.isOverAnyEntryPanel(event.x(), event.y())) {
			list.setSelected(null);
		}

		return super.mouseClicked(event, doubleClick);
	}

	@Override
	public void onClose() {
		minecraft.setScreen(lastScreen);
	}

	private boolean isMouseOverCopyStackTraceToggle(double mouseX, double mouseY) {
		return mouseX >= copyStackTraceAreaX1
			&& mouseX < copyStackTraceAreaX2
			&& mouseY >= copyStackTraceAreaY1
			&& mouseY < copyStackTraceAreaY2;
	}

	private int getFooterHintX() {
		return 8;
	}

	private int getFooterHintY() {
		return this.height - 23;
	}

	private String getFooterHintContextLabel() {
		return viewing == warnings ? "warnings" : "errors";
	}

	private Component getDisclaimerLine1() {
		return Component.literal("Do not screenshot this window when reporting script " + getFooterHintContextLabel()).withColor(0xFFF5D08A);
	}

	private Component getDisclaimerLine2() {
		return Component.literal("Copy and paste the " + (viewing == warnings ? "warning" : "error") + " message instead").withColor(0xFFFFFFFF);
	}

	private boolean isMouseOverFooterHint(double mouseX, double mouseY) {
		int x1 = getFooterHintX();
		int y1 = getFooterHintY();
		int width = Mth.ceil(Math.max(font.width(FOOTER_HINT_LINE_1), font.width(FOOTER_HINT_LINE_2)) * (FOOTER_HINT_SCALE + 0.05F));
		int height = FOOTER_HINT_LINE_GAP + Mth.ceil(font.lineHeight * (FOOTER_HINT_SCALE + 0.05F));
		return mouseX >= x1 && mouseX < x1 + width && mouseY >= y1 && mouseY < y1 + height;
	}

	private void renderScaledText(GuiGraphicsExtractor graphics, Font font, Component text, int x, int y, int color, float scale) {
		graphics.pose().pushMatrix();
		graphics.pose().translate(x, y);
		graphics.pose().scale(scale, scale);
		graphics.text(font, text, 0, 0, color);
		graphics.pose().popMatrix();
	}

	private void renderScaledCenteredText(GuiGraphicsExtractor graphics, Font font, Component text, int centerX, int y, int color, float scale) {
		int scaledWidth = Mth.ceil(font.width(text) * scale);
		renderScaledText(graphics, font, text, centerX - scaledWidth / 2, y, color, scale);
	}

	private void showCopyToast(int x, int y, Component text) {
		copyToastStartedAt = Util.getMillis();
		copyToastText = text;
		copyToastX = x;
		copyToastY = y;
	}

	private void renderHeader(GuiGraphicsExtractor graphics) {
		var title = Component.literal(getHeaderTitleText());
		int iconWidth = Mth.ceil(font.width(TextIcons.logo()) * HEADER_ICON_SCALE);
		int titleWidth = font.width(title);
		int totalWidth = iconWidth + HEADER_ICON_GAP + titleWidth;
		int startX = width / 2 - totalWidth / 2;
		int iconY = 10;
		int textX = startX + iconWidth + HEADER_ICON_GAP;

		graphics.pose().pushMatrix();
		graphics.pose().translate(startX, iconY);
		graphics.pose().scale(HEADER_ICON_SCALE, HEADER_ICON_SCALE);
		graphics.text(font, TextIcons.logo(), 0, 0, 0xFFFFFFFF);
		graphics.pose().popMatrix();
		graphics.text(font, title, textX, 12, 0xFFFFFFFF);
	}

	private String getHeaderTitleText() {
		return "KubeJS " + formatScriptTypeTitle(scriptType) + " Script " + (viewing == errors ? "Errors" : "Warnings");
	}

	private static String formatScriptTypeTitle(ScriptType scriptType) {
		String name = scriptType.name;
		return name.isEmpty() ? "Script" : Character.toUpperCase(name.charAt(0)) + name.substring(1);
	}

	private void syncRenderedTooltip() {
		boolean hasTooltip = tooltip != null && !tooltip.isEmpty();
		tooltipFadeProgress = approachHoverProgress(tooltipFadeProgress, hasTooltip);

		if (hasTooltip) {
			renderedTooltip = List.copyOf(tooltip);
			renderedTooltipShowReportingDisclaimer = tooltipShowReportingDisclaimer;
			renderedTooltipCenterAligned = tooltipCenterAligned;
			renderedTooltipScale = tooltipScale <= 0F ? 1F : tooltipScale;
		} else if (tooltipFadeProgress <= 0.01F) {
			renderedTooltip = null;
			renderedTooltipShowReportingDisclaimer = false;
			renderedTooltipCenterAligned = false;
			renderedTooltipScale = 1F;
		}
	}

	private void renderTooltip(GuiGraphicsExtractor graphics, int mx, int my, List<FormattedCharSequence> tooltipLines, boolean showReportingDisclaimer, float scale, boolean centerAligned, float opacity) {
		int lineHeight = font.lineHeight + 1;
		int tooltipWidth = 0;

		for (var line : tooltipLines) {
			tooltipWidth = Math.max(tooltipWidth, font.width(line));
		}

		int contentWidth = tooltipWidth + TOOLTIP_PADDING * 2;
		int contentHeight = tooltipLines.size() * lineHeight - 1 + TOOLTIP_PADDING * 2;
		int disclaimerHeight = 0;
		int totalWidth = contentWidth;
		Component disclaimerLine1 = getDisclaimerLine1();
		Component disclaimerLine2 = getDisclaimerLine2();

		if (showReportingDisclaimer) {
			int disclaimerTextWidth = Math.max(font.width(disclaimerLine1), font.width(disclaimerLine2));
			totalWidth = Math.max(totalWidth, DISCLAIMER_ICON_WIDTH + disclaimerTextWidth + TOOLTIP_PADDING * 3);
			int scaledLineHeight = Math.max(1, Mth.ceil(font.lineHeight * DISCLAIMER_TEXT_SCALE));
			disclaimerHeight = scaledLineHeight * 2 + TOOLTIP_PADDING * 2 - 1;
		}

		int totalHeight = contentHeight + disclaimerHeight;
		int scaledWidth = Mth.ceil(totalWidth * scale);
		int scaledHeight = Mth.ceil(totalHeight * scale);
		int px = mx + 12;
		int py = my - 12;

		if (px + scaledWidth > graphics.guiWidth()) {
			px = mx - 12 - scaledWidth;
		}
		if (py + scaledHeight + 6 > graphics.guiHeight()) {
			py = graphics.guiHeight() - scaledHeight - 6;
		}
		if (py < 4) {
			py = 4;
		}
		if (px < 4) {
			px = 4;
		}
		if (px + scaledWidth > graphics.guiWidth() - 4) {
			px = graphics.guiWidth() - scaledWidth - 4;
		}

		graphics.pose().pushMatrix();
		graphics.pose().translate(px, py);
		graphics.pose().scale(scale, scale);

		graphics.fill(0, 0, totalWidth, totalHeight, multiplyOpacity(0xF0100010, opacity));
		graphics.fill(0, 0, totalWidth, 1, multiplyOpacity(0x505000FF, opacity));
		graphics.fill(0, totalHeight - 1, totalWidth, totalHeight, multiplyOpacity(0x5028007F, opacity));
		graphics.fill(0, 1, 1, totalHeight - 1, multiplyOpacity(0x505000FF, opacity));
		graphics.fill(totalWidth - 1, 1, totalWidth, totalHeight - 1, multiplyOpacity(0x5028007F, opacity));

		for (int i = 0; i < tooltipLines.size(); i++) {
			int lineX = TOOLTIP_PADDING;
			if (centerAligned) {
				lineX += Math.max(0, (tooltipWidth - font.width(tooltipLines.get(i))) / 2);
			}

			graphics.text(font, tooltipLines.get(i), lineX, TOOLTIP_PADDING + i * lineHeight, multiplyOpacity(0xFFFFFFFF, opacity));
		}

		if (showReportingDisclaimer) {
			int footerY = contentHeight;
			int disclaimerTextWidth = Math.max(
				Mth.ceil(font.width(disclaimerLine1) * DISCLAIMER_TEXT_SCALE),
				Mth.ceil(font.width(disclaimerLine2) * DISCLAIMER_TEXT_SCALE)
			);
			int textX = DISCLAIMER_ICON_WIDTH + TOOLTIP_PADDING * 2 - 3;
			int line1Y = footerY + TOOLTIP_PADDING - 1;
			int scaledLineHeight = Math.max(1, Mth.ceil(font.lineHeight * DISCLAIMER_TEXT_SCALE));
			int line2Y = line1Y + scaledLineHeight;
			int line2Width = Mth.ceil(font.width(disclaimerLine2) * DISCLAIMER_TEXT_SCALE);
			int line2X = textX + Math.max(0, (disclaimerTextWidth - line2Width) / 2);
			int iconX = (DISCLAIMER_ICON_WIDTH - font.width(BATIcons.WARN)) / 2;
			int iconY = footerY + (disclaimerHeight - font.lineHeight) / 2;

			graphics.fill(0, footerY, totalWidth, footerY + disclaimerHeight, multiplyOpacity(0xCC20130B, opacity));
			graphics.fill(0, footerY, totalWidth, footerY + 1, multiplyOpacity(0x80B3742A, opacity));
			graphics.fill(0, footerY, DISCLAIMER_ICON_WIDTH, footerY + disclaimerHeight, multiplyOpacity(0xCC5A2E11, opacity));
			graphics.fill(DISCLAIMER_ICON_WIDTH, footerY + 2, DISCLAIMER_ICON_WIDTH + 1, footerY + disclaimerHeight - 2, multiplyOpacity(0x809B6A2A, opacity));
			graphics.text(font, BATIcons.WARN.copy().withColor(0xFFF5D08A), iconX, iconY, multiplyOpacity(0xFFF5D08A, opacity));
			renderScaledText(graphics, font, disclaimerLine1, textX, line1Y, multiplyOpacity(0xFFF5D08A, opacity), DISCLAIMER_TEXT_SCALE);
			renderScaledText(graphics, font, disclaimerLine2, line2X, line2Y, multiplyOpacity(0xFFFFFFFF, opacity), DISCLAIMER_TEXT_SCALE);
		}

		graphics.pose().popMatrix();
	}

	private List<FormattedCharSequence> buildCompactTooltip(Component... components) {
		var compact = new ArrayList<FormattedCharSequence>();

		for (var component : components) {
			if (component == null) {
				continue;
			}

			String text = component.getString();
			if (text.isBlank()) {
				continue;
			}

			compact.addAll(wrapCompactTooltipText(limitCharactersPerLine(text, MINI_TOOLTIP_CHARS_PER_LINE), component.getStyle(), 180));
		}

		return compact;
	}

	private List<FormattedCharSequence> wrapCompactTooltipText(String text, Style style, int maxWidth) {
		var lines = new ArrayList<FormattedCharSequence>();

		for (String paragraph : text.split("\n", -1)) {
			for (String wrapped : wrapCompactTooltipLine(paragraph, maxWidth)) {
				lines.add(Component.literal(wrapped).setStyle(style).getVisualOrderText());
			}
		}

		return lines;
	}

	private List<String> wrapCompactTooltipLine(String line, int maxWidth) {
		var lines = new ArrayList<String>();
		if (line.isEmpty()) {
			lines.add("");
			return lines;
		}

		int start = 0;

		while (start < line.length()) {
			int end = findCompactTooltipWrapIndex(line, start, maxWidth);
			if (end <= start) {
				end = line.length();
			}

			int trimmedEnd = end;
			while (trimmedEnd > start && line.charAt(trimmedEnd - 1) == ' ') {
				trimmedEnd--;
			}

			lines.add(line.substring(start, trimmedEnd));
			start = end;
			while (start < line.length() && line.charAt(start) == ' ') {
				start++;
			}
		}

		return lines;
	}

	private int findCompactTooltipWrapIndex(String line, int start, int maxWidth) {
		int bestFit = -1;
		int cursor = start;

		while (cursor < line.length()) {
			int next = nextCompactTooltipBoundary(line, cursor);
			int width = font.width(line.substring(start, next));

			if (width <= maxWidth) {
				bestFit = next;
				cursor = next;
				continue;
			}

			return bestFit > start ? bestFit : next;
		}

		return line.length();
	}

	private int nextCompactTooltipBoundary(String line, int start) {
		for (int i = start; i < line.length(); i++) {
			char c = line.charAt(i);
			if (c == ' ' || c == '\\' || c == '/' || c == '>' || c == ']' || c == ')') {
				return i + 1;
			}
		}

		return line.length();
	}

	private void renderCopyToast(GuiGraphicsExtractor graphics) {
		if (copyToastStartedAt < 0L) {
			return;
		}

		float progress = (Util.getMillis() - copyToastStartedAt) / (float) COPY_TOAST_DURATION_MS;
		if (progress >= 1F) {
			copyToastStartedAt = -1L;
			return;
		}

		float alpha = getCopyToastAlpha(progress);
		int textColor = ((int) (alpha * 255F) << 24) | 0x9FFFC6;
		int borderColor = ((int) (alpha * 110F) << 24) | 0x66C98C;
		int backgroundColor = ((int) (alpha * 60F) << 24) | 0x0F1813;
		int rise = Mth.floor(easeOutCubic(progress) * 6F);
		int textWidth = font.width(copyToastText);
		int toastWidth = textWidth + 8;
		int toastHeight = font.lineHeight + 4;
		int toastX = copyToastX - toastWidth / 2;
		int toastY = copyToastY - 16 - rise;

		if (toastY < 4) {
			toastY = Math.min(graphics.guiHeight() - toastHeight - 4, copyToastY + 10);
		}

		toastX = Mth.clamp(toastX, 4, Math.max(4, graphics.guiWidth() - toastWidth - 4));
		toastY = Mth.clamp(toastY, 4, Math.max(4, graphics.guiHeight() - toastHeight - 4));

		graphics.fill(toastX, toastY, toastX + toastWidth, toastY + toastHeight, backgroundColor);
		graphics.fill(toastX, toastY, toastX + toastWidth, toastY + 1, borderColor);
		graphics.fill(toastX, toastY + toastHeight - 1, toastX + toastWidth, toastY + toastHeight, borderColor);
		graphics.fill(toastX, toastY, toastX + 1, toastY + toastHeight, borderColor);
		graphics.fill(toastX + toastWidth - 1, toastY, toastX + toastWidth, toastY + toastHeight, borderColor);
		graphics.text(font, copyToastText, toastX + 4, toastY + 2, textColor);
	}

	private float getCopyToastAlpha(float progress) {
		if (progress <= 0.18F) {
			return easeOutCubic(progress / 0.18F);
		}

		if (progress >= 0.62F) {
			return 1F - easeInCubic((progress - 0.62F) / 0.38F);
		}

		return 1F;
	}

	private float easeOutCubic(float value) {
		float clamped = Mth.clamp(value, 0F, 1F);
		float inverse = 1F - clamped;
		return 1F - inverse * inverse * inverse;
	}

	private float easeInCubic(float value) {
		float clamped = Mth.clamp(value, 0F, 1F);
		return clamped * clamped * clamped;
	}

	private static void renderHoverableIcon(GuiGraphicsExtractor graphics, Font font, Component icon, int x, int y, float hoverProgress, int glowColor) {
		float clamped = Mth.clamp(hoverProgress, 0F, 1F);
		if (clamped <= 0F) {
			graphics.text(font, icon, x, y, 0xFFFFFFFF);
			return;
		}

		float eased = easeOutBack(clamped);
		float scale = 1F + 0.14F * eased;
		float floatProgress = Mth.clamp((clamped - 0.55F) / 0.45F, 0F, 1F);
		float cycle = (Util.getMillis() % Mth.floor(ICON_HOVER_LOOP_SECONDS * 1000F)) / (ICON_HOVER_LOOP_SECONDS * 1000F);
		float floatOffsetY = floatProgress <= 0F ? 0F : getNaturalHoverOffset(cycle) * floatProgress;
		float halfWidth = font.width(icon) / 2F;
		float halfHeight = font.lineHeight / 2F;
		int halo = withAlphaColor(glowColor, Mth.ceil(76F * clamped));
		int shadow = withAlphaColor(0x14202A, Mth.ceil(152F * clamped));
		int highlight = withAlphaColor(0xFFFFFF, Mth.ceil(94F * clamped));

		graphics.pose().pushMatrix();
		graphics.pose().translate(x + halfWidth, y + halfHeight + floatOffsetY);
		graphics.pose().scale(scale, scale);
		graphics.pose().translate(-halfWidth, -halfHeight);

		graphics.pose().pushMatrix();
		graphics.pose().translate(0F, -0.2F);
		graphics.text(font, icon.copy().withColor(glowColor), 0, 0, halo);
		graphics.pose().popMatrix();

		graphics.pose().pushMatrix();
		graphics.pose().translate(0.7F, 0.9F);
		graphics.text(font, icon.copy().withColor(0xFF14202A), 0, 0, shadow);
		graphics.pose().popMatrix();

		graphics.pose().pushMatrix();
		graphics.pose().translate(0F, -0.45F);
		graphics.text(font, icon.copy().withColor(0xFFFFFFFF), 0, 0, highlight);
		graphics.pose().popMatrix();

		graphics.text(font, icon.copy().withColor(0xFFFFFFFF), 0, 0, 0xFFFFFFFF);
		graphics.pose().popMatrix();
	}

	private static void renderHoverableText(GuiGraphicsExtractor graphics, Font font, FormattedCharSequence text, int x, int y, float hoverProgress, int color) {
		float clamped = Mth.clamp(hoverProgress, 0F, 1F);
		if (clamped <= 0F) {
			graphics.text(font, text, x, y, color);
			return;
		}

		float eased = easeOutCubicStatic(clamped);
		float scale = 1F + (PANEL_TEXT_HOVER_SCALE - 1F) * eased;
		float halfHeight = font.lineHeight / 2F;

		graphics.pose().pushMatrix();
		graphics.pose().translate(x, y + halfHeight);
		graphics.pose().scale(scale, scale);
		graphics.pose().translate(0F, -halfHeight);
		graphics.text(font, text, 0, 0, color);
		graphics.pose().popMatrix();
	}

	private static int withAlphaColor(int rgb, int alpha) {
		return (Mth.clamp(alpha, 0, 255) << 24) | (rgb & 0xFFFFFF);
	}

	private static int multiplyOpacity(int argb, float opacity) {
		int alpha = argb >>> 24;
		return (Mth.clamp(Mth.floor(alpha * Mth.clamp(opacity, 0F, 1F)), 0, 255) << 24) | (argb & 0xFFFFFF);
	}

	private static float approachHoverProgress(float current, boolean hovered) {
		float target = hovered ? 1F : 0F;
		float next = Mth.lerp(0.28F, current, target);
		return Math.abs(next - target) < 0.01F ? target : next;
	}

	private static float easeOutBack(float value) {
		float clamped = Mth.clamp(value, 0F, 1F);
		float c1 = 1.70158F;
		float c3 = c1 + 1F;
		float inverse = clamped - 1F;
		return 1F + c3 * inverse * inverse * inverse + c1 * inverse * inverse;
	}

	private static float easeOutCubicStatic(float value) {
		float clamped = Mth.clamp(value, 0F, 1F);
		float inverse = 1F - clamped;
		return 1F - inverse * inverse * inverse;
	}

	private static float getNaturalHoverOffset(float cycle) {
		float clampedCycle = Mth.clamp(cycle, 0F, 1F);
		float phase = clampedCycle * Mth.TWO_PI;
		float primary = -(float) Math.cos(phase);
		float secondary = (float) Math.sin(phase * 2F + 0.75F) * 0.16F;
		float tertiary = (float) Math.sin(phase * 3F - 0.35F) * 0.06F;
		return (primary * 0.84F + secondary + tertiary) * 0.52F;
	}

	private String limitCharactersPerLine(String text, int maxCharsPerLine) {
		if (text.length() <= maxCharsPerLine) {
			return text;
		}

		var builder = new StringBuilder();
		int index = 0;

		while (index < text.length()) {
			int lineEnd = text.indexOf('\n', index);
			if (lineEnd < 0) {
				lineEnd = text.length();
			}

			String line = text.substring(index, lineEnd);
			appendWrappedLine(builder, line, maxCharsPerLine);

			if (lineEnd < text.length()) {
				builder.append('\n');
			}

			index = lineEnd + 1;
		}

		return builder.toString();
	}

	private void appendWrappedLine(StringBuilder builder, String line, int maxCharsPerLine) {
		int index = 0;

		while (index < line.length()) {
			int remaining = line.length() - index;

			if (remaining <= maxCharsPerLine) {
				builder.append(line, index, line.length());
				return;
			}

			int preferredBreak = findPreferredWrapIndex(line, index, maxCharsPerLine);
			if (preferredBreak <= index) {
				preferredBreak = Math.min(line.length(), index + maxCharsPerLine);
			}

			builder.append(line, index, preferredBreak);
			if (preferredBreak < line.length()) {
				builder.append('\n');
			}

			index = preferredBreak;
			while (index < line.length() && line.charAt(index) == ' ') {
				index++;
			}
		}
	}

	private int findPreferredWrapIndex(String text, int start, int maxCharsPerLine) {
		int softLimit = Math.min(text.length(), start + maxCharsPerLine);
		int whitespaceBreak = -1;
		for (int i = softLimit; i < text.length(); i++) {
			char c = text.charAt(i);
			if (Character.isWhitespace(c)) {
				whitespaceBreak = i;
				break;
			}
			if (c == '\\' || c == '/') {
				return i + 1;
			}
		}

		if (whitespaceBreak >= 0) {
			return whitespaceBreak;
		}

		for (int i = softLimit; i > start; i--) {
			if (isWrapBoundary(text.charAt(i - 1), i < text.length() ? text.charAt(i) : '\0')) {
				return includeTrailingSeparator(text, i);
			}
		}

		return softLimit;
	}

	private boolean isWrapBoundary(char previous, char next) {
		return Character.isWhitespace(previous)
			|| previous == '\\'
			|| previous == '/'
			|| previous == '>'
			|| previous == ']'
			|| previous == ')'
			|| next == '\\'
			|| next == '/';
	}

	private int includeTrailingSeparator(String text, int index) {
		if (index < text.length()) {
			char c = text.charAt(index);
			if (c == '\\' || c == '/') {
				return index + 1;
			}
		}

		return index;
	}

	private static void renderFlatButtonChrome(GuiGraphicsExtractor graphics, int x1, int y1, int x2, int y2, boolean hovered, boolean active) {
		int fill = !active ? FLAT_BUTTON_FILL_DISABLED : (hovered ? FLAT_BUTTON_FILL_HOVERED : FLAT_BUTTON_FILL);
		int top = !active ? FLAT_BUTTON_TOP_DISABLED : (hovered ? FLAT_BUTTON_TOP_HOVERED : FLAT_BUTTON_TOP);
		int bottom = FLAT_BUTTON_BOTTOM;
		graphics.fill(x1, y1, x2, y2, fill);
		graphics.fill(x1, y1, x2, y1 + 1, top);
		graphics.fill(x1, y2 - 1, x2, y2, bottom);
		graphics.fill(x1, y1, x1 + 1, y2, top);
		graphics.fill(x2 - 1, y1, x2, y2, bottom);
	}

	private static class KubeJSErrorButton extends Button {
		private KubeJSErrorButton(Builder builder) {
			super(builder);
		}

		@Override
		protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
			renderFlatButtonChrome(graphics, getX(), getY(), getX() + getWidth(), getY() + getHeight(), active && isHoveredOrFocused(), active);
			int color = active ? 0xFFFFFFFF : FLAT_BUTTON_TEXT_DISABLED;
			graphics.centeredText(Minecraft.getInstance().font, getMessage(), getX() + getWidth() / 2, getY() + 6, color);
		}

		@Override
		public void playDownSound(SoundManager soundManager) {
			playScreenClickSound(soundManager);
		}
	}

	private static class IconTextButton extends KubeJSErrorButton {
		private final @Nullable KubeJSErrorScreen screen;
		private final Component icon;
		private final int iconGlowColor;
		private final Component[] shiftTooltip;
		private float iconHoverProgress;

		private IconTextButton(@Nullable KubeJSErrorScreen screen, Builder builder, Component icon, int iconGlowColor, Component... shiftTooltip) {
			super(builder);
			this.screen = screen;
			this.icon = icon;
			this.iconGlowColor = iconGlowColor;
			this.shiftTooltip = shiftTooltip;
		}

		@Override
		protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
			var font = Minecraft.getInstance().font;
			boolean hovered = active && isHoveredOrFocused();
			renderFlatButtonChrome(graphics, getX(), getY(), getX() + getWidth(), getY() + getHeight(), hovered, active);
			int iconWidth = font.width(icon);
			int labelWidth = font.width(getMessage());
			int contentWidth = iconWidth + ICON_TEXT_GAP + labelWidth;
			int contentX = getX() + Math.max(2, (getWidth() - contentWidth) / 2);
			int iconY = getY() + 5;
			int textY = getY() + 6;
			iconHoverProgress = approachHoverProgress(iconHoverProgress, hovered);

			if (active) {
				renderHoverableIcon(graphics, font, icon, contentX, iconY, iconHoverProgress, iconGlowColor);
			} else {
				graphics.text(font, icon.copy().withColor(FLAT_BUTTON_TEXT_DISABLED), contentX, iconY, FLAT_BUTTON_TEXT_DISABLED);
			}

			graphics.text(font, getMessage(), contentX + iconWidth + ICON_TEXT_GAP, textY, active ? 0xFFFFFFFF : FLAT_BUTTON_TEXT_DISABLED);

			if (screen != null && hovered && active && shiftTooltip.length > 0 && Minecraft.getInstance().hasShiftDown() && (screen.tooltip == null || screen.tooltip.isEmpty())) {
				screen.setCompactTooltip(shiftTooltip);
			}
		}
	}

	public static class ErrorList extends ObjectSelectionList<Entry> {
		private static final int ENTRY_HEIGHT = 28;
		private static final int PANEL_TOP_PADDING = 4;
		private static final int PANEL_BOTTOM_PADDING = 4;
		private static final int PANEL_SIDE_PADDING = 5;
		private static final double SCROLL_EASING = 0.35D;
		public final KubeJSErrorScreen screen;
		public final List<ConsoleLine> lines;
		private double targetScrollAmount;
		private boolean animatingScroll;

		public ErrorList(KubeJSErrorScreen screen, Minecraft minecraft, int width, int height, int top, int bottom, List<ConsoleLine> lines) {
			super(minecraft, width, bottom - top, top, ENTRY_HEIGHT);
			this.screen = screen;
			this.lines = lines;
			this.targetScrollAmount = scrollAmount();

			var calendar = Calendar.getInstance();

			for (int i = 0; i < lines.size(); i++) {
				addEntry(new KubeJSErrorScreen.Entry(this, minecraft, i, lines.get(i), calendar));
			}
		}

		@Override
		public boolean keyPressed(KeyEvent event) {
			int key = event.key();
			if (key == 257 || key == 32 || key == 335) {
				var sel = getSelected();
				if (sel != null) {
					sel.open();
					return true;
				}
			}

			return super.keyPressed(event);
		}

		@Override
		public void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
			animateScroll();
			super.extractWidgetRenderState(graphics, mouseX, mouseY, delta);
		}

		@Override
		public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
			if (!isMouseOver(mouseX, mouseY) || !scrollable()) {
				return false;
			}

			targetScrollAmount = clampScrollAmount(targetScrollAmount - verticalAmount * scrollRate());
			return true;
		}

		@Override
		public void setScrollAmount(double amount) {
			super.setScrollAmount(amount);

			if (!animatingScroll) {
				targetScrollAmount = scrollAmount();
			}
		}

		private void animateScroll() {
			double current = scrollAmount();
			double diff = targetScrollAmount - current;

			if (Math.abs(diff) < 0.35D) {
				if (diff != 0D) {
					setAnimatedScrollAmount(targetScrollAmount);
				}

				return;
			}

			setAnimatedScrollAmount(Mth.lerp(SCROLL_EASING, current, targetScrollAmount));
		}

		private void setAnimatedScrollAmount(double amount) {
			animatingScroll = true;
			try {
				super.setScrollAmount(clampScrollAmount(amount));
			} finally {
				animatingScroll = false;
			}
		}

		private double clampScrollAmount(double amount) {
			return Math.max(0D, Math.min(maxScrollAmount(), amount));
		}

		@Override
		public int getRowWidth() {
			return (int) (this.width * 0.93D);
		}

		@Override
		protected void extractSelection(GuiGraphicsExtractor graphics, KubeJSErrorScreen.Entry entry, int outlineColor) {
		}

		public boolean isOverAnyEntryPanel(double mouseX, double mouseY) {
			for (var entry : children()) {
				int panelX1 = entry.getX() - PANEL_SIDE_PADDING;
				int panelY1 = entry.getY() + PANEL_TOP_PADDING;
				int panelX2 = entry.getX() + entry.getWidth() - 2;
				int panelY2 = entry.getY() + entry.getHeight() - PANEL_BOTTOM_PADDING;

				if (mouseX >= panelX1 && mouseX < panelX2 && mouseY >= panelY1 && mouseY < panelY2) {
					return true;
				}
			}

			return false;
		}
	}

	public static class Entry extends ObjectSelectionList.Entry<Entry> {
		private static final int TOOLTIP_WRAP_WIDTH = 260;
		private static final int MAX_TOOLTIP_CHARS = 420;
		private static final int MAX_TOOLTIP_LINES = 16;
		private static final int PANEL_TOP_PADDING = 4;
		private static final int PANEL_BOTTOM_PADDING = 4;
		private static final int PANEL_SIDE_PADDING = 5;
		private static final int PANEL_SEPARATOR_INSET = 3;
		private static final int SELECTED_TAIL_LENGTH = 4;
		private static final float PANEL_GLOW_RADIUS_X = 72F;
		private static final float PANEL_GLOW_RADIUS_Y = 29F;
		private static final int PANEL_GLOW_MAX_ALPHA = 24;
		private static final int PANEL_GLOW_COLOR = 0xE3EDF7;
		private record ButtonBounds(int x1, int y1, int x2, int y2) {
			public boolean contains(double x, double y) {
				return x >= x1 && x < x2 && y >= y1 && y < y2;
			}
		}

		private static final int BUTTON_HEIGHT = 16;
		private static final int BUTTON_PADDING_X = 6;
		private static final int BUTTON_GAP = 4;
		private static final Component COPY_BUTTON_LABEL = Component.literal("Copy");
		private static final Component COPY_BUTTON_ICON = TextIcons.copy();
		private final ErrorList errorList;
		private final Minecraft minecraft;
		private final int index;
		private final ConsoleLine line;
		private final FormattedCharSequence indexText;
		private @Nullable SourceLine primarySource;
		private @Nullable Component sourceIcon;
		private @Nullable Component sourceLabelComponent;
		private @Nullable String sourceLabel;
		private @Nullable FormattedCharSequence timestampText;
		private int copyButtonWidth = -1;
		private @Nullable Component openButtonIcon;
		private @Nullable Component openButtonLabel;
		private int openButtonWidth = -1;
		private @Nullable Path openPath;
		private int openLine = 1;
		private @Nullable String clipboardSummaryText;
		private @Nullable String clipboardFullText;
		private @Nullable Component firstStackTraceTooltip;
		private @Nullable Component stackTraceTooltip;
		private @Nullable Component fullStackTraceTooltip;
		private boolean displayPrepared;
		private boolean openPrepared;
		private boolean clipboardPrepared;
		private boolean stackTracePrepared;
		private float sourceIconHoverProgress;
		private float sourceTextHoverProgress;
		private float copyIconHoverProgress;
		private float openIconHoverProgress;

		public Entry(ErrorList errorList, Minecraft minecraft, int index, ConsoleLine line, Calendar calendar) {
			this.errorList = errorList;
			this.minecraft = minecraft;
			this.index = index;
			this.line = line;

			this.indexText = Component.literal("#" + (index + 1)).getVisualOrderText();
		}

		@Override
		public Component getNarration() {
			return Component.empty();
		}

		private void ensureDisplayPrepared() {
			if (displayPrepared) {
				return;
			}

			primarySource = getPrimarySourceLine();
			sourceLabel = primarySource == null ? (line.type == LogType.WARN ? "Internal Warning" : "Internal Error") : formatDisplaySourceLabel(primarySource);
			sourceIcon = line.type == LogType.ERROR ? TextIcons.error() : TextIcons.warn();
			sourceLabelComponent = Component.literal(sourceLabel);

			var sb = new StringBuilder();
			var calendar = Calendar.getInstance();
			calendar.setTimeInMillis(line.timestamp);
			TimeJS.appendTimestamp(sb, calendar);
			timestampText = Component.literal(sb.toString()).getVisualOrderText();

			copyButtonWidth = minecraft.font.width(COPY_BUTTON_ICON) + ICON_TEXT_GAP + minecraft.font.width(COPY_BUTTON_LABEL) + BUTTON_PADDING_X * 2;
			displayPrepared = true;
		}

		private void ensureOpenPrepared() {
			if (openPrepared) {
				return;
			}

			ensureDisplayPrepared();
			openPath = getOpenPath(primarySource);
			openLine = getOpenLine(primarySource);
			boolean canOpen = openPath != null && Files.exists(openPath);
			openButtonIcon = canOpen && EditorExt.isKnownVSCode() ? TextIcons.vscode() : null;
			openButtonLabel = canOpen ? Component.literal("Open") : null;
			openButtonWidth = openButtonLabel == null ? 0 : (openButtonIcon == null ? 0 : minecraft.font.width(openButtonIcon) + ICON_TEXT_GAP) + minecraft.font.width(openButtonLabel) + BUTTON_PADDING_X * 2;
			openPrepared = true;
		}

		private void ensureClipboardPrepared() {
			if (clipboardPrepared) {
				return;
			}

			clipboardSummaryText = line.getText();
			clipboardFullText = createClipboardFullText();
			clipboardPrepared = true;
		}

		private void ensureStackTracePrepared() {
			if (stackTracePrepared) {
				return;
			}

			if (line.stackTrace.isEmpty()) {
				firstStackTraceTooltip = null;
				stackTraceTooltip = null;
				fullStackTraceTooltip = null;
				stackTracePrepared = true;
				return;
			}

			firstStackTraceTooltip = Component.literal(line.stackTrace.getFirst()).setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY));
			var preview = new StringBuilder();
			var full = new StringBuilder();
			int previewLines = 0;
			start:
			for (int i = 1; i < line.stackTrace.size(); i++) {
				for (var l1 : line.stackTrace.get(i).split("\n")) {
					if (!preview.isEmpty()) {
						preview.append('\n');
					}
					preview.append(l1);
					previewLines++;

					if (previewLines >= 4) {
						break start;
					}
				}
			}

			for (int i = 1; i < line.stackTrace.size(); i++) {
				for (var l1 : line.stackTrace.get(i).split("\n")) {
					if (!full.isEmpty()) {
						full.append('\n');
					}
					full.append(l1);
				}
			}

			stackTraceTooltip = preview.isEmpty() ? null : Component.literal(preview.toString()).setStyle(Style.EMPTY.withColor(ChatFormatting.DARK_GRAY));
			fullStackTraceTooltip = full.isEmpty() ? null : Component.literal(full.toString()).setStyle(Style.EMPTY.withColor(ChatFormatting.DARK_GRAY));
			stackTracePrepared = true;
		}


		@Override
		public void extractContent(GuiGraphicsExtractor g, int mouseX, int mouseY, boolean hovered, float delta) {
			ensureDisplayPrepared();
			ensureOpenPrepared();
			int x = getX();
			int y = getY();
			int w = getWidth();
			int indexX = x + 1;
			int indexWidth = minecraft.font.width(indexText);

			int col = line.type == LogType.ERROR ? 0xFFFF5B63 : 0xFFFFBB5B;
			var openBounds = getOpenButtonBounds(x, y, w);
			var copyBounds = getCopyButtonBounds(x, y, openBounds);
			int timestampX = copyBounds.x1() - TIMESTAMP_BUTTON_GAP - minecraft.font.width(timestampText);
			int sourceIconX = indexX + indexWidth + 8;
			int sourceTextX = sourceIconX + minecraft.font.width(sourceIcon) + ICON_TEXT_GAP;
			int sourceMaxWidth = Math.max(40, Mth.floor((timestampX - sourceTextX - 8) / PANEL_TEXT_HOVER_SCALE));
			var visibleSource = minecraft.font.split(sourceLabelComponent, sourceMaxWidth).getFirst();
			boolean selected = errorList.getSelected() == this;
			boolean copyHovered = copyBounds.contains(mouseX, mouseY);
			boolean openHovered = openBounds != null && openBounds.contains(mouseX, mouseY);
			sourceIconHoverProgress = approachHoverProgress(sourceIconHoverProgress, hovered);
			sourceTextHoverProgress = approachHoverProgress(sourceTextHoverProgress, hovered);
			copyIconHoverProgress = approachHoverProgress(copyIconHoverProgress, copyHovered);
			openIconHoverProgress = approachHoverProgress(openIconHoverProgress, openHovered);

			renderPanelMesh(g, x, y, w, copyBounds, openBounds, mouseX, mouseY, hovered, selected);

			g.text(minecraft.font, indexText, indexX, y + 10, col);
			renderHoverableIcon(g, minecraft.font, sourceIcon, sourceIconX, y + 10, sourceIconHoverProgress, line.type == LogType.ERROR ? 0xFFFF6E76 : 0xFFFFC86A);
			renderHoverableText(g, minecraft.font, visibleSource, sourceTextX, y + 10, sourceTextHoverProgress, 0xFFFFFFFF);
			g.text(minecraft.font, timestampText, timestampX, y + 10, 0xFF7A7A7A);
			renderActionButton(g, copyBounds, COPY_BUTTON_ICON, COPY_BUTTON_LABEL, copyHovered, copyIconHoverProgress, 0xFF66C9FF, 0xFF66C9FF, 1);

			if (openBounds != null && openButtonLabel != null) {
				renderActionButton(g, openBounds, openButtonIcon, openButtonLabel, openHovered, openIconHoverProgress, 0xFF7BE495, 0xFF4AA8FF, 1);
			}

			if (hovered) {
				if (copyHovered && Minecraft.getInstance().hasShiftDown()) {
					errorList.screen.setCompactTooltip(Component.literal(ClientProperties.get().includeStackTraceWhenCopyingErrors ? "Copy the error with stack trace" : "Copy error without stacktrace"));
				} else if (openHovered && Minecraft.getInstance().hasShiftDown()) {
					errorList.screen.setCompactTooltip(
						Component.literal("Open the source file").withColor(0x7BE495),
						openPath == null ? null : Component.literal(openPath.toAbsolutePath().toString()).withStyle(Style.EMPTY.withColor(ChatFormatting.GRAY))
					);
				} else if (Minecraft.getInstance().hasShiftDown()) {
					errorList.screen.setTooltip(buildTooltip(
						Component.literal(sourceLabel),
						line.message.isBlank() ? null : Component.literal(line.message).withStyle(Style.EMPTY.withColor(ChatFormatting.GRAY))
					), true, 1F, false);
				}
			}
		}

		@Override
		public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
			errorList.setSelected(this);
			ensureClipboardPrepared();
			ensureOpenPrepared();
			var openBounds = getOpenButtonBounds(getX(), getY(), getWidth());
			var copyBounds = getCopyButtonBounds(getX(), getY(), openBounds);

			if (copyBounds.contains(event.x(), event.y())) {
				playScreenClickSound(Minecraft.getInstance().getSoundManager());
				Minecraft.getInstance().keyboardHandler.setClipboard(ClientProperties.get().includeStackTraceWhenCopyingErrors ? clipboardFullText : clipboardSummaryText);
				errorList.screen.showCopyToast(Mth.floor(event.x()), Mth.floor(event.y()), Component.literal("Copied " + (line.type == LogType.WARN ? "warning" : "error") + " to Clipboard!"));
				return true;
			}

			if (openBounds != null && openBounds.contains(event.x(), event.y())) {
				playScreenClickSound(Minecraft.getInstance().getSoundManager());
				open();
				return true;
			}

			if (doubleClick) {
				playScreenClickSound(Minecraft.getInstance().getSoundManager());
				open();
				return true;
			}

			return true;
		}

		@Nullable
		private String fixSource(@Nullable String source) {
			if (source != null && !source.isEmpty()) {
				int c = source.indexOf(':');

				if (c >= 0) {
					return source.substring(c + 1);
				}
			}

			return source;
		}

		private @Nullable SourceLine getPrimarySourceLine() {
			SourceLine fallback = null;

			for (var sourceLine : line.sourceLines) {
				if (sourceLine.isUnknown()) {
					continue;
				}

				if (sourceLine.source().endsWith(".js")) {
					return sourceLine;
				}

				if (!sourceLine.source().endsWith(".java") && fallback == null) {
					fallback = sourceLine;
				} else if (fallback == null) {
					fallback = sourceLine;
				}
			}

			if (fallback != null) {
				return fallback;
			}

			return line.externalFile == null ? null : SourceLine.of(line.externalFile.getFileName().toString(), 0);
		}

		private @Nullable Path getOpenPath(@Nullable SourceLine primarySource) {
			if (line.externalFile != null) {
				return line.externalFile;
			}

			if (primarySource == null || primarySource.source().isEmpty()) {
				return null;
			}

			return line.console.scriptType.path.resolve(fixSource(primarySource.source()));
		}

		private String formatDisplaySourceLabel(SourceLine sourceLine) {
			String source = fixSource(sourceLine.source());

			if (source == null || source.isEmpty()) {
				return sourceLine.line() > 0 ? "<unknown source>#" + sourceLine.line() : "";
			}

			return sourceLine.line() > 0 ? source + "#" + sourceLine.line() : source;
		}

		private int getOpenLine(@Nullable SourceLine primarySource) {
			if (primarySource != null && primarySource.line() > 0) {
				return primarySource.line();
			}

			for (var sourceLine : line.sourceLines) {
				if (sourceLine.line() > 0) {
					return sourceLine.line();
				}
			}

			return 1;
		}

		private String createClipboardFullText() {
			if (line.stackTrace.isEmpty()) {
				return line.getText();
			}

			return line.getText() + "\n" + String.join("\n", line.stackTrace);
		}

		private ButtonBounds getOpenButtonBounds(int x, int y, int w) {
			if (openButtonLabel == null || openButtonWidth <= 0) {
				return null;
			}

			int x2 = x + w - 4;
			int x1 = x2 - openButtonWidth;
			int y1 = y + (getHeight() - BUTTON_HEIGHT) / 2;
			return new ButtonBounds(x1, y1, x2, y1 + BUTTON_HEIGHT);
		}

		private ButtonBounds getCopyButtonBounds(int x, int y, @Nullable ButtonBounds openBounds) {
			int x2 = openBounds == null ? x + getWidth() - 4 : openBounds.x1() - BUTTON_GAP;
			int x1 = x2 - copyButtonWidth;
			int y1 = y + (getHeight() - BUTTON_HEIGHT) / 2;
			return new ButtonBounds(x1, y1, x2, y1 + BUTTON_HEIGHT);
		}

		private List<FormattedCharSequence> buildTooltip(@Nullable Component... components) {
			var lines = new ArrayList<FormattedCharSequence>();
			int remainingChars = MAX_TOOLTIP_CHARS;
			boolean truncated = false;

			for (var component : components) {
				if (component == null || remainingChars <= 0 || lines.size() >= MAX_TOOLTIP_LINES) {
					if (component != null && (remainingChars <= 0 || lines.size() >= MAX_TOOLTIP_LINES)) {
						truncated = true;
					}
					continue;
				}

				String text = component.getString();
				if (text.isBlank()) {
					continue;
				}

				if (text.length() > remainingChars) {
					text = text.substring(0, Math.max(0, remainingChars - 3)) + "...";
					truncated = true;
				}

				var limited = Component.literal(text).setStyle(component.getStyle());
				for (var split : minecraft.font.split(limited, TOOLTIP_WRAP_WIDTH)) {
					if (lines.size() >= MAX_TOOLTIP_LINES) {
						truncated = true;
						break;
					}

					lines.add(split);
				}

				remainingChars -= text.length();
			}

			if (truncated && !lines.isEmpty() && lines.size() < MAX_TOOLTIP_LINES) {
				lines.add(Component.literal("...").withStyle(ChatFormatting.DARK_GRAY).getVisualOrderText());
			}

			return lines;
		}

		private void renderActionButton(GuiGraphicsExtractor g, ButtonBounds bounds, @Nullable Component icon, Component label, boolean hovered, float iconHoverProgress, int borderAccentColor, int iconGlowColor, int iconYOffset) {
			int background = hovered ? 0xAA24303A : 0x7A181C22;
			int border = hovered ? borderAccentColor : 0xFF404852;
			g.fill(bounds.x1(), bounds.y1(), bounds.x2(), bounds.y2(), background);
			g.fill(bounds.x1(), bounds.y1(), bounds.x2(), bounds.y1() + 1, border);
			g.fill(bounds.x1(), bounds.y2() - 1, bounds.x2(), bounds.y2(), border);
			g.fill(bounds.x1(), bounds.y1(), bounds.x1() + 1, bounds.y2(), border);
			g.fill(bounds.x2() - 1, bounds.y1(), bounds.x2(), bounds.y2(), border);
			int contentX = bounds.x1() + BUTTON_PADDING_X;
			int textY = bounds.y1() + 4;

			if (icon != null) {
				renderHoverableIcon(g, minecraft.font, icon, contentX, bounds.y1() + 3 + iconYOffset, iconHoverProgress, iconGlowColor);
				contentX += minecraft.font.width(icon) + ICON_TEXT_GAP;
			}

			g.text(minecraft.font, label, contentX, textY, hovered ? 0xFFFFFFFF : 0xFFD8E2EB);
		}

		private void renderPanelMesh(GuiGraphicsExtractor g, int x, int y, int width, ButtonBounds copyBounds, @Nullable ButtonBounds openBounds, int mouseX, int mouseY, boolean hovered, boolean selected) {
			int panelX1 = x - PANEL_SIDE_PADDING;
			int panelY1 = y + PANEL_TOP_PADDING;
			int panelX2 = x + width - 2;
			int panelY2 = y + getHeight() - PANEL_BOTTOM_PADDING;
			int borderThickness = selected ? 2 : 1;
			int fill = selected ? 0x58313B43 : hovered ? 0x4620282E : 0x30161B20;
			int border = selected ? 0xFF91A2B0 : hovered ? 0x6A56616A : 0x4A434D56;
			int rail = selected ? 0xFF6F808E : 0x3A384149;

			g.fill(panelX1, panelY1, panelX2, panelY2, fill);
			g.fill(panelX1, panelY1, panelX2, panelY1 + borderThickness, border);
			g.fill(panelX1, panelY2 - borderThickness, panelX2, panelY2, border);
			g.fill(panelX1, panelY1 + borderThickness, panelX1 + borderThickness, panelY2 - borderThickness, border);
			g.fill(panelX2 - borderThickness, panelY1 + borderThickness, panelX2, panelY2 - borderThickness, border);

			if (index > 0) {
				g.fill(panelX1, y, panelX1 + 1, panelY1, rail);
				g.fill(panelX2 - 1, y, panelX2, panelY1, rail);
			}

			if (index < errorList.lines.size() - 1) {
				g.fill(panelX1, panelY2, panelX1 + 1, y + getHeight(), rail);
				g.fill(panelX2 - 1, panelY2, panelX2, y + getHeight(), rail);
			}

			int buttonSeparatorX = openBounds == null ? Integer.MIN_VALUE : ((copyBounds.x2() + openBounds.x1()) / 2) - 1;

			if (buttonSeparatorX != Integer.MIN_VALUE) {
				g.fill(buttonSeparatorX, panelY1 + PANEL_SEPARATOR_INSET, buttonSeparatorX + 1, panelY2 - PANEL_SEPARATOR_INSET, rail);
			}

			if (selected) {
				renderSelectedPanelTails(g, panelX1, panelY1, panelX2, panelY2, y, borderThickness, border);
			}

			renderPanelGlow(g, panelX1, panelY1, panelX2, panelY2, buttonSeparatorX, y, mouseX, mouseY);
		}

		private void renderSelectedPanelTails(GuiGraphicsExtractor g, int panelX1, int panelY1, int panelX2, int panelY2, int entryY, int borderThickness, int borderColor) {
			if (index > 0) {
				renderSelectedTail(g, panelX1, panelY1 - SELECTED_TAIL_LENGTH, panelY1, borderThickness, borderColor, true);
				renderSelectedTail(g, panelX2 - borderThickness, panelY1 - SELECTED_TAIL_LENGTH, panelY1, borderThickness, borderColor, true);
			}

			if (index < errorList.lines.size() - 1) {
				renderSelectedTail(g, panelX1, panelY2, panelY2 + SELECTED_TAIL_LENGTH, borderThickness, borderColor, false);
				renderSelectedTail(g, panelX2 - borderThickness, panelY2, panelY2 + SELECTED_TAIL_LENGTH, borderThickness, borderColor, false);
			}
		}

		private void renderSelectedTail(GuiGraphicsExtractor g, int x, int y1, int y2, int width, int color, boolean fadeTowardTop) {
			int length = y2 - y1;

			for (int i = 0; i < length; i++) {
				int drawY = y1 + i;
				float progress = length <= 1 ? 1F : i / (float) (length - 1);
				float alphaProgress = fadeTowardTop ? progress : (1F - progress);
				int alpha = Mth.floor(28F + alphaProgress * 132F);
				g.fill(x, drawY, x + width, drawY + 1, withAlpha(color, alpha));
			}
		}

		private void renderPanelGlow(GuiGraphicsExtractor g, int panelX1, int panelY1, int panelX2, int panelY2, int buttonSeparatorX, int entryY, int mouseX, int mouseY) {
			renderGlowHorizontalLine(g, panelX1 + 1, panelX2 - 1, panelY1, mouseX, mouseY);
			renderGlowHorizontalLine(g, panelX1 + 1, panelX2 - 1, panelY2 - 1, mouseX, mouseY);
			renderGlowVerticalLine(g, panelX1, panelY1 + 1, panelY2 - 1, mouseX, mouseY);
			renderGlowVerticalLine(g, panelX2 - 1, panelY1 + 1, panelY2 - 1, mouseX, mouseY);

			if (buttonSeparatorX != Integer.MIN_VALUE) {
				renderGlowVerticalLine(g, buttonSeparatorX, panelY1 + PANEL_SEPARATOR_INSET, panelY2 - PANEL_SEPARATOR_INSET, mouseX, mouseY);
			}

			if (index > 0) {
				renderGlowVerticalLine(g, panelX1, entryY, panelY1, mouseX, mouseY);
				renderGlowVerticalLine(g, panelX2 - 1, entryY, panelY1, mouseX, mouseY);
			}

			if (index < errorList.lines.size() - 1) {
				renderGlowVerticalLine(g, panelX1, panelY2, entryY + getHeight(), mouseX, mouseY);
				renderGlowVerticalLine(g, panelX2 - 1, panelY2, entryY + getHeight(), mouseX, mouseY);
			}
		}

		private int withAlpha(int rgb, int alpha) {
			return (Mth.clamp(alpha, 0, 255) << 24) | (rgb & 0xFFFFFF);
		}

		private void renderGlowHorizontalLine(GuiGraphicsExtractor g, int x1, int x2, int y, int mouseX, int mouseY) {
			int runStart = -1;
			int runAlpha = -1;

			for (int x = x1; x < x2; x++) {
				int alpha = getGlowAlpha(x + 0.5F, y + 0.5F, mouseX, mouseY);

				if (alpha <= 0) {
					if (runStart >= 0) {
						g.fill(runStart, y, x, y + 1, withAlpha(PANEL_GLOW_COLOR, runAlpha));
						runStart = -1;
						runAlpha = -1;
					}

					continue;
				}

				if (runStart < 0) {
					runStart = x;
					runAlpha = alpha;
				} else if (Math.abs(alpha - runAlpha) > 1) {
					g.fill(runStart, y, x, y + 1, withAlpha(PANEL_GLOW_COLOR, runAlpha));
					runStart = x;
					runAlpha = alpha;
				}
			}

			if (runStart >= 0) {
				g.fill(runStart, y, x2, y + 1, withAlpha(PANEL_GLOW_COLOR, runAlpha));
			}
		}

		private void renderGlowVerticalLine(GuiGraphicsExtractor g, int x, int y1, int y2, int mouseX, int mouseY) {
			int runStart = -1;
			int runAlpha = -1;

			for (int y = y1; y < y2; y++) {
				int alpha = getGlowAlpha(x + 0.5F, y + 0.5F, mouseX, mouseY);

				if (alpha <= 0) {
					if (runStart >= 0) {
						g.fill(x, runStart, x + 1, y, withAlpha(PANEL_GLOW_COLOR, runAlpha));
						runStart = -1;
						runAlpha = -1;
					}

					continue;
				}

				if (runStart < 0) {
					runStart = y;
					runAlpha = alpha;
				} else if (Math.abs(alpha - runAlpha) > 1) {
					g.fill(x, runStart, x + 1, y, withAlpha(PANEL_GLOW_COLOR, runAlpha));
					runStart = y;
					runAlpha = alpha;
				}
			}

			if (runStart >= 0) {
				g.fill(x, runStart, x + 1, y2, withAlpha(PANEL_GLOW_COLOR, runAlpha));
			}
		}

		private int getGlowAlpha(float px, float py, int mouseX, int mouseY) {
			float dx = (px - mouseX) / PANEL_GLOW_RADIUS_X;
			float dy = (py - mouseY) / PANEL_GLOW_RADIUS_Y;
			float distance = dx * dx + dy * dy;
			if (distance >= 1F) {
				return 0;
			}

			float intensity = smoothstep(1F - distance);
			return Mth.floor(PANEL_GLOW_MAX_ALPHA * intensity);
		}

		private float smoothstep(float value) {
			float clamped = Mth.clamp(value, 0F, 1F);
			return clamped * clamped * (3F - 2F * clamped);
		}

		public void open() {
			if (openPath == null || !Files.exists(openPath)) {
				return;
			}

			try {
				if (Files.isRegularFile(openPath)) {
					EditorExt.openFile(openPath, openLine, 0);
				} else {
					Util.getPlatform().openPath(openPath);
				}
			} catch (Exception ignored) {
			}
		}
	}
}
