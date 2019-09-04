package dev.latvian.kubejs.player;

import dev.latvian.kubejs.entity.EntityJS;
import dev.latvian.kubejs.text.Text;
import dev.latvian.kubejs.text.TextString;
import dev.latvian.kubejs.util.MessageSender;
import dev.latvian.kubejs.world.WorldJS;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.ITextComponent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Predicate;

/**
 * @author LatvianModder
 */
public class EntityArrayList extends ArrayList<EntityJS> implements MessageSender
{
	public final WorldJS world;

	public EntityArrayList(WorldJS w, int size)
	{
		super(size);
		world = w;
	}

	public EntityArrayList(WorldJS w, Collection<? extends Entity> c)
	{
		this(w, c.size());

		for (Entity entity : c)
		{
			add(world.entity(entity));
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
		ITextComponent component = Text.of(message).component();

		for (EntityJS entity : this)
		{
			entity.entity.sendMessage(component);
		}
	}

	@Override
	public void statusMessage(Object message)
	{
		ITextComponent component = Text.of(message).component();

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

	public EntityArrayList filter(Predicate<EntityJS> filter)
	{
		if (isEmpty())
		{
			return this;
		}

		EntityArrayList list = new EntityArrayList(world, size());

		for (EntityJS entity : this)
		{
			if (filter.test(entity))
			{
				list.add(entity);
			}
		}

		return list;
	}
}