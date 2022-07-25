package dev.latvian.mods.kubejs.mixin.common;

import dev.latvian.mods.kubejs.core.ServerLevelKJS;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin implements ServerLevelKJS {
	private CompoundTag kjs$persistentData;

	@Override
	public CompoundTag kjs$getPersistentData() {
		if (kjs$persistentData == null) {
			var t = kjs$self().dimension().location().toString();
			kjs$persistentData = kjs$self().getServer().kjs$getPersistentData().getCompound(t);
			kjs$self().getServer().kjs$getPersistentData().put(t, kjs$persistentData);
		}

		return kjs$persistentData;
	}
}
