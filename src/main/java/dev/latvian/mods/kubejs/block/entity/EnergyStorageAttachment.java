package dev.latvian.mods.kubejs.block.entity;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.DirectionWrapper;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.TransferPreconditions;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.energy.EnergyHandlerUtil;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EnergyStorageAttachment implements BlockEntityAttachment {
	public static final BlockEntityAttachmentType TYPE = new BlockEntityAttachmentType(KubeJS.id("energy_storage"), Factory.class);

	public record Factory(int capacity, Optional<Integer> maxReceive, Optional<Integer> maxExtract, Optional<Integer> autoOutput) implements BlockEntityAttachmentFactory {
		@Override
		public BlockEntityAttachment create(BlockEntityAttachmentInfo info, KubeBlockEntity entity) {
			int rx = Math.max(0, maxReceive.orElse(0));
			int tx = Math.max(0, maxExtract.orElse(0));
			int auto = Math.max(0, autoOutput.orElse(0));
			return new EnergyStorageAttachment(entity, capacity, rx, tx, auto, auto > 0 ? info.directions().isEmpty() ? DirectionWrapper.VALUES : info.directions().toArray(new Direction[0]) : DirectionWrapper.NONE);
		}

		@Override
		public boolean isTicking() {
			return autoOutput.isPresent() && autoOutput.get() > 0;
		}

		@Override
		public List<BlockCapability<?, ?>> getCapabilities() {
			return List.of(Capabilities.Energy.BLOCK);
		}
	}

	public static class Wrapped extends SnapshotJournal<Integer> implements EnergyHandler {
		private final EnergyStorageAttachment attachment;
		private final int capacity;
		private final int maxReceive;
		private final int maxExtract;
		private int energy;

		public Wrapped(EnergyStorageAttachment attachment, int capacity, int maxReceive, int maxExtract) {
			this.attachment = attachment;
			this.capacity = Math.max(0, capacity);
			this.maxReceive = Math.max(0, maxReceive);
			this.maxExtract = Math.max(0, maxExtract);
			this.energy = 0;
		}

		public int getEnergyStored() {
			return energy;
		}

		public void setEnergyStored(int energy) {
			this.energy = Mth.clamp(energy, 0, capacity);
		}

		public int addEnergy(int add, boolean simulate) {
			int i = Mth.clamp(this.capacity - this.energy, 0, add);
			if (!simulate && i > 0) {
				this.energy += i;
			}
			return i;
		}

		public int removeEnergy(int remove, boolean simulate) {
			int i = Math.min(this.energy, remove);
			if (!simulate && i > 0) {
				this.energy -= i;
			}
			return i;
		}

		public boolean useEnergy(int use, boolean simulate) {
			if (this.energy >= use) {
				if (!simulate) {
					this.energy -= use;
				}
				return true;
			}
			return false;
		}

		@Override
		public long getAmountAsLong() {
			return energy;
		}

		@Override
		public long getCapacityAsLong() {
			return capacity;
		}

		@Override
		public int insert(int amount, TransactionContext transaction) {
			TransferPreconditions.checkNonNegative(amount);
			if (amount == 0 || maxReceive == 0 || capacity == 0) {
				return 0;
			}

			updateSnapshots(transaction);

			int toInsert = Math.min(amount, maxReceive);
			int inserted = Math.min(toInsert, capacity - energy);
			if (inserted > 0) {
				energy += inserted;
			}
			return inserted;
		}

		@Override
		public int extract(int amount, TransactionContext transaction) {
			TransferPreconditions.checkNonNegative(amount);
			if (amount == 0 || maxExtract == 0) {
				return 0;
			}

			updateSnapshots(transaction);

			int toExtract = Math.min(amount, maxExtract);
			int extracted = Math.min(toExtract, energy);
			if (extracted > 0) {
				energy -= extracted;
			}
			return extracted;
		}

		@Override
		protected Integer createSnapshot() {
			return energy;
		}

		@Override
		protected void revertToSnapshot(Integer snapshot) {
			energy = snapshot == null ? 0 : snapshot;
		}

		@Override
		protected void onRootCommit(Integer originalState) {
			if (originalState == null || originalState != energy) {
				if (attachment.entity.getLevel() != null && !attachment.entity.getLevel().isClientSide()) {
					attachment.entity.save();
				}
			}
		}
	}

	private final KubeBlockEntity entity;
	public final Wrapped energyStorage;
	public final int autoOutput;
	public final Direction[] autoOutputDirections;

	public EnergyStorageAttachment(KubeBlockEntity entity, int capacity, int maxReceive, int maxExtract, int autoOutput, Direction[] autoOutputDirections) {
		this.entity = entity;
		this.energyStorage = new Wrapped(this, capacity, maxReceive, maxExtract);
		this.autoOutput = autoOutput;
		this.autoOutputDirections = autoOutputDirections;
	}

	@Override
	public Object getWrappedObject() {
		return energyStorage;
	}

	@Override
	@Nullable
	public <CAP, SRC> CAP getCapability(BlockCapability<CAP, SRC> capability) {
		if (capability == Capabilities.Energy.BLOCK) {
			return (CAP) energyStorage;
		}
		return null;
	}

	@Override
	@Nullable
	public Tag serialize(HolderLookup.Provider registries) {
		int e = energyStorage.getEnergyStored();
		return e <= 0 ? null : IntTag.valueOf(e);
	}

	@Override
	public void deserialize(HolderLookup.Provider registries, @Nullable Tag tag) {
		if (tag instanceof IntTag i) {
			energyStorage.setEnergyStored(i.asInt().orElse(0));
		} else {
			energyStorage.setEnergyStored(0);
		}
	}

	@Override
	public void serverTick() {
		if (autoOutputDirections.length > 0 && autoOutput > 0) {
			var list = new ArrayList<EnergyHandler>(1);

			for (var dir : autoOutputDirections) {
				var c = entity.getLevel().getCapability(Capabilities.Energy.BLOCK, entity.getBlockPos().relative(dir), dir.getOpposite());
				if (c != null && c != energyStorage) {
					list.add(c);
				}
			}

			if (!list.isEmpty()) {
				int stored = energyStorage.getEnergyStored();
				int draw = Math.min(autoOutput, stored) / list.size();

				if (draw > 0) {
					for (var c : list) {
						int moved = EnergyHandlerUtil.move(energyStorage, c, draw, null);
						if (moved <= 0) {
							break;
						}
					}
				}
			}
		}
	}
}
