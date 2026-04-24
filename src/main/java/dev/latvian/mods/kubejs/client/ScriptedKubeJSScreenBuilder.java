package dev.latvian.mods.kubejs.client;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ScriptedKubeJSScreenBuilder {
	@FunctionalInterface
	public interface RenderCallback {
		void render(ScriptedKubeJSScreen screen, GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick);
	}

	@FunctionalInterface
	public interface MouseClickedCallback {
		boolean click(ScriptedKubeJSScreen screen, double mouseX, double mouseY, int button);
	}

	@FunctionalInterface
	public interface TextProvider {
		Object get(ScriptedKubeJSScreen screen);
	}

	@FunctionalInterface
	public interface IntProvider {
		int get(ScriptedKubeJSScreen screen);
	}

	@FunctionalInterface
	public interface BooleanProvider {
		boolean get(ScriptedKubeJSScreen screen);
	}

	@FunctionalInterface
	public interface ColorProvider {
		int get(ScriptedKubeJSScreen screen);
	}

	@FunctionalInterface
	public interface ButtonCreatedCallback {
		void created(ScriptedKubeJSScreen screen, Button button);
	}

	private int width = -1;
	private int height = -1;
	private int titleLabelX = Integer.MIN_VALUE;
	private int titleLabelY = Integer.MIN_VALUE;
	private int inventoryLabelX = Integer.MIN_VALUE;
	private int inventoryLabelY = Integer.MIN_VALUE;
	private boolean renderKubeContainerBackground = true;
	private Consumer<ScriptedKubeJSScreen> initCallback = screen -> {
	};
	private Consumer<ScriptedKubeJSScreen> tickCallback = screen -> {
	};
	private Consumer<ScriptedKubeJSScreen> closeCallback = screen -> {
	};
	private RenderCallback backgroundCallback = (screen, graphics, mouseX, mouseY, partialTick) -> {
	};
	private RenderCallback foregroundCallback = (screen, graphics, mouseX, mouseY, partialTick) -> {
	};
	private MouseClickedCallback mouseClickedCallback = (screen, mouseX, mouseY, button) -> false;

	public void size(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public void width(int width) {
		this.width = width;
	}

	public void height(int height) {
		this.height = height;
	}

	public void titleLabel(int x, int y) {
		this.titleLabelX = x;
		this.titleLabelY = y;
	}

	public void inventoryLabel(int x, int y) {
		this.inventoryLabelX = x;
		this.inventoryLabelY = y;
	}

	public void renderKubeContainerBackground(boolean render) {
		this.renderKubeContainerBackground = render;
	}

	public void init(Consumer<ScriptedKubeJSScreen> callback) {
		this.initCallback = this.initCallback.andThen(callback);
	}

	public void tick(Consumer<ScriptedKubeJSScreen> callback) {
		this.tickCallback = this.tickCallback.andThen(callback);
	}

	public void closed(Consumer<ScriptedKubeJSScreen> callback) {
		this.closeCallback = this.closeCallback.andThen(callback);
	}

	public void background(RenderCallback callback) {
		var existing = this.backgroundCallback;
		this.backgroundCallback = (screen, graphics, mouseX, mouseY, partialTick) -> {
			existing.render(screen, graphics, mouseX, mouseY, partialTick);
			callback.render(screen, graphics, mouseX, mouseY, partialTick);
		};
	}

	public void foreground(RenderCallback callback) {
		var existing = this.foregroundCallback;
		this.foregroundCallback = (screen, graphics, mouseX, mouseY, partialTick) -> {
			existing.render(screen, graphics, mouseX, mouseY, partialTick);
			callback.render(screen, graphics, mouseX, mouseY, partialTick);
		};
	}

	public void backgroundIf(BooleanProvider condition, RenderCallback callback) {
		background((screen, graphics, mouseX, mouseY, partialTick) -> {
			if (condition.get(screen)) {
				callback.render(screen, graphics, mouseX, mouseY, partialTick);
			}
		});
	}

	public void foregroundIf(BooleanProvider condition, RenderCallback callback) {
		foreground((screen, graphics, mouseX, mouseY, partialTick) -> {
			if (condition.get(screen)) {
				callback.render(screen, graphics, mouseX, mouseY, partialTick);
			}
		});
	}

	public void mouseClicked(MouseClickedCallback callback) {
		var existing = this.mouseClickedCallback;
		this.mouseClickedCallback = (screen, mouseX, mouseY, button) -> existing.click(screen, mouseX, mouseY, button) || callback.click(screen, mouseX, mouseY, button);
	}

	public void defaultData(String key, Object value) {
		init(screen -> screen.putDefault(key, value));
	}

	public void defaultContainerBackground() {
		background((screen, graphics, mouseX, mouseY, partialTick) -> screen.drawDefaultContainerBackground(graphics));
	}

	public void button(int x, int y, int width, int height, String text, ScriptedKubeJSScreen.ButtonCallback callback) {
		button(x, y, width, height, text, callback, (screen, button) -> {
		});
	}

	public void button(int x, int y, int width, int height, String text, ScriptedKubeJSScreen.ButtonCallback callback, ButtonCreatedCallback createdCallback) {
		init(screen -> {
			var button = screen.addButton(x, y, width, height, text, callback);
			createdCallback.created(screen, button);
		});
	}

	public void toggleButton(int x, int y, int width, int height, String key, boolean defaultValue, String onText, String offText) {
		defaultData(key, defaultValue);
		button(x, y, width, height, defaultValue ? onText : offText, (screen, button) -> {
			boolean next = screen.toggleBoolean(key, defaultValue);
			screen.setButtonText(button, next ? onText : offText);
		}, (screen, button) -> screen.setButtonText(button, screen.getBoolean(key, defaultValue) ? onText : offText));
	}

	public void cycleButton(int x, int y, int width, int height, String key, int defaultValue, String prefix, Object values) {
		List<String> options = normalizeValues(values);

		if (options.isEmpty()) {
			button(x, y, width, height, prefix, (screen, button) -> {
			});
			return;
		}

		defaultData(key, defaultValue);
		button(x, y, width, height, buildCycleLabel(prefix, options, defaultValue), (screen, button) -> {
			int next = screen.cycleInt(key, options.size(), defaultValue);
			screen.setButtonText(button, buildCycleLabel(prefix, options, next));
		}, (screen, button) -> screen.setButtonText(button, buildCycleLabel(prefix, options, screen.getInt(key, defaultValue))));
	}

	public void panel(int x, int y, int width, int height, int outerColor, int innerColor) {
		background((screen, graphics, mouseX, mouseY, partialTick) -> screen.drawPanel(graphics, x, y, width, height, outerColor, innerColor));
	}

	public void panelWithHeader(int x, int y, int width, int height, int outerColor, int innerColor, int headerHeight, int headerColor) {
		background((screen, graphics, mouseX, mouseY, partialTick) -> screen.drawPanelWithHeader(graphics, x, y, width, height, outerColor, innerColor, headerHeight, headerColor));
	}

	public void panelWithHeader(int x, int y, int width, int height, int outerColor, int innerColor, int headerHeight, ColorProvider headerColor) {
		background((screen, graphics, mouseX, mouseY, partialTick) -> screen.drawPanelWithHeader(graphics, x, y, width, height, outerColor, innerColor, headerHeight, headerColor.get(screen)));
	}

	public void dynamicPanelWithHeader(int x, int y, int width, int height, int outerColor, ColorProvider innerColor, int headerHeight, ColorProvider headerColor) {
		background((screen, graphics, mouseX, mouseY, partialTick) -> screen.drawPanelWithHeader(graphics, x, y, width, height, outerColor, innerColor.get(screen), headerHeight, headerColor.get(screen)));
	}

	public void horizontalLine(int x, int y, int width, int color) {
		background((screen, graphics, mouseX, mouseY, partialTick) -> screen.drawHorizontalLine(graphics, x, y, width, color));
	}

	public void dynamicHorizontalLine(int x, int y, int width, ColorProvider color) {
		background((screen, graphics, mouseX, mouseY, partialTick) -> screen.drawHorizontalLine(graphics, x, y, width, color.get(screen)));
	}

	public void slotHighlight(int firstSlot, int lastSlot, int fillColor, int borderColor) {
		background((screen, graphics, mouseX, mouseY, partialTick) -> screen.drawSlotHighlight(graphics, firstSlot, lastSlot, fillColor, borderColor));
	}

	public void slotHighlight(BooleanProvider visible, int firstSlot, int lastSlot, int fillColor, int borderColor) {
		backgroundIf(visible, (screen, graphics, mouseX, mouseY, partialTick) -> screen.drawSlotHighlight(graphics, firstSlot, lastSlot, fillColor, borderColor));
	}

	public void arrow(int x1, int y, int x2, int color) {
		background((screen, graphics, mouseX, mouseY, partialTick) -> screen.drawArrow(graphics, x1, y, x2, color));
	}

	public void dynamicArrow(int x1, int y, int x2, ColorProvider color) {
		background((screen, graphics, mouseX, mouseY, partialTick) -> screen.drawArrow(graphics, x1, y, x2, color.get(screen)));
	}

	public void arrow(BooleanProvider visible, int x1, int y, int x2, int color) {
		backgroundIf(visible, (screen, graphics, mouseX, mouseY, partialTick) -> screen.drawArrow(graphics, x1, y, x2, color));
	}

	public void dynamicArrow(BooleanProvider visible, int x1, int y, int x2, ColorProvider color) {
		backgroundIf(visible, (screen, graphics, mouseX, mouseY, partialTick) -> screen.drawArrow(graphics, x1, y, x2, color.get(screen)));
	}

	public void text(int x, int y, Object text, int color) {
		dynamicText(x, y, screen -> text, color, false);
	}

	public void text(int x, int y, Object text, int color, boolean shadow) {
		dynamicText(x, y, screen -> text, color, shadow);
	}

	public void dynamicText(int x, int y, TextProvider text, int color) {
		dynamicText(x, y, text, color, false);
	}

	public void dynamicText(int x, int y, TextProvider text, int color, boolean shadow) {
		foreground((screen, graphics, mouseX, mouseY, partialTick) -> screen.drawText(graphics, text.get(screen), x, y, color, shadow));
	}

	public void centeredText(int centerX, int y, Object text, int color) {
		dynamicCenteredText(centerX, y, screen -> text, color);
	}

	public void dynamicCenteredText(int centerX, int y, TextProvider text, int color) {
		foreground((screen, graphics, mouseX, mouseY, partialTick) -> screen.drawCenteredText(graphics, text.get(screen), centerX, y, color));
	}

	public void meter(int x, int y, String label, int max, int barColor, IntProvider value) {
		foreground((screen, graphics, mouseX, mouseY, partialTick) -> screen.drawMeter(graphics, x, y, label, value.get(screen), max, barColor));
	}

	public void meter(int x, int y, TextProvider label, int max, int barColor, IntProvider value) {
		foreground((screen, graphics, mouseX, mouseY, partialTick) -> screen.drawMeter(graphics, x, y, String.valueOf(label.get(screen)), value.get(screen), max, barColor));
	}

	ScriptedKubeJSScreen.Config build() {
		return new ScriptedKubeJSScreen.Config(
			width,
			height,
			titleLabelX,
			titleLabelY,
			inventoryLabelX,
			inventoryLabelY,
			renderKubeContainerBackground,
			initCallback,
			tickCallback,
			closeCallback,
			backgroundCallback,
			foregroundCallback,
			mouseClickedCallback
		);
	}

	private static String buildCycleLabel(String prefix, List<String> options, int index) {
		if (options.isEmpty()) {
			return prefix;
		}

		int safeIndex = Math.floorMod(index, options.size());
		return prefix + options.get(safeIndex);
	}

	private static List<String> normalizeValues(Object values) {
		var list = new ArrayList<String>();

		if (values == null) {
			return list;
		}

		if (values instanceof Iterable<?> iterable) {
			for (var value : iterable) {
				list.add(String.valueOf(value));
			}

			return list;
		}

		if (values.getClass().isArray()) {
			int length = Array.getLength(values);

			for (int i = 0; i < length; i++) {
				list.add(String.valueOf(Array.get(values, i)));
			}

			return list;
		}

		list.add(String.valueOf(values));
		return list;
	}
}
