package dev.latvian.mods.kubejs.platform.fabric;

import dev.latvian.mods.kubejs.platform.MiscPlatformHelper;
import dev.latvian.mods.kubejs.script.PlatformWrapper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.MobCategory;

public class MiscPlatformHelperImpl implements MiscPlatformHelper {
	@Override
	public void setModName(PlatformWrapper.ModInfo info, String name) {
		try {
			var meta = FabricLoader.getInstance().getModContainer(info.getId()).get().getMetadata();
			var field = meta.getClass().getDeclaredField("name");
			field.setAccessible(true);
			field.set(meta, name);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public MobCategory getMobCategory(String name) {
		// safe cast, mojang just specified too general of a type
		return ((StringRepresentable.EnumCodec<MobCategory>) MobCategory.CODEC).byName(name);
	}
}
