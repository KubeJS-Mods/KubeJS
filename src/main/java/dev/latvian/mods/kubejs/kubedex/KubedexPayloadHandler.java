package dev.latvian.mods.kubejs.kubedex;

import dev.latvian.mods.kubejs.component.DataComponentWrapper;
import dev.latvian.mods.kubejs.net.WebServerUpdateNBTPayload;
import dev.latvian.mods.kubejs.util.Cast;
import dev.latvian.mods.kubejs.util.OrderedCompoundTag;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class KubedexPayloadHandler {
	public record SlotItem(ItemStack item, int slot) {
	}

	private static ListTag sortedTagList(Stream<? extends TagKey<?>> stream) {
		return stream
			.map(TagKey::location)
			.sorted(ResourceLocation::compareNamespaced)
			.map(ResourceLocation::toString)
			.map(StringTag::valueOf)
			.collect(ListTag::new, ListTag::add, ListTag::addAll);
	}

	private static CompoundTag flags(int flags) {
		var tag = new OrderedCompoundTag();
		tag.putBoolean("shift", (flags & 1) != 0);
		tag.putBoolean("ctrl", (flags & 2) != 0);
		tag.putBoolean("alt", (flags & 4) != 0);
		return tag;
	}

	public static void block(ServerPlayer player, BlockPos pos, int flags) {
		var registries = player.server.registryAccess();
		var blockState = player.level().getBlockState(pos);

		if (!blockState.isAir()) {
			var payload = new OrderedCompoundTag();
			payload.put("flags", flags(flags));

			var payloadBlock = new OrderedCompoundTag();

			payloadBlock.putString("id", blockState.getBlock().kjs$getId());
			payloadBlock.putString("dimension", player.level().dimension().location().toString());

			var jpos = new OrderedCompoundTag();
			payloadBlock.put("pos", jpos);
			jpos.putInt("x", pos.getX());
			jpos.putInt("y", pos.getY());
			jpos.putInt("z", pos.getZ());

			var p = new CompoundTag();
			payloadBlock.put("properties", p);

			for (var pk : blockState.getBlock().getStateDefinition().getProperties()) {
				p.putString(pk.getName(), pk.getName(Cast.to(blockState.getValue(pk))));
			}

			var blockEntity = player.level().getBlockEntity(pos);

			if (blockEntity != null) {
				var ejson = new CompoundTag();
				payloadBlock.put("block_entity", ejson);
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

			payload.put("block", payloadBlock);

			PacketDistributor.sendToPlayer(player, new WebServerUpdateNBTPayload("highlight/block", "highlight", Optional.of(payload)));
		}
	}

	public static void entity(ServerPlayer player, int entityId, int flags) {
		var entity = player.level().getEntity(entityId);

		if (entity != null) {
			var payload = new OrderedCompoundTag();
			payload.put("flags", flags(flags));

			var payloadEntity = new OrderedCompoundTag();

			payloadEntity.putString("id", BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).toString());
			payloadEntity.putInt("network_id", entityId);
			payloadEntity.putString("unique_id", entity.getUUID().toString());
			payloadEntity.putString("dimension", player.level().dimension().location().toString());

			var jpos = new OrderedCompoundTag();
			payloadEntity.put("pos", jpos);
			jpos.putDouble("x", entity.position().x);
			jpos.putDouble("y", entity.position().y);
			jpos.putDouble("z", entity.position().z);

			try {
				payloadEntity.put("data", entity.saveWithoutId(new CompoundTag()));
			} catch (Exception ex) {
				payloadEntity.put("data", new CompoundTag());
			}

			payload.put("entity", payloadEntity);

			PacketDistributor.sendToPlayer(player, new WebServerUpdateNBTPayload("highlight/entity", "highlight", Optional.of(payload)));
		}
	}

	public static void inventory(ServerPlayer player, List<Integer> slots, List<ItemStack> stacks, int flags) {
		var allStacks = new LinkedHashSet<SlotItem>();

		for (var s : stacks) {
			if (!s.isEmpty()) {
				allStacks.add(new SlotItem(s, -1));
			}
		}

		for (int s : slots) {
			if (s >= 0 && s < player.getInventory().getContainerSize()) {
				var item = player.getInventory().getItem(s);

				if (!item.isEmpty()) {
					allStacks.add(new SlotItem(item, s));
				}
			}
		}

		itemStacks(player, allStacks, flags);
	}

	public static void itemStacks(ServerPlayer player, Collection<SlotItem> stacks, int flags) {
		var ops = player.server.registryAccess().createSerializationContext(NbtOps.INSTANCE);

		var payload = new CompoundTag();
		payload.put("flags", flags(flags));

		var payloadItems = new ListTag();

		for (var slotStack : stacks) {
			var stack = slotStack.item;
			var tag = new OrderedCompoundTag();
			tag.putString("string", stack.kjs$toItemString0(ops));
			tag.put("item", ItemStack.CODEC.encodeStart(ops, stack).result().get());
			tag.put("name", ComponentSerialization.FLAT_CODEC.encodeStart(ops, stack.getHoverName()).getOrThrow());
			tag.putString("icon", stack.kjs$getWebIconURL(ops, 64).toString());
			tag.putInt("slot", slotStack.slot);

			var patch = stack.getComponentsPatch();

			if (!patch.isEmpty()) {
				tag.putString("component_string", DataComponentWrapper.patchToString(new StringBuilder(), ops, patch).toString());
			}

			var itemTagList = sortedTagList(stack.getItemHolder().tags());

			if (!itemTagList.isEmpty()) {
				tag.put("tags", itemTagList);
			}

			if (stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() != Blocks.AIR) {
				var blockTagList = sortedTagList(blockItem.getBlock().builtInRegistryHolder().tags());

				if (!blockTagList.isEmpty()) {
					tag.put("block_tags", blockTagList);
				}
			}

			if (stack.getItem() instanceof BucketItem bucket && bucket.content != Fluids.EMPTY) {
				var fluidTagList = sortedTagList(bucket.content.builtInRegistryHolder().tags());

				if (!fluidTagList.isEmpty()) {
					tag.put("fluid_tags", fluidTagList);
				}
			}

			if (stack.getItem() instanceof SpawnEggItem egg) {
				var entityType = egg.getType(stack);

				if (entityType != null) {
					var entityTagList = sortedTagList(entityType.builtInRegistryHolder().tags());

					if (!entityTagList.isEmpty()) {
						tag.put("entity_tags", entityTagList);
					}
				}
			}

			payloadItems.add(tag);
		}

		payload.put("items", payloadItems);

		PacketDistributor.sendToPlayer(player, new WebServerUpdateNBTPayload("highlight/items", "highlight", Optional.of(payload)));
	}
}
