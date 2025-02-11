package dev.latvian.mods.kubejs.util;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class MobEffectUtil {
	@Contract("_ -> new")
	public static @NotNull MobEffectInstance of(MobEffectInstance oldInstance) {
		return new MobEffectInstance(oldInstance)
	}

    @Contract("_ -> new")
    public static @NotNull MobEffectInstance of(Holder<MobEffect> effect) {
        return new MobEffectInstance(effect);
    }

    @Contract("_, _ -> new")
    public static @NotNull MobEffectInstance of(Holder<MobEffect> effect, int duration) {
        return new MobEffectInstance(effect, duration);
    }

    @Contract("_, _, _ -> new")
    public static @NotNull MobEffectInstance of(Holder<MobEffect> effect, int duration, int amplifier) {
        return new MobEffectInstance(effect, duration, amplifier);
    }

    @Contract("_, _, _, _, _ -> new")
    public static @NotNull MobEffectInstance of(Holder<MobEffect> effect, int duration, int amplifier, boolean ambient, boolean visible) {
        return new MobEffectInstance(effect, duration, amplifier, ambient, visible);
    }

    @Contract("_, _, _, _, _, _ -> new")
    public static @NotNull MobEffectInstance of(Holder<MobEffect> effect, int duration, int amplifier, boolean ambient, boolean visible, boolean showIcon) {
        return new MobEffectInstance(effect, duration, amplifier, ambient, visible, showIcon);
    }
}
