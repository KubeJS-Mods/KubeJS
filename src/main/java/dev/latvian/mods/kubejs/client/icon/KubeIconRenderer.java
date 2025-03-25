package dev.latvian.mods.kubejs.client.icon;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import dev.latvian.mods.kubejs.util.Cast;
import dev.latvian.mods.kubejs.util.Lazy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Function;

public interface KubeIconRenderer {
	interface Registry {
		<T extends KubeIcon> void register(KubeIconType<T> type, Function<T, KubeIconRenderer> factory);
	}

	Lazy<Map<KubeIconType<?>, Function<?, KubeIconRenderer>>> RENDERERS = Lazy.map(map -> {
		Registry registry = map::put;
		registry.register(TextureKubeIcon.TYPE, FromTexture::new);
		registry.register(AtlasSpriteKubeIcon.TYPE, FromAtlasSprite::new);
		registry.register(ItemKubeIcon.TYPE, FromItem::new);
		// FIXME: Allow custom renderers
	});

	@Nullable
	static KubeIconRenderer from(KubeIcon icon) {
		var factory = RENDERERS.get().get(icon.getType());
		return factory != null ? factory.apply(Cast.to(icon)) : null;
	}

	void draw(Minecraft mc, GuiGraphics graphics, int x, int y, int size);

	record FromTexture(TextureKubeIcon icon) implements KubeIconRenderer {
		@Override
		public void draw(Minecraft mc, GuiGraphics graphics, int x, int y, int size) {
			RenderSystem.setShaderTexture(0, icon.texture());
			RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
			RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
			var m = graphics.pose().last().pose();

			int p0 = -size / 2;
			int p1 = p0 + size;

			var buf = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
			buf.addVertex(m, x + p0, y + p1, 0F).setUv(0F, 1F).setColor(255, 255, 255, 255);
			buf.addVertex(m, x + p1, y + p1, 0F).setUv(1F, 1F).setColor(255, 255, 255, 255);
			buf.addVertex(m, x + p1, y + p0, 0F).setUv(1F, 0F).setColor(255, 255, 255, 255);
			buf.addVertex(m, x + p0, y + p0, 0F).setUv(0F, 0F).setColor(255, 255, 255, 255);
			BufferUploader.drawWithShader(buf.buildOrThrow());
		}
	}

	record FromAtlasSprite(AtlasSpriteKubeIcon icon) implements KubeIconRenderer {
		@Override
		public void draw(Minecraft mc, GuiGraphics graphics, int x, int y, int size) {
			var sprite = (icon.atlas().isEmpty() ? mc.kjs$getBlockTextureAtlas() : mc.getTextureAtlas(icon.atlas().get())).apply(icon.sprite());

			RenderSystem.setShaderTexture(0, sprite.atlasLocation());
			RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
			RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
			var m = graphics.pose().last().pose();

			int p0 = -size / 2;
			int p1 = p0 + size;

			float u0 = sprite.getU0();
			float v0 = sprite.getV0();
			float u1 = sprite.getU1();
			float v1 = sprite.getV1();

			var buf = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
			buf.addVertex(m, x + p0, y + p1, 0F).setUv(u0, v1).setColor(255, 255, 255, 255);
			buf.addVertex(m, x + p1, y + p1, 0F).setUv(u1, v1).setColor(255, 255, 255, 255);
			buf.addVertex(m, x + p1, y + p0, 0F).setUv(u1, v0).setColor(255, 255, 255, 255);
			buf.addVertex(m, x + p0, y + p0, 0F).setUv(u0, v0).setColor(255, 255, 255, 255);
			BufferUploader.drawWithShader(buf.buildOrThrow());
		}
	}

	record FromItem(ItemKubeIcon icon) implements KubeIconRenderer {
		@Override
		public void draw(Minecraft mc, GuiGraphics graphics, int x, int y, int size) {
			var m = RenderSystem.getModelViewStack();
			m.pushMatrix();
			m.translate(x - 2F, y + 2F, 0F);
			float s = size / 16F;
			m.scale(s, s, s);
			RenderSystem.applyModelViewMatrix();
			graphics.renderFakeItem(icon.item(), -8, -8);
			m.popMatrix();
			RenderSystem.applyModelViewMatrix();
		}
	}
}
