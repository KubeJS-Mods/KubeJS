package dev.latvian.mods.kubejs.block.entity;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.DirectionWrapper;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.energy.IEnergyStorage;
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
			return List.of(Capabilities.EnergyStorage.BLOCK);
		}
	}

	public static class Wrapped extends EnergyStorage {
		private final EnergyStorageAttachment attachment;

		public Wrapped(EnergyStorageAttachment attachment, int capacity, int maxReceive, int maxExtract) {
			super(capacity, maxReceive, maxExtract);
			this.attachment = attachment;
		}

		public void setEnergyStored(int energy) {
			this.energy = Mth.clamp(energy, 0, capacity);
		}

		public int addEnergy(int add, boolean simulate) {
			int i = Mth.clamp(this.capacity - this.energy, 0, add);

			if (!simulate && i > 0) {
				energy += i;
				attachment.entity.save();
			}

			return i;
		}

		public int removeEnergy(int remove, boolean simulate) {
			int i = Math.max(energy, remove);

			if (!simulate && i > 0) {
				energy -= i;
				attachment.entity.save();
			}

			return i;
		}

		public boolean useEnergy(int use, boolean simulate) {
			if (energy >= use) {
				if (!simulate) {
					energy -= use;
					attachment.entity.save();
				}

				return true;
			}

			return false;
		}

		@Override
		public int extractEnergy(int toExtract, boolean simulate) {
			int s = super.extractEnergy(toExtract, simulate);

			if (s > 0 && !simulate && !attachment.entity.getLevel().isClientSide()) {
				attachment.entity.save();
			}

			return s;
		}

		@Override
		public int receiveEnergy(int toReceive, boolean simulate) {
			int s = super.receiveEnergy(toReceive, simulate);

			if (s > 0 && !simulate && !attachment.entity.getLevel().isClientSide()) {
				attachment.entity.save();
			}

			return s;
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
		if (capability == Capabilities.EnergyStorage.BLOCK) {
			return (CAP) energyStorage;
		}

		return null;
	}

	@Override
	public void serverTick() {
		if (autoOutputDirections.length > 0 && autoOutput > 0) {
			var list = new ArrayList<IEnergyStorage>(1);

			for (var dir : autoOutputDirections) {
				var c = entity.getLevel().getCapability(Capabilities.EnergyStorage.BLOCK, entity.getBlockPos().relative(dir), dir.getOpposite());

				if (c != null && c != energyStorage) {
					list.add(c);
				}
			}

			if (!list.isEmpty()) {
				int draw = Math.min(autoOutput, energyStorage.getEnergyStored()) / list.size();

				if (draw > 0) {
					for (var c : list) {
						int e = energyStorage.extractEnergy(draw, true);

						if (e > 0) {
							energyStorage.extractEnergy(c.receiveEnergy(e, false), false);
						} else {
							break;
						}
					}
				}
			}
		}
	}
}
