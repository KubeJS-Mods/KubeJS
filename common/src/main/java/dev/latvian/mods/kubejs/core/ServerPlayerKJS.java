package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.level.BlockContainerJS;
import dev.latvian.mods.kubejs.net.NotificationMessage;
import dev.latvian.mods.kubejs.net.PaintMessage;
import dev.latvian.mods.kubejs.net.SendDataFromServerMessage;
import dev.latvian.mods.kubejs.player.PlayerStatsJS;
import dev.latvian.mods.kubejs.util.NotificationBuilder;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetCarriedItemPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.UserBanListEntry;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import org.jetbrains.annotations.Nullable;

import java.util.Date;

@RemapPrefixForJS("kjs$")
public interface ServerPlayerKJS extends PlayerKJS {
	@Override
	default ServerPlayer kjs$self() {
		return (ServerPlayer) this;
	}

	@Override
	default void kjs$sendData(String channel, @Nullable CompoundTag data) {
		if (!channel.isEmpty()) {
			new SendDataFromServerMessage(channel, data).sendTo(kjs$self());
		}
	}

	@Override
	default void kjs$paint(CompoundTag renderer) {
		new PaintMessage(renderer).sendTo(kjs$self());
	}

	@Override
	default PlayerStatsJS kjs$getStats() {
		return new PlayerStatsJS(kjs$self(), kjs$self().getStats());
	}

	@Override
	default boolean kjs$isMiningBlock() {
		return kjs$self().gameMode.isDestroyingBlock;
	}

	@Override
	default void kjs$setPositionAndRotation(double x, double y, double z, float yaw, float pitch) {
		PlayerKJS.super.kjs$setPositionAndRotation(x, y, z, yaw, pitch);
		kjs$self().connection.teleport(x, y, z, yaw, pitch);
	}

	default void kjs$setCreativeMode(boolean mode) {
		kjs$self().setGameMode(mode ? GameType.CREATIVE : GameType.SURVIVAL);
	}

	default boolean kjs$isOp() {
		return kjs$self().server.getPlayerList().isOp(kjs$self().getGameProfile());
	}

	default void kjs$kick(Component reason) {
		kjs$self().connection.disconnect(reason);
	}

	default void kjs$kick() {
		kjs$kick(Component.translatable("multiplayer.disconnect.kicked"));
	}

	default void kjs$ban(String banner, String reason, long expiresInMillis) {
		var date = new Date();
		var userlistbansentry = new UserBanListEntry(kjs$self().getGameProfile(), date, banner, new Date(date.getTime() + (expiresInMillis <= 0L ? 315569260000L : expiresInMillis)), reason);
		kjs$self().server.getPlayerList().getBans().add(userlistbansentry);
		kjs$kick(Component.translatable("multiplayer.disconnect.banned"));
	}

	default boolean kjs$isAdvancementDone(ResourceLocation id) {
		var a = kjs$self().server.kjs$getAdvancement(id);
		return a != null && kjs$self().getAdvancements().getOrStartProgress(a.advancement).isDone();
	}

	default void kjs$unlockAdvancement(ResourceLocation id) {
		var a = kjs$self().server.kjs$getAdvancement(id);

		if (a != null) {
			var advancementprogress = kjs$self().getAdvancements().getOrStartProgress(a.advancement);

			for (var s : advancementprogress.getRemainingCriteria()) {
				kjs$self().getAdvancements().award(a.advancement, s);
			}
		}
	}

	default void kjs$revokeAdvancement(ResourceLocation id) {
		var a = kjs$self().server.kjs$getAdvancement(id);

		if (a != null) {
			var advancementprogress = kjs$self().getAdvancements().getOrStartProgress(a.advancement);

			if (advancementprogress.hasProgress()) {
				for (var s : advancementprogress.getCompletedCriteria()) {
					kjs$self().getAdvancements().revoke(a.advancement, s);
				}
			}
		}
	}

	@Override
	default void kjs$setSelectedSlot(int index) {
		var p = kjs$getSelectedSlot();
		PlayerKJS.super.kjs$setSelectedSlot(index);
		var n = kjs$getSelectedSlot();

		if (p != n && kjs$self().connection != null) {
			kjs$self().connection.send(new ClientboundSetCarriedItemPacket(n));
		}
	}

	@Override
	default void kjs$setMouseItem(ItemStack item) {
		PlayerKJS.super.kjs$setMouseItem(item);

		if (kjs$self().connection != null) {
			kjs$self().inventoryMenu.broadcastChanges();
		}
	}

	@Nullable
	default BlockContainerJS kjs$getSpawnLocation() {
		var pos = kjs$self().getRespawnPosition();
		return pos == null ? null : new BlockContainerJS(kjs$self().level, pos);
	}

	default void kjs$setSpawnLocation(BlockContainerJS c) {
		kjs$self().setRespawnPosition(c.minecraftLevel.dimension(), c.getPos(), 0F, true, false);
	}

	@Override
	default void kjs$notify(NotificationBuilder builder) {
		new NotificationMessage(builder).sendTo(kjs$self());
	}
}
