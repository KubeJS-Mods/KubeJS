package dev.latvian.mods.kubejs.command;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;

public class StageCommands {
	public static int addStage(CommandSourceStack source, Collection<ServerPlayer> players, String stage) {
		for (var p : players) {
			if (p.kjs$getStages().add(stage)) {
				source.sendSuccess(() -> Component.literal("Added '" + stage + "' stage for " + p.getScoreboardName()), true);
			}
		}

		return 1;
	}

	public static int removeStage(CommandSourceStack source, Collection<ServerPlayer> players, String stage) {
		for (var p : players) {
			if (p.kjs$getStages().remove(stage)) {
				source.sendSuccess(() -> Component.literal("Removed '" + stage + "' stage for " + p.getScoreboardName()), true);
			}
		}

		return 1;
	}

	public static int clearStages(CommandSourceStack source, Collection<ServerPlayer> players) {
		for (var p : players) {
			if (p.kjs$getStages().clear()) {
				source.sendSuccess(() -> Component.literal("Cleared stages for " + p.getScoreboardName()), true);
			}
		}

		return 1;
	}

	public static int listStages(CommandSourceStack source, Collection<ServerPlayer> players) {
		for (var p : players) {
			source.sendSystemMessage(Component.literal(p.getScoreboardName() + " stages:"));
			p.kjs$getStages().getAll().stream().sorted().forEach(s -> source.sendSystemMessage(Component.literal("- " + s)));
		}

		return 1;
	}
}
