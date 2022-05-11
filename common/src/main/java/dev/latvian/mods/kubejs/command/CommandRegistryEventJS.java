package dev.latvian.mods.kubejs.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.latvian.mods.kubejs.server.ServerEventJS;
import dev.latvian.mods.kubejs.util.ClassWrapper;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

/**
 * @author LatvianModder
 */
public class CommandRegistryEventJS extends ServerEventJS {
	public final CommandDispatcher<CommandSourceStack> dispatcher;
	public final Commands.CommandSelection selection;

	public CommandRegistryEventJS(CommandDispatcher<CommandSourceStack> dispatcher, Commands.CommandSelection selection) {
		this.dispatcher = dispatcher;
		this.selection = selection;
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

}