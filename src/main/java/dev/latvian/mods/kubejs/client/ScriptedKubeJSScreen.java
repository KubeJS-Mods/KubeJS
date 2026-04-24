package dev.latvian.mods.kubejs.client;

import dev.latvian.mods.kubejs.gui.KubeJSMenu;
import dev.latvian.mods.kubejs.gui.KubeJSScreen;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class ScriptedKubeJSScreen extends KubeJSScreen {
	@FunctionalInterface
	public interface ButtonCallback {
		void click(ScriptedKubeJSScreen screen, Button button);
	}

	record Config(
		int width,
		int height,
		int titleLabelX,
		int titleLabelY,
		int inventoryLabelX,
		int inventoryLabelY,
		boolean renderKubeContainerBackground,
		Consumer<ScriptedKubeJSScreen> initCallback,
		Consumer<ScriptedKubeJSScreen> tickCallback,
		Consumer<ScriptedKubeJSScreen> closeCallback,
		ScriptedKubeJSScreenBuilder.RenderCallback backgroundCallback,
		ScriptedKubeJSScreenBuilder.RenderCallback foregroundCallback,
		ScriptedKubeJSScreenBuilder.MouseClickedCallback mouseClickedCallback
	) {
	}

	private final Config config;
	private final Map<String, Object> data;

	public ScriptedKubeJSScreen(KubeJSMenu menu, Inventory inventory, Component component, Config config) {
		super(
			menu,
			inventory,
			component,
			config.width() > 0 ? config.width() : 176,
			config.height() > 0 ? config.height() : 114 + menu.guiData.inventoryHeight * 18
		);
		this.config = config;
		this.data = new HashMap<>();

		if (config.titleLabelX() != Integer.MIN_VALUE) {
			this.titleLabelX = config.titleLabelX();
		}

		if (config.titleLabelY() != Integer.MIN_VALUE) {
			this.titleLabelY = config.titleLabelY();
		}

		if (config.inventoryLabelX() != Integer.MIN_VALUE) {
			this.inventoryLabelX = config.inventoryLabelX();
		}

		if (config.inventoryLabelY() != Integer.MIN_VALUE) {
			this.inventoryLabelY = config.inventoryLabelY();
		}
	}

	@Override
	protected boolean shouldRenderKubeContainerBackground() {
		return config.renderKubeContainerBackground();
	}

	@Override
	protected void init() {
		super.init();
		config.initCallback().accept(this);
	}

	@Override
	protected void containerTick() {
		super.containerTick();
		config.tickCallback().accept(this);
	}

	@Override
	public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
		super.extractBackground(graphics, mouseX, mouseY, partialTick);
		config.backgroundCallback().render(this, graphics, mouseX, mouseY, partialTick);
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
		super.extractRenderState(graphics, mouseX, mouseY, partialTick);
		config.foregroundCallback().render(this, graphics, mouseX, mouseY, partialTick);
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		if (config.mouseClickedCallback().click(this, event.x(), event.y(), event.button())) {
			return true;
		}

		return super.mouseClicked(event, doubleClick);
	}

	@Override
	public void onClose() {
		config.closeCallback().accept(this);
		super.onClose();
	}

	public Button addButton(int x, int y, int width, int height, String text, ButtonCallback callback) {
		return addButton(x, y, width, height, Component.literal(text), callback);
	}

	public Button addButton(int x, int y, int width, int height, Component text, ButtonCallback callback) {
		return addRenderableWidget(Button.builder(text, button -> callback.click(this, button))
			.bounds(leftPos + x, topPos + y, width, height)
			.build());
	}

	public void setButtonText(Button button, Object text) {
		button.setMessage(Component.literal(String.valueOf(text)));
	}

	public Font getFontRenderer() {
		return font;
	}

	public int getLeft() {
		return leftPos;
	}

	public int getTop() {
		return topPos;
	}

	public int getGuiWidth() {
		return imageWidth;
	}

	public int getGuiHeight() {
		return imageHeight;
	}

	public int getContainerColumns() {
		return menu.guiData.inventoryWidth;
	}

	public int getContainerRows() {
		return menu.guiData.inventoryHeight;
	}

	public int getContainerSlotCount() {
		return menu.guiData.inventory.kjs$getSlots();
	}

	public int getTotalSlotCount() {
		return menu.slots.size();
	}

	public Slot getSlot(int index) {
		return menu.slots.get(index);
	}

	public ItemStack getContainerStack(int slot) {
		return slot >= 0 && slot < getContainerSlotCount() ? menu.slots.get(slot).getItem() : ItemStack.EMPTY;
	}

	public int getContainerStackCount(int slot) {
		return getContainerStack(slot).getCount();
	}

	public String getContainerStackName(int slot) {
		return getContainerStackName(slot, "empty");
	}

	public String getContainerStackName(int slot, String empty) {
		var stack = getContainerStack(slot);
		return stack.isEmpty() ? empty : stack.getHoverName().getString();
	}

	public int countFilledColumns(int columnStart, int columnEndExclusive) {
		int filled = 0;

		for (int slot = 0; slot < getContainerSlotCount(); slot++) {
			int column = slot % getContainerColumns();

			if (column >= columnStart && column < columnEndExclusive && !getContainerStack(slot).isEmpty()) {
				filled++;
			}
		}

		return filled;
	}

	public int countContainerStacks() {
		int stacks = 0;

		for (int slot = 0; slot < getContainerSlotCount(); slot++) {
			if (!getContainerStack(slot).isEmpty()) {
				stacks++;
			}
		}

		return stacks;
	}

	public int countContainerItems() {
		int items = 0;

		for (int slot = 0; slot < getContainerSlotCount(); slot++) {
			items += getContainerStack(slot).getCount();
		}

		return items;
	}

	public void drawDefaultContainerBackground(GuiGraphicsExtractor graphics) {
		drawKubeContainerBackground(graphics);
	}

	public void drawPanel(GuiGraphicsExtractor graphics, int x, int y, int width, int height, int outerColor, int innerColor) {
		graphics.fill(leftPos + x, topPos + y, leftPos + x + width, topPos + y + height, outerColor);
		graphics.fill(leftPos + x + 1, topPos + y + 1, leftPos + x + width - 1, topPos + y + height - 1, innerColor);
	}

	public void drawPanelWithHeader(GuiGraphicsExtractor graphics, int x, int y, int width, int height, int outerColor, int innerColor, int headerHeight, int headerColor) {
		drawPanel(graphics, x, y, width, height, outerColor, innerColor);
		graphics.fill(leftPos + x + 1, topPos + y + 1, leftPos + x + width - 1, topPos + y + headerHeight, headerColor);
	}

	public void drawHorizontalLine(GuiGraphicsExtractor graphics, int x, int y, int width, int color) {
		graphics.fill(leftPos + x, topPos + y, leftPos + x + width, topPos + y + 1, color);
	}

	public void drawText(GuiGraphicsExtractor graphics, Object text, int x, int y, int color, boolean shadow) {
		graphics.text(font, String.valueOf(text), leftPos + x, topPos + y, color, shadow);
	}

	public void drawCenteredText(GuiGraphicsExtractor graphics, Object text, int centerX, int y, int color) {
		graphics.centeredText(font, String.valueOf(text), leftPos + centerX, topPos + y, color);
	}

	public void drawMeter(GuiGraphicsExtractor graphics, int x, int y, String label, int value, int max, int barColor) {
		int width = 90;
		int fill = max <= 0 ? 0 : Math.min(width, Math.max(0, value * width / max));

		drawText(graphics, label, x, y, 0xFFFFFFFF, false);
		graphics.fill(leftPos + x, topPos + y + 8, leftPos + x + width, topPos + y + 13, 0xAA0A0D12);
		graphics.fill(leftPos + x + 1, topPos + y + 9, leftPos + x + fill, topPos + y + 12, barColor);
	}

	public void drawSlotHighlight(GuiGraphicsExtractor graphics, int firstSlot, int lastSlot, int fillColor, int borderColor) {
		Slot first = getSlot(firstSlot);
		Slot last = getSlot(lastSlot);
		int x1 = leftPos + Math.min(first.x, last.x) - 3;
		int y1 = topPos + Math.min(first.y, last.y) - 3;
		int x2 = leftPos + Math.max(first.x, last.x) + 19;
		int y2 = topPos + Math.max(first.y, last.y) + 19;

		graphics.fill(x1, y1, x2, y2, fillColor);
		graphics.fill(x1, y1, x2, y1 + 1, borderColor);
		graphics.fill(x1, y2 - 1, x2, y2, borderColor);
		graphics.fill(x1, y1, x1 + 1, y2, borderColor);
		graphics.fill(x2 - 1, y1, x2, y2, borderColor);
	}

	public void drawArrow(GuiGraphicsExtractor graphics, int x1, int y, int x2, int color) {
		graphics.fill(leftPos + x1, topPos + y, leftPos + x2, topPos + y + 2, color);
		graphics.fill(leftPos + x2 - 4, topPos + y - 3, leftPos + x2, topPos + y + 5, color);
	}

	public Map<String, Object> getData() {
		return data;
	}

	public void putData(String key, Object value) {
		data.put(key, value);
	}

	public Object putDefault(String key, Object value) {
		return data.putIfAbsent(key, value);
	}

	public Object getDataValue(String key) {
		return data.get(key);
	}

	public int getInt(String key) {
		return getInt(key, 0);
	}

	public int getInt(String key, int fallback) {
		var value = data.get(key);
		return value instanceof Number n ? n.intValue() : fallback;
	}

	public boolean getBoolean(String key) {
		return getBoolean(key, false);
	}

	public boolean getBoolean(String key, boolean fallback) {
		var value = data.get(key);
		return value instanceof Boolean b ? b : fallback;
	}

	public String getString(String key) {
		return getString(key, "");
	}

	public String getString(String key, String fallback) {
		var value = data.get(key);
		return value == null ? fallback : String.valueOf(value);
	}

	public int incrementInt(String key, int amount) {
		return incrementInt(key, amount, 0);
	}

	public int incrementInt(String key, int amount, int fallback) {
		int next = getInt(key, fallback) + amount;
		putData(key, next);
		return next;
	}

	public int cycleInt(String key, int size) {
		return cycleInt(key, size, 0);
	}

	public int cycleInt(String key, int size, int fallback) {
		if (size <= 0) {
			putData(key, fallback);
			return fallback;
		}

		int next = Math.floorMod(getInt(key, fallback) + 1, size);
		putData(key, next);
		return next;
	}

	public boolean toggleBoolean(String key) {
		return toggleBoolean(key, false);
	}

	public boolean toggleBoolean(String key, boolean fallback) {
		boolean next = !getBoolean(key, fallback);
		putData(key, next);
		return next;
	}
}
