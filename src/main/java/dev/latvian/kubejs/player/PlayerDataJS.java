package dev.latvian.kubejs.player;

import com.mojang.authlib.GameProfile;
import dev.latvian.kubejs.docs.MinecraftClass;
import dev.latvian.kubejs.util.AttachedData;
import dev.latvian.kubejs.util.WithAttachedData;
import dev.latvian.kubejs.world.WorldJS;
import net.minecraft.entity.player.PlayerEntity;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * @author LatvianModder
 */
public abstract class PlayerDataJS<E extends PlayerEntity, P extends PlayerJS<E>> implements WithAttachedData
{
	private AttachedData data;

	public abstract UUID getId();

	public abstract String getName();

	@MinecraftClass
	public GameProfile getProfile()
	{
		return new GameProfile(getId(), getName());
	}

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
	@MinecraftClass
	public abstract E getMinecraftPlayer();

	public abstract P getPlayer();
}