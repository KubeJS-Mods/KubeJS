package dev.latvian.kubejs.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import dev.latvian.kubejs.item.ItemStackJS;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

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
				.then(Commands.literal("hand")
						.executes(context -> hand(context.getSource().asPlayer()))
				)
				.then(Commands.literal("output_recipes")
						.executes(context -> outputRecipes(context.getSource().asPlayer()))
				)
				.then(Commands.literal("input_recipes")
						.executes(context -> inputRecipes(context.getSource().asPlayer()))
				)
				.then(Commands.literal("check_recipe_conflicts")
						.executes(context -> checkRecipeConflicts(context.getSource().asPlayer()))
				)
		);
	}

	private static int itemInfo(ServerPlayerEntity player)
	{
		ItemStack stack = player.getHeldItemMainhand();
		ITextComponent c = stack.getDisplayName();
		c.getStyle().setBold(true);
		player.sendMessage(new StringTextComponent("=== ").func_240699_a_(TextFormatting.GREEN).func_230529_a_(c).func_240702_b_(" ==="), Util.DUMMY_UUID);

		player.sendMessage(new StringTextComponent("= Item Tags =").func_240699_a_(TextFormatting.YELLOW), Util.DUMMY_UUID);

		List<ResourceLocation> tags = new ArrayList<>(stack.getItem().getTags());
		tags.sort(null);

		for (ResourceLocation id : tags)
		{
			ITextComponent component = new StringTextComponent("- " + id);
			component.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, id.toString()));
			component.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new StringTextComponent("Click to copy")));
			player.sendMessage(component, Util.DUMMY_UUID);
		}

		return Command.SINGLE_SUCCESS;
	}

	private static int hand(ServerPlayerEntity player)
	{
		ItemStackJS is = ItemStackJS.of(player.getHeldItemMainhand());
		ITextComponent component = new StringTextComponent(is.toString() + " [Click to copy]");
		component.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, is.toString()));
		player.sendMessage(component, Util.DUMMY_UUID);
		return 1;
	}

	private static int outputRecipes(ServerPlayerEntity player)
	{
		player.sendMessage(new StringTextComponent("WIP!"), Util.DUMMY_UUID);
		return Command.SINGLE_SUCCESS;
	}

	private static int inputRecipes(ServerPlayerEntity player)
	{
		player.sendMessage(new StringTextComponent("WIP!"), Util.DUMMY_UUID);
		return Command.SINGLE_SUCCESS;
	}

	private static int checkRecipeConflicts(ServerPlayerEntity player)
	{
		player.sendMessage(new StringTextComponent("WIP!"), Util.DUMMY_UUID);
		return Command.SINGLE_SUCCESS;
	}
}