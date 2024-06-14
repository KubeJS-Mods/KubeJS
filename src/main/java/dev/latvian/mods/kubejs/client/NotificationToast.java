package dev.latvian.mods.kubejs.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import dev.latvian.mods.kubejs.bindings.TextWrapper;
import dev.latvian.mods.kubejs.util.IconKJS;
import dev.latvian.mods.kubejs.util.NotificationToastData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.FastColor;
import net.minecraft.util.FormattedCharSequence;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public class NotificationToast implements Toast {
	private final NotificationToastData notification;

	private final long duration;
	private final IconKJS icon;
	private final List<FormattedCharSequence> text;
	private int width, height;

	private long lastChanged;
	private boolean changed;

	public NotificationToast(Minecraft mc, NotificationToastData notification) {
		this.notification = notification;
		this.duration = notification.duration().toMillis();

		this.icon = notification.icon();

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

	private void drawRectangle(Matrix4f m, int x0, int y0, int x1, int y1, int r, int g, int b) {
		RenderSystem.setShader(GameRenderer::getPositionColorShader);
		var buf = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
		buf.addVertex(m, x0, y1, 0F).setColor(r, g, b, 255);
		buf.addVertex(m, x1, y1, 0F).setColor(r, g, b, 255);
		buf.addVertex(m, x1, y0, 0F).setColor(r, g, b, 255);
		buf.addVertex(m, x0, y0, 0F).setColor(r, g, b, 255);
		BufferUploader.drawWithShader(buf.buildOrThrow());
	}

	@Override
	public Toast.Visibility render(GuiGraphics graphics, ToastComponent toastComponent, long l) {
		if (this.changed) {
			this.lastChanged = l;
			this.changed = false;
		}

		var mc = toastComponent.getMinecraft();

		var poseStack = graphics.pose();

		poseStack.pushPose();
		poseStack.translate(-2D, 2D, 0D);
		var m = poseStack.last().pose();
		int w = width();
		int h = height();

		int oc = notification.outlineColor().getRgbJS();
		int ocr = FastColor.ARGB32.red(oc);
		int ocg = FastColor.ARGB32.green(oc);
		int ocb = FastColor.ARGB32.blue(oc);

		int bc = notification.borderColor().getRgbJS();
		int bcr = FastColor.ARGB32.red(bc);
		int bcg = FastColor.ARGB32.green(bc);
		int bcb = FastColor.ARGB32.blue(bc);

		int bgc = notification.backgroundColor().getRgbJS();
		int bgcr = FastColor.ARGB32.red(bgc);
		int bgcg = FastColor.ARGB32.green(bgc);
		int bgcb = FastColor.ARGB32.blue(bgc);

		RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		drawRectangle(m, 2, 0, w - 2, h, ocr, ocg, ocb);
		drawRectangle(m, 0, 2, w, h - 2, ocr, ocg, ocb);
		drawRectangle(m, 1, 1, w - 1, h - 1, ocr, ocg, ocb);
		drawRectangle(m, 2, 1, w - 2, h - 1, bcr, bcg, bcb);
		drawRectangle(m, 1, 2, w - 1, h - 2, bcr, bcg, bcb);
		drawRectangle(m, 2, 2, w - 2, h - 2, bgcr, bgcg, bgcb);

		if (icon != null) {
			icon.draw(mc, graphics, 14, h / 2, notification.iconSize());
		}

		int th = icon == null ? 6 : 26;
		int tv = (h - text.size() * 10) / 2 + 1;

		for (var i = 0; i < text.size(); i++) {
			graphics.drawString(mc.font, text.get(i), th, tv + i * 10, 0xFFFFFF, notification.textShadow());
		}

		poseStack.popPose();
		return l - this.lastChanged < duration ? Toast.Visibility.SHOW : Toast.Visibility.HIDE;
	}
}
