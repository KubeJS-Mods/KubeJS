package dev.latvian.kubejs.player;

import dev.latvian.kubejs.entity.EntityJS;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public class PlayerDeathEventJS extends PlayerEventJS
{
	public final String type;

	@Nullable
	public final EntityJS immediateSource;

	@Nullable
	public final EntityJS trueSource;

	public PlayerDeathEventJS(LivingDeathEvent event)
	{
		super(event.getEntity());
		type = event.getSource().damageType;
		immediateSource = world.entity(event.getSource().getImmediateSource());
		trueSource = world.entity(event.getSource().getTrueSource());
	}

	@Override
	public boolean canCancel()
	{
		return true;
	}
}