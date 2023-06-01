package dev.latvian.mods.kubejs.platform.fabric;

import dev.latvian.mods.kubejs.platform.MiscPlatformHelper;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.MobCategory;

@SuppressWarnings("UnstableApiUsage")
public class MiscFabricHelper implements MiscPlatformHelper {
	private Boolean dataGen;

	@Override
	@SuppressWarnings("deprecation")
	public MobCategory getMobCategory(String name) {
		// safe cast, mojang just specified too general of a type
		return ((StringRepresentable.EnumCodec<MobCategory>) MobCategory.CODEC).byName(name);
	}

	@Override
	public boolean isDataGen() {
		if (dataGen == null) {
			// FabricDataGenHelper.ENABLED
			dataGen = System.getProperty("fabric-api.datagen") != null;
		}

		return dataGen;
	}

	@Override
	public long ingotFluidAmount() {
		return FluidConstants.INGOT;
	}

	@Override
	public long bottleFluidAmount() {
		return FluidConstants.BOTTLE;
	}
}
