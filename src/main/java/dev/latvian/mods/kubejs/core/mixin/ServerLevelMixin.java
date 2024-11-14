package dev.latvian.mods.kubejs.core.mixin;

import dev.latvian.mods.kubejs.core.ServerLevelKJS;
import dev.latvian.mods.rhino.util.HideFromJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.entity.LevelEntityGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

@Mixin(ServerLevel.class)
@RemapPrefixForJS("kjs$")
public abstract class ServerLevelMixin implements ServerLevelKJS {
	@Unique
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
	@Nullable
	@HideFromJS
	public abstract Entity getEntity(UUID uniqueId);

	@Shadow
	@HideFromJS
	public abstract LevelEntityGetter<Entity> getEntities();
}
