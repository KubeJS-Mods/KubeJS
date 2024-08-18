package dev.latvian.mods.kubejs.web.local.client;

import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.Lighting;
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
import dev.latvian.mods.kubejs.util.CachedComponentObject;
import dev.latvian.mods.kubejs.web.KJSHTTPRequest;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.UUID;
import java.util.function.Consumer;

public class ImageGenerator {
	public static final ItemTransform ROTATED_BLOCK_TRANSFORM = new ItemTransform(
		new Vector3f(30F, 225F, 0F),
		new Vector3f(0F, 0F, 0F),
		new Vector3f(0.625F, 0.625F, 0.625F)
	);

	private record RenderImage(Minecraft mc, GuiGraphics graphics, int size) {
	}

	public static final Int2ObjectMap<TextureTarget> FB_CACHE = new Int2ObjectArrayMap<>();

	private static HTTPResponse renderCanvas(KJSHTTPRequest req, int canvasSize, String dir, StringBuilder cacheId, Consumer<RenderImage> render) {
		return renderCanvas(req, canvasSize, dir, cacheId.isEmpty() ? null : UUID.nameUUIDFromBytes(cacheId.toString().getBytes(StandardCharsets.UTF_8)), render);
	}

	private static HTTPResponse renderCanvas(KJSHTTPRequest req, int canvasSize, String dir, @Nullable UUID cacheUUID, Consumer<RenderImage> render) {
		int size = Integer.parseInt(req.variables().get("size"));

		if (size < 1 || size > 1024) {
			return HTTPStatus.BAD_REQUEST.text("Invalid size, must be [1, 1024]");
		}

		var cacheUUIDStr = cacheUUID == null || req.query().containsKey("uncached") ? null : UUIDWrapper.toString(cacheUUID);
		var cachePath = cacheUUIDStr == null ? null : KubeJSPaths.dir(KubeJSPaths.LOCAL.resolve("cache/web/img/" + dir + "/" + cacheUUIDStr.substring(0, 2))).resolve(cacheUUIDStr + "_" + size + ".png");

		if (cachePath != null && Files.exists(cachePath)) {
			return HTTPResponse.ok().content(cachePath).header("X-KubeJS-Cache-Key", cacheUUIDStr);
		}

		var bytes = req.supplyInRenderThread(() -> {
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

		if (req.header("Accept").equals("text/plain")) {
			if (cachePath == null) {
				return HTTPStatus.NOT_FOUND;
			}

			return HTTPResponse.ok().text(cacheUUIDStr);
		}

		return HTTPResponse.ok().content(bytes, "image/png").header("X-KubeJS-Cache-Key", cacheUUIDStr);
	}

	public static HTTPResponse item(KJSHTTPRequest req) throws Exception {
		var stack = BuiltInRegistries.ITEM.get(req.id()).getDefaultInstance();
		stack.applyComponents(req.components(req.registries().nbt()));

		if (stack.isEmpty()) {
			return HTTPStatus.NOT_FOUND;
		}

		return renderCanvas(req, 16, "item", CachedComponentObject.ofItemStack(stack, true).cacheKey(), render -> {
			render.graphics.renderFakeItem(stack, 0, 0, 0);
			render.graphics.renderItemDecorations(render.mc.font, stack, 0, 0);
		});
	}

	public static HTTPResponse block(KJSHTTPRequest req) throws Exception {
		var state = BlockWrapper.withProperties(BuiltInRegistries.BLOCK.get(req.id()).defaultBlockState(), req.query());

		if (state.isEmpty()) {
			return HTTPStatus.NOT_FOUND;
		}

		var sb = new StringBuilder();
		sb.append(state.kjs$getId());

		for (var entry : req.query().entrySet()) {
			sb.append(entry.getKey());
			sb.append(entry.getValue());
		}

		return renderCanvas(req, 16, "block", sb, render -> {
			var model = render.mc.getBlockRenderer().getBlockModel(state);
			var pose = render.graphics.pose();
			pose.pushPose();
			pose.translate(8F, 8F, 150F);
			pose.scale(16F, -16F, 16F);

			boolean flag = !model.usesBlockLight();

			if (flag) {
				Lighting.setupForFlatItems();
			}

			// model = ClientHooks.handleCameraTransforms(pose, model, ItemDisplayContext.GUI, false);
			ROTATED_BLOCK_TRANSFORM.apply(false, pose);
			pose.translate(-0.5F, -0.5F, -0.5F);

			for (var renderType : model.getRenderTypes(state, RandomSource.create(0L), ModelData.EMPTY)) {
				render.mc.getBlockRenderer().renderSingleBlock(state, pose, render.graphics.bufferSource(), 15728880, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, renderType);
			}

			try {
				var fluidState = state.getFluidState();

				if (!fluidState.is(Fluids.EMPTY)) {
					var world = new FakeClientWorld(render.mc.level, state, Biomes.THE_VOID);
					render.mc.getBlockRenderer().renderLiquid(BlockPos.ZERO, world, new MovedVertexConsumer(render.graphics.bufferSource().getBuffer(ItemBlockRenderTypes.getRenderLayer(fluidState)), pose.last()), state, fluidState);
				}
			} catch (Exception ignored) {
			}

			render.graphics.flush();

			if (flag) {
				Lighting.setupFor3DItems();
			}

			render.graphics.pose().popPose();
		});
	}

	public static HTTPResponse fluid(KJSHTTPRequest req) throws Exception {
		var stack = new FluidStack(BuiltInRegistries.FLUID.get(req.id()), FluidType.BUCKET_VOLUME);
		stack.applyComponents(req.components(req.registries().nbt()));

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

		return renderCanvas(req, 16, "fluid", CachedComponentObject.ofFluidStack(stack, true).cacheKey(), render -> {
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

	public static HTTPResponse itemTag(KJSHTTPRequest req) throws Exception {
		var tagKey = ItemTags.create(req.id());

		return renderCanvas(req, 16, "item_tag", new StringBuilder(), render -> {
			// render.graphics.fill(0, 0, 16, 16, 0xFFFF00FF);
			render.graphics.renderFakeItem(Items.NAME_TAG.getDefaultInstance(), 0, 0, 0);
		});
	}

	public static HTTPResponse blockTag(KJSHTTPRequest req) throws Exception {
		var tagKey = BlockTags.create(req.id());

		return renderCanvas(req, 16, "block_tag", new StringBuilder(), render -> {
			// render.graphics.fill(0, 0, 16, 16, 0xFFFF00FF);
			render.graphics.renderFakeItem(Items.NAME_TAG.getDefaultInstance(), 0, 0, 0);
		});
	}

	public static HTTPResponse fluidTag(KJSHTTPRequest req) throws Exception {
		var tagKey = FluidTags.create(req.id());

		return renderCanvas(req, 16, "fluid_tag", new StringBuilder(), render -> {
			// render.graphics.fill(0, 0, 16, 16, 0xFFFF00FF);
			render.graphics.renderFakeItem(Items.NAME_TAG.getDefaultInstance(), 0, 0, 0);
		});
	}
}
