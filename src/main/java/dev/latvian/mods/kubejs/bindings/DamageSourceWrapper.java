package dev.latvian.mods.kubejs.bindings;

import dev.latvian.mods.kubejs.script.KubeJSContext;
import dev.latvian.mods.kubejs.util.ID;
import dev.latvian.mods.rhino.Context;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class DamageSourceWrapper {
	public static DamageSource of(Context cx, Object from) {
		return switch (from) {
			case DamageSource source -> source;
			case Player player -> ((KubeJSContext) cx).getDamageSources().playerAttack(player);
			case LivingEntity livingEntity -> ((KubeJSContext) cx).getDamageSources().mobAttack(livingEntity);
			case null, default -> ((KubeJSContext) cx).getDamageSources().source(ResourceKey.create(Registries.DAMAGE_TYPE, ID.mc(from)));
		};
	}
}
