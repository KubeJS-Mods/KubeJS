package dev.latvian.mods.kubejs.block.entity;

import dev.latvian.mods.kubejs.block.BlockBuilder;
import dev.latvian.mods.kubejs.core.ServerPlayerKJS;
import dev.latvian.mods.kubejs.item.ItemPredicate;
import dev.latvian.mods.kubejs.script.ConsoleJS;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.util.HideFromJS;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BlockEntityInfo {
	public transient final BlockBuilder blockBuilder;
	public transient BlockEntityType<?> entityType;
	public transient CompoundTag initialData;
	public transient boolean serverTicking;
	public transient boolean clientTicking;
	public transient int tickFrequency;
	public transient int tickOffset;
	public transient boolean sync;
	public transient List<BlockEntityAttachmentHolder> attachments;
	public transient Int2ObjectMap<BlockEntityEventCallback> eventHandlers;

	public BlockEntityInfo(BlockBuilder blockBuilder) {
		this.blockBuilder = blockBuilder;
		this.initialData = new CompoundTag();
		this.serverTicking = false;
		this.clientTicking = false;
		this.tickFrequency = 1;
		this.tickOffset = 0;
		this.sync = false;
		this.attachments = new ArrayList<>(1);
		this.eventHandlers = new Int2ObjectArrayMap<>(0);
	}

	public void initialData(CompoundTag data) {
		initialData = data;
	}

	public void serverTicking() {
		serverTicking = true;
	}

	public void clientTicking() {
		clientTicking = true;
	}

	public void ticking() {
		serverTicking();
		clientTicking();
	}

	public void tickFrequency(int frequency) {
		tickFrequency = Math.max(1, frequency);
	}

	public void tickOffset(int offset) {
		tickOffset = Math.max(0, offset);
	}

	public void enableSync() {
		sync = true;
	}

	public void attach(Context cx, String type, Map<String, Object> args) {
		var att = BlockEntityAttachmentType.ALL.get().get(type);

		if (att != null) {
			try {
				attachments.add(new BlockEntityAttachmentHolder(attachments.size(), att.factory().createFactory(cx, args)));
			} catch (Exception ex) {
				ConsoleJS.STARTUP.error("Error while creating BlockEntity attachment '" + type + "'", ex);
			}
		} else {
			ConsoleJS.STARTUP.error("BlockEntity attachment '" + type + "' not found!");
		}
	}

	public void inventory(Context cx, int width, int height) {
		attach(cx, "inventory", Map.of("width", width, "height", height));
	}

	public void inventory(Context cx, int width, int height, ItemPredicate inputFilter) {
		attach(cx, "inventory", Map.of("width", width, "height", height, "inputFilter", inputFilter));
	}

	public void eventHandler(int eventId, BlockEntityEventCallback callback) {
		eventHandlers.put(eventId, callback);
	}

	public void rightClickOpensInventory() {
		blockBuilder.rightClick = e -> {
			if (e.getBlock().getEntity() instanceof KubeBlockEntity entity && entity.inventory != null) {
				((ServerPlayerKJS) e.getPlayer()).kjs$openInventoryGUI(entity.inventory, blockBuilder.get().getName());
			}
		};
	}

	@HideFromJS
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new KubeBlockEntity(pos, state, this);
	}

	@HideFromJS
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level) {
		if (level.isClientSide()) {
			return clientTicking ? (BlockEntityTicker) KubeBlockEntity.TICKER : null;
		} else {
			return serverTicking ? (BlockEntityTicker) KubeBlockEntity.TICKER : null;
		}
	}

	@Override
	public String toString() {
		return "BlockEntityInfo[" + blockBuilder.id + "]";
	}
}
