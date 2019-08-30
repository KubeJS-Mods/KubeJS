package dev.latvian.kubejs.player;

import dev.latvian.kubejs.entity.EntityJS;
import dev.latvian.kubejs.server.ServerJS;
import dev.latvian.kubejs.text.Text;
import dev.latvian.kubejs.text.TextString;
import dev.latvian.kubejs.text.TextUtilsJS;
import dev.latvian.kubejs.util.MessageSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.ITextComponent;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author LatvianModder
 */
public class EntityArrayList extends ArrayList<EntityJS> implements MessageSender
{
	public final ServerJS server;

	public EntityArrayList(ServerJS s, int size)
	{
		super(size);
		server = s;
	}

	public EntityArrayList(ServerJS s, Collection<? extends Entity> c)
	{
		super(c.size());
		server = s;

		for (Entity entity : c)
		{
			add(server.entity(entity));
		}
	}

	@Override
	public String name()
	{
		return toString();
	}

	@Override
	public Text displayName()
	{
		return new TextString(toString()).lightPurple();
	}

	@Override
	public void tell(Object message)
	{
		ITextComponent component = TextUtilsJS.INSTANCE.of(message).component();

		for (EntityJS entity : this)
		{
			entity.entity.sendMessage(component);
		}
	}

	@Override
	public void statusMessage(Object message)
	{
		ITextComponent component = TextUtilsJS.INSTANCE.of(message).component();

		for (EntityJS entity : this)
		{
			if (entity.entity instanceof EntityPlayerMP)
			{
				((EntityPlayerMP) entity.entity).sendStatusMessage(component, true);
			}
		}
	}

	@Override
	public int runCommand(String command)
	{
		int m = 0;

		for (EntityJS entity : this)
		{
			m = Math.max(m, entity.runCommand(command));
		}

		return m;
	}

	public void kill()
	{
		for (EntityJS entity : this)
		{
			entity.kill();
		}
	}
}