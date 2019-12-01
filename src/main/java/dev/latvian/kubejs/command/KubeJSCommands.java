package dev.latvian.kubejs.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LatvianModder
 */
public class KubeJSCommands
{
	public static void register(CommandDispatcher<CommandSource> dispatcher)
	{
		dispatcher.register(Commands.literal("kubejs")
				.then(Commands.literal("item_info")
						.executes(context -> itemInfo(context.getSource().asPlayer()))
				)
		);
	}

	private static int itemInfo(ServerPlayerEntity player)
	{
		player.addTag("");

		ItemStack stack = player.getHeldItem(Hand.MAIN_HAND);
		player.sendMessage(new StringTextComponent("=== ").applyTextStyle(TextFormatting.GREEN).appendSibling(stack.getDisplayName().applyTextStyle(TextFormatting.BOLD)).appendText(" ==="));

		player.sendMessage(new StringTextComponent("= Tags =").applyTextStyle(TextFormatting.YELLOW));

		List<ResourceLocation> tags = new ArrayList<>(stack.getItem().getTags());
		tags.sort(null);

		for (ResourceLocation id : tags)
		{
			ITextComponent component = new StringTextComponent("- " + id);
			component.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, id.toString()));
			player.sendMessage(component);
		}

		return Command.SINGLE_SUCCESS;
	}
}