package dev.latvian.mods.kubejs.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import dev.latvian.mods.kubejs.core.WithPersistentData;
import dev.latvian.mods.kubejs.util.UtilsJS;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.CompoundTagArgument;
import net.minecraft.commands.arguments.ObjectiveArgument;
import net.minecraft.commands.arguments.ScoreHolderArgument;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;

import java.util.Collection;

public class PersistentDataCommands {
	@FunctionalInterface
	public interface PersistentDataFactory {
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

	public static ArgumentBuilder<CommandSourceStack, ?> addPersistentDataCommands(ArgumentBuilder<CommandSourceStack, ?> cmd, PersistentDataFactory factory) {
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
}
