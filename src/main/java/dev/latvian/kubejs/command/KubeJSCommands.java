package dev.latvian.kubejs.command;

import com.mojang.brigadier.CommandDispatcher;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.item.ingredient.GroupIngredientJS;
import dev.latvian.kubejs.item.ingredient.ModIngredientJS;
import dev.latvian.kubejs.item.ingredient.TagIngredientJS;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
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
				.then(Commands.literal("hand")
						.executes(context -> hand(context.getSource().asPlayer()))
				)
		);

		dispatcher.register(Commands.literal("kjs_hand")
				.executes(context -> hand(context.getSource().asPlayer()))
		);
	}

	private static ITextComponent copy(String s, TextFormatting col, String info)
	{
		ITextComponent component = new StringTextComponent("- ");
		component.getStyle().setColor(TextFormatting.GRAY);
		component.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, s));
		component.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new StringTextComponent(info + " (Click to copy)")));
		component.appendSibling(new StringTextComponent(s).applyTextStyle(col));
		return component;
	}

	private static int hand(ServerPlayerEntity player)
	{
		player.sendMessage(new StringTextComponent("Item in hand:"));
		ItemStackJS stack = ItemStackJS.of(player.getHeldItemMainhand());
		player.sendMessage(copy(stack.toString(), TextFormatting.GREEN, "Item ID"));

		List<ResourceLocation> tags = new ArrayList<>(stack.getItem().getTags());
		tags.sort(null);

		for (ResourceLocation id : tags)
		{
			player.sendMessage(copy("#" + id.toString(), TextFormatting.YELLOW, "Item Tag [" + new TagIngredientJS(id).getStacks().size() + " items]"));
		}

		player.sendMessage(copy("@" + stack.getMod(), TextFormatting.AQUA, "Mod [" + new ModIngredientJS(stack.getMod()).getStacks().size() + " items]"));

		if (stack.getItem().getGroup() != null)
		{
			player.sendMessage(copy("%" + stack.getItemGroup(), TextFormatting.LIGHT_PURPLE, "Item Group [" + new GroupIngredientJS(stack.getItem().getGroup()).getStacks().size() + " items]"));
		}

		return 1;
	}
}