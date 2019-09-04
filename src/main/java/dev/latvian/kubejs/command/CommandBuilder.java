package dev.latvian.kubejs.command;

import dev.latvian.kubejs.server.ServerJS;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandNotFoundException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author LatvianModder
 */
public class CommandBuilder
{
	@FunctionalInterface
	public interface ExecuteFunction
	{
		void execute(CommandSender sender, List<String> args);
	}

	@FunctionalInterface
	public interface UsernameFunction
	{
		boolean isUsername(List<String> args, int index);
	}

	public final transient Consumer<CommandBase> callback;
	public final String name;
	public final List<String> aliases;
	public ExecuteFunction execute;
	public UsernameFunction username;
	public int requiredPermissionLevel;

	public CommandBuilder(Consumer<CommandBase> c, String n)
	{
		callback = c;
		name = n;
		aliases = new ArrayList<>();
		execute = null;
		username = null;
		requiredPermissionLevel = 0;
	}

	public CommandBuilder alias(String a)
	{
		aliases.add(a);
		return this;
	}

	public CommandBuilder execute(ExecuteFunction e)
	{
		execute = e;
		return this;
	}

	public CommandBuilder username(UsernameFunction u)
	{
		username = u;
		return this;
	}

	public CommandBuilder username(final int index)
	{
		username = (args, i) -> index == i;
		return this;
	}

	public CommandBuilder op()
	{
		requiredPermissionLevel = 2;
		return this;
	}

	public void add()
	{
		callback.accept(new Cmd(this));
	}

	private static class Cmd extends CommandBase
	{
		private final CommandBuilder properties;

		private Cmd(CommandBuilder p)
		{
			properties = p;
		}

		@Override
		public String getName()
		{
			return properties.name;
		}

		@Override
		public List<String> getAliases()
		{
			return properties.aliases;
		}

		@Override
		public String getUsage(ICommandSender sender)
		{
			return "commands." + properties.name + ".usage";
		}

		@Override
		public boolean isUsernameIndex(String[] args, int index)
		{
			return properties.username != null && properties.username.isUsername(Arrays.asList(args), index);
		}

		@Override
		public int getRequiredPermissionLevel()
		{
			return properties.requiredPermissionLevel;
		}

		@Override
		public boolean checkPermission(MinecraftServer server, ICommandSender sender)
		{
			return properties.requiredPermissionLevel == 0 || super.checkPermission(server, sender);
		}

		@Override
		public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos)
		{
			if (args.length == 0)
			{
				return Collections.emptyList();
			}
			else if (isUsernameIndex(args, args.length - 1))
			{
				return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
			}

			return super.getTabCompletions(server, sender, args, pos);
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
		{
			if (properties.execute == null)
			{
				throw new CommandNotFoundException();
			}

			properties.execute.execute(new CommandSender(ServerJS.instance, sender), Arrays.asList(args));
		}
	}
}