package dev.latvian.mods.kubejs.command;

import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import dev.latvian.mods.kubejs.CommonProperties;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.KubeJSPaths;
import dev.latvian.mods.kubejs.bindings.event.ServerEvents;
import dev.latvian.mods.kubejs.core.WithPersistentData;
import dev.latvian.mods.kubejs.event.EventGroups;
import dev.latvian.mods.kubejs.event.EventResult;
import dev.latvian.mods.kubejs.helpers.IngredientHelper;
import dev.latvian.mods.kubejs.net.DisplayClientErrorsPayload;
import dev.latvian.mods.kubejs.net.DisplayServerErrorsPayload;
import dev.latvian.mods.kubejs.net.PaintPayload;
import dev.latvian.mods.kubejs.net.ReloadStartupScriptsPayload;
import dev.latvian.mods.kubejs.script.KubeJSContext;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.script.data.ExportablePackResources;
import dev.latvian.mods.kubejs.server.CustomCommandKubeEvent;
import dev.latvian.mods.kubejs.server.DataExport;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.JavaMembers;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.CompoundTagArgument;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ObjectiveArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.ScoreHolderArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.network.PacketDistributor;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public class KubeJSCommands {

	private static final char UNICODE_TICK = '✔';
	private static final char UNICODE_CROSS = '✘';

	public static final DynamicCommandExceptionType NO_REGISTRY = new DynamicCommandExceptionType((id) ->
		Component.literal("No builtin or static registry found for " + id)
	);

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		Predicate<CommandSourceStack> spOrOP = (source) -> source.getServer().isSingleplayer() || source.hasPermission(2);
		var cmd = Commands.literal("kubejs")
			.then(Commands.literal("help")
				.executes(context -> help(context.getSource()))
			)
			.then(Commands.literal("custom_command")
				.then(Commands.argument("id", StringArgumentType.word())
					.suggests((ctx, builder) -> SharedSuggestionProvider.suggest(ServerEvents.CUSTOM_COMMAND.findUniqueExtraIds(ScriptType.SERVER).stream().map(String::valueOf), builder))
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
				.then(Commands.literal("startup_scripts")
					.requires(spOrOP)
					.executes(context -> reloadStartup(context.getSource()))
				)
				.then(Commands.literal("server_scripts")
					.requires(spOrOP)
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
				.requires(spOrOP)
				.executes(context -> export(context.getSource()))
				.then(Commands.literal("pack_zips")
					.executes(context -> exportPacks(context.getSource(), true))
				)
				.then(Commands.literal("pack_folders")
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
			.then(Commands.literal("dump_registry")
				.then(Commands.argument("registry", ResourceLocationArgument.id())
					.suggests((ctx, builder) -> SharedSuggestionProvider.suggest(
						ctx.getSource().registryAccess()
							.registries()
							.map(entry -> entry.key().location().toString()), builder)
					)
					.executes(ctx -> dumpRegistry(ctx.getSource(), registry(ctx, "registry")))
				)
			)
			.then(Commands.literal("stages")
				.requires(spOrOP)
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
				.requires(spOrOP)
				.then(Commands.argument("player", EntityArgument.players())
					.then(Commands.argument("object", CompoundTagArgument.compoundTag())
						.executes(context -> painter(context.getSource(), EntityArgument.getPlayers(context, "player"), CompoundTagArgument.getCompoundTag(context, "object")))
					)
				)
			)
			.then(Commands.literal("generate_typings")
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
			.then(Commands.literal("dump_internals")
				.then(Commands.literal("events")
					.requires(spOrOP)
					.executes(context -> dumpEvents(context.getSource()))
				)
			)
			.then(Commands.literal("persistent_data")
				.requires(spOrOP)
				.then(addPersistentDataCommands(Commands.literal("server"), ctx -> Set.of(ctx.getSource().getServer())))
				.then(Commands.literal("dimension")
					.then(addPersistentDataCommands(Commands.literal("*"), ctx -> (Collection<ServerLevel>) ctx.getSource().getServer().getAllLevels()))
					.then(addPersistentDataCommands(Commands.argument("dimension", DimensionArgument.dimension()), ctx -> Set.of(DimensionArgument.getDimension(ctx, "dimension"))))
				)
				.then(Commands.literal("entity")
					.then(addPersistentDataCommands(Commands.argument("entity", EntityArgument.entities()), ctx -> EntityArgument.getEntities(ctx, "entity"))))
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
	}

	private static int dumpEvents(CommandSourceStack source) {
		var groups = EventGroups.ALL.get().map();

		var output = KubeJSPaths.LOCAL.resolve("event_groups");

		// create a folder for each event group,
		// and a markdown file for each event handler in that group
		// the markdown file should contain:
		// - the event handler name (i.e. ServerEvents.recipes)
		// - the valid script types for that event
		// - a link to the event class on GitHub
		//   (base link is https://github.com/KubeJS-Mods/KubeJS/tree/1902/common/src/main/java/{package}/{class_name}.java,
		//   but we need to replace the package dots with slashes)
		// - a table of all (public, non-transient) fields and (public) methods in the event and their parameters
		// - a space for an example script
		for (var entry : groups.entrySet()) {
			var groupName = entry.getKey();
			var group = entry.getValue();

			var groupFolder = output.resolve(groupName);
			try {
				Files.createDirectories(groupFolder);
				FileUtils.cleanDirectory(groupFolder.toFile());
			} catch (IOException e) {
				ConsoleJS.SERVER.error("Failed to create folder for event group " + groupName, e);
				source.sendFailure(Component.literal("Failed to create folder for event group " + groupName));
				return 0;
			}

			for (var handlerEntry : group.getHandlers().entrySet()) {
				var handlerName = handlerEntry.getKey();
				var handler = handlerEntry.getValue();

				var handlerFile = groupFolder.resolve(handlerName + ".md");

				var fullName = "%s.%s".formatted(groupName, handlerName);

				var eventType = handler.eventType.get();

				var builder = new StringBuilder();

				builder.append("# ").append(fullName).append("\n\n");

				builder.append("## Basic info\n\n");

				builder.append("- Valid script types: ").append(handler.scriptTypePredicate.getValidTypes()).append("\n\n");

				builder.append("- Has result? ").append(handler.getHasResult() ? UNICODE_TICK : UNICODE_CROSS).append("\n\n");

				builder.append("- Event class: ");

				if (eventType.getPackageName().startsWith("dev.latvian.mods.kubejs")) {
					builder.append('[').append(UtilsJS.toMappedTypeString(eventType)).append(']')
						.append('(').append("https://github.com/KubeJS-Mods/KubeJS/tree/")
						.append(KubeJS.MC_VERSION_NUMBER)
						.append("/common/src/main/java/")
						.append(eventType.getPackageName().replace('.', '/'))
						.append('/').append(eventType.getSimpleName()).append(".java")
						.append(')');
				} else {
					builder.append(UtilsJS.toMappedTypeString(eventType)).append(" (third-party)");
				}

				builder.append("\n\n");

				var classInfo = eventType.getAnnotation(Info.class);
				if (classInfo != null) {
					builder.append("```\n")
						.append(classInfo.value())
						.append("```");
					builder.append("\n\n");
				}

				var scriptManager = source.getServer().getServerResources().managers().kjs$getServerScriptManager();
				var cx = (KubeJSContext) scriptManager.contextFactory.enter();

				var members = JavaMembers.lookupClass(cx, cx.topLevelScope, eventType, null, false);

				var hasDocumentedMembers = false;
				var documentedMembers = new StringBuilder("### Documented members:\n\n");

				builder.append("### Available fields:\n\n");
				builder.append("| Name | Type | Static? |\n");
				builder.append("| ---- | ---- | ------- |\n");
				for (var field : members.getAccessibleFields(cx, false)) {
					if (field.field.getDeclaringClass() == Object.class) {
						continue;
					}

					var typeName = UtilsJS.toMappedTypeString(field.field.getGenericType());
					builder.append("| ").append(field.name).append(" | ").append(typeName).append(" | ");
					builder.append(Modifier.isStatic(field.field.getModifiers()) ? UNICODE_TICK : UNICODE_CROSS).append(" |\n");

					var info = field.field.getAnnotation(Info.class);
					if (info != null) {
						hasDocumentedMembers = true;
						documentedMembers.append("- `").append(typeName).append(' ').append(field.name).append("`\n");
						documentedMembers.append("```\n");
						var desc = info.value();
						documentedMembers.append(desc);
						if (!desc.endsWith("\n")) {
							documentedMembers.append("\n");
						}
						documentedMembers.append("```\n\n");
					}
				}

				builder.append("\n").append("Note: Even if no fields are listed above, some methods are still available as fields through *beans*.\n\n");

				builder.append("### Available methods:\n\n");
				builder.append("| Name | Parameters | Return type | Static? |\n");
				builder.append("| ---- | ---------- | ----------- | ------- |\n");
				for (var method : members.getAccessibleMethods(cx, false)) {
					if (method.hidden || method.method.getDeclaringClass() == Object.class) {
						continue;
					}
					builder.append("| ").append(method.name).append(" | ");
					var params = method.method.getGenericParameterTypes();

					var paramTypes = new String[params.length];
					for (var i = 0; i < params.length; i++) {
						paramTypes[i] = UtilsJS.toMappedTypeString(params[i]);
					}
					builder.append(String.join(", ", paramTypes)).append(" | ");

					var returnType = UtilsJS.toMappedTypeString(method.method.getGenericReturnType());
					builder.append(" | ").append(returnType).append(" | ");
					builder.append(Modifier.isStatic(method.method.getModifiers()) ? UNICODE_TICK : UNICODE_CROSS).append(" |\n");

					var info = method.method.getAnnotation(Info.class);
					if (info != null) {
						hasDocumentedMembers = true;
						documentedMembers.append("- ").append('`');
						if (Modifier.isStatic(method.method.getModifiers())) {
							documentedMembers.append("static ");
						}
						documentedMembers.append(returnType).append(' ').append(method.name).append('(');

						var namedParams = info.params();
						var paramNames = new String[params.length];
						var signature = new String[params.length];
						for (var i = 0; i < params.length; i++) {
							var name = "var" + i;
							if (namedParams.length > i) {
								var name1 = namedParams[i].name();
								if (!Strings.isNullOrEmpty(name1)) {
									name = name1;
								}
							}
							paramNames[i] = name;
							signature[i] = paramTypes[i] + ' ' + name;
						}

						documentedMembers.append(String.join(", ", signature)).append(')').append('`').append("\n");

						if (params.length > 0) {
							documentedMembers.append("\n  Parameters:\n");
							for (var i = 0; i < params.length; i++) {
								documentedMembers.append("  - ")
									.append(paramNames[i])
									.append(": ")
									.append(paramTypes[i])
									.append(namedParams.length > i ? "- " + namedParams[i].value() : "")
									.append("\n");
							}
							documentedMembers.append("\n");
						}

						documentedMembers.append("```\n");
						var desc = info.value();
						documentedMembers.append(desc);
						if (!desc.endsWith("\n")) {
							documentedMembers.append("\n");
						}
						documentedMembers.append("```\n\n");
					}
				}

				builder.append("\n\n");

				if (hasDocumentedMembers) {
					builder.append(documentedMembers).append("\n\n");
				}

				builder.append("### Example script:\n\n");
				builder.append("```js\n");
				builder.append(fullName).append('(');
				if (handler.extra != null) {
					builder.append(handler.extraRequired ? "extra_id, " : "/* extra_id (optional), */ ");
				}
				builder.append("(event) => {\n");
				builder.append("\t// This space (un)intentionally left blank\n");
				builder.append("});\n");
				builder.append("```\n\n");

				try {
					Files.writeString(handlerFile, builder.toString());
				} catch (IOException e) {
					ConsoleJS.SERVER.error("Failed to write file for event handler " + fullName, e);
					source.sendFailure(Component.literal("Failed to write file for event handler " + fullName));
					return 0;
				}
			}
		}

		source.sendSystemMessage(Component.literal("Successfully dumped event groups to " + output));
		return 1;
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
		return copy(Component.literal(s).withStyle(col), info);
	}

	private static Component copy(Component c, String info) {
		return Component.literal("- ")
			.withStyle(ChatFormatting.GRAY)
			.withStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, c.getString())))
			.withStyle(Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal(info + " (Click to copy)"))))
			.append(c);
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

	private static int customCommand(CommandSourceStack source, String id) {
		if (ServerEvents.CUSTOM_COMMAND.hasListeners(id)) {
			var result = ServerEvents.CUSTOM_COMMAND.post(new CustomCommandKubeEvent(source.getLevel(), source.getEntity(), BlockPos.containing(source.getPosition()), id), id);

			if (result.type() == EventResult.Type.ERROR) {
				source.sendFailure(Component.literal(result.value().toString()));
				return 0;
			}

			return 1;
		}

		return 0;
	}

	private static int hand(ServerPlayer player, InteractionHand hand) {
		player.sendSystemMessage(Component.literal("Item in hand:"));
		var stack = player.getItemInHand(hand);
		var holder = stack.getItemHolder();

		// item info
		// id
		player.sendSystemMessage(copy(stack.kjs$toItemString0(player.server.registryAccess()), ChatFormatting.GREEN, "Item ID"));
		// item tags
		var itemTags = holder.tags().toList();
		for (var tag : itemTags) {
			var id = "'#%s'".formatted(tag.location());
			var size = BuiltInRegistries.ITEM.getTag(tag).map(HolderSet::size).orElse(0);
			player.sendSystemMessage(copy(id, ChatFormatting.YELLOW, "Item Tag [" + size + " items]"));
		}
		// mod
		player.sendSystemMessage(copy("'@" + stack.kjs$getMod() + "'", ChatFormatting.AQUA, "Mod [" + IngredientHelper.get().mod(stack.kjs$getMod()).kjs$getStacks().size() + " items]"));
		// TODO: creative tabs (neo has made them client only in 1.20.1, this is fixed in 1.20.4)
		/*var cat = stack.getItem().getItemCategory();
		if (cat != null) {
			player.sendSystemMessage(copy("'%" + cat.getRecipeFolderName() + "'", ChatFormatting.LIGHT_PURPLE, "Item Group [" + IngredientPlatformHelper.get().creativeTab(cat).kjs$getStacks().size() + " items]"));
		}*/

		// block info
		if (stack.getItem() instanceof BlockItem blockItem) {
			player.sendSystemMessage(Component.literal("Held block:"));
			var block = blockItem.getBlock();
			var blockHolder = block.builtInRegistryHolder();
			// id
			player.sendSystemMessage(copy(block.kjs$getId(), ChatFormatting.GREEN, "Block ID"));
			// block tags
			var blockTags = blockHolder.tags().toList();
			for (var tag : blockTags) {
				var id = "'#%s'".formatted(tag.location());
				var size = BuiltInRegistries.BLOCK.getTag(tag).map(HolderSet::size).orElse(0);
				player.sendSystemMessage(copy(id, ChatFormatting.YELLOW, "Block Tag [" + size + " items]"));
			}
		}

		// fluid info
		var containedFluid = FluidUtil.getFluidContained(stack);
		if (containedFluid.isPresent()) {
			player.sendSystemMessage(Component.literal("Held fluid:"));
			var fluid = containedFluid.orElseThrow();
			var fluidHolder = fluid.getFluid().builtInRegistryHolder();
			// id
			player.sendSystemMessage(copy(fluidHolder.key().location().toString(), ChatFormatting.GREEN, "Fluid ID"));
			// fluid tags
			var fluidTags = fluidHolder.tags().toList();
			for (var tag : fluidTags) {
				var id = "'#%s'".formatted(tag.location());
				var size = BuiltInRegistries.FLUID.getTag(tag).map(HolderSet::size).orElse(0);
				player.sendSystemMessage(copy(id, ChatFormatting.YELLOW, "Fluid Tag [" + size + " items]"));
			}
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
		var registries = player.server.registryAccess();
		var dump = stacks.stream().filter(is -> !is.isEmpty()).map(is -> is.kjs$toItemString0(registries)).toList();
		player.sendSystemMessage(copy(dump.toString(), ChatFormatting.WHITE, name + " Item List"));
		return 1;
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

	private static int outputRecipes(ServerPlayer player) {
		player.sendSystemMessage(Component.literal("WIP!"));
		return Command.SINGLE_SUCCESS;
	}

	private static int inputRecipes(ServerPlayer player) {
		player.sendSystemMessage(Component.literal("WIP!"));
		return Command.SINGLE_SUCCESS;
	}

	private static int checkRecipeConflicts(ServerPlayer player) {
		player.sendSystemMessage(Component.literal("WIP!"));
		return Command.SINGLE_SUCCESS;
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

	private static <T> int dumpRegistry(CommandSourceStack source, ResourceKey<Registry<T>> registry) throws CommandSyntaxException {
		var ids = source.registryAccess().registry(registry)
			.orElseThrow(() -> NO_REGISTRY.create(registry.location()))
			.holders();

		source.sendSystemMessage(Component.empty());
		source.sendSystemMessage(Component.literal("List of all entries for registry " + registry.location() + ":"));
		source.sendSystemMessage(Component.empty());

		var size = ids.map(holder -> {
			var id = holder.key().location();
			return Component.literal("- %s".formatted(id)).withStyle(Style.EMPTY
				.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("%s [%s]".formatted(holder.value(), holder.value().getClass().getName()))))
			);
		}).mapToLong(msg -> {
			source.sendSystemMessage(msg);
			return 1;
		}).sum();

		source.sendSystemMessage(Component.empty());
		source.sendSystemMessage(Component.literal("Total: %d entries".formatted(size)));
		source.sendSystemMessage(Component.empty());


		return 1;
	}

	private static int addStage(CommandSourceStack source, Collection<ServerPlayer> players, String stage) {
		for (var p : players) {
			if (p.kjs$getStages().add(stage)) {
				source.sendSuccess(() -> Component.literal("Added '" + stage + "' stage for " + p.getScoreboardName()), true);
			}
		}

		return 1;
	}

	private static int removeStage(CommandSourceStack source, Collection<ServerPlayer> players, String stage) {
		for (var p : players) {
			if (p.kjs$getStages().remove(stage)) {
				source.sendSuccess(() -> Component.literal("Removed '" + stage + "' stage for " + p.getScoreboardName()), true);
			}
		}

		return 1;
	}

	private static int clearStages(CommandSourceStack source, Collection<ServerPlayer> players) {
		for (var p : players) {
			if (p.kjs$getStages().clear()) {
				source.sendSuccess(() -> Component.literal("Cleared stages for " + p.getScoreboardName()), true);
			}
		}

		return 1;
	}

	private static int listStages(CommandSourceStack source, Collection<ServerPlayer> players) {
		for (var p : players) {
			source.sendSystemMessage(Component.literal(p.getScoreboardName() + " stages:"));
			p.kjs$getStages().getAll().stream().sorted().forEach(s -> source.sendSystemMessage(Component.literal("- " + s)));
		}

		return 1;
	}

	private static int painter(CommandSourceStack source, Collection<ServerPlayer> players, CompoundTag object) {
		var payload = new ClientboundCustomPayloadPacket(new PaintPayload(object));

		for (var player : players) {
			player.connection.send(payload);
		}

		return 1;
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

	@FunctionalInterface
	private interface PersistentDataFactory {
		SimpleCommandExceptionType EMPTY_LIST = new SimpleCommandExceptionType(Component.literal("Expected at least one target"));

		Collection<? extends WithPersistentData> apply(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException;

		default Collection<? extends WithPersistentData> getAll(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
			var list = apply(ctx);

			if (list.isEmpty()) {
				throw EMPTY_LIST.create();
			}

			return list;
		}

		default WithPersistentData getOne(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
			var list = apply(ctx);

			if (list.isEmpty()) {
				throw EMPTY_LIST.create();
			}

			return list.iterator().next();
		}
	}

	private static ArgumentBuilder<CommandSourceStack, ?> addPersistentDataCommands(ArgumentBuilder<CommandSourceStack, ?> cmd, PersistentDataFactory factory) {
		cmd.then(Commands.literal("get")
			.then(Commands.literal("*")
				.executes(ctx -> {
					var objects = factory.getAll(ctx);

					for (var o : objects) {
						var dataStr = NbtUtils.toPrettyComponent(o.kjs$getPersistentData());
						ctx.getSource().sendSuccess(() -> Component.literal("").append(Component.literal("").withStyle(ChatFormatting.YELLOW).append(o.kjs$getDisplayName())).append(": ").append(dataStr), false);
					}

					return objects.size();
				})
			)
			.then(Commands.argument("key", StringArgumentType.string())
				.executes(ctx -> {
					var objects = factory.getAll(ctx);
					var key = StringArgumentType.getString(ctx, "key");

					for (var o : objects) {
						var data = key.equals("*") ? o.kjs$getPersistentData() : o.kjs$getPersistentData().get(key);
						var dataStr = data == null ? Component.literal("null").withStyle(ChatFormatting.RED) : NbtUtils.toPrettyComponent(data);
						ctx.getSource().sendSuccess(() -> Component.literal("").append(Component.literal("").withStyle(ChatFormatting.YELLOW).append(o.kjs$getDisplayName())).append(": ").append(dataStr), false);
					}

					return objects.size();
				})
			)
		);

		cmd.then(Commands.literal("merge")
			.then(Commands.argument("nbt", CompoundTagArgument.compoundTag())
				.executes(ctx -> {
					var objects = factory.getAll(ctx);
					var tag = CompoundTagArgument.getCompoundTag(ctx, "nbt");

					for (var o : objects) {
						o.kjs$getPersistentData().merge(tag);
						ctx.getSource().sendSuccess(() -> Component.literal("").append(Component.literal("").withStyle(ChatFormatting.YELLOW).append(o.kjs$getDisplayName())).append(" updated"), false);
					}

					return objects.size();
				})
			)
		);

		cmd.then(Commands.literal("remove")
			.then(Commands.literal("*")
				.executes(ctx -> {
					var objects = factory.getAll(ctx);

					for (var o : objects) {
						o.kjs$getPersistentData().getAllKeys().removeIf(UtilsJS.ALWAYS_TRUE);
					}

					return objects.size();
				})
			)
			.then(Commands.argument("key", StringArgumentType.string())
				.executes(ctx -> {
					var objects = factory.getAll(ctx);
					var key = StringArgumentType.getString(ctx, "key");

					for (var o : objects) {
						o.kjs$getPersistentData().remove(key);
					}

					return objects.size();
				})
			)
		);

		cmd.then(Commands.literal("scoreboard")
			.then(Commands.literal("import")
				.then(Commands.argument("key", StringArgumentType.string())
					.then(Commands.argument("target", ScoreHolderArgument.scoreHolder())
						.suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS)
						.then(Commands.argument("objective", ObjectiveArgument.objective())
							.executes(ctx -> {
								var scoreboard = ctx.getSource().getServer().getScoreboard();
								var objects = factory.getAll(ctx);
								var key = StringArgumentType.getString(ctx, "key");
								var target = ScoreHolderArgument.getName(ctx, "target");
								var objective = ObjectiveArgument.getObjective(ctx, "objective");

								var info = scoreboard.getPlayerScoreInfo(target, objective);

								int score = info != null ? info.value() : 0;

								for (var o : objects) {
									o.kjs$getPersistentData().putInt(key, score);
								}

								return objects.size();
							})
						)
					)
				)
			).then(Commands.literal("export")
				.then(Commands.argument("key", StringArgumentType.string())
					.then(Commands.argument("targets", ScoreHolderArgument.scoreHolders())
						.suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS)
						.then(Commands.argument("objective", ObjectiveArgument.objective())
							.executes(ctx -> {
								var scoreboard = ctx.getSource().getServer().getScoreboard();
								var object = factory.getOne(ctx);
								var key = StringArgumentType.getString(ctx, "key");
								var targets = ScoreHolderArgument.getNames(ctx, "targets");
								var objective = ObjectiveArgument.getObjective(ctx, "objective");

								int score = object.kjs$getPersistentData().getInt(key);

								for (var target : targets) {
									scoreboard.getOrCreatePlayerScore(target, objective).set(score);
								}

								return 1;
							})
						)
					)
				)
			)
		);

		return cmd;
	}

	private static int eval(CommandSourceStack source, String code) {
		var cx = (KubeJSContext) source.getServer().getServerResources().managers().kjs$getServerScriptManager().contextFactory.enter();
		cx.evaluateString(cx.topLevelScope, code, "eval", 1, null);
		return 1;
	}
}