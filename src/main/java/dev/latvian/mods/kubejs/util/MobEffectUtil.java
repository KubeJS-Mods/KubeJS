package dev.latvian.mods.kubejs.util;

import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class MobEffectUtil {
	@Contract("_ -> new")
	@Info("Copies an existing MobEffectInstance")
	public static @NotNull MobEffectInstance of(MobEffectInstance oldInstance) {
		return new MobEffectInstance(oldInstance);
	}

	@Contract("_ -> new")
	@Info("Creates an instance for the given effect. Default duration and amplifier is 0")
	public static @NotNull MobEffectInstance of(Holder<MobEffect> effect) {
		return new MobEffectInstance(effect);
	}

	@Contract("_, _ -> new")
	@Info("Creates an instance for the given effect and duration (in ticks)")
	public static @NotNull MobEffectInstance of(Holder<MobEffect> effect, int duration) {
		return new MobEffectInstance(effect, duration);
	}

	@Contract("_, _, _ -> new")
	@Info("Creates an instance for the given effect, duration and amplifier")
	public static @NotNull MobEffectInstance of(Holder<MobEffect> effect, int duration, int amplifier) {
		return new MobEffectInstance(effect, duration, amplifier);
	}

	@Contract("_, _, _, _, _ -> new")
	@Info("Creates an instance for the given effect, duration, amplifier, ambient, and visible to the HUD")
	public static @NotNull MobEffectInstance of(Holder<MobEffect> effect, int duration, int amplifier, boolean ambient, boolean visible) {
		return new MobEffectInstance(effect, duration, amplifier, ambient, visible);
	}

	@Contract("_, _, _, _, _, _ -> new")
	@Info("Creates an instance for the given effect, duration, amplifier, ambient, visible to the HUD, and to show the icon on the sceen")
	public static @NotNull MobEffectInstance of(Holder<MobEffect> effect, int duration, int amplifier, boolean ambient, boolean visible, boolean showIcon) {
		return new MobEffectInstance(effect, duration, amplifier, ambient, visible, showIcon);
	}
}
