package dev.latvian.mods.kubejs.platform.forge;

import dev.latvian.mods.kubejs.platform.MiscPlatformHelper;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.fml.ModLoader;

public class MiscForgeHelper implements MiscPlatformHelper {
	@Override
	public MobCategory getMobCategory(String name) {
		return MobCategory.byName(name);
	}

	@Override
	public boolean isDataGen() {
		return ModLoader.isDataGenRunning();
	}

	@Override
	public long ingotFluidAmount() {
		return 90;
	}

	@Override
	public long bottleFluidAmount() {
		return 250;
	}
}
