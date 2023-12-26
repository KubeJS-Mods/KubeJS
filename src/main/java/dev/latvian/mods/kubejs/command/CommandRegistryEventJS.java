package dev.latvian.mods.kubejs.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.latvian.mods.kubejs.event.EventJS;
import dev.latvian.mods.kubejs.util.ClassWrapper;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;

public class CommandRegistryEventJS extends EventJS {
	public final CommandDispatcher<CommandSourceStack> dispatcher;
	public final CommandBuildContext context;
	public final Commands.CommandSelection selection;

	public CommandRegistryEventJS(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context, Commands.CommandSelection selection) {
		this.dispatcher = dispatcher;
		this.context = context;
		this.selection = selection;
	}

	public boolean isForSinglePlayer() {
		return selection.includeIntegrated;
	}

	public boolean isForMultiPlayer() {
		return selection.includeDedicated;
	}

	public CommandBuildContext getRegistry() {
		return context;
	}

	public LiteralCommandNode<CommandSourceStack> register(LiteralArgumentBuilder<CommandSourceStack> command) {
		return dispatcher.register(command);
	}

	public ClassWrapper<Commands> getCommands() {
		return new ClassWrapper<>(Commands.class);
	}

	public ClassWrapper<ArgumentTypeWrappers> getArguments() {
		return new ClassWrapper<>(ArgumentTypeWrappers.class);
	}

	// Used to access the static members of SharedSuggestionProvider
	// can be used within commands like so:
	// [cmd] .suggests((ctx, builder) => event.builtinSuggestions.suggest(["123", "456"], builder))
	public ClassWrapper<SharedSuggestionProvider> getBuiltinSuggestions() {
		return new ClassWrapper<>(SharedSuggestionProvider.class);
	}

}