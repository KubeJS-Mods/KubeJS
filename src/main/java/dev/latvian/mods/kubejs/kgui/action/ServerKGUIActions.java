package dev.latvian.mods.kubejs.kgui.action;

import dev.latvian.mods.kubejs.kgui.KGUIType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

public record ServerKGUIActions(ServerPlayer player) implements KGUIActions {
	@Override
	public void show(KGUIType type, String id, @Nullable CompoundTag data) {
		// PacketDistributor.sendToPlayer(player, new KGUIShowPayload(type, id, data == null ? new CompoundTag() : data));
	}

	@Override
	public void update(String id, @Nullable CompoundTag data) {
		// PacketDistributor.sendToPlayer(player, new KGUIUpdatePayload(id, data == null ? new CompoundTag() : data));
	}

	@Override
	public void hide(String id) {
		// PacketDistributor.sendToPlayer(player, new KGUIHidePayload(id));
	}

	@Override
	public void mouseClicked(String id, int clickType, int button) {
	}
}