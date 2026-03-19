package dev.latvian.mods.kubejs.command;

import dev.latvian.mods.kubejs.holder.NamespaceHolderSet;
import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.fluid.FluidUtil;

import java.util.List;

public class InformationCommands {
	private static Component copy(String s, ChatFormatting col, String info) {
		return copy(Component.literal(s).withStyle(col), Component.literal(info));
	}

	private static Component copy(String s, ChatFormatting col, Component info) {
		return copy(Component.literal(s).withStyle(col), info);
	}

	private static Component copy(Component c, Component info) {
		return Component.literal("- ")
			.withStyle(ChatFormatting.GRAY)
			.withStyle(Style.EMPTY.withClickEvent(new ClickEvent.CopyToClipboard(c.getString())))
			.withStyle(Style.EMPTY.withHoverEvent(new HoverEvent.ShowText(info.copy().append(" (Click to copy)"))))
			.append(c);
	}

	public static int hand(ServerPlayer player, InteractionHand hand) {
		player.sendSystemMessage(Component.literal("Item in hand:"));
		var stack = player.getItemInHand(hand);
		var holder = stack.typeHolder();
		var itemRegistry = player.registryAccess().lookupOrThrow(Registries.ITEM);
		var blockRegistry = player.registryAccess().lookupOrThrow(Registries.BLOCK);
		var fluidRegistry = player.registryAccess().lookupOrThrow(Registries.FLUID);
		var tabRegistry = player.registryAccess().lookupOrThrow(Registries.CREATIVE_MODE_TAB);

		// item info
		// id
		player.sendSystemMessage(copy(stack.kjs$toItemString0(player.server.registryAccess().createSerializationContext(NbtOps.INSTANCE)), ChatFormatting.GREEN, "Item ID"));
		// item tags
		var itemTags = holder.tags().toList();
		for (var tag : itemTags) {
			var id = "'#%s'".formatted(tag.location());
			var size = itemRegistry.get(tag).map(HolderSet::size).orElse(0);
			player.sendSystemMessage(copy(id, ChatFormatting.YELLOW, "Item Tag [" + size + " items]"));
		}
		// mod
		player.sendSystemMessage(copy("'@" + stack.kjs$getMod() + "'", ChatFormatting.AQUA, "Mod [" + NamespaceHolderSet.of(itemRegistry, stack.kjs$getMod()).size() + " items]"));

		// creative tab
		for (var tab : tabRegistry) {
			if (tab.contains(stack)) {
				var id = tabRegistry.getKey(tab);
				var count = tab.getDisplayItems().size();
				var searchCount = tab.getSearchTabDisplayItems().size();

				player.sendSystemMessage(copy("'%" + id + "'", ChatFormatting.LIGHT_PURPLE, tab.getDisplayName().copy().append(" [%d/%d items in tab / search tab]".formatted(count, searchCount))));
			}
		}

		// block info
		if (stack.getItem() instanceof BlockItem blockItem) {
			player.sendSystemMessage(Component.literal("Held block:"));
			var block = blockItem.getBlock();
			var blockHolder = block.defaultBlockState().typeHolder();
			// id
			player.sendSystemMessage(copy("'" + block.kjs$getId() + "'", ChatFormatting.GREEN, "Block ID"));
			// block tags
			var blockTags = blockHolder.tags().toList();
			for (var tag : blockTags) {
				var id = "'#%s'".formatted(tag.location());
				var size = blockRegistry.get(tag).map(HolderSet::size).orElse(0);
				player.sendSystemMessage(copy(id, ChatFormatting.YELLOW, "Block Tag [" + size + " items]"));
			}
		}
		// fluid info
		var containedFluid = FluidUtil.getFirstStackContained(stack);
		if (!containedFluid.isEmpty()) {
			player.sendSystemMessage(Component.literal("Held fluid:"));
			var fluid = containedFluid.typeHolder();
			// id
			player.sendSystemMessage(copy(fluid.getRegisteredName(), ChatFormatting.GREEN, "Fluid ID"));
			// fluid tags
			var fluidTags = containedFluid.tags().toList();
			for (var tag : fluidTags) {
				var id = "'#%s'".formatted(tag.location());
				var size = fluidRegistry.get(tag).map(HolderSet::size).orElse(0);
				player.sendSystemMessage(copy(id, ChatFormatting.YELLOW, "Fluid Tag [" + size + " items]"));
			}
		}

		return 1;
	}

	public static int inventory(ServerPlayer player) {
		return dump(player.getInventory().getNonEquipmentItems(), player, "Inventory");
	}

	public static int hotbar(ServerPlayer player) {
		return dump(player.getInventory().getNonEquipmentItems().subList(0, 9), player, "Hotbar");
	}

	public static int dump(List<ItemStack> stacks, ServerPlayer player, String name) {
		var ops = player.server.registryAccess().createSerializationContext(NbtOps.INSTANCE);
		var dump = stacks.stream().filter(is -> !is.isEmpty()).map(is -> is.kjs$toItemString0(ops)).toList();
		player.sendSystemMessage(copy(dump.toString(), ChatFormatting.WHITE, name + " Item List"));
		return 1;
	}
}
