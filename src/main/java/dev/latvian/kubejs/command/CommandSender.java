package dev.latvian.kubejs.command;

import dev.latvian.kubejs.player.PlayerJS;
import dev.latvian.kubejs.server.ServerJS;
import dev.latvian.kubejs.text.Text;
import dev.latvian.kubejs.util.MessageSender;
import dev.latvian.kubejs.world.BlockContainerJS;
import dev.latvian.kubejs.world.WorldJS;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;

/**
 * @author LatvianModder
 */
public class CommandSender implements MessageSender
{
	public final ServerJS server;
	public final ICommandSender sender;

	public CommandSender(ServerJS w, ICommandSender s)
	{
		server = w;
		sender = s;
	}

	@Override
	public String name()
	{
		return sender.getName();
	}

	@Override
	public Text displayName()
	{
		return Text.of(sender.getDisplayName());
	}

	public WorldJS world()
	{
		return server.world(sender.getEntityWorld());
	}

	public PlayerJS asPlayer() throws PlayerNotFoundException
	{
		return server.player(CommandBase.getCommandSenderAsPlayer(sender).getUniqueID());
	}

	public BlockContainerJS block()
	{
		BlockPos p = sender.getPosition();
		return world().block(p.getX(), p.getY(), p.getZ());
	}

	@Override
	public void tell(Object message)
	{
		sender.sendMessage(Text.of(message).component());
	}

	@Override
	public void statusMessage(Object message)
	{
		if (sender instanceof EntityPlayerMP)
		{
			((EntityPlayerMP) sender).sendStatusMessage(Text.of(message).component(), true);
		}
	}

	@Override
	public int runCommand(String command)
	{
		return server.server.getCommandManager().executeCommand(sender, command);
	}
}