package dev.latvian.mods.kubejs.block.entity;

import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.level.LevelBlock;
import dev.latvian.mods.kubejs.plugin.builtin.event.BlockEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class KubeBlockEntity extends BlockEntity {
	public static final BlockEntityTicker<KubeBlockEntity> TICKER = (level, pos, state, entity) -> entity.tick();

	public final BlockEntityInfo info;
	public final ResourceKey<Block> blockKey;
	protected @Nullable LevelBlock block;
	public final int x, y, z;
	public int tick, cycle;
	public CompoundTag data;
	public final Map<String, Object> attachments;
	public final Map<String, EnergyStorageAttachment.Wrapped> energyWrappers;
	public final Map<String, FluidTankAttachment.Wrapped> fluidWrappers;
	public final Map<String, InventoryAttachment.Wrapped> inventoryWrappers;
	public final Map<String, CustomCapabilityAttachment> customCapabilities;
	public @Nullable UUID placerId;
	private @Nullable BlockEntityTickKubeEvent tickEvent;
	private boolean save;
	private boolean sync;

	public KubeBlockEntity(BlockPos blockPos, BlockState blockState, BlockEntityInfo entityInfo) {
		super(entityInfo.entityType, blockPos, blockState);
		this.info = entityInfo;
		this.blockKey = blockState.kjs$getKey();
		this.x = blockPos.getX();
		this.y = blockPos.getY();
		this.z = blockPos.getZ();
		this.data = info.initialData.copy();

		var jsMap = new LinkedHashMap<String, Object>();

		if (!entityInfo.energyConfigs.isEmpty()) {
			this.energyWrappers = new HashMap<>(entityInfo.energyConfigs.size());
			for (var config : entityInfo.energyConfigs) {
				var wrapped = new EnergyStorageAttachment.Wrapped(this, config);
				energyWrappers.put(config.id(), wrapped);
				jsMap.put(config.id(), wrapped);
			}
		} else {
			this.energyWrappers = Map.of();
		}

		if (!entityInfo.fluidConfigs.isEmpty()) {
			this.fluidWrappers = new HashMap<>(entityInfo.fluidConfigs.size());
			for (var config : entityInfo.fluidConfigs) {
				var wrapped = new FluidTankAttachment.Wrapped(this, config);
				fluidWrappers.put(config.id(), wrapped);
				jsMap.put(config.id(), wrapped);
			}
		} else {
			this.fluidWrappers = Map.of();
		}

		if (!entityInfo.inventoryConfigs.isEmpty()) {
			this.inventoryWrappers = new HashMap<>(entityInfo.inventoryConfigs.size());
			for (var config : entityInfo.inventoryConfigs) {
				var wrapped = new InventoryAttachment.Wrapped(this, config);
				inventoryWrappers.put(config.id(), wrapped);
				jsMap.put(config.id(), wrapped);
			}
		} else {
			this.inventoryWrappers = Map.of();
		}

		if (!entityInfo.customCapConfigs.isEmpty()) {
			this.customCapabilities = new HashMap<>(entityInfo.customCapConfigs.size());
			for (var config : entityInfo.customCapConfigs) {
				var entry = new CustomCapabilityAttachment(config.capability(), config.dataFactory().get());
				customCapabilities.put(config.id(), entry);
				jsMap.put(config.id(), entry.data());
			}
		} else {
			this.customCapabilities = Map.of();
		}
		this.attachments = Map.copyOf(jsMap);
	}

	@Override
	public void setLevel(Level level) {
		super.setLevel(level);
		block = null;
	}

	@Override
	protected void saveAdditional(ValueOutput output) {
		super.saveAdditional(output);

		output.store("data", CompoundTag.CODEC, data);

		if (tick > 0) {
			output.putInt("tick", tick);
		} else {
			output.discard("tick");
		}

		if (cycle > 0) {
			output.putInt("cycle", cycle);
		} else {
			output.discard("cycle");
		}

		output.storeNullable("placer", UUIDUtil.CODEC, placerId);

	}

	@Override
	protected void loadAdditional(ValueInput input) {
		super.loadAdditional(input);

		data = input.read("data", CompoundTag.CODEC).orElseGet(CompoundTag::new);
		tick = input.read("tick", Codec.INT).orElse(0);
		cycle = input.read("cycle", Codec.INT).orElse(0);
		placerId = input.read("placer", UUIDUtil.CODEC).orElse(null);

		syncWrappersFromAttachments();
	}

	private void syncWrappersFromAttachments() {
		for (var wrapper : energyWrappers.values()) {
			wrapper.syncFromAttachment();
		}
		for (var wrapper : fluidWrappers.values()) {
			wrapper.syncFromAttachment();
		}
		for (var wrapper : inventoryWrappers.values()) {
			wrapper.syncFromAttachment();
		}
	}

	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
		var tag = new CompoundTag();

		if (info.sync && !data.isEmpty()) {
			tag.put("data", data);
		}

		if (tick > 0) {
			tag.putInt("tick", tick);
		}

		if (cycle > 0) {
			tag.putInt("cycle", cycle);
		}

		return tag;
	}

	@Nullable
	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	public void save() {
		if (level != null) {
			if (info.getTicker(level) != null) {
				save = true;
			} else {
				level.blockEntityChanged(worldPosition);
			}
		}
	}

	public void sync() {
		if (level != null) {
			if (info.getTicker(level) != null) {
				sync = true;
			} else {
				save();
				level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 11);
			}
		}
	}

	public void sendEvent(int eventId, int data) {
		level.blockEvent(worldPosition, getBlockState().getBlock(), eventId, data);
	}

	@Override
	public boolean triggerEvent(int eventId, int data) {
		if (info.eventHandlers != null) {
			var e = info.eventHandlers.get(eventId);

			if (e != null) {
				e.accept(this, data);
				return true;
			}
		}

		return false;
	}

	public LevelBlock getBlock() {
		if (block == null) {
			this.block = level.kjs$getBlock(worldPosition).cache(this).cache(getBlockState());
		}

		return block;
	}

	private void tick() {
		if (level == null) {
			return;
		}

		if (level.isClientSide() ? info.clientTicking : info.serverTicking) {
			if (tick % info.tickFrequency == info.tickOffset) {
				var side = level.kjs$getScriptType();

				try {
					if (tickEvent == null) {
						tickEvent = new BlockEntityTickKubeEvent(this);
					}

					BlockEvents.BLOCK_ENTITY_TICK.post(side, blockKey, tickEvent);
				} catch (Exception ex) {
					side.console.error("Error while ticking KubeJS block entity '" + info.blockBuilder.id + "'", ex);
				}

				cycle++;
			}

			tick++;
		}

		if (!level.isClientSide() && info.attachmentsTicking) {
			for (var wrapper : energyWrappers.values()) {
				wrapper.serverTick();
			}
		}

		if ((sync || save) && level.getGameTime() % 20L == 0L) {
			if (sync) {
				level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 11);
				save = true;
				sync = false;
			}

			if (save) {
				level.blockEntityChanged(worldPosition);
				save = false;
			}
		}
	}

	@Nullable
	public Entity getPlacer() {
		return level == null ? null : level.kjs$getEntityByUUID(placerId);
	}
}
