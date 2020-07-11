package dev.latvian.kubejs.player;

import dev.latvian.kubejs.docs.ID;
import dev.latvian.kubejs.entity.EntityJS;
import dev.latvian.kubejs.text.Text;
import dev.latvian.kubejs.text.TextString;
import dev.latvian.kubejs.util.MapJS;
import dev.latvian.kubejs.util.MessageSender;
import dev.latvian.kubejs.util.UtilsJS;
import dev.latvian.kubejs.world.WorldJS;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.registries.ForgeRegistries;

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

	public EntityArrayList(WorldJS w, Iterable<? extends Entity> c)
	{
		this(w, c instanceof Collection ? ((Collection) c).size() : 10);

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
	public Text getName()
	{
		return new TextString("EntityList");
	}

	@Override
	public Text getDisplayName()
	{
		return new TextString(toString()).lightPurple();
	}

	@Override
	public void tell(Object message)
	{
		ITextComponent component = Text.of(message).component();

		for (EntityJS entity : this)
		{
			entity.minecraftEntity.sendMessage(component, Util.DUMMY_UUID);
		}
	}

	@Override
	public void setStatusMessage(Object message)
	{
		ITextComponent component = Text.of(message).component();

		for (EntityJS entity : this)
		{
			if (entity.minecraftEntity instanceof ServerPlayerEntity)
			{
				((ServerPlayerEntity) entity.minecraftEntity).sendStatusMessage(component, true);
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

	public void playSound(@ID String id, float volume, float pitch)
	{
		SoundEvent event = ForgeRegistries.SOUND_EVENTS.getValue(UtilsJS.getMCID(id));

		if (event != null)
		{
			for (EntityJS entity : this)
			{
				entity.minecraftEntity.world.playSound(null, entity.getX(), entity.getY(), entity.getZ(), event, entity.minecraftEntity.getSoundCategory(), volume, pitch);
			}
		}
	}

	public void playSound(@ID String id)
	{
		playSound(id, 1F, 1F);
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

	public void sendData(String channel, @Nullable Object data)
	{
		CompoundNBT nbt = MapJS.nbt(data);

		for (EntityJS entity : this)
		{
			if (entity instanceof PlayerJS)
			{
				((PlayerJS) entity).sendData(channel, nbt);
			}
		}
	}

	public EntityJS getFirst()
	{
		return get(0);
	}
}