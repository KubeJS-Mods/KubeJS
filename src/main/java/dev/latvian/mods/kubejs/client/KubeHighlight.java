package dev.latvian.mods.kubejs.client;

import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.latvian.mods.kubejs.KubeJS;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.client.RenderTypeHelper;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class KubeHighlight {
	private static final class WrappedRenderType extends RenderType {
		public final RenderType delegate;

		public WrappedRenderType(RenderType delegate) {
			super("kubejs:wrapped", delegate.format(), delegate.mode(), delegate.bufferSize(), delegate.affectsCrumbling(), delegate.sortOnUpload(), () -> {
				delegate.setupRenderState();
				RenderSystem.setShader(() -> INSTANCE.highlightShader);
			}, () -> {
				delegate.clearRenderState();
			});

			this.delegate = delegate;
		}

		@Override
		public String toString() {
			return "kubejs:wrapped[" + delegate + "]";
		}
	}

	private record WrappedMultiBufferSource(MultiBufferSource delegate, int red, int green, int blue) implements MultiBufferSource {
		private WrappedMultiBufferSource(MultiBufferSource parent, int color) {
			this(parent, (color >> 16) & 0xFF, (color >> 8) & 0xFF, color & 0xFF);
		}

		@Override
		public VertexConsumer getBuffer(RenderType renderType) {
			return new WrappedVertexConsumer(delegate.getBuffer(new WrappedRenderType(renderType)), red, green, blue);
		}
	}

	private record WrappedVertexConsumer(VertexConsumer delegate, int red, int green, int blue) implements VertexConsumer {
		@Override
		public VertexConsumer addVertex(float f, float g, float h) {
			this.delegate.addVertex(f, g, h);
			return this;
		}

		@Override
		public VertexConsumer setColor(int i, int j, int k, int l) {
			this.delegate.setColor(red, green, blue, 255);
			return this;
		}

		@Override
		public VertexConsumer setUv(float f, float g) {
			this.delegate.setUv(f, g);
			return this;
		}

		@Override
		public VertexConsumer setUv1(int i, int j) {
			this.delegate.setUv1(i, j);
			return this;
		}

		@Override
		public VertexConsumer setUv2(int i, int j) {
			this.delegate.setUv2(i, j);
			return this;
		}

		@Override
		public VertexConsumer setNormal(float f, float g, float h) {
			this.delegate.setNormal(f, g, h);
			return this;
		}
	}

	public static KubeHighlight INSTANCE = new KubeHighlight();
	public static KeyMapping keyMapping;

	public int color = 0x99FFB3;
	public boolean key;
	public boolean actualKey;

	@Nullable
	public PostChain postChain;

	@Nullable
	public RenderTarget renderInput;

	@Nullable
	public RenderTarget renderOutput;

	@Nullable
	public ShaderInstance highlightShader;

	public boolean renderAnything;

	public final Set<Slot> hoveredSlots = new HashSet<>();

	public void loadPostChains(Minecraft mc) {
		if (postChain != null) {
			postChain.close();
			postChain = null;
			renderInput = null;
			renderOutput = null;
		}

		var id = ResourceLocation.withDefaultNamespace("shaders/post/kubejs/highlight.json");

		try {
			postChain = new PostChain(mc.getTextureManager(), mc.getResourceManager(), mc.getMainRenderTarget(), id);
			postChain.resize(mc.getWindow().getWidth(), mc.getWindow().getHeight());
			renderInput = postChain.getTempTarget("input");
			renderOutput = postChain.getTempTarget("output");
		} catch (IOException ex) {
			KubeJS.LOGGER.warn("Failed to load shader: {}", id, ex);
		} catch (JsonSyntaxException ex) {
			KubeJS.LOGGER.warn("Failed to parse shader: {}", id, ex);
		}
	}

	public void tickPre(Minecraft mc) {
		boolean prevKeyDown = actualKey;
		actualKey = mc.level != null && mc.player != null && keyMapping != null && !mc.isPaused() && mc.kjs$isKeyMappingDown(keyMapping);

		while (actualKey && key && mc.options.keyInventory.consumeClick()) {
			keyToggled(false);
		}

		if (prevKeyDown != actualKey) {
			keyToggled(actualKey);
		}
	}

	private void keyToggled(boolean on) {
		key = on;

		if (!key) {
			hoveredSlots.clear();
		}
	}

	public void clearBuffers(Minecraft mc) {
		if (renderInput != null) {
			renderInput.clear(Minecraft.ON_OSX);
			mc.getMainRenderTarget().bindWrite(false);
		}

		renderAnything = false;
	}

	public void renderAfterEntities(Minecraft mc, RenderLevelStageEvent event) {
		if (!key || mc.hitResult == null || mc.hitResult.getType() == HitResult.Type.MISS || renderInput == null || highlightShader == null || mc.screen != null) {
			return;
		}

		var ms = event.getPoseStack();
		var cam = event.getCamera().getPosition();
		var delta = event.getPartialTick().getGameTimeDeltaPartialTick(false);

		ms.pushPose();
		ms.translate(-cam.x, -cam.y, -cam.z);

		if (mc.hitResult instanceof BlockHitResult hit) {
			mc.renderBuffers().bufferSource().endBatch();
			renderInput.bindWrite(false);
			renderAnything = true;

			double x = hit.getBlockPos().getX();
			double y = hit.getBlockPos().getY();
			double z = hit.getBlockPos().getZ();

			ms.translate(x, y, z);

			var state = mc.level.getBlockState(hit.getBlockPos());
			var model = mc.getBlockRenderer().getBlockModel(state);

			var seed = state.getSeed(hit.getBlockPos());

			var bufferSource = new WrappedMultiBufferSource(mc.renderBuffers().bufferSource(), color);

			for (var renderType : model.getRenderTypes(state, RandomSource.create(seed), ModelData.EMPTY)) {
				mc.getBlockRenderer().getModelRenderer().tesselateBlock(mc.level, model, state, hit.getBlockPos(), ms, bufferSource.getBuffer(RenderTypeHelper.getMovingBlockRenderType(renderType)), false, RandomSource.create(), seed, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, renderType);
			}

			var entity = mc.level.getBlockEntity(hit.getBlockPos());

			if (entity != null) {
				mc.getBlockEntityRenderDispatcher().render(entity, delta, ms, bufferSource);
			} else if (state.getRenderShape() == RenderShape.INVISIBLE) {
				var buf = bufferSource.getBuffer(RenderType.debugQuads());
				var m = ms.last().pose();
				box(buf, m, 0F, 0F, 0F, 1F, 1F, 1F);
			}

			mc.renderBuffers().bufferSource().endBatch();
			mc.getMainRenderTarget().bindWrite(false);
		} else if (mc.hitResult instanceof EntityHitResult hit) {
			var entity = hit.getEntity();
			mc.renderBuffers().bufferSource().endBatch();
			renderInput.bindWrite(false);
			renderAnything = true;

			var p = entity.getPosition(delta);
			float yaw = Mth.lerp(delta, hit.getEntity().yRotO, entity.getYRot());

			var renderer = mc.getEntityRenderDispatcher().getRenderer(entity);
			var bufferSource = new WrappedMultiBufferSource(mc.renderBuffers().bufferSource(), color);

			if (renderer != null) {
				var off = renderer.getRenderOffset(entity, delta);
				double x1 = p.x + off.x();
				double y1 = p.y + off.y();
				double z1 = p.z + off.z();
				ms.pushPose();
				ms.translate(x1, y1, z1);
				renderer.render(entity, yaw, delta, ms, bufferSource, LightTexture.FULL_BRIGHT);
				ms.popPose();
			} else {
				var buf = bufferSource.getBuffer(RenderType.debugQuads());

				ms.translate(p.x, p.y, p.z);
				var m = ms.last().pose();
				float w = entity.getBbWidth() / 2F;
				box(buf, m, -w, 0F, -w, w, entity.getBbHeight(), w);
			}

			mc.renderBuffers().bufferSource().endBatch();
			mc.getMainRenderTarget().bindWrite(false);
		}

		ms.popPose();
	}

	private void box(VertexConsumer buf, Matrix4f m, float x0, float y0, float z0, float x1, float y1, float z1) {
		buf.addVertex(m, x0, y0, z0).setColor(255, 255, 255, 255);
		buf.addVertex(m, x1, y0, z0).setColor(255, 255, 255, 255);
		buf.addVertex(m, x1, y0, z1).setColor(255, 255, 255, 255);
		buf.addVertex(m, x0, y0, z1).setColor(255, 255, 255, 255);
		buf.addVertex(m, x0, y1, z0).setColor(255, 255, 255, 255);
		buf.addVertex(m, x0, y1, z1).setColor(255, 255, 255, 255);
		buf.addVertex(m, x1, y1, z1).setColor(255, 255, 255, 255);
		buf.addVertex(m, x1, y1, z0).setColor(255, 255, 255, 255);
		buf.addVertex(m, x0, y0, z0).setColor(255, 255, 255, 255);
		buf.addVertex(m, x0, y1, z0).setColor(255, 255, 255, 255);
		buf.addVertex(m, x1, y1, z0).setColor(255, 255, 255, 255);
		buf.addVertex(m, x1, y0, z0).setColor(255, 255, 255, 255);
		buf.addVertex(m, x0, y0, z1).setColor(255, 255, 255, 255);
		buf.addVertex(m, x1, y0, z1).setColor(255, 255, 255, 255);
		buf.addVertex(m, x1, y1, z1).setColor(255, 255, 255, 255);
		buf.addVertex(m, x0, y1, z1).setColor(255, 255, 255, 255);
		buf.addVertex(m, x0, y0, z0).setColor(255, 255, 255, 255);
		buf.addVertex(m, x0, y0, z1).setColor(255, 255, 255, 255);
		buf.addVertex(m, x0, y1, z1).setColor(255, 255, 255, 255);
		buf.addVertex(m, x0, y1, z0).setColor(255, 255, 255, 255);
		buf.addVertex(m, x1, y0, z0).setColor(255, 255, 255, 255);
		buf.addVertex(m, x1, y1, z0).setColor(255, 255, 255, 255);
		buf.addVertex(m, x1, y1, z1).setColor(255, 255, 255, 255);
		buf.addVertex(m, x1, y0, z1).setColor(255, 255, 255, 255);
	}

	public void screen(Minecraft mc, GuiGraphics graphics, AbstractContainerScreen<?> screen, int mx, int my, float delta) {
		if (renderInput == null || highlightShader == null) {
			return;
		}

		while (actualKey && key && mc.options.keyInventory.consumeClick()) {
			keyToggled(false);
		}

		if (key) {
			for (var slot : screen.getMenu().slots) {
				int sx = slot.x + screen.getGuiLeft();
				int sy = slot.y + screen.getGuiTop();

				if (mx >= sx && mx < sx + 16 && my >= sy && my < sy + 16 && slot.hasItem()) {
					hoveredSlots.add(slot);
				}
			}

			if (!hoveredSlots.isEmpty()) {
				renderAnything = true;
				graphics.flush();
				renderInput.bindWrite(false);

				var bufferSource = new WrappedMultiBufferSource(mc.renderBuffers().bufferSource(), color);

				for (var slot : hoveredSlots) {
					int x = slot.x + screen.getGuiLeft();
					int y = slot.y + screen.getGuiTop();
					var stack = slot.getItem();

					var model = mc.getItemRenderer().getModel(stack, mc.level, mc.player, 0);

					graphics.pose().pushPose();
					graphics.pose().translate(x + 8F, y + 8F, 0F);
					graphics.pose().scale(16F, -16F, 16F);

					try {
						var renderStack = stack.copy();
						renderStack.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, false);
						renderStack.setDamageValue(0);
						renderStack.setCount(1);
						mc.getItemRenderer().render(renderStack, ItemDisplayContext.GUI, false, graphics.pose(), bufferSource, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, model);
					} catch (Throwable throwable) {
						CrashReport crashreport = CrashReport.forThrowable(throwable, "Rendering item");
						CrashReportCategory crashreportcategory = crashreport.addCategory("Item being rendered");
						crashreportcategory.setDetail("Item Type", () -> String.valueOf(stack.getItem()));
						crashreportcategory.setDetail("Item Components", () -> String.valueOf(stack.getComponents()));
						crashreportcategory.setDetail("Item Foil", () -> String.valueOf(stack.hasFoil()));
						throw new ReportedException(crashreport);
					}

					graphics.pose().popPose();
				}

				graphics.flush();
				mc.getMainRenderTarget().bindWrite(false);
			}
		}
	}

	public void afterEverything(Minecraft mc, float delta) {
		if (renderOutput == null || postChain == null || !renderAnything) {
			return;
		}

		postChain.process(delta);
		mc.getMainRenderTarget().bindWrite(false);

		RenderSystem.enableBlend();
		RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
		renderOutput.blitToScreen(mc.getWindow().getWidth(), mc.getWindow().getHeight(), false);
		RenderSystem.disableBlend();
		RenderSystem.defaultBlendFunc();

		mc.getMainRenderTarget().bindWrite(false);
	}

	public void resizePostChains(int width, int height) {
		if (postChain != null) {
			postChain.resize(width, height);
		}
	}
}
