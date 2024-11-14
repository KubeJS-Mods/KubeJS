package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.gui.KubeJSGUI;
import dev.latvian.mods.kubejs.gui.KubeJSMenu;
import dev.latvian.mods.kubejs.gui.chest.ChestMenuData;
import dev.latvian.mods.kubejs.gui.chest.CustomChestMenu;
import dev.latvian.mods.kubejs.level.LevelBlock;
import dev.latvian.mods.kubejs.net.NotificationPayload;
import dev.latvian.mods.kubejs.net.SendDataFromServerPayload;
import dev.latvian.mods.kubejs.net.SetActivePostShaderPayload;
import dev.latvian.mods.kubejs.player.PlayerStatsJS;
import dev.latvian.mods.kubejs.util.NotificationToastData;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetCarriedItemPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.UserBanListEntry;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.HashMap;
import java.util.Optional;
import java.util.function.Consumer;

@RemapPrefixForJS("kjs$")
public interface ServerPlayerKJS extends PlayerKJS {
	@Override
	default ServerPlayer kjs$self() {
		return (ServerPlayer) this;
	}

	@Override
	default void kjs$sendData(String channel, @Nullable CompoundTag data) {
		if (!channel.isEmpty()) {
			PacketDistributor.sendToPlayer(kjs$self(), new SendDataFromServerPayload(channel, data));
		}
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
		return a != null && kjs$self().getAdvancements().getOrStartProgress(a.holder()).isDone();
	}

	default void kjs$unlockAdvancement(ResourceLocation id) {
		var a = kjs$self().server.kjs$getAdvancement(id);

		if (a != null) {
			var advancementprogress = kjs$self().getAdvancements().getOrStartProgress(a.holder());

			for (var s : advancementprogress.getRemainingCriteria()) {
				kjs$self().getAdvancements().award(a.holder(), s);
			}
		}
	}

	default void kjs$revokeAdvancement(ResourceLocation id) {
		var a = kjs$self().server.kjs$getAdvancement(id);

		if (a != null) {
			var advancementprogress = kjs$self().getAdvancements().getOrStartProgress(a.holder());

			if (advancementprogress.hasProgress()) {
				for (var s : advancementprogress.getCompletedCriteria()) {
					kjs$self().getAdvancements().revoke(a.holder(), s);
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
	default LevelBlock kjs$getSpawnLocation() {
		var pos = kjs$self().getRespawnPosition();
		return pos == null ? null : kjs$getLevel().kjs$getBlock(pos);
	}

	default void kjs$setSpawnLocation(LevelBlock c) {
		kjs$self().setRespawnPosition(c.getDimensionKey(), c.getPos(), 0F, true, false);
	}

	@Override
	default void kjs$notify(NotificationToastData builder) {
		PacketDistributor.sendToPlayer(kjs$self(), new NotificationPayload(builder));
	}

	default void kjs$openChestGUI(Consumer<KubeJSGUI> gui) {
		var data = new KubeJSGUI();
		gui.accept(data);

		kjs$self().openMenu(new MenuProvider() {
			@Override
			public Component getDisplayName() {
				return data.title;
			}

			@Override
			public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
				return new KubeJSMenu(i, inventory, data);
			}
		}, data::write);
	}

	default void kjs$openInventoryGUI(InventoryKJS inventory, Component title) {
		kjs$openChestGUI(gui -> {
			gui.title = title;
			gui.setInventory(inventory);
		});
	}

	default Container kjs$captureInventory(boolean autoRestore) {
		var playerItems = kjs$self().getInventory().items;

		var captured = new SimpleContainer(playerItems.size());
		var map = new HashMap<Integer, ItemStack>();

		for (int i = 0; i < playerItems.size(); i++) {
			var c = playerItems.set(i, ItemStack.EMPTY);

			if (!c.isEmpty()) {
				if (autoRestore) {
					map.put(i, c);
				}

				captured.setItem(i, c.copy());
			}
		}

		if (autoRestore && !map.isEmpty()) {
			kjs$self().getServer().kjs$restoreInventories().put(kjs$self().getUUID(), map);
		}

		return captured;
	}

	default void kjs$openChestGUI(Component title, int rows, Consumer<ChestMenuData> gui) {
		var data = new ChestMenuData(kjs$self(), title, Mth.clamp(rows, 1, 6));
		gui.accept(data);

		if (kjs$self().containerMenu instanceof CustomChestMenu open) {
			data.capturedInventory = open.data.capturedInventory;
		} else {
			data.capturedInventory = kjs$captureInventory(true);
		}

		if (kjs$self().containerMenu instanceof CustomChestMenu open && open.data.rows == data.rows && open.data.title.equals(title)) {
			open.data = data;
			data.sync();
		} else {
			data.sync();

			kjs$self().openMenu(new MenuProvider() {
				@Override
				public Component getDisplayName() {
					return title;
				}

				@Override
				public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
					return new CustomChestMenu(i, data);
				}
			});
		}
	}

	default void kjs$heal() {
		kjs$self().heal(kjs$self().getMaxHealth());
		kjs$self().getFoodData().eat(20, 1F);
	}

	@Override
	default void kjs$setActivePostShader(@Nullable ResourceLocation id) {
		PacketDistributor.sendToPlayer(kjs$self(), new SetActivePostShaderPayload(Optional.ofNullable(id)));
	}
}
