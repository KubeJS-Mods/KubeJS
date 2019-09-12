package dev.latvian.kubejs.player;

import dev.latvian.kubejs.documentation.DocClass;
import dev.latvian.kubejs.documentation.DocMethod;
import dev.latvian.kubejs.util.AttachedData;
import dev.latvian.kubejs.util.WithAttachedData;
import dev.latvian.kubejs.world.WorldJS;
import net.minecraft.entity.player.EntityPlayer;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * @author LatvianModder
 */
@DocClass
public abstract class PlayerDataJS<E extends EntityPlayer, P extends PlayerJS<E>> implements WithAttachedData
{
	private AttachedData data;

	public abstract UUID getId();

	public abstract String getName();

	@Override
	public AttachedData getData()
	{
		if (data == null)
		{
			data = new AttachedData(this);
		}

		return data;
	}

	public boolean hasClientMod()
	{
		return true;
	}

	public abstract WorldJS getOverworld();

	@Nullable
	public abstract E getPlayerEntity();

	@DocMethod
	public abstract P getPlayer();
}