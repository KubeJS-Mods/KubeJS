package dev.latvian.mods.kubejs.item;

import com.google.common.collect.Lists;
import dev.architectury.hooks.item.food.FoodPropertiesHooks;
import dev.latvian.mods.kubejs.KubeJSRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author LatvianModder
 */
public class FoodBuilder {
	private int hunger;
	private float saturation;
	private boolean meat;
	private boolean alwaysEdible;
	private boolean fastToEat;
	private final List<Pair<Supplier<MobEffectInstance>, Float>> effects = Lists.newArrayList();
	public Consumer<FoodEatenEventJS> eaten;

	public FoodBuilder() {
	}

	public FoodBuilder(FoodProperties properties) {
		hunger = properties.getNutrition();
		saturation = properties.getSaturationModifier();
		meat = properties.isMeat();
		alwaysEdible = properties.canAlwaysEat();
		fastToEat = properties.isFastFood();

		properties.getEffects().forEach(pair -> {
			effects.add(Pair.of(pair::getFirst, pair.getSecond()));
		});
	}

	public FoodBuilder hunger(int h) {
		hunger = h;
		return this;
	}

	public FoodBuilder saturation(float s) {
		saturation = s;
		return this;
	}

	public FoodBuilder meat(boolean flag) {
		meat = flag;
		return this;
	}

	public FoodBuilder meat() {
		return meat(true);
	}

	public FoodBuilder alwaysEdible(boolean flag) {
		alwaysEdible = flag;
		return this;
	}

	public FoodBuilder alwaysEdible() {
		return alwaysEdible(true);
	}

	public FoodBuilder fastToEat(boolean flag) {
		fastToEat = flag;
		return this;
	}

	public FoodBuilder fastToEat() {
		return fastToEat(true);
	}

	public FoodBuilder effect(ResourceLocation mobEffectId, int duration, int amplifier, float probability) {
		effects.add(Pair.of(new EffectSupplier(mobEffectId, duration, amplifier), probability));
		return this;
	}

	public FoodBuilder removeEffect(MobEffect mobEffect) {
		if (mobEffect == null) {
			return this;
		}

		effects.removeIf(pair -> {
			var effectInstance = pair.getKey().get();
			return effectInstance.getDescriptionId().equals(mobEffect.getDescriptionId());
		});

		return this;
	}

	public FoodBuilder eaten(Consumer<FoodEatenEventJS> e) {
		eaten = e;
		return this;
	}

	public FoodProperties build() {
		var b = new FoodProperties.Builder();
		b.nutrition(hunger);
		b.saturationMod(saturation);

		if (meat) {
			b.meat();
		}

		if (alwaysEdible) {
			b.alwaysEat();
		}

		if (fastToEat) {
			b.fast();
		}

		for (var effect : effects) {
			FoodPropertiesHooks.effect(b, effect.getLeft(), effect.getRight());
		}

		return b.build();
	}

	private static class EffectSupplier implements Supplier<MobEffectInstance> {
		private final ResourceLocation id;
		private final int duration;
		private final int amplifier;

		private MobEffect cachedEffect;

		public EffectSupplier(ResourceLocation id, int duration, int amplifier) {
			this.id = id;
			this.duration = duration;
			this.amplifier = amplifier;
		}

		@Override
		public MobEffectInstance get() {
			if (cachedEffect == null) {
				cachedEffect = KubeJSRegistries.mobEffects().get(id);
				if (cachedEffect == null) {
					var effectIds = KubeJSRegistries.mobEffects().entrySet().stream().map(entry -> entry.getKey().location()).collect(Collectors.toSet());
					throw new RuntimeException(String.format("Missing effect '%s'. Check spelling or maybe potion id was used instead of effect id. Possible ids: %s", id, effectIds));
				}
			}

			return new MobEffectInstance(cachedEffect, duration, amplifier);
		}
	}
}