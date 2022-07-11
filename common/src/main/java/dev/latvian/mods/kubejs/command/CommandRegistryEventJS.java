package dev.latvian.mods.kubejs.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.latvian.mods.kubejs.server.ServerEventJS;
import dev.latvian.mods.kubejs.util.ClassWrapper;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.RegistryAccess;

/**
 * @author LatvianModder
 */
public class CommandRegistryEventJS extends ServerEventJS {
	public final CommandDispatcher<CommandSourceStack> dispatcher;
	public final Commands.CommandSelection selection;
	public final CommandBuildContext context;

	public CommandRegistryEventJS(CommandDispatcher<CommandSourceStack> dispatcher, Commands.CommandSelection selection) {
		this.dispatcher = dispatcher;
		this.selection = selection;
		// TODO: swap this with context from the event when forge fixes their stuff
		this.context = new CommandBuildContext(RegistryAccess.BUILTIN.get());
	}

	public boolean isForSinglePlayer() {
		return selection.includeIntegrated;
	}

	public boolean isForMultiPlayer() {
		return selection.includeDedicated;
	}

	public LiteralCommandNode<CommandSourceStack> register(LiteralArgumentBuilder<CommandSourceStack> command) {
		return dispatcher.register(command);
	}

	public ClassWrapper<Commands> getCommands() {
		return new ClassWrapper<>(Commands.class);
	}

	public ClassWrapper<ArgumentTypeWrapper> getArguments() {
		return new ClassWrapper<>(ArgumentTypeWrapper.class);
	}

	// Used to access the static members of SharedSuggestionProvider
	// can be used within commands like so:
	// [cmd] .suggests((ctx, builder) => event.builtinSuggestions.suggest(["123", "456"], builder))
	public ClassWrapper<SharedSuggestionProvider> getBuiltinSuggestions() {
		return new ClassWrapper<>(SharedSuggestionProvider.class);
	}

}