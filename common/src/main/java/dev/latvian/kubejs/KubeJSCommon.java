package dev.latvian.kubejs;

import dev.latvian.kubejs.script.BindingsEvent;
import dev.latvian.kubejs.util.Overlay;
import dev.latvian.kubejs.world.WorldJS;
import org.jetbrains.annotations.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

/**
 * @author LatvianModder
 */
public class KubeJSCommon
{
	public void init()
	{
	}

	public void clientBindings(BindingsEvent event)
	{
	}

	public void handleDataToClientPacket(String channel, @Nullable CompoundTag data)
	{
	}

	@Nullable
	public Player getClientPlayer()
	{
		return null;
	}

	public void openOverlay(Overlay o)
	{
	}

	public void closeOverlay(String id)
	{
	}

	public WorldJS getClientWorld()
	{
		throw new IllegalStateException("Can't access client world from server side!");
	}
}