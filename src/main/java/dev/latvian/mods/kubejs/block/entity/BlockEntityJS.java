package dev.latvian.mods.kubejs.block.entity;

import dev.latvian.mods.kubejs.core.InventoryKJS;
import dev.latvian.mods.kubejs.level.BlockContainerJS;
import dev.latvian.mods.rhino.mod.util.NbtType;
import dev.latvian.mods.rhino.util.HideFromJS;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class BlockEntityJS extends BlockEntity {
	public final BlockEntityInfo info;
	protected BlockContainerJS block;
	public final int x, y, z;
	public int tick, cycle;
	public CompoundTag data;
	public final BlockEntityAttachment[] attachments;
	public InventoryKJS inventory;
	public UUID placerId;

	public BlockEntityJS(BlockPos blockPos, BlockState blockState, BlockEntityInfo entityInfo) {
		super(entityInfo.entityType, blockPos, blockState);
		this.info = entityInfo;
		this.x = blockPos.getX();
		this.y = blockPos.getY();
		this.z = blockPos.getZ();
		this.tick = 0;
		this.cycle = 0;
		this.data = info.initialData.copy();

		if (entityInfo.attachments != null) {
			this.attachments = new BlockEntityAttachment[entityInfo.attachments.size()];

			for (int i = 0; i < this.attachments.length; i++) {
				this.attachments[i] = entityInfo.attachments.get(i).factory().create(this);

				if (this.inventory == null && this.attachments[i] instanceof InventoryKJS inv) {
					this.inventory = inv;
				}
			}
		} else {
			this.attachments = BlockEntityAttachment.EMPTY_ARRAY;
		}
	}

	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
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

		if (attachments.length > 0) {
			var list = new ListTag();

			for (var att : attachments) {
				list.add(att.writeAttachment());
			}

			tag.put("attachments", list);
		}
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		data = tag.getCompound("data");
		tick = tag.getInt("tick");
		cycle = tag.getInt("cycle");
		placerId = tag.contains("placer") ? tag.getUUID("placer") : null;

		if (attachments.length > 0) {
			var list = tag.getList("attachments", NbtType.COMPOUND);

			if (attachments.length == list.size()) {
				for (int i = 0; i < attachments.length; i++) {
					attachments[i].readAttachment(list.getCompound(i));
				}
			} else {
				for (var att : attachments) {
					att.readAttachment(new CompoundTag());
				}
			}
		}
	}

	@Override
	public CompoundTag getUpdateTag() {
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
			level.blockEntityChanged(worldPosition);
		}
	}

	public void sync() {
		if (level != null) {
			save();
			level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 11);
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

	@HideFromJS
	public void postTick(boolean c) {
		tick++;

		if (c) {
			cycle++;
		}
	}

	public BlockContainerJS getBlock() {
		if (block == null) {
			this.block = new BlockContainerJS(level, worldPosition);
			this.block.cachedEntity = this;
		}
		return block;
	}
}
