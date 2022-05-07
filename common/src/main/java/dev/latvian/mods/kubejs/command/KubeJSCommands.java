package dev.latvian.mods.kubejs.command;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.KubeJSEvents;
import dev.latvian.mods.kubejs.KubeJSPaths;
import dev.latvian.mods.kubejs.core.MinecraftServerKJS;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.kubejs.item.ingredient.GroupIngredientJS;
import dev.latvian.mods.kubejs.item.ingredient.ModIngredientJS;
import dev.latvian.mods.kubejs.item.ingredient.TagIngredientJS;
import dev.latvian.mods.kubejs.net.PaintMessage;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.script.data.VirtualKubeJSDataPack;
import dev.latvian.mods.kubejs.server.CustomCommandEventJS;
import dev.latvian.mods.kubejs.server.ServerScriptManager;
import dev.latvian.mods.kubejs.server.ServerSettings;
import dev.latvian.mods.kubejs.stages.Stages;
import dev.latvian.mods.kubejs.util.UtilsJS;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.CompoundTagArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.commands.ReloadCommand;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * @author LatvianModder
 */
public class KubeJSCommands {

	public static final DynamicCommandExceptionType NO_REGISTRY = new DynamicCommandExceptionType((id) ->
			new TextComponent("No builtin or static registry found for " + id)
	);

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
				.then(Commands.literal("reload")
						.then(Commands.literal("startup_scripts")
								.requires(source -> source.getServer().isSingleplayer() || source.hasPermission(2))
								.executes(context -> reloadStartup(context.getSource()))
						)
						.then(Commands.literal("server_scripts")
								.requires(source -> source.getServer().isSingleplayer() || source.hasPermission(2))
								.executes(context -> reloadServer(context.getSource()))
						)
						.then(Commands.literal("client_scripts")
								.requires(source -> true)
								.executes(context -> reloadClient(context.getSource()))
						)
						.then(Commands.literal("textures")
								.requires(source -> true)
								.executes(context -> reloadTextures(context.getSource()))
						)
						.then(Commands.literal("lang")
								.requires(source -> true)
								.executes(context -> reloadLang(context.getSource()))
						)
				)
				.then(Commands.literal("export")
						.requires(source -> source.getServer().isSingleplayer() || source.hasPermission(2))
						.executes(context -> export(context.getSource()))
				)
				.then(Commands.literal("export_virtual_data")
						.requires(source -> source.getServer().isSingleplayer() || source.hasPermission(2))
						.executes(context -> exportVirtualData(context.getSource())))
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
						.then(Commands.argument("registry", ResourceLocationArgument.id())
								.suggests((ctx, builder) -> SharedSuggestionProvider.suggest(
										ctx.getSource().registryAccess()
												.registries()
												.map(entry -> entry.key().location().toString()), builder)

								)
								.executes(ctx -> listTagsFor(ctx.getSource(), registry(ctx, "registry")))
								.then(Commands.argument("tag", ResourceLocationArgument.id())
										.suggests((ctx, builder) -> SharedSuggestionProvider.suggest(
												allTags(ctx.getSource(), registry(ctx, "registry"))
														.map(TagKey::location)
														.map(ResourceLocation::toString), builder)
										)
										.executes(ctx -> tagObjects(ctx.getSource(), TagKey.create(registry(ctx, "registry"),
												ResourceLocationArgument.getId(ctx, "tag")))
										)
								)
						)
				)
				.then(Commands.literal("wiki")
						.executes(context -> wiki(context.getSource()))
				)
				.then(Commands.literal("stages")
						.then(Commands.literal("add")
								.then(Commands.argument("player", EntityArgument.players())
										.then(Commands.argument("stage", StringArgumentType.string())
												.executes(context -> addStage(context.getSource(), EntityArgument.getPlayers(context, "player"), StringArgumentType.getString(context, "stage")))
										)
								)
						)
						.then(Commands.literal("remove")
								.then(Commands.argument("player", EntityArgument.players())
										.then(Commands.argument("stage", StringArgumentType.string())
												.executes(context -> removeStage(context.getSource(), EntityArgument.getPlayers(context, "player"), StringArgumentType.getString(context, "stage")))
										)
								)
						)
						.then(Commands.literal("clear")
								.then(Commands.argument("player", EntityArgument.players())
										.executes(context -> clearStages(context.getSource(), EntityArgument.getPlayers(context, "player")))
								)
						)
						.then(Commands.literal("list")
								.then(Commands.argument("player", EntityArgument.players())
										.executes(context -> listStages(context.getSource(), EntityArgument.getPlayers(context, "player")))
								)
						)
				)
				.then(Commands.literal("painter")
						.then(Commands.argument("player", EntityArgument.players())
								.then(Commands.argument("object", CompoundTagArgument.compoundTag())
										.executes(context -> painter(context.getSource(), EntityArgument.getPlayers(context, "player"), CompoundTagArgument.getCompoundTag(context, "object")))
								)
						)
				)
		);

		dispatcher.register(Commands.literal("kjs_hand")
				.executes(context -> hand(context.getSource().getPlayerOrException(), InteractionHand.MAIN_HAND))
		);
	}


	private static <T> ResourceKey<Registry<T>> registry(CommandContext<CommandSourceStack> ctx, String arg) {
		return ResourceKey.createRegistryKey(ResourceLocationArgument.getId(ctx, arg));
	}

	private static <T> Stream<TagKey<T>> allTags(CommandSourceStack source, ResourceKey<Registry<T>> registry) throws CommandSyntaxException {
		return source.registryAccess().registry(registry)
				.orElseThrow(() -> NO_REGISTRY.create(registry.location()))
				.getTagNames();
	}

	private static Component copy(String s, ChatFormatting col, String info) {
		var component = new TextComponent("- ");
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
		var stack = ItemStackJS.of(player.getItemInHand(hand));
		player.sendMessage(copy(stack.toString(), ChatFormatting.GREEN, "Item ID"), Util.NIL_UUID);

		List<ResourceLocation> tags = new ArrayList<>(stack.getTags());
		tags.sort(null);

		for (var id : tags) {
			player.sendMessage(copy("'#" + id + "'", ChatFormatting.YELLOW, "Item Tag [" + TagIngredientJS.createTag(id.toString()).getStacks().size() + " items]"), Util.NIL_UUID);
		}

		player.sendMessage(copy("'@" + stack.getMod() + "'", ChatFormatting.AQUA, "Mod [" + new ModIngredientJS(stack.getMod()).getStacks().size() + " items]"), Util.NIL_UUID);

		if (stack.getItem().getItemCategory() != null) {
			player.sendMessage(copy("'%" + stack.getItemGroup() + "'", ChatFormatting.LIGHT_PURPLE, "Item Group [" + new GroupIngredientJS(stack.getItem().getItemCategory()).getStacks().size() + " items]"), Util.NIL_UUID);
		}

		return 1;
	}

	private static int inventory(ServerPlayer player) {
		return dump(player.getInventory().items, player, "Inventory");
	}

	private static int hotbar(ServerPlayer player) {
		return dump(player.getInventory().items.subList(0, 9), player, "Hotbar");
	}

	private static int dump(List<ItemStack> stacks, ServerPlayer player, String name) {
		List<ItemStackJS> stackList = new ArrayList<>(stacks.size());
		for (var stack : stacks) {
			if (!stack.isEmpty()) {
				stackList.add(ItemStackJS.of(stack));
			}
		}
		var dump = stackList.toString();
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

		for (var i = 0; i < ScriptType.SERVER.errors.size(); i++) {
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

		for (var i = 0; i < ScriptType.SERVER.warnings.size(); i++) {
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

	private static int reloadServer(CommandSourceStack source) {
		ServerScriptManager.instance.reloadScriptManager(((MinecraftServerKJS) source.getServer()).getReloadableResourcesKJS().resourceManager());
		UtilsJS.postModificationEvents();
		source.sendSuccess(new TextComponent("Done! To reload recipes, tags, loot tables and other datapack things, run /reload"), false);
		return 1;
	}

	private static int reloadClient(CommandSourceStack source) {
		KubeJS.PROXY.reloadClientInternal();
		source.sendSuccess(new TextComponent("Done! To reload textures, models and other assets, press F3 + T"), false);
		return 1;
	}

	private static int reloadTextures(CommandSourceStack source) {
		KubeJS.PROXY.reloadTextures();
		return 1;
	}

	private static int reloadLang(CommandSourceStack source) {
		KubeJS.PROXY.reloadLang();
		return 1;
	}

	private static int export(CommandSourceStack source) {
		if (ServerSettings.dataExport != null) {
			return 0;
		}

		ServerSettings.source = source;
		ServerSettings.dataExport = new JsonObject();
		source.sendSuccess(new TextComponent("Reloading server and exporting data..."), false);

		var minecraftServer = source.getServer();
		var packRepository = minecraftServer.getPackRepository();
		var worldData = minecraftServer.getWorldData();
		var collection = packRepository.getSelectedIds();
		packRepository.reload();
		Collection<String> collection2 = Lists.newArrayList(collection);
		Collection<String> collection3 = worldData.getDataPackConfig().getDisabled();

		for (var string : packRepository.getAvailableIds()) {
			if (!collection3.contains(string) && !collection2.contains(string)) {
				collection2.add(string);
			}
		}

		ReloadCommand.reloadPacks(collection2, source);
		return 1;
	}

	private static int exportVirtualData(CommandSourceStack source) {
		return source.getServer().getResourceManager()
				.listPacks()
				.filter(pack -> pack instanceof VirtualKubeJSDataPack)
				.map(pack -> (VirtualKubeJSDataPack) pack)
				.mapToInt(pack -> {
							var path = KubeJSPaths.EXPORTED.resolve(pack.getName() + ".zip");
							try {
								Files.deleteIfExists(path);
								try (var fs = FileSystems.newFileSystem(path, Map.of("create", true))) {
									pack.export(fs);
								}
								source.sendSuccess(new TextComponent("Successfully exported %s to %s".formatted(pack, path)).withStyle(ChatFormatting.GREEN), false);
								return 1;
							} catch (IOException e) {
								e.printStackTrace();
								source.sendFailure(new TextComponent("Failed to export %s!".formatted(pack)).withStyle(style ->
										style.withColor(ChatFormatting.RED)
												.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent(e.getMessage())))));
								return 0;
							}
						}
				).sum();
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

	private static <T> int listTagsFor(CommandSourceStack source, ResourceKey<Registry<T>> registry) throws CommandSyntaxException {
		var tags = allTags(source, registry);

		source.sendSuccess(TextComponent.EMPTY, false);
		source.sendSuccess(new TextComponent("List of all Tags for " + registry.location() + ":"), false);
		source.sendSuccess(TextComponent.EMPTY, false);

		var size = tags.map(TagKey::location).map(tag -> new TextComponent("- " + tag).withStyle(Style.EMPTY
				.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/kubejs list_tag " + registry.location() + " " + tag))
				.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent("[Show all entries for " + tag + "]")))
		)).mapToLong(msg -> {
			source.sendSuccess(msg, false);
			return 1;
		}).sum();

		source.sendSuccess(TextComponent.EMPTY, false);
		source.sendSuccess(new TextComponent("Total: " + size + " tags"), false);
		source.sendSuccess(new TextComponent("(Click on any of the above tags to list their contents!)"), false);
		source.sendSuccess(TextComponent.EMPTY, false);

		return Command.SINGLE_SUCCESS;
	}

	private static <T> int tagObjects(CommandSourceStack source, TagKey<T> key) throws CommandSyntaxException {
		var registry = source.registryAccess()
				.registry(key.registry())
				.orElseThrow(() -> NO_REGISTRY.create(key.registry().location()));

		var tag = registry.getTag(key);

		if (tag.isEmpty()) {
			source.sendFailure(new TextComponent("Tag not found or empty!"));
			return 0;
		}
		source.sendSuccess(TextComponent.EMPTY, false);
		source.sendSuccess(new TextComponent("Contents of #" + key.location() + " [" + key.registry().location() + "]:"), false);
		source.sendSuccess(TextComponent.EMPTY, false);

		var items = tag.get();

		for (var holder : items) {
			var id = holder.unwrap().map(o -> o.location().toString(), o -> o + " (unknown ID)");
			source.sendSuccess(new TextComponent("- " + id), false);
		}

		source.sendSuccess(TextComponent.EMPTY, false);
		source.sendSuccess(new TextComponent("Total: " + items.size() + " elements"), false);
		source.sendSuccess(TextComponent.EMPTY, false);
		return Command.SINGLE_SUCCESS;
	}

	private static int wiki(CommandSourceStack source) {
		source.sendSuccess(new TextComponent("Click here to open the Wiki").withStyle(ChatFormatting.BLUE).withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://kubejs.com/"))), false);
		return Command.SINGLE_SUCCESS;
	}

	private static int addStage(CommandSourceStack source, Collection<ServerPlayer> players, String stage) {
		for (var p : players) {
			if (Stages.get(p).add(stage)) {
				source.sendSuccess(new TextComponent("Added '" + stage + "' stage for " + p.getScoreboardName()), false);
			}
		}

		return 1;
	}

	private static int removeStage(CommandSourceStack source, Collection<ServerPlayer> players, String stage) {
		for (var p : players) {
			if (Stages.get(p).remove(stage)) {
				source.sendSuccess(new TextComponent("Removed '" + stage + "' stage for " + p.getScoreboardName()), false);
			}
		}

		return 1;
	}

	private static int clearStages(CommandSourceStack source, Collection<ServerPlayer> players) {
		for (var p : players) {
			if (Stages.get(p).clear()) {
				source.sendSuccess(new TextComponent("Cleared stages for " + p.getScoreboardName()), false);
			}
		}

		return 1;
	}

	private static int listStages(CommandSourceStack source, Collection<ServerPlayer> players) {
		for (var p : players) {
			source.sendSuccess(new TextComponent(p.getScoreboardName() + " stages:"), false);
			Stages.get(p).getAll().stream().sorted().forEach(s -> source.sendSuccess(new TextComponent("- " + s), false));
		}

		return 1;
	}

	private static int painter(CommandSourceStack source, Collection<ServerPlayer> players, CompoundTag object) {
		new PaintMessage(object).sendTo(players);
		return 1;
	}
}