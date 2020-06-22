package dev.latvian.kubejs.entity;

import dev.latvian.kubejs.docs.ID;
import dev.latvian.kubejs.util.UtilsJS;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class EntityPotionEffectsJS
{
	private final LivingEntity entity;

	public EntityPotionEffectsJS(LivingEntity e)
	{
		entity = e;
	}

	public void clear()
	{
		entity.clearActivePotions();
	}

	public Collection<EffectInstance> getActive()
	{
		return entity.getActivePotionEffects();
	}

	public Map<Effect, EffectInstance> getMap()
	{
		return entity.getActivePotionMap();
	}

	public boolean isActive(@ID String potion)
	{
		Effect p = UtilsJS.getPotion(potion);
		return p != null && entity.isPotionActive(p);
	}

	@Nullable
	public EffectInstance getActive(@ID String potion)
	{
		Effect p = UtilsJS.getPotion(potion);
		return p == null ? null : entity.getActivePotionEffect(p);
	}

	public void add(@ID String potion)
	{
		add(potion, 0, 0);
	}

	public void add(@ID String potion, int duration)
	{
		add(potion, duration, 0);
	}

	public void add(@ID String potion, int duration, int amplifier)
	{
		add(potion, duration, amplifier, false, true);
	}

	public void add(@ID String potion, int duration, int amplifier, boolean ambient, boolean showParticles)
	{
		Effect p = UtilsJS.getPotion(potion);

		if (p != null)
		{
			entity.addPotionEffect(new EffectInstance(p, duration, amplifier, ambient, showParticles));
		}
	}

	public boolean isApplicable(EffectInstance effect)
	{
		return entity.isPotionApplicable(effect);
	}
}