package dev.latvian.kubejs.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.item.ingredient.GroupIngredientJS;
import dev.latvian.kubejs.item.ingredient.ModIngredientJS;
import dev.latvian.kubejs.item.ingredient.TagIngredientJS;
import dev.latvian.kubejs.util.Tags;
import me.shedaniel.architectury.registry.Registries;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagCollection;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LatvianModder
 */
public class KubeJSCommands
{
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher)
	{
		dispatcher.register(Commands.literal("kubejs")
				.then(Commands.literal("hand")
						.executes(context -> hand(context.getSource().getPlayerOrException()))
				)
				.then(Commands.literal("output_recipes")
						.executes(context -> outputRecipes(context.getSource().getPlayerOrException()))
				)
				.then(Commands.literal("input_recipes")
						.executes(context -> inputRecipes(context.getSource().getPlayerOrException()))
				)
				.then(Commands.literal("check_recipe_conflicts")
						.executes(context -> checkRecipeConflicts(context.getSource().getPlayerOrException()))
				)
				.then(Commands.literal("list_tag")
						.then(Commands.argument("tag", ResourceLocationArgument.id())
								.executes(context -> tagObjects(context.getSource().getPlayerOrException(), Tags.items(), ResourceLocationArgument.getId(context, "tag")))
								.then(Commands.literal("item")
										.executes(context -> tagObjects(context.getSource().getPlayerOrException(), Tags.items(), ResourceLocationArgument.getId(context, "tag")))
								)
								.then(Commands.literal("block")
										.executes(context -> tagObjects(context.getSource().getPlayerOrException(), Tags.blocks(), ResourceLocationArgument.getId(context, "tag")))
								)
								.then(Commands.literal("fluid")
										.executes(context -> tagObjects(context.getSource().getPlayerOrException(), Tags.fluids(), ResourceLocationArgument.getId(context, "tag")))
								)
								.then(Commands.literal("entity_type")
										.executes(context -> tagObjects(context.getSource().getPlayerOrException(), Tags.entityTypes(), ResourceLocationArgument.getId(context, "tag")))
								)
						)
				)
				.then(Commands.literal("wiki")
						.executes(context -> wiki(context.getSource()))
				)
		);

		dispatcher.register(Commands.literal("kjs_hand")
				.executes(context -> hand(context.getSource().getPlayerOrException()))
		);
	}

	private static Component copy(String s, ChatFormatting col, String info)
	{
		TextComponent component = new TextComponent("- ");
		component.setStyle(component.getStyle().withColor(TextColor.fromLegacyFormat(ChatFormatting.GRAY)));
		component.setStyle(component.getStyle().withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, s)));
		component.setStyle(component.getStyle().withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent(info + " (Click to copy)"))));
		component.append(new TextComponent(s).withStyle(col));
		return component;
	}

	private static int hand(ServerPlayer player)
	{
		player.sendMessage(new TextComponent("Item in hand:"), Util.NIL_UUID);
		ItemStackJS stack = ItemStackJS.of(player.getMainHandItem());
		player.sendMessage(copy(stack.toString(), ChatFormatting.GREEN, "Item ID"), Util.NIL_UUID);

		List<ResourceLocation> tags = new ArrayList<>(Tags.byItem(stack.getItem()));
		tags.sort(null);

		for (ResourceLocation id : tags)
		{
			player.sendMessage(copy("'#" + id + "'", ChatFormatting.YELLOW, "Item Tag [" + TagIngredientJS.createTag(id.toString()).getStacks().size() + " items]"), Util.NIL_UUID);
		}

		player.sendMessage(copy("'@" + stack.getMod() + "'", ChatFormatting.AQUA, "Mod [" + new ModIngredientJS(stack.getMod()).getStacks().size() + " items]"), Util.NIL_UUID);

		if (stack.getItem().getItemCategory() != null)
		{
			player.sendMessage(copy("'%" + stack.getItemGroup() + "'", ChatFormatting.LIGHT_PURPLE, "Item Group [" + new GroupIngredientJS(stack.getItem().getItemCategory()).getStacks().size() + " items]"), Util.NIL_UUID);
		}

		return 1;
	}

	private static int outputRecipes(ServerPlayer player)
	{
		player.sendMessage(new TextComponent("WIP!"), Util.NIL_UUID);
		return Command.SINGLE_SUCCESS;
	}

	private static int inputRecipes(ServerPlayer player)
	{
		player.sendMessage(new TextComponent("WIP!"), Util.NIL_UUID);
		return Command.SINGLE_SUCCESS;
	}

	private static int checkRecipeConflicts(ServerPlayer player)
	{
		player.sendMessage(new TextComponent("WIP!"), Util.NIL_UUID);
		return Command.SINGLE_SUCCESS;
	}

	private static int tagObjects(ServerPlayer player, TagCollection<?> collection, ResourceLocation t)
	{
		Tag<?> tag = collection.getTag(t);

		if (tag == null || tag.getValues().isEmpty())
		{
			player.sendMessage(new TextComponent("Tag not found!"), Util.NIL_UUID);
			return 0;
		}

		player.sendMessage(new TextComponent(t + ":"), Util.NIL_UUID);

		for (Object o : tag.getValues())
		{
			ResourceLocation id = Registries.getRegistryName(o);
			if (id == null)
			{
				player.sendMessage(new TextComponent("- " + o), Util.NIL_UUID);
			}
			else
			{
				player.sendMessage(new TextComponent("- " + id.toString()), Util.NIL_UUID);
			}
		}

		player.sendMessage(new TextComponent(tag.getValues().size() + " elements"), Util.NIL_UUID);
		return Command.SINGLE_SUCCESS;
	}

	private static int wiki(CommandSourceStack source)
	{
		source.sendSuccess(new TextComponent("Click here to open the Wiki").withStyle(ChatFormatting.BLUE).withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://mods.latvian.dev/books/kubejs"))), false);
		return Command.SINGLE_SUCCESS;
	}
}