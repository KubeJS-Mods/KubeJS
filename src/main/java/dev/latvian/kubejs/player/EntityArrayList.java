package dev.latvian.kubejs.player;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.documentation.P;
import dev.latvian.kubejs.documentation.T;
import dev.latvian.kubejs.entity.EntityJS;
import dev.latvian.kubejs.text.Text;
import dev.latvian.kubejs.text.TextString;
import dev.latvian.kubejs.util.ID;
import dev.latvian.kubejs.util.MessageSender;
import dev.latvian.kubejs.util.nbt.NBTBaseJS;
import dev.latvian.kubejs.world.WorldJS;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Predicate;

/**
 * @author LatvianModder
 */
public class EntityArrayList extends ArrayList<EntityJS> implements MessageSender
{
	private final WorldJS world;

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
			add(world.getEntity(entity));
		}
	}

	public WorldJS getWorld()
	{
		return world;
	}

	@Override
	public String getName()
	{
		return toString();
	}

	@Override
	public Text getDisplayName()
	{
		return new TextString(toString()).lightPurple();
	}

	@Override
	public void tell(@P("message") @T(Text.class) Object message)
	{
		ITextComponent component = Text.of(message).component();

		for (EntityJS entity : this)
		{
			entity.minecraftEntity.sendMessage(component);
		}
	}

	@Override
	public void setStatusMessage(@P("message") @T(Text.class) Object message)
	{
		ITextComponent component = Text.of(message).component();

		for (EntityJS entity : this)
		{
			if (entity.minecraftEntity instanceof EntityPlayerMP)
			{
				((EntityPlayerMP) entity.minecraftEntity).sendStatusMessage(component, true);
			}
		}
	}

	@Override
	public int runCommand(@P("command") String command)
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

	public void playSound(@P("id") Object id, @P("volume") float volume, @P("pitch") float pitch)
	{
		SoundEvent event = id instanceof SoundEvent ? (SoundEvent) id : SoundEvent.REGISTRY.getObject(ID.of(id).mc());

		if (event != null)
		{
			for (EntityJS entity : this)
			{
				entity.playSound(event, volume, pitch);
			}
		}
	}

	public void playSound(@P("id") Object id)
	{
		playSound(id, 1F, 1F);
	}

	public EntityArrayList filter(@P("filter") Predicate<EntityJS> filter)
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

	public void sendData(@P("channel") String channel, @Nullable @P("data") Object data)
	{
		NBTTagCompound nbt = NBTBaseJS.of(data).asCompound().createNBT();

		for (EntityJS entity : this)
		{
			if (entity instanceof PlayerJS)
			{
				KubeJS.PROXY.sendData(((PlayerJS) entity).minecraftPlayer, channel, nbt);
			}
		}
	}

	public EntityJS getFirst()
	{
		return get(0);
	}
}