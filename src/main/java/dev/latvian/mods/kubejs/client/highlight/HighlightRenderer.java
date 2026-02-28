package dev.latvian.mods.kubejs.client.highlight;

import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.framegraph.FrameGraphBuilder;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
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
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.longs.Long2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2IntMap;
import it.unimi.dsi.fastutil.objects.Reference2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.PostChainConfig;
import net.minecraft.client.renderer.ShaderManager;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.network.PacketDistributor;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class HighlightRenderer {
	public enum Mode {
		NONE,
		SCREEN,
		WORLD
	}

	public static RenderPipeline HIGHLIGHT_PIPELINE_BLOCK;
	public static RenderPipeline HIGHLIGHT_PIPELINE_ENTITY;

	private static RenderType highlightBlock() {
		return RenderType.create(
			"kubejs_highlight_block",
			RenderSetup.builder(HIGHLIGHT_PIPELINE_BLOCK)
				.withTexture("Sampler0", TextureAtlas.LOCATION_BLOCKS)
				.useLightmap()
				.useOverlay()
				.createRenderSetup()
		);
	}

	private static RenderType highlightEntity() {
		return RenderType.create(
			"kubejs_highlight_entity",
			RenderSetup.builder(HIGHLIGHT_PIPELINE_ENTITY)
				.withTexture("Sampler0", TextureAtlas.LOCATION_BLOCKS)
				.useLightmap()
				.useOverlay()
				.createRenderSetup()
		);
	}

	public record ShaderChain(
		PostChain postChain,
		RenderTarget renderInput,
		RenderTarget mcDepthInput,
		RenderTarget renderOutput,
		Identifier renderInputId,
		Identifier mcDepthInputId,
		Identifier renderOutputId,
		MutableBoolean renderAnything
	) {
		/*@Nullable
		public static ShaderChain load(
			TextureManager textureManager,
			PostChainConfig config,
			Set<Identifier> allowedExternalTargets,
			CachedOrthoProjectionMatrixBuffer projectionMatrixBuffer,
			Identifier chainId,
			RenderTarget renderInput,
			RenderTarget mcDepthInput,
			RenderTarget renderOutput,
			Identifier renderInputId,
			Identifier mcDepthInputId,
			Identifier renderOutputId
		) {
			try {
				var postChain = PostChain.load(config, textureManager, allowedExternalTargets, chainId, projectionMatrixBuffer);
				return new ShaderChain(postChain, renderInput, mcDepthInput, renderOutput, renderInputId, mcDepthInputId, renderOutputId, new MutableBoolean(false));
			} catch (ShaderManager.CompilationException ex) {
				KubeJS.LOGGER.warn("Failed to compile shader chain: {}", chainId, ex);
			} catch (JsonSyntaxException ex) {
				KubeJS.LOGGER.warn("Failed to parse shader chain: {}", chainId, ex);
			}
			return null;
		}*/

		public void close() {
			postChain.close();
		}

		public void clearInput() {
			RenderSystem.assertOnRenderThread();
			renderAnything.setFalse();
			renderInput.resize(renderInput.width, renderInput.height);
		}

		public void clearDepth(Minecraft mc, boolean copy) {
			RenderSystem.assertOnRenderThread();
			mcDepthInput.resize(mcDepthInput.width, mcDepthInput.height);

			if (copy) {
				mcDepthInput.copyDepthFrom(mc.getMainRenderTarget());
			}
		}

		public void draw(Minecraft mc) {
			if (renderAnything.isFalse()) {
				return;
			}

			int w = mc.getWindow().getWidth();
			int h = mc.getWindow().getHeight();

			FrameGraphBuilder frame = new FrameGraphBuilder();

			var mainHandle = frame.importExternal("kubejs_main", mc.getMainRenderTarget());
			var inputHandle = frame.importExternal("kubejs_input", renderInput);
			var depthHandle = frame.importExternal("kubejs_mcdepth", mcDepthInput);
			var outputHandle = frame.importExternal("kubejs_output", renderOutput);

			var map = new java.util.HashMap<Identifier, com.mojang.blaze3d.resource.ResourceHandle<RenderTarget>>();
			map.put(PostChain.MAIN_TARGET_ID, mainHandle);
			map.put(renderInputId, inputHandle);
			map.put(mcDepthInputId, depthHandle);
			map.put(renderOutputId, outputHandle);

			var bundle = new MultiTargetBundle(map);

			postChain.addToFrame(frame, w, h, bundle);
			frame.execute(com.mojang.blaze3d.resource.GraphicsResourceAllocator.UNPOOLED);

			renderOutput.blitToScreen();
		}
	}

	private static final class MultiTargetBundle implements PostChain.TargetBundle {
		private final java.util.Map<Identifier, com.mojang.blaze3d.resource.ResourceHandle<RenderTarget>> targets;

		private MultiTargetBundle(java.util.Map<Identifier, com.mojang.blaze3d.resource.ResourceHandle<RenderTarget>> targets) {
			this.targets = targets;
		}

		@Override
		public void replace(Identifier id, com.mojang.blaze3d.resource.ResourceHandle<RenderTarget> handle) {
			targets.put(id, handle);
		}

		@Override
		public @Nullable com.mojang.blaze3d.resource.ResourceHandle<RenderTarget> get(Identifier id) {
			return targets.get(id);
		}
	}

	private record WrappedMultiBufferSource(
		MultiBufferSource delegate,
		int red,
		int green,
		int blue,
		RenderType highlightType
	) implements MultiBufferSource {
		private WrappedMultiBufferSource(MultiBufferSource parent, int color, RenderType highlightType) {
			this(parent, (color >> 16) & 0xFF, (color >> 8) & 0xFF, color & 0xFF, highlightType);
		}

		@Override
		public VertexConsumer getBuffer(RenderType renderType) {
			return new WrappedVertexConsumer(delegate.getBuffer(highlightType), red, green, blue);
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
		public VertexConsumer setColor(int i) {
			return delegate.setColor(i);
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

		@Override
		public VertexConsumer setLineWidth(float v) {
			return delegate.setLineWidth(v);
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
	public ShaderManager highlightShader;

	public final Set<Slot> hoveredSlots = new HashSet<>();
	public final Reference2IntMap<Entity> highlightedEntities = new Reference2IntLinkedOpenHashMap<>(0);
	public final Long2IntMap highlightedBlocks = new Long2IntLinkedOpenHashMap(0);
	public final IntOpenHashSet uniqueColors = new IntOpenHashSet(0);
	public boolean cancelBlockHighlight;

	public void loadPostChains(Minecraft mc) {
		if (worldChain != null) {
			worldChain.close();
			worldChain = null;
		}

		if (guiChain != null) {
			guiChain.close();
			guiChain = null;
		}

		var chainId = Identifier.withDefaultNamespace("shaders/post/kubejs/highlight.json");

		PostChainConfig config;
		try (var reader = mc.getResourceManager().openAsReader(chainId)) {
			var json = com.google.gson.JsonParser.parseReader(reader);
			var result = PostChainConfig.CODEC.parse(com.mojang.serialization.JsonOps.INSTANCE, json);
			config = result.getOrThrow();
		} catch (Exception ex) {
			KubeJS.LOGGER.warn("Failed to read shader chain config: {}", chainId, ex);
			return;
		}

		var referenced = config.passes().stream().flatMap(p -> p.referencedTargets()).collect(java.util.stream.Collectors.toSet());

		Identifier renderInputId = null;
		Identifier mcDepthInputId = null;
		Identifier renderOutputId = null;

		for (var id : referenced) {
			var path = id.getPath();
			if (renderInputId == null && (path.contains("input") || path.contains("render_input"))) {
				renderInputId = id;
			} else if (mcDepthInputId == null && (path.contains("depth") || path.contains("mcdepth"))) {
				mcDepthInputId = id;
			} else if (renderOutputId == null && (path.contains("output") || path.contains("render_output"))) {
				renderOutputId = id;
			}
		}

		if (renderInputId == null || mcDepthInputId == null || renderOutputId == null) {
			KubeJS.LOGGER.warn("Shader chain {} is missing expected external target ids (input/depth/output). Referenced targets: {}", chainId, referenced);
			return;
		}

		KubeJS.LOGGER.warn(
			"HighlightRenderer.loadPostChains: need a concrete 26.1 RenderTarget implementation/factory to create renderInput/mcDepthInput/renderOutput (and a CachedOrthoProjectionMatrixBuffer source). Parsed ok; ids: input={}, depth={}, output={}",
			renderInputId, mcDepthInputId, renderOutputId
		);
	}

	public void tickPre(Minecraft mc) {
		boolean prevKeyDown = actualKey;
		actualKey = mc.level != null && mc.player != null && keyMapping != null && !mc.isPaused() && Commands.LEVEL_GAMEMASTERS.check(mc.player.permissions()) && mc.kjs$isKeyMappingDown(keyMapping);

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
		cancelBlockHighlight = false;

		if (mc.level != null && mc.player != null) {
			var event = new HighlightKubeEvent(mc, this);
			ClientEvents.HIGHLIGHT.post(event);

			if (mode == Mode.WORLD) {
				event.addTarget(color);
			}

			if (mc.hitResult instanceof BlockHitResult hit && hit.getType() == HitResult.Type.BLOCK && highlightedBlocks.containsKey(hit.getBlockPos().asLong())) {
				cancelBlockHighlight = true;
			}
		}
	}

	private void playSound(Minecraft mc) {
		var sound = DevProperties.get().kubedexSound;

		if (!sound.isEmpty()) {
			mc.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvent.createVariableRangeEvent(Identifier.parse(sound)), 1F));
		}
	}

	private int getFlags() {
		int flags = 0;
		flags |= Minecraft.getInstance().hasShiftDown() ? 1 : 0;
		flags |= Minecraft.getInstance().hasControlDown() ? 2 : 0;
		flags |= Minecraft.getInstance().hasAltDown() ? 4 : 0;
		return flags;
	}

	private void requestBlock(BlockPos pos) {
		ClientPacketDistributor.sendToServer(new RequestBlockKubedexPayload(pos, getFlags()));
	}

	private void requestEntity(Entity entity) {
		ClientPacketDistributor.sendToServer(new RequestEntityKubedexPayload(entity.getId(), getFlags()));
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

		ClientPacketDistributor.sendToServer(new RequestInventoryKubedexPayload(slotIds, stacks, getFlags()));
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
			worldChain.clearInput();
		}

		if (guiChain != null) {
			guiChain.clearInput();
		}
	}

	public void renderAfterLevel(Minecraft mc, RenderLevelStageEvent event) {
		updateDepth(mc);

		if (worldChain != null) {
			worldChain.draw(mc);
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
			worldChain.renderInput.resize(width, height);
			worldChain.mcDepthInput.resize(width, height);
			worldChain.renderOutput.resize(width, height);
		}

		if (guiChain != null) {
			guiChain.renderInput.resize(width, height);
			guiChain.mcDepthInput.resize(width, height);
			guiChain.renderOutput.resize(width, height);
		}

		loadPostChains(Minecraft.getInstance());
	}

	public void renderAfterEntities(Minecraft mc, RenderLevelStageEvent event) {
		if (mc.level == null || worldChain == null || highlightedBlocks.isEmpty() && highlightedEntities.isEmpty()) {
			return;
		}

		mc.renderBuffers().bufferSource().endBatch();
		beginOutputOverride(worldChain.renderInput, worldChain.mcDepthInput);

		try {
			worldChain.renderAnything.setTrue();

			var bufferSource = new WrappedMultiBufferSource(
				mc.renderBuffers().bufferSource(),
				color.kjs$getRGB(),
				highlightBlock()
			);

			mc.renderBuffers().bufferSource().endBatch();
		} finally {
			endOutputOverride();
		}
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
		if (guiChain == null) {
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
		//graphics.flush();

		beginOutputOverride(guiChain.renderInput, null);

		try {
			var bufferSource = new WrappedMultiBufferSource(
				mc.renderBuffers().bufferSource(),
				color.kjs$getRGB(),
				highlightEntity()
			);

			//graphics.flush();
		} finally {
			endOutputOverride();
		}

		guiChain.draw(mc);
	}

	private static void beginOutputOverride(@Nullable RenderTarget colorTarget, @Nullable RenderTarget depthTarget) {
		RenderSystem.assertOnRenderThread();
		RenderSystem.outputColorTextureOverride = colorTarget == null ? null : colorTarget.getColorTextureView();
		RenderSystem.outputDepthTextureOverride = depthTarget == null ? null : (depthTarget.useDepth ? depthTarget.getDepthTextureView() : null);
	}

	private static void endOutputOverride() {
		RenderSystem.assertOnRenderThread();
		RenderSystem.outputColorTextureOverride = null;
		RenderSystem.outputDepthTextureOverride = null;
	}

	public void hudPostDraw(Minecraft mc, GuiGraphics graphics, float delta) {
	}


}
