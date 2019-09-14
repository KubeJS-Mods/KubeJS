package dev.latvian.kubejs.command;

import dev.latvian.kubejs.documentation.Documentation;
import dev.latvian.kubejs.documentation.DocumentationServer;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

/**
 * @author LatvianModder
 */
public class CommandDocs extends CommandBase
{
	@Override
	public String getName()
	{
		return "docs";
	}

	@Override
	public String getUsage(ICommandSender sender)
	{
		return "commands.kubejs.docs.usage";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args)
	{
		Documentation.clearCache();
		DocumentationServer.INSTANCE.startServer();
		ITextComponent component = new TextComponentString("KubeJS docs @ ");
		String url = DocumentationServer.INSTANCE.getUrl(server);
		ITextComponent urlc = new TextComponentString(url);
		urlc.getStyle().setColor(TextFormatting.BLUE);
		urlc.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url));
		urlc.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString("Click to open URL")));
		component.appendSibling(urlc);
		sender.sendMessage(component);
	}
}