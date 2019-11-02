package dev.latvian.kubejs.entity;

import dev.latvian.kubejs.documentation.P;
import dev.latvian.kubejs.util.UtilsJS;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class EntityPotionEffectsJS
{
	private final EntityLivingBase entity;

	public EntityPotionEffectsJS(EntityLivingBase e)
	{
		entity = e;
	}

	public void clear()
	{
		entity.clearActivePotions();
	}

	public Collection<PotionEffect> getActive()
	{
		return entity.getActivePotionEffects();
	}

	public Map<Potion, PotionEffect> getMap()
	{
		return entity.getActivePotionMap();
	}

	public boolean isActive(@P("potion") Object potion)
	{
		Potion p = UtilsJS.getPotion(potion);
		return p != null && entity.isPotionActive(p);
	}

	@Nullable
	public PotionEffect getActive(@P("potion") Object potion)
	{
		Potion p = UtilsJS.getPotion(potion);
		return p == null ? null : entity.getActivePotionEffect(p);
	}

	public void add(@P("potion") Object potion)
	{
		add(potion, 0, 0);
	}

	public void add(@P("potion") Object potion, @P("duration") int duration)
	{
		add(potion, duration, 0);
	}

	public void add(@P("potion") Object potion, @P("duration") int duration, @P("amplifier") int amplifier)
	{
		add(potion, duration, amplifier, false, true);
	}

	public void add(@P("potion") Object potion, @P("duration") int duration, @P("amplifier") int amplifier, @P("ambient") boolean ambient, @P("showParticles") boolean showParticles)
	{
		Potion p = UtilsJS.getPotion(potion);

		if (p != null)
		{
			entity.addPotionEffect(new PotionEffect(p, duration, amplifier, ambient, showParticles));
		}
	}

	public boolean isApplicable(@P("effect") PotionEffect effect)
	{
		return entity.isPotionApplicable(effect);
	}
}