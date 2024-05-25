package dev.latvian.mods.kubejs.core.mixin;

import dev.latvian.mods.kubejs.core.ServerLevelKJS;
import dev.latvian.mods.rhino.util.HideFromJS;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.entity.LevelEntityGetter;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin implements ServerLevelKJS {
	@Shadow
	@Final
	@HideFromJS
	List<ServerPlayer> players;

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

	@Shadow
	@HideFromJS
	public abstract List<ServerPlayer> players();

	@Shadow
	@HideFromJS
	protected abstract LevelEntityGetter<Entity> getEntities();
}
