package dev.latvian.mods.kubejs.bindings;

import dev.latvian.mods.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.Context;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

public class DamageSourceWrapper {
	public static DamageSource of(Context cx, Object from) {
		return switch (from) {
			case DamageSource source -> source;
			case Player player -> ServerLifecycleHooks.getCurrentServer().kjs$getOverworld().damageSources().playerAttack(player);
			case LivingEntity livingEntity -> ServerLifecycleHooks.getCurrentServer().kjs$getOverworld().damageSources().mobAttack(livingEntity);
			case null, default -> ServerLifecycleHooks.getCurrentServer().kjs$getOverworld().damageSources().source(ResourceKey.create(Registries.DAMAGE_TYPE, UtilsJS.getMCID(cx, from)));
		};
	}
}
