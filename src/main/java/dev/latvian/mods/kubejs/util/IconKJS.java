package dev.latvian.mods.kubejs.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.serialization.Codec;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public interface IconKJS {
	IconKJS NONE = new IconKJS() {
	};

	Codec<IconKJS> CODEC = Codec.unit(NONE);

	StreamCodec<RegistryFriendlyByteBuf, IconKJS> STREAM_CODEC = new StreamCodec<>() {
		@Override
		public void encode(RegistryFriendlyByteBuf buf, IconKJS icon) {
			if (icon == NONE) {
				buf.writeByte(0);
			} else if (icon instanceof FromTexture t) {
				buf.writeByte(1);
				buf.writeResourceLocation(t.texture);
			} else if (icon instanceof FromItem i) {
				buf.writeByte(2);
				ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, i.stack);
			} else if (icon instanceof FromAtlasSprite s) {
				if (s.atlas.equals(InventoryMenu.BLOCK_ATLAS)) {
					buf.writeByte(3);
					buf.writeResourceLocation(s.icon);
				} else {
					buf.writeByte(4);
					buf.writeResourceLocation(s.atlas);
					buf.writeResourceLocation(s.icon);
				}
			} else {
				throw new IllegalArgumentException("Unknown icon type: " + icon);
			}
		}

		@Override
		public IconKJS decode(RegistryFriendlyByteBuf buf) {
			return switch (buf.readByte()) {
				case 1 -> new FromTexture(buf.readResourceLocation());
				case 2 -> new FromItem(ItemStack.OPTIONAL_STREAM_CODEC.decode(buf));
				case 3 -> new FromAtlasSprite(InventoryMenu.BLOCK_ATLAS, buf.readResourceLocation());
				case 4 -> new FromAtlasSprite(buf.readResourceLocation(), buf.readResourceLocation());
				default -> NONE;
			};
		}
	};

	record FromTexture(ResourceLocation texture) implements IconKJS {
		@Override
		@OnlyIn(Dist.CLIENT)
		public void draw(Minecraft mc, GuiGraphics graphics, int x, int y, int size) {
			RenderSystem.setShaderTexture(0, texture);
			RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
			RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
			var m = graphics.pose().last().pose();

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

	record FromItem(ItemStack stack) implements IconKJS {
		@Override
		@OnlyIn(Dist.CLIENT)
		public void draw(Minecraft mc, GuiGraphics graphics, int x, int y, int size) {
			var m = RenderSystem.getModelViewStack();
			m.pushMatrix();
			m.translate(x - 2F, y + 2F, 0F);
			float s = size / 16F;
			m.scale(s, s, s);
			RenderSystem.applyModelViewMatrix();
			graphics.renderFakeItem(stack, -8, -8);
			m.popMatrix();
			RenderSystem.applyModelViewMatrix();
		}
	}

	record FromAtlasSprite(ResourceLocation atlas, ResourceLocation icon) implements IconKJS {
		@Override
		@OnlyIn(Dist.CLIENT)
		public void draw(Minecraft mc, GuiGraphics graphics, int x, int y, int size) {
			var sprite = mc.getTextureAtlas(atlas).apply(icon);

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

			var buf = Tesselator.getInstance().getBuilder();
			buf.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
			buf.vertex(m, x + p0, y + p1, 0F).uv(u0, v1).color(255, 255, 255, 255).endVertex();
			buf.vertex(m, x + p1, y + p1, 0F).uv(u1, v1).color(255, 255, 255, 255).endVertex();
			buf.vertex(m, x + p1, y + p0, 0F).uv(u1, v0).color(255, 255, 255, 255).endVertex();
			buf.vertex(m, x + p0, y + p0, 0F).uv(u0, v0).color(255, 255, 255, 255).endVertex();
			BufferUploader.drawWithShader(buf.end());
		}
	}

	@OnlyIn(Dist.CLIENT)
	default void draw(Minecraft mc, GuiGraphics graphics, int x, int y, int size) {
	}
}
