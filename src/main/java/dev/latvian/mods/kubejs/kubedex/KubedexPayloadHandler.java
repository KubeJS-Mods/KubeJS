package dev.latvian.mods.kubejs.kubedex;

import dev.latvian.mods.kubejs.KubeJS;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

public class KubedexPayloadHandler {
	public static void block(ServerPlayer player, BlockPos pos) {
		var registries = player.server.registryAccess();
		var blockState = player.level().getBlockState(pos);

		if (!blockState.isAir()) {
			KubeJS.LOGGER.info("[Kubedex][" + player.getScoreboardName() + "] Block State " + blockState + " @ " + pos);
		}

		var blockEntity = player.level().getBlockEntity(pos);

		if (blockEntity != null) {
			KubeJS.LOGGER.info("[Kubedex][" + player.getScoreboardName() + "] Block Entity " + blockEntity.saveWithoutMetadata(registries));
		}
	}

	public static void entity(ServerPlayer player, int entityId) {
		var registries = player.server.registryAccess();
		var entity = player.level().getEntity(entityId);

		if (entity != null) {
			KubeJS.LOGGER.info("[Kubedex][" + player.getScoreboardName() + "] Entity " + entity.getName().getString() + " #" + entityId + " " + entity.serializeNBT(registries));
		}
	}

	public static void inventory(ServerPlayer player, List<Integer> slots, List<ItemStack> stacks) {
		var allStacks = new LinkedHashSet<>(stacks);

		for (int s : slots) {
			if (s >= 0 && s < player.getInventory().getContainerSize()) {
				var item = player.getInventory().getItem(s);

				if (!item.isEmpty()) {
					allStacks.add(item);
				}
			}
		}

		itemStacks(player, allStacks);
	}

	public static void itemStacks(ServerPlayer player, Collection<ItemStack> stacks) {
		var ops = player.server.registryAccess().createSerializationContext(NbtOps.INSTANCE);

		for (var stack : stacks) {
			KubeJS.LOGGER.info("[Kubedex][" + player.getScoreboardName() + "] Item " + stack.kjs$toItemString0(ops));
		}
	}
}
