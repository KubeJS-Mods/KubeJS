package dev.latvian.mods.kubejs.web.local.client;

public class ImageGenerator {
/*	public static final ItemTransform ROTATED_BLOCK_TRANSFORM = new ItemTransform(
		new Vector3f(30F, 225F, 0F),
		new Vector3f(0F, 0F, 0F),
		new Vector3f(0.625F, 0.625F, 0.625F)
	);

	public static final Identifier WILDCARD_TEXTURE = KubeJS.id("textures/misc/wildcard.png");

	private record RenderImage(Minecraft mc, GuiGraphicsExtractor graphics, int size) {
	}

	public record CachedImage(HTTPResponse response, @Nullable String pathStr) {
	}

	private record BodyKey(byte[] bytes) {
		@Override
		public int hashCode() {
			return Arrays.hashCode(bytes);
		}

		@Override
		public boolean equals(Object o) {
			return this == o || o instanceof BodyKey(byte[] bytes1) && Arrays.equals(bytes, bytes1);
		}
	}

	public static final Int2ObjectMap<TextureTarget> FB_CACHE = new Int2ObjectArrayMap<>();

	public static TextureTarget getCanvas(int size) {
		var target = FB_CACHE.get(size);

		if (target == null) {
			target = new TextureTarget(null, size, size, true);
			// setClearColor no longer exists; handle clear color via RenderPass if needed
			FB_CACHE.put(size, target);
		}

		return target;
	}

	private static CachedImage renderCanvas(KJSHTTPRequest req, int canvasSize, int imageSize, String dir, @Nullable ByteBuf cacheBuf, boolean wildcard, Consumer<RenderImage> render) {
		int size = imageSize > 0 ? imageSize : req.variable("size").asInt();

		if (size < 1 || size > 1024) {
			return new CachedImage(HTTPStatus.BAD_REQUEST.text("Invalid size, must be [1, 1024]"), null);
		}

		if (req.query().containsKey("uncached")) {
			cacheBuf = null;
		}

		if (cacheBuf != null) {
			cacheBuf.writeBoolean(wildcard);
		}

		var cacheUUIDStr = cacheBuf == null ? null : UUIDWrapper.toString(UUID.nameUUIDFromBytes(cacheBuf.array()));
		var cachePath = cacheUUIDStr == null ? null : KubeJSPaths.dir(KubeJSPaths.LOCAL.resolve("cache/web/img/" + dir + "/" + cacheUUIDStr.substring(0, 2))).resolve(cacheUUIDStr + "_" + size + ".png");

		if (cachePath != null && Files.exists(cachePath)) {
			var pathStr = KubeJSPaths.GAMEDIR.relativize(cachePath).toString().replace('\\', '/');
			return new CachedImage(HTTPResponse.ok().content(cachePath).header("X-KubeJS-Cache-Key", cacheUUIDStr).header("X-KubeJS-Cache-Path", pathStr), pathStr);
		}

		var bytes = req.supplyInMainThread(() -> {
			var target = getCanvas(size);
			var mc = Minecraft.getInstance();
			var bufferSource = mc.renderBuffers().bufferSource();

			// Clear by creating a render pass with a clear color
			var encoder = RenderSystem.getDevice().createCommandEncoder();
			encoder.clearColorTexture(target.getColorTexture(), 0x8A8A8A00); // ~0.54 grey, 0 alpha

			target.bindWrite(true);
			RenderSystem.setProjectionMatrix(new Matrix4f().setOrtho(0, canvasSize, canvasSize, 0, -1000F, 1000F), VertexSorting.ORTHOGRAPHIC_Z);

			var view = RenderSystem.getModelViewStack();
			view.pushMatrix();
			view.translation(0F, 0F, 0F);
			RenderSystem.applyModelViewMatrix();

			GuiGraphicsExtractor graphics = new GuiGraphicsExtractor(mc, bufferSource);
			render.accept(new RenderImage(mc, graphics, size));

			if (wildcard) {
				RenderSystem.enableBlend();
				RenderSystem.defaultBlendFunc();
				graphics.blit(WILDCARD_TEXTURE, 0, 0, 300, 0F, 0F, 16, 16, 16, 16);
			}

			graphics.flush();

			// Allocate a GpuBuffer to receive the texture data (size * size * 4 bytes for RGBA8)
			int byteCount = size * size * 4;
			var device = RenderSystem.getDevice();
			var readbackBuffer = device.createBuffer(() -> "ImageGenerator readback", GpuBuffer.Usage.COPY_DST | GpuBuffer.Usage.MAP_READ, byteCount);

			byte[][] result = {null};

			encoder = device.createCommandEncoder();
			encoder.copyTextureToBuffer(target.getColorTexture(), readbackBuffer, 0L, () -> {
				try (var mapped = device.createCommandEncoder().mapBuffer(readbackBuffer, true, false)) {
					var buf = mapped.data();
					try (var image = new NativeImage(size, size, false)) {
						// Copy buffer into NativeImage pixel by pixel, flipping Y
						for (int y = 0; y < size; y++) {
							for (int x = 0; x < size; x++) {
								int srcIdx = ((size - 1 - y) * size + x) * 4; // flipY
								int r = buf.get(srcIdx) & 0xFF;
								int g = buf.get(srcIdx + 1) & 0xFF;
								int b = buf.get(srcIdx + 2) & 0xFF;
								int a = buf.get(srcIdx + 3) & 0xFF;

								int argb;
								if (a == 0) {
									argb = 0;
								} else if (a < 255) {
									argb = (0xFF << 24) | (r << 16) | (g << 8) | b;
								} else {
									argb = (a << 24) | (r << 16) | (g << 8) | b;
								}
								image.setPixel(x, y, argb);
							}
						}
						result[0] = image.asByteArray();
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				} finally {
					readbackBuffer.close();
				}
			}, size);

			// Fence to ensure callback has completed before returning
			var fence = encoder.createFence();
			fence.awaitCompletion(10_000_000_000L); // 10s timeout in nanoseconds
			fence.close();

			view.popMatrix();
			RenderSystem.applyModelViewMatrix();

			return result[0];
		});

		if (cachePath != null) {
			try {
				Files.write(cachePath, bytes);
			} catch (Exception ignore) {
			}

			var pathStr = KubeJSPaths.GAMEDIR.relativize(cachePath).toString().replace('\\', '/');
			return new CachedImage(HTTPResponse.ok().content(bytes, "image/png").header("X-KubeJS-Cache-Key", cacheUUIDStr).header("X-KubeJS-Cache-Path", pathStr), pathStr);
		}

		return new CachedImage(HTTPResponse.ok().content(bytes, "image/png"), null);
	}

	private static CachedImage renderAnimated(KJSHTTPRequest req, String dir, @Nullable ByteBuf cacheBuf, List<CachedImage> images) throws Exception {
		int size = req.variable("size").asInt();

		if (size < 1 || size > 1024) {
			return new CachedImage(HTTPStatus.BAD_REQUEST.text("Invalid size, must be [1, 1024]"), null);
		}

		if (req.query().containsKey("uncached")) {
			cacheBuf = null;
		}

		var cacheUUIDStr = cacheBuf == null ? null : UUIDWrapper.toString(UUID.nameUUIDFromBytes(cacheBuf.array()));
		var cachePath = cacheUUIDStr == null ? null : KubeJSPaths.dir(KubeJSPaths.LOCAL.resolve("cache/web/img/" + dir + "/" + cacheUUIDStr.substring(0, 2))).resolve(cacheUUIDStr + "_" + size + ".gif");

		if (cachePath != null && Files.exists(cachePath)) {
			var pathStr = KubeJSPaths.GAMEDIR.relativize(cachePath).toString().replace('\\', '/');
			return new CachedImage(HTTPResponse.ok().content(cachePath).header("X-KubeJS-Cache-Key", cacheUUIDStr).header("X-KubeJS-Cache-Path", pathStr), pathStr);
		}

		var outputStream = new ByteArrayOutputStream();
		var encoder = new AnimatedGifEncoder();
		encoder.start(outputStream);
		encoder.setSize(size, size);
		encoder.setBackground(Color.BLUE);
		encoder.setTransparent(Color.BLUE, false);

		encoder.setRepeat(0);
		encoder.setDelay(1000);

		var bodyKeys = new HashSet<BodyKey>();

		req.runInMainThread(() -> {
			for (var image : images) {
				try {
					var content = new ContentGrabber(KubeJS.DISPLAY_NAME, req.startTime());
					image.response().build(content);

					if (content.body != null && bodyKeys.add(new BodyKey(content.body))) {
						encoder.addFrame(ImageIO.read(new ByteArrayInputStream(content.body)));
					}
				} catch (Exception ignore) {
				}
			}
		});

		encoder.finish();

		var bytes = outputStream.toByteArray();

		if (cachePath != null) {
			try {
				Files.write(cachePath, bytes);
			} catch (Exception ignore) {
			}

			var pathStr = KubeJSPaths.GAMEDIR.relativize(cachePath).toString().replace('\\', '/');
			return new CachedImage(HTTPResponse.ok().content(bytes, "image/gif").header("X-KubeJS-Cache-Key", cacheUUIDStr).header("X-KubeJS-Cache-Path", pathStr), pathStr);
		}

		return new CachedImage(HTTPResponse.ok().content(bytes, "image/gif"), null);
	}

	public static HTTPResponse renderAllItems(KJSHTTPRequest req) throws Exception {
		int size = req.variable("size").asInt();

		if (size < 1 || size > 1024) {
			return HTTPStatus.BAD_REQUEST.text("Invalid size, must be [1, 1024]");
		}

		return HTTPResponse.noContent();
	}

	public static HTTPResponse item(KJSHTTPRequest req) throws Exception {
		var stack = BuiltInRegistries.ITEM.get(req.id()).getDefaultInstance();
		stack.applyComponents(req.components(req.registries().nbt()));
		return renderItem(req, 0, stack, req.query().containsKey("wildcard")).response();
	}

	public static CachedImage renderItem(KJSHTTPRequest req, int imageSize, ItemStack stack, boolean wildcard) {
		if (stack.isEmpty()) {
			return new CachedImage(HTTPStatus.NOT_FOUND, null);
		}

		var buf = new FriendlyByteBuf(Unpooled.buffer());
		CachedComponentObject.writeCacheKey(buf, stack.getItem(), DataComponentWrapper.visualPatch(stack.getComponentsPatch()));

		return renderCanvas(req, 16, imageSize, "item", buf, wildcard, render -> {
			render.graphics.renderFakeItem(stack, 0, 0, 0);
			render.graphics.renderItemDecorations(render.mc.font, stack, 0, 0);
		});
	}

	public static HTTPResponse block(KJSHTTPRequest req) throws Exception {
		var state = BlockWrapper.withProperties(BuiltInRegistries.BLOCK.get(req.id()).defaultBlockState(), req.query());
		return renderBlock(req, state, req.query().containsKey("wildcard")).response();
	}

	public static CachedImage renderBlock(KJSHTTPRequest req, BlockState state, boolean wildcard) {
		if (state.isEmpty()) {
			return new CachedImage(HTTPStatus.NOT_FOUND, null);
		}

		var buf = new FriendlyByteBuf(Unpooled.buffer());
		buf.writeUtf(state.kjs$getId());
		buf.writeVarInt(state.getBlock().getStateDefinition().getProperties().size());

		for (var p : state.getProperties()) {
			buf.writeUtf(p.getName());

			if (p instanceof BooleanProperty p1) {
				buf.writeBoolean(state.getValue(p1));
			} else if (p instanceof IntegerProperty p1) {
				buf.writeVarInt(Cast.to(state.getValue(p1)));
			} else {
				buf.writeUtf(p.getName(Cast.to(state.getValue(p))));
			}
		}

		return renderCanvas(req, 16, 0, "block", buf, wildcard, render -> {
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
		return renderFluid(req, stack, req.query().containsKey("wildcard")).response();
	}

	public static CachedImage renderFluid(KJSHTTPRequest req, FluidStack stack, boolean wildcard) {
		if (stack.isEmpty()) {
			return new CachedImage(HTTPStatus.NOT_FOUND, null);
		}

		var fluidInfo = IClientFluidTypeExtensions.of(stack.getFluid());
		var still = fluidInfo.getStillTexture(stack);
		var tint = fluidInfo.getTintColor(stack);
		int a = 255;
		int r = (tint >> 16) & 0xFF;
		int g = (tint >> 8) & 0xFF;
		int b = tint & 0xFF;

		var buf = new FriendlyByteBuf(Unpooled.buffer());
		CachedComponentObject.writeCacheKey(buf, stack.getFluid(), DataComponentWrapper.visualPatch(stack.getComponentsPatch()));

		return renderCanvas(req, 16, 0, "fluid", buf, wildcard, render -> {
			var s = render.mc.kjs$getBlockTextureAtlas().apply(still);
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
		var tag = BuiltInRegistries.ITEM.getTag(ItemTags.create(req.id()));

		if (tag.isEmpty()) {
			return HTTPStatus.NOT_FOUND;
		}

		var buf = new FriendlyByteBuf(Unpooled.buffer());
		var list = new ArrayList<CachedImage>();

		for (var holder : tag.get()) {
			buf.writeUtf(holder.value().kjs$getId());
			list.add(renderItem(req, 0, holder.value().getDefaultInstance(), true));
		}

		return renderAnimated(req, "item_tag", buf, list).response();
	}

	public static HTTPResponse blockTag(KJSHTTPRequest req) throws Exception {
		var tag = BuiltInRegistries.BLOCK.getTag(BlockTags.create(req.id()));

		if (tag.isEmpty()) {
			return HTTPStatus.NOT_FOUND;
		}

		var buf = new FriendlyByteBuf(Unpooled.buffer());
		var list = new ArrayList<CachedImage>();

		for (var holder : tag.get()) {
			buf.writeUtf(holder.value().kjs$getId());

			var item = holder.value().asItem();

			if (item != Items.AIR) {
				list.add(renderItem(req, 0, item.getDefaultInstance(), true));
			} else {
				list.add(renderBlock(req, holder.value().defaultBlockState(), true));
			}
		}

		return renderAnimated(req, "block_tag", buf, list).response();
	}

	public static HTTPResponse fluidTag(KJSHTTPRequest req) throws Exception {
		var tag = BuiltInRegistries.FLUID.getTag(FluidTags.create(req.id()));

		if (tag.isEmpty()) {
			return HTTPStatus.NOT_FOUND;
		}

		var buf = new FriendlyByteBuf(Unpooled.buffer());
		var list = new ArrayList<CachedImage>();

		for (var holder : tag.get()) {
			buf.writeUtf(holder.value().kjs$getId());
			list.add(renderFluid(req, new FluidStack(holder, FluidType.BUCKET_VOLUME), true));
		}

		return renderAnimated(req, "fluid_tag", buf, list).response();
	}

	private static class ContentGrabber extends HTTPPayload {
		private byte[] body = null;

		public ContentGrabber(String serverName, Instant serverTime) {
			super(serverName, serverTime);
		}

		@Override
		public void setBody(ResponseContent body) {
			try {
				var out = new ByteArrayOutputStream();
				body.write(out);
				this.body = out.toByteArray();
			} catch (Exception ex) {

			}
		}
	}*/
}
