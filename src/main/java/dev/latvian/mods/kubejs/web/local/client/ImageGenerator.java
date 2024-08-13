package dev.latvian.mods.kubejs.web.local.client;

import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexSorting;
import dev.latvian.apps.tinyserver.http.response.HTTPResponse;
import dev.latvian.apps.tinyserver.http.response.HTTPStatus;
import dev.latvian.mods.kubejs.KubeJSPaths;
import dev.latvian.mods.kubejs.bindings.BlockWrapper;
import dev.latvian.mods.kubejs.bindings.UUIDWrapper;
import dev.latvian.mods.kubejs.component.DataComponentWrapper;
import dev.latvian.mods.kubejs.web.KJSHTTPRequest;
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

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.UUID;
import java.util.function.Consumer;

public class ImageGenerator {
	private record RenderImage(Minecraft mc, GuiGraphics graphics, int size) {
	}

	public static final Int2ObjectMap<TextureTarget> FB_CACHE = new Int2ObjectArrayMap<>();

	private static HTTPResponse renderCanvas(KJSHTTPRequest ctx, int canvasSize, StringBuilder cacheId, Consumer<RenderImage> render) {
		int size = Integer.parseInt(ctx.variables().get("size"));

		if (size < 1 || size > 1024) {
			return HTTPStatus.BAD_REQUEST.text("Invalid size, must be [1, 1024]");
		}

		if (!cacheId.isEmpty()) {
			cacheId.append(size);
		}

		if (cacheId.isEmpty() && ctx.header("Accept").equals("text/plain")) {
			return HTTPStatus.NOT_FOUND;
		}

		var cacheUUID = cacheId.isEmpty() ? null : UUIDWrapper.toString(UUID.nameUUIDFromBytes(cacheId.toString().getBytes(StandardCharsets.UTF_8)));
		var cachePath = cacheUUID == null ? null : KubeJSPaths.dir(KubeJSPaths.LOCAL_WEB_IMG_CACHE.resolve(cacheUUID.substring(0, 2))).resolve(cacheUUID + ".png");

		if (cachePath != null && Files.exists(cachePath)) {
			if (ctx.header("Accept").equals("text/plain")) {
				return HTTPResponse.ok().text(cacheUUID);
			}

			return HTTPResponse.ok().content(cachePath).header("X-KubeJS-Cache-Key", cacheUUID);
		}

		var bytes = ctx.supplyInRenderThread(() -> {
			var target = FB_CACHE.get(size);

			if (target == null) {
				target = new TextureTarget(size, size, true, Minecraft.ON_OSX);
				FB_CACHE.put(size, target);
			}

			var mc = Minecraft.getInstance();
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

		if (cachePath != null) {
			try {
				Files.write(cachePath, bytes);
			} catch (Exception ignore) {
			}
		}

		if (ctx.header("Accept").equals("text/plain")) {
			if (cachePath == null) {
				return HTTPStatus.NOT_FOUND;
			}

			return HTTPResponse.ok().text(cacheUUID);
		}

		return HTTPResponse.ok().content(bytes, "image/png").header("X-KubeJS-Cache-Key", cacheUUID);
	}

	public static HTTPResponse item(KJSHTTPRequest ctx) throws Exception {
		var stack = BuiltInRegistries.ITEM.get(ctx.id()).getDefaultInstance();
		stack.applyComponents(ctx.queryAsPatch(ctx.registries().nbt()));

		if (stack.isEmpty()) {
			return HTTPStatus.NOT_FOUND;
		}

		var sb = new StringBuilder();
		sb.append(stack.kjs$getId());
		DataComponentWrapper.writeVisualComponentsForCache(sb, ctx.registries().nbt(), stack.getComponents());

		return renderCanvas(ctx, 16, sb, render -> {
			render.graphics.renderFakeItem(stack, 0, 0, 0);
			render.graphics.renderItemDecorations(render.mc.font, stack, 0, 0);
		});
	}

	public static HTTPResponse block(KJSHTTPRequest ctx) throws Exception {
		var state = BlockWrapper.withProperties(BuiltInRegistries.BLOCK.get(ctx.id()).defaultBlockState(), ctx.query());

		if (state.isEmpty()) {
			return HTTPStatus.NOT_FOUND;
		}

		var sb = new StringBuilder();
		sb.append(state.kjs$getId());

		for (var entry : ctx.query().entrySet()) {
			sb.append(entry.getKey());
			sb.append(entry.getValue());
		}

		return renderCanvas(ctx, 16, sb, render -> {
			render.graphics.fill(0, 0, 16, 16, 0xFFFF00FF);
		});
	}

	public static HTTPResponse fluid(KJSHTTPRequest ctx) throws Exception {
		var stack = new FluidStack(BuiltInRegistries.FLUID.get(ctx.id()), FluidType.BUCKET_VOLUME);
		stack.applyComponents(ctx.queryAsPatch(ctx.registries().nbt()));

		if (stack.isEmpty()) {
			return HTTPStatus.NOT_FOUND;
		}

		var fluidInfo = IClientFluidTypeExtensions.of(stack.getFluid());
		var still = fluidInfo.getStillTexture(stack);
		var tint = fluidInfo.getTintColor(stack);
		int a = 255;
		int r = (tint >> 16) & 0xFF;
		int g = (tint >> 8) & 0xFF;
		int b = tint & 0xFF;

		var sb = new StringBuilder();
		sb.append(stack.getFluid().kjs$getId());
		DataComponentWrapper.writeVisualComponentsForCache(sb, ctx.registries().nbt(), stack.getComponents());

		return renderCanvas(ctx, 16, sb, render -> {
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

	public static HTTPResponse itemTag(KJSHTTPRequest ctx) throws Exception {
		var tagKey = ItemTags.create(ctx.id());

		return renderCanvas(ctx, 16, new StringBuilder(), render -> {
			// render.graphics.fill(0, 0, 16, 16, 0xFFFF00FF);
			render.graphics.renderFakeItem(Items.NAME_TAG.getDefaultInstance(), 0, 0, 0);
		});
	}

	public static HTTPResponse blockTag(KJSHTTPRequest ctx) throws Exception {
		var tagKey = BlockTags.create(ctx.id());

		return renderCanvas(ctx, 16, new StringBuilder(), render -> {
			// render.graphics.fill(0, 0, 16, 16, 0xFFFF00FF);
			render.graphics.renderFakeItem(Items.NAME_TAG.getDefaultInstance(), 0, 0, 0);
		});
	}

	public static HTTPResponse fluidTag(KJSHTTPRequest ctx) throws Exception {
		var tagKey = FluidTags.create(ctx.id());

		return renderCanvas(ctx, 16, new StringBuilder(), render -> {
			// render.graphics.fill(0, 0, 16, 16, 0xFFFF00FF);
			render.graphics.renderFakeItem(Items.NAME_TAG.getDefaultInstance(), 0, 0, 0);
		});
	}
}
