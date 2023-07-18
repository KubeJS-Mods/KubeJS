package dev.latvian.mods.kubejs.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;
import dev.latvian.mods.kubejs.bindings.TextWrapper;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.kubejs.util.NotificationBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public class NotificationToast implements Toast {
	public interface ToastIcon {
		void draw(Minecraft mc, PoseStack pose, int x, int y, int size);
	}

	public static final Map<Integer, BiFunction<Minecraft, String, ToastIcon>> ICONS = new HashMap<>(Map.of(
			1, TextureIcon::new,
			2, ItemIcon::new,
			3, AtlasIcon::of
	));

	public record TextureIcon(ResourceLocation texture) implements ToastIcon {
		public TextureIcon(Minecraft ignored, String icon) {
			this(new ResourceLocation(icon));
		}

		@Override
		public void draw(Minecraft mc, PoseStack pose, int x, int y, int size) {
			RenderSystem.setShaderTexture(0, texture);
			RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
			RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
			var m = pose.last().pose();

			int p0 = -size / 2;
			int p1 = p0 + size;

			var buf = Tesselator.getInstance().getBuilder();
			buf.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
			buf.vertex(m, x + p0, y + p1, 0F).uv(0F, 1F).color(255, 255, 255, 255).endVertex();
			buf.vertex(m, x + p1, y + p1, 0F).uv(1F, 1F).color(255, 255, 255, 255).endVertex();
			buf.vertex(m, x + p1, y + p0, 0F).uv(1F, 0F).color(255, 255, 255, 255).endVertex();
			buf.vertex(m, x + p0, y + p0, 0F).uv(0F, 0F).color(255, 255, 255, 255).endVertex();
			BufferUploader.drawWithShader(buf.end());
		}
	}

	public record ItemIcon(ItemStack stack) implements ToastIcon {
		public ItemIcon(Minecraft ignored, String icon) {
			this(ItemStackJS.of(icon));
		}

		@Override
		public void draw(Minecraft mc, PoseStack pose, int x, int y, int size) {
			var m = RenderSystem.getModelViewStack();
			m.pushPose();
			m.translate(x - 2D, y + 2D, 0D);
			float s = size / 16F;
			m.scale(s, s, s);
			RenderSystem.applyModelViewMatrix();
			mc.getItemRenderer().renderAndDecorateFakeItem(stack, -8, -8);
			m.popPose();
			RenderSystem.applyModelViewMatrix();
		}
	}

	public record AtlasIcon(TextureAtlasSprite sprite) implements ToastIcon {
		public static AtlasIcon of(Minecraft mc, String icon) {
			var s = icon.split("\\|");

			if (s.length == 2) {
				return new AtlasIcon(mc.getTextureAtlas(new ResourceLocation(s[0])).apply(new ResourceLocation(s[1])));
			} else {
				return new AtlasIcon(mc.getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(new ResourceLocation(icon)));
			}
		}

		@Override
		public void draw(Minecraft mc, PoseStack pose, int x, int y, int size) {
			RenderSystem.setShaderTexture(0, sprite.atlas().location());
			RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
			RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
			var m = pose.last().pose();

			int p0 = -size / 2;
			int p1 = p0 + size;

			float u0 = sprite.getU0();
			float v0 = sprite.getV0();
			float u1 = sprite.getU1();
			float v1 = sprite.getV1();

			var buf = Tesselator.getInstance().getBuilder();
			buf.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
			buf.vertex(m, x + p0, y + p1, 0F).uv(u0, v1).color(255, 255, 255, 255).endVertex();
			buf.vertex(m, x + p1, y + p1, 0F).uv(u1, v1).color(255, 255, 255, 255).endVertex();
			buf.vertex(m, x + p1, y + p0, 0F).uv(u1, v0).color(255, 255, 255, 255).endVertex();
			buf.vertex(m, x + p0, y + p0, 0F).uv(u0, v0).color(255, 255, 255, 255).endVertex();
			BufferUploader.drawWithShader(buf.end());
		}
	}

	private final NotificationBuilder notification;

	private final long duration;
	private final ToastIcon icon;
	private final List<FormattedCharSequence> text;
	private int width, height;

	private long lastChanged;
	private boolean changed;

	public NotificationToast(Minecraft mc, NotificationBuilder notification) {
		this.notification = notification;
		this.duration = notification.duration.toMillis();

		this.icon = ICONS.containsKey(this.notification.iconType) ? ICONS.get(this.notification.iconType).apply(mc, this.notification.icon) : null;

		this.text = new ArrayList<>(2);
		this.width = 0;
		this.height = 0;

		if (!TextWrapper.isEmpty(notification.text)) {
			this.text.addAll(mc.font.split(notification.text, 240));
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
		var buf = Tesselator.getInstance().getBuilder();
		buf.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
		buf.vertex(m, x0, y1, 0F).color(r, g, b, 255).endVertex();
		buf.vertex(m, x1, y1, 0F).color(r, g, b, 255).endVertex();
		buf.vertex(m, x1, y0, 0F).color(r, g, b, 255).endVertex();
		buf.vertex(m, x0, y0, 0F).color(r, g, b, 255).endVertex();
		BufferUploader.drawWithShader(buf.end());
	}

	@Override
	public Toast.Visibility render(PoseStack poseStack, ToastComponent toastComponent, long l) {
		if (this.changed) {
			this.lastChanged = l;
			this.changed = false;
		}

		var mc = toastComponent.getMinecraft();

		poseStack.pushPose();
		poseStack.translate(-2D, 2D, 0D);
		var m = poseStack.last().pose();
		int w = width();
		int h = height();

		int oc = notification.outlineColor.getRgbJS();
		int ocr = FastColor.ARGB32.red(oc);
		int ocg = FastColor.ARGB32.green(oc);
		int ocb = FastColor.ARGB32.blue(oc);

		int bc = notification.borderColor.getRgbJS();
		int bcr = FastColor.ARGB32.red(bc);
		int bcg = FastColor.ARGB32.green(bc);
		int bcb = FastColor.ARGB32.blue(bc);

		int bgc = notification.backgroundColor.getRgbJS();
		int bgcr = FastColor.ARGB32.red(bgc);
		int bgcg = FastColor.ARGB32.green(bgc);
		int bgcb = FastColor.ARGB32.blue(bgc);

		RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
		RenderSystem.disableTexture();
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		drawRectangle(m, 2, 0, w - 2, h, ocr, ocg, ocb);
		drawRectangle(m, 0, 2, w, h - 2, ocr, ocg, ocb);
		drawRectangle(m, 1, 1, w - 1, h - 1, ocr, ocg, ocb);
		drawRectangle(m, 2, 1, w - 2, h - 1, bcr, bcg, bcb);
		drawRectangle(m, 1, 2, w - 1, h - 2, bcr, bcg, bcb);
		drawRectangle(m, 2, 2, w - 2, h - 2, bgcr, bgcg, bgcb);
		RenderSystem.enableTexture();

		if (icon != null) {
			icon.draw(mc, poseStack, 14, h / 2, notification.iconSize);
		}

		int th = icon == null ? 6 : 26;
		int tv = (h - text.size() * 10) / 2 + 1;

		for (var i = 0; i < text.size(); i++) {
			var line = text.get(i);

			if (notification.textShadow) {
				mc.font.drawShadow(poseStack, line, th, tv + i * 10, 0xFFFFFF);
			} else {
				mc.font.draw(poseStack, line, th, tv + i * 10, 0xFFFFFF);
			}
		}

		poseStack.popPose();
		return l - this.lastChanged < duration ? Toast.Visibility.SHOW : Toast.Visibility.HIDE;
	}
}
