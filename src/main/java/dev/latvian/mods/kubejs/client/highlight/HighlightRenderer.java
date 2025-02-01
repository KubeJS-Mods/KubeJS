package dev.latvian.mods.kubejs.client.highlight;

import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.latvian.mods.kubejs.DevProperties;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.color.KubeColor;
import dev.latvian.mods.kubejs.color.SimpleColor;
import dev.latvian.mods.kubejs.net.RequestBlockKubedexPayload;
import dev.latvian.mods.kubejs.net.RequestEntityKubedexPayload;
import dev.latvian.mods.kubejs.net.RequestInventoryKubedexPayload;
import dev.latvian.mods.kubejs.plugin.builtin.event.ClientEvents;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.client.RenderTypeHelper;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.network.PacketDistributor;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class HighlightRenderer {
	public enum Mode {
		NONE(false),
		SCREEN(false),
		WORLD(true);

		public final boolean cancelBlockHighlight;

		Mode(boolean cancelBlockHighlight) {
			this.cancelBlockHighlight = cancelBlockHighlight;
		}
	}

	public record ShaderChain(PostChain postChain, RenderTarget renderInput, RenderTarget mcDepthInput, RenderTarget renderOutput, MutableBoolean renderAnything) {
		@Nullable
		public static ShaderChain load(Minecraft mc, ResourceLocation id) {
			try {
				var postChain = new PostChain(mc.getTextureManager(), mc.getResourceManager(), mc.getMainRenderTarget(), id);
				postChain.resize(mc.getWindow().getWidth(), mc.getWindow().getHeight());
				var renderInput = postChain.getTempTarget("input");
				var mcDepthInput = postChain.getTempTarget("mcdepth");
				var renderOutput = postChain.getTempTarget("output");
				return new ShaderChain(postChain, renderInput, mcDepthInput, renderOutput, new MutableBoolean(false));
			} catch (IOException ex) {
				KubeJS.LOGGER.warn("Failed to load shader: {}", id, ex);
			} catch (JsonSyntaxException ex) {
				KubeJS.LOGGER.warn("Failed to parse shader: {}", id, ex);
			}

			return null;
		}

		public void close() {
			postChain.close();
		}

		public void clearInput(Minecraft mc) {
			renderInput.clear(Minecraft.ON_OSX);
			mc.getMainRenderTarget().bindWrite(false);
			renderAnything.setFalse();
		}

		public void clearDepth(Minecraft mc, boolean copy) {
			// mcDepthInput.bindWrite(false);
			mcDepthInput.clear(Minecraft.ON_OSX);

			if (copy) {
				mcDepthInput.copyDepthFrom(mc.getMainRenderTarget());
			}

			mc.getMainRenderTarget().bindWrite(false);
		}

		public void draw(Minecraft mc, float delta) {
			if (renderAnything.isFalse()) {
				return;
			}

			RenderSystem.setShaderColor(1F, 1F, 1F, 1F);

			postChain.setUniform("OutlineSize", (float) mc.getWindow().getGuiScale());
			postChain.process(delta);
			mc.getMainRenderTarget().bindWrite(false);

			RenderSystem.enableBlend();
			RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
			renderOutput.blitToScreen(mc.getWindow().getWidth(), mc.getWindow().getHeight(), false);
			RenderSystem.disableBlend();
			RenderSystem.defaultBlendFunc();

			mc.getMainRenderTarget().bindWrite(false);
		}
	}

	private static final class WrappedRenderType extends RenderType {
		public final RenderType delegate;

		public WrappedRenderType(RenderType delegate) {
			super("kubejs:wrapped", delegate.format(), delegate.mode(), delegate.bufferSize(), delegate.affectsCrumbling(), delegate.sortOnUpload(), () -> {
				delegate.setupRenderState();
				RenderSystem.setShader(() -> INSTANCE.highlightShader);
			}, delegate::clearRenderState);

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

	public static HighlightRenderer INSTANCE = new HighlightRenderer();
	public static KeyMapping keyMapping;

	public KubeColor color = new SimpleColor(0x99FFB3);
	public Mode mode = Mode.NONE;
	public boolean actualKey;

	@Nullable
	public ShaderChain worldChain;

	@Nullable
	public ShaderChain guiChain;

	@Nullable
	public ShaderInstance highlightShader;

	public final Set<Slot> hoveredSlots = new HashSet<>();
	public final Reference2IntOpenHashMap<Entity> highlightedEntities = new Reference2IntOpenHashMap<>(0);
	public final Long2IntOpenHashMap highlightedBlocks = new Long2IntOpenHashMap(0);
	public final IntOpenHashSet uniqueColors = new IntOpenHashSet(0);
	public boolean cancelBlockHighlight;

	public void loadPostChains(Minecraft mc) {
		if (worldChain != null) {
			worldChain.close();
		}

		if (guiChain != null) {
			guiChain.close();
		}

		var id = ResourceLocation.withDefaultNamespace("shaders/post/kubejs/highlight.json");
		worldChain = ShaderChain.load(mc, id);
		guiChain = ShaderChain.load(mc, id);
	}

	public void tickPre(Minecraft mc) {
		boolean prevKeyDown = actualKey;
		actualKey = mc.level != null && mc.player != null && keyMapping != null && !mc.isPaused() && mc.player.hasPermissions(2) && mc.kjs$isKeyMappingDown(keyMapping);

		while (actualKey && mode != Mode.NONE && mc.options.keyInventory.consumeClick()) {
			keyToggled(mc, Mode.NONE, false);
		}

		if (prevKeyDown != actualKey) {
			if (!actualKey) {
				keyToggled(mc, Mode.NONE, true);
			} else if (mc.screen != null) {
				keyToggled(mc, Mode.SCREEN, true);
			} else {
				keyToggled(mc, Mode.WORLD, true);
			}
		}

		highlightedEntities.clear();
		highlightedBlocks.clear();
		uniqueColors.clear();
		cancelBlockHighlight = mode.cancelBlockHighlight;

		if (mc.level != null && mc.player != null) {
			var event = new HighlightKubeEvent(mc, this);
			ClientEvents.HIGHLIGHT.post(event);

			if (mode == Mode.WORLD) {
				event.addTarget(color);
			}
		}
	}

	private void playSound(Minecraft mc) {
		var sound = DevProperties.get().kubedexSound;

		if (!sound.isEmpty()) {
			mc.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvent.createVariableRangeEvent(ResourceLocation.parse(sound)), 1F));
		}
	}

	private int getFlags() {
		int flags = 0;
		flags |= Screen.hasShiftDown() ? 1 : 0;
		flags |= Screen.hasControlDown() ? 2 : 0;
		flags |= Screen.hasAltDown() ? 4 : 0;
		return flags;
	}

	private void requestBlock(BlockPos pos) {
		PacketDistributor.sendToServer(new RequestBlockKubedexPayload(pos, getFlags()));
	}

	private void requestEntity(Entity entity) {
		PacketDistributor.sendToServer(new RequestEntityKubedexPayload(entity.getId(), getFlags()));
	}

	private void requestInventory(Set<Slot> slots) {
		var slotIds = new ArrayList<Integer>();
		var stacks = new ArrayList<ItemStack>();

		for (var slot : slots) {
			if (slot.container instanceof Inventory) {
				slotIds.add(slot.getSlotIndex());
			} else {
				var stack = slot.getItem();

				if (!stack.isEmpty()) {
					stacks.add(stack);
				}
			}
		}

		PacketDistributor.sendToServer(new RequestInventoryKubedexPayload(slotIds, stacks, getFlags()));
	}

	private void keyToggled(Minecraft mc, Mode newMode, boolean success) {
		if (newMode == Mode.NONE) {
			if (mode == Mode.SCREEN) {
				if (success && !hoveredSlots.isEmpty()) {
					playSound(mc);
					requestInventory(hoveredSlots);
				}

				hoveredSlots.clear();
			} else if (success) {
				if (mc.hitResult instanceof EntityHitResult hit) {
					playSound(mc);
					requestEntity(hit.getEntity());
				} else if (mc.hitResult instanceof BlockHitResult hit && hit.getType() == HitResult.Type.BLOCK) {
					playSound(mc);
					requestBlock(hit.getBlockPos());
				}
			}
		}

		mode = newMode;
	}

	public void clearBuffers(Minecraft mc) {
		if (worldChain != null) {
			worldChain.clearInput(mc);
		}

		if (guiChain != null) {
			guiChain.clearInput(mc);
		}
	}

	public void renderAfterLevel(Minecraft mc, RenderLevelStageEvent event) {
		updateDepth(mc);
		// renderAfterEntities(mc, event);

		if (worldChain != null) {
			worldChain.draw(mc, event.getPartialTick().getGameTimeDeltaPartialTick(false));
		}
	}

	public void updateDepth(Minecraft mc) {
		if (worldChain != null) {
			worldChain.clearDepth(mc, true);
		}

		if (guiChain != null) {
			guiChain.clearDepth(mc, false);
		}
	}

	public void resizePostChains(int width, int height) {
		if (worldChain != null) {
			worldChain.postChain.resize(width, height);
		}

		if (guiChain != null) {
			guiChain.postChain.resize(width, height);
		}
	}

	public void renderAfterEntities(Minecraft mc, RenderLevelStageEvent event) {
		if (mc.level == null || worldChain == null || highlightShader == null || highlightedBlocks.isEmpty() && highlightedEntities.isEmpty()) {
			return;
		}

		mc.renderBuffers().bufferSource().endBatch();
		worldChain.renderInput.bindWrite(false);

		var ms = event.getPoseStack();
		var cam = event.getCamera().getPosition();
		var delta = event.getPartialTick().getGameTimeDeltaPartialTick(false);

		ms.pushPose();
		ms.translate(-cam.x, -cam.y, -cam.z);

		var sources = new Int2ObjectOpenHashMap<WrappedMultiBufferSource>();

		for (int color : uniqueColors) {
			sources.put(color, new WrappedMultiBufferSource(mc.renderBuffers().bufferSource(), color));
		}

		for (var entry : highlightedBlocks.long2IntEntrySet()) {
			var pos = BlockPos.of(entry.getLongKey());
			var state = mc.level.getBlockState(pos);

			if (state.isAir()) {
				continue;
			}

			worldChain.renderAnything.setTrue();

			double x = pos.getX();
			double y = pos.getY();
			double z = pos.getZ();

			ms.pushPose();
			ms.translate(x, y, z);

			var model = mc.getBlockRenderer().getBlockModel(state);
			var seed = state.getSeed(pos);

			var bufferSource = sources.get(entry.getIntValue());

			for (var renderType : model.getRenderTypes(state, RandomSource.create(seed), ModelData.EMPTY)) {
				mc.getBlockRenderer().getModelRenderer().tesselateBlock(mc.level, model, state, pos, ms, bufferSource.getBuffer(RenderTypeHelper.getMovingBlockRenderType(renderType)), false, RandomSource.create(), seed, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, renderType);
			}

			var entity = mc.level.getBlockEntity(pos);

			if (entity != null) {
				mc.getBlockEntityRenderDispatcher().render(entity, delta, ms, bufferSource);
			} else if (state.getRenderShape() == RenderShape.INVISIBLE) {
				var buf = bufferSource.getBuffer(RenderType.debugQuads());
				var m = ms.last().pose();
				box(buf, m, 0F, 0F, 0F, 1F, 1F, 1F);
			}

			ms.popPose();
		}

		for (var entry : highlightedEntities.reference2IntEntrySet()) {
			var entity = entry.getKey();
			worldChain.renderAnything.setTrue();

			var p = entity.getPosition(delta);
			float yaw = Mth.lerp(delta, entity.yRotO, entity.getYRot());

			var renderer = mc.getEntityRenderDispatcher().getRenderer(entity);
			var bufferSource = sources.get(entry.getIntValue());

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

				ms.pushPose();
				ms.translate(p.x, p.y, p.z);
				var m = ms.last().pose();
				float w = entity.getBbWidth() / 2F;
				box(buf, m, -w, 0F, -w, w, entity.getBbHeight(), w);
				ms.popPose();
			}
		}

		ms.popPose();

		mc.renderBuffers().bufferSource().endBatch();
		mc.getMainRenderTarget().bindWrite(false);
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
		if (guiChain == null || highlightShader == null) {
			return;
		}

		while (actualKey && mode != Mode.NONE && mc.options.keyInventory.consumeClick()) {
			keyToggled(mc, Mode.NONE, false);
		}

		if (mode != Mode.SCREEN) {
			return;
		}

		var menu = screen.getMenu();

		for (var slot : menu.slots) {
			int sx = slot.x + screen.getGuiLeft();
			int sy = slot.y + screen.getGuiTop();

			if (mx >= sx && mx < sx + 16 && my >= sy && my < sy + 16 && slot.hasItem()) {
				hoveredSlots.add(slot);
			}
		}

		if (hoveredSlots.isEmpty()) {
			return;
		}

		guiChain.renderAnything.setTrue();
		graphics.flush();
		guiChain.renderInput.bindWrite(false);

		var bufferSource = new WrappedMultiBufferSource(mc.renderBuffers().bufferSource(), color.kjs$getRGB());

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
		guiChain.draw(mc, delta);
	}

	public void hudPostDraw(Minecraft mc, GuiGraphics graphics, float delta) {
		if (worldChain != null) {
			graphics.flush();
			// worldChain.draw(mc, delta);
		}
	}
}
