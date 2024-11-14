package dev.latvian.mods.kubejs.block.entity;

import dev.latvian.mods.kubejs.bindings.event.BlockEvents;
import dev.latvian.mods.kubejs.level.LevelBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
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
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class KubeBlockEntity extends BlockEntity {
	public static final BlockEntityTicker<KubeBlockEntity> TICKER = (level, pos, state, entity) -> entity.tick();

	public final BlockEntityInfo info;
	public final ResourceKey<Block> blockKey;
	protected LevelBlock block;
	public final int x, y, z;
	public int tick, cycle;
	public CompoundTag data;
	public final Map<String, Object> attachments;
	public final transient BlockEntityAttachmentHolder[] attachmentArray;
	public UUID placerId;
	private BlockEntityTickKubeEvent tickEvent;
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

		if (entityInfo.attachments != null) {
			var map = new HashMap<String, Object>(entityInfo.attachments.size());
			this.attachmentArray = new BlockEntityAttachmentHolder[entityInfo.attachments.size()];

			for (var aInfo : entityInfo.attachments.values()) {
				var f = aInfo.factory().create(aInfo, this);
				map.put(aInfo.id(), f.getWrappedObject());
				this.attachmentArray[aInfo.index()] = new BlockEntityAttachmentHolder(aInfo, f);
			}

			this.attachments = Map.copyOf(map);
		} else {
			this.attachments = Map.of();
			this.attachmentArray = BlockEntityAttachmentHolder.EMPTY_ARRAY;
		}
	}

	@Override
	public void setLevel(Level level) {
		super.setLevel(level);
		block = null;
	}

	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.saveAdditional(tag, registries);
		tag.put("data", data);

		if (tick > 0) {
			tag.putInt("tick", tick);
		}

		if (cycle > 0) {
			tag.putInt("cycle", cycle);
		}

		if (placerId != null) {
			tag.putUUID("placer", placerId);
		}

		if (attachmentArray.length > 0) {
			var data = new CompoundTag();

			for (var entry : attachmentArray) {
				var t = entry.attachment().serialize(registries);

				if (t != null) {
					data.put(entry.info().id(), t);
				}
			}

			tag.put("attachments", data);
		}
	}

	@Override
	public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);
		data = tag.getCompound("data");
		tick = tag.getInt("tick");
		cycle = tag.getInt("cycle");
		placerId = tag.contains("placer") ? tag.getUUID("placer") : null;

		if (attachmentArray.length > 0) {
			var data = tag.getCompound("attachments");

			for (var entry : attachmentArray) {
				entry.attachment().deserialize(registries, data.get(entry.info().id()));
			}
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

		if (level.isClientSide ? info.clientTicking : info.serverTicking) {
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

		if (!level.isClientSide && info.attachmentsTicking) {
			for (var entry : attachmentArray) {
				entry.attachment().serverTick();
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
