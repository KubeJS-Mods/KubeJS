package dev.latvian.mods.kubejs.client;

import dev.latvian.mods.kubejs.client.icon.KubeIconRenderer;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.TextWrapper;
import dev.latvian.mods.kubejs.util.NotificationToastData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastManager;
import net.minecraft.util.FormattedCharSequence;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class NotificationToast implements Toast {
	private final NotificationToastData notification;

	private final long duration;
	private final @Nullable KubeIconRenderer icon;
	private final List<FormattedCharSequence> text;
	private int width, height;

	private long lastChanged;
	private boolean changed;

	private Toast.Visibility wantedVisibility = Toast.Visibility.SHOW;

	public NotificationToast(Minecraft mc, NotificationToastData notification) {
		this.notification = notification;
		this.duration = notification.duration().toMillis();

		this.icon = notification.icon().map(KubeIconRenderer::from).orElse(null);

		this.text = new ArrayList<>(2);
		this.width = 0;
		this.height = 0;

		if (!TextWrapper.isEmpty(notification.text())) {
			this.text.addAll(mc.font.split(notification.text(), 240));
		}

		for (var l : this.text) {
			this.width = Math.max(this.width, mc.font.width(l));
		}

		this.width += 12;

		if (this.icon != null) {
			this.width += 24;
		}

		this.height = Math.max(this.text.size() * 10 + 12, 28);

		if (this.text.isEmpty() && this.icon != null) {
			this.width = 28;
			this.height = 28;
		}

		//this.width = Math.max(160, 30 + Math.max(mc.font.width(component), component2 == null ? 0 : mc.font.width(component2));
	}

	@Override
	public int width() {
		return this.width;
	}

	@Override
	public int height() {
		return this.height;
	}

	private void drawRectangle(GuiGraphicsExtractor graphics, int x0, int y0, int x1, int y1, int r, int g, int b) {
		graphics.fill(x0, y0, x1, y1, (0xFF << 24) | (r << 16) | (g << 8) | b);
	}

	private void drawRectangle(GuiGraphicsExtractor graphics, int x0, int y0, int x1, int y1, int rgb) {
		graphics.fill(x0, y0, x1, y1, 0xFF000000 | (rgb & 0xFFFFFF));
	}


	@Override
	public Toast.Visibility getWantedVisibility() {
		return wantedVisibility;
	}

	@Override
	public void update(ToastManager toastManager, long l) {
		if (changed) {
			lastChanged = l;
			changed = false;
		}

		wantedVisibility = l - lastChanged < duration ? Toast.Visibility.SHOW : Toast.Visibility.HIDE;
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor graphics, Font font, long l) {
		if (changed) {
			lastChanged = l;
			changed = false;
		}

		var mc = Minecraft.getInstance();

		int w = width();
		int h = height();

		int xOff = -2;
		int yOff = 2;

		int oc = notification.outlineColor().orElse(NotificationToastData.DEFAULT_OUTLINE_COLOR).kjs$getRGB();
		int bc = notification.borderColor().orElse(NotificationToastData.DEFAULT_BORDER_COLOR).kjs$getRGB();
		int bgc = notification.backgroundColor().orElse(NotificationToastData.DEFAULT_BACKGROUND_COLOR).kjs$getRGB();

		int o = 0xFF000000 | oc;
		int b = 0xFF000000 | bc;
		int bg = 0xFF000000 | bgc;

		graphics.fill(xOff + 2, yOff, xOff + w - 2, yOff + h, o);
		graphics.fill(xOff, yOff + 2, xOff + w, yOff + h - 2, o);
		graphics.fill(xOff + 1, yOff + 1, xOff + w - 1, yOff + h - 1, o);

		graphics.fill(xOff + 2, yOff + 1, xOff + w - 2, yOff + h - 1, b);
		graphics.fill(xOff + 1, yOff + 2, xOff + w - 1, yOff + h - 2, b);

		graphics.fill(xOff + 2, yOff + 2, xOff + w - 2, yOff + h - 2, bg);

		if (icon != null) {
			icon.draw(mc, graphics, xOff + 14, yOff + h / 2, notification.iconSize());
		}

		int th = icon == null ? 6 : 26;
		int tv = (h - text.size() * 10) / 2 + 1;

		for (var i = 0; i < text.size(); i++) {
			graphics.text(mc.font, text.get(i), xOff + th, yOff + tv + i * 10, 0xFFFFFF, notification.textShadow());
		}
	}

}
