package dev.latvian.kubejs.item;

import com.google.common.collect.Lists;
import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.docs.ID;
import dev.latvian.kubejs.util.UtilsJS;
import me.shedaniel.architectury.registry.Registries;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author LatvianModder
 */
public class FoodBuilder
{
	private int hunger;
	private float saturation;
	private boolean meat;
	private boolean alwaysEdible;
	private boolean fastToEat;
	private final List<Pair<Supplier<MobEffectInstance>, Float>> effects = Lists.newArrayList();
	public Consumer<ItemFoodEatenEventJS> eaten;

	public FoodBuilder hunger(int h)
	{
		hunger = h;
		return this;
	}

	public FoodBuilder saturation(float s)
	{
		saturation = s;
		return this;
	}

	public FoodBuilder meat()
	{
		meat = true;
		return this;
	}

	public FoodBuilder alwaysEdible()
	{
		alwaysEdible = true;
		return this;
	}

	public FoodBuilder fastToEat()
	{
		fastToEat = true;
		return this;
	}

	public FoodBuilder effect(@ID String potion, int duration, int amplifier, float probability)
	{
		ResourceLocation id = UtilsJS.getMCID(potion);
		effects.add(Pair.of(() -> new MobEffectInstance(Registries.get(KubeJS.MOD_ID).get(Registry.MOB_EFFECT_REGISTRY).get(id), duration, amplifier), probability));
		return this;
	}

	public FoodBuilder eaten(Consumer<ItemFoodEatenEventJS> e)
	{
		eaten = e;
		return this;
	}

	public FoodProperties build()
	{
		FoodProperties.Builder b = new FoodProperties.Builder();
		b.nutrition(hunger);
		b.saturationMod(saturation);

		if (meat)
		{
			b.meat();
		}

		if (alwaysEdible)
		{
			b.alwaysEat();
		}

		if (fastToEat)
		{
			b.fast();
		}

		for (Pair<Supplier<MobEffectInstance>, Float> effect : effects)
		{
			b.effect(effect.getKey().get(), effect.getRight());
		}

		return b.build();
	}
}