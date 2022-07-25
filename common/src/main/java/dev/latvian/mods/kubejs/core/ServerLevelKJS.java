package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.player.EntityArrayList;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.storage.ServerLevelData;
import org.jetbrains.annotations.Nullable;

@RemapPrefixForJS("kjs$")
public interface ServerLevelKJS extends LevelKJS, WithPersistentData {
	@Override
	default ServerLevel kjs$self() {
		return (ServerLevel) this;
	}

	@Override
	default ScriptType kjs$getSide() {
		return ScriptType.SERVER;
	}

	@Override
	default EntityArrayList kjs$getEntities() {
		return new EntityArrayList(kjs$self(), kjs$self().getAllEntities());
	}

	default void kjs$spawnLightning(double x, double y, double z, boolean effectOnly, @Nullable ServerPlayer player) {
		if (kjs$self() instanceof ServerLevel) {
			var e = EntityType.LIGHTNING_BOLT.create(kjs$self());
			e.moveTo(x, y, z);
			e.setCause(player);
			e.setVisualOnly(effectOnly);
			kjs$self().addFreshEntity(e);
		}
	}

	default void kjs$spawnLightning(double x, double y, double z, boolean effectOnly) {
		kjs$spawnLightning(x, y, z, effectOnly, null);
	}

	default void kjs$setTime(long time) {
		((ServerLevelData) kjs$self().getLevelData()).setGameTime(time);
	}
}
