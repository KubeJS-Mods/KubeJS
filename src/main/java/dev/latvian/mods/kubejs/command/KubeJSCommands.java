package dev.latvian.mods.kubejs.command;

import com.google.common.base.Predicate;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import dev.latvian.mods.kubejs.CommonProperties;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.KubeJSPaths;
import dev.latvian.mods.kubejs.bindings.event.ServerEvents;
import dev.latvian.mods.kubejs.net.DisplayClientErrorsPayload;
import dev.latvian.mods.kubejs.net.DisplayServerErrorsPayload;
import dev.latvian.mods.kubejs.net.ReloadStartupScriptsPayload;
import dev.latvian.mods.kubejs.script.KubeJSContext;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.script.data.ExportablePackResources;
import dev.latvian.mods.kubejs.server.BasicCommandKubeEvent;
import dev.latvian.mods.kubejs.server.DataExport;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.network.PacketDistributor;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public class KubeJSCommands {
	public static final DynamicCommandExceptionType NO_REGISTRY = new DynamicCommandExceptionType(id -> Component.literal("No builtin or static registry found for " + id));

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		Predicate<CommandSourceStack> spOrOP = (source) -> source.getServer().isSingleplayer() || source.hasPermission(2);
		var cmd = Commands.literal("kubejs")
			.then(Commands.literal("help")
				.executes(context -> help(context.getSource()))
			)
			.then(Commands.literal("hand")
				.executes(context -> InformationCommands.hand(context.getSource().getPlayerOrException(), InteractionHand.MAIN_HAND))
			)
			.then(Commands.literal("offhand")
				.executes(context -> InformationCommands.hand(context.getSource().getPlayerOrException(), InteractionHand.OFF_HAND))
			)
			.then(Commands.literal("inventory")
				.executes(context -> InformationCommands.inventory(context.getSource().getPlayerOrException()))
			)
			.then(Commands.literal("hotbar")
				.executes(context -> InformationCommands.hotbar(context.getSource().getPlayerOrException()))
			)
			.then(Commands.literal("errors")
				.then(Commands.literal("startup")
					.requires(spOrOP)
					.executes(context -> errors(context.getSource(), ScriptType.STARTUP))
				)
				.then(Commands.literal("server")
					.requires(spOrOP)
					.executes(context -> errors(context.getSource(), ScriptType.SERVER))
				)
				.then(Commands.literal("client")
					.requires(source -> true)
					.executes(context -> errors(context.getSource(), ScriptType.CLIENT))
				)
			)
			.then(Commands.literal("reload")
				.then(Commands.literal("config")
					.requires(spOrOP)
					.executes(context -> reloadConfig(context.getSource()))
				)
				.then(Commands.literal("startup-scripts")
					.requires(spOrOP)
					.executes(context -> reloadStartup(context.getSource()))
				)
				.then(Commands.literal("server-scripts")
					.requires(spOrOP)
					.executes(context -> reloadServer(context.getSource()))
				)
				.then(Commands.literal("client-scripts")
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
				.requires(spOrOP)
				.executes(context -> export(context.getSource()))
				.then(Commands.literal("pack-zips")
					.executes(context -> exportPacks(context.getSource(), true))
				)
				.then(Commands.literal("pack-folders")
					.executes(context -> exportPacks(context.getSource(), false))
				)
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
			.then(Commands.literal("list-tag")
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
			.then(Commands.literal("dump")
				.then(Commands.literal("registry")
					.then(Commands.argument("registry", ResourceLocationArgument.id())
						.suggests((ctx, builder) -> SharedSuggestionProvider.suggest(
							ctx.getSource().registryAccess()
								.registries()
								.map(entry -> entry.key().location().toString()), builder)
						)
						.executes(ctx -> DumpCommands.registry(ctx.getSource(), registry(ctx, "registry")))
					)
				)
				.then(Commands.literal("events")
					.requires(spOrOP)
					.executes(context -> DumpCommands.events(context.getSource()))
				)
			)
			.then(Commands.literal("stages")
				.requires(spOrOP)
				.then(Commands.literal("add")
					.then(Commands.argument("player", EntityArgument.players())
						.then(Commands.argument("stage", StringArgumentType.string())
							.executes(context -> StageCommands.addStage(context.getSource(), EntityArgument.getPlayers(context, "player"), StringArgumentType.getString(context, "stage")))
						)
					)
				)
				.then(Commands.literal("remove")
					.then(Commands.argument("player", EntityArgument.players())
						.then(Commands.argument("stage", StringArgumentType.string())
							.executes(context -> StageCommands.removeStage(context.getSource(), EntityArgument.getPlayers(context, "player"), StringArgumentType.getString(context, "stage")))
						)
					)
				)
				.then(Commands.literal("clear")
					.then(Commands.argument("player", EntityArgument.players())
						.executes(context -> StageCommands.clearStages(context.getSource(), EntityArgument.getPlayers(context, "player")))
					)
				)
				.then(Commands.literal("list")
					.then(Commands.argument("player", EntityArgument.players())
						.executes(context -> StageCommands.listStages(context.getSource(), EntityArgument.getPlayers(context, "player")))
					)
				)
			)
			.then(Commands.literal("generate-typings")
				.requires(spOrOP)
				.executes(context -> generateTypings(context.getSource()))
			)
			.then(Commands.literal("packmode")
				.requires(spOrOP)
				.executes(context -> packmode(context.getSource(), ""))
				.then(Commands.argument("name", StringArgumentType.word())
					.executes(context -> packmode(context.getSource(), StringArgumentType.getString(context, "name")))
				)
			)
			.then(Commands.literal("persistent-data")
				.requires(spOrOP)
				.then(PersistentDataCommands.addPersistentDataCommands(Commands.literal("server"), ctx -> Set.of(ctx.getSource().getServer())))
				.then(Commands.literal("dimension")
					.then(PersistentDataCommands.addPersistentDataCommands(Commands.literal("*"), ctx -> (Collection<ServerLevel>) ctx.getSource().getServer().getAllLevels()))
					.then(PersistentDataCommands.addPersistentDataCommands(Commands.argument("dimension", DimensionArgument.dimension()), ctx -> Set.of(DimensionArgument.getDimension(ctx, "dimension"))))
				)
				.then(Commands.literal("entity")
					.then(PersistentDataCommands.addPersistentDataCommands(Commands.argument("entity", EntityArgument.entities()), ctx -> EntityArgument.getEntities(ctx, "entity"))))
			);

		if (!FMLLoader.isProduction()) {
			cmd.then(Commands.literal("eval")
				.requires(spOrOP)
				.then(Commands.argument("code", StringArgumentType.greedyString())
					.executes(ctx -> eval(ctx.getSource(), StringArgumentType.getString(ctx, "code")))
				)
			);
		}

		var cmd1 = dispatcher.register(cmd);
		dispatcher.register(Commands.literal("kjs").redirect(cmd1));

		for (var id : ServerEvents.BASIC_COMMAND.findUniqueExtraIds(ScriptType.SERVER)) {
			dispatcher.register(Commands.literal(id)
				.requires(spOrOP)
				.executes(ctx -> customCommand(ctx.getSource(), id, ""))
				.then(Commands.argument("input", StringArgumentType.greedyString())
					.executes(ctx -> customCommand(ctx.getSource(), id, StringArgumentType.getString(ctx, "input")))
				)
			);
		}
	}

	private static <T> ResourceKey<Registry<T>> registry(CommandContext<CommandSourceStack> ctx, String arg) {
		return ResourceKey.createRegistryKey(ResourceLocationArgument.getId(ctx, arg));
	}

	private static <T> Stream<TagKey<T>> allTags(CommandSourceStack source, ResourceKey<Registry<T>> registry) throws CommandSyntaxException {
		return source.registryAccess().registry(registry)
			.orElseThrow(() -> NO_REGISTRY.create(registry.location()))
			.getTagNames();
	}

	private static void link(CommandSourceStack source, ChatFormatting color, String name, String url) {
		source.sendSystemMessage(Component.literal("• ").append(Component.literal(name).withStyle(color).withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url)))));
	}

	private static int help(CommandSourceStack source) {
		link(source, ChatFormatting.GOLD, "Wiki", "https://kubejs.com/?" + KubeJS.QUERY);
		link(source, ChatFormatting.GREEN, "Support", "https://kubejs.com/support?" + KubeJS.QUERY);
		link(source, ChatFormatting.BLUE, "Changelog", "https://kubejs.com/changelog?" + KubeJS.QUERY);
		return Command.SINGLE_SUCCESS;
	}

	private static int customCommand(CommandSourceStack source, String id, String input) {
		if (ServerEvents.BASIC_COMMAND.hasListeners(id)) {
			var result = ServerEvents.BASIC_COMMAND.post(new BasicCommandKubeEvent(source.getLevel(), source.getEntity(), BlockPos.containing(source.getPosition()), id, input.trim()), id);

			if (result.value() instanceof Throwable ex) {
				source.sendFailure(Component.literal(ex.toString()));
				return 0;
			}

			return 1;
		}

		return 0;
	}

	private static int errors(CommandSourceStack source, ScriptType type) throws CommandSyntaxException {
		if (type == ScriptType.CLIENT) {
			var player = source.getPlayerOrException();
			PacketDistributor.sendToPlayer(player, new DisplayClientErrorsPayload());
			return 1;
		}

		if (type.console.errors.isEmpty() && type.console.warnings.isEmpty()) {
			source.sendSystemMessage(Component.literal("No errors or warnings found!").withStyle(ChatFormatting.GREEN));
			return 0;
		}

		if (source.getServer().isSingleplayer()) {
			KubeJS.PROXY.openErrors(type);
			return 1;
		}

		var player = source.getPlayerOrException();
		var errors = new ArrayList<>(type.console.errors);
		var warnings = new ArrayList<>(type.console.warnings);
		player.sendSystemMessage(Component.literal("You need KubeJS on client side!").withStyle(ChatFormatting.RED), true);
		PacketDistributor.sendToPlayer(player, new DisplayServerErrorsPayload(type.ordinal(), errors, warnings));

		// FIXME
		/*
		var lines = ConsoleJS.SERVER.errors.toArray(ConsoleLine.EMPTY_ARRAY);

		if (lines.length == 0) {
			source.sendSystemMessage(Component.literal("No errors found!").withStyle(ChatFormatting.GREEN));

			if (!ConsoleJS.SERVER.warnings.isEmpty()) {
				source.sendSystemMessage(ConsoleJS.SERVER.warningsComponent("/kubejs warnings"));
			}
			return 1;
		}

		for (var i = 0; i < lines.length; i++) {
			var component = Component.literal((i + 1) + ") ").append(Component.literal(lines[i].getText()).withStyle(ChatFormatting.RED)).withStyle(ChatFormatting.DARK_RED);
			source.sendSystemMessage(component);
		}

		source.sendSuccess(() -> Component.literal("More info in ")
				.append(Component.literal("'logs/kubejs/server.log'")
					.kjs$clickOpenFile(ScriptType.SERVER.getLogFile().toString())
					.kjs$hover(Component.literal("Click to open"))).withStyle(ChatFormatting.DARK_RED),
			false);

		if (!ConsoleJS.SERVER.warnings.isEmpty()) {
			source.sendSystemMessage(ConsoleJS.SERVER.warningsComponent("/kubejs warnings"));
		}
		 */

		return 1;
	}

	private static int reloadConfig(CommandSourceStack source) {
		KubeJS.PROXY.reloadConfig();
		source.sendSystemMessage(Component.literal("Done!"));
		return 1;
	}

	private static int reloadStartup(CommandSourceStack source) {
		KubeJS.getStartupScriptManager().reload();
		source.sendSystemMessage(Component.literal("Done!"));
		PacketDistributor.sendToAllPlayers(new ReloadStartupScriptsPayload(source.getServer().isDedicatedServer()));
		return 1;
	}

	private static int reloadServer(CommandSourceStack source) {
		var resources = source.getServer().getServerResources();
		resources.managers().kjs$getServerScriptManager().reload();
		source.sendSuccess(() -> Component.literal("Done! To reload recipes, tags, loot tables and other datapack things, run ")
				.append(Component.literal("'/reload'")
					.kjs$clickRunCommand("/reload")
					.kjs$hover(Component.literal("Click to run"))),
			false);
		return 1;
	}

	// TODO: move these commands to client commands
	private static int reloadClient(CommandSourceStack source) {
		KubeJS.PROXY.reloadClientInternal();
		source.sendSystemMessage(Component.literal("Done! To reload textures, models and other assets, press F3 + T"));
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
		if (DataExport.export != null) {
			return 0;
		}

		DataExport.export = new DataExport();
		DataExport.export.source = source;
		source.sendSuccess(() -> Component.literal("Reloading server and exporting data..."), true);
		source.getServer().kjs$runCommand("reload");
		return 1;
	}

	private static void afterReload(CommandSourceStack source) {
		// System.out.println("Hello");
		source.sendSuccess(() -> Component.literal("Reloaded!"), true);
	}

	private static int exportPacks(CommandSourceStack source, boolean exportZip) {
		var packs = new ArrayList<ExportablePackResources>();

		for (var pack : source.getServer().getResourceManager().listPacks().toList()) {
			if (pack instanceof ExportablePackResources e) {
				packs.add(e);
			}
		}

		KubeJS.PROXY.export(packs);
		int success = 0;

		for (var pack : packs) {
			try {
				if (exportZip) {
					var path = KubeJSPaths.EXPORTED_PACKS.resolve(pack.packId() + ".zip");
					Files.deleteIfExists(path);

					try (var fs = FileSystems.newFileSystem(path, Map.of("create", true))) {
						pack.export(fs.getPath("."));
					}
				} else {
					var path = KubeJSPaths.EXPORTED_PACKS.resolve(pack.packId());

					if (Files.exists(path)) {
						Files.walk(path)
							.sorted(Comparator.reverseOrder())
							.map(Path::toFile)
							.forEach(File::delete);
					}

					Files.createDirectories(path);
					pack.export(path);
				}

				source.sendSuccess(() -> Component.literal("Successfully exported ").withStyle(ChatFormatting.GREEN).append(Component.literal(pack.packId()).withStyle(ChatFormatting.BLUE)), false);
				success++;
			} catch (IOException e) {
				e.printStackTrace();
				source.sendFailure(Component.literal("Failed to export %s!".formatted(pack)).withStyle(style ->
					style.withColor(ChatFormatting.RED)
						.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal(e.getMessage())))));
			}
		}

		int success1 = success;

		if (source.getServer().isSingleplayer() && !source.getServer().isPublished()) {
			source.sendSuccess(() -> Component.literal("Exported " + success1 + " packs").kjs$clickOpenFile(KubeJSPaths.EXPORTED_PACKS.toAbsolutePath().toString()), false);
		} else {
			source.sendSuccess(() -> Component.literal("Exported " + success1 + " packs"), false);
		}

		return success;
	}

	private static <T> int listTagsFor(CommandSourceStack source, ResourceKey<Registry<T>> registry) throws CommandSyntaxException {
		var tags = allTags(source, registry);

		source.sendSystemMessage(Component.empty());
		source.sendSystemMessage(Component.literal("List of all Tags for " + registry.location() + ":"));
		source.sendSystemMessage(Component.empty());

		var size = tags.map(TagKey::location).map(tag -> Component.literal("- %s".formatted(tag)).withStyle(Style.EMPTY
			.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/kubejs list_tag %s %s".formatted(registry.location(), tag)))
			.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("[Show all entries for %s]".formatted(tag))))
		)).mapToLong(msg -> {
			source.sendSystemMessage(msg);
			return 1;
		}).sum();

		source.sendSystemMessage(Component.empty());
		source.sendSystemMessage(Component.literal("Total: %d tags".formatted(size)));
		source.sendSystemMessage(Component.literal("(Click on any of the above tags to list their contents!)"));
		source.sendSystemMessage(Component.empty());

		return Command.SINGLE_SUCCESS;
	}

	private static <T> int tagObjects(CommandSourceStack source, TagKey<T> key) throws CommandSyntaxException {
		var registry = source.registryAccess()
			.registry(key.registry())
			.orElseThrow(() -> NO_REGISTRY.create(key.registry().location()));

		var tag = registry.getTag(key);

		if (tag.isEmpty()) {
			source.sendFailure(Component.literal("Tag not found or empty!"));
			return 0;
		}
		source.sendSystemMessage(Component.empty());
		source.sendSystemMessage(Component.literal("Contents of #" + key.location() + " [" + key.registry().location() + "]:"));
		source.sendSystemMessage(Component.empty());

		var items = tag.get();

		for (var holder : items) {
			var id = holder.unwrap().map(o -> o.location().toString(), o -> o + " (unknown ID)");
			source.sendSystemMessage(Component.literal("- " + id));
		}

		source.sendSystemMessage(Component.empty());
		source.sendSystemMessage(Component.literal("Total: " + items.size() + " elements"));
		source.sendSystemMessage(Component.empty());
		return Command.SINGLE_SUCCESS;
	}

	private static int generateTypings(CommandSourceStack source) {
		if (!source.getServer().isSingleplayer()) {
			source.sendFailure(Component.literal("You can only run this command in singleplayer!"));
			return 0;
		}

		KubeJS.PROXY.generateTypings(source);
		return 1;
	}

	private static int packmode(CommandSourceStack source, String packmode) {
		if (packmode.isEmpty()) {
			source.sendSuccess(() -> Component.literal("Current packmode: " + CommonProperties.get().packMode), false);
		} else {
			CommonProperties.get().setPackMode(packmode);
			source.sendSuccess(() -> Component.literal("Set packmode to: " + packmode), true);
		}

		return 1;
	}

	private static int eval(CommandSourceStack source, String code) {
		var cx = (KubeJSContext) source.getServer().getServerResources().managers().kjs$getServerScriptManager().contextFactory.enter();
		cx.evaluateString(cx.topLevelScope, code, "eval", 1, null);
		return 1;
	}
}