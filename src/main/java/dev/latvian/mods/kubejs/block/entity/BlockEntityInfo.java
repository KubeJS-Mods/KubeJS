package dev.latvian.mods.kubejs.block.entity;

import dev.latvian.mods.kubejs.block.BlockBuilder;
import dev.latvian.mods.kubejs.core.ServerPlayerKJS;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.rhino.util.HideFromJS;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.crafting.Ingredient;
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
	public transient BlockEntityJSTicker serverTicker;
	public transient BlockEntityJSTicker clientTicker;
	public transient boolean sync;
	public transient List<BlockEntityAttachmentHolder> attachments;
	public transient Int2ObjectMap<BlockEntityEventCallback> eventHandlers;

	public BlockEntityInfo(BlockBuilder blockBuilder) {
		this.blockBuilder = blockBuilder;
		this.initialData = new CompoundTag();
		this.sync = false;
		this.attachments = new ArrayList<>(1);
		this.eventHandlers = new Int2ObjectArrayMap<>(0);
	}

	public void initialData(CompoundTag data) {
		initialData = data;
	}

	public void serverTick(int frequency, int offset, BlockEntityCallback callback) {
		serverTicker = new BlockEntityJSTicker(this, Math.max(1, frequency), Math.max(0, offset), callback, true);
	}

	public void serverTick(BlockEntityCallback callback) {
		serverTick(1, 0, callback);
	}

	public void clientTick(int frequency, int offset, BlockEntityCallback callback) {
		clientTicker = new BlockEntityJSTicker(this, Math.max(1, frequency), Math.max(0, offset), callback, false);
	}

	public void clientTick(BlockEntityCallback callback) {
		clientTick(1, 0, callback);
	}

	public void tick(int frequency, int offset, BlockEntityCallback callback) {
		serverTick(frequency, offset, callback);
		clientTick(frequency, offset, callback);
	}

	public void tick(BlockEntityCallback callback) {
		serverTick(callback);
		clientTick(callback);
	}

	public void enableSync() {
		sync = true;
	}

	public void attach(String type, Map<String, Object> args) {
		var att = BlockEntityAttachmentType.ALL.get().get(type);

		if (att != null) {
			try {
				attachments.add(new BlockEntityAttachmentHolder(attachments.size(), att.factory().apply(args)));
			} catch (Exception ex) {
				ConsoleJS.STARTUP.error("Error while creating BlockEntity attachment '" + type + "'", ex);
			}
		} else {
			ConsoleJS.STARTUP.error("BlockEntity attachment '" + type + "' not found!");
		}
	}

	public void inventory(int width, int height) {
		attach("inventory", Map.of("width", width, "height", height));
	}

	public void inventory(int width, int height, Ingredient inputFilter) {
		attach("inventory", Map.of("width", width, "height", height, "inputFilter", inputFilter));
	}

	public void eventHandler(int eventId, BlockEntityEventCallback callback) {
		eventHandlers.put(eventId, callback);
	}

	public void rightClickOpensInventory() {
		blockBuilder.rightClick = e -> {
			if (e.getBlock().getEntity() instanceof BlockEntityJS entity && entity.inventory != null) {
				((ServerPlayerKJS) e.getPlayer()).kjs$openInventoryGUI(entity.inventory, blockBuilder.get().getName());
			}
		};
	}

	@HideFromJS
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new BlockEntityJS(pos, state, this);
	}

	@HideFromJS
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level) {
		return (BlockEntityTicker) (level.isClientSide() ? clientTicker : serverTicker);
	}

	@Override
	public String toString() {
		return "BlockEntityInfo[" + blockBuilder.id + "]";
	}
}
