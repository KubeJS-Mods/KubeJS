package dev.latvian.mods.kubejs.item;

import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.typings.Param;
import dev.latvian.mods.rhino.util.HideFromJS;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.Util;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodConstants;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.UseRemainder;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

// TODO: the API for this needs to be reworked; it looks like it can
//  *almost* just be a record builder, if not for the eaten callback;
//  that said, vanilla does this through ConsumeEffects now, which are
//  just a normal registry
public class FoodBuilder {
	private int nutrition;
	private float saturation;
	private boolean alwaysEdible;
	private float eatSeconds;
	private @Nullable ItemStackTemplate usingConvertsTo;
	private final List<ApplyStatusEffectsConsumeEffect> effects;
	public @Nullable Consumer<FoodEatenKubeEvent> eaten;

	public FoodBuilder() {
		this.nutrition = 0;
		this.saturation = 0;
		this.alwaysEdible = false;
		this.eatSeconds = Consumable.DEFAULT_CONSUME_SECONDS;
		this.usingConvertsTo = null;
		this.effects = new ArrayList<>();
	}

	public FoodBuilder(ItemStack stack) {
		this();

		var food = stack.get(DataComponents.FOOD);

		if (food != null) {
			this.nutrition = food.nutrition();
			this.saturation = food.nutrition() > 0 ? food.saturation() / (food.nutrition() * 2.0F) : 0;
			this.alwaysEdible = food.canAlwaysEat();
		}

		var consumable = stack.get(DataComponents.CONSUMABLE);
		if (consumable != null) {
			this.eatSeconds = consumable.consumeSeconds();

			for (var e : consumable.onConsumeEffects()) {
				if (e instanceof ApplyStatusEffectsConsumeEffect effect) {
					effects.add(effect);
				}
			}
		}

		var rem = stack.get(DataComponents.USE_REMAINDER);
		if (rem != null) {
			this.usingConvertsTo = rem.convertInto();
		}
	}

	@Info("Sets the hunger restored.")
	public FoodBuilder nutrition(int h) {
		nutrition = h;
		return this;
	}

	@Info("Sets the saturation modifier. Note that the saturation restored is hunger * saturation.")
	public FoodBuilder saturation(float s) {
		saturation = s;
		return this;
	}

	@Info("Sets whether the food is always edible.")
	public FoodBuilder alwaysEdible(boolean flag) {
		alwaysEdible = flag;
		return this;
	}

	@Info("Sets the food is always edible.")
	public FoodBuilder alwaysEdible() {
		return alwaysEdible(true);
	}

	@Info("Sets seconds it takes to eat the food.")
	public FoodBuilder eatSeconds(float seconds) {
		eatSeconds = seconds;
		return this;
	}

	@Info("Sets the food is fast to eat (having half of the eating time).")
	public FoodBuilder fastToEat() {
		return eatSeconds(0.8F);
	}

	public FoodBuilder usingConvertsTo(ItemStack stack) {
		usingConvertsTo = stack.isEmpty() ? null : ItemStackTemplate.fromNonEmptyStack(stack);
		return this;
	}

	@Info(
		value = """
			Adds an effect to the food. Note that the effect duration is in ticks (20 ticks = 1 second).
			""",
		params = {
			@Param(name = "effect", value = "The registry id of the effect to apply."),
			@Param(name = "duration", value = "The duration of the effect in ticks."),
			@Param(name = "amplifier", value = "The amplifier of the effect. 0 means level 1, 1 means level 2, etc."),
			@Param(name = "probability", value = "The probability of the effect being applied. 1 = 100%.")
		}
	)
	public FoodBuilder effect(Holder<MobEffect> effect, int duration, int amplifier, float probability) {
		effects.add(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(effect, duration, amplifier), probability));
		return this;
	}

	@Info("Removes an effect from the food.")
	public FoodBuilder removeEffect(Holder<MobEffect> mobEffect) {
		for (var iterator = effects.listIterator(); iterator.hasNext(); ) {
			var effect = iterator.next();
			var newList = new ArrayList<MobEffectInstance>(effect.effects().size());
			for (var instance : effect.effects()) {
				if (!instance.is(mobEffect)) {
					newList.add(instance);
				}
			}

			if (newList.size() != effect.effects().size()) {
				if (newList.isEmpty()) {
					iterator.remove();
				} else {
					iterator.set(new ApplyStatusEffectsConsumeEffect(newList, effect.probability()));
				}
			}
		}
		return this;
	}

	@Info("""
		Sets a callback that is called when the food is eaten.
		
		Note: This is currently not having effect in `ItemEvents.modification`,
		as firing this callback requires an `ItemBuilder` instance in the `Item`.
		""")
	public FoodBuilder eaten(Consumer<FoodEatenKubeEvent> e) {
		eaten = e;
		return this;
	}

	@ApiStatus.Internal
	@HideFromJS
	public void applyTo(KubeJSItemProperties properties) {
		var food = new FoodProperties(nutrition, FoodConstants.saturationByModifier(nutrition, saturation), alwaysEdible);
		properties.food(food, Util.make(Consumable.builder(), builder -> {
			builder.consumeSeconds(eatSeconds);
			effects.forEach(builder::onConsume);
		}).build());

		if (usingConvertsTo != null) {
			properties.component(DataComponents.USE_REMAINDER, new UseRemainder(usingConvertsTo));
		}
	}

	public float getEatSeconds() {
		return eatSeconds;
	}

	public ItemStack getUsingConvertsTo() {
		return usingConvertsTo != null ? usingConvertsTo.create() : ItemStack.EMPTY;
	}

	public List<ApplyStatusEffectsConsumeEffect> getEffects() {
		return effects;
	}
}
