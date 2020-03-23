package dev.latvian.kubejs.item;

import com.google.common.collect.Lists;
import dev.latvian.kubejs.util.UtilsJS;
import net.minecraft.item.Food;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
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
	private final List<Pair<Supplier<EffectInstance>, Float>> effects = Lists.newArrayList();
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

	public FoodBuilder effect(Object potion, int duration, int amplifier, float probability)
	{
		ResourceLocation id = UtilsJS.getID(potion);
		effects.add(Pair.of(() -> new EffectInstance(ForgeRegistries.POTIONS.getValue(id), duration, amplifier), probability));
		return this;
	}

	public FoodBuilder eaten(Consumer<ItemFoodEatenEventJS> e)
	{
		eaten = e;
		return this;
	}

	public Food build()
	{
		Food.Builder b = new Food.Builder();
		b.hunger(hunger);
		b.saturation(saturation);

		if (meat)
		{
			b.meat();
		}

		if (alwaysEdible)
		{
			b.setAlwaysEdible();
		}

		if (fastToEat)
		{
			b.fastToEat();
		}

		for (Pair<Supplier<EffectInstance>, Float> effect : effects)
		{
			b.effect(effect.getKey(), effect.getRight());
		}

		return b.build();
	}
}