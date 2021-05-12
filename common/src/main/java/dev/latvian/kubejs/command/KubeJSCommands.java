package dev.latvian.kubejs.command;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.docs.DocumentationEvent;
import dev.latvian.kubejs.docs.TypeDefinition;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.item.ingredient.GroupIngredientJS;
import dev.latvian.kubejs.item.ingredient.ModIngredientJS;
import dev.latvian.kubejs.item.ingredient.TagIngredientJS;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.server.CustomCommandEventJS;
import dev.latvian.kubejs.server.ServerSettings;
import dev.latvian.kubejs.util.Tags;
import dev.latvian.kubejs.util.UtilsJS;
import me.shedaniel.architectury.registry.Registries;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.commands.ReloadCommand;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagCollection;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.WorldData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class KubeJSCommands {
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal("kubejs")
				.then(Commands.literal("custom_command")
						.then(Commands.argument("id", StringArgumentType.word())
								.executes(context -> customCommand(context.getSource(), StringArgumentType.getString(context, "id")))
						)
				)
				.then(Commands.literal("hand")
						.executes(context -> hand(context.getSource().getPlayerOrException(), InteractionHand.MAIN_HAND))
				)
				.then(Commands.literal("offhand")
						.executes(context -> hand(context.getSource().getPlayerOrException(), InteractionHand.OFF_HAND))
				)
				.then(Commands.literal("inventory")
						.executes(context -> inventory(context.getSource().getPlayerOrException()))
				)
				.then(Commands.literal("hotbar")
						.executes(context -> hotbar(context.getSource().getPlayerOrException()))
				)
				.then(Commands.literal("errors")
						.executes(context -> errors(context.getSource()))
				)
				.then(Commands.literal("warnings")
						.executes(context -> warnings(context.getSource()))
				)
				.then(Commands.literal("reload_startup_scripts")
						.requires(source -> source.getServer().isSingleplayer() || source.hasPermission(2))
						.executes(context -> reloadStartup(context.getSource()))
				)
				.then(Commands.literal("export")
						.requires(source -> source.getServer().isSingleplayer() || source.hasPermission(2))
						.executes(context -> export(context.getSource()))
				)
				/*
				.then(Commands.literal("output_recipes")
						.executes(context -> outputRecipes(context.getSource().getPlayerOrException()))
				)
				.then(Commands.literal("input_recipes")
						.executes(context -> inputRecipes(context.getSource().getPlayerOrException()))
				)
				.then(Commands.literal("check_recipe_conflicts")
						.executes(context -> checkRecipeConflicts(context.getSource().getPlayerOrException()))
				)
				 */
				.then(Commands.literal("list_tag")
						.then(Commands.argument("tag", ResourceLocationArgument.id())
								.executes(context -> tagObjects(context.getSource().getPlayerOrException(), Tags.items(), Registry.ITEM_REGISTRY, ResourceLocationArgument.getId(context, "tag")))
								.then(Commands.literal("item")
										.executes(context -> tagObjects(context.getSource().getPlayerOrException(), Tags.items(), Registry.ITEM_REGISTRY, ResourceLocationArgument.getId(context, "tag")))
								)
								.then(Commands.literal("block")
										.executes(context -> tagObjects(context.getSource().getPlayerOrException(), Tags.blocks(), Registry.BLOCK_REGISTRY, ResourceLocationArgument.getId(context, "tag")))
								)
								.then(Commands.literal("fluid")
										.executes(context -> tagObjects(context.getSource().getPlayerOrException(), Tags.fluids(), Registry.FLUID_REGISTRY, ResourceLocationArgument.getId(context, "tag")))
								)
								.then(Commands.literal("entity_type")
										.executes(context -> tagObjects(context.getSource().getPlayerOrException(), Tags.entityTypes(), Registry.ENTITY_TYPE_REGISTRY, ResourceLocationArgument.getId(context, "tag")))
								)
						)
				)
				.then(Commands.literal("wiki")
						.executes(context -> wiki(context.getSource()))
				)
				.then(Commands.literal("generate_docs")
						.executes(context -> generateDocs(context.getSource()))
				)
		);

		dispatcher.register(Commands.literal("kjs_hand")
				.executes(context -> hand(context.getSource().getPlayerOrException(), InteractionHand.MAIN_HAND))
		);
	}

	private static Component copy(String s, ChatFormatting col, String info) {
		TextComponent component = new TextComponent("- ");
		component.setStyle(component.getStyle().withColor(TextColor.fromLegacyFormat(ChatFormatting.GRAY)));
		component.setStyle(component.getStyle().withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, s)));
		component.setStyle(component.getStyle().withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent(info + " (Click to copy)"))));
		component.append(new TextComponent(s).withStyle(col));
		return component;
	}

	private static int customCommand(CommandSourceStack source, String id) {
		new CustomCommandEventJS(source.getLevel(), source.getEntity(), new BlockPos(source.getPosition()), id).post(ScriptType.SERVER, KubeJSEvents.SERVER_CUSTOM_COMMAND, id);
		return 1;
	}

	private static int hand(ServerPlayer player, InteractionHand hand) {
		player.sendMessage(new TextComponent("Item in hand:"), Util.NIL_UUID);
		ItemStackJS stack = ItemStackJS.of(player.getItemInHand(hand));
		player.sendMessage(copy(stack.toString(), ChatFormatting.GREEN, "Item ID"), Util.NIL_UUID);

		List<ResourceLocation> tags = new ArrayList<>(Tags.byItem(stack.getItem()));
		tags.sort(null);

		for (ResourceLocation id : tags) {
			player.sendMessage(copy("'#" + id + "'", ChatFormatting.YELLOW, "Item Tag [" + TagIngredientJS.createTag(id.toString()).getStacks().size() + " items]"), Util.NIL_UUID);
		}

		player.sendMessage(copy("'@" + stack.getMod() + "'", ChatFormatting.AQUA, "Mod [" + new ModIngredientJS(stack.getMod()).getStacks().size() + " items]"), Util.NIL_UUID);

		if (stack.getItem().getItemCategory() != null) {
			player.sendMessage(copy("'%" + stack.getItemGroup() + "'", ChatFormatting.LIGHT_PURPLE, "Item Group [" + new GroupIngredientJS(stack.getItem().getItemCategory()).getStacks().size() + " items]"), Util.NIL_UUID);
		}

		return 1;
	}

	private static int inventory(ServerPlayer player) {
		return dump(player.inventory.items, player, "Inventory");
	}

	private static int hotbar(ServerPlayer player) {
		return dump(player.inventory.items.subList(0, 9), player, "Hotbar");
	}

	private static int dump(List<ItemStack> stacks, ServerPlayer player, String name) {
		List<ItemStackJS> stackList = new ArrayList<>(stacks.size());
		for (ItemStack stack : stacks) {
			if (!stack.isEmpty()) {
				stackList.add(ItemStackJS.of(stack));
			}
		}
		String dump = stackList.toString();
		player.sendMessage(copy(dump, ChatFormatting.WHITE, name + " Item List"), Util.NIL_UUID);
		return 1;
	}

	private static int errors(CommandSourceStack source) {
		if (ScriptType.SERVER.errors.isEmpty()) {
			source.sendSuccess(new TextComponent("No errors found!").withStyle(ChatFormatting.GREEN), false);

			if (!ScriptType.SERVER.warnings.isEmpty()) {
				source.sendSuccess(new TextComponent(ScriptType.SERVER.warnings.size() + " warnings found. Run /kubejs warnings to see them").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFFA500))), false);
			}
			return 1;
		}

		for (int i = 0; i < ScriptType.SERVER.errors.size(); i++) {
			source.sendSuccess(new TextComponent("[" + (i + 1) + "] " + ScriptType.SERVER.errors.get(i)).withStyle(ChatFormatting.RED), false);
		}

		source.sendSuccess(new TextComponent("More info in 'logs/kubejs/server.txt'").withStyle(ChatFormatting.DARK_RED), false);

		if (!ScriptType.SERVER.warnings.isEmpty()) {
			source.sendSuccess(new TextComponent(ScriptType.SERVER.warnings.size() + " warnings found. Run '/kubejs warnings' to see them").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFFA500))), false);
		}

		return 1;
	}

	private static int warnings(CommandSourceStack source) {
		if (ScriptType.SERVER.warnings.isEmpty()) {
			source.sendSuccess(new TextComponent("No warnings found!").withStyle(ChatFormatting.GREEN), false);
			return 1;
		}

		for (int i = 0; i < ScriptType.SERVER.warnings.size(); i++) {
			source.sendSuccess(new TextComponent("[" + (i + 1) + "] " + ScriptType.SERVER.warnings.get(i)).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFFA500))), false);
		}

		return 1;
	}

	private static int reloadStartup(CommandSourceStack source) {
		KubeJS.startupScriptManager.unload();
		KubeJS.startupScriptManager.loadFromDirectory();
		KubeJS.startupScriptManager.load();
		UtilsJS.postModificationEvents();
		source.sendSuccess(new TextComponent("Done!"), false);
		return 1;
	}

	private static int export(CommandSourceStack source) {
		if (ServerSettings.dataExport != null) {
			return 0;
		}

		ServerSettings.source = source;
		ServerSettings.dataExport = new JsonObject();
		source.sendSuccess(new TextComponent("Reloading server and exporting data..."), false);

		MinecraftServer minecraftServer = source.getServer();
		PackRepository packRepository = minecraftServer.getPackRepository();
		WorldData worldData = minecraftServer.getWorldData();
		Collection<String> collection = packRepository.getSelectedIds();
		packRepository.reload();
		Collection<String> collection2 = Lists.newArrayList(collection);
		Collection<String> collection3 = worldData.getDataPackConfig().getDisabled();

		for (String string : packRepository.getAvailableIds()) {
			if (!collection3.contains(string) && !collection2.contains(string)) {
				collection2.add(string);
			}
		}

		ReloadCommand.reloadPacks(collection2, source);
		return 1;
	}

	private static int outputRecipes(ServerPlayer player) {
		player.sendMessage(new TextComponent("WIP!"), Util.NIL_UUID);
		return Command.SINGLE_SUCCESS;
	}

	private static int inputRecipes(ServerPlayer player) {
		player.sendMessage(new TextComponent("WIP!"), Util.NIL_UUID);
		return Command.SINGLE_SUCCESS;
	}

	private static int checkRecipeConflicts(ServerPlayer player) {
		player.sendMessage(new TextComponent("WIP!"), Util.NIL_UUID);
		return Command.SINGLE_SUCCESS;
	}

	private static <T> int tagObjects(ServerPlayer player, TagCollection<T> collection, ResourceKey<Registry<T>> reg, ResourceLocation t) {
		Tag<T> tag = collection.getTag(t);

		if (tag == null || tag.getValues().isEmpty()) {
			player.sendMessage(new TextComponent("Tag not found!"), Util.NIL_UUID);
			return 0;
		}

		player.sendMessage(new TextComponent(t + ":"), Util.NIL_UUID);

		for (T item : tag.getValues()) {
			ResourceLocation id = Registries.getId(item, reg);
			if (id == null) {
				player.sendMessage(new TextComponent("- " + item), Util.NIL_UUID);
			} else {
				player.sendMessage(new TextComponent("- " + id.toString()), Util.NIL_UUID);
			}
		}

		player.sendMessage(new TextComponent(tag.getValues().size() + " elements"), Util.NIL_UUID);
		return Command.SINGLE_SUCCESS;
	}

	private static int wiki(CommandSourceStack source) {
		source.sendSuccess(new TextComponent("Click here to open the Wiki").withStyle(ChatFormatting.BLUE).withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://kubejs.com/"))), false);
		return Command.SINGLE_SUCCESS;
	}

	private static int generateDocs(CommandSourceStack source) {
		Map<Class<?>, TypeDefinition> map = DocumentationEvent.collectDocs();

		source.sendSuccess(new TextComponent("Docs generated"), false);
		return Command.SINGLE_SUCCESS;
	}
}