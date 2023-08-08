package dev.latvian.mods.kubejs.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;

public interface ArgumentTypeWrapper {
	ArgumentType<?> create(CommandRegistryEventJS event);

	Object getResult(CommandContext<CommandSourceStack> context, String input) throws CommandSyntaxException;
}
