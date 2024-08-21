package dev.latvian.mods.kubejs.kubedex;

import dev.latvian.mods.kubejs.net.WebServerUpdateNBTPayload;
import dev.latvian.mods.kubejs.util.Cast;
import dev.latvian.mods.kubejs.util.OrderedCompoundTag;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;

public class KubedexPayloadHandler {
	public static void block(ServerPlayer player, BlockPos pos) {
		var registries = player.server.registryAccess();
		var blockState = player.level().getBlockState(pos);

		if (!blockState.isAir()) {
			var payload = new OrderedCompoundTag();
			payload.putString("id", blockState.getBlock().kjs$getId());
			payload.putString("dimension", player.level().dimension().location().toString());

			var jpos = new OrderedCompoundTag();
			payload.put("pos", jpos);
			jpos.putInt("x", pos.getX());
			jpos.putInt("y", pos.getY());
			jpos.putInt("z", pos.getZ());

			var p = new CompoundTag();
			payload.put("properties", p);

			for (var pk : blockState.getBlock().getStateDefinition().getProperties()) {
				p.putString(pk.getName(), pk.getName(Cast.to(blockState.getValue(pk))));
			}

			var blockEntity = player.level().getBlockEntity(pos);

			if (blockEntity != null) {
				var ejson = new CompoundTag();
				payload.put("block_entity", ejson);
				ejson.putString("id", BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(blockEntity.getType()).toString());

				try {
					ejson.put("components", DataComponentMap.CODEC.encodeStart(registries.createSerializationContext(NbtOps.INSTANCE), blockEntity.components()).result().get());
				} catch (Exception ex) {
					ejson.put("components", new CompoundTag());
				}

				try {
					ejson.put("data", blockEntity.saveCustomOnly(registries));
				} catch (Exception ex) {
					ejson.put("data", new CompoundTag());
				}
			}

			PacketDistributor.sendToPlayer(player, new WebServerUpdateNBTPayload("highlight/block", Optional.of(payload)));
		}
	}

	public static void entity(ServerPlayer player, int entityId) {
		var entity = player.level().getEntity(entityId);

		if (entity != null) {
			var payload = new OrderedCompoundTag();
			payload.putString("id", BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).toString());
			payload.putInt("network_id", entityId);
			payload.putString("unique_id", entity.getUUID().toString());
			payload.putString("dimension", player.level().dimension().location().toString());

			var jpos = new OrderedCompoundTag();
			payload.put("pos", jpos);
			jpos.putDouble("x", entity.position().x);
			jpos.putDouble("y", entity.position().y);
			jpos.putDouble("z", entity.position().z);

			try {
				payload.put("data", entity.saveWithoutId(new CompoundTag()));
			} catch (Exception ex) {
				payload.put("data", new CompoundTag());
			}

			PacketDistributor.sendToPlayer(player, new WebServerUpdateNBTPayload("highlight/entity", Optional.of(payload)));
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

		var payload = new ListTag();

		for (var stack : stacks) {
			payload.add(ItemStack.CODEC.encodeStart(ops, stack).result().get());
		}

		PacketDistributor.sendToPlayer(player, new WebServerUpdateNBTPayload("highlight/items", Optional.of(payload)));
	}
}
