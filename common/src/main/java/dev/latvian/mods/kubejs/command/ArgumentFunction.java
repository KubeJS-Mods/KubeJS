package dev.latvian.mods.kubejs.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;

interface ArgumentFunction<U> {
	U getResult(CommandContext<CommandSourceStack> context, String name) throws CommandSyntaxException;
}
