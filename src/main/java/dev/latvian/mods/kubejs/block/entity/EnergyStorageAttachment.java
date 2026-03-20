package dev.latvian.mods.kubejs.block.entity;

import dev.latvian.mods.kubejs.plugin.builtin.wrapper.DirectionWrapper;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.TransferPreconditions;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.energy.EnergyHandlerUtil;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Set;

public class EnergyStorageAttachment {
	public record Config(String id, EnumSet<Direction> directions, int capacity, int maxReceive, int maxExtract, int autoOutput) {
		public Direction[] resolveAutoOutputDirections() {
			if (autoOutput <= 0) {
				return DirectionWrapper.NONE;
			}
			return directions.isEmpty() ? DirectionWrapper.VALUES : directions.toArray(new Direction[0]);
		}

		public boolean isTicking() {
			return autoOutput > 0;
		}
	}

	public static Config createConfig(String id, Set<Direction> directions, int capacity, int maxReceive, int maxExtract, int autoOutput) {
		return new Config(
			id,
			directions == null || directions.isEmpty() ? DirectionWrapper.EMPTY_SET : EnumSet.copyOf(directions),
			Math.max(0, capacity),
			Math.max(0, maxReceive),
			Math.max(0, maxExtract),
			Math.max(0, autoOutput)
		);
	}

	public static class Wrapped extends SnapshotJournal<Integer> implements EnergyHandler {
		private final KubeBlockEntity entity;
		private final String id;
		private final int capacity;
		private final int maxReceive;
		private final int maxExtract;
		private final int autoOutput;
		private final Direction[] autoOutputDirections;
		private int energy;

		public Wrapped(KubeBlockEntity entity, Config config) {
			this.entity = entity;
			this.id = config.id();
			this.capacity = config.capacity();
			this.maxReceive = config.maxReceive();
			this.maxExtract = config.maxExtract();
			this.autoOutput = config.autoOutput();
			this.autoOutputDirections = config.resolveAutoOutputDirections();
			this.energy = 0;
		}

		public int getEnergyStored() {
			return energy;
		}

		public void setEnergyStored(int energy) {
			this.energy = Mth.clamp(energy, 0, capacity);
			syncToAttachment();
		}

		public int addEnergy(int add, boolean simulate) {
			int i = Mth.clamp(this.capacity - this.energy, 0, add);
			if (!simulate && i > 0) {
				this.energy += i;
				syncToAttachment();
			}
			return i;
		}

		public int removeEnergy(int remove, boolean simulate) {
			int i = Math.min(this.energy, remove);
			if (!simulate && i > 0) {
				this.energy -= i;
				syncToAttachment();
			}
			return i;
		}

		public boolean useEnergy(int use, boolean simulate) {
			if (this.energy >= use) {
				if (!simulate) {
					this.energy -= use;
					syncToAttachment();
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
				syncToAttachment();
				if (entity.getLevel() != null && !entity.getLevel().isClientSide()) {
					entity.save();
				}
			}
		}

		public void syncFromAttachment() {
			var map = entity.getData(KubeJSAttachmentTypes.ENERGY.get());
			energy = map.getOrDefault(id, 0);
		}

		public void syncToAttachment() {
			var map = entity.getData(KubeJSAttachmentTypes.ENERGY.get());
			if (energy > 0) {
				map.put(id, energy);
			} else {
				map.remove(id);
			}
		}

		public void serverTick() {
			if (autoOutputDirections.length > 0 && autoOutput > 0) {
				var list = new ArrayList<EnergyHandler>(1);

				for (var dir : autoOutputDirections) {
					var c = entity.getLevel().getCapability(Capabilities.Energy.BLOCK, entity.getBlockPos().relative(dir), dir.getOpposite());
					if (c != null && c != this) {
						list.add(c);
					}
				}

				if (!list.isEmpty()) {
					int stored = energy;
					int draw = Math.min(autoOutput, stored) / list.size();

					if (draw > 0) {
						for (var c : list) {
							int moved = EnergyHandlerUtil.move(this, c, draw, null);
							if (moved <= 0) {
								break;
							}
						}
					}
				}
			}
		}
	}
}
