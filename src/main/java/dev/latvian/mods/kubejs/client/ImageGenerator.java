package dev.latvian.mods.kubejs.client;

import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexSorting;
import com.mojang.brigadier.StringReader;
import dev.latvian.mods.kubejs.bindings.BlockWrapper;
import dev.latvian.mods.kubejs.component.DataComponentWrapper;
import dev.latvian.mods.kubejs.web.http.HTTPContext;
import dev.latvian.mods.kubejs.web.http.HTTPResponse;
import dev.latvian.mods.kubejs.web.http.SimpleHTTPResponse;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import org.joml.Matrix4f;

import java.util.function.Consumer;

public class ImageGenerator {
	private record RenderImage(Minecraft mc, GuiGraphics graphics, int size) {
	}

	public static final Int2ObjectMap<TextureTarget> FB_CACHE = new Int2ObjectArrayMap<>();

	private static HTTPResponse renderCanvas(HTTPContext ctx, int canvasSize, Consumer<RenderImage> render) {
		int size = Integer.parseInt(ctx.variables().get("size"));

		if (size < 1 || size > 1024) {
			return SimpleHTTPResponse.text(400, "Invalid size, must be [1, 1024]");
		}

		var bytes = ctx.supplyInRenderThread(() -> {
			var target = FB_CACHE.get(size);

			if (target == null) {
				target = new TextureTarget(size, size, true, Minecraft.ON_OSX);
				FB_CACHE.put(size, target);
			}

			var mc = Minecraft.getInstance();

			/*
			mc.renderBuffers().bufferSource().endBatch();

			target.clear(Minecraft.ON_OSX);
			target.bindWrite(true);

			RenderSystem.setProjectionMatrix(new Matrix4f().setOrtho(0, canvasSize, canvasSize, 0, 1000F, 3000F), VertexSorting.ORTHOGRAPHIC_Z);

			var view = RenderSystem.getModelViewStack();
			view.pushMatrix();
			view.translate(0F, 0F, 0F);
			RenderSystem.applyModelViewMatrix();

			var graphics = new GuiGraphics(mc, mc.renderBuffers().bufferSource());
			RenderSystem.disableCull();
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
			render.accept(new RenderImage(mc, graphics, size));
			graphics.flush();

			try (var img = new NativeImage(size, size, false)) {
				target.bindRead();
				RenderSystem.bindTexture(target.getColorTextureId());
				img.downloadTexture(0, false);
				img.flipY();
				return img.asByteArray();
			} catch (Exception ex) {
				ex.printStackTrace();
				return new byte[0];
			} finally {
				target.unbindRead();
				target.unbindWrite();
				mc.getMainRenderTarget().bindWrite(true);

				view.popMatrix();
				RenderSystem.applyModelViewMatrix();
			}
			 */

			var bufferSource = mc.renderBuffers().bufferSource();

			target.clear(Minecraft.ON_OSX);
			target.bindWrite(true);
			RenderSystem.setProjectionMatrix(new Matrix4f().setOrtho(0, canvasSize, canvasSize, 0, -1000F, 1000F), VertexSorting.ORTHOGRAPHIC_Z);

			var view = RenderSystem.getModelViewStack();
			view.pushMatrix();
			view.translation(0F, 0F, 0F);

			RenderSystem.applyModelViewMatrix();

			GuiGraphics graphics = new GuiGraphics(mc, bufferSource);
			render.accept(new RenderImage(mc, graphics, size));
			graphics.flush();

			target.bindRead();
			RenderSystem.bindTexture(target.getColorTextureId());

			try (var image = new NativeImage(size, size, false)) {
				image.downloadTexture(0, false);
				image.flipY();
				return image.asByteArray();
			} catch (Exception ex) {
				ex.printStackTrace();
				return null;
			} finally {
				target.unbindRead();
				target.unbindWrite();

				view.popMatrix();
				RenderSystem.applyModelViewMatrix();
			}
		});

		return new SimpleHTTPResponse(200, bytes, "image/png");
	}

	public static HTTPResponse item(HTTPContext ctx) throws Exception {
		var stack = BuiltInRegistries.ITEM.get(ctx.id()).getDefaultInstance();
		stack.applyComponents(DataComponentWrapper.readPatch(ctx.registries().nbt(), new StringReader(ctx.query().getOrDefault("components", ""))));

		if (stack.isEmpty()) {
			return HTTPResponse.NOT_FOUND;
		}

		return renderCanvas(ctx, 16, render -> {
			render.graphics.renderFakeItem(stack, 0, 0, 0);
			render.graphics.renderItemDecorations(render.mc.font, stack, 0, 0);
		});
	}

	public static HTTPResponse block(HTTPContext ctx) throws Exception {
		var state = BlockWrapper.withProperties(BuiltInRegistries.BLOCK.get(ctx.id()).defaultBlockState(), ctx.query());

		if (state.isEmpty()) {
			return HTTPResponse.NOT_FOUND;
		}

		return renderCanvas(ctx, 16, render -> {
			render.graphics.fill(0, 0, 16, 16, 0xFFFF00FF);
		});
	}

	public static HTTPResponse fluid(HTTPContext ctx) throws Exception {
		var stack = new FluidStack(BuiltInRegistries.FLUID.get(ctx.id()), FluidType.BUCKET_VOLUME);
		stack.applyComponents(DataComponentWrapper.readPatch(ctx.registries().nbt(), new StringReader(ctx.query().getOrDefault("components", ""))));

		if (stack.isEmpty()) {
			return HTTPResponse.NOT_FOUND;
		}

		var fluidInfo = IClientFluidTypeExtensions.of(stack.getFluid());
		var still = fluidInfo.getStillTexture(stack);
		var tint = fluidInfo.getTintColor(stack);
		int a = 255;
		int r = (tint >> 16) & 0xFF;
		int g = (tint >> 8) & 0xFF;
		int b = tint & 0xFF;

		return renderCanvas(ctx, 16, render -> {
			var s = render.mc.getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(still);
			RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);
			RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
			var builder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
			var m = render.graphics.pose().last().pose();
			builder.addVertex(m, 0F, 0F, 0F).setUv(s.getU0(), s.getV1()).setColor(r, g, b, a);
			builder.addVertex(m, 0F, 16F, 0F).setUv(s.getU0(), s.getV0()).setColor(r, g, b, a);
			builder.addVertex(m, 16F, 16F, 0F).setUv(s.getU1(), s.getV0()).setColor(r, g, b, a);
			builder.addVertex(m, 16F, 0F, 0F).setUv(s.getU1(), s.getV1()).setColor(r, g, b, a);
			BufferUploader.drawWithShader(builder.buildOrThrow());
		});
	}

	public static HTTPResponse itemTag(HTTPContext ctx) throws Exception {
		var tagKey = ItemTags.create(ctx.id());

		return renderCanvas(ctx, 16, render -> {
			// render.graphics.fill(0, 0, 16, 16, 0xFFFF00FF);
			render.graphics.renderFakeItem(Items.NAME_TAG.getDefaultInstance(), 0, 0, 0);
		});
	}

	public static HTTPResponse blockTag(HTTPContext ctx) throws Exception {
		var tagKey = BlockTags.create(ctx.id());

		return renderCanvas(ctx, 16, render -> {
			// render.graphics.fill(0, 0, 16, 16, 0xFFFF00FF);
			render.graphics.renderFakeItem(Items.NAME_TAG.getDefaultInstance(), 0, 0, 0);
		});
	}

	public static HTTPResponse fluidTag(HTTPContext ctx) throws Exception {
		var tagKey = FluidTags.create(ctx.id());

		return renderCanvas(ctx, 16, render -> {
			// render.graphics.fill(0, 0, 16, 16, 0xFFFF00FF);
			render.graphics.renderFakeItem(Items.NAME_TAG.getDefaultInstance(), 0, 0, 0);
		});
	}
}
