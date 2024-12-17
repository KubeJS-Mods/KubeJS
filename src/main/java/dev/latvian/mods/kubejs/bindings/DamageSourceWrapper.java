package dev.latvian.mods.kubejs.bindings;

import dev.latvian.mods.kubejs.util.ID;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class DamageSourceWrapper {
	public static DamageSource wrap(RegistryAccessContainer registries, Object from) {
		return switch (from) {
			case DamageSource source -> source;
			case Player player -> registries.damageSources().playerAttack(player);
			case LivingEntity livingEntity -> registries.damageSources().mobAttack(livingEntity);
			case null, default -> registries.damageSources().source(ResourceKey.create(Registries.DAMAGE_TYPE, ID.mc(from)));
		};
	}
}
